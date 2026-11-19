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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.data.MokayRegionsCatalog
import com.example.game.data.MokayWorldCatalog
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
import kotlin.math.hypot

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
    onOpenMap: () -> Unit,
    onOpenGrimoire: () -> Unit,
    onOpenQuests: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeBoss = state.activeEnemies.firstOrNull { it.isBoss }

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
            // Top-left Player Vitals & Region Badge
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.width(184.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DeepTwilight.copy(alpha = 0.84f),
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
                            Text(
                                text = "BOXY",
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                                color = WarmParchment,
                                fontWeight = FontWeight.Bold
                            )
                            SyncBadge(syncState = syncState, onSignOut = onSignOut)
                        }

                        VitalBar(
                            label = "VIDA",
                            current = state.hp.toFloat(),
                            max = state.maxHp.toFloat(),
                            barColor = VitalCrimson
                        )

                        StaminaVitalBar(
                            stamina = state.stamina,
                            maxStamina = state.maxStamina,
                            exhausted = state.exhausted
                        )

                        VitalBar(
                            label = "MAGIA",
                            current = state.mana,
                            max = state.maxMana,
                            barColor = ManaIndigo
                        )
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
            }

            // Top-right Minimap & Action Menus
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Top
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    HudMenuIconButton(
                        icon = Icons.Filled.Backpack,
                        label = "Mochila",
                        tint = SparkGold,
                        testTag = "open_inventory_button",
                        onClick = onOpenInventory
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
                }

                MinimapCard(
                    state = state,
                    onClick = onOpenMap
                )
            }
        }

        // BOSS HEALTH BAR (Docked cleanly at top-center so the horizontal center playfield stays open)
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
                                .background(
                                    Brush.horizontalGradient(listOf(VitalCrimson, FlameOrange))
                                )
                        )
                    }
                }
            }
        }

        // BOTTOM ROW: Left Virtual Joystick + Center Quick Slots + Right Combat & Interaction Cluster
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // Bottom-Left: 360° Virtual Joystick
            VirtualJoystick(
                onMove = onJoystickMove,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 4.dp, bottom = 2.dp)
            )

            // Bottom-Center: 3 Quick-Use Consumable Slots
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
                        name = item?.name ?: itemId,
                        quantity = qty,
                        color = item?.let { Color(it.colorHex) } ?: SparkGold,
                        onClick = { onUseQuickSlot(itemId) },
                        modifier = Modifier.testTag("quick_slot_$idx")
                    )
                }
            }

            // Bottom-Right: Action Cluster (Attack, Dodge, Foskay, Interact)
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
    onSignOut: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val (icon, tint, label) = when (syncState) {
            is CloudSyncUiState.Syncing -> Triple(Icons.Filled.CloudSync, SparkGold, "...")
            is CloudSyncUiState.Synced -> Triple(Icons.Filled.CloudDone, RunicTeal, "Nube")
            is CloudSyncUiState.Error -> Triple(Icons.Filled.CloudOff, VitalCrimson, "Local")
        }
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = tint
        )
        IconButton(
            onClick = onSignOut,
            modifier = Modifier
                .size(28.dp)
                .testTag("sign_out_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Cerrar sesión",
                tint = WarmParchment.copy(alpha = 0.75f),
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
private fun VitalBar(
    label: String,
    current: Float,
    max: Float,
    barColor: Color
) {
    val fraction = if (max > 0f) (current / max).coerceIn(0f, 1f) else 0f
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = WarmParchment.copy(alpha = 0.85f)
            )
            Text(
                text = "${current.toInt()}/${max.toInt()}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = WarmParchment.copy(alpha = 0.85f)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(InkBlack.copy(alpha = 0.65f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(7.dp)
                    .background(barColor)
            )
        }
    }
}

@Composable
private fun StaminaVitalBar(
    stamina: Float,
    maxStamina: Float,
    exhausted: Boolean
) {
    val pulse = rememberInfiniteTransition(label = "fatiguePulse")
    val alpha by pulse.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(420),
            repeatMode = RepeatMode.Reverse
        ),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (exhausted) "CANSADO" else "ESTAMINA",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = if (exhausted) FatigueAmber else WarmParchment.copy(alpha = 0.85f),
                    fontWeight = if (exhausted) FontWeight.ExtraBold else FontWeight.SemiBold,
                    modifier = Modifier.alpha(if (exhausted) alpha else 1f)
                )
                if (exhausted) {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = "Cansado",
                        tint = FatigueAmber,
                        modifier = Modifier
                            .size(11.dp)
                            .alpha(alpha)
                    )
                }
            }
            Text(
                text = "${stamina.toInt()}/${maxStamina.toInt()}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = WarmParchment.copy(alpha = 0.85f)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(InkBlack.copy(alpha = 0.65f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(7.dp)
                    .alpha(if (exhausted) alpha else 1f)
                    .background(barColor)
            )
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
        modifier = Modifier
            .size(48.dp)
            .testTag(testTag)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun MinimapCard(
    state: GameWorldState,
    onClick: () -> Unit
) {
    val region = MokayRegionsCatalog.regions.getValue(state.currentRegionId)
    val isBosqueFogged = state.currentRegionId == RegionId.BOSQUE_OLVIDADO &&
        RegionId.BOSQUE_OLVIDADO !in state.discoveredRegions

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DeepTwilight.copy(alpha = 0.90f),
        border = BorderStroke(1.8.dp, AntiqueGold.copy(alpha = 0.7f)),
        modifier = Modifier
            .size(88.dp)
            .testTag("minimap_button")
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                val sx = size.width / region.worldWidth
                val sy = size.height / region.worldHeight

                drawRoundRect(
                    color = region.theme.groundPrimary,
                    size = size,
                    cornerRadius = CornerRadius(12f, 12f)
                )

                // Landmarks
                region.landmarks.forEach { lm ->
                    drawCircle(
                        color = lm.accentColor.copy(alpha = 0.65f),
                        radius = 4f,
                        center = Offset(lm.x * sx, lm.y * sy)
                    )
                }

                // Gates
                region.gates.forEach { g ->
                    drawRect(
                        color = if (g.id in state.unlockedGateIds) RunicTeal else AntiqueGold,
                        topLeft = Offset(g.x * sx - 3f, g.y * sy - 3f),
                        size = Size(6f, 6f)
                    )
                }

                // NPCs
                region.npcs.forEach { n ->
                    drawCircle(
                        color = Color(0xFF4CC37A),
                        radius = 2.6f,
                        center = Offset(n.x * sx, n.y * sy)
                    )
                }

                // Active enemies / Bosses
                state.activeEnemies.forEach { e ->
                    drawCircle(
                        color = if (e.isBoss) FlameOrange else VitalCrimson,
                        radius = if (e.isBoss) 4.8f else 2.8f,
                        center = Offset(e.x * sx, e.y * sy)
                    )
                }

                // Fog for Bosque Olvidado if unlit
                if (isBosqueFogged) {
                    drawRect(Color(0xAA0C1311))
                }

                // Boxy position dot
                val px = (state.playerX * sx).coerceIn(4f, size.width - 4f)
                val py = (state.playerY * sy).coerceIn(4f, size.height - 4f)
                drawCircle(Color.White, 5.2f, Offset(px, py))
                drawCircle(FlameOrange, 3.6f, Offset(px, py))
            }

            Icon(
                imageVector = Icons.Filled.Map,
                contentDescription = "Abrir mapa completo",
                tint = SparkGold,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(14.dp)
            )
        }
    }
}

@Composable
private fun VirtualJoystick(
    onMove: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var knobOffset by remember { mutableStateOf(Offset.Zero) }
    val outerRadiusPx = 115f

    Box(
        modifier = modifier
            .size(116.dp)
            .clip(CircleShape)
            .background(DeepTwilight.copy(alpha = 0.45f))
            .border(2.dp, BoxyLavender.copy(alpha = 0.45f), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val delta = offset - center
                        val dist = hypot(delta.x, delta.y).coerceAtLeast(1f)
                        val clamped = if (dist > outerRadiusPx) delta * (outerRadiusPx / dist) else delta
                        knobOffset = clamped
                        onMove(clamped.x / outerRadiusPx, clamped.y / outerRadiusPx)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val next = knobOffset + dragAmount
                        val dist = hypot(next.x, next.y).coerceAtLeast(1f)
                        val clamped = if (dist > outerRadiusPx) next * (outerRadiusPx / dist) else next
                        knobOffset = clamped
                        onMove(clamped.x / outerRadiusPx, clamped.y / outerRadiusPx)
                    },
                    onDragEnd = {
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    },
                    onDragCancel = {
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                color = WarmParchment.copy(alpha = 0.14f),
                radius = size.minDimension * 0.36f,
                center = center,
                style = Stroke(width = 2f)
            )
            val knobPos = center + knobOffset * 0.48f
            drawCircle(
                color = BoxyLavender.copy(alpha = 0.85f),
                radius = 24f,
                center = knobPos
            )
            drawCircle(
                color = InkBlack,
                radius = 24f,
                center = knobPos,
                style = Stroke(width = 3f)
            )
            drawCircle(
                color = FlameOrange,
                radius = 9f,
                center = knobPos
            )
        }
    }
}

@Composable
private fun QuickSlotPill(
    slotNumber: Int,
    name: String,
    quantity: Int,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DeepTwilight.copy(alpha = if (quantity > 0) 0.9f else 0.55f),
        border = BorderStroke(
            1.5.dp,
            if (quantity > 0) color.copy(alpha = 0.85f) else Color.Gray.copy(alpha = 0.35f)
        ),
        modifier = modifier
            .size(width = 62.dp, height = 52.dp)
            .clickable(enabled = quantity > 0, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.TopStart)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = "x$quantity",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = if (quantity > 0) SparkGold else Color.Gray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopEnd)
            )
            Text(
                text = name.split(" ").firstOrNull() ?: "$slotNumber",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = WarmParchment,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun ActionOrbButton(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    contentColor: Color,
    size: Dp,
    testTag: String,
    enabled: Boolean = true,
    highlighted: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = containerColor,
        border = BorderStroke(
            width = if (highlighted) 2.dp else 1.2.dp,
            color = if (highlighted) SparkGold else InkBlack.copy(alpha = 0.7f)
        ),
        shadowElevation = if (highlighted) 6.dp else 2.dp,
        modifier = Modifier
            .size(size)
            .alpha(if (enabled) 1f else 0.45f)
            .testTag(testTag)
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(if (size >= 60.dp) 24.dp else 20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = contentColor,
                maxLines = 1
            )
        }
    }
}
