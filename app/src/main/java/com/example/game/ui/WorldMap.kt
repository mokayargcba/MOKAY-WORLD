package com.example.game.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.HolidayVillage
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.data.MokayRegionsCatalog
import com.example.game.engine.GameWorldState
import com.example.game.model.LandmarkSpec
import com.example.game.model.NpcSpec
import com.example.game.model.RegionId
import com.example.game.model.RegionSpec
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BoxyLavender
import com.example.ui.theme.CardSlate
import com.example.ui.theme.DeepTwilight
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.InkBlack
import com.example.ui.theme.RunicTeal
import com.example.ui.theme.SparkGold
import com.example.ui.theme.StaminaEmerald
import com.example.ui.theme.WarmParchment
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

private data class VillageSummary(
    val name: String,
    val residents: List<NpcSpec>,
    val x: Float,
    val y: Float,
    val hasShop: Boolean,
    val hasQuest: Boolean
)

@Composable
fun WorldMap(
    state: GameWorldState,
    selectedRegionId: RegionId,
    onSelectRegion: (RegionId) -> Unit,
    modifier: Modifier = Modifier
) {
    var detailFilter by remember { mutableIntStateOf(0) } // 0 = Todos, 1 = Descubiertos, 2 = Pendientes
    val regions = MokayRegionsCatalog.regions

    val totalRegions = regions.size
    val allLandmarks = remember(regions) { regions.values.flatMap { it.landmarks } }
    val allVillageNames = remember(regions) {
        regions.values.flatMap { reg ->
            reg.npcs.mapNotNull { it.villageName.takeIf(String::isNotBlank) }
        }.distinct()
    }
    val discoveredLandmarksCount = allLandmarks.count { it.id in state.discoveredLandmarkIds }
    val visitedVillagesCount = allVillageNames.count { it in state.visitedVillages }
    val totalExploreUnits = (totalRegions + allLandmarks.size + allVillageNames.size).coerceAtLeast(1)
    val achievedExploreUnits = state.discoveredRegions.size + discoveredLandmarksCount + visitedVillagesCount
    val globalProgress = (achievedExploreUnits.toFloat() / totalExploreUnits).coerceIn(0f, 1f)

    val pulseTransition = rememberInfiniteTransition(label = "worldMapPulse")
    val pulseRadius by pulseTransition.animateFloat(
        initialValue = 16f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "pulseRadius"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("world_map_component"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MokayaProgressSummaryBar(
            discoveredRegions = state.discoveredRegions.size,
            totalRegions = totalRegions,
            discoveredLandmarks = discoveredLandmarksCount,
            totalLandmarks = allLandmarks.size,
            visitedVillages = visitedVillagesCount,
            totalVillages = allVillageNames.size,
            forcesCount = state.collectedForces.size,
            overallFraction = globalProgress
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left: Interactive Mokaya Archipelago Map
            ArchipelagoMapCanvas(
                state = state,
                selectedRegionId = selectedRegionId,
                pulseRadius = pulseRadius,
                onSelectRegion = onSelectRegion,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1.04f)
            )

            // Right: Region Selector Chips + Discovered Landmarks & Visited Villages Breakdown
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(end = 4.dp)
                ) {
                    items(RegionId.entries, key = { it.id }) { regionId ->
                        val reg = regions.getValue(regionId)
                        val isFogged = regionId == RegionId.BOSQUE_OLVIDADO &&
                            RegionId.BOSQUE_OLVIDADO !in state.discoveredRegions
                        val isSelected = regionId == selectedRegionId
                        val isDiscovered = regionId in state.discoveredRegions
                        val chipLabel = if (isFogged) "??? Niebla" else regionId.title
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectRegion(regionId) },
                            label = {
                                Text(
                                    text = chipLabel,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1
                                )
                            },
                            leadingIcon = {
                                if (regionId == state.currentRegionId) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(FlameOrange)
                                    )
                                } else if (isDiscovered) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(reg.theme.accentColor)
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = CardSlate,
                                labelColor = WarmParchment.copy(alpha = 0.85f),
                                selectedContainerColor = reg.theme.accentColor.copy(alpha = 0.25f),
                                selectedLabelColor = SparkGold
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = BoxyLavender.copy(alpha = 0.25f),
                                selectedBorderColor = SparkGold
                            ),
                            modifier = Modifier.testTag("world_map_region_chip_${regionId.id}")
                        )
                    }
                }

                // Filter bar for Landmarks & Villages
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Todos", "Descubiertos", "Pendientes").forEachIndexed { idx, label ->
                        val selected = detailFilter == idx
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) RunicTeal.copy(alpha = 0.22f) else CardSlate,
                            border = BorderStroke(
                                1.dp,
                                if (selected) RunicTeal else BoxyLavender.copy(alpha = 0.22f)
                            ),
                            modifier = Modifier
                                .testTag("world_map_filter_$idx")
                                .clickable { detailFilter = idx }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (selected) RunicTeal else WarmParchment.copy(alpha = 0.78f),
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                val regSpec = regions.getValue(selectedRegionId)
                RegionProgressDetailsPanel(
                    region = regSpec,
                    state = state,
                    filterMode = detailFilter,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun MokayaProgressSummaryBar(
    discoveredRegions: Int,
    totalRegions: Int,
    discoveredLandmarks: Int,
    totalLandmarks: Int,
    visitedVillages: Int,
    totalVillages: Int,
    forcesCount: Int,
    overallFraction: Float
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CardSlate.copy(alpha = 0.92f),
        border = BorderStroke(1.2.dp, AntiqueGold.copy(alpha = 0.45f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("world_map_progress_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1.25f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progreso de Mokaya",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = SparkGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${(overallFraction * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = RunicTeal,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                LinearProgressIndicator(
                    progress = { overallFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = RunicTeal,
                    trackColor = DeepTwilight
                )
            }

            SummaryCounterBadge(
                label = "Regiones",
                value = "$discoveredRegions/$totalRegions",
                tint = SparkGold
            )
            SummaryCounterBadge(
                label = "Lugares",
                value = "$discoveredLandmarks/$totalLandmarks",
                tint = FlameOrange
            )
            SummaryCounterBadge(
                label = "Aldeas",
                value = "$visitedVillages/$totalVillages",
                tint = StaminaEmerald
            )
            SummaryCounterBadge(
                label = "Fuerzas",
                value = "$forcesCount/4",
                tint = BoxyLavender
            )
        }
    }
}

@Composable
private fun SummaryCounterBadge(
    label: String,
    value: String,
    tint: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DeepTwilight.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = tint,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = WarmParchment.copy(alpha = 0.82f)
            )
        }
    }
}

@Composable
private fun ArchipelagoMapCanvas(
    state: GameWorldState,
    selectedRegionId: RegionId,
    pulseRadius: Float,
    onSelectRegion: (RegionId) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1F2744), Color(0xFF15172E), Color(0xFF1E0D36))
                )
            )
            .testTag("world_map_canvas")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tap ->
                        var closestRegion: RegionId? = null
                        var bestDist = 48f
                        MokayRegionsCatalog.regions.values.forEach { reg ->
                            val cx = reg.mapX * size.width
                            val cy = reg.mapY * size.height
                            val d = hypot(tap.x - cx, tap.y - cy)
                            if (d < bestDist) {
                                bestDist = d
                                closestRegion = reg.id
                            }
                        }
                        closestRegion?.let(onSelectRegion)
                    }
                }
        ) {
            val regions = MokayRegionsCatalog.regions
            drawMokayaArchipelagoBackdrop(size.width, size.height, pulseRadius)

            // Gate connections between regions
            regions.values.forEach { reg ->
                val start = Offset(reg.mapX * size.width, reg.mapY * size.height)
                reg.gates.forEach { g ->
                    val target = regions[g.toRegion] ?: return@forEach
                    val end = Offset(target.mapX * size.width, target.mapY * size.height)
                    val isAncestralBeam =
                        (reg.id == RegionId.BOSQUE_OLVIDADO && target.id == RegionId.ARBOL_ORIGINAL) ||
                            (reg.id == RegionId.ARBOL_ORIGINAL && target.id == RegionId.BOSQUE_OLVIDADO)
                    val isUnlocked = g.id in state.unlockedGateIds ||
                        (reg.id in state.discoveredRegions && target.id in state.discoveredRegions)
                    drawLine(
                        color = when {
                            isAncestralBeam -> Color(0xFFC77DFF).copy(alpha = 0.72f)
                            isUnlocked -> RunicTeal.copy(alpha = 0.6f)
                            else -> AntiqueGold.copy(alpha = 0.35f)
                        },
                        start = start,
                        end = end,
                        strokeWidth = if (isAncestralBeam) 6f else 3f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Region medallions with exploration completion arc
            regions.values.forEach { reg ->
                val center = Offset(reg.mapX * size.width, reg.mapY * size.height)
                val isCurrent = reg.id == state.currentRegionId
                val isSelected = reg.id == selectedRegionId
                val isDiscovered = reg.id in state.discoveredRegions
                val isBosqueHidden = reg.id == RegionId.BOSQUE_OLVIDADO && !isDiscovered

                val villageNames = reg.npcs.mapNotNull { it.villageName.takeIf(String::isNotBlank) }.distinct()
                val totalPoints = (reg.landmarks.size + villageNames.size).coerceAtLeast(1)
                val earnedPoints = reg.landmarks.count { it.id in state.discoveredLandmarkIds } +
                    villageNames.count { it in state.visitedVillages }
                val progressFrac = earnedPoints.toFloat() / totalPoints
                val isCompletedRegion = earnedPoints == totalPoints

                if (isCurrent) {
                    drawCircle(
                        color = FlameOrange.copy(alpha = 0.38f),
                        radius = pulseRadius * 0.88f,
                        center = center
                    )
                }
                if (isSelected) {
                    drawCircle(
                        color = SparkGold.copy(alpha = 0.8f),
                        radius = 20f,
                        center = center,
                        style = Stroke(2.4f)
                    )
                }

                val r = if (reg.id == RegionId.PLAZA_INFINITA || reg.id == RegionId.ARBOL_ORIGINAL) 14f else 11.5f
                if (isBosqueHidden) {
                    drawCircle(Color(0xFF283735).copy(alpha = 0.92f), 15f, center)
                    drawCircle(BoxyLavender.copy(alpha = 0.5f), 15f, center, style = Stroke(2f))
                } else {
                    drawCircle(
                        color = if (isDiscovered) reg.theme.accentColor else Color(0xFF47445E),
                        radius = r,
                        center = center
                    )
                    drawCircle(
                        color = if (isCurrent) Color.White else InkBlack,
                        radius = r,
                        center = center,
                        style = Stroke(2f)
                    )

                    // Exploration progress arc around the region medallion
                    if (progressFrac > 0f) {
                        val arcRadius = r + 4.5f
                        drawArc(
                            color = if (isCompletedRegion) StaminaEmerald else SparkGold,
                            startAngle = -90f,
                            sweepAngle = 360f * progressFrac,
                            useCenter = false,
                            topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                            size = Size(arcRadius * 2f, arcRadius * 2f),
                            style = Stroke(width = 2.6f, cap = StrokeCap.Round)
                        )
                    }
                    if (isCompletedRegion) {
                        drawCircle(
                            color = StaminaEmerald,
                            radius = 4.2f,
                            center = Offset(center.x + r * 0.85f, center.y - r * 0.85f)
                        )
                    }
                }

                val label = if (isBosqueHidden) "??? (Niebla)" else reg.id.title
                val measured = textMeasurer.measure(
                    label,
                    TextStyle(
                        color = if (isCurrent || isSelected) SparkGold else WarmParchment,
                        fontSize = 8.sp,
                        fontWeight = if (isCurrent || isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                )
                val lx = (center.x - measured.size.width / 2f).coerceIn(3f, size.width - measured.size.width - 3f)
                val ly = (center.y + 16f).coerceAtMost(size.height - measured.size.height - 2f)
                drawRoundRect(
                    color = InkBlack.copy(alpha = 0.68f),
                    topLeft = Offset(lx - 3f, ly - 1f),
                    size = Size(measured.size.width + 6f, measured.size.height + 2f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                drawText(measured, topLeft = Offset(lx, ly))
            }
        }
    }
}

@Composable
private fun RegionProgressDetailsPanel(
    region: RegionSpec,
    state: GameWorldState,
    filterMode: Int,
    modifier: Modifier = Modifier
) {
    val isRegionDiscovered = region.id in state.discoveredRegions
    val isFogged = region.id == RegionId.BOSQUE_OLVIDADO && !isRegionDiscovered

    val villages = remember(region) {
        region.npcs
            .filter { it.villageName.isNotBlank() }
            .groupBy { it.villageName }
            .map { (name, npcs) ->
                VillageSummary(
                    name = name,
                    residents = npcs,
                    x = npcs.map { it.x }.average().toFloat(),
                    y = npcs.map { it.y }.average().toFloat(),
                    hasShop = npcs.any { it.offersShop },
                    hasQuest = npcs.any { it.questId != null }
                )
            }
    }

    val discoveredLmCount = region.landmarks.count { it.id in state.discoveredLandmarkIds }
    val visitedVillageCount = villages.count { it.name in state.visitedVillages }
    val totalPoints = (region.landmarks.size + villages.size).coerceAtLeast(1)
    val regionProgress = (discoveredLmCount + visitedVillageCount).toFloat() / totalPoints

    val filteredLandmarks = region.landmarks.filter { lm ->
        val found = lm.id in state.discoveredLandmarkIds
        when (filterMode) {
            1 -> found
            2 -> !found
            else -> true
        }
    }
    val filteredVillages = villages.filter { v ->
        val visited = v.name in state.visitedVillages
        when (filterMode) {
            1 -> visited
            2 -> !visited
            else -> true
        }
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CardSlate,
        border = BorderStroke(1.5.dp, region.theme.accentColor.copy(alpha = 0.55f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Region Title + Discovery Status + Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isFogged) "Territorio Cubierto por Niebla" else region.id.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = SparkGold,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isFogged) "Nivel 3 • Territorio Restringido" else "${region.id.subtitle} • Nivel ${region.id.tier}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = BoxyLavender
                    )
                }

                val statusLabel = when {
                    region.id == state.currentRegionId -> "ESTÁS AQUÍ"
                    isRegionDiscovered -> "EXPLORADA"
                    else -> "SIN VISITAR"
                }
                val statusColor = when {
                    region.id == state.currentRegionId -> FlameOrange
                    isRegionDiscovered -> StaminaEmerald
                    else -> WarmParchment.copy(alpha = 0.5f)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.65f))
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            if (isFogged) {
                Text(
                    text = "Derrota a Laughing Cat y Street Dino en Sombravía e ilumina la entrada para revelar sus lugares emblemáticos y aldeas.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = WarmParchment.copy(alpha = 0.85f)
                )
                return@Column
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = { regionProgress },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = region.theme.accentColor,
                    trackColor = DeepTwilight
                )
                Text(
                    text = "${(regionProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = SparkGold,
                    fontWeight = FontWeight.Bold
                )
            }

            // Section 1: Lugares Emblemáticos Descubiertos
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Filled.Place, contentDescription = null, tint = SparkGold, modifier = Modifier.size(15.dp))
                Text(
                    text = "Lugares Emblemáticos ($discoveredLmCount/${region.landmarks.size})",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                    color = SparkGold,
                    fontWeight = FontWeight.Bold
                )
            }

            if (filteredLandmarks.isEmpty()) {
                Text(
                    text = if (filterMode == 1) "Aún no descubriste lugares emblemáticos aquí." else "¡Ya descubriste todos los lugares de esta región!",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = WarmParchment.copy(alpha = 0.62f)
                )
            } else {
                filteredLandmarks.forEach { lm ->
                    LandmarkDiscoveryRow(
                        landmark = lm,
                        discovered = lm.id in state.discoveredLandmarkIds,
                        compassZone = regionCompassZone(lm.x, lm.y, region.worldWidth, region.worldHeight)
                    )
                }
            }

            Spacer(Modifier.height(2.dp))

            // Section 2: Aldeas Visitadas
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Filled.HolidayVillage, contentDescription = null, tint = StaminaEmerald, modifier = Modifier.size(15.dp))
                Text(
                    text = "Aldeas Visitadas ($visitedVillageCount/${villages.size})",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                    color = StaminaEmerald,
                    fontWeight = FontWeight.Bold
                )
            }

            if (filteredVillages.isEmpty()) {
                Text(
                    text = if (filterMode == 1) "Camina cerca de los barrios y habla con sus habitantes para registrar aldeas." else "¡Visitaste todas las aldeas de esta región!",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = WarmParchment.copy(alpha = 0.62f)
                )
            } else {
                filteredVillages.forEach { village ->
                    VillageVisitRow(
                        village = village,
                        visited = village.name in state.visitedVillages,
                        compassZone = regionCompassZone(village.x, village.y, region.worldWidth, region.worldHeight)
                    )
                }
            }
        }
    }
}

@Composable
private fun LandmarkDiscoveryRow(
    landmark: LandmarkSpec,
    discovered: Boolean,
    compassZone: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (discovered) DeepTwilight.copy(alpha = 0.92f) else DeepTwilight.copy(alpha = 0.45f),
        border = BorderStroke(
            1.dp,
            if (discovered) landmark.accentColor.copy(alpha = 0.6f) else BoxyLavender.copy(alpha = 0.16f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("landmark_row_${landmark.id}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (discovered) Icons.Filled.AutoAwesome else Icons.Filled.Lock,
                contentDescription = null,
                tint = if (discovered) landmark.accentColor else WarmParchment.copy(alpha = 0.4f),
                modifier = Modifier.size(16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (discovered) landmark.name else "Lugar sin descubrir ($compassZone)",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = if (discovered) WarmParchment else WarmParchment.copy(alpha = 0.55f),
                    fontWeight = if (discovered) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (discovered) landmark.description else "Explora la zona $compassZone para registrarlo.",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (discovered) RunicTeal else WarmParchment.copy(alpha = 0.42f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (discovered) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Descubierto",
                    tint = StaminaEmerald,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun VillageVisitRow(
    village: VillageSummary,
    visited: Boolean,
    compassZone: String
) {
    val residentNames = village.residents.joinToString { it.name }
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (visited) DeepTwilight.copy(alpha = 0.92f) else DeepTwilight.copy(alpha = 0.45f),
        border = BorderStroke(
            1.dp,
            if (visited) StaminaEmerald.copy(alpha = 0.6f) else BoxyLavender.copy(alpha = 0.16f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("village_row_${village.name}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (visited) Icons.Filled.HolidayVillage else Icons.Filled.Explore,
                contentDescription = null,
                tint = if (visited) StaminaEmerald else WarmParchment.copy(alpha = 0.45f),
                modifier = Modifier.size(16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (visited) village.name else "Aldea por visitar ($compassZone)",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = if (visited) WarmParchment else WarmParchment.copy(alpha = 0.6f),
                    fontWeight = if (visited) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (visited) "Habitantes: $residentNames" else "Zona $compassZone • Busca a $residentNames",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (visited) BoxyLavender else WarmParchment.copy(alpha = 0.45f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (visited && village.hasShop) {
                Icon(
                    imageVector = Icons.Filled.Storefront,
                    contentDescription = "Tiene tienda",
                    tint = SparkGold,
                    modifier = Modifier.size(15.dp)
                )
            }
            if (visited) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StaminaEmerald.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Visitada",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = StaminaEmerald,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

internal fun regionCompassZone(x: Float, y: Float, width: Float, height: Float): String {
    val vert = when {
        y < height * 0.34f -> "Norte"
        y > height * 0.66f -> "Sur"
        else -> ""
    }
    val horiz = when {
        x < width * 0.34f -> "Oeste"
        x > width * 0.66f -> "Este"
        else -> ""
    }
    return when {
        vert.isEmpty() && horiz.isEmpty() -> "Centro"
        vert.isNotEmpty() && horiz.isNotEmpty() -> when ("$vert-$horiz") {
            "Norte-Este" -> "Noreste"
            "Norte-Oeste" -> "Noroeste"
            "Sur-Este" -> "Sureste"
            "Sur-Oeste" -> "Suroeste"
            else -> "$vert $horiz"
        }
        else -> vert.ifEmpty { horiz }
    }
}

internal fun DrawScope.drawMokayaArchipelagoBackdrop(
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
