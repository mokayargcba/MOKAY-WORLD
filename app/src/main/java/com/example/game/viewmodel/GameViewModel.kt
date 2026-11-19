package com.example.game.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.GameSaveDocument
import com.example.data.firebase.SaveRepository
import com.example.game.data.MokayRegionsCatalog
import com.example.game.data.MokayWorldCatalog
import com.example.game.engine.GameLoopEngine
import com.example.game.engine.GameWorldState
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

sealed interface CloudSyncUiState {
    data object Syncing : CloudSyncUiState
    data object Synced : CloudSyncUiState
    data class Error(val message: String) : CloudSyncUiState
}

class GameViewModel(
    private val saveRepository: SaveRepository,
    private val currentUserId: String
) : ViewModel() {

    private val _worldState = MutableStateFlow(
        GameLoopEngine.spawnRegionEntities(GameWorldState(), RegionId.PLAZA_INFINITA)
    )
    val worldState: StateFlow<GameWorldState> = _worldState.asStateFlow()

    private val _syncState = MutableStateFlow<CloudSyncUiState>(CloudSyncUiState.Syncing)
    val syncState: StateFlow<CloudSyncUiState> = _syncState.asStateFlow()

    private val _activeNpcDialog = MutableStateFlow<NpcSpec?>(null)
    val activeNpcDialog: StateFlow<NpcSpec?> = _activeNpcDialog.asStateFlow()

    private val _activeGateDialog = MutableStateFlow<GateSpec?>(null)
    val activeGateDialog: StateFlow<GateSpec?> = _activeGateDialog.asStateFlow()

    private var hasHydratedFromCloud = false
    private var currentInput = PlayerInput()

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
                            _syncState.value = CloudSyncUiState.Synced
                        }
                    } else {
                        _syncState.value = CloudSyncUiState.Synced
                    }
                }
        }
    }

    private fun applyCloudSave(doc: GameSaveDocument) {
        val regionId = RegionId.fromId(doc.currentRegionId)
        val base = GameWorldState(
            currentRegionId = regionId,
            playerX = doc.playerX.toFloat(),
            playerY = doc.playerY.toFloat(),
            hp = doc.hp.toInt().coerceIn(10, 100),
            stamina = doc.stamina.toFloat().coerceIn(0f, 100f),
            mana = doc.mana.toFloat().coerceIn(0f, 100f),
            inventory = doc.inventory.mapValues { it.value.toInt() }.ifEmpty {
                mapOf("slice_pizza" to 2, "cafe_bw" to 2, "elixir_chispa" to 2)
            },
            quickSlots = doc.quickSlots.ifEmpty {
                listOf("slice_pizza", "cafe_bw", "elixir_chispa")
            },
            collectedBookIds = doc.collectedBookIds.toSet(),
            discoveredKeywords = doc.discoveredKeywords.toSet(),
            discoveredRegions = doc.discoveredRegions.map { RegionId.fromId(it) }.toSet() + regionId,
            completedQuests = doc.completedQuests.toSet(),
            solvedPuzzleIds = doc.solvedPuzzleIds.toSet(),
            defeatedBossStages = doc.defeatedBossStages.toSet(),
            unlockedGateIds = doc.unlockedGateIds.toSet(),
            collectedForces = doc.collectedForces.toSet()
        )
        _worldState.value = GameLoopEngine.spawnRegionEntities(base, regionId)
    }

    fun setJoystickInput(x: Float, y: Float) {
        currentInput = PlayerInput(x, y)
    }

    fun tickFrame(dt: Float) {
        val prev = _worldState.value
        val next = GameLoopEngine.step(prev, currentInput, dt)
        _worldState.value = next
        if (next.collectedBookIds.size > prev.collectedBookIds.size ||
            next.solvedPuzzleIds.size > prev.solvedPuzzleIds.size ||
            next.defeatedBossStages.size > prev.defeatedBossStages.size
        ) {
            saveNow()
        }
    }

    fun onAttackPressed() {
        val prev = _worldState.value
        val next = GameLoopEngine.triggerAttack(prev)
        _worldState.value = next
        if (next.defeatedBossStages.size > prev.defeatedBossStages.size) {
            saveNow()
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
            saveNow()
        }
    }

    fun onConsumeItem(itemId: String) {
        _worldState.update { GameLoopEngine.consumeItem(it, itemId) }
        saveNow()
    }

    fun assignQuickSlot(slotIndex: Int, itemId: String) {
        if (slotIndex !in 0..2) return
        _worldState.update { s ->
            val slots = s.quickSlots.toMutableList()
            while (slots.size < 3) slots.add("slice_pizza")
            slots[slotIndex] = itemId
            s.copy(quickSlots = slots)
        }
        saveNow()
    }

    fun craftRecipe(recipeId: String) {
        val recipe = MokayWorldCatalog.recipes.firstOrNull { it.id == recipeId } ?: return
        _worldState.update { s ->
            val canCraft = recipe.ingredients.all { (id, req) -> (s.inventory[id] ?: 0) >= req }
            if (!canCraft) return@update s.copy(toastMessage = "Faltan ingredientes para craftear.")
            val nextInv = s.inventory.toMutableMap()
            recipe.ingredients.forEach { (id, req) ->
                val rem = (nextInv[id] ?: 0) - req
                if (rem <= 0) nextInv.remove(id) else nextInv[id] = rem
            }
            nextInv[recipe.resultItemId] = (nextInv[recipe.resultItemId] ?: 0) + recipe.resultQuantity
            val itemName = MokayWorldCatalog.items[recipe.resultItemId]?.name ?: recipe.resultItemId
            s.copy(
                inventory = nextInv,
                toastMessage = "¡Crafteado x${recipe.resultQuantity} $itemName!"
            )
        }
        saveNow()
    }

    fun onInteractPressed() {
        val s = _worldState.value
        val npc = s.nearbyNpc
        val gate = s.nearbyGate
        if (npc != null) {
            _activeNpcDialog.value = npc
            tryCompleteQuestFromNpc(npc)
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

    private fun tryCompleteQuestFromNpc(npc: NpcSpec) {
        val qid = npc.questId ?: return
        val quest = MokayWorldCatalog.quests.firstOrNull { it.id == qid } ?: return
        _worldState.update { s ->
            if (quest.id in s.completedQuests) return@update s
            val hasItem = quest.requiredItemId == null || (s.inventory[quest.requiredItemId] ?: 0) >= quest.requiredCount
            val hasBook = quest.requiredBookId == null || quest.requiredBookId in s.collectedBookIds
            val hasBoss = quest.requiredBossStageId == null || quest.requiredBossStageId in s.defeatedBossStages
            val hasPuzzle = quest.requiredPuzzleId == null || quest.requiredPuzzleId in s.solvedPuzzleIds
            if (hasItem && hasBook && hasBoss && hasPuzzle) {
                val inv = s.inventory.toMutableMap()
                inv[quest.rewardItemId] = (inv[quest.rewardItemId] ?: 0) + 2
                val forces = if (quest.rewardForce != null) s.collectedForces + quest.rewardForce else s.collectedForces
                s.copy(
                    completedQuests = s.completedQuests + quest.id,
                    inventory = inv,
                    collectedForces = forces,
                    toastMessage = "¡Misión '${quest.title}' completada!"
                )
            } else s
        }
        saveNow()
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
            GameLoopEngine.spawnRegionEntities(moved, gate.toRegion)
        }
        saveNow()
    }

    fun dismissNpcDialog() {
        _activeNpcDialog.value = null
    }

    fun dismissGateDialog() {
        _activeGateDialog.value = null
    }

    fun clearToast() {
        _worldState.update { it.copy(toastMessage = null) }
    }

    fun saveNow() {
        val s = _worldState.value
        val doc = GameSaveDocument(
            userId = currentUserId,
            currentRegionId = s.currentRegionId.id,
            playerX = s.playerX.toDouble(),
            playerY = s.playerY.toDouble(),
            hp = s.hp.toLong(),
            stamina = s.stamina.toInt().toLong(),
            mana = s.mana.toInt().toLong(),
            inventory = s.inventory.mapValues { it.value.toLong() },
            quickSlots = s.quickSlots,
            collectedBookIds = s.collectedBookIds.toList(),
            discoveredKeywords = s.discoveredKeywords.toList(),
            discoveredRegions = s.discoveredRegions.map { it.id },
            completedQuests = s.completedQuests.toList(),
            solvedPuzzleIds = s.solvedPuzzleIds.toList(),
            defeatedBossStages = s.defeatedBossStages.toList(),
            unlockedGateIds = s.unlockedGateIds.toList(),
            collectedForces = s.collectedForces.toList()
        )
        viewModelScope.launch {
            _syncState.value = CloudSyncUiState.Syncing
            val result = saveRepository.saveOrCreateProgress(currentUserId, doc)
            _syncState.value = result.fold(
                onSuccess = { CloudSyncUiState.Synced },
                onFailure = { CloudSyncUiState.Error(it.message ?: "Error al guardar en la nube.") }
            )
        }
    }

    private companion object {
        const val TAG = "GameViewModel"
    }
}
