package com.example.game.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.data.MokayRegionsCatalog
import com.example.game.data.MokayWorldCatalog
import com.example.game.engine.GameLoopEngine
import com.example.game.engine.GameWorldState
import com.example.game.model.RegionId
import com.example.game.viewmodel.CloudSyncUiState
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BoxyLavender
import com.example.ui.theme.BoxyLavenderDeep
import com.example.ui.theme.CardSlate
import com.example.ui.theme.DeepTwilight
import com.example.ui.theme.FatigueAmber
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.InkBlack
import com.example.ui.theme.ManaIndigo
import com.example.ui.theme.RunicTeal
import com.example.ui.theme.SparkGold
import com.example.ui.theme.StaminaEmerald
import com.example.ui.theme.VitalCrimson
import com.example.ui.theme.WarmParchment

@Composable
fun HudAndControls(
    state: GameWorldState,
    syncState: CloudSyncUiState,
    onJoystickMove: (Float, Float) -> Unit,
    onAttack: () -> Unit,
    onDodge: () -> Unit,
    onFoskay: () -> Unit,
    onInteract: () -> Unit,
    onUseQuickSlot: (String) -> Unit,
    onOpenInventory: () -> Unit,
    onOpenCrafting: () -> Unit,
    onOpenMap: () -> Unit,
    onOpenGrimoire: () -> Unit,
    onOpenQuests: () -> Unit,
    onOpenJournal: () -> Unit,
    onManualSave: () -> Unit,
    onOpenWebVersion: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val activeBoss = state.activeEnemies.firstOrNull { it.isBoss }
    val trackedQuest = MokayWorldCatalog.quests.firstOrNull { it.id == state.trackedQuestId }

    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // TOP ROW: Left Stats & Location + Center Boss Bar + Right Minimap & Quick Menu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.width(196.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DeepTwilight.copy(alpha = 0.86f),
                    border = BorderStroke(1.3.dp, BoxyLavender.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "BOXY",
                                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                                    color = WarmParchment,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SparkGold.copy(alpha = 0.16f),
                                    border = BorderStroke(0.8.dp, SparkGold.copy(alpha = 0.55f))
                                ) {
                                    Text(
                                        text = "✨ ${state.chispas}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = SparkGold,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            SyncBadge(syncState = syncState, onManualSave = onManualSave)
                        }

                        VitalBar("VIDA", state.hp.toFloat(), state.maxHp.toFloat(), VitalCrimson)
                        StaminaVitalBar(state.stamina, state.maxStamina, state.exhausted)
                        VitalBar("MAGIA", state.mana, state.maxMana, ManaIndigo)
                    }
                }

                // Region & Landmark Badge
                Surface(
                    shape = RoundedCornerShape(11.dp),
                    color = CardSlate.copy(alpha = 0.86f),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)) {
                        Text(
                            text = state.currentRegionId.title.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = SparkGold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = state.nearestLandmarkName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                            color = WarmParchment.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Tracked Quest HUD Pill
                if (trackedQuest != null) {
                    val done = trackedQuest.id in state.completedQuests
                    val ready = !done && GameLoopEngine.isQuestReadyToComplete(state, trackedQuest)
                    val progressNote = when {
                        done -> "Completada ✓"
                        ready -> "¡Lista para entregar!"
                        trackedQuest.requiredItemId != null -> {
                            val have = state.inventory[trackedQuest.requiredItemId] ?: 0
                            val itemName = MokayWorldCatalog.items[trackedQuest.requiredItemId]?.name ?: ""
                            "$itemName ($have/${trackedQuest.requiredCount})"
                        }
                        else -> trackedQuest.regionId.title
                    }
                    Surface(
                        shape = RoundedCornerShape(11.dp),
                        color = DeepTwilight.copy(alpha = 0.84f),
                        border = BorderStroke(
                            1.dp,
                            if (ready) StaminaEmerald else RunicTeal.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tracked_quest_pill")
                            .clickable(onClick = onOpenQuests)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)) {
                            Text(
                                text = "MISIÓN: ${trackedQuest.title}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = if (ready) StaminaEmerald else RunicTeal,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = progressNote,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 9.sp),
                                color = WarmParchment.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Top-right Action Menus & Minimap
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Top
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    HudMenuIconButton(
                        icon = Icons.Filled.Backpack,
                        label = "Mochila",
                        tint = SparkGold,
                        testTag = "open_inventory_button",
                        onClick = onOpenInventory
                    )
                    HudMenuIconButton(
                        icon = Icons.Filled.Build,
                        label = "Taller",
                        tint = FlameOrange,
                        testTag = "open_crafting_button",
                        onClick = onOpenCrafting
                    )
                    HudMenuIconButton(
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        label = "Grimorio",
                        tint = BoxyLavender,
                        testTag = "open_grimoire_button",
                        onClick = onOpenGrimoire
                    )
                    HudMenuIconButton(
                        icon = Icons.Filled.Flag,
                        label = "Misiones",
                        tint = RunicTeal,
                        testTag = "open_quests_button",
                        onClick = onOpenQuests
                    )
                    HudMenuIconButton(
                        icon = Icons.Filled.Menu,
                        label = "Diario",
                        tint = WarmParchment,
                        testTag = "open_journal_button",
                        onClick = onOpenJournal
                    )
                    if (onOpenWebVersion != null) {
                        HudMenuIconButton(
                            icon = Icons.Filled.Language,
                            label = "Web",
                            tint = RunicTeal,
                            testTag = "open_web_version_button",
                            onClick = onOpenWebVersion
                        )
                    }
                }

                MinimapCard(state = state, onClick = onOpenMap)
            }
        }

        // BOSS HEALTH BAR
        if (activeBoss != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 2.dp)
                    .width(240.dp),
                shape = RoundedCornerShape(12.dp),
                color = DeepTwilight.copy(alpha = 0.92f),
                border = BorderStroke(1.5.dp, FlameOrange)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "JEFE: ${activeBoss.name.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = SparkGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF2D1E2F))
                    ) {
                        val frac = (activeBoss.hp.toFloat() / activeBoss.maxHp.toFloat()).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(frac)
                                .height(7.dp)
                                .background(Brush.horizontalGradient(listOf(VitalCrimson, FlameOrange)))
                        )
                    }
                }
            }
        }

        // BOTTOM ROW: Left Joystick + Center Quick Slots + Right Combat & Interaction Cluster
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            VirtualJoystick(
                onMove = onJoystickMove,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 4.dp, bottom = 2.dp)
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.quickSlots.take(3).forEachIndexed { idx, itemId ->
                    val item = MokayWorldCatalog.items[itemId]
                    val qty = state.inventory[itemId] ?: 0
                    QuickSlotPill(
                        slotNumber = idx + 1,
                        itemId = itemId,
                        name = item?.name ?: itemId,
                        quantity = qty,
                        color = item?.let { Color(it.colorHex) } ?: SparkGold,
                        onClick = { onUseQuickSlot(itemId) },
                        modifier = Modifier.testTag("quick_slot_$idx")
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 4.dp, bottom = 2.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val canInteract = state.nearbyNpc != null || state.nearbyGate != null
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionOrbButton(
                        icon = Icons.Filled.Forum,
                        label = when {
                            state.nearbyNpc != null -> "Hablar"
                            state.nearbyGate != null -> "Puerta"
                            else -> "Acción"
                        },
                        containerColor = if (canInteract) RunicTeal else CardSlate.copy(alpha = 0.78f),
                        contentColor = if (canInteract) InkBlack else WarmParchment,
                        size = 49.dp,
                        highlighted = canInteract,
                        testTag = "interact_button",
                        onClick = onInteract
                    )

                    ActionOrbButton(
                        icon = Icons.Filled.LocalFireDepartment,
                        label = "Foskay",
                        containerColor = if (state.mana >= 12f) FlameOrange else CardSlate.copy(alpha = 0.75f),
                        contentColor = Color.White,
                        size = 52.dp,
                        highlighted = state.mana >= 12f,
                        testTag = "foskay_button",
                        onClick = onFoskay
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionOrbButton(
                        icon = Icons.Filled.Shield,
                        label = "Esquivar",
                        containerColor = if (!state.exhausted) BoxyLavenderDeep else CardSlate.copy(alpha = 0.5f),
                        contentColor = WarmParchment,
                        size = 49.dp,
                        enabled = !state.exhausted,
                        testTag = "dodge_button",
                        onClick = onDodge
                    )

                    ActionOrbButton(
                        icon = Icons.Filled.SportsMma,
                        label = "Atacar",
                        containerColor = VitalCrimson,
                        contentColor = Color.White,
                        size = 58.dp,
                        highlighted = true,
                        testTag = "attack_button",
                        onClick = onAttack
                    )
                }
            }
        }
    }
}

@Composable
private fun SyncBadge(
    syncState: CloudSyncUiState,
    onManualSave: () -> Unit
) {
    val (icon, tint, label) = when (syncState) {
        is CloudSyncUiState.Syncing -> Triple(Icons.Filled.CloudSync, SparkGold, "...")
        is CloudSyncUiState.Synced -> Triple(Icons.Filled.CloudDone, RunicTeal, syncState.savedAtLabel)
        is CloudSyncUiState.Error -> Triple(Icons.Filled.CloudOff, VitalCrimson, "Local")
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onManualSave)
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .testTag("sync_badge_save")
    ) {
        Icon(icon, contentDescription = "Guardar en nube ($label)", tint = tint, modifier = Modifier.size(14.dp))
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = tint)
    }
}

@Composable
private fun VitalBar(label: String, current: Float, max: Float, barColor: Color) {
    val fraction = if (max > 0f) (current / max).coerceIn(0f, 1f) else 0f
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = WarmParchment.copy(alpha = 0.85f))
            Text("${current.toInt()}/${max.toInt()}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = WarmParchment.copy(alpha = 0.85f))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(InkBlack.copy(alpha = 0.65f))
        ) {
            Box(modifier = Modifier.fillMaxWidth(fraction).height(7.dp).background(barColor))
        }
    }
}

@Composable
private fun StaminaVitalBar(stamina: Float, maxStamina: Float, exhausted: Boolean) {
    val pulse = rememberInfiniteTransition(label = "fatiguePulse")
    val alpha by pulse.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(420), repeatMode = RepeatMode.Reverse),
        label = "pulseAlpha"
    )
    val barColor by animateColorAsState(
        targetValue = if (exhausted) FatigueAmber else StaminaEmerald,
        label = "staminaColor"
    )
    val fraction = if (maxStamina > 0f) (stamina / maxStamina).coerceIn(0f, 1f) else 0f

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (exhausted) "CANSADO" else "ESTAMINA",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = if (exhausted) FatigueAmber else WarmParchment.copy(alpha = 0.85f),
                    fontWeight = if (exhausted) FontWeight.ExtraBold else FontWeight.SemiBold,
                    modifier = Modifier.alpha(if (exhausted) alpha else 1f)
                )
                if (exhausted) {
                    Icon(Icons.Filled.Bolt, contentDescription = "Cansado", tint = FatigueAmber, modifier = Modifier.size(11.dp).alpha(alpha))
                }
            }
            Text("${stamina.toInt()}/${maxStamina.toInt()}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = WarmParchment.copy(alpha = 0.85f))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(InkBlack.copy(alpha = 0.65f))
        ) {
            Box(modifier = Modifier.fillMaxWidth(fraction).height(7.dp).alpha(if (exhausted) alpha else 1f).background(barColor))
        }
    }
}

@Composable
private fun HudMenuIconButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DeepTwilight.copy(alpha = 0.88f),
        border = BorderStroke(1.5.dp, tint.copy(alpha = 0.55f)),
        modifier = Modifier.size(48.dp).testTag(testTag).clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun MinimapCard(state: GameWorldState, onClick: () -> Unit) {
    val region = MokayRegionsCatalog.regions.getValue(state.currentRegionId)
    val isBosqueFogged = state.currentRegionId == RegionId.BOSQUE_OLVIDADO &&
        RegionId.BOSQUE_OLVIDADO !in state.discoveredRegions

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DeepTwilight.copy(alpha = 0.90f),
        border = BorderStroke(1.8.dp, AntiqueGold.copy(alpha = 0.7f)),
        modifier = Modifier.size(86.dp).testTag("minimap_button").clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                val sx = size.width / region.worldWidth
                val sy = size.height / region.worldHeight
                drawRoundRect(region.theme.groundPrimary, size = size, cornerRadius = CornerRadius(12f, 12f))
                region.landmarks.forEach { lm ->
                    val lmAlpha = if (lm.id in state.discoveredLandmarkIds) 0.9f else 0.3f
                    drawCircle(lm.accentColor.copy(alpha = lmAlpha), 4f, Offset(lm.x * sx, lm.y * sy))
                }
                region.gates.forEach { g ->
                    drawRect(
                        color = if (g.id in state.unlockedGateIds) RunicTeal else AntiqueGold,
                        topLeft = Offset(g.x * sx - 3f, g.y * sy - 3f),
                        size = Size(6f, 6f)
                    )
                }
                region.npcs.forEach { n ->
                    val col = if (n.villageName in state.visitedVillages) Color(0xFF4CC37A) else WarmParchment.copy(alpha = 0.45f)
                    drawCircle(col, 2.8f, Offset(n.x * sx, n.y * sy))
                }
                state.activeEnemies.forEach { e ->
                    drawCircle(if (e.isBoss) FlameOrange else VitalCrimson, if (e.isBoss) 4.8f else 2.8f, Offset(e.x * sx, e.y * sy))
                }
                if (isBosqueFogged) drawRect(Color(0xAA0C1311))
                val px = (state.playerX * sx).coerceIn(4f, size.width - 4f)
                val py = (state.playerY * sy).coerceIn(4f, size.height - 4f)
                drawCircle(Color.White, 5.2f, Offset(px, py))
                drawCircle(FlameOrange, 3.6f, Offset(px, py))
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = InkBlack.copy(alpha = 0.72f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "${state.discoveredRegions.size}/11",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = SparkGold,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Filled.Map,
                        contentDescription = "Abrir mapa completo",
                        tint = SparkGold,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }
    }
}
