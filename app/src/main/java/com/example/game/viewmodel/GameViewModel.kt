package com.example.game.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.GameSaveDocument
import com.example.data.firebase.SaveRepository
import com.example.game.data.MokayWorldCatalog
import com.example.game.engine.AchievementManager
import com.example.game.engine.CraftExecutionResult
import com.example.game.engine.CraftingManager
import com.example.game.engine.DialogueChoice
import com.example.game.engine.DialogueManager
import com.example.game.engine.GameLoopEngine
import com.example.game.engine.GameWorldState
import com.example.game.engine.NpcDialogueTree
import com.example.game.engine.PlayerInput
import com.example.game.model.GateSpec
import com.example.game.model.NpcSpec
import com.example.game.model.RegionId
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.hypot

sealed interface CloudSyncUiState {
    data object Syncing : CloudSyncUiState
    data class Synced(val savedAtLabel: String = "Sincronizado") : CloudSyncUiState
    data class Error(val message: String) : CloudSyncUiState
}

data class ActiveDialogueSession(
    val npc: NpcSpec,
    val tree: NpcDialogueTree,
    val currentNodeId: String,
    val lastDelta: Int? = null,
    val lastAwardedChispas: Int = 0
)

class GameViewModel(
    private val saveRepository: SaveRepository,
    private val currentUserId: String,
    val dialogueManager: DialogueManager = DialogueManager()
) : ViewModel() {

    private val _worldState = MutableStateFlow(
        GameLoopEngine.spawnRegionEntities(GameWorldState(), RegionId.PLAZA_INFINITA)
    )
    val worldState: StateFlow<GameWorldState> = _worldState.asStateFlow()

    private val _syncState = MutableStateFlow<CloudSyncUiState>(CloudSyncUiState.Syncing)
    val syncState: StateFlow<CloudSyncUiState> = _syncState.asStateFlow()

    private val _activeDialogue = MutableStateFlow<ActiveDialogueSession?>(null)
    val activeDialogue: StateFlow<ActiveDialogueSession?> = _activeDialogue.asStateFlow()

    private val _activeShopDiscountPercent = MutableStateFlow(0)
    val activeShopDiscountPercent: StateFlow<Int> = _activeShopDiscountPercent.asStateFlow()

    private val _activeGateDialog = MutableStateFlow<GateSpec?>(null)
    val activeGateDialog: StateFlow<GateSpec?> = _activeGateDialog.asStateFlow()

    private val _showVictoryBanner = MutableStateFlow(false)
    val showVictoryBanner: StateFlow<Boolean> = _showVictoryBanner.asStateFlow()

    val craftingManager = CraftingManager()
    val achievementManager = AchievementManager()

    private var hasHydratedFromCloud = false
    private var currentInput = PlayerInput()
    private var autoSaveTimerSec = 0f
    private var dirtyForAutoSave = false
    private var lastSavedX = 900f
    private var lastSavedY = 740f
    private var hasShownVictoryOnce = false

    init {
        observeCloudSave()
    }

    private fun observeCloudSave() {
        viewModelScope.launch {
            saveRepository.observeSave(currentUserId)
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val root = firestoreError ?: error
                    Log.w(TAG, "Error observing cloud save", root)
                    _syncState.value = CloudSyncUiState.Error(
                        root.message ?: "No se pudo sincronizar la partida en la nube."
                    )
                }
                .collect { doc ->
                    if (!hasHydratedFromCloud) {
                        hasHydratedFromCloud = true
                        if (doc == null) {
                            saveNow()
                        } else {
                            applyCloudSave(doc)
                            _syncState.value = CloudSyncUiState.Synced(currentTimeLabel())
                        }
                    } else if (_syncState.value is CloudSyncUiState.Syncing) {
                        _syncState.value = CloudSyncUiState.Synced(currentTimeLabel())
                    }
                }
        }
    }

    private fun applyCloudSave(doc: GameSaveDocument) {
        val regionId = RegionId.fromId(doc.currentRegionId)
        val restoredMaxHp = doc.maxHp.toInt().coerceIn(80, 300)
        val restoredMaxStam = doc.maxStamina.toFloat().coerceIn(80f, 260f)
        val restoredMaxMana = doc.maxMana.toFloat().coerceIn(80f, 260f)

        val base = GameWorldState(
            currentRegionId = regionId,
            playerX = doc.playerX.toFloat().coerceIn(80f, 1720f),
            playerY = doc.playerY.toFloat().coerceIn(80f, 1120f),
            hp = doc.hp.toInt().coerceIn(10, restoredMaxHp),
            maxHp = restoredMaxHp,
            stamina = doc.stamina.toFloat().coerceIn(0f, restoredMaxStam),
            maxStamina = restoredMaxStam,
            mana = doc.mana.toFloat().coerceIn(0f, restoredMaxMana),
            maxMana = restoredMaxMana,
            chispas = doc.chispas.toInt().coerceAtLeast(0),
            inventory = doc.inventory.mapValues { it.value.toInt() }.ifEmpty {
                mapOf("slice_pizza" to 2, "cafe_bw" to 2, "elixir_chispa" to 2)
            },
            quickSlots = doc.quickSlots.ifEmpty {
                listOf("slice_pizza", "cafe_bw", "elixir_chispa")
            },
            collectedBookIds = doc.collectedBookIds.toSet(),
            discoveredKeywords = doc.discoveredKeywords.toSet(),
            discoveredRegions = doc.discoveredRegions.map { RegionId.fromId(it) }.toSet() + regionId,
            discoveredLandmarkIds = doc.discoveredLandmarkIds.toSet(),
            visitedVillages = doc.visitedVillages.toSet(),
            activeQuestIds = doc.activeQuestIds.toSet(),
            completedQuests = doc.completedQuests.toSet(),
            trackedQuestId = doc.trackedQuestId.ifBlank { "q_tintopolis_cat" },
            solvedPuzzleIds = doc.solvedPuzzleIds.toSet(),
            defeatedBossStages = doc.defeatedBossStages.toSet(),
            unlockedGateIds = doc.unlockedGateIds.toSet(),
            collectedForces = doc.collectedForces.toSet(),
            collectedPickupIds = doc.collectedPickupIds.toSet(),
            npcRelationships = doc.npcRelationships.mapValues { it.value.toInt().coerceIn(-100, 100) },
            unlockedAchievementIds = doc.unlockedAchievementIds.toSet()
        )
        lastSavedX = base.playerX
        lastSavedY = base.playerY
        _worldState.value = GameLoopEngine.spawnRegionEntities(base, regionId)
        evaluateAchievementsAndSync(saveIfAlreadyDirty = false)
    }

    fun setJoystickInput(x: Float, y: Float) {
        currentInput = PlayerInput(x, y)
    }

    fun tickFrame(dt: Float) {
        val prev = _worldState.value
        val next = GameLoopEngine.step(prev, currentInput, dt)
        _worldState.value = next

        val criticalChange = next.collectedBookIds.size > prev.collectedBookIds.size ||
            next.solvedPuzzleIds.size > prev.solvedPuzzleIds.size ||
            next.defeatedBossStages.size > prev.defeatedBossStages.size ||
            next.discoveredLandmarkIds.size > prev.discoveredLandmarkIds.size ||
            next.visitedVillages.size > prev.visitedVillages.size ||
            next.currentRegionId != prev.currentRegionId

        if (criticalChange) {
            evaluateAchievementsAndSync(saveIfAlreadyDirty = true)
            return
        }

        if (next.collectedPickupIds.size > prev.collectedPickupIds.size ||
            next.chispas != prev.chispas ||
            next.inventory != prev.inventory
        ) {
            dirtyForAutoSave = true
        }

        autoSaveTimerSec += dt
        if (autoSaveTimerSec >= AUTO_SAVE_INTERVAL_SEC) {
            autoSaveTimerSec = 0f
            val movedDist = hypot(
                (next.playerX - lastSavedX).toDouble(),
                (next.playerY - lastSavedY).toDouble()
            ).toFloat()
            if (dirtyForAutoSave || movedDist >= 140f) {
                saveNow()
            }
        }
    }

    fun onAttackPressed() {
        val prev = _worldState.value
        val next = GameLoopEngine.triggerAttack(prev)
        _worldState.value = next
        if (next.defeatedBossStages.size > prev.defeatedBossStages.size) {
            evaluateAchievementsAndSync(saveIfAlreadyDirty = true)
        } else if (next.chispas > prev.chispas || next.inventory != prev.inventory) {
            dirtyForAutoSave = true
        }
    }

    fun onDodgePressed() {
        _worldState.update { GameLoopEngine.triggerDodge(it) }
    }

    fun onFoskayPressed() {
        val prev = _worldState.value
        val next = GameLoopEngine.triggerFoskayAbility(prev)
        _worldState.value = next
        if (next.solvedPuzzleIds.size > prev.solvedPuzzleIds.size ||
            next.defeatedBossStages.size > prev.defeatedBossStages.size
        ) {
            evaluateAchievementsAndSync(saveIfAlreadyDirty = true)
        } else if (next.chispas > prev.chispas) {
            dirtyForAutoSave = true
        }
    }

    fun onConsumeItem(itemId: String) {
        _worldState.update { GameLoopEngine.consumeItem(it, itemId) }
        saveNow()
    }

    fun onDiscardItem(itemId: String) {
        _worldState.update { GameLoopEngine.discardItem(it, itemId) }
        saveNow()
    }

    fun assignQuickSlot(slotIndex: Int, itemId: String) {
        if (slotIndex !in 0..2) return
        _worldState.update { s ->
            val slots = s.quickSlots.toMutableList()
            while (slots.size < 3) slots.add("slice_pizza")
            slots[slotIndex] = itemId
            s.copy(
                quickSlots = slots,
                toastMessage = "Asignado al Slot ${slotIndex + 1}"
            )
        }
        saveNow()
    }

    fun craftRecipe(recipeId: String, batches: Int = 1) {
        var succeeded = false
        _worldState.update { s ->
            when (val result = craftingManager.craft(s, recipeId, batches)) {
                is CraftExecutionResult.Success -> {
                    succeeded = true
                    result.nextState
                }
                is CraftExecutionResult.Failure -> result.state
            }
        }
        if (succeeded) {
            saveNow()
        }
    }

    fun purchaseShopOffer(offerId: String) {
        val discount = _activeShopDiscountPercent.value
        _worldState.update { GameLoopEngine.purchaseShopOffer(it, offerId, discount) }
        saveNow()
    }

    fun clearShopDiscount() {
        _activeShopDiscountPercent.value = 0
    }

    fun acceptQuest(questId: String) {
        _worldState.update { GameLoopEngine.acceptQuest(it, questId) }
        saveNow()
    }

    fun trackQuest(questId: String) {
        _worldState.update { s ->
            val q = MokayWorldCatalog.quests.firstOrNull { it.id == questId } ?: return@update s
            s.copy(
                trackedQuestId = questId,
                activeQuestIds = if (questId !in s.completedQuests) s.activeQuestIds + questId else s.activeQuestIds,
                toastMessage = "Siguiendo misión: ${q.title}"
            )
        }
        saveNow()
    }

    fun completeQuest(questId: String) {
        val prevCompleted = _worldState.value.completedQuests
        _worldState.update { GameLoopEngine.completeQuest(it, questId) }
        val next = _worldState.value
        if (questId !in prevCompleted && questId in next.completedQuests) {
            if (questId == "q_arbol_harmony" && !hasShownVictoryOnce) {
                hasShownVictoryOnce = true
                _showVictoryBanner.value = true
            }
            evaluateAchievementsAndSync(saveIfAlreadyDirty = true)
        }
    }

    fun onInteractPressed() {
        val s = _worldState.value
        val npc = s.nearbyNpc
        val gate = s.nearbyGate
        if (npc != null) {
            if (npc.villageName.isNotBlank() && npc.villageName !in s.visitedVillages) {
                _worldState.update { st -> st.copy(visitedVillages = st.visitedVillages + npc.villageName) }
                saveNow()
            }
            // Auto-accept or auto-complete quest when talking, while keeping dialogue open for full details/shop
            npc.questId?.let { qid ->
                val quest = MokayWorldCatalog.quests.firstOrNull { it.id == qid }
                if (quest != null && qid !in s.completedQuests) {
                    if (GameLoopEngine.isQuestReadyToComplete(s, quest)) {
                        completeQuest(qid)
                    } else if (qid !in s.activeQuestIds) {
                        acceptQuest(qid)
                    }
                }
            }
            val latestState = _worldState.value
            val score = latestState.npcRelationships[npc.id] ?: 0
            val tree = dialogueManager.getTreeForNpc(npc)
            val startNodeId = dialogueManager.resolveStartNodeId(tree, score)
            _activeShopDiscountPercent.value = dialogueManager.shopDiscountFor(score)
            _activeDialogue.value = ActiveDialogueSession(
                npc = npc,
                tree = tree,
                currentNodeId = startNodeId
            )
        } else if (gate != null) {
            val autoOpen = gate.id in s.unlockedGateIds ||
                (gate.requiredBookId == null && gate.requiredQuestItemId == null && gate.requiredBossStageId == null && gate.requiredForces.isEmpty())
            if (autoOpen) {
                travelThroughGate(gate)
            } else {
                _activeGateDialog.value = gate
            }
        } else {
            _worldState.update { it.copy(toastMessage = "Acércate a un personaje, libro o puerta para interactuar.") }
        }
    }

    fun attemptUnlockGate(gate: GateSpec, enteredKeyword: String) {
        val s = _worldState.value
        val cleanKey = enteredKeyword.trim().uppercase()
        val hasBook = gate.requiredBookId == null || gate.requiredBookId in s.collectedBookIds
        val hasItem = gate.requiredQuestItemId == null || (s.inventory[gate.requiredQuestItemId] ?: 0) > 0
        val hasBoss = gate.requiredBossStageId == null || gate.requiredBossStageId in s.defeatedBossStages
        val hasForces = gate.requiredForces.all { it in s.collectedForces }
        val keyMatches = gate.requiredKeyword == null || cleanKey == gate.requiredKeyword.uppercase()

        if (hasBook && hasItem && hasBoss && hasForces && keyMatches) {
            _activeGateDialog.value = null
            _worldState.update { it.copy(unlockedGateIds = it.unlockedGateIds + gate.id) }
            travelThroughGate(gate)
        } else {
            val reason = when {
                !hasBoss || !hasItem || !hasForces -> gate.lockedMessage
                !hasBook -> "Necesitas encontrar y leer el libro correspondiente primero."
                else -> "La palabra clave no coincide con el sello del libro."
            }
            _worldState.update { it.copy(toastMessage = reason) }
        }
    }

    private fun travelThroughGate(gate: GateSpec) {
        _worldState.update { s ->
            val moved = s.copy(
                playerX = gate.spawnX,
                playerY = gate.spawnY,
                toastMessage = "Llegaste a ${gate.toRegion.title}"
            )
            GameLoopEngine.spawnRegionEntities(moved, gate.toRegion, refreshRespawningPickups = true)
        }
        evaluateAchievementsAndSync(saveIfAlreadyDirty = true)
    }

    fun selectDialogueChoice(choice: DialogueChoice) {
        val session = _activeDialogue.value ?: return
        val outcome = dialogueManager.applyChoice(_worldState.value, session.npc, choice)
        _worldState.value = outcome.nextState
        _activeShopDiscountPercent.value = dialogueManager.shopDiscountFor(outcome.newScore)

        val nextNodeId = outcome.nextNodeId
        if (nextNodeId != null && session.tree.nodes.containsKey(nextNodeId)) {
            _activeDialogue.value = session.copy(
                currentNodeId = nextNodeId,
                lastDelta = outcome.delta,
                lastAwardedChispas = outcome.awardedChispas
            )
        } else {
            _activeDialogue.value = null
        }

        evaluateAchievementsAndSync(
            saveIfAlreadyDirty = outcome.delta != 0 || outcome.awardedChispas > 0
        )
    }

    private fun evaluateAchievementsAndSync(saveIfAlreadyDirty: Boolean) {
        val outcome = achievementManager.evaluateAndUnlock(_worldState.value)
        if (outcome.newlyUnlocked.isNotEmpty()) {
            _worldState.value = outcome.nextState
            saveNow()
        } else if (saveIfAlreadyDirty) {
            saveNow()
        }
    }

    fun openShopFromNpcDialogue() {
        val session = _activeDialogue.value
        val score = if (session != null) {
            _worldState.value.npcRelationships[session.npc.id] ?: 0
        } else 0
        _activeShopDiscountPercent.value = dialogueManager.shopDiscountFor(score)
        _activeDialogue.value = null
    }

    fun dismissNpcDialog() {
        _activeDialogue.value = null
    }

    fun dismissGateDialog() {
        _activeGateDialog.value = null
    }

    fun dismissVictoryBanner() {
        _showVictoryBanner.value = false
    }

    fun clearToast() {
        _worldState.update { it.copy(toastMessage = null) }
    }

    fun saveNow(showConfirmationToast: Boolean = false) {
        val s = _worldState.value
        dirtyForAutoSave = false
        lastSavedX = s.playerX
        lastSavedY = s.playerY
        val doc = buildSaveDoc(s)
        viewModelScope.launch {
            _syncState.value = CloudSyncUiState.Syncing
            val result = saveRepository.saveOrCreateProgress(currentUserId, doc)
            _syncState.value = result.fold(
                onSuccess = {
                    if (showConfirmationToast) {
                        _worldState.update { st -> st.copy(toastMessage = "Progreso guardado en la nube ✓") }
                    }
                    CloudSyncUiState.Synced(currentTimeLabel())
                },
                onFailure = { CloudSyncUiState.Error(it.message ?: "Error al guardar en la nube.") }
            )
        }
    }

    fun resetAdventure() {
        val starterState = GameLoopEngine.spawnRegionEntities(GameWorldState(), RegionId.PLAZA_INFINITA)
        val starterDoc = buildSaveDoc(starterState)
        _worldState.value = starterState.copy(toastMessage = "Partida reiniciada desde Plaza Infinita.")
        hasShownVictoryOnce = false
        _showVictoryBanner.value = false
        _activeDialogue.value = null
        _activeShopDiscountPercent.value = 0
        _activeGateDialog.value = null
        lastSavedX = starterState.playerX
        lastSavedY = starterState.playerY
        dirtyForAutoSave = false

        viewModelScope.launch {
            _syncState.value = CloudSyncUiState.Syncing
            val result = saveRepository.resetSave(currentUserId, starterDoc)
            _syncState.value = result.fold(
                onSuccess = { CloudSyncUiState.Synced(currentTimeLabel()) },
                onFailure = { CloudSyncUiState.Error(it.message ?: "No se pudo reiniciar en la nube.") }
            )
        }
    }

    private fun buildSaveDoc(s: GameWorldState): GameSaveDocument = GameSaveDocument(
        userId = currentUserId,
        currentRegionId = s.currentRegionId.id,
        playerX = s.playerX.toDouble(),
        playerY = s.playerY.toDouble(),
        hp = s.hp.toLong(),
        maxHp = s.maxHp.toLong(),
        stamina = s.stamina.toInt().toLong(),
        maxStamina = s.maxStamina.toInt().toLong(),
        mana = s.mana.toInt().toLong(),
        maxMana = s.maxMana.toInt().toLong(),
        chispas = s.chispas.toLong(),
        inventory = s.inventory.mapValues { it.value.toLong() },
        quickSlots = s.quickSlots,
        collectedBookIds = s.collectedBookIds.toList(),
        discoveredKeywords = s.discoveredKeywords.toList(),
        discoveredRegions = s.discoveredRegions.map { it.id },
        discoveredLandmarkIds = s.discoveredLandmarkIds.toList(),
        visitedVillages = s.visitedVillages.toList(),
        activeQuestIds = s.activeQuestIds.toList(),
        completedQuests = s.completedQuests.toList(),
        trackedQuestId = s.trackedQuestId,
        solvedPuzzleIds = s.solvedPuzzleIds.toList(),
        defeatedBossStages = s.defeatedBossStages.toList(),
        unlockedGateIds = s.unlockedGateIds.toList(),
        collectedForces = s.collectedForces.toList(),
        collectedPickupIds = s.collectedPickupIds.toList(),
        npcRelationships = s.npcRelationships.mapValues { it.value.toLong().coerceIn(-100L, 100L) },
        unlockedAchievementIds = s.unlockedAchievementIds.toList()
    )

    private fun currentTimeLabel(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    private companion object {
        const val TAG = "GameViewModel"
        const val AUTO_SAVE_INTERVAL_SEC = 8f
    }
}
