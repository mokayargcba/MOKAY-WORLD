package com.example.game.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import com.example.game.engine.ActiveEnemy
import com.example.game.model.EnemyVisual
import com.example.game.model.NpcSpec
import com.example.game.model.RegionThemeColors
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.InkBlack
import com.example.ui.theme.SparkGold
import kotlin.math.sin

fun DrawScope.drawEnemyOrBoss(
    enemy: ActiveEnemy,
    phase: Float,
    spriteBank: MokaySpriteBank
) {
    val flash = enemy.hitFlashTimer > 0f
    val bounce = sin(phase * 2.1f) * 2.5f
    val targetH = when (enemy.visual) {
        EnemyVisual.BOSS_STREET_DINO -> 114f
        EnemyVisual.BOSS_LAUGHING_CAT, EnemyVisual.BOSS_DINAMITA, EnemyVisual.BOSS_MOLOTOV -> 98f
        EnemyVisual.BOX_ANTAGONISTA -> 66f
        else -> 52f
    }
    val bottomY = enemy.y + targetH * 0.40f + bounce

    drawOval(
        color = Color.Black.copy(alpha = 0.28f),
        topLeft = Offset(enemy.x - targetH * 0.30f, bottomY - 6f),
        size = Size(targetH * 0.60f, 12f)
    )

    val sprite = spriteBank.enemySprites[enemy.visual]
    if (sprite != null) {
        val hitFilter = if (flash) ColorFilter.tint(Color.White.copy(alpha = 0.85f), BlendMode.SrcAtop) else null
        drawOriginalSprite(
            bitmap = sprite,
            centerX = enemy.x,
            bottomY = bottomY,
            targetHeight = targetH,
            squashX = 1f + sin(phase * 2.1f) * 0.025f,
            squashY = 1f - sin(phase * 2.1f) * 0.025f,
            colorFilter = hitFilter
        )
    } else {
        withTransform({
            translate(enemy.x, enemy.y + bounce)
            scale(1.32f, 1.32f, Offset.Zero)
        }) {
            when (enemy.visual) {
                EnemyVisual.CUBO_REBELDE -> drawCuboRebelde(flash, phase)
                EnemyVisual.SOMBRA_MENOR -> drawSombraMenor(flash, phase)
                EnemyVisual.CHISPA_ERRANTE -> drawChispaErrante(flash, phase)
                else -> Unit
            }
        }
    }

    // Crisp HP bar above enemy/boss
    val barW = if (enemy.isBoss) 78f else 44f
    val ratio = (enemy.hp.toFloat() / enemy.maxHp.toFloat()).coerceIn(0f, 1f)
    val barTop = bottomY - targetH - 12f
    drawRoundRect(
        InkBlack.copy(alpha = 0.85f),
        Offset(enemy.x - barW / 2f, barTop),
        Size(barW, 7f),
        CornerRadius(3.5f, 3.5f)
    )
    drawRoundRect(
        if (enemy.isBoss) FlameOrange else Color(0xFFD94E4E),
        Offset(enemy.x - barW / 2f + 1f, barTop + 1f),
        Size((barW - 2f) * ratio, 5f),
        CornerRadius(2.5f, 2.5f)
    )
}

fun DrawScope.drawNpcCharacter(
    npc: NpcSpec,
    regionTheme: RegionThemeColors,
    phase: Float,
    spriteBank: MokaySpriteBank
) {
    val bounce = sin(phase * 1.6f + npc.x * 0.02f) * 2.0f
    val targetH = if (npc.visual.isNamedOriginal) 70f else 66f
    val bottomY = npc.y + targetH * 0.40f + bounce

    // District badge ring under non-unique regional citizens so district colors are visible without altering original art
    if (!npc.visual.isNamedOriginal) {
        drawOval(
            color = regionTheme.citizenOutfit.copy(alpha = 0.42f),
            topLeft = Offset(npc.x - 24f, bottomY - 9f),
            size = Size(48f, 14f)
        )
        drawOval(
            color = regionTheme.accentColor,
            topLeft = Offset(npc.x - 24f, bottomY - 9f),
            size = Size(48f, 14f),
            style = Stroke(2f)
        )
    }

    drawOval(
        color = Color.Black.copy(alpha = 0.25f),
        topLeft = Offset(npc.x - 20f, bottomY - 6f),
        size = Size(40f, 11f)
    )

    val sprite = spriteBank.npcSprites[npc.visual]
    if (sprite != null) {
        drawOriginalSprite(
            bitmap = sprite,
            centerX = npc.x,
            bottomY = bottomY,
            targetHeight = targetH,
            squashX = 1f + sin(phase * 1.6f + npc.x * 0.02f) * 0.02f,
            squashY = 1f - sin(phase * 1.6f + npc.x * 0.02f) * 0.02f
        )
    }
}

private fun DrawScope.drawCuboRebelde(flash: Boolean, phase: Float) {
    withTransform({ rotate(sin(phase * 2f) * 14f) }) {
        drawRoundRect(if (flash) Color.White else SparkGold, Offset(-13f, -13f), Size(26f, 26f), CornerRadius(5f, 5f))
        drawRoundRect(InkBlack, Offset(-13f, -13f), Size(26f, 26f), CornerRadius(5f, 5f), style = InkOutline)
        drawCircle(Color.White, 3.2f, Offset(-4.5f, -2.5f)); drawCircle(Color.White, 3.2f, Offset(4.5f, -2.5f))
        drawCircle(InkBlack, 1.7f, Offset(-4.5f, -2.5f)); drawCircle(InkBlack, 1.7f, Offset(4.5f, -2.5f))
    }
}

private fun DrawScope.drawSombraMenor(flash: Boolean, phase: Float) {
    val c = if (flash) Color.White else Color(0xFF3C096C)
    val dy = sin(phase * 3f) * 2.5f
    val blob = Path().apply {
        moveTo(0f, -18f + dy)
        cubicTo(14f, -18f + dy, 16f, 0f + dy, 12f, 10f + dy)
        quadraticTo(6f, 14f + dy, 0f, 9f + dy)
        quadraticTo(-6f, 14f + dy, -12f, 10f + dy)
        cubicTo(-16f, 0f + dy, -14f, -18f + dy, 0f, -18f + dy)
        close()
    }
    drawPath(blob, c)
    drawPath(blob, InkBlack, style = InkOutline)
    drawCircle(Color(0xFFE0AAFF), 3.1f, Offset(-4.5f, -4f + dy))
    drawCircle(Color(0xFFE0AAFF), 3.1f, Offset(4.5f, -4f + dy))
}

private fun DrawScope.drawChispaErrante(flash: Boolean, phase: Float) {
    drawSmoothFlame(0f, 10f + sin(phase * 4f) * 1.4f, 24f, 26f)
    if (flash) drawCircle(Color.White, 11f, Offset(0f, -2f))
    drawCircle(Color.White, 2.5f, Offset(-3.5f, -3f)); drawCircle(Color.White, 2.5f, Offset(3.5f, -3f))
    drawCircle(InkBlack, 1.3f, Offset(-3.5f, -3f)); drawCircle(InkBlack, 1.3f, Offset(3.5f, -3f))
}
