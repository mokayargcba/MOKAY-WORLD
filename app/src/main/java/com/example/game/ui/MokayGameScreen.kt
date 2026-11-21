package com.example.game.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.auth.signOutFromMokay
import com.example.game.viewmodel.GameViewModel
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepTwilight
import com.example.ui.theme.SparkGold
import com.example.ui.theme.WarmParchment
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun MokayGameScreen(
    viewModel: GameViewModel,
    onOpenWebVersion: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val worldState by viewModel.worldState.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val activeDialogue by viewModel.activeDialogue.collectAsStateWithLifecycle()
    val activeShopDiscount by viewModel.activeShopDiscountPercent.collectAsStateWithLifecycle()
    val activeGate by viewModel.activeGateDialog.collectAsStateWithLifecycle()
    val showVictory by viewModel.showVictoryBanner.collectAsStateWithLifecycle()

    var showInventory by remember { mutableStateOf(false) }
    var inventoryInitialTab by remember { mutableIntStateOf(0) }
    var showCraftingWorkshop by remember { mutableStateOf(false) }
    var showMap by remember { mutableStateOf(false) }
    var showGrimoire by remember { mutableStateOf(false) }
    var showQuests by remember { mutableStateOf(false) }
    var showJournal by remember { mutableStateOf(false) }
    var regionBannerTitle by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val haptics = LocalHapticFeedback.current

    // 60 FPS Game Loop
    LaunchedEffect(Unit) {
        var lastNanos = 0L
        while (isActive) {
            withFrameNanos { frameNanos ->
                if (lastNanos == 0L) {
                    lastNanos = frameNanos
                } else {
                    val dt = ((frameNanos - lastNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                    lastNanos = frameNanos
                    viewModel.tickFrame(dt)
                }
            }
        }
    }

    // Tactile feedback on combat hits, pickups, crafting & quest completions
    LaunchedEffect(worldState.hapticTick) {
        if (worldState.hapticTick > 0) {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    // Region Entrance Banner
    LaunchedEffect(worldState.currentRegionId) {
        val reg = worldState.currentRegionId
        regionBannerTitle = "${reg.title.uppercase()} — ${reg.subtitle}"
        delay(2400L)
        regionBannerTitle = null
    }

    // Auto-clear toast banner
    LaunchedEffect(worldState.toastMessage) {
        if (worldState.toastMessage != null) {
            delay(2800L)
            viewModel.clearToast()
        }
    }

    val anyOverlayOpen = showInventory || showCraftingWorkshop || showMap ||
        showGrimoire || showQuests || showJournal ||
        activeDialogue != null || activeGate != null || showVictory

    BackHandler(enabled = anyOverlayOpen) {
        when {
            showVictory -> viewModel.dismissVictoryBanner()
            activeGate != null -> viewModel.dismissGateDialog()
            activeDialogue != null -> viewModel.dismissNpcDialog()
            showCraftingWorkshop -> showCraftingWorkshop = false
            showInventory -> showInventory = false
            showJournal -> showJournal = false
            showMap -> showMap = false
            showGrimoire -> showGrimoire = false
            showQuests -> showQuests = false
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        GameCanvasView(state = worldState)

        HudAndControls(
            state = worldState,
            syncState = syncState,
            onJoystickMove = viewModel::setJoystickInput,
            onAttack = viewModel::onAttackPressed,
            onDodge = viewModel::onDodgePressed,
            onFoskay = viewModel::onFoskayPressed,
            onInteract = viewModel::onInteractPressed,
            onUseQuickSlot = viewModel::onConsumeItem,
            onOpenInventory = {
                viewModel.clearShopDiscount()
                inventoryInitialTab = 0
                showInventory = true
            },
            onOpenCrafting = { showCraftingWorkshop = true },
            onOpenMap = { showMap = true },
            onOpenGrimoire = { showGrimoire = true },
            onOpenQuests = { showQuests = true },
            onOpenJournal = { showJournal = true },
            onManualSave = { viewModel.saveNow(showConfirmationToast = true) },
            onOpenWebVersion = onOpenWebVersion
        )

        // Region Entrance Title Card Banner
        AnimatedVisibility(
            visible = regionBannerTitle != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(top = 18.dp, start = 24.dp, end = 24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = DeepTwilight.copy(alpha = 0.90f),
                border = BorderStroke(1.8.dp, AntiqueGold),
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = regionBannerTitle.orEmpty(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = SparkGold
                    )
                }
            }
        }

        // Floating Toast Banner
        AnimatedVisibility(
            visible = worldState.toastMessage != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(top = 68.dp, start = 24.dp, end = 24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DeepTwilight.copy(alpha = 0.94f),
                border = BorderStroke(1.5.dp, SparkGold),
                shadowElevation = 8.dp
            ) {
                Text(
                    text = worldState.toastMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = WarmParchment,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }

        if (showCraftingWorkshop) {
            CraftingWorkshopDialog(
                state = worldState,
                syncState = syncState,
                craftingManager = viewModel.craftingManager,
                onCraftRecipe = { id, batches -> viewModel.craftRecipe(id, batches) },
                onDismiss = { showCraftingWorkshop = false }
            )
        }

        if (showInventory) {
            InventoryCraftingSheet(
                state = worldState,
                initialTab = inventoryInitialTab,
                shopDiscountPercent = activeShopDiscount,
                craftingManager = viewModel.craftingManager,
                onConsumeItem = viewModel::onConsumeItem,
                onDiscardItem = viewModel::onDiscardItem,
                onAssignQuickSlot = viewModel::assignQuickSlot,
                onCraftRecipe = { id -> viewModel.craftRecipe(id, 1) },
                onPurchaseOffer = viewModel::purchaseShopOffer,
                onOpenDedicatedWorkshop = {
                    showInventory = false
                    showCraftingWorkshop = true
                },
                onDismiss = {
                    viewModel.clearShopDiscount()
                    showInventory = false
                }
            )
        }

        if (showMap) {
            WorldMapDialog(
                state = worldState,
                onDismiss = { showMap = false }
            )
        }

        if (showGrimoire) {
            GrimoireDialog(
                state = worldState,
                onDismiss = { showGrimoire = false }
            )
        }

        if (showQuests) {
            QuestLogDialog(
                state = worldState,
                onTrackQuest = viewModel::trackQuest,
                onCompleteQuest = viewModel::completeQuest,
                onDismiss = { showQuests = false }
            )
        }

        if (showJournal) {
            AdventureJournalDialog(
                state = worldState,
                syncState = syncState,
                achievementManager = viewModel.achievementManager,
                onManualSave = { viewModel.saveNow(showConfirmationToast = true) },
                onResetAdventure = viewModel::resetAdventure,
                onSignOut = {
                    viewModel.saveNow()
                    signOutFromMokay(credentialManager, scope)
                },
                onDismiss = { showJournal = false }
            )
        }

        if (showVictory) {
            MokayaVictoryDialog(
                state = worldState,
                onContinueExploring = viewModel::dismissVictoryBanner
            )
        }

        activeDialogue?.let { session ->
            NpcDialogueDialog(
                session = session,
                dialogueManager = viewModel.dialogueManager,
                state = worldState,
                onSelectChoice = viewModel::selectDialogueChoice,
                onAcceptQuest = viewModel::acceptQuest,
                onCompleteQuest = viewModel::completeQuest,
                onOpenShop = {
                    viewModel.openShopFromNpcDialogue()
                    inventoryInitialTab = 2
                    showInventory = true
                },
                onDismiss = viewModel::dismissNpcDialog
            )
        }

        activeGate?.let { gate ->
            SealedGateDialog(
                gate = gate,
                state = worldState,
                onAttemptUnlock = { kw -> viewModel.attemptUnlockGate(gate, kw) },
                onDismiss = viewModel::dismissGateDialog
            )
        }
    }
}
