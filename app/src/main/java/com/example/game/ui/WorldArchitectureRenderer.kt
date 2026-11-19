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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.game.model.HouseSpec
import com.example.game.model.HouseStyle
import com.example.game.model.LandmarkKind
import com.example.game.model.LandmarkSpec
import com.example.game.model.PropKind
import com.example.game.model.PropSpec
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.InkBlack
import com.example.ui.theme.RunicTeal
import com.example.ui.theme.SparkGold
import com.example.ui.theme.WarmParchment
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawHouse2D(
    house: HouseSpec,
    animPhase: Float,
    textMeasurer: TextMeasurer? = null
) {
    val left = house.x - house.width / 2f
    val top = house.y - house.height / 2f
    val w = house.width
    val h = house.height

    // 1. Ground contact shadow
    drawOval(
        color = Color.Black.copy(alpha = 0.28f),
        topLeft = Offset(left - 8f, top + h - 10f),
        size = Size(w + 16f, 22f)
    )

    // 2. Chimney with gentle animated cartoon smoke puff
    if (house.style != HouseStyle.CORAL_HUT && house.style != HouseStyle.CRYSTAL_PAVILION) {
        val chX = left + w * 0.74f
        val chY = top - h * 0.18f
        drawRect(Color(0xFF6B442A), Offset(chX, chY), Size(16f, 24f))
        drawRect(InkBlack, Offset(chX, chY), Size(16f, 24f), style = InkThin)
        val drift = (animPhase * 12f + house.x * 0.1f) % 22f
        drawCircle(
            Color.White.copy(alpha = (0.45f - drift / 50f).coerceIn(0f, 0.45f)),
            radius = 5f + drift * 0.18f,
            center = Offset(chX + 8f + sin(animPhase + drift * 0.2f) * 3f, chY - 6f - drift)
        )
    }

    // 3. Main wall body
    val wallTop = top + h * 0.34f
    val wallH = h * 0.66f
    drawRoundRect(
        color = house.wallColor,
        topLeft = Offset(left + 4f, wallTop),
        size = Size(w - 8f, wallH),
        cornerRadius = CornerRadius(8f, 8f)
    )
    // Lower stone plinth foundation
    drawRect(
        color = Color(0xFF423A36).copy(alpha = 0.32f),
        topLeft = Offset(left + 4f, top + h - 12f),
        size = Size(w - 8f, 12f)
    )
    drawRoundRect(
        color = InkBlack,
        topLeft = Offset(left + 4f, wallTop),
        size = Size(w - 8f, wallH),
        cornerRadius = CornerRadius(8f, 8f),
        style = InkOutline
    )

    // 4. Doors & Glowing Windows
    val doorW = (w * 0.22f).coerceIn(18f, 28f)
    val doorH = (wallH * 0.48f).coerceIn(24f, 34f)
    val doorX = house.x - doorW / 2f
    val doorY = top + h - doorH
    drawRoundRect(
        color = Color(0xFF5D3A24),
        topLeft = Offset(doorX, doorY),
        size = Size(doorW, doorH),
        cornerRadius = CornerRadius(doorW / 2f, doorW / 2f)
    )
    drawRoundRect(
        color = InkBlack,
        topLeft = Offset(doorX, doorY),
        size = Size(doorW, doorH),
        cornerRadius = CornerRadius(doorW / 2f, doorW / 2f),
        style = InkThin
    )
    drawCircle(SparkGold, 2.2f, Offset(doorX + doorW - 5f, doorY + doorH * 0.56f))

    // Left & right glowing windows
    val winGlow = 0.82f + sin(animPhase * 1.4f + house.x) * 0.12f
    val winPositions = listOf(left + w * 0.20f, left + w * 0.80f)
    winPositions.forEach { wx ->
        val wy = wallTop + wallH * 0.28f
        drawRoundRect(
            color = SparkGold.copy(alpha = winGlow),
            topLeft = Offset(wx - 9f, wy - 9f),
            size = Size(18f, 18f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = InkBlack,
            topLeft = Offset(wx - 9f, wy - 9f),
            size = Size(18f, 18f),
            cornerRadius = CornerRadius(4f, 4f),
            style = InkThin
        )
        drawLine(InkBlack, Offset(wx, wy - 9f), Offset(wx, wy + 9f), strokeWidth = 1.4f)
        drawLine(InkBlack, Offset(wx - 9f, wy), Offset(wx + 9f, wy), strokeWidth = 1.4f)
    }

    // 5. Stylized Roof per HouseStyle
    when (house.style) {
        HouseStyle.RENAISSANCE_VILLA, HouseStyle.CLOUD_TEMPLE, HouseStyle.CRYSTAL_PAVILION -> {
            // Grand dome + pediment
            drawOval(
                color = house.roofColor,
                topLeft = Offset(left + w * 0.16f, top - 8f),
                size = Size(w * 0.68f, h * 0.50f)
            )
            drawOval(
                color = InkBlack,
                topLeft = Offset(left + w * 0.16f, top - 8f),
                size = Size(w * 0.68f, h * 0.50f),
                style = InkOutline
            )
            drawRoundRect(
                color = house.wallColor,
                topLeft = Offset(left - 2f, wallTop - 8f),
                size = Size(w + 4f, 12f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawRoundRect(InkBlack, Offset(left - 2f, wallTop - 8f), Size(w + 4f, 12f), CornerRadius(4f, 4f), style = InkThin)
            drawCircle(SparkGold, 4.5f, Offset(house.x, top - 10f))
        }
        HouseStyle.CORAL_HUT -> {
            // Scallop seashell / dome coral roof
            val dome = Path().apply {
                moveTo(left - 6f, wallTop + 4f)
                cubicTo(left + w * 0.15f, top - 10f, left + w * 0.85f, top - 10f, left + w + 6f, wallTop + 4f)
                close()
            }
            drawPath(dome, house.roofColor)
            drawPath(dome, InkBlack, style = InkOutline)
            drawLine(Color.White.copy(alpha = 0.35f), Offset(house.x, top - 2f), Offset(house.x - w * 0.18f, wallTop + 2f), strokeWidth = 2f)
            drawLine(Color.White.copy(alpha = 0.35f), Offset(house.x, top - 2f), Offset(house.x + w * 0.18f, wallTop + 2f), strokeWidth = 2f)
        }
        HouseStyle.CUBIST_STUDIO, HouseStyle.NEON_LOFT -> {
            // Geometric layered flat/angled cubist or neon marquee roof
            drawRoundRect(
                color = house.roofColor,
                topLeft = Offset(left - 4f, top + 4f),
                size = Size(w + 8f, h * 0.34f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawRoundRect(InkBlack, Offset(left - 4f, top + 4f), Size(w + 8f, h * 0.34f), CornerRadius(6f, 6f), style = InkOutline)
            drawRect(SparkGold.copy(alpha = 0.85f), Offset(left + 10f, top + 12f), Size(w - 20f, 5f))
        }
        else -> {
            // Classic Cuphead/RPG pitched gable roof
            val roof = Path().apply {
                moveTo(left - 8f, wallTop + 6f)
                lineTo(house.x, top - 6f)
                lineTo(left + w + 8f, wallTop + 6f)
                close()
            }
            drawPath(roof, house.roofColor)
            drawPath(roof, InkBlack, style = InkOutline)
            // Roof ridge highlight
            drawLine(
                Color.White.copy(alpha = 0.28f),
                Offset(left + 8f, wallTop + 1f),
                Offset(house.x, top - 2f),
                strokeWidth = 2.4f
            )
        }
    }

    // Striped Café awning across the front when CAFE_AWNING
    if (house.style == HouseStyle.CAFE_AWNING) {
        val awTop = wallTop + wallH * 0.44f
        for (i in 0 until 6) {
            val segW = (w - 8f) / 6f
            drawRect(
                color = if (i % 2 == 0) house.roofColor else WarmParchment,
                topLeft = Offset(left + 4f + i * segW, awTop),
                size = Size(segW + 0.5f, 11f)
            )
        }
        drawRect(InkBlack, Offset(left + 4f, awTop), Size(w - 8f, 11f), style = InkThin)
    }

    // Small engraved plaque only if textMeasurer passed and house is named
    if (textMeasurer != null && house.name.isNotBlank()) {
        val m = textMeasurer.measure(
            house.name,
            TextStyle(color = WarmParchment, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
        )
        drawRoundRect(
            color = InkBlack.copy(alpha = 0.62f),
            topLeft = Offset(house.x - m.size.width / 2f - 5f, top + h + 3f),
            size = Size(m.size.width + 10f, m.size.height + 3f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        drawText(m, topLeft = Offset(house.x - m.size.width / 2f, top + h + 4f))
    }
}

fun DrawScope.drawLandmark2D(
    lm: LandmarkSpec,
    animPhase: Float,
    textMeasurer: TextMeasurer
) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    val w = lm.width
    val h = lm.height

    // Drop shadow
    drawOval(Color.Black.copy(alpha = 0.3f), Offset(left - 6f, top + h - 14f), Size(w + 12f, 24f))

    when (lm.kind) {
        LandmarkKind.FOUNTAIN -> drawFountainMonument(lm, animPhase)
        LandmarkKind.CINEMA, LandmarkKind.THEATER -> drawMarqueeBuilding(lm, animPhase)
        LandmarkKind.CATHEDRAL_LIBRARY, LandmarkKind.DREAM_PALACE -> drawGrandDomePalace(lm, animPhase)
        LandmarkKind.OBSERVATORY, LandmarkKind.LIGHTHOUSE, LandmarkKind.BEACON_TOWER -> drawTowerMonument(lm, animPhase)
        LandmarkKind.STADIUM -> drawStadiumArena(lm)
        LandmarkKind.SKATE_PARK, LandmarkKind.BASKET_COURT -> drawStreetCourtLandmark(lm)
        LandmarkKind.ANCESTRAL_PORTAL -> drawAncestralPortal(lm, animPhase)
        LandmarkKind.CRYSTAL_HEART, LandmarkKind.CREATOR_CROWN -> drawCrystalTreeHeart(lm, animPhase)
        LandmarkKind.GROTTO, LandmarkKind.GLOWING_LAKE -> drawGlowingGrottoOrLake(lm, animPhase)
        LandmarkKind.GRAFFITI_WALL -> drawGraffitiWall(lm)
        LandmarkKind.TRAIN_YARD -> drawTrainYardDepot(lm)
        else -> {
            // Illustrated multi-roof civic building / shop / museum / studio
            drawHouse2D(
                house = HouseSpec(
                    id = lm.id,
                    name = "",
                    x = lm.x,
                    y = lm.y,
                    width = w,
                    height = h,
                    roofColor = lm.accentColor,
                    wallColor = Color(0xFFEBDCCB),
                    style = when (lm.kind) {
                        LandmarkKind.CAFE -> HouseStyle.CAFE_AWNING
                        LandmarkKind.GALLERY -> HouseStyle.RENAISSANCE_VILLA
                        LandmarkKind.MUSEUM, LandmarkKind.ILLUSION_HALL -> HouseStyle.CUBIST_STUDIO
                        LandmarkKind.GOTHIC_MANOR -> HouseStyle.GOTHIC_HOUSE
                        LandmarkKind.STAGE_SHELL -> HouseStyle.CORAL_HUT
                        else -> HouseStyle.TOWNHOUSE
                    }
                ),
                animPhase = animPhase
            )
        }
    }

    // Crisp illustrated wooden/stone nameplate plaque
    val measured = textMeasurer.measure(
        lm.name,
        TextStyle(color = SparkGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    )
    val badgeW = measured.size.width + 14f
    val badgeH = measured.size.height + 6f
    val bx = lm.x - badgeW / 2f
    val by = top + h + 4f
    drawRoundRect(Color(0xFF1A1426).copy(alpha = 0.88f), Offset(bx, by), Size(badgeW, badgeH), CornerRadius(8f, 8f))
    drawRoundRect(lm.accentColor.copy(alpha = 0.85f), Offset(bx, by), Size(badgeW, badgeH), CornerRadius(8f, 8f), style = Stroke(1.6f))
    drawText(measured, topLeft = Offset(bx + 7f, by + 3f))
}

private fun DrawScope.drawFountainMonument(lm: LandmarkSpec, animPhase: Float) {
    val r = lm.width * 0.44f
    val center = Offset(lm.x, lm.y)
    // Outer stone basin
    drawCircle(Color(0xFF5D536B), r, center)
    drawCircle(InkBlack, r, center, style = InkOutline)
    // Water / Ink pool inside
    drawCircle(lm.accentColor.copy(alpha = 0.78f), r * 0.82f, center)
    val ripple = ((animPhase * 12f) % (r * 0.65f))
    drawCircle(Color.White.copy(alpha = 0.42f), r * 0.25f + ripple * 0.55f, center, style = Stroke(2f))
    // Center tiered pedestal
    drawCircle(Color(0xFFDCD3C2), r * 0.34f, center)
    drawCircle(InkBlack, r * 0.34f, center, style = InkThin)
    // Animated central water/ink jet crown
    for (i in 0 until 6) {
        val ang = (i * 60.0 + animPhase * 22f) * (PI / 180.0)
        val sprayR = r * 0.45f + sin(animPhase * 4f + i) * 4f
        drawCircle(
            Color.White.copy(alpha = 0.78f),
            radius = 4.2f,
            center = Offset(center.x + cos(ang).toFloat() * sprayR, center.y + sin(ang).toFloat() * sprayR - 6f)
        )
    }
    drawCircle(SparkGold, 8f, Offset(center.x, center.y - 10f))
}

private fun DrawScope.drawMarqueeBuilding(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    drawRoundRect(Color(0xFF3E3546), Offset(left, top + 16f), Size(lm.width, lm.height - 16f), CornerRadius(10f, 10f))
    drawRoundRect(InkBlack, Offset(left, top + 16f), Size(lm.width, lm.height - 16f), CornerRadius(10f, 10f), style = InkOutline)
    // Marquee canopy
    drawRoundRect(lm.accentColor, Offset(left - 6f, top), Size(lm.width + 12f, 28f), CornerRadius(8f, 8f))
    drawRoundRect(InkBlack, Offset(left - 6f, top), Size(lm.width + 12f, 28f), CornerRadius(8f, 8f), style = InkOutline)
    // Blinking marquee bulbs
    var bx = left + 4f
    var idx = 0
    while (bx < left + lm.width - 2f) {
        val lit = (idx + (animPhase * 3f).toInt()) % 2 == 0
        drawCircle(if (lit) SparkGold else Color(0xFF5D4037), 2.8f, Offset(bx, top + 22f))
        bx += 14f
        idx++
    }
    // Red / purple theater entrance curtain
    drawRoundRect(Color(0xFFD94E4E), Offset(lm.x - 24f, top + 46f), Size(48f, lm.height - 48f), CornerRadius(12f, 12f))
    drawRoundRect(InkBlack, Offset(lm.x - 24f, top + 46f), Size(48f, lm.height - 48f), CornerRadius(12f, 12f), style = InkThin)
}

private fun DrawScope.drawGrandDomePalace(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    // Cream marble facade with columns
    drawRoundRect(Color(0xFFF4E7D1), Offset(left, top + 28f), Size(lm.width, lm.height - 28f), CornerRadius(10f, 10f))
    drawRoundRect(InkBlack, Offset(left, top + 28f), Size(lm.width, lm.height - 28f), CornerRadius(10f, 10f), style = InkOutline)
    // Grand purple/gold cupola dome
    drawOval(lm.accentColor, Offset(lm.x - lm.width * 0.34f, top - 14f), Size(lm.width * 0.68f, 58f))
    drawOval(InkBlack, Offset(lm.x - lm.width * 0.34f, top - 14f), Size(lm.width * 0.68f, 58f), style = InkOutline)
    // Columns
    for (c in 0..4) {
        val cx = left + 16f + c * ((lm.width - 32f) / 4f)
        drawRect(Color(0xFFE6D5B8), Offset(cx - 5f, top + 36f), Size(10f, lm.height - 40f))
        drawRect(InkBlack, Offset(cx - 5f, top + 36f), Size(10f, lm.height - 40f), style = InkThin)
    }
    drawCircle(SparkGold.copy(alpha = 0.85f + sin(animPhase * 2f) * 0.15f), 7f, Offset(lm.x, top - 16f))
}

private fun DrawScope.drawTowerMonument(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width * 0.32f
    val top = lm.y - lm.height / 2f
    val tw = lm.width * 0.64f
    drawRoundRect(Color(0xFFE6DFD1), Offset(left, top + 14f), Size(tw, lm.height - 14f), CornerRadius(14f, 14f))
    drawRoundRect(InkBlack, Offset(left, top + 14f), Size(tw, lm.height - 14f), CornerRadius(14f, 14f), style = InkOutline)
    drawCircle(lm.accentColor, tw * 0.5f, Offset(lm.x, top + 18f))
    drawCircle(InkBlack, tw * 0.5f, Offset(lm.x, top + 18f), style = InkOutline)
    // Rotating light beam cone
    val beamAngle = sin(animPhase * 0.9f) * 35f
    withTransform({ rotate(beamAngle, Offset(lm.x, top + 18f)) }) {
        val cone = Path().apply {
            moveTo(lm.x, top + 18f)
            lineTo(lm.x + 85f, top - 10f)
            lineTo(lm.x + 85f, top + 46f)
            close()
        }
        drawPath(cone, SparkGold.copy(alpha = 0.22f))
    }
    drawCircle(SparkGold, 9f, Offset(lm.x, top + 18f))
}

private fun DrawScope.drawStadiumArena(lm: LandmarkSpec) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    drawOval(Color(0xFFD9C7A3), Offset(left, top), Size(lm.width, lm.height))
    drawOval(InkBlack, Offset(left, top), Size(lm.width, lm.height), style = InkOutline)
    drawOval(lm.accentColor.copy(alpha = 0.85f), Offset(left + 12f, top + 12f), Size(lm.width - 24f, lm.height - 24f), style = Stroke(8f))
    drawOval(Color(0xFF3E8E41), Offset(left + 26f, top + 24f), Size(lm.width - 52f, lm.height - 48f))
    drawLine(Color.White.copy(alpha = 0.7f), Offset(lm.x, top + 26f), Offset(lm.x, top + lm.height - 26f), strokeWidth = 2f)
}

private fun DrawScope.drawStreetCourtLandmark(lm: LandmarkSpec) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    drawRoundRect(Color(0xFF3A3548), Offset(left, top), Size(lm.width, lm.height), CornerRadius(18f, 18f))
    drawRoundRect(lm.accentColor, Offset(left + 6f, top + 6f), Size(lm.width - 12f, lm.height - 12f), CornerRadius(14f, 14f), style = Stroke(4f))
    drawCircle(lm.accentColor.copy(alpha = 0.4f), 28f, Offset(lm.x, lm.y), style = Stroke(3f))
}

private fun DrawScope.drawAncestralPortal(lm: LandmarkSpec, animPhase: Float) {
    val r = lm.width * 0.42f
    val c = Offset(lm.x, lm.y)
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFFE0AAFF), Color(0xFF7B2CBF), Color.Transparent), c, r * 1.15f),
        radius = r * 1.15f,
        center = c
    )
    drawCircle(Color(0xFF2B2238), r * 0.88f, c, style = Stroke(14f))
    drawCircle(InkBlack, r * 0.96f, c, style = InkOutline)
    drawCircle(Color(0xFFC77DFF).copy(alpha = 0.65f + sin(animPhase * 3f) * 0.2f), r * 0.65f, c)
    for (i in 0 until 8) {
        val ang = (i * 45.0 + animPhase * 28f) * (PI / 180.0)
        drawCircle(SparkGold, 4f, Offset(c.x + cos(ang).toFloat() * r * 0.88f, c.y + sin(ang).toFloat() * r * 0.88f))
    }
}

private fun DrawScope.drawCrystalTreeHeart(lm: LandmarkSpec, animPhase: Float) {
    val c = Offset(lm.x, lm.y)
    val pulse = 1f + sin(animPhase * 2f) * 0.08f
    drawCircle(
        brush = Brush.radialGradient(listOf(SparkGold.copy(alpha = 0.6f), FlameOrange.copy(alpha = 0.25f), Color.Transparent), c, 125f * pulse),
        radius = 125f * pulse,
        center = c
    )
    // Massive luminescent trunk & canopy
    drawRoundRect(Color(0xFF6B3E26), Offset(c.x - 28f, c.y - 40f), Size(56f, 88f), CornerRadius(14f, 14f))
    drawRoundRect(InkBlack, Offset(c.x - 28f, c.y - 40f), Size(56f, 88f), CornerRadius(14f, 14f), style = InkOutline)
    drawCircle(Color(0xFF9D4EDD), 58f, Offset(c.x - 42f, c.y - 52f))
    drawCircle(Color(0xFFFF6A3D), 58f, Offset(c.x + 42f, c.y - 52f))
    drawCircle(SparkGold, 66f, Offset(c.x, c.y - 76f))
    drawCircle(Color.White.copy(alpha = 0.9f), 18f, Offset(c.x, c.y - 8f))
}

private fun DrawScope.drawGlowingGrottoOrLake(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    drawOval(Color(0xFF1B4F63), Offset(left, top), Size(lm.width, lm.height))
    drawOval(lm.accentColor.copy(alpha = 0.55f + sin(animPhase * 2f) * 0.15f), Offset(left + 14f, top + 12f), Size(lm.width - 28f, lm.height - 24f))
    drawOval(InkBlack, Offset(left, top), Size(lm.width, lm.height), style = InkOutline)
}

private fun DrawScope.drawGraffitiWall(lm: LandmarkSpec) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    drawRoundRect(Color(0xFF28203E), Offset(left, top + 20f), Size(lm.width, lm.height - 24f), CornerRadius(8f, 8f))
    drawRoundRect(InkBlack, Offset(left, top + 20f), Size(lm.width, lm.height - 24f), CornerRadius(8f, 8f), style = InkOutline)
    drawCircle(Color(0xFFFF3D9A), 20f, Offset(lm.x - 36f, lm.y + 4f))
    drawCircle(SparkGold, 18f, Offset(lm.x, lm.y))
    drawCircle(RunicTeal, 18f, Offset(lm.x + 38f, lm.y + 6f))
}

private fun DrawScope.drawTrainYardDepot(lm: LandmarkSpec) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    drawRoundRect(Color(0xFF42304D), Offset(left, top + 10f), Size(lm.width, lm.height - 20f), CornerRadius(10f, 10f))
    drawRoundRect(InkBlack, Offset(left, top + 10f), Size(lm.width, lm.height - 20f), CornerRadius(10f, 10f), style = InkOutline)
    drawLine(Color(0xFFFF6A3D), Offset(left + 10f, top + 34f), Offset(left + lm.width - 10f, top + 34f), strokeWidth = 5f)
}

fun DrawScope.drawProp2D(
    prop: PropSpec,
    animPhase: Float
) {
    withTransform({
        translate(prop.x, prop.y)
        scale(prop.scale, prop.scale, Offset.Zero)
    }) {
        when (prop.kind) {
            PropKind.LAMP -> {
                val glowAlpha = 0.22f + sin(animPhase * 2.2f + prop.x) * 0.05f
                drawCircle(
                    brush = Brush.radialGradient(listOf(prop.tint.copy(alpha = glowAlpha), Color.Transparent), Offset(0f, -34f), 46f),
                    radius = 46f,
                    center = Offset(0f, -34f)
                )
                drawRect(Color(0xFF2B2B33), Offset(-2.5f, -32f), Size(5f, 34f))
                drawCircle(prop.tint, 6.5f, Offset(0f, -36f))
                drawCircle(InkBlack, 6.5f, Offset(0f, -36f), style = InkThin)
            }
            PropKind.BENCH -> {
                drawOval(Color.Black.copy(alpha = 0.2f), Offset(-18f, 0f), Size(36f, 8f))
                drawRoundRect(Color(0xFF8D5B4C), Offset(-18f, -12f), Size(36f, 12f), CornerRadius(3f, 3f))
                drawRoundRect(InkBlack, Offset(-18f, -12f), Size(36f, 12f), CornerRadius(3f, 3f), style = InkThin)
                drawLine(Color(0xFF3E2723), Offset(-14f, -6f), Offset(14f, -6f), strokeWidth = 2f)
            }
            PropKind.UMBRELLA_TABLE -> {
                drawOval(Color.Black.copy(alpha = 0.22f), Offset(-18f, -2f), Size(36f, 10f))
                drawCircle(WarmParchment, 11f, Offset(0f, -6f))
                drawCircle(InkBlack, 11f, Offset(0f, -6f), style = InkThin)
                drawCircle(prop.tint, 17f, Offset(0f, -22f))
                drawCircle(InkBlack, 17f, Offset(0f, -22f), style = InkOutline)
                drawCircle(Color.White.copy(alpha = 0.55f), 4f, Offset(-4f, -26f))
            }
            PropKind.CRATE_STACK -> {
                drawRect(Color(0xFF9E6B43), Offset(-13f, -16f), Size(16f, 16f))
                drawRect(InkBlack, Offset(-13f, -16f), Size(16f, 16f), style = InkThin)
                drawRect(Color(0xFFB87F50), Offset(-1f, -12f), Size(14f, 12f))
                drawRect(InkBlack, Offset(-1f, -12f), Size(14f, 12f), style = InkThin)
            }
            PropKind.BANNER -> {
                drawLine(Color(0xFF4E342E), Offset(0f, 4f), Offset(0f, -38f), strokeWidth = 3f)
                val wave = sin(animPhase * 2.5f + prop.x * 0.05f) * 3f
                val flag = Path().apply {
                    moveTo(0f, -36f)
                    lineTo(18f, -32f + wave)
                    lineTo(14f, -22f + wave)
                    lineTo(0f, -20f)
                    close()
                }
                drawPath(flag, prop.tint)
                drawPath(flag, InkBlack, style = InkThin)
            }
            PropKind.FENCE_H -> {
                for (px in listOf(-18f, 0f, 18f)) {
                    drawRect(Color(0xFF8D5B4C), Offset(px - 2.5f, -14f), Size(5f, 16f))
                    drawRect(InkBlack, Offset(px - 2.5f, -14f), Size(5f, 16f), style = InkThin)
                }
                drawRect(Color(0xFFA6725B), Offset(-22f, -10f), Size(44f, 4f))
                drawRect(InkBlack, Offset(-22f, -10f), Size(44f, 4f), style = InkThin)
            }
            PropKind.PILLAR -> {
                drawRoundRect(Color(0xFFE6D5B8), Offset(-8f, -30f), Size(16f, 32f), CornerRadius(3f, 3f))
                drawRoundRect(InkBlack, Offset(-8f, -30f), Size(16f, 32f), CornerRadius(3f, 3f), style = InkThin)
                drawCircle(prop.tint, 5f, Offset(0f, -34f))
            }
            PropKind.SOCCER_GOAL -> {
                drawLine(Color.White, Offset(-24f, 0f), Offset(-24f, -24f), strokeWidth = 3.5f, cap = StrokeCap.Round)
                drawLine(Color.White, Offset(24f, 0f), Offset(24f, -24f), strokeWidth = 3.5f, cap = StrokeCap.Round)
                drawLine(Color.White, Offset(-24f, -24f), Offset(24f, -24f), strokeWidth = 3.5f, cap = StrokeCap.Round)
            }
            PropKind.WELL -> {
                drawCircle(Color(0xFF7D7482), 14f, Offset(0f, -4f))
                drawCircle(RunicTeal.copy(alpha = 0.8f), 9f, Offset(0f, -4f))
                drawCircle(InkBlack, 14f, Offset(0f, -4f), style = InkOutline)
            }
            PropKind.MUSHROOM_CLUSTER -> {
                drawRect(WarmParchment, Offset(-4f, -10f), Size(8f, 12f))
                drawOval(prop.tint, Offset(-11f, -20f), Size(22f, 12f))
                drawOval(InkBlack, Offset(-11f, -20f), Size(22f, 12f), style = InkThin)
                drawCircle(Color.White, 2.5f, Offset(-3f, -15f))
                drawCircle(Color.White, 2f, Offset(4f, -13f))
            }
        }
    }
}
