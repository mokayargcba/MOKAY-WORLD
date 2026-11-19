package com.example.game.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.auth.signOutFromMokay
import com.example.game.viewmodel.GameViewModel
import com.example.ui.theme.DeepTwilight
import com.example.ui.theme.SparkGold
import com.example.ui.theme.WarmParchment
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun MokayGameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val worldState by viewModel.worldState.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val activeNpc by viewModel.activeNpcDialog.collectAsStateWithLifecycle()
    val activeGate by viewModel.activeGateDialog.collectAsStateWithLifecycle()

    var showInventory by remember { mutableStateOf(false) }
    var showMap by remember { mutableStateOf(false) }
    var showGrimoire by remember { mutableStateOf(false) }
    var showQuests by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

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

    // Auto-clear toast banner
    LaunchedEffect(worldState.toastMessage) {
        if (worldState.toastMessage != null) {
            delay(2800L)
            viewModel.clearToast()
        }
    }

    val anyOverlayOpen = showInventory || showMap || showGrimoire || showQuests ||
        activeNpc != null || activeGate != null

    BackHandler(enabled = anyOverlayOpen) {
        when {
            activeGate != null -> viewModel.dismissGateDialog()
            activeNpc != null -> viewModel.dismissNpcDialog()
            showInventory -> showInventory = false
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
            onOpenInventory = { showInventory = true },
            onOpenMap = { showMap = true },
            onOpenGrimoire = { showGrimoire = true },
            onOpenQuests = { showQuests = true },
            onSignOut = {
                viewModel.saveNow()
                signOutFromMokay(credentialManager, scope)
            }
        )

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

        if (showInventory) {
            InventoryCraftingSheet(
                state = worldState,
                onConsumeItem = viewModel::onConsumeItem,
                onAssignQuickSlot = viewModel::assignQuickSlot,
                onCraftRecipe = viewModel::craftRecipe,
                onDismiss = { showInventory = false }
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
                onDismiss = { showQuests = false }
            )
        }

        activeNpc?.let { npc ->
            NpcDialogueDialog(
                npc = npc,
                state = worldState,
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
