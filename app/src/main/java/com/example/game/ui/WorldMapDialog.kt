package com.example.game.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.data.MokayRegionsCatalog
import com.example.game.engine.GameWorldState
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.CardSlate
import com.example.ui.theme.DeepTwilight
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.RunicTeal
import com.example.ui.theme.SparkGold
import com.example.ui.theme.StaminaEmerald
import com.example.ui.theme.VitalCrimson
import com.example.ui.theme.WarmParchment

@Composable
fun WorldMapDialog(
    state: GameWorldState,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var inspectedRegion by remember { mutableStateOf(state.currentRegionId) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 860.dp)
                .heightIn(max = 416.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DeepTwilight),
            border = BorderStroke(2.dp, AntiqueGold.copy(alpha = 0.65f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Atlas de Mokaya",
                        style = MaterialTheme.typography.titleLarge,
                        color = WarmParchment
                    )
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = CardSlate,
                        contentColor = SparkGold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 14.dp)
                            .clip(RoundedCornerShape(14.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            modifier = Modifier.testTag("map_tab_world"),
                            text = { Text("Mokaya (11 Regiones)", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            icon = { Icon(Icons.Filled.Explore, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            modifier = Modifier.testTag("map_tab_district"),
                            text = { Text("Distrito Actual", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            icon = { Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("close_map_button")
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar mapa", tint = WarmParchment)
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (selectedTab == 0) {
                    WorldMap(
                        state = state,
                        selectedRegionId = inspectedRegion,
                        onSelectRegion = { inspectedRegion = it },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                } else {
                    CurrentDistrictRadar(
                        state = state,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentDistrictRadar(
    state: GameWorldState,
    modifier: Modifier = Modifier
) {
    val currentReg = MokayRegionsCatalog.regions.getValue(state.currentRegionId)
    val textMeasurer = rememberTextMeasurer()
    val pulseTransition = rememberInfiniteTransition(label = "districtPulse")
    val pulseRadius by pulseTransition.animateFloat(
        initialValue = 12f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(tween(850), RepeatMode.Reverse),
        label = "pulse"
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(currentReg.theme.waterOrVoid)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                val sx = size.width / currentReg.worldWidth
                val sy = size.height / currentReg.worldHeight

                drawRoundRect(
                    color = currentReg.theme.groundPrimary,
                    size = size,
                    cornerRadius = CornerRadius(24f, 24f)
                )

                currentReg.gates.forEach { g ->
                    drawLine(
                        color = currentReg.theme.pathColor.copy(alpha = 0.35f),
                        start = Offset(size.width / 2f, size.height / 2f),
                        end = Offset(g.x * sx, g.y * sy),
                        strokeWidth = 6f
                    )
                    drawRect(
                        color = if (g.id in state.unlockedGateIds) RunicTeal else AntiqueGold,
                        topLeft = Offset(g.x * sx - 8f, g.y * sy - 8f),
                        size = Size(16f, 16f)
                    )
                }

                // Landmarks: solid glow & name if discovered, muted dashed marker "?" if undiscovered
                currentReg.landmarks.forEach { lm ->
                    val lx = lm.x * sx
                    val ly = lm.y * sy
                    val discovered = lm.id in state.discoveredLandmarkIds
                    val rectTopLeft = Offset(lx - (lm.width * sx) / 2f, ly - (lm.height * sy) / 2f)
                    val rectSize = Size(lm.width * sx, lm.height * sy)
                    drawRoundRect(
                        color = if (discovered) lm.accentColor.copy(alpha = 0.38f) else Color.White.copy(alpha = 0.10f),
                        topLeft = rectTopLeft,
                        size = rectSize,
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                    if (!discovered) {
                        drawRoundRect(
                            color = WarmParchment.copy(alpha = 0.35f),
                            topLeft = rectTopLeft,
                            size = rectSize,
                            cornerRadius = CornerRadius(8f, 8f),
                            style = Stroke(width = 1.6f)
                        )
                    }
                    val label = if (discovered) lm.name else "?"
                    val m = textMeasurer.measure(
                        label,
                        TextStyle(
                            color = if (discovered) SparkGold else WarmParchment.copy(alpha = 0.65f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    drawText(
                        m,
                        topLeft = Offset(
                            (lx - m.size.width / 2f).coerceIn(2f, size.width - m.size.width),
                            ly - 6f
                        )
                    )
                }

                // Villages & NPCs
                currentReg.npcs.forEach { n ->
                    val visited = n.villageName.isNotBlank() && n.villageName in state.visitedVillages
                    val pos = Offset(n.x * sx, n.y * sy)
                    if (visited) {
                        drawCircle(StaminaEmerald, 6f, pos)
                        if (n.villageName.isNotBlank()) {
                            val m = textMeasurer.measure(
                                n.villageName,
                                TextStyle(color = StaminaEmerald, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            )
                            drawText(
                                m,
                                topLeft = Offset(
                                    (pos.x - m.size.width / 2f).coerceIn(2f, size.width - m.size.width),
                                    (pos.y + 7f).coerceAtMost(size.height - m.size.height)
                                )
                            )
                        }
                    } else {
                        drawCircle(WarmParchment.copy(alpha = 0.45f), 5f, pos, style = Stroke(2f))
                    }
                }

                state.activeEnemies.forEach { e ->
                    drawCircle(
                        color = if (e.isBoss) FlameOrange else VitalCrimson,
                        radius = if (e.isBoss) 8f else 5f,
                        center = Offset(e.x * sx, e.y * sy)
                    )
                }

                val px = (state.playerX * sx).coerceIn(8f, size.width - 8f)
                val py = (state.playerY * sy).coerceIn(8f, size.height - 8f)
                drawCircle(FlameOrange.copy(alpha = 0.35f), pulseRadius * 0.7f, Offset(px, py))
                drawCircle(Color.White, 7f, Offset(px, py))
                drawCircle(FlameOrange, 4.5f, Offset(px, py))
            }
        }

        // District Legend Bar
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = CardSlate,
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DistrictLegendItem(FlameOrange, "Boxy")
                DistrictLegendItem(SparkGold, "Lugar Descubierto")
                DistrictLegendItem(StaminaEmerald, "Aldea Visitada")
                DistrictLegendItem(RunicTeal, "Puerta")
                DistrictLegendItem(VitalCrimson, "Enemigo")
            }
        }
    }
}

@Composable
private fun DistrictLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = WarmParchment.copy(alpha = 0.9f)
        )
    }
}
