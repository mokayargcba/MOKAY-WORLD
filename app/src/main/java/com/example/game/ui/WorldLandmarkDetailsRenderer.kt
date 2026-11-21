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
import com.example.game.model.HouseSpec
import com.example.game.model.HouseStyle
import com.example.game.model.LandmarkKind
import com.example.game.model.LandmarkSpec
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.InkBlack
import com.example.ui.theme.RunicTeal
import com.example.ui.theme.SparkGold
import com.example.ui.theme.WarmParchment
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal fun DrawScope.drawStreetCourtLandmark(lm: LandmarkSpec) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    if (lm.kind == LandmarkKind.BASKET_COURT) {
        // Vivid blue & orange Arena Viva streetball court with hoops
        drawRoundRect(Color(0xFF1E4C8A), Offset(left, top), Size(lm.width, lm.height), CornerRadius(14f, 14f))
        drawRoundRect(Color(0xFFFF6A3D), Offset(left + 12f, top + 12f), Size(lm.width - 24f, lm.height - 24f), CornerRadius(10f, 10f))
        drawRoundRect(Color.White.copy(alpha = 0.85f), Offset(left + 14f, top + 14f), Size(lm.width - 28f, lm.height - 28f), CornerRadius(8f, 8f), style = Stroke(3f))
        drawLine(Color.White.copy(alpha = 0.85f), Offset(lm.x, top + 14f), Offset(lm.x, top + lm.height - 14f), strokeWidth = 2.8f)
        drawCircle(Color.White.copy(alpha = 0.85f), 22f, Offset(lm.x, lm.y), style = Stroke(2.8f))
        // Twin hoops
        drawCircle(SparkGold, 7f, Offset(left + 26f, lm.y), style = Stroke(3f))
        drawCircle(SparkGold, 7f, Offset(left + lm.width - 26f, lm.y), style = Stroke(3f))
    } else {
        // Skate Plaza / BMX Park 2.5D concrete bowl + orange grind rails & ramps
        drawRoundRect(Color(0xFF3E3A52), Offset(left, top), Size(lm.width, lm.height), CornerRadius(24f, 24f))
        drawRoundRect(InkBlack, Offset(left, top), Size(lm.width, lm.height), CornerRadius(24f, 24f), style = InkOutline)
        drawRoundRect(Color(0xFF5C5675), Offset(left + 16f, top + 16f), Size(lm.width - 32f, lm.height - 32f), CornerRadius(18f, 18f))
        drawRoundRect(lm.accentColor, Offset(left + 18f, top + 18f), Size(lm.width - 36f, lm.height - 36f), CornerRadius(16f, 16f), style = Stroke(4f))
        // Orange skate ramp & center grind rail
        drawRoundRect(Color(0xFFFF6A3D), Offset(left + 24f, top + 28f), Size(42f, lm.height - 56f), CornerRadius(8f, 8f))
        drawLine(SparkGold, Offset(lm.x - 24f, lm.y), Offset(lm.x + 28f, lm.y), strokeWidth = 5f, cap = StrokeCap.Round)
    }
}

internal fun DrawScope.drawGraffitiWall(lm: LandmarkSpec) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    drawRoundRect(Color(0xFF28203E), Offset(left, top + 16f), Size(lm.width, lm.height - 20f), CornerRadius(10f, 10f))
    drawRoundRect(InkBlack, Offset(left, top + 16f), Size(lm.width, lm.height - 20f), CornerRadius(10f, 10f), style = InkOutline)
    // Vibrant street-art spray bursts matching Rhythm Hood's "CREATE INSPIRE" mural
    drawCircle(Color(0xFFFF3D9A), 26f, Offset(lm.x - 42f, lm.y + 4f))
    drawCircle(SparkGold, 24f, Offset(lm.x, lm.y - 4f))
    drawCircle(RunicTeal, 24f, Offset(lm.x + 42f, lm.y + 6f))
    drawCircle(Color(0xFFFF6A3D), 14f, Offset(lm.x - 18f, lm.y + 14f))
    drawCircle(Color(0xFF9D4EDD), 14f, Offset(lm.x + 20f, lm.y - 10f))
}

internal fun DrawScope.drawTrainYardDepot(lm: LandmarkSpec) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    drawRoundRect(Color(0xFF3E2C4D), Offset(left, top + 8f), Size(lm.width, lm.height - 16f), CornerRadius(10f, 10f))
    drawRoundRect(InkBlack, Offset(left, top + 8f), Size(lm.width, lm.height - 16f), CornerRadius(10f, 10f), style = InkOutline)
    // Parked graffiti railcar inside yard
    drawRoundRect(Color(0xFF6C63FF), Offset(left + 20f, top + 24f), Size(lm.width - 40f, 44f), CornerRadius(8f, 8f))
    drawRoundRect(InkBlack, Offset(left + 20f, top + 24f), Size(lm.width - 40f, 44f), CornerRadius(8f, 8f), style = InkThin)
    drawCircle(Color(0xFFFF3D9A), 14f, Offset(lm.x - 24f, top + 46f))
    drawCircle(SparkGold, 12f, Offset(lm.x + 20f, top + 46f))
    // Wooden skate jump ramps beside track
    drawRect(Color(0xFFFF6A3D), Offset(left + 22f, top + lm.height - 34f), Size(lm.width - 44f, 14f))
}

internal fun DrawScope.drawStageShellReef(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    // Arrecife Musical: Glowing bioluminescent clam stage + twin DJ speaker stacks!
    drawOval(Color(0xFF2B214D), Offset(left + 12f, top + 34f), Size(lm.width - 24f, lm.height - 36f))
    val shell = Path().apply {
        moveTo(left + 18f, lm.y + 10f)
        cubicTo(left + 12f, top - 16f, left + lm.width - 12f, top - 16f, left + lm.width - 18f, lm.y + 10f)
        close()
    }
    drawPath(shell, lm.accentColor)
    drawPath(shell, InkBlack, style = InkOutline)
    // Twin side speakers
    for (sx in listOf(left + 4f, left + lm.width - 30f)) {
        drawRoundRect(Color(0xFF1F1D2E), Offset(sx, top + 24f), Size(26f, 52f), CornerRadius(5f, 5f))
        drawCircle(Color(0xFF6FD0E8), 7f, Offset(sx + 13f, top + 40f))
        drawCircle(Color(0xFF6FD0E8), 8f, Offset(sx + 13f, top + 62f))
    }
    val beat = 1f + sin(animPhase * 4f) * 0.15f
    drawCircle(Color(0xFFA6F2FF).copy(alpha = 0.6f), 14f * beat, Offset(lm.x, lm.y))
}

internal fun DrawScope.drawPearlGrotto(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    // Dark rock cavern mouth + glowing pearl inside
    val cave = Path().apply {
        moveTo(left + 8f, top + lm.height - 10f)
        cubicTo(left + 18f, top - 8f, left + lm.width - 18f, top - 8f, left + lm.width - 8f, top + lm.height - 10f)
        close()
    }
    drawPath(cave, Color(0xFF243447))
    drawPath(cave, InkBlack, style = InkOutline)
    drawOval(Color(0xFF0E1521), Offset(lm.x - 42f, top + 24f), Size(84f, lm.height - 36f))
    val glow = 0.55f + sin(animPhase * 2.2f) * 0.2f
    drawCircle(lm.accentColor.copy(alpha = glow), 34f, Offset(lm.x, lm.y + 10f))
    drawCircle(Color(0xFFFFF8E1), 16f, Offset(lm.x, lm.y + 12f))
    drawCircle(Color.White, 5f, Offset(lm.x - 5f, lm.y + 6f))
}

internal fun DrawScope.drawGlowingLake(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    drawOval(Color(0xFF143C4A), Offset(left, top), Size(lm.width, lm.height))
    drawOval(lm.accentColor.copy(alpha = 0.62f + sin(animPhase * 2f) * 0.14f), Offset(left + 16f, top + 14f), Size(lm.width - 32f, lm.height - 28f))
    drawOval(Color.White.copy(alpha = 0.3f), Offset(left + 36f, top + 26f), Size(lm.width - 72f, lm.height - 52f), style = Stroke(2.2f))
    drawOval(InkBlack, Offset(left, top), Size(lm.width, lm.height), style = InkOutline)
}

internal fun DrawScope.drawCoralPier(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    // Planked wooden dock jetty
    drawRoundRect(Color(0xFF8C583A), Offset(left + 10f, top + 24f), Size(lm.width - 20f, lm.height - 40f), CornerRadius(8f, 8f))
    drawRoundRect(InkBlack, Offset(left + 10f, top + 24f), Size(lm.width - 20f, lm.height - 40f), CornerRadius(8f, 8f), style = InkOutline)
    var px = left + 26f
    while (px < left + lm.width - 16f) {
        drawLine(InkBlack.copy(alpha = 0.3f), Offset(px, top + 26f), Offset(px, top + lm.height - 18f), strokeWidth = 2f)
        px += 18f
    }
    // Cute docked yellow submarine from Marea Viva artwork!
    val bob = sin(animPhase * 2f) * 3f
    drawOval(SparkGold, Offset(lm.x - 28f, top + lm.height - 30f + bob), Size(56f, 26f))
    drawOval(InkBlack, Offset(lm.x - 28f, top + lm.height - 30f + bob), Size(56f, 26f), style = InkThin)
    drawCircle(Color(0xFF8BE0E6), 5f, Offset(lm.x - 8f, top + lm.height - 17f + bob))
    drawCircle(Color(0xFF8BE0E6), 5f, Offset(lm.x + 10f, top + lm.height - 17f + bob))
}

internal fun DrawScope.drawThemedGarden(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    drawRoundRect(Color(0xFF2E5C3E), Offset(left + 8f, top + 8f), Size(lm.width - 16f, lm.height - 16f), CornerRadius(14f, 14f))
    drawRoundRect(InkBlack, Offset(left + 8f, top + 8f), Size(lm.width - 16f, lm.height - 16f), CornerRadius(14f, 14f), style = InkOutline)
    if (lm.id == "ar_jardines") {
        // Artelia Jardines Geométricos clipped boxwood paths + mini fountain
        drawRoundRect(Color(0xFF4CC37A), Offset(left + 20f, top + 20f), Size(44f, 34f), CornerRadius(8f, 8f))
        drawRoundRect(Color(0xFF4CC37A), Offset(left + lm.width - 64f, top + 20f), Size(44f, 34f), CornerRadius(8f, 8f))
        drawCircle(Color(0xFF6FD0E8), 18f, Offset(lm.x, lm.y))
        drawCircle(InkBlack, 18f, Offset(lm.x, lm.y), style = InkThin)
    } else {
        // Marea Viva Bosque de Algas swaying tall bioluminescent kelp
        for (kx in listOf(lm.x - 42f, lm.x, lm.x + 42f)) {
            val sway = sin(animPhase * 1.8f + kx) * 6f
            drawLine(lm.accentColor, Offset(kx, lm.y + 30f), Offset(kx + sway, lm.y - 26f), strokeWidth = 10f, cap = StrokeCap.Round)
        }
    }
}

internal fun DrawScope.drawArtOrIllusionBuilding(lm: LandmarkSpec, animPhase: Float) {
    val style = if (lm.kind == LandmarkKind.GALLERY) HouseStyle.RENAISSANCE_VILLA else HouseStyle.CUBIST_STUDIO
    drawHouse2D(HouseSpec(lm.id, "", lm.x, lm.y, lm.width * 0.92f, lm.height * 0.88f, lm.accentColor, Color(0xFFF4E7D1), style), animPhase)
    // Framed art canvas emblem
    drawRect(SparkGold, Offset(lm.x - 16f, lm.y - 12f), Size(32f, 22f))
    drawRect(Color(0xFF6C63FF), Offset(lm.x - 12f, lm.y - 8f), Size(24f, 14f))
}

internal fun DrawScope.drawGothicDoubtManor(lm: LandmarkSpec, animPhase: Float) {
    drawHouse2D(HouseSpec(lm.id, "", lm.x, lm.y, lm.width * 0.92f, lm.height * 0.88f, lm.accentColor, Color(0xFF2D1E42), HouseStyle.GOTHIC_HOUSE), animPhase)
    // Glowing "?" floating billboard from Sombravía's Calle de las Dudas
    drawRoundRect(Color(0xFF1A0F2E), Offset(lm.x - 20f, lm.y - lm.height * 0.52f), Size(40f, 22f), CornerRadius(6f, 6f))
    drawRoundRect(Color(0xFFE0AAFF), Offset(lm.x - 20f, lm.y - lm.height * 0.52f), Size(40f, 22f), CornerRadius(6f, 6f), style = Stroke(2f))
}

internal fun DrawScope.drawFearMaze(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    drawRoundRect(Color(0xFF241638), Offset(left, top), Size(lm.width, lm.height), CornerRadius(12f, 12f))
    drawRoundRect(lm.accentColor, Offset(left + 6f, top + 6f), Size(lm.width - 12f, lm.height - 12f), CornerRadius(10f, 10f), style = Stroke(5f))
    drawRoundRect(lm.accentColor.copy(alpha = 0.7f), Offset(left + 26f, top + 22f), Size(lm.width - 52f, lm.height - 44f), CornerRadius(8f, 8f), style = Stroke(4f))
    drawCircle(SparkGold.copy(alpha = 0.75f + sin(animPhase * 2.5f) * 0.2f), 11f, Offset(lm.x, lm.y))
}

internal fun DrawScope.drawAncestralPortal(lm: LandmarkSpec, animPhase: Float) {
    val r = lm.width * 0.42f
    val c = Offset(lm.x, lm.y)
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFFE0AAFF), Color(0xFF7B2CBF), Color.Transparent), c, r * 1.2f),
        radius = r * 1.2f,
        center = c
    )
    drawCircle(Color(0xFF2B2238), r * 0.88f, c, style = Stroke(15f))
    drawCircle(InkBlack, r * 0.97f, c, style = InkOutline)
    drawCircle(Color(0xFFC77DFF).copy(alpha = 0.68f + sin(animPhase * 3f) * 0.22f), r * 0.64f, c)
    for (i in 0 until 8) {
        val ang = (i * 45.0 + animPhase * 28f) * (PI / 180.0)
        drawCircle(SparkGold, 4.5f, Offset(c.x + cos(ang).toFloat() * r * 0.88f, c.y + sin(ang).toFloat() * r * 0.88f))
    }
}

internal fun DrawScope.drawCrystalTreeHeart(lm: LandmarkSpec, animPhase: Float) {
    val c = Offset(lm.x, lm.y)
    val pulse = 1f + sin(animPhase * 2f) * 0.08f
    drawCircle(
        brush = Brush.radialGradient(listOf(SparkGold.copy(alpha = 0.65f), FlameOrange.copy(alpha = 0.28f), Color.Transparent), c, 138f * pulse),
        radius = 138f * pulse,
        center = c
    )
    // Gnarled golden-bark trunk & 7-color luminous canopy
    val trunk = Path().apply {
        moveTo(c.x - 42f, c.y + 52f)
        quadraticTo(c.x - 24f, c.y + 8f, c.x - 26f, c.y - 44f)
        lineTo(c.x + 26f, c.y - 44f)
        quadraticTo(c.x + 24f, c.y + 8f, c.x + 42f, c.y + 52f)
        close()
    }
    drawPath(trunk, Color(0xFF6B3E26))
    drawPath(trunk, InkBlack, style = InkOutline)

    drawCircle(Color(0xFF9D4EDD), 60f, Offset(c.x - 48f, c.y - 54f))
    drawCircle(Color(0xFF2EC4B6), 54f, Offset(c.x + 48f, c.y - 54f))
    drawCircle(FlameOrange, 56f, Offset(c.x - 24f, c.y - 76f))
    drawCircle(Color(0xFFE97893), 56f, Offset(c.x + 24f, c.y - 76f))
    drawCircle(SparkGold, 66f, Offset(c.x, c.y - 84f))

    // Glowing smiling Corazón de Mokaya inside trunk hollow
    drawSmoothFlame(c.x, c.y + 4f, 34f, 44f)
    drawCircle(InkBlack, 2.2f, Offset(c.x - 5f, c.y - 10f))
    drawCircle(InkBlack, 2.2f, Offset(c.x + 5f, c.y - 10f))
}
