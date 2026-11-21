package com.example.game.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BoxyLavender
import com.example.ui.theme.DeepTwilight
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.InkBlack
import com.example.ui.theme.SparkGold
import com.example.ui.theme.WarmParchment
import kotlin.math.hypot

@Composable
fun VirtualJoystick(
    onMove: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var knobOffset by remember { mutableStateOf(Offset.Zero) }
    val outerRadiusPx = 115f

    Box(
        modifier = modifier
            .size(116.dp)
            .clip(CircleShape)
            .background(DeepTwilight.copy(alpha = 0.45f))
            .border(2.dp, BoxyLavender.copy(alpha = 0.45f), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val delta = offset - center
                        val dist = hypot(delta.x, delta.y).coerceAtLeast(1f)
                        val clamped = if (dist > outerRadiusPx) delta * (outerRadiusPx / dist) else delta
                        knobOffset = clamped
                        onMove(clamped.x / outerRadiusPx, clamped.y / outerRadiusPx)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val next = knobOffset + dragAmount
                        val dist = hypot(next.x, next.y).coerceAtLeast(1f)
                        val clamped = if (dist > outerRadiusPx) next * (outerRadiusPx / dist) else next
                        knobOffset = clamped
                        onMove(clamped.x / outerRadiusPx, clamped.y / outerRadiusPx)
                    },
                    onDragEnd = {
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    },
                    onDragCancel = {
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                color = WarmParchment.copy(alpha = 0.14f),
                radius = size.minDimension * 0.36f,
                center = center,
                style = Stroke(width = 2f)
            )
            val knobPos = center + knobOffset * 0.48f
            drawCircle(
                color = BoxyLavender.copy(alpha = 0.85f),
                radius = 24f,
                center = knobPos
            )
            drawCircle(
                color = InkBlack,
                radius = 24f,
                center = knobPos,
                style = Stroke(width = 3f)
            )
            drawCircle(
                color = FlameOrange,
                radius = 9f,
                center = knobPos
            )
        }
    }
}

@Composable
fun QuickSlotPill(
    slotNumber: Int,
    itemId: String,
    name: String,
    quantity: Int,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DeepTwilight.copy(alpha = if (quantity > 0) 0.92f else 0.55f),
        border = BorderStroke(
            1.5.dp,
            if (quantity > 0) color.copy(alpha = 0.88f) else Color.Gray.copy(alpha = 0.35f)
        ),
        modifier = modifier
            .size(width = 74.dp, height = 52.dp)
            .clickable(enabled = quantity > 0, onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 5.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxSize().weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ItemVectorBadge(itemId = itemId, accentColor = color, size = 22.dp)
                Text(
                    text = "x$quantity",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (quantity > 0) SparkGold else Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = name.split(" ").firstOrNull() ?: "$slotNumber",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = WarmParchment,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ActionOrbButton(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    contentColor: Color,
    size: Dp,
    testTag: String,
    enabled: Boolean = true,
    highlighted: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = containerColor,
        border = BorderStroke(
            width = if (highlighted) 2.dp else 1.2.dp,
            color = if (highlighted) SparkGold else InkBlack.copy(alpha = 0.7f)
        ),
        shadowElevation = if (highlighted) 6.dp else 2.dp,
        modifier = Modifier
            .size(size)
            .alpha(if (enabled) 1f else 0.45f)
            .testTag(testTag)
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(if (size >= 60.dp) 24.dp else 20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = contentColor,
                maxLines = 1
            )
        }
    }
}
