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
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.InkBlack
import com.example.ui.theme.RunicTeal
import com.example.ui.theme.SparkGold
import com.example.ui.theme.WarmParchment
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawLandmark2D(
    lm: LandmarkSpec,
    animPhase: Float,
    textMeasurer: TextMeasurer
) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    val w = lm.width
    val h = lm.height

    // Soft contact ground shadow
    drawOval(Color.Black.copy(alpha = 0.32f), Offset(left - 8f, top + h - 16f), Size(w + 16f, 28f))

    when (lm.kind) {
        LandmarkKind.FOUNTAIN -> drawThemedBiomeFountain(lm, animPhase)
        LandmarkKind.CINEMA -> drawCinemaRubber(lm, animPhase)
        LandmarkKind.THEATER -> drawThemedTheater(lm, animPhase)
        LandmarkKind.STUDIO -> drawThemedStudio(lm, animPhase)
        LandmarkKind.PRINT_SHOP -> drawPrintShop(lm)
        LandmarkKind.WORKSHOP -> drawSparkWorkshop(lm, animPhase)
        LandmarkKind.CAFE -> drawThemedCafeOrBeach(lm, animPhase)
        LandmarkKind.CATHEDRAL_LIBRARY -> drawCathedralOrRuins(lm, animPhase)
        LandmarkKind.DREAM_PALACE -> drawDreamPalaceOrCrown(lm, animPhase)
        LandmarkKind.OBSERVATORY, LandmarkKind.LIGHTHOUSE, LandmarkKind.BEACON_TOWER -> drawTowerMonument(lm, animPhase)
        LandmarkKind.STADIUM -> drawStadiumArena(lm)
        LandmarkKind.SKATE_PARK, LandmarkKind.BASKET_COURT -> drawStreetCourtLandmark(lm)
        LandmarkKind.GRAFFITI_WALL -> drawGraffitiWall(lm)
        LandmarkKind.TRAIN_YARD -> drawTrainYardDepot(lm)
        LandmarkKind.STAGE_SHELL -> drawStageShellReef(lm, animPhase)
        LandmarkKind.GROTTO -> drawPearlGrotto(lm, animPhase)
        LandmarkKind.GLOWING_LAKE -> drawGlowingLake(lm, animPhase)
        LandmarkKind.PIER -> drawCoralPier(lm, animPhase)
        LandmarkKind.GARDEN -> drawThemedGarden(lm, animPhase)
        LandmarkKind.GALLERY, LandmarkKind.MUSEUM, LandmarkKind.ILLUSION_HALL -> drawArtOrIllusionBuilding(lm, animPhase)
        LandmarkKind.GOTHIC_MANOR -> drawGothicDoubtManor(lm, animPhase)
        LandmarkKind.MAZE -> drawFearMaze(lm, animPhase)
        LandmarkKind.ANCESTRAL_PORTAL -> drawAncestralPortal(lm, animPhase)
        LandmarkKind.CRYSTAL_HEART, LandmarkKind.CREATOR_CROWN -> drawCrystalTreeHeart(lm, animPhase)
        else -> {
            drawHouse2D(
                house = HouseSpec(lm.id, "", lm.x, lm.y, w, h, lm.accentColor, Color(0xFFEBDCCB), HouseStyle.TOWNHOUSE),
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
    val by = top + h + 5f
    drawRoundRect(Color(0xFF1A1426).copy(alpha = 0.90f), Offset(bx, by), Size(badgeW, badgeH), CornerRadius(8f, 8f))
    drawRoundRect(lm.accentColor.copy(alpha = 0.88f), Offset(bx, by), Size(badgeW, badgeH), CornerRadius(8f, 8f), style = Stroke(1.6f))
    drawText(measured, topLeft = Offset(bx + 7f, by + 3f))
}

private fun DrawScope.drawThemedBiomeFountain(lm: LandmarkSpec, animPhase: Float) {
    val r = lm.width * 0.42f
    val c = Offset(lm.x, lm.y)

    when (lm.id) {
        "tm_fuente" -> {
            // Tintópolis: Fuente de la Tinta (B&W stone pool + glossy black ink + tilted fountain pen nib!)
            drawCircle(Color(0xFF424242), r, c)
            drawCircle(InkBlack, r, c, style = InkOutline)
            drawCircle(Color(0xFF121214), r * 0.80f, c)
            val ripple = (animPhase * 12f) % (r * 0.6f)
            drawCircle(Color.White.copy(alpha = 0.45f), r * 0.25f + ripple * 0.55f, c, style = Stroke(2.2f))
            // Ink splash jets
            for (i in 0 until 6) {
                val ang = (i * 60.0 + animPhase * 22f) * (PI / 180.0)
                val sr = r * 0.42f + sin(animPhase * 4f + i) * 4f
                drawCircle(Color(0xFFEAEAEA), 4.5f, Offset(c.x + cos(ang).toFloat() * sr, c.y + sin(ang).toFloat() * sr - 6f))
            }
            // Giant tilted vintage fountain pen nib rising from center
            withTransform({ rotate(28f, Offset(c.x, c.y)) }) {
                val nib = Path().apply {
                    moveTo(c.x, c.y + 8f)
                    lineTo(c.x - 14f, c.y - 26f)
                    lineTo(c.x - 8f, c.y - 54f)
                    lineTo(c.x + 8f, c.y - 54f)
                    lineTo(c.x + 14f, c.y - 26f)
                    close()
                }
                drawPath(nib, Color(0xFFD9D9D9))
                drawPath(nib, InkBlack, style = InkOutline)
                drawLine(InkBlack, Offset(c.x, c.y - 4f), Offset(c.x, c.y - 28f), strokeWidth = 2f)
                drawCircle(InkBlack, 3.2f, Offset(c.x, c.y - 28f))
            }
        }
        "ar_humanismo" -> {
            // Artelia: Stepped Cream Marble Plaza Humanismo + White Statue + 4 mini corner jets
            drawCircle(Color(0xFFE6D5B8), r, c)
            drawCircle(InkBlack, r, c, style = InkOutline)
            drawCircle(Color(0xFFF6EBDC), r * 0.72f, c)
            drawCircle(Color(0xFFE0A94F), r * 0.72f, c, style = Stroke(4f))
            // Sculpted white marble statue centerpiece
            drawRoundRect(Color(0xFFD6C2A3), Offset(c.x - 18f, c.y - 12f), Size(36f, 22f), CornerRadius(5f, 5f))
            drawRoundRect(InkBlack, Offset(c.x - 18f, c.y - 12f), Size(36f, 22f), CornerRadius(5f, 5f), style = InkThin)
            drawOval(Color(0xFFFDFBF7), Offset(c.x - 12f, c.y - 36f), Size(24f, 28f))
            drawCircle(Color(0xFFFDFBF7), 10f, Offset(c.x, c.y - 44f))
            drawCircle(InkBlack, 10f, Offset(c.x, c.y - 44f), style = InkThin)
            for (angDeg in listOf(45.0, 135.0, 225.0, 315.0)) {
                val rad = angDeg * (PI / 180.0)
                val fx = c.x + cos(rad).toFloat() * r * 0.74f
                val fy = c.y + sin(rad).toFloat() * r * 0.74f
                drawCircle(Color(0xFF6FD0E8), 11f, Offset(fx, fy))
                drawCircle(Color.White.copy(alpha = 0.75f), 4f, Offset(fx, fy - 4f))
            }
        }
        "mv_plaza" -> {
            // Marea Viva: Plaza Coral purple seashell fountain & glowing pearl
            drawCircle(Color(0xFF1F6C82), r, c)
            drawCircle(InkBlack, r, c, style = InkOutline)
            drawCircle(Color(0xFF4AD0E8).copy(alpha = 0.7f), r * 0.8f, c)
            val shell = Path().apply {
                moveTo(c.x - 32f, c.y + 6f)
                cubicTo(c.x - 38f, c.y - 38f, c.x + 38f, c.y - 38f, c.x + 32f, c.y + 6f)
                close()
            }
            drawPath(shell, Color(0xFF9D4EDD))
            drawPath(shell, InkBlack, style = InkOutline)
            drawCircle(Color(0xFFFFF8E1), 12f, Offset(c.x, c.y - 8f))
            drawCircle(Color.White, 4f, Offset(c.x - 4f, c.y - 12f))
        }
        "pc_formas" -> {
            // Picasia: Plaza de las Formas stacked cubist monument!
            drawCircle(Color(0xFF2B1C47), r, c)
            drawCircle(Color(0xFF2EC4B6).copy(alpha = 0.75f), r * 0.80f, c)
            drawCircle(InkBlack, r, c, style = InkOutline)
            // Stacked vibrant geometric sculpture
            drawRect(Color(0xFFE97893), Offset(c.x - 20f, c.y - 12f), Size(40f, 24f))
            drawRect(InkBlack, Offset(c.x - 20f, c.y - 12f), Size(40f, 24f), style = InkThin)
            drawCircle(Color(0xFFFFB703), 15f, Offset(c.x, c.y - 26f))
            drawCircle(InkBlack, 15f, Offset(c.x, c.y - 26f), style = InkThin)
            val tri = Path().apply {
                moveTo(c.x, c.y - 62f)
                lineTo(c.x - 14f, c.y - 36f)
                lineTo(c.x + 14f, c.y - 36f)
                close()
            }
            drawPath(tri, Color(0xFF6C63FF))
            drawPath(tri, InkBlack, style = InkThin)
        }
        "sm_reflejo" -> {
            // Sombravía: Dark Mirror Pool + Roaring Violet Flame Chalice
            drawCircle(Color(0xFF140924), r, c)
            drawCircle(Color(0xFF9D4EDD), r * 0.82f, c, style = Stroke(3.5f))
            drawCircle(InkBlack, r, c, style = InkOutline)
            drawCircle(Color(0xFF2F1852), r * 0.45f, c)
            drawCircle(Color(0xFFE0AAFF).copy(alpha = 0.35f + sin(animPhase * 3f) * 0.12f), 36f, Offset(c.x, c.y - 14f))
            drawSmoothFlame(c.x, c.y - 2f, 28f, 44f)
        }
        else -> {
            // Plaza Infinita / Constelaria Crystal Spire Fountain
            drawCircle(Color(0xFF5D536B), r, c)
            drawCircle(InkBlack, r, c, style = InkOutline)
            drawCircle(lm.accentColor.copy(alpha = 0.80f), r * 0.82f, c)
            val ripple = ((animPhase * 12f) % (r * 0.65f))
            drawCircle(Color.White.copy(alpha = 0.45f), r * 0.25f + ripple * 0.55f, c, style = Stroke(2.2f))
            drawCircle(Color(0xFFE6D5B8), r * 0.34f, c)
            drawCircle(InkBlack, r * 0.34f, c, style = InkThin)
            val spire = Path().apply {
                moveTo(c.x, c.y - 46f)
                lineTo(c.x - 12f, c.y - 8f)
                lineTo(c.x + 12f, c.y - 8f)
                close()
            }
            drawPath(spire, SparkGold)
            drawPath(spire, InkBlack, style = InkThin)
        }
    }
}

private fun DrawScope.drawCinemaRubber(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    drawRoundRect(Color(0xFF333138), Offset(left, top + 22f), Size(lm.width, lm.height - 22f), CornerRadius(10f, 10f))
    drawRoundRect(InkBlack, Offset(left, top + 22f), Size(lm.width, lm.height - 22f), CornerRadius(10f, 10f), style = InkOutline)
    // Giant twin vintage cartoon film reels perched on the roof!
    for (rx in listOf(lm.x - 32f, lm.x + 32f)) {
        drawCircle(Color(0xFF1F1F23), 24f, Offset(rx, top + 4f))
        drawCircle(Color(0xFFDADADA), 17f, Offset(rx, top + 4f), style = Stroke(2.5f))
        drawCircle(InkBlack, 24f, Offset(rx, top + 4f), style = InkOutline)
        for (h in 0..3) {
            val ang = (h * 90.0 + animPhase * 45f) * (PI / 180.0)
            drawCircle(Color(0xFFE0E0E0), 4.5f, Offset(rx + cos(ang).toFloat() * 10f, top + 4f + sin(ang).toFloat() * 10f))
        }
    }
    // Classic marquee canopy with chasing bulbs
    drawRoundRect(lm.accentColor, Offset(left - 8f, top + 14f), Size(lm.width + 16f, 26f), CornerRadius(8f, 8f))
    drawRoundRect(InkBlack, Offset(left - 8f, top + 14f), Size(lm.width + 16f, 26f), CornerRadius(8f, 8f), style = InkOutline)
    var bx = left + 4f
    var idx = 0
    while (bx < left + lm.width - 2f) {
        val lit = (idx + (animPhase * 3f).toInt()) % 2 == 0
        drawCircle(if (lit) SparkGold else Color(0xFF4E4039), 2.8f, Offset(bx, top + 34f))
        bx += 14f
        idx++
    }
    drawRoundRect(Color(0xFF231F20), Offset(lm.x - 24f, top + 52f), Size(48f, lm.height - 54f), CornerRadius(10f, 10f))
}

private fun DrawScope.drawThemedTheater(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    if (lm.id == "rh_arena") {
        // Rhythm Hood Freestyle Arena circular battle stage!
        drawOval(Color(0xFF281A3B), Offset(left, top + 12f), Size(lm.width, lm.height - 12f))
        drawOval(Color(0xFFFF3D9A), Offset(left + 10f, top + 20f), Size(lm.width - 20f, lm.height - 28f), style = Stroke(5f))
        drawOval(Color(0xFF42216B), Offset(left + 26f, top + 32f), Size(lm.width - 52f, lm.height - 52f))
        drawOval(InkBlack, Offset(left, top + 12f), Size(lm.width, lm.height - 12f), style = InkOutline)
        drawCircle(Color(0xFF2EC4B6).copy(alpha = 0.25f + sin(animPhase * 3f) * 0.1f), 42f, Offset(lm.x, lm.y + 8f))
        return
    }
    drawRoundRect(Color(0xFF3E3546), Offset(left, top + 18f), Size(lm.width, lm.height - 18f), CornerRadius(10f, 10f))
    drawRoundRect(InkBlack, Offset(left, top + 18f), Size(lm.width, lm.height - 18f), CornerRadius(10f, 10f), style = InkOutline)
    drawRoundRect(lm.accentColor, Offset(left - 6f, top + 2f), Size(lm.width + 12f, 26f), CornerRadius(8f, 8f))
    drawRoundRect(InkBlack, Offset(left - 6f, top + 2f), Size(lm.width + 12f, 26f), CornerRadius(8f, 8f), style = InkOutline)
    // Velvet curtains & twin comedy/tragedy masks
    drawRoundRect(Color(0xFFC93B4B), Offset(lm.x - 28f, top + 46f), Size(56f, lm.height - 48f), CornerRadius(12f, 12f))
    drawCircle(WarmParchment, 11f, Offset(lm.x - 16f, top + 15f))
    drawCircle(Color(0xFF2B2238), 11f, Offset(lm.x + 16f, top + 15f))
}

private fun DrawScope.drawThemedStudio(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    if (lm.id == "tm_estudio") {
        // Tintópolis Estudio Ink: Loft + elevated wooden INK water tower + leaning paintbrush
        drawHouse2D(HouseSpec(lm.id, "", lm.x, lm.y + 8f, lm.width * 0.85f, lm.height * 0.78f, Color(0xFF3A3A3A), Color(0xFFE0E0E0)), animPhase)
        // Stilts & INK Water Tower
        drawLine(InkBlack, Offset(lm.x - 16f, top + 30f), Offset(lm.x - 22f, top + 58f), strokeWidth = 3f)
        drawLine(InkBlack, Offset(lm.x + 16f, top + 30f), Offset(lm.x + 22f, top + 58f), strokeWidth = 3f)
        drawRoundRect(Color(0xFFBDBDBD), Offset(lm.x - 24f, top - 14f), Size(48f, 44f), CornerRadius(6f, 6f))
        drawRoundRect(InkBlack, Offset(lm.x - 24f, top - 14f), Size(48f, 44f), CornerRadius(6f, 6f), style = InkOutline)
        drawCircle(InkBlack, 10f, Offset(lm.x, top + 8f))
    } else {
        // Rhythm Hood Beat Labs: Neon turntable rooftop + speaker towers
        drawHouse2D(HouseSpec(lm.id, "", lm.x, lm.y + 6f, lm.width * 0.9f, lm.height * 0.82f, lm.accentColor, Color(0xFF3E2A56), HouseStyle.NEON_LOFT), animPhase)
        drawCircle(Color(0xFF1A1426), 24f, Offset(lm.x, top + 8f))
        drawCircle(Color(0xFFFF3D9A), 16f, Offset(lm.x, top + 8f), style = Stroke(3f))
        drawCircle(SparkGold, 6f, Offset(lm.x, top + 8f))
    }
}

private fun DrawScope.drawPrintShop(lm: LandmarkSpec) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    drawRoundRect(Color(0xFFE0E0E0), Offset(left, top + 20f), Size(lm.width, lm.height - 20f), CornerRadius(8f, 8f))
    drawRoundRect(InkBlack, Offset(left, top + 20f), Size(lm.width, lm.height - 20f), CornerRadius(8f, 8f), style = InkOutline)
    // Newspaper printing press roller on roof
    drawRoundRect(Color(0xFF333333), Offset(lm.x - 36f, top), Size(72f, 24f), CornerRadius(10f, 10f))
    drawRoundRect(InkBlack, Offset(lm.x - 36f, top), Size(72f, 24f), CornerRadius(10f, 10f), style = InkOutline)
    drawRect(WarmParchment, Offset(lm.x - 24f, top + 5f), Size(48f, 14f))
}

private fun DrawScope.drawSparkWorkshop(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    drawHouse2D(HouseSpec(lm.id, "", lm.x, lm.y + 6f, lm.width * 0.9f, lm.height * 0.84f, lm.accentColor, Color(0xFFEBDCCB), HouseStyle.TOWNHOUSE), animPhase)
    // Rotating golden gear crest above roof
    withTransform({ rotate(animPhase * 28f, Offset(lm.x, top + 4f)) }) {
        drawCircle(SparkGold, 16f, Offset(lm.x, top + 4f))
        drawCircle(InkBlack, 16f, Offset(lm.x, top + 4f), style = InkOutline)
        drawCircle(Color(0xFF5D3A24), 6f, Offset(lm.x, top + 4f))
    }
}

private fun DrawScope.drawThemedCafeOrBeach(lm: LandmarkSpec, animPhase: Float) {
    if (lm.id == "av_playa") {
        // Arena Viva Playa Activa surf hut & surfboards
        drawHouse2D(HouseSpec(lm.id, "", lm.x, lm.y, lm.width * 0.85f, lm.height * 0.8f, Color(0xFF2EC4B6), Color(0xFFF6E3BA), HouseStyle.CAFE_AWNING), animPhase)
        // Propped surfboards outside
        drawOval(Color(0xFFFF6A3D), Offset(lm.x + lm.width * 0.34f, lm.y), Size(14f, 44f))
        drawOval(InkBlack, Offset(lm.x + lm.width * 0.34f, lm.y), Size(14f, 44f), style = InkThin)
        drawOval(SparkGold, Offset(lm.x + lm.width * 0.42f, lm.y + 6f), Size(14f, 42f))
        drawOval(InkBlack, Offset(lm.x + lm.width * 0.42f, lm.y + 6f), Size(14f, 42f), style = InkThin)
    } else {
        drawHouse2D(HouseSpec(lm.id, "", lm.x, lm.y, lm.width * 0.9f, lm.height * 0.85f, lm.accentColor, Color(0xFFF4E7D1), HouseStyle.CAFE_AWNING), animPhase)
        // Cute rooftop coffee cup emblem
        drawRoundRect(WarmParchment, Offset(lm.x - 14f, lm.y - lm.height * 0.56f), Size(28f, 20f), CornerRadius(6f, 6f))
        drawRoundRect(InkBlack, Offset(lm.x - 14f, lm.y - lm.height * 0.56f), Size(28f, 20f), CornerRadius(6f, 6f), style = InkThin)
    }
}

private fun DrawScope.drawCathedralOrRuins(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    if (lm.id == "bo_ruinas") {
        // Bosque Olvidado: Ruinas Susurrantes mossy arches & purple rune stones
        drawRoundRect(Color(0xFF3E4E4A), Offset(left + 10f, top + 28f), Size(lm.width - 20f, lm.height - 30f), CornerRadius(8f, 8f))
        drawRoundRect(InkBlack, Offset(left + 10f, top + 28f), Size(lm.width - 20f, lm.height - 30f), CornerRadius(8f, 8f), style = InkOutline)
        for (px in listOf(left + 32f, lm.x, left + lm.width - 32f)) {
            drawRect(Color(0xFF5C6B67), Offset(px - 10f, top + 6f), Size(20f, lm.height - 16f))
            drawRect(InkBlack, Offset(px - 10f, top + 6f), Size(20f, lm.height - 16f), style = InkThin)
            drawCircle(Color(0xFFC77DFF), 6f, Offset(px, top + 28f))
        }
        return
    }
    // Artelia Gran Biblioteca: Cream marble facade, columns, rose window & ribbed violet cupola
    drawRoundRect(Color(0xFFF4E7D1), Offset(left, top + 30f), Size(lm.width, lm.height - 30f), CornerRadius(10f, 10f))
    drawRoundRect(InkBlack, Offset(left, top + 30f), Size(lm.width, lm.height - 30f), CornerRadius(10f, 10f), style = InkOutline)
    drawOval(lm.accentColor, Offset(lm.x - lm.width * 0.34f, top - 16f), Size(lm.width * 0.68f, 62f))
    drawOval(InkBlack, Offset(lm.x - lm.width * 0.34f, top - 16f), Size(lm.width * 0.68f, 62f), style = InkOutline)
    drawCircle(SparkGold, 15f, Offset(lm.x, top + 56f))
    drawCircle(InkBlack, 15f, Offset(lm.x, top + 56f), style = InkThin)
    for (c in 0..4) {
        val cx = left + 18f + c * ((lm.width - 36f) / 4f)
        drawRect(Color(0xFFE6D5B8), Offset(cx - 5f, top + 38f), Size(10f, lm.height - 42f))
        drawRect(InkBlack, Offset(cx - 5f, top + 38f), Size(10f, lm.height - 42f), style = InkThin)
    }
    drawCircle(SparkGold.copy(alpha = 0.85f + sin(animPhase * 2f) * 0.15f), 8f, Offset(lm.x, top - 18f))
}

private fun DrawScope.drawDreamPalaceOrCrown(lm: LandmarkSpec, animPhase: Float) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    drawRoundRect(Color(0xFFE6D9FF), Offset(left + 12f, top + 28f), Size(lm.width - 24f, lm.height - 28f), CornerRadius(14f, 14f))
    drawRoundRect(InkBlack, Offset(left + 12f, top + 28f), Size(lm.width - 24f, lm.height - 28f), CornerRadius(14f, 14f), style = InkOutline)
    // Triple celestial domes
    drawOval(lm.accentColor, Offset(lm.x - 42f, top - 14f), Size(84f, 52f))
    drawOval(InkBlack, Offset(lm.x - 42f, top - 14f), Size(84f, 52f), style = InkOutline)
    drawOval(SparkGold, Offset(left + 6f, top + 8f), Size(38f, 34f))
    drawOval(SparkGold, Offset(left + lm.width - 44f, top + 8f), Size(38f, 34f))
    drawCircle(SparkGold, 8f, Offset(lm.x, top - 16f + sin(animPhase * 2f) * 2f))
}

private fun DrawScope.drawTowerMonument(lm: LandmarkSpec, animPhase: Float) {
    val tw = lm.width * 0.56f
    val left = lm.x - tw / 2f
    val top = lm.y - lm.height / 2f
    drawRoundRect(Color(0xFFE6DFD1), Offset(left, top + 14f), Size(tw, lm.height - 14f), CornerRadius(14f, 14f))
    if (lm.kind == LandmarkKind.LIGHTHOUSE) {
        // Spiral lighthouse stripes
        drawRect(lm.accentColor, Offset(left + 4f, top + 50f), Size(tw - 8f, 16f))
        drawRect(lm.accentColor, Offset(left + 4f, top + 86f), Size(tw - 8f, 16f))
    }
    drawRoundRect(InkBlack, Offset(left, top + 14f), Size(tw, lm.height - 14f), CornerRadius(14f, 14f), style = InkOutline)
    drawCircle(lm.accentColor, tw * 0.5f, Offset(lm.x, top + 18f))
    drawCircle(InkBlack, tw * 0.5f, Offset(lm.x, top + 18f), style = InkOutline)

    val beamAngle = sin(animPhase * 0.9f) * 35f
    withTransform({ rotate(beamAngle, Offset(lm.x, top + 18f)) }) {
        val cone = Path().apply {
            moveTo(lm.x, top + 18f)
            lineTo(lm.x + 95f, top - 14f)
            lineTo(lm.x + 95f, top + 50f)
            close()
        }
        drawPath(cone, SparkGold.copy(alpha = 0.24f))
    }
    if (lm.kind == LandmarkKind.BEACON_TOWER) {
        drawSmoothFlame(lm.x, top + 14f, 24f, 34f)
    } else {
        drawCircle(SparkGold, 10f, Offset(lm.x, top + 18f))
    }
}

private fun DrawScope.drawStadiumArena(lm: LandmarkSpec) {
    val left = lm.x - lm.width / 2f
    val top = lm.y - lm.height / 2f
    // Outer Coliseum concrete & blue-yellow stands bowl
    drawOval(Color(0xFFD9C7A3), Offset(left, top), Size(lm.width, lm.height))
    drawOval(InkBlack, Offset(left, top), Size(lm.width, lm.height), style = InkOutline)
    drawOval(Color(0xFF1E88E5), Offset(left + 12f, top + 10f), Size(lm.width - 24f, lm.height - 20f), style = Stroke(12f))
    drawOval(lm.accentColor, Offset(left + 22f, top + 18f), Size(lm.width - 44f, lm.height - 36f), style = Stroke(6f))
    // Inner green soccer pitch inside stadium
    drawOval(Color(0xFF3E8E41), Offset(left + 32f, top + 28f), Size(lm.width - 64f, lm.height - 56f))
    drawLine(Color.White.copy(alpha = 0.75f), Offset(lm.x, top + 30f), Offset(lm.x, top + lm.height - 30f), strokeWidth = 2.4f)
    drawCircle(Color.White.copy(alpha = 0.75f), 18f, Offset(lm.x, lm.y), style = Stroke(2.2f))
    // Four stadium floodlight towers
    for (fx in listOf(left + 22f, left + lm.width - 22f)) {
        for (fy in listOf(top + 22f, top + lm.height - 22f)) {
            drawCircle(SparkGold, 7f, Offset(fx, fy))
            drawCircle(InkBlack, 7f, Offset(fx, fy), style = InkThin)
        }
    }
}
