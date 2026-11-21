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
import com.example.game.model.PropKind
import com.example.game.model.PropSpec
import com.example.ui.theme.InkBlack
import com.example.ui.theme.RunicTeal
import com.example.ui.theme.SparkGold
import com.example.ui.theme.WarmParchment
import kotlin.math.sin

internal fun Color.darken(factor: Float = 0.72f): Color = Color(
    red = (red * factor).coerceIn(0f, 1f),
    green = (green * factor).coerceIn(0f, 1f),
    blue = (blue * factor).coerceIn(0f, 1f),
    alpha = alpha
)

fun DrawScope.drawHouse2D(
    house: HouseSpec,
    animPhase: Float,
    textMeasurer: TextMeasurer? = null
) {
    val left = house.x - house.width / 2f
    val top = house.y - house.height / 2f
    val w = house.width
    val h = house.height
    val depthX = (w * 0.12f).coerceIn(12f, 20f)

    // 1. Soft 2.5D angled ground shadow
    drawOval(
        color = Color.Black.copy(alpha = 0.32f),
        topLeft = Offset(left - 8f, top + h - 12f),
        size = Size(w + depthX + 12f, 26f)
    )

    // 2. Chimney with gentle animated cartoon smoke puffs
    if (house.style != HouseStyle.CORAL_HUT && house.style != HouseStyle.CRYSTAL_PAVILION && house.style != HouseStyle.CLOUD_TEMPLE) {
        val chX = left + w * 0.70f
        val chY = top - h * 0.16f
        drawRect(house.roofColor.darken(0.65f), Offset(chX, chY), Size(16f, 24f))
        drawRect(InkBlack, Offset(chX, chY), Size(16f, 24f), style = InkThin)
        drawRect(house.roofColor.darken(0.85f), Offset(chX - 2f, chY - 3f), Size(20f, 5f))
        drawRect(InkBlack, Offset(chX - 2f, chY - 3f), Size(20f, 5f), style = InkThin)
        val drift = (animPhase * 12f + house.x * 0.1f) % 24f
        drawCircle(
            Color.White.copy(alpha = (0.48f - drift / 52f).coerceIn(0f, 0.48f)),
            radius = 5.5f + drift * 0.2f,
            center = Offset(chX + 8f + sin(animPhase + drift * 0.2f) * 3.5f, chY - 7f - drift)
        )
    }

    // Floating cloud cushion under CLOUD_TEMPLE
    if (house.style == HouseStyle.CLOUD_TEMPLE) {
        val cCol = Color(0xFFF6F2FC)
        drawCircle(cCol, 22f, Offset(left + 16f, top + h - 4f))
        drawCircle(cCol, 26f, Offset(house.x, top + h - 2f))
        drawCircle(cCol, 22f, Offset(left + w - 16f, top + h - 4f))
    }

    // 3. 2.5D Extruded Front + Shaded Right Side Wall
    val wallTop = top + h * 0.34f
    val wallH = h * 0.66f
    val frontW = w - depthX
    val sideWallColor = house.wallColor.darken(0.76f)

    // Right 3D side wall face
    val sideWall = Path().apply {
        moveTo(left + frontW, wallTop + 2f)
        lineTo(left + w + 2f, wallTop - 6f)
        lineTo(left + w + 2f, top + h - 8f)
        lineTo(left + frontW, top + h)
        close()
    }
    drawPath(sideWall, sideWallColor)
    drawPath(sideWall, InkBlack, style = InkOutline)
    // Side window slit
    drawRoundRect(
        color = SparkGold.copy(alpha = 0.65f),
        topLeft = Offset(left + frontW + depthX * 0.25f, wallTop + wallH * 0.26f),
        size = Size(depthX * 0.46f, 16f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Main front facade
    drawRoundRect(
        color = house.wallColor,
        topLeft = Offset(left + 2f, wallTop),
        size = Size(frontW - 2f, wallH),
        cornerRadius = CornerRadius(6f, 6f)
    )
    // Stone plinth skirting along base
    drawRect(
        color = Color(0xFF3E3530).copy(alpha = 0.36f),
        topLeft = Offset(left + 2f, top + h - 13f),
        size = Size(frontW - 2f, 13f)
    )
    drawRoundRect(
        color = InkBlack,
        topLeft = Offset(left + 2f, wallTop),
        size = Size(frontW - 2f, wallH),
        cornerRadius = CornerRadius(6f, 6f),
        style = InkOutline
    )

    // Biome facade architectural flourishes
    when (house.style) {
        HouseStyle.RENAISSANCE_VILLA -> {
            // Classical cream columns flanking entrance
            for (px in listOf(left + 10f, left + frontW - 18f)) {
                drawRect(Color(0xFFE6D5B8), Offset(px, wallTop + 8f), Size(8f, wallH - 18f))
                drawRect(InkBlack, Offset(px, wallTop + 8f), Size(8f, wallH - 18f), style = InkThin)
            }
        }
        HouseStyle.CUBIST_STUDIO -> {
            // Asymmetrical interlocking color panels on front wall
            drawRect(house.roofColor.copy(alpha = 0.35f), Offset(left + 6f, wallTop + 6f), Size(frontW * 0.42f, wallH * 0.45f))
            drawRect(SparkGold.copy(alpha = 0.25f), Offset(left + frontW * 0.56f, wallTop + wallH * 0.42f), Size(frontW * 0.36f, wallH * 0.42f))
        }
        HouseStyle.MOSS_SHRINE -> {
            // Hanging emerald vines on facade
            for (vx in listOf(left + 14f, left + frontW * 0.32f, left + frontW - 18f)) {
                drawLine(Color(0xFF4CC37A), Offset(vx, wallTop + 2f), Offset(vx + 3f, wallTop + 26f), strokeWidth = 4f, cap = StrokeCap.Round)
            }
        }
        else -> Unit
    }

    // 4. Arched Door + Warm Glowing Windows
    val frontCenterX = left + frontW * 0.5f
    val doorW = (frontW * 0.24f).coerceIn(18f, 28f)
    val doorH = (wallH * 0.50f).coerceIn(24f, 36f)
    val doorX = frontCenterX - doorW / 2f
    val doorY = top + h - doorH
    drawRoundRect(Color(0xFF543320), Offset(doorX, doorY), Size(doorW, doorH), CornerRadius(doorW / 2f, doorW / 2f))
    drawRoundRect(InkBlack, Offset(doorX, doorY), Size(doorW, doorH), CornerRadius(doorW / 2f, doorW / 2f), style = InkThin)
    drawCircle(SparkGold, 2.3f, Offset(doorX + doorW - 5f, doorY + doorH * 0.56f))

    val winGlow = 0.82f + sin(animPhase * 1.4f + house.x) * 0.12f
    val winColor = when (house.style) {
        HouseStyle.GOTHIC_HOUSE -> Color(0xFFC77DFF).copy(alpha = winGlow)
        HouseStyle.CORAL_HUT -> Color(0xFF8BE0E6).copy(alpha = winGlow)
        HouseStyle.NEON_LOFT -> Color(0xFFFF3D9A).copy(alpha = winGlow)
        else -> SparkGold.copy(alpha = winGlow)
    }
    listOf(left + frontW * 0.22f, left + frontW * 0.78f).forEach { wx ->
        val wy = wallTop + wallH * 0.28f
        if (house.style == HouseStyle.CORAL_HUT) {
            drawCircle(winColor, 9f, Offset(wx, wy))
            drawCircle(InkBlack, 9f, Offset(wx, wy), style = InkThin)
        } else {
            drawRoundRect(winColor, Offset(wx - 9f, wy - 10f), Size(18f, 20f), CornerRadius(4f, 4f))
            drawRoundRect(InkBlack, Offset(wx - 9f, wy - 10f), Size(18f, 20f), CornerRadius(4f, 4f), style = InkThin)
            drawLine(InkBlack, Offset(wx, wy - 10f), Offset(wx, wy + 10f), strokeWidth = 1.4f)
            drawLine(InkBlack, Offset(wx - 9f, wy), Offset(wx + 9f, wy), strokeWidth = 1.4f)
        }
    }

    // 5. Stylized 2.5D Roofs per HouseStyle
    when (house.style) {
        HouseStyle.RENAISSANCE_VILLA, HouseStyle.CLOUD_TEMPLE -> {
            drawOval(house.roofColor, Offset(left + frontW * 0.14f, top - 10f), Size(frontW * 0.74f, h * 0.52f))
            drawOval(InkBlack, Offset(left + frontW * 0.14f, top - 10f), Size(frontW * 0.74f, h * 0.52f), style = InkOutline)
            drawArc(Color.White.copy(alpha = 0.28f), 200f, 110f, false, Offset(left + frontW * 0.22f, top - 4f), Size(frontW * 0.56f, h * 0.42f), style = Stroke(2.2f))
            drawRoundRect(house.wallColor, Offset(left - 2f, wallTop - 8f), Size(w + 6f, 12f), CornerRadius(4f, 4f))
            drawRoundRect(InkBlack, Offset(left - 2f, wallTop - 8f), Size(w + 6f, 12f), CornerRadius(4f, 4f), style = InkThin)
            drawCircle(SparkGold, 4.8f, Offset(frontCenterX, top - 12f))
        }
        HouseStyle.CORAL_HUT -> {
            val dome = Path().apply {
                moveTo(left - 6f, wallTop + 5f)
                cubicTo(left + w * 0.18f, top - 12f, left + w * 0.84f, top - 12f, left + w + 6f, wallTop + 5f)
                close()
            }
            drawPath(dome, house.roofColor)
            drawPath(dome, InkBlack, style = InkOutline)
            for (rib in listOf(-0.22f, 0f, 0.22f)) {
                drawLine(Color.White.copy(alpha = 0.35f), Offset(house.x, top - 2f), Offset(house.x + w * rib, wallTop + 2f), strokeWidth = 2f)
            }
        }
        HouseStyle.CUBIST_STUDIO -> {
            // Stepped asymmetrical geometric cubist blocks on top
            drawRoundRect(house.roofColor, Offset(left - 2f, top + 6f), Size(frontW * 0.65f, h * 0.32f), CornerRadius(5f, 5f))
            drawRoundRect(InkBlack, Offset(left - 2f, top + 6f), Size(frontW * 0.65f, h * 0.32f), CornerRadius(5f, 5f), style = InkOutline)
            drawRoundRect(SparkGold, Offset(left + frontW * 0.52f, top - 2f), Size(w * 0.48f, h * 0.38f), CornerRadius(5f, 5f))
            drawRoundRect(InkBlack, Offset(left + frontW * 0.52f, top - 2f), Size(w * 0.48f, h * 0.38f), CornerRadius(5f, 5f), style = InkOutline)
        }
        HouseStyle.NEON_LOFT -> {
            drawRoundRect(house.roofColor.darken(0.78f), Offset(left - 4f, top + 4f), Size(w + 8f, h * 0.34f), CornerRadius(6f, 6f))
            drawRoundRect(InkBlack, Offset(left - 4f, top + 4f), Size(w + 8f, h * 0.34f), CornerRadius(6f, 6f), style = InkOutline)
            drawRect(house.roofColor, Offset(left + 6f, top + 12f), Size(w - 12f, 6f))
            drawCircle(SparkGold, 8f, Offset(frontCenterX, top + 4f))
            drawCircle(InkBlack, 8f, Offset(frontCenterX, top + 4f), style = InkThin)
            drawCircle(InkBlack, 2.5f, Offset(frontCenterX, top + 4f))
        }
        HouseStyle.GOTHIC_HOUSE -> {
            val spire = Path().apply {
                moveTo(left - 6f, wallTop + 5f)
                lineTo(frontCenterX, top - 16f)
                lineTo(left + frontW + 6f, wallTop + 5f)
                close()
            }
            drawPath(spire, house.roofColor)
            drawPath(spire, InkBlack, style = InkOutline)
            drawCircle(Color(0xFFE0AAFF), 4.5f, Offset(frontCenterX, top - 18f))
        }
        HouseStyle.CRYSTAL_PAVILION -> {
            val gem = Path().apply {
                moveTo(left - 4f, wallTop + 4f)
                lineTo(frontCenterX - w * 0.16f, top + 4f)
                lineTo(frontCenterX, top - 14f)
                lineTo(frontCenterX + w * 0.16f, top + 4f)
                lineTo(left + w + 4f, wallTop + 4f)
                close()
            }
            drawPath(gem, house.roofColor)
            drawPath(gem, InkBlack, style = InkOutline)
            drawLine(Color.White.copy(alpha = 0.45f), Offset(frontCenterX, top - 12f), Offset(frontCenterX, wallTop), strokeWidth = 2f)
        }
        else -> {
            // 2.5D Extruded Pitched Gable Roof (side roof slope + front gable triangle)
            val sideRoof = Path().apply {
                moveTo(frontCenterX, top - 6f)
                lineTo(left + w + 6f, top - 2f)
                lineTo(left + w + 8f, wallTop + 2f)
                lineTo(left + frontW + 2f, wallTop + 6f)
                close()
            }
            drawPath(sideRoof, house.roofColor.darken(0.75f))
            drawPath(sideRoof, InkBlack, style = InkOutline)

            val frontRoof = Path().apply {
                moveTo(left - 8f, wallTop + 6f)
                lineTo(frontCenterX, top - 6f)
                lineTo(left + frontW + 4f, wallTop + 6f)
                close()
            }
            drawPath(frontRoof, house.roofColor)
            drawPath(frontRoof, InkBlack, style = InkOutline)
        }
    }

    // Striped Café awning across front when CAFE_AWNING
    if (house.style == HouseStyle.CAFE_AWNING) {
        val awTop = wallTop + wallH * 0.42f
        for (i in 0 until 6) {
            val segW = (frontW - 6f) / 6f
            drawRect(
                color = if (i % 2 == 0) house.roofColor else WarmParchment,
                topLeft = Offset(left + 3f + i * segW, awTop),
                size = Size(segW + 0.5f, 12f)
            )
        }
        drawRect(InkBlack, Offset(left + 3f, awTop), Size(frontW - 6f, 12f), style = InkThin)
    }

    if (textMeasurer != null && house.name.isNotBlank()) {
        val m = textMeasurer.measure(
            house.name,
            TextStyle(color = WarmParchment, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
        )
        drawRoundRect(
            color = InkBlack.copy(alpha = 0.66f),
            topLeft = Offset(house.x - m.size.width / 2f - 5f, top + h + 3f),
            size = Size(m.size.width + 10f, m.size.height + 3f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        drawText(m, topLeft = Offset(house.x - m.size.width / 2f, top + h + 4f))
    }
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
                val glowAlpha = 0.25f + sin(animPhase * 2.2f + prop.x) * 0.06f
                drawCircle(
                    brush = Brush.radialGradient(listOf(prop.tint.copy(alpha = glowAlpha), Color.Transparent), Offset(0f, -36f), 50f),
                    radius = 50f,
                    center = Offset(0f, -36f)
                )
                drawOval(Color.Black.copy(alpha = 0.22f), Offset(-7f, 0f), Size(14f, 5f))
                drawRect(Color(0xFF282830), Offset(-2.5f, -34f), Size(5f, 35f))
                drawLine(Color(0xFF282830), Offset(-7f, -26f), Offset(7f, -26f), strokeWidth = 2.4f)
                drawCircle(prop.tint, 7f, Offset(0f, -37f))
                drawCircle(InkBlack, 7f, Offset(0f, -37f), style = InkThin)
            }
            PropKind.BENCH -> {
                drawOval(Color.Black.copy(alpha = 0.24f), Offset(-20f, 0f), Size(40f, 8f))
                drawRoundRect(Color(0xFF7B4B36), Offset(-18f, -15f), Size(36f, 14f), CornerRadius(3f, 3f))
                drawRoundRect(InkBlack, Offset(-18f, -15f), Size(36f, 14f), CornerRadius(3f, 3f), style = InkThin)
                drawLine(Color(0xFF3E2723), Offset(-15f, -8f), Offset(15f, -8f), strokeWidth = 2.2f)
            }
            PropKind.UMBRELLA_TABLE -> {
                drawOval(Color.Black.copy(alpha = 0.24f), Offset(-20f, -2f), Size(40f, 11f))
                drawCircle(WarmParchment, 12f, Offset(0f, -7f))
                drawCircle(InkBlack, 12f, Offset(0f, -7f), style = InkThin)
                // Striped bistro parasol
                drawCircle(prop.tint, 18f, Offset(0f, -24f))
                drawArc(WarmParchment, -40f, 75f, true, Offset(-18f, -42f), Size(36f, 36f))
                drawArc(WarmParchment, 140f, 75f, true, Offset(-18f, -42f), Size(36f, 36f))
                drawCircle(InkBlack, 18f, Offset(0f, -24f), style = InkOutline)
                drawCircle(SparkGold, 2.8f, Offset(0f, -24f))
            }
            PropKind.CRATE_STACK -> {
                drawRect(Color(0xFF9E6B43), Offset(-14f, -17f), Size(17f, 17f))
                drawLine(InkBlack, Offset(-14f, -17f), Offset(3f, 0f), strokeWidth = 1.5f)
                drawRect(InkBlack, Offset(-14f, -17f), Size(17f, 17f), style = InkThin)
                drawRect(Color(0xFFB87F50), Offset(-1f, -13f), Size(15f, 13f))
                drawRect(InkBlack, Offset(-1f, -13f), Size(15f, 13f), style = InkThin)
            }
            PropKind.BANNER -> {
                drawLine(Color(0xFF4E342E), Offset(0f, 4f), Offset(0f, -40f), strokeWidth = 3.2f)
                drawCircle(SparkGold, 3.2f, Offset(0f, -41f))
                val wave = sin(animPhase * 2.5f + prop.x * 0.05f) * 3.5f
                val flag = Path().apply {
                    moveTo(0f, -38f)
                    lineTo(20f, -34f + wave)
                    lineTo(15f, -23f + wave)
                    lineTo(0f, -20f)
                    close()
                }
                drawPath(flag, prop.tint)
                drawPath(flag, InkBlack, style = InkThin)
            }
            PropKind.FENCE_H -> {
                for (px in listOf(-19f, 0f, 19f)) {
                    drawRect(Color(0xFF8D5B4C), Offset(px - 2.5f, -15f), Size(5f, 17f))
                    drawRect(InkBlack, Offset(px - 2.5f, -15f), Size(5f, 17f), style = InkThin)
                }
                drawRect(Color(0xFFA6725B), Offset(-23f, -11f), Size(46f, 4.5f))
                drawRect(InkBlack, Offset(-23f, -11f), Size(46f, 4.5f), style = InkThin)
            }
            PropKind.PILLAR -> {
                drawRoundRect(Color(0xFFE6D5B8), Offset(-9f, -32f), Size(18f, 34f), CornerRadius(3f, 3f))
                drawRoundRect(InkBlack, Offset(-9f, -32f), Size(18f, 34f), CornerRadius(3f, 3f), style = InkThin)
                drawRect(Color(0xFFD6C2A3), Offset(-11f, -35f), Size(22f, 5f))
                drawCircle(prop.tint, 5.5f, Offset(0f, -39f))
            }
            PropKind.SOCCER_GOAL -> {
                for (nx in -20..20 step 8) {
                    drawLine(Color.White.copy(alpha = 0.45f), Offset(nx.toFloat(), -24f), Offset(nx.toFloat(), 0f), strokeWidth = 1.2f)
                }
                drawLine(Color.White.copy(alpha = 0.45f), Offset(-24f, -12f), Offset(24f, -12f), strokeWidth = 1.2f)
                drawLine(Color.White, Offset(-25f, 0f), Offset(-25f, -25f), strokeWidth = 3.8f, cap = StrokeCap.Round)
                drawLine(Color.White, Offset(25f, 0f), Offset(25f, -25f), strokeWidth = 3.8f, cap = StrokeCap.Round)
                drawLine(Color.White, Offset(-25f, -25f), Offset(25f, -25f), strokeWidth = 3.8f, cap = StrokeCap.Round)
            }
            PropKind.WELL -> {
                drawCircle(Color(0xFF7D7482), 15f, Offset(0f, -4f))
                drawCircle(RunicTeal.copy(alpha = 0.85f), 10f, Offset(0f, -4f))
                drawCircle(InkBlack, 15f, Offset(0f, -4f), style = InkOutline)
            }
            PropKind.MUSHROOM_CLUSTER -> {
                drawRect(WarmParchment, Offset(-4.5f, -11f), Size(9f, 13f))
                drawOval(prop.tint, Offset(-12f, -21f), Size(24f, 13f))
                drawOval(InkBlack, Offset(-12f, -21f), Size(24f, 13f), style = InkThin)
                drawCircle(Color.White, 2.6f, Offset(-3.5f, -15f))
                drawCircle(Color.White, 2.1f, Offset(4.5f, -13f))
            }
        }
    }
}
