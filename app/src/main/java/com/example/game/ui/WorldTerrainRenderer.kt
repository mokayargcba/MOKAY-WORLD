package com.example.game.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import com.example.game.model.RegionId
import com.example.game.model.RegionSpec
import com.example.ui.theme.InkBlack
import com.example.ui.theme.SparkGold
import com.example.ui.theme.WarmParchment
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

internal fun DrawScope.drawWorldEnvironmentBase(
    region: RegionSpec,
    animPhase: Float
) {
    val w = region.worldWidth
    val h = region.worldHeight
    val theme = region.theme

    // 1. Deep void / animated ocean / starry sky backdrop
    drawRect(theme.waterOrVoid)
    when {
        region.hasWaterMoat || region.isSportsZone ->
            drawAnimatedWaterBackdrop(w, h, theme.waterOrVoid, region.id, animPhase)
        region.hasCloudsBorder || region.id == RegionId.CONSTELARIA || region.id == RegionId.ARBOL_ORIGINAL ->
            drawSkyOrCosmicBackdrop(w, h, region.id, animPhase)
        else -> {
            for (i in 0 until 18) {
                val rx = ((i * 211) % w.toInt()).toFloat()
                val ry = ((i * 157) % h.toInt()).toFloat()
                drawCircle(
                    color = theme.accentColor.copy(alpha = 0.065f),
                    radius = 120f + (i % 3) * 44f,
                    center = Offset(rx, ry)
                )
            }
        }
    }

    // 2. 3D Stepped Island Cliff Base & Drop Shadow
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.42f),
        topLeft = Offset(24f, 48f),
        size = Size(w - 48f, h - 46f),
        cornerRadius = CornerRadius(62f, 62f)
    )
    val cliffColor = when (region.id) {
        RegionId.TINTOPOLIS -> Color(0xFF222222)
        RegionId.ARTELIA -> Color(0xFF8C7355)
        RegionId.PICASIA -> Color(0xFF2B1C47)
        RegionId.SOMBRAVIA -> Color(0xFF1A0F2E)
        RegionId.MAREA_VIVA -> Color(0xFF1B4A59)
        RegionId.ARBOL_ORIGINAL -> Color(0xFF2C1347)
        else -> Color(0xFF42362B)
    }
    drawRoundRect(
        color = cliffColor,
        topLeft = Offset(28f, 36f),
        size = Size(w - 56f, h - 50f),
        cornerRadius = CornerRadius(56f, 56f)
    )
    drawRoundRect(
        color = InkBlack,
        topLeft = Offset(28f, 36f),
        size = Size(w - 56f, h - 50f),
        cornerRadius = CornerRadius(56f, 56f),
        style = Stroke(3.2f)
    )

    // Vertical rock striations along the southern cliff lip
    var sx = 96f
    while (sx < w - 96f) {
        drawLine(
            color = Color.Black.copy(alpha = 0.28f),
            start = Offset(sx, h - 34f),
            end = Offset(sx + 6f, h - 18f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
        sx += 34f
    }

    // 3. Main District Turf / Stone Terrace Surface
    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(theme.groundSecondary, theme.groundPrimary),
            center = Offset(w / 2f, h / 2f),
            radius = w * 0.68f
        ),
        topLeft = Offset(30f, 24f),
        size = Size(w - 60f, h - 62f),
        cornerRadius = CornerRadius(54f, 54f)
    )

    // Biome-specific ground texture & stonework
    drawTerrainTexturePatches(region, animPhase)

    // Decorative terrace stone wall / glowing balustrade trim
    val rimColor = when (region.id) {
        RegionId.TINTOPOLIS -> Color(0xFF9E9E9E)
        RegionId.ARTELIA -> Color(0xFFE6D5B8)
        RegionId.SOMBRAVIA -> Color(0xFF7B2CBF)
        else -> theme.accentColor.copy(alpha = 0.72f)
    }
    drawRoundRect(
        color = rimColor,
        topLeft = Offset(30f, 24f),
        size = Size(w - 60f, h - 62f),
        cornerRadius = CornerRadius(54f, 54f),
        style = Stroke(width = 6f)
    )
    drawRoundRect(
        color = InkBlack.copy(alpha = 0.65f),
        topLeft = Offset(34f, 28f),
        size = Size(w - 68f, h - 70f),
        cornerRadius = CornerRadius(50f, 50f),
        style = Stroke(width = 2f)
    )

    // Clouds & cascading waterfalls for floating/water islands
    if (region.hasCloudsBorder || region.id == RegionId.PLAZA_INFINITA || region.id == RegionId.BOSQUE_OLVIDADO) {
        drawFloatingIslandCloudsAndFalls(region, animPhase)
    }

    // 4. Paved Stone / Boardwalk / Neon Avenues connecting Center Plaza to every Gate
    val centerX = w / 2f
    val centerY = h / 2f
    region.gates.forEach { gate ->
        drawPavedAvenue(
            start = Offset(centerX, centerY),
            end = Offset(gate.x, gate.y),
            roadColor = theme.pathColor,
            regionId = region.id
        )
    }

    // 5. Biome-faithful Radial Cobblestone / Mosaic / Checkerboard Central Plaza
    drawRadialCobblestonePlaza(
        center = Offset(centerX, centerY - 14f),
        radius = region.cobblestonePlazaRadius,
        stoneColor = theme.pathColor,
        accentColor = theme.accentColor,
        regionId = region.id,
        animPhase = animPhase
    )

    // 6. Special Biome Ground Markings
    if (region.id == RegionId.ARBOL_ORIGINAL) {
        drawSevenColorTreeRootsFloor(centerX, centerY - 75f, w, h, animPhase)
    } else if (region.isSportsZone) {
        drawSportsZoneCourts(region, animPhase)
    }

    // 7. Animated Biome-Specific Train & Track
    if (region.hasTrainTrack) {
        drawTrainTrackAndMovingTrain(
            regionId = region.id,
            worldWidth = w,
            trackY = h - 104f,
            animPhase = animPhase
        )
    }

    // 8. Animated Swaying Grass Tufts, Pebbles, & Blossoms
    drawDeterministicGroundGrass(region, animPhase)
}

private fun DrawScope.drawAnimatedWaterBackdrop(
    w: Float,
    h: Float,
    waterColor: Color,
    regionId: RegionId,
    animPhase: Float
) {
    val waveColor = if (regionId == RegionId.MAREA_VIVA) {
        Color(0xFFA6F2FF).copy(alpha = 0.24f)
    } else {
        Color.White.copy(alpha = 0.16f)
    }
    val rippleOffset = sin(animPhase * 1.15f) * 16f
    for (row in 0..9) {
        val y = 36f + row * (h / 9f)
        for (col in 0..8) {
            val x = 44f + col * (w / 8f) + (if (row % 2 == 0) rippleOffset else -rippleOffset)
            drawArc(
                color = waveColor,
                startAngle = 15f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(x - 26f, y - 6f),
                size = Size(52f, 12f),
                style = Stroke(width = 2.6f, cap = StrokeCap.Round)
            )
        }
    }
    // Shoreline foam rings pulsing gently around the island perimeter
    val foamAlpha = 0.28f + sin(animPhase * 1.6f) * 0.08f
    drawRoundRect(
        color = Color(0xFFC8F7FF).copy(alpha = foamAlpha),
        topLeft = Offset(14f, 12f),
        size = Size(w - 28f, h - 24f),
        cornerRadius = CornerRadius(68f, 68f),
        style = Stroke(width = 8f)
    )
    drawCircle(
        brush = Brush.radialGradient(
            listOf(waterColor.copy(alpha = 0.35f), Color.Transparent),
            Offset(w * 0.5f, h * 0.5f),
            w * 0.65f
        ),
        radius = w * 0.65f,
        center = Offset(w * 0.5f, h * 0.5f)
    )
}

private fun DrawScope.drawSkyOrCosmicBackdrop(
    w: Float,
    h: Float,
    regionId: RegionId,
    animPhase: Float
) {
    for (i in 0 until 54) {
        val sx = ((i * 229 + 53) % w.toInt()).toFloat()
        val sy = ((i * 163 + 37) % h.toInt()).toFloat()
        val twinkle = 0.32f + 0.55f * ((sin(animPhase * 2.1f + i * 0.9f) + 1f) * 0.5f)
        val starColor = when (regionId) {
            RegionId.ARTELIA -> Color(0xFFFFF8E1).copy(alpha = twinkle * 0.72f)
            RegionId.ARBOL_ORIGINAL -> SparkGold.copy(alpha = twinkle * 0.85f)
            else -> Color(0xFFE6D9FF).copy(alpha = twinkle * 0.88f)
        }
        drawCircle(starColor, radius = if (i % 4 == 0) 3.8f else 2.2f, center = Offset(sx, sy))
    }
}

private fun DrawScope.drawFloatingIslandCloudsAndFalls(
    region: RegionSpec,
    animPhase: Float
) {
    val w = region.worldWidth
    val h = region.worldHeight
    val accent = region.theme.accentColor
    if (region.hasCloudsBorder) {
        val cloudColor = Color(0xFFF6F2FC).copy(alpha = 0.78f)
        val drift = sin(animPhase * 0.55f) * 10f
        val spots = listOf(
            Offset(64f, 96f), Offset(210f, 32f), Offset(w - 190f, 34f), Offset(w - 54f, 114f),
            Offset(56f, h - 92f), Offset(260f, h - 24f), Offset(w - 260f, h - 24f), Offset(w - 62f, h - 98f)
        )
        spots.forEachIndexed { idx, pos ->
            val cx = pos.x + if (idx % 2 == 0) drift else -drift
            drawCircle(cloudColor, 38f, Offset(cx - 24f, pos.y))
            drawCircle(cloudColor, 46f, Offset(cx, pos.y - 11f))
            drawCircle(cloudColor, 34f, Offset(cx + 26f, pos.y + 3f))
            drawCircle(InkBlack.copy(alpha = 0.25f), 46f, Offset(cx, pos.y - 11f), style = Stroke(2f))
        }
    }
    // Cascading cliff waterfalls around south/side edges
    val fallSpots = if (region.id == RegionId.PLAZA_INFINITA) {
        listOf(w * 0.24f, w * 0.50f, w * 0.76f)
    } else {
        listOf(w * 0.30f, w * 0.70f)
    }
    fallSpots.forEach { fx ->
        val shimmer = (animPhase * 22f) % 24f
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF6FD0E8).copy(alpha = 0.85f), accent.copy(alpha = 0.25f))),
            topLeft = Offset(fx - 18f, h - 42f),
            size = Size(36f, 46f),
            cornerRadius = CornerRadius(12f, 12f)
        )
        drawLine(Color.White.copy(alpha = 0.7f), Offset(fx - 6f, h - 38f + shimmer * 0.25f), Offset(fx - 6f, h - 6f), strokeWidth = 3f)
        drawLine(Color.White.copy(alpha = 0.6f), Offset(fx + 6f, h - 34f + shimmer * 0.2f), Offset(fx + 6f, h - 4f), strokeWidth = 2.6f)
        drawOval(Color.White.copy(alpha = 0.45f), Offset(fx - 26f, h - 10f), Size(52f, 14f))
    }
}

private fun DrawScope.drawTerrainTexturePatches(region: RegionSpec, animPhase: Float) {
    val w = region.worldWidth
    val h = region.worldHeight
    when (region.id) {
        RegionId.PICASIA -> {
            // Colorful inlaid cubist diamond/square tile accents
            val palette = listOf(Color(0xFFFFB703), Color(0xFFE97893), Color(0xFF2EC4B6), Color(0xFF6C63FF))
            for (i in 0 until 22) {
                val px = 110f + ((i * 293 + 57) % (w - 220f).toInt())
                val py = 100f + ((i * 211 + 43) % (h - 200f).toInt())
                val sz = 36f + (i % 3) * 14f
                withTransform({ rotate((i * 17f) % 45f, Offset(px, py)) }) {
                    drawRoundRect(
                        palette[i % palette.size].copy(alpha = 0.14f),
                        Offset(px - sz / 2f, py - sz / 2f),
                        Size(sz, sz),
                        CornerRadius(4f, 4f)
                    )
                    drawRoundRect(
                        InkBlack.copy(alpha = 0.12f),
                        Offset(px - sz / 2f, py - sz / 2f),
                        Size(sz, sz),
                        CornerRadius(4f, 4f),
                        style = Stroke(1.4f)
                    )
                }
            }
        }
        RegionId.TINTOPOLIS -> {
            // Vintage halftone/cobble patches in monochrome grays
            for (i in 0 until 24) {
                val px = 90f + ((i * 257 + 41) % (w - 180f).toInt())
                val py = 90f + ((i * 193 + 61) % (h - 180f).toInt())
                drawOval(
                    color = if (i % 2 == 0) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.10f),
                    topLeft = Offset(px - 44f, py - 24f),
                    size = Size(88f, 48f)
                )
            }
        }
        RegionId.MAREA_VIVA -> {
            // Shallow reef lagoon pools inside the district
            val lagoons = listOf(Offset(w * 0.34f, h * 0.45f), Offset(w * 0.66f, h * 0.45f), Offset(w * 0.50f, h * 0.66f))
            lagoons.forEachIndexed { idx, c ->
                val rw = 160f + idx * 14f
                val rh = 78f
                drawOval(Color(0xFF0F4C64).copy(alpha = 0.55f), Offset(c.x - rw / 2f, c.y - rh / 2f), Size(rw, rh))
                drawOval(Color(0xFF8BE0E6).copy(alpha = 0.25f), Offset(c.x - rw / 2f, c.y - rh / 2f), Size(rw, rh), style = Stroke(3f))
            }
        }
        else -> {
            val patchLight = Color.White.copy(alpha = 0.05f)
            val patchDark = Color.Black.copy(alpha = 0.06f)
            for (i in 0 until 28) {
                val px = 90f + ((i * 263 + 71) % (w - 180f).toInt())
                val py = 90f + ((i * 199 + 41) % (h - 180f).toInt())
                val rW = 76f + (i % 4) * 28f
                val rH = 44f + (i % 3) * 18f
                drawOval(
                    color = if (i % 2 == 0) patchLight else patchDark,
                    topLeft = Offset(px - rW / 2f, py - rH / 2f),
                    size = Size(rW, rH)
                )
            }
        }
    }

    val pulse = 0.07f + sin(animPhase * 0.9f) * 0.02f
    drawCircle(
        brush = Brush.radialGradient(
            listOf(region.theme.accentColor.copy(alpha = pulse), Color.Transparent),
            center = Offset(w / 2f, h / 2f),
            radius = 380f
        ),
        radius = 380f,
        center = Offset(w / 2f, h / 2f)
    )
}

private fun DrawScope.drawPavedAvenue(
    start: Offset,
    end: Offset,
    roadColor: Color,
    regionId: RegionId
) {
    val isBoardwalk = regionId == RegionId.MAREA_VIVA || regionId == RegionId.BOSQUE_OLVIDADO
    val isUrban = regionId == RegionId.RHYTHM_HOOD || regionId == RegionId.ARENA_VIVA
    val roadW = if (isUrban) 68f else 62f

    drawLine(InkBlack.copy(alpha = 0.55f), start, end, strokeWidth = roadW + 10f, cap = StrokeCap.Round)
    drawLine(
        color = if (regionId == RegionId.RHYTHM_HOOD) Color(0xFF332645) else roadColor.copy(alpha = 0.90f),
        start = start,
        end = end,
        strokeWidth = roadW,
        cap = StrokeCap.Round
    )
    drawLine(
        color = when (regionId) {
            RegionId.RHYTHM_HOOD -> Color(0xFFFF3D9A).copy(alpha = 0.45f)
            RegionId.PICASIA -> Color(0xFFFFB703).copy(alpha = 0.35f)
            else -> Color.White.copy(alpha = 0.16f)
        },
        start = start,
        end = end,
        strokeWidth = if (isUrban) 6f else 32f,
        cap = StrokeCap.Round
    )

    val dx = end.x - start.x
    val dy = end.y - start.y
    val dist = hypot(dx, dy).coerceAtLeast(1f)
    val steps = (dist / 30f).toInt().coerceAtLeast(2)
    val nx = -dy / dist
    val ny = dx / dist
    for (i in 1 until steps) {
        val t = i / steps.toFloat()
        val px = start.x + dx * t
        val py = start.y + dy * t
        when {
            isBoardwalk -> {
                drawLine(InkBlack.copy(alpha = 0.34f), Offset(px - nx * 26f, py - ny * 26f), Offset(px + nx * 26f, py + ny * 26f), strokeWidth = 2.8f)
            }
            regionId == RegionId.RHYTHM_HOOD -> {
                // Crisp urban crosswalk bars near avenue ends
                if (i == 2 || i == steps - 2) {
                    for (stripe in -2..2) {
                        val sx = px + nx * stripe * 9f
                        val sy = py + ny * stripe * 9f
                        drawCircle(WarmParchment.copy(alpha = 0.72f), 3.4f, Offset(sx, sy))
                    }
                }
            }
            else -> {
                val side = if (i % 2 == 0) 13f else -13f
                drawCircle(InkBlack.copy(alpha = 0.16f), 7.5f, Offset(px + nx * side, py + ny * side), style = Stroke(1.6f))
            }
        }
    }
}

private fun DrawScope.drawRadialCobblestonePlaza(
    center: Offset,
    radius: Float,
    stoneColor: Color,
    accentColor: Color,
    regionId: RegionId,
    animPhase: Float
) {
    // Outer stone curb
    drawCircle(InkBlack.copy(alpha = 0.62f), radius + 9f, center)
    drawCircle(stoneColor.copy(alpha = 0.94f), radius, center)

    // Biome-specific inlaid plaza pattern
    when (regionId) {
        RegionId.TINTOPOLIS -> {
            // Signature radial B&W checkerboard / sunburst wedges from Tintópolis artwork
            for (i in 0 until 16) {
                val sweep = 360f / 16f
                drawArc(
                    color = if (i % 2 == 0) Color(0xFF424242) else Color(0xFFEAEAEA),
                    startAngle = i * sweep,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = Offset(center.x - radius * 0.84f, center.y - radius * 0.84f),
                    size = Size(radius * 1.68f, radius * 1.68f)
                )
            }
            drawCircle(Color(0xFFDADADA), radius * 0.56f, center)
        }
        RegionId.PICASIA -> {
            val colors = listOf(Color(0xFFFFB703), Color(0xFF2EC4B6), Color(0xFFE97893), Color(0xFF6C63FF))
            for (i in 0 until 12) {
                drawArc(
                    color = colors[i % colors.size].copy(alpha = 0.35f),
                    startAngle = i * 30f,
                    sweepAngle = 28f,
                    useCenter = true,
                    topLeft = Offset(center.x - radius * 0.86f, center.y - radius * 0.86f),
                    size = Size(radius * 1.72f, radius * 1.72f)
                )
            }
            drawCircle(stoneColor, radius * 0.58f, center)
        }
        RegionId.ARTELIA -> {
            // Classic Renaissance cream marble concentric step rings & floral compass
            drawCircle(Color(0xFFE0A94F).copy(alpha = 0.55f), radius * 0.90f, center, style = Stroke(8f))
            drawCircle(Color(0xFF6C63FF).copy(alpha = 0.32f), radius * 0.72f, center, style = Stroke(6f))
            drawCircle(Color(0xFFFDF5E6), radius * 0.56f, center)
        }
        RegionId.SOMBRAVIA -> {
            // Cosmic dark mirror reflection pool
            drawCircle(Color(0xFF120822), radius * 0.86f, center)
            drawCircle(Color(0xFF9D4EDD).copy(alpha = 0.45f), radius * 0.72f, center, style = Stroke(4f))
            drawCircle(Color(0xFFE0AAFF).copy(alpha = 0.25f), radius * 0.50f, center, style = Stroke(2.5f))
        }
        else -> {
            drawCircle(accentColor.copy(alpha = 0.44f), radius * 0.80f, center, style = Stroke(6.5f))
            drawCircle(Color.White.copy(alpha = 0.28f), radius * 0.58f, center, style = Stroke(3.5f))
        }
    }

    // Crisp radial stone spokes
    for (i in 0 until 12) {
        val ang = (i * 30.0) * (PI / 180.0)
        val inner = Offset(center.x + cos(ang).toFloat() * (radius * 0.32f), center.y + sin(ang).toFloat() * (radius * 0.32f))
        val outer = Offset(center.x + cos(ang).toFloat() * radius, center.y + sin(ang).toFloat() * radius)
        drawLine(InkBlack.copy(alpha = 0.24f), inner, outer, strokeWidth = 2.4f)
    }

    val glowAlpha = 0.14f + sin(animPhase * 1.5f) * 0.05f
    drawCircle(accentColor.copy(alpha = glowAlpha), radius * 0.42f, center)
}

private fun DrawScope.drawSevenColorTreeRootsFloor(
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    animPhase: Float
) {
    val rainbow = listOf(
        Color(0xFFFF6A3D), Color(0xFFFFC24D), Color(0xFF4CC37A),
        Color(0xFF2EC4B6), Color(0xFF6C63FF), Color(0xFFE97893), Color(0xFFB7B0E6)
    )
    rainbow.forEachIndexed { idx, col ->
        val ang = -PI * 0.15 + idx * (PI * 1.3 / 6.0)
        val endX = cx + cos(ang).toFloat() * (w * 0.45f)
        val endY = cy + sin(ang).toFloat() * (h * 0.45f)
        val ctrlX = cx + cos(ang + 0.28).toFloat() * (w * 0.22f)
        val ctrlY = cy + sin(ang + 0.28).toFloat() * (h * 0.22f)
        val rootPath = Path().apply {
            moveTo(cx, cy)
            quadraticTo(ctrlX, ctrlY, endX, endY)
        }
        val pulse = 0.55f + 0.2f * sin(animPhase * 2f + idx)
        drawPath(rootPath, col.copy(alpha = pulse), style = Stroke(width = 16f, cap = StrokeCap.Round))
        drawPath(rootPath, Color.White.copy(alpha = 0.4f), style = Stroke(width = 4.2f, cap = StrokeCap.Round))
    }
}

private fun DrawScope.drawSportsZoneCourts(region: RegionSpec, animPhase: Float) {
    val w = region.worldWidth
    val h = region.worldHeight
    if (region.id == RegionId.ARENA_VIVA) {
        // Striped Zona Fútbol pitch
        val sx = w * 0.625f
        val sy = h * 0.49f
        val sw = w * 0.23f
        val sh = h * 0.17f
        drawRoundRect(Color(0xFF337A38), Offset(sx, sy), Size(sw, sh), CornerRadius(14f, 14f))
        for (strip in 0 until 6) {
            if (strip % 2 == 0) {
                drawRect(Color(0xFF479E4C).copy(alpha = 0.55f), Offset(sx + 8f + strip * ((sw - 16f) / 6f), sy + 8f), Size((sw - 16f) / 6f, sh - 16f))
            }
        }
        drawRoundRect(Color.White.copy(alpha = 0.85f), Offset(sx + 10f, sy + 10f), Size(sw - 20f, sh - 20f), CornerRadius(10f, 10f), style = Stroke(3f))
        drawLine(Color.White.copy(alpha = 0.85f), Offset(sx + sw / 2f, sy + 10f), Offset(sx + sw / 2f, sy + sh - 10f), strokeWidth = 3f)
        drawCircle(Color.White.copy(alpha = 0.85f), sh * 0.18f, Offset(sx + sw / 2f, sy + sh / 2f), style = Stroke(3f))
        drawRect(Color.White.copy(alpha = 0.7f), Offset(sx + 10f, sy + sh * 0.28f), Size(sw * 0.12f, sh * 0.44f), style = Stroke(2.4f))
        drawRect(Color.White.copy(alpha = 0.7f), Offset(sx + sw - 10f - sw * 0.12f, sy + sh * 0.28f), Size(sw * 0.12f, sh * 0.44f), style = Stroke(2.4f))

        // Playa Activa golden sandy beach cove & ocean surf waves
        val bx = w * 0.055f
        val by = h * 0.675f
        val bw = w * 0.37f
        val bh = h * 0.20f
        drawRoundRect(Color(0xFFF6D992), Offset(bx, by), Size(bw, bh), CornerRadius(52f, 52f))
        drawRoundRect(InkBlack.copy(alpha = 0.3f), Offset(bx, by), Size(bw, bh), CornerRadius(52f, 52f), style = Stroke(2.8f))
        val tideShift = sin(animPhase * 1.4f) * 6f
        drawArc(Color(0xFF4AD0E8).copy(alpha = 0.65f), 15f, 150f, false, Offset(bx + 24f, by + bh - 46f + tideShift), Size(bw * 0.72f, 34f), style = Stroke(6f, cap = StrokeCap.Round))
        // Beach volleyball net
        drawLine(WarmParchment, Offset(bx + bw * 0.45f, by + bh * 0.42f), Offset(bx + bw * 0.65f, by + bh * 0.42f), strokeWidth = 3.5f)
        drawLine(Color(0xFF5D4037), Offset(bx + bw * 0.45f, by + bh * 0.35f), Offset(bx + bw * 0.45f, by + bh * 0.56f), strokeWidth = 3f)
        drawLine(Color(0xFF5D4037), Offset(bx + bw * 0.65f, by + bh * 0.35f), Offset(bx + bw * 0.65f, by + bh * 0.56f), strokeWidth = 3f)
    } else if (region.id == RegionId.RHYTHM_HOOD) {
        // Canchas Urbanas basketball & street court markings
        val cx = w * 0.49f
        val cy = h * 0.60f
        val cw = w * 0.16f
        val ch = h * 0.13f
        drawRoundRect(Color(0xFF42216B), Offset(cx, cy), Size(cw, ch), CornerRadius(12f, 12f))
        drawRoundRect(Color(0xFFFF3D9A).copy(alpha = 0.8f), Offset(cx + 8f, cy + 8f), Size(cw - 16f, ch - 16f), CornerRadius(8f, 8f), style = Stroke(3f))
        drawLine(Color(0xFFFFC24D).copy(alpha = 0.75f), Offset(cx + cw / 2f, cy + 8f), Offset(cx + cw / 2f, cy + ch - 8f), strokeWidth = 2.4f)
        drawCircle(Color(0xFFFFC24D).copy(alpha = 0.8f), ch * 0.2f, Offset(cx + cw / 2f, cy + ch / 2f), style = Stroke(2.6f))
    }
}

private fun DrawScope.drawTrainTrackAndMovingTrain(
    regionId: RegionId,
    worldWidth: Float,
    trackY: Float,
    animPhase: Float
) {
    // Elevated gravel ballast bed + rails
    drawRect(Color(0xFF282124).copy(alpha = 0.82f), Offset(52f, trackY - 18f), Size(worldWidth - 104f, 36f))
    var tx = 66f
    while (tx < worldWidth - 70f) {
        drawRect(Color(0xFF5C4033), Offset(tx, trackY - 15f), Size(9f, 30f))
        tx += 24f
    }
    drawLine(Color(0xFFC8CED4), Offset(56f, trackY - 9f), Offset(worldWidth - 56f, trackY - 9f), strokeWidth = 3.2f)
    drawLine(Color(0xFFC8CED4), Offset(56f, trackY + 9f), Offset(worldWidth - 56f, trackY + 9f), strokeWidth = 3.2f)

    val travelSpan = worldWidth - 320f
    val progress = ((animPhase * 0.075f) % 1f)
    val trainX = 135f + progress * travelSpan

    val primary = when (regionId) {
        RegionId.TINTOPOLIS -> Color(0xFF3A3A3A)
        RegionId.ARENA_VIVA -> Color(0xFFFFC24D)
        RegionId.SOMBRAVIA -> Color(0xFF3C096C)
        else -> Color(0xFF6C63FF)
    }
    val secondary = when (regionId) {
        RegionId.TINTOPOLIS -> Color(0xFFDFDFDF)
        RegionId.ARENA_VIVA -> Color(0xFF6C63FF)
        RegionId.SOMBRAVIA -> Color(0xFFE0AAFF)
        else -> Color(0xFFFF3D9A)
    }

    // Animated steam puffs (especially classic cartoon puffs on Tintópolis & Sombravía)
    if (regionId == RegionId.TINTOPOLIS || regionId == RegionId.SOMBRAVIA) {
        for (p in 0..3) {
            val puffLift = ((animPhase * 18f + p * 9f) % 34f)
            drawCircle(
                color = (if (regionId == RegionId.SOMBRAVIA) Color(0xFFE0AAFF) else Color.White)
                    .copy(alpha = (0.62f - puffLift / 56f).coerceIn(0f, 0.62f)),
                radius = 7f + puffLift * 0.28f,
                center = Offset(trainX + 106f - puffLift * 0.85f, trackY - 40f - puffLift)
            )
        }
    }

    // Two passenger coaches
    listOf(-142f, -72f).forEach { carOff ->
        drawRoundRect(primary, Offset(trainX + carOff, trackY - 28f), Size(64f, 28f), CornerRadius(6f, 6f))
        drawRoundRect(InkBlack, Offset(trainX + carOff, trackY - 28f), Size(64f, 28f), CornerRadius(6f, 6f), style = Stroke(2.4f))
        drawRect(secondary, Offset(trainX + carOff + 5f, trackY - 11f), Size(54f, 4f))
        for (wIdx in 0..2) {
            drawRoundRect(
                if (regionId == RegionId.TINTOPOLIS) Color(0xFFF5F5F5) else Color(0xFFFFE57F),
                Offset(trainX + carOff + 7f + wIdx * 18f, trackY - 22f),
                Size(13f, 9f),
                CornerRadius(2.5f, 2.5f)
            )
        }
    }

    // Locomotive / Streamliner Nose
    drawRoundRect(primary, Offset(trainX, trackY - 34f), Size(118f, 34f), CornerRadius(10f, 10f))
    drawRoundRect(InkBlack, Offset(trainX, trackY - 34f), Size(118f, 34f), CornerRadius(10f, 10f), style = Stroke(2.6f))
    drawRect(secondary, Offset(trainX + 6f, trackY - 13f), Size(106f, 5.5f))
    // Boiler dome / smokestack + (M) emblem for Tintópolis or cab glass for modern expresses
    if (regionId == RegionId.TINTOPOLIS || regionId == RegionId.SOMBRAVIA) {
        drawRect(Color(0xFF1F1F1F), Offset(trainX + 90f, trackY - 46f), Size(14f, 14f))
        drawRect(InkBlack, Offset(trainX + 90f, trackY - 46f), Size(14f, 14f), style = InkThin)
        drawCircle(secondary, 10f, Offset(trainX + 106f, trackY - 19f))
        drawCircle(InkBlack, 10f, Offset(trainX + 106f, trackY - 19f), style = InkThin)
    } else {
        drawRoundRect(Color(0xFF8BE0E6), Offset(trainX + 86f, trackY - 28f), Size(24f, 13f), CornerRadius(5f, 5f))
    }
    drawCircle(SparkGold, 5.5f, Offset(trainX + 119f, trackY - 15f))

    listOf(-128f, -92f, -56f, -20f, 18f, 56f, 96f).forEach { wx ->
        drawCircle(Color(0xFF1B1B1E), 6.8f, Offset(trainX + wx, trackY + 2f))
        drawCircle(secondary, 2.8f, Offset(trainX + wx, trackY + 2f))
    }
}

fun DrawScope.drawSwayingGrassCluster(
    x: Float,
    y: Float,
    tint: Color,
    phase: Float,
    scale: Float = 1f
) {
    val sway = sin(phase * 1.9f + x * 0.04f) * 3.8f * scale
    withTransform({
        translate(x, y)
        scale(scale, scale, Offset.Zero)
    }) {
        val blades = Path().apply {
            moveTo(-6f, 0f); quadraticTo(-8f + sway * 0.5f, -7f, -9f + sway, -12f)
            moveTo(0f, 0f); quadraticTo(sway * 0.5f, -9f, sway * 1.1f, -15f)
            moveTo(6f, 0f); quadraticTo(8f + sway * 0.5f, -6f, 9f + sway, -11f)
        }
        drawPath(blades, tint, style = Stroke(width = 2.6f, cap = StrokeCap.Round))
    }
}

private fun DrawScope.drawDeterministicGroundGrass(
    region: RegionSpec,
    animPhase: Float
) {
    val w = region.worldWidth
    val h = region.worldHeight
    val grassTint = when (region.id) {
        RegionId.TINTOPOLIS -> Color(0xFF9E9E9E)
        RegionId.SOMBRAVIA -> Color(0xFF7B2CBF)
        RegionId.PICASIA -> Color(0xFF6C63FF)
        RegionId.CONSTELARIA -> Color(0xFF8BB3FF)
        RegionId.MAREA_VIVA -> Color(0xFF2EC4B6)
        RegionId.ARBOL_ORIGINAL -> Color(0xFFFFC24D)
        else -> Color(0xFF4CC37A)
    }
    val cx = w / 2f
    val cy = h / 2f

    for (i in 0 until 135) {
        val gx = 72f + ((i * 223 + region.id.ordinal * 47) % (w - 144f).toInt())
        val gy = 72f + ((i * 271 + region.id.ordinal * 83) % (h - 164f).toInt())
        if (hypot((gx - cx).toDouble(), (gy - cy).toDouble()) < region.cobblestonePlazaRadius * 0.88) continue

        if (i % 5 == 0) {
            drawOval(Color.Black.copy(alpha = 0.18f), Offset(gx - 5.5f, gy - 3f), Size(11f, 6.5f))
            drawOval(region.theme.pathColor.copy(alpha = 0.5f), Offset(gx - 4.5f, gy - 2.5f), Size(9f, 5f))
        } else {
            val scale = 0.8f + (i % 3) * 0.2f
            drawSwayingGrassCluster(gx, gy, grassTint, animPhase + i * 0.4f, scale)
            if (i % 6 == 0) {
                val sway = sin(animPhase * 1.9f + gx * 0.04f + i * 0.4f) * 3.8f * scale
                drawCircle(region.theme.accentColor, 3f, Offset(gx + sway, gy - 14f * scale))
            }
        }
    }
}
