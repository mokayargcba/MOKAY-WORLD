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

    // 1. Deep void / water / sky backdrop with subtle animated water waves or stars
    drawRect(theme.waterOrVoid)

    if (region.hasWaterMoat || region.isSportsZone) {
        drawAnimatedWaterBackdrop(w, h, theme.waterOrVoid, animPhase)
    } else if (region.hasCloudsBorder || region.id == RegionId.CONSTELARIA || region.id == RegionId.ARBOL_ORIGINAL) {
        drawSkyOrCosmicBackdrop(w, h, region.id, animPhase)
    } else {
        // Subtle moody atmosphere waves outside borders
        for (i in 0 until 14) {
            val rx = ((i * 211) % w.toInt()).toFloat()
            val ry = ((i * 157) % h.toInt()).toFloat()
            drawCircle(
                color = theme.accentColor.copy(alpha = 0.06f),
                radius = 120f + (i % 3) * 40f,
                center = Offset(rx, ry)
            )
        }
    }

    // 2. Island / District cliff drop shadow & stone terrace rim
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.36f),
        topLeft = Offset(28f, 44f),
        size = Size(w - 56f, h - 50f),
        cornerRadius = CornerRadius(58f, 58f)
    )
    drawRoundRect(
        color = Color(0xFF3E352B),
        topLeft = Offset(28f, 34f),
        size = Size(w - 56f, h - 54f),
        cornerRadius = CornerRadius(56f, 56f)
    )

    // 3. Main District Ground Turf / Stone Surface
    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(theme.groundSecondary, theme.groundPrimary),
            center = Offset(w / 2f, h / 2f),
            radius = w * 0.68f
        ),
        topLeft = Offset(30f, 26f),
        size = Size(w - 60f, h - 62f),
        cornerRadius = CornerRadius(54f, 54f)
    )

    // Subtle organic meadow / stone tile patches so terrain never looks flat
    drawTerrainTexturePatches(region, animPhase)

    // Rim border trim
    drawRoundRect(
        color = theme.accentColor.copy(alpha = 0.65f),
        topLeft = Offset(30f, 26f),
        size = Size(w - 60f, h - 62f),
        cornerRadius = CornerRadius(54f, 54f),
        style = Stroke(width = 5.5f)
    )

    // Fluffy cloud border & glowing waterfalls for floating districts (Artelia, Constelaria, Árbol Original)
    if (region.hasCloudsBorder) {
        drawFloatingIslandCloudsAndFalls(w, h, theme.accentColor, animPhase)
    }

    // 4. Paved Stone / Boardwalk Avenues connecting the Center Plaza to every Gate
    val centerX = w / 2f
    val centerY = h / 2f
    region.gates.forEach { gate ->
        drawPavedAvenue(
            start = Offset(centerX, centerY),
            end = Offset(gate.x, gate.y),
            roadColor = theme.pathColor,
            isBoardwalk = region.id == RegionId.MAREA_VIVA || region.id == RegionId.BOSQUE_OLVIDADO,
            isNeon = region.id == RegionId.RHYTHM_HOOD || region.id == RegionId.PICASIA
        )
    }

    // 5. Radial Cobblestone Central Plaza (signature motif across Plaza Infinita, Tintópolis, Artelia, Picasia, Marea Viva)
    drawRadialCobblestonePlaza(
        center = Offset(centerX, centerY - 12f),
        radius = region.cobblestonePlazaRadius,
        stoneColor = theme.pathColor,
        accentColor = theme.accentColor,
        isBAndW = region.id == RegionId.TINTOPOLIS,
        animPhase = animPhase
    )

    // 6. Special Biome Ground Markings (7-color roots in Árbol Original, Sports pitches in Arena Viva / Rhythm Hood)
    if (region.id == RegionId.ARBOL_ORIGINAL) {
        drawSevenColorTreeRootsFloor(centerX, centerY - 60f, w, h, animPhase)
    } else if (region.isSportsZone) {
        drawSportsZoneCourts(region)
    }

    // 7. Animated Mokay Express / Vintage Steam Train on districts with train tracks
    if (region.hasTrainTrack) {
        drawTrainTrackAndMovingTrain(
            regionId = region.id,
            worldWidth = w,
            trackY = h - 98f,
            animPhase = animPhase
        )
    }

    // 8. Animated Swaying Grass Tufts, Pebbles, Clover across the ground
    drawDeterministicGroundGrass(region, animPhase)
}

private fun DrawScope.drawAnimatedWaterBackdrop(
    w: Float,
    h: Float,
    waterColor: Color,
    animPhase: Float
) {
    val waveColor = Color.White.copy(alpha = 0.14f)
    val rippleOffset = sin(animPhase * 1.1f) * 14f
    for (row in 0..8) {
        val y = 40f + row * (h / 8f)
        for (col in 0..7) {
            val x = 50f + col * (w / 7f) + (if (row % 2 == 0) rippleOffset else -rippleOffset)
            drawArc(
                color = waveColor,
                startAngle = 18f,
                sweepAngle = 144f,
                useCenter = false,
                topLeft = Offset(x - 24f, y - 6f),
                size = Size(48f, 12f),
                style = Stroke(width = 2.4f, cap = StrokeCap.Round)
            )
        }
    }
    // Shoreline foam halo around island edge
    drawRoundRect(
        color = Color(0xFFBDF2F7).copy(alpha = 0.24f + sin(animPhase * 1.6f) * 0.08f),
        topLeft = Offset(16f, 14f),
        size = Size(w - 32f, h - 28f),
        cornerRadius = CornerRadius(66f, 66f),
        style = Stroke(width = 7f)
    )
    // Soft glint near bottom water
    drawCircle(
        brush = Brush.radialGradient(listOf(waterColor.copy(alpha = 0.4f), Color.Transparent), Offset(w * 0.5f, h * 0.5f), w * 0.6f),
        radius = w * 0.6f,
        center = Offset(w * 0.5f, h * 0.5f)
    )
}

private fun DrawScope.drawSkyOrCosmicBackdrop(
    w: Float,
    h: Float,
    regionId: RegionId,
    animPhase: Float
) {
    for (i in 0 until 42) {
        val sx = ((i * 197 + 43) % w.toInt()).toFloat()
        val sy = ((i * 139 + 29) % h.toInt()).toFloat()
        val twinkle = 0.35f + 0.5f * ((sin(animPhase * 2f + i * 0.9f) + 1f) * 0.5f)
        val starColor = when (regionId) {
            RegionId.ARTELIA -> Color.White.copy(alpha = twinkle * 0.6f)
            RegionId.ARBOL_ORIGINAL -> SparkGold.copy(alpha = twinkle * 0.8f)
            else -> Color(0xFFE6D9FF).copy(alpha = twinkle * 0.85f)
        }
        drawCircle(starColor, radius = if (i % 4 == 0) 3.6f else 2f, center = Offset(sx, sy))
    }
}

private fun DrawScope.drawFloatingIslandCloudsAndFalls(
    w: Float,
    h: Float,
    accent: Color,
    animPhase: Float
) {
    val cloudColor = Color(0xFFF4F1FA).copy(alpha = 0.72f)
    val drift = sin(animPhase * 0.6f) * 8f
    val spots = listOf(
        Offset(56f, 90f), Offset(180f, 34f), Offset(w - 160f, 38f), Offset(w - 50f, 110f),
        Offset(48f, h - 90f), Offset(220f, h - 28f), Offset(w - 220f, h - 26f), Offset(w - 56f, h - 100f)
    )
    spots.forEachIndexed { idx, pos ->
        val cx = pos.x + if (idx % 2 == 0) drift else -drift
        drawCircle(cloudColor, 34f, Offset(cx - 22f, pos.y))
        drawCircle(cloudColor, 42f, Offset(cx, pos.y - 10f))
        drawCircle(cloudColor, 32f, Offset(cx + 24f, pos.y + 2f))
    }
    // Glowing waterfall ribbons cascading down southern cliff
    val fallX = listOf(w * 0.28f, w * 0.72f)
    fallX.forEach { fx ->
        val shimmer = (animPhase * 18f) % 24f
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF6FD0E8).copy(alpha = 0.75f), accent.copy(alpha = 0.25f))),
            topLeft = Offset(fx - 14f, h - 38f),
            size = Size(28f, 44f),
            cornerRadius = CornerRadius(10f, 10f)
        )
        drawLine(Color.White.copy(alpha = 0.65f), Offset(fx - 4f, h - 34f + shimmer * 0.3f), Offset(fx - 4f, h - 6f), strokeWidth = 3f)
        drawLine(Color.White.copy(alpha = 0.55f), Offset(fx + 5f, h - 30f + shimmer * 0.2f), Offset(fx + 5f, h - 4f), strokeWidth = 2.5f)
    }
}

private fun DrawScope.drawTerrainTexturePatches(region: RegionSpec, animPhase: Float) {
    val w = region.worldWidth
    val h = region.worldHeight
    val patchLight = Color.White.copy(alpha = 0.045f)
    val patchDark = Color.Black.copy(alpha = 0.055f)
    for (i in 0 until 26) {
        val px = 90f + ((i * 263 + 71) % (w - 180f).toInt())
        val py = 90f + ((i * 199 + 41) % (h - 180f).toInt())
        val rW = 70f + (i % 4) * 28f
        val rH = 42f + (i % 3) * 18f
        drawOval(
            color = if (i % 2 == 0) patchLight else patchDark,
            topLeft = Offset(px - rW / 2f, py - rH / 2f),
            size = Size(rW, rH)
        )
    }
    // Subtle sun/moon dappled light beam across central plaza
    val pulse = 0.06f + sin(animPhase * 0.9f) * 0.02f
    drawCircle(
        brush = Brush.radialGradient(
            listOf(region.theme.accentColor.copy(alpha = pulse), Color.Transparent),
            center = Offset(w / 2f, h / 2f),
            radius = 340f
        ),
        radius = 340f,
        center = Offset(w / 2f, h / 2f)
    )
}

private fun DrawScope.drawPavedAvenue(
    start: Offset,
    end: Offset,
    roadColor: Color,
    isBoardwalk: Boolean,
    isNeon: Boolean
) {
    // Road dark curb outline
    drawLine(
        color = InkBlack.copy(alpha = 0.48f),
        start = start,
        end = end,
        strokeWidth = 64f,
        cap = StrokeCap.Round
    )
    // Main warm paved surface
    drawLine(
        color = roadColor.copy(alpha = 0.86f),
        start = start,
        end = end,
        strokeWidth = 54f,
        cap = StrokeCap.Round
    )
    // Center highlight / neon trim
    drawLine(
        color = if (isNeon) Color(0xFFFF3D9A).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.16f),
        start = start,
        end = end,
        strokeWidth = if (isNeon) 7f else 32f,
        cap = StrokeCap.Round
    )
    // Cobblestone slabs or wooden boardwalk planks along the path
    val dx = end.x - start.x
    val dy = end.y - start.y
    val dist = hypot(dx, dy).coerceAtLeast(1f)
    val steps = (dist / 32f).toInt().coerceAtLeast(2)
    val nx = -dy / dist
    val ny = dx / dist
    for (i in 1 until steps) {
        val t = i / steps.toFloat()
        val px = start.x + dx * t
        val py = start.y + dy * t
        if (isBoardwalk) {
            drawLine(
                color = InkBlack.copy(alpha = 0.30f),
                start = Offset(px - nx * 24f, py - ny * 24f),
                end = Offset(px + nx * 24f, py + ny * 24f),
                strokeWidth = 2.6f
            )
        } else {
            val side = if (i % 2 == 0) 11f else -11f
            drawCircle(
                color = InkBlack.copy(alpha = 0.14f),
                radius = 7f,
                center = Offset(px + nx * side, py + ny * side),
                style = Stroke(1.6f)
            )
        }
    }
}

private fun DrawScope.drawRadialCobblestonePlaza(
    center: Offset,
    radius: Float,
    stoneColor: Color,
    accentColor: Color,
    isBAndW: Boolean,
    animPhase: Float
) {
    // Outer stone curb ring
    drawCircle(InkBlack.copy(alpha = 0.55f), radius + 7f, center)
    drawCircle(stoneColor.copy(alpha = 0.92f), radius, center)

    // Decorative concentric mosaic rings
    val ring1 = if (isBAndW) Color(0xFF4F4F4F) else accentColor.copy(alpha = 0.42f)
    val ring2 = if (isBAndW) Color(0xFFB8B8B8) else Color.White.copy(alpha = 0.25f)
    drawCircle(ring1, radius * 0.78f, center, style = Stroke(width = 6f))
    drawCircle(ring2, radius * 0.56f, center, style = Stroke(width = 3.5f))

    // Radial stone spokes like the Tintópolis / Artelia / Plaza Infinita artwork
    for (i in 0 until 12) {
        val ang = (i * 30.0) * (PI / 180.0)
        val inner = Offset(center.x + cos(ang).toFloat() * (radius * 0.34f), center.y + sin(ang).toFloat() * (radius * 0.34f))
        val outer = Offset(center.x + cos(ang).toFloat() * radius, center.y + sin(ang).toFloat() * radius)
        drawLine(InkBlack.copy(alpha = 0.22f), inner, outer, strokeWidth = 2.2f)
    }

    // Subtle shimmer ring
    val glowAlpha = 0.14f + sin(animPhase * 1.5f) * 0.05f
    drawCircle(accentColor.copy(alpha = glowAlpha), radius * 0.44f, center)
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
        val endX = cx + cos(ang).toFloat() * (w * 0.44f)
        val endY = cy + sin(ang).toFloat() * (h * 0.44f)
        val ctrlX = cx + cos(ang + 0.28).toFloat() * (w * 0.22f)
        val ctrlY = cy + sin(ang + 0.28).toFloat() * (h * 0.22f)
        val rootPath = Path().apply {
            moveTo(cx, cy)
            quadraticTo(ctrlX, ctrlY, endX, endY)
        }
        val pulse = 0.48f + 0.18f * sin(animPhase * 2f + idx)
        drawPath(rootPath, col.copy(alpha = pulse), style = Stroke(width = 12f, cap = StrokeCap.Round))
        drawPath(rootPath, Color.White.copy(alpha = 0.32f), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
    }
}

private fun DrawScope.drawSportsZoneCourts(region: RegionSpec) {
    val w = region.worldWidth
    val h = region.worldHeight
    if (region.id == RegionId.ARENA_VIVA) {
        // Green soccer pitch lines south-east of stadium (proportional to Mokaya world dimensions)
        val sx = w * 0.625f
        val sy = h * 0.50f
        val sw = w * 0.22f
        val sh = h * 0.16f
        drawRoundRect(Color(0xFF3E8E41), Offset(sx, sy), Size(sw, sh), CornerRadius(14f, 14f))
        drawRoundRect(Color.White.copy(alpha = 0.8f), Offset(sx + 10f, sy + 10f), Size(sw - 20f, sh - 20f), CornerRadius(10f, 10f), style = Stroke(2.8f))
        drawLine(Color.White.copy(alpha = 0.8f), Offset(sx + sw / 2f, sy + 10f), Offset(sx + sw / 2f, sy + sh - 10f), strokeWidth = 2.8f)
        drawCircle(Color.White.copy(alpha = 0.8f), sh * 0.17f, Offset(sx + sw / 2f, sy + sh / 2f), style = Stroke(2.8f))
        // Golden sandy beach strip at bottom-left
        val bx = w * 0.06f
        val by = h * 0.69f
        val bw = w * 0.36f
        val bh = h * 0.18f
        drawRoundRect(Color(0xFFF3D58D), Offset(bx, by), Size(bw, bh), CornerRadius(44f, 44f))
        drawRoundRect(InkBlack.copy(alpha = 0.25f), Offset(bx, by), Size(bw, bh), CornerRadius(44f, 44f), style = Stroke(2.8f))
    } else if (region.id == RegionId.RHYTHM_HOOD) {
        // Streetball court markings
        val cx = w * 0.495f
        val cy = h * 0.61f
        val cw = w * 0.145f
        val ch = h * 0.115f
        drawRoundRect(Color(0xFF4A2574), Offset(cx, cy), Size(cw, ch), CornerRadius(12f, 12f))
        drawRoundRect(Color(0xFFFF3D9A).copy(alpha = 0.7f), Offset(cx + 8f, cy + 8f), Size(cw - 16f, ch - 16f), CornerRadius(8f, 8f), style = Stroke(2.8f))
        drawCircle(Color(0xFFFFC24D).copy(alpha = 0.75f), ch * 0.18f, Offset(cx + cw / 2f, cy + ch / 2f), style = Stroke(2.4f))
    }
}

private fun DrawScope.drawTrainTrackAndMovingTrain(
    regionId: RegionId,
    worldWidth: Float,
    trackY: Float,
    animPhase: Float
) {
    // Ballast bed
    drawRect(Color(0xFF2F2826).copy(alpha = 0.75f), Offset(52f, trackY - 16f), Size(worldWidth - 104f, 32f))
    // Wooden railway ties
    var tx = 66f
    while (tx < worldWidth - 70f) {
        drawRect(Color(0xFF5C4033), Offset(tx, trackY - 14f), Size(8f, 28f))
        tx += 24f
    }
    // Dual steel rails
    drawLine(Color(0xFFC8CED4), Offset(56f, trackY - 8f), Offset(worldWidth - 56f, trackY - 8f), strokeWidth = 3f)
    drawLine(Color(0xFFC8CED4), Offset(56f, trackY + 8f), Offset(worldWidth - 56f, trackY + 8f), strokeWidth = 3f)

    // Animated Mokay Train gliding smoothly along the track
    val travelSpan = worldWidth - 260f
    val progress = ((animPhase * 0.08f) % 1f)
    val trainX = 110f + progress * travelSpan
    val isBAndW = regionId == RegionId.TINTOPOLIS
    val primary = when (regionId) {
        RegionId.TINTOPOLIS -> Color(0xFF404040)
        RegionId.ARENA_VIVA -> Color(0xFFFFC24D)
        else -> Color(0xFF6C63FF)
    }
    val trim = when (regionId) {
        RegionId.TINTOPOLIS -> Color(0xFFE0E0E0)
        RegionId.ARENA_VIVA -> Color(0xFF2EC4B6)
        else -> Color(0xFFFF3D9A)
    }

    // Puffing animated cartoon steam clouds (for vintage steam loco or express)
    for (p in 0..2) {
        val puffLift = ((animPhase * 16f + p * 11f) % 30f)
        drawCircle(
            color = Color.White.copy(alpha = (0.55f - puffLift / 55f).coerceIn(0f, 0.55f)),
            radius = 7f + puffLift * 0.25f,
            center = Offset(trainX + 104f - puffLift * 0.7f, trackY - 34f - puffLift)
        )
    }

    // Passenger car + Locomotive
    drawRoundRect(primary, Offset(trainX - 68f, trackY - 26f), Size(64f, 26f), CornerRadius(6f, 6f))
    drawRoundRect(InkBlack, Offset(trainX - 68f, trackY - 26f), Size(64f, 26f), CornerRadius(6f, 6f), style = Stroke(2.2f))
    drawRect(trim, Offset(trainX - 62f, trackY - 20f), Size(14f, 9f))
    drawRect(trim, Offset(trainX - 42f, trackY - 20f), Size(14f, 9f))
    drawRect(trim, Offset(trainX - 22f, trackY - 20f), Size(12f, 9f))

    // Engine car
    drawRoundRect(primary, Offset(trainX + 2f, trackY - 30f), Size(112f, 30f), CornerRadius(8f, 8f))
    drawRoundRect(InkBlack, Offset(trainX + 2f, trackY - 30f), Size(112f, 30f), CornerRadius(8f, 8f), style = Stroke(2.4f))
    drawRect(trim, Offset(trainX + 8f, trackY - 12f), Size(100f, 5f))
    // Chimney & golden headlamp
    drawRect(if (isBAndW) Color(0xFF262626) else Color(0xFF2D1E2F), Offset(trainX + 94f, trackY - 40f), Size(12f, 12f))
    drawCircle(SparkGold, 5.5f, Offset(trainX + 114f, trackY - 14f))
    // Wheels
    listOf(-54f, -20f, 18f, 54f, 92f).forEach { wx ->
        drawCircle(Color(0xFF1F1F1F), 6.5f, Offset(trainX + wx, trackY + 2f))
        drawCircle(trim, 2.5f, Offset(trainX + wx, trackY + 2f))
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

    // 105 deterministic animated grass tufts & tiny clover flowers across lawns and park margins
    for (i in 0 until 105) {
        val gx = 74f + ((i * 223 + region.id.ordinal * 47) % (w - 148f).toInt())
        val gy = 74f + ((i * 271 + region.id.ordinal * 83) % (h - 168f).toInt())
        // Skip central stone plaza
        if (hypot((gx - cx).toDouble(), (gy - cy).toDouble()) < region.cobblestonePlazaRadius * 0.85) continue

        if (i % 5 == 0) {
            // Tiny cobblestone pebbles
            drawOval(Color.Black.copy(alpha = 0.18f), Offset(gx - 5f, gy - 3f), Size(10f, 6f))
            drawOval(region.theme.pathColor.copy(alpha = 0.45f), Offset(gx - 4f, gy - 2.5f), Size(8f, 5f))
        } else {
            val scale = 0.78f + (i % 3) * 0.18f
            drawSwayingGrassCluster(gx, gy, grassTint, animPhase + i * 0.4f, scale)
            if (i % 7 == 0) {
                // Tiny blossom dot swaying at grass tip
                val sway = sin(animPhase * 1.9f + gx * 0.04f + i * 0.4f) * 3.8f * scale
                drawCircle(region.theme.accentColor, 2.8f, Offset(gx + sway, gy - 14f * scale))
            }
        }
    }
}
