package com.example.game.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.game.model.RegionId
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BoxyLavender
import com.example.ui.theme.CardSlate
import com.example.ui.theme.DeepTwilight
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.InkBlack
import com.example.ui.theme.RunicTeal
import com.example.ui.theme.SparkGold
import com.example.ui.theme.VitalCrimson
import com.example.ui.theme.WarmParchment
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun WorldMapDialog(
    state: GameWorldState,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var inspectedRegion by remember { mutableStateOf(state.currentRegionId) }
    val textMeasurer = rememberTextMeasurer()

    val pulseTransition = rememberInfiniteTransition(label = "herePulse")
    val pulseRadius by pulseTransition.animateFloat(
        initialValue = 16f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "pulse"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 820.dp)
                .heightIn(max = 408.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DeepTwilight),
            border = BorderStroke(2.dp, AntiqueGold.copy(alpha = 0.65f))
        ) {
            Column(
                modifier = Modifier
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
                        modifier = Modifier.size(48.dp).testTag("close_map_button")
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar mapa", tint = WarmParchment)
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (selectedTab == 0) {
                    // Horizontal 2-column layout so Mokaya's Archipelago retains its authentic 1:1 circular proportions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(310.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Left: Square-proportioned Illustrated Mokaya World Map
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1.04f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF1F2744), Color(0xFF15172E), Color(0xFF1E0D36))
                                    )
                                )
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(state.discoveredRegions) {
                                        detectTapGestures { tap ->
                                            MokayRegionsCatalog.regions.values.forEach { reg ->
                                                val cx = reg.mapX * size.width
                                                val cy = reg.mapY * size.height
                                                if (hypot(tap.x - cx, tap.y - cy) < 42f) {
                                                    inspectedRegion = reg.id
                                                }
                                            }
                                        }
                                    }
                            ) {
                                val regions = MokayRegionsCatalog.regions
                                drawMokayaArchipelagoBackdrop(size.width, size.height, pulseRadius)

                                // Bridges connecting each region's gates
                                regions.values.forEach { reg ->
                                    val start = Offset(reg.mapX * size.width, reg.mapY * size.height)
                                    reg.gates.forEach { g ->
                                        val target = regions[g.toRegion] ?: return@forEach
                                        val end = Offset(target.mapX * size.width, target.mapY * size.height)
                                        val isAncestralBeam =
                                            (reg.id == RegionId.BOSQUE_OLVIDADO && target.id == RegionId.ARBOL_ORIGINAL) ||
                                                (reg.id == RegionId.ARBOL_ORIGINAL && target.id == RegionId.BOSQUE_OLVIDADO)
                                        drawLine(
                                            color = if (isAncestralBeam) Color(0xFFC77DFF).copy(alpha = 0.72f) else AntiqueGold.copy(alpha = 0.45f),
                                            start = start,
                                            end = end,
                                            strokeWidth = if (isAncestralBeam) 6f else 3.2f,
                                            cap = StrokeCap.Round
                                        )
                                    }
                                }

                                // Region district medallions
                                regions.values.forEach { reg ->
                                    val center = Offset(reg.mapX * size.width, reg.mapY * size.height)
                                    val isCurrent = reg.id == state.currentRegionId
                                    val isSelected = reg.id == inspectedRegion
                                    val isDiscovered = reg.id in state.discoveredRegions
                                    val isBosqueHidden = reg.id == RegionId.BOSQUE_OLVIDADO && !isDiscovered

                                    if (isCurrent) {
                                        drawCircle(
                                            color = FlameOrange.copy(alpha = 0.38f),
                                            radius = pulseRadius * 0.88f,
                                            center = center
                                        )
                                    }
                                    if (isSelected && !isCurrent) {
                                        drawCircle(
                                            color = SparkGold.copy(alpha = 0.45f),
                                            radius = 19f,
                                            center = center,
                                            style = Stroke(2.2f)
                                        )
                                    }

                                    if (isBosqueHidden) {
                                        drawCircle(Color(0xFF283735).copy(alpha = 0.92f), 16f, center)
                                        drawCircle(BoxyLavender.copy(alpha = 0.5f), 16f, center, style = Stroke(2f))
                                    } else {
                                        val r = if (reg.id == RegionId.PLAZA_INFINITA || reg.id == RegionId.ARBOL_ORIGINAL) 15f else 12f
                                        drawCircle(
                                            color = if (isDiscovered) reg.theme.accentColor else Color(0xFF47445E),
                                            radius = r,
                                            center = center
                                        )
                                        drawCircle(
                                            color = if (isCurrent) Color.White else InkBlack,
                                            radius = r,
                                            center = center,
                                            style = Stroke(2.2f)
                                        )
                                    }

                                    val label = if (isBosqueHidden) "??? (Niebla)" else reg.id.title
                                    val measured = textMeasurer.measure(
                                        label,
                                        TextStyle(
                                            color = if (isCurrent) SparkGold else WarmParchment,
                                            fontSize = 8.sp,
                                            fontWeight = if (isCurrent || isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    )
                                    val lx = (center.x - measured.size.width / 2f).coerceIn(3f, size.width - measured.size.width - 3f)
                                    val ly = (center.y + 15f).coerceAtMost(size.height - measured.size.height - 2f)
                                    drawRoundRect(
                                        color = InkBlack.copy(alpha = 0.62f),
                                        topLeft = Offset(lx - 3f, ly - 1f),
                                        size = Size(measured.size.width + 6f, measured.size.height + 2f),
                                        cornerRadius = CornerRadius(4f, 4f)
                                    )
                                    drawText(measured, topLeft = Offset(lx, ly))
                                }
                            }
                        }

                        // Right: 4 Ancestral Forces + Inspected Region Information
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Fuerzas Ancestrales",
                                style = MaterialTheme.typography.labelLarge,
                                color = AntiqueGold
                            )
                            val forces = listOf("CREATIVIDAD", "VALENTIA", "CURIOSIDAD", "AMISTAD")
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                for (row in 0..1) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                        for (col in 0..1) {
                                            val force = forces[row * 2 + col]
                                            val unlocked = force in state.collectedForces
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = if (unlocked) RunicTeal.copy(alpha = 0.22f) else CardSlate,
                                                border = BorderStroke(1.dp, if (unlocked) RunicTeal else BoxyLavender.copy(alpha = 0.25f)),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = if (unlocked) "✓ $force" else force,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    color = if (unlocked) RunicTeal else WarmParchment.copy(alpha = 0.55f),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            val regInfo = MokayRegionsCatalog.regions.getValue(inspectedRegion)
                            val isFogged = inspectedRegion == RegionId.BOSQUE_OLVIDADO &&
                                RegionId.BOSQUE_OLVIDADO !in state.discoveredRegions
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = CardSlate,
                                border = BorderStroke(1.5.dp, regInfo.theme.accentColor.copy(alpha = 0.55f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = if (isFogged) "Territorio Cubierto por Niebla" else "${regInfo.id.title} — ${regInfo.id.subtitle}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = SparkGold
                                    )
                                    Text(
                                        text = if (isFogged) {
                                            "Derrota a Laughing Cat y Street Dino en Sombravía e ilumina el bosque para revelarlo."
                                        } else {
                                            val prefix = if (inspectedRegion == state.currentRegionId) "ESTÁS AQUÍ • " else ""
                                            "${prefix}Nivel ${regInfo.id.tier} • Escala 3:2 (${regInfo.worldWidth.toInt()}x${regInfo.worldHeight.toInt()})"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = RunicTeal
                                    )
                                    if (!isFogged) {
                                        Text(
                                            text = "Lugares: ${regInfo.landmarks.joinToString { it.name }}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                            color = WarmParchment.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Toca cualquier región del archipiélago para ver sus detalles.",
                                style = MaterialTheme.typography.labelSmall,
                                color = WarmParchment.copy(alpha = 0.65f)
                            )
                        }
                    }
                } else {
                    // Current District Detailed 3:2 Map
                    val currentReg = MokayRegionsCatalog.regions.getValue(state.currentRegionId)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(306.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(currentReg.theme.waterOrVoid)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
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

                            currentReg.landmarks.forEach { lm ->
                                val lx = lm.x * sx
                                val ly = lm.y * sy
                                drawRoundRect(
                                    color = lm.accentColor.copy(alpha = 0.35f),
                                    topLeft = Offset(lx - (lm.width * sx) / 2f, ly - (lm.height * sy) / 2f),
                                    size = Size(lm.width * sx, lm.height * sy),
                                    cornerRadius = CornerRadius(8f, 8f)
                                )
                                val m = textMeasurer.measure(
                                    lm.name,
                                    TextStyle(color = WarmParchment, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                )
                                drawText(m, topLeft = Offset((lx - m.size.width / 2f).coerceIn(2f, size.width - m.size.width), ly - 6f))
                            }

                            currentReg.npcs.forEach { n ->
                                drawCircle(Color(0xFF4CC37A), 5f, Offset(n.x * sx, n.y * sy))
                            }
                            state.activeEnemies.forEach { e ->
                                drawCircle(if (e.isBoss) FlameOrange else VitalCrimson, if (e.isBoss) 8f else 5f, Offset(e.x * sx, e.y * sy))
                            }

                            val px = (state.playerX * sx).coerceIn(8f, size.width - 8f)
                            val py = (state.playerY * sy).coerceIn(8f, size.height - 8f)
                            drawCircle(FlameOrange.copy(alpha = 0.35f), pulseRadius * 0.55f, Offset(px, py))
                            drawCircle(Color.White, 7f, Offset(px, py))
                            drawCircle(FlameOrange, 4.5f, Offset(px, py))
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawMokayaArchipelagoBackdrop(
    w: Float,
    h: Float,
    pulseRadius: Float
) {
    // 1. Upper Circular Floating Archipelago Ring + Lagoon
    val ringCenter = Offset(w * 0.50f, h * 0.33f)
    val outerRx = w * 0.41f
    val outerRy = h * 0.25f
    drawOval(
        color = Color(0xFF3E352B),
        topLeft = Offset(ringCenter.x - outerRx, ringCenter.y - outerRy + 8f),
        size = Size(outerRx * 2f, outerRy * 2f)
    )
    drawOval(
        brush = Brush.radialGradient(
            listOf(Color(0xFF28A4B8), Color(0xFF1F6E82), Color(0xFF2F5E3F)),
            center = ringCenter,
            radius = outerRx
        ),
        topLeft = Offset(ringCenter.x - outerRx, ringCenter.y - outerRy),
        size = Size(outerRx * 2f, outerRy * 2f)
    )
    drawOval(
        color = Color(0xFF55A65A).copy(alpha = 0.55f),
        topLeft = Offset(ringCenter.x - outerRx, ringCenter.y - outerRy),
        size = Size(outerRx * 2f, outerRy * 2f),
        style = Stroke(width = 16f)
    )
    // Central Plaza Infinita stone disc
    drawCircle(Color(0xFFE0CFA8).copy(alpha = 0.85f), w * 0.08f, ringCenter)
    drawCircle(Color(0xFF9D4EDD).copy(alpha = 0.45f), w * 0.045f, ringCenter)

    // 2. Bosque Olvidado misty southern terrace belt
    drawOval(
        brush = Brush.verticalGradient(listOf(Color(0xFF1B4539), Color(0xFF112A24))),
        topLeft = Offset(w * 0.16f, h * 0.58f),
        size = Size(w * 0.68f, h * 0.16f)
    )
    drawOval(
        color = Color(0xFF6B8A82).copy(alpha = 0.25f),
        topLeft = Offset(w * 0.18f, h * 0.60f),
        size = Size(w * 0.64f, h * 0.13f),
        style = Stroke(width = 4f)
    )

    // 3. Portal Ancestral violet vertical beam descending to Árbol Original
    drawLine(
        brush = Brush.verticalGradient(
            listOf(Color(0xFFE0AAFF), Color(0xFF9D4EDD), FlameOrange),
            startY = h * 0.68f,
            endY = h * 0.88f
        ),
        start = Offset(w * 0.50f, h * 0.68f),
        end = Offset(w * 0.50f, h * 0.88f),
        strokeWidth = 7f + (pulseRadius - 16f) * 0.25f,
        cap = StrokeCap.Round
    )

    // 4. Radiant 7-color Árbol Original canopy + roots at base
    val treeCenter = Offset(w * 0.50f, h * 0.88f)
    val rainbow = listOf(
        Color(0xFFFF6A3D), Color(0xFFFFC24D), Color(0xFF4CC37A),
        Color(0xFF2EC4B6), Color(0xFF6C63FF), Color(0xFFE97893), Color(0xFF9D4EDD)
    )
    rainbow.forEachIndexed { i, col ->
        val ang = -PI * 0.85 + i * (PI * 0.7 / 6.0)
        val bx = treeCenter.x + cos(ang).toFloat() * (w * 0.18f)
        val by = treeCenter.y - sin(ang).toFloat().let { kotlin.math.abs(it) } * (h * 0.08f)
        val branch = Path().apply {
            moveTo(treeCenter.x, treeCenter.y)
            quadraticTo((treeCenter.x + bx) * 0.5f, treeCenter.y - 18f, bx, by)
        }
        drawPath(branch, col.copy(alpha = 0.65f), style = Stroke(width = 5f, cap = StrokeCap.Round))
    }
    drawOval(
        brush = Brush.radialGradient(listOf(SparkGold.copy(alpha = 0.55f), Color.Transparent), treeCenter, w * 0.22f),
        topLeft = Offset(treeCenter.x - w * 0.22f, treeCenter.y - h * 0.09f),
        size = Size(w * 0.44f, h * 0.18f)
    )
}
