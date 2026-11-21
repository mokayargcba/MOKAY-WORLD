package com.example.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.InkBlack
import com.example.ui.theme.SparkGold

@Composable
fun ItemVectorBadge(
    itemId: String,
    accentColor: Color,
    size: Dp = 32.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f

            drawCircle(accentColor.copy(alpha = 0.22f), radius = w * 0.48f, center = Offset(cx, cy))
            drawCircle(accentColor.copy(alpha = 0.72f), radius = w * 0.46f, center = Offset(cx, cy), style = Stroke(1.6f))

            when (itemId) {
                "slice_pizza" -> {
                    val crust = Path().apply {
                        moveTo(cx - w * 0.26f, cy - h * 0.18f)
                        lineTo(cx + w * 0.28f, cy - h * 0.08f)
                        lineTo(cx - w * 0.12f, cy + h * 0.28f)
                        close()
                    }
                    drawPath(crust, Color(0xFFFFC24D))
                    drawPath(crust, InkBlack, style = Stroke(1.5f))
                    drawCircle(Color(0xFFD94E4E), w * 0.055f, Offset(cx - w * 0.04f, cy - h * 0.02f))
                    drawCircle(Color(0xFFD94E4E), w * 0.045f, Offset(cx + w * 0.08f, cy - h * 0.04f))
                }
                "cafe_bw", "te_zen" -> {
                    drawRoundRect(
                        color = if (itemId == "cafe_bw") Color(0xFFF5EFE6) else Color(0xFF4CC37A),
                        topLeft = Offset(cx - w * 0.22f, cy - h * 0.14f),
                        size = Size(w * 0.40f, h * 0.34f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                    drawRoundRect(
                        color = InkBlack,
                        topLeft = Offset(cx - w * 0.22f, cy - h * 0.14f),
                        size = Size(w * 0.40f, h * 0.34f),
                        cornerRadius = CornerRadius(4f, 4f),
                        style = Stroke(1.5f)
                    )
                    drawArc(
                        color = InkBlack,
                        startAngle = -75f,
                        sweepAngle = 150f,
                        useCenter = false,
                        topLeft = Offset(cx + w * 0.10f, cy - h * 0.09f),
                        size = Size(w * 0.16f, h * 0.18f),
                        style = Stroke(1.8f)
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.8f),
                        start = Offset(cx - w * 0.06f, cy - h * 0.22f),
                        end = Offset(cx - w * 0.02f, cy - h * 0.30f),
                        strokeWidth = 1.6f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.8f),
                        start = Offset(cx + w * 0.06f, cy - h * 0.22f),
                        end = Offset(cx + w * 0.10f, cy - h * 0.30f),
                        strokeWidth = 1.6f,
                        cap = StrokeCap.Round
                    )
                }
                "elixir_chispa", "esencia_cristal" -> {
                    drawRoundRect(
                        Color(0xFFD9C7A3),
                        Offset(cx - w * 0.08f, cy - h * 0.30f),
                        Size(w * 0.16f, h * 0.12f),
                        CornerRadius(2f, 2f)
                    )
                    drawCircle(accentColor, w * 0.24f, Offset(cx, cy + h * 0.06f))
                    drawCircle(Color.White.copy(alpha = 0.55f), w * 0.06f, Offset(cx - w * 0.08f, cy - h * 0.02f))
                    drawCircle(InkBlack, w * 0.24f, Offset(cx, cy + h * 0.06f), style = Stroke(1.6f))
                }
                "fruto_creacion" -> {
                    drawCircle(Color(0xFFE97893), w * 0.23f, Offset(cx, cy + h * 0.05f))
                    drawCircle(InkBlack, w * 0.23f, Offset(cx, cy + h * 0.05f), style = Stroke(1.5f))
                    drawOval(
                        color = Color(0xFF4CC37A),
                        topLeft = Offset(cx - w * 0.04f, cy - h * 0.30f),
                        size = Size(w * 0.18f, h * 0.12f)
                    )
                }
                "llave_tinta", "perla_coral", "vinilo_chispa", "prisma_cubista", "corona_superacion" -> {
                    val star = Path().apply {
                        moveTo(cx, cy - h * 0.28f)
                        lineTo(cx + w * 0.12f, cy - h * 0.07f)
                        lineTo(cx + w * 0.28f, cy)
                        lineTo(cx + w * 0.12f, cy + h * 0.07f)
                        lineTo(cx, cy + h * 0.28f)
                        lineTo(cx - w * 0.12f, cy + h * 0.07f)
                        lineTo(cx - w * 0.28f, cy)
                        lineTo(cx - w * 0.12f, cy - h * 0.07f)
                        close()
                    }
                    drawPath(star, SparkGold)
                    drawPath(star, InkBlack, style = Stroke(1.5f))
                }
                else -> {
                    val diamond = Path().apply {
                        moveTo(cx, cy - h * 0.26f)
                        lineTo(cx + w * 0.22f, cy)
                        lineTo(cx, cy + h * 0.26f)
                        lineTo(cx - w * 0.22f, cy)
                        close()
                    }
                    drawPath(diamond, accentColor)
                    drawPath(diamond, InkBlack, style = Stroke(1.5f))
                }
            }
        }
    }
}
