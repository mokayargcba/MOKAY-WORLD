package com.example.game.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.game.model.Direction
import com.example.game.model.FloraKind
import com.example.game.model.LivingFloraSpec
import com.example.game.model.PlayerAnimState
import com.example.ui.theme.BoxyLavender
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.InkBlack
import com.example.ui.theme.SparkGold
import com.example.ui.theme.WarmParchment
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

internal val InkOutline = Stroke(width = 2.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
internal val InkThin = Stroke(width = 1.6f, cap = StrokeCap.Round, join = StrokeJoin.Round)

internal fun DrawScope.drawOriginalSprite(
    bitmap: ImageBitmap,
    centerX: Float,
    bottomY: Float,
    targetHeight: Float,
    flipX: Boolean = false,
    squashX: Float = 1f,
    squashY: Float = 1f,
    rotateDeg: Float = 0f,
    colorFilter: ColorFilter? = null,
    alpha: Float = 1f
) {
    val aspect = bitmap.width.toFloat() / bitmap.height.toFloat().coerceAtLeast(1f)
    val drawH = targetHeight
    val drawW = targetHeight * aspect
    withTransform({
        translate(centerX, bottomY)
        rotate(rotateDeg, pivot = Offset(0f, -drawH * 0.45f))
        scale(
            scaleX = (if (flipX) -1f else 1f) * squashX,
            scaleY = squashY,
            pivot = Offset.Zero
        )
    }) {
        drawImage(
            image = bitmap,
            srcOffset = IntOffset.Zero,
            srcSize = IntSize(bitmap.width, bitmap.height),
            dstOffset = IntOffset((-drawW / 2f).roundToInt(), (-drawH).roundToInt()),
            dstSize = IntSize(drawW.roundToInt().coerceAtLeast(1), drawH.roundToInt().coerceAtLeast(1)),
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = FilterQuality.High
        )
    }
}

fun DrawScope.drawBoxyCharacter(
    x: Float,
    y: Float,
    direction: Direction,
    animState: PlayerAnimState,
    phase: Float,
    actionProgress: Float,
    scale: Float = 1.0f,
    invulnerableBlink: Boolean = false,
    spriteBank: MokaySpriteBank? = null,
    useHeroPose: Boolean = false,
    foskayLightActive: Boolean = false,
    foskayEmergence: Float = 0f
) {
    if (invulnerableBlink && ((phase * 4f).toInt() % 2 == 0)) return

    // actionTimer counts down 1f -> 0f, so t goes 0f -> 1f across ATTACK/DODGE/FOSKAY/CONSUME
    val actionT = (1f - actionProgress).coerceIn(0f, 1f)
    val stridePhase = phase * 2.25f

    // Kinetic lunge & squash-stretch curves for expressive Cuphead-style animation
    val attackLunge = if (animState == PlayerAnimState.ATTACK) {
        val curve = if (actionT < 0.25f) -actionT * 20f else sin(((actionT - 0.25f) / 0.75f) * PI).toFloat() * 14f
        curve * scale
    } else 0f

    val dirSignX = when (direction) {
        Direction.LEFT -> -1f
        Direction.RIGHT -> 1f
        else -> 0f
    }
    val dirSignY = when (direction) {
        Direction.BACK -> -1f
        Direction.FRONT -> 1f
        else -> 0f
    }
    val drawX = x + dirSignX * attackLunge
    val groundY = y + 30f * scale + dirSignY * (attackLunge * 0.45f)

    val bounce = when (animState) {
        PlayerAnimState.MOVE -> -abs(sin(stridePhase)) * 5.8f
        PlayerAnimState.FATIGUED -> sin(phase * 0.85f) * 2.0f + 1.5f
        PlayerAnimState.DODGE -> -6.5f
        PlayerAnimState.ATTACK -> if (actionT < 0.25f) 2.5f else -4.5f * sin(actionT * PI).toFloat()
        PlayerAnimState.FOSKAY_FIRE, PlayerAnimState.FOSKAY_LIGHT -> -3.5f + sin(phase * 3f) * 1.6f
        else -> sin(phase * 0.9f) * 1.4f
    }

    val squashX = when (animState) {
        PlayerAnimState.DODGE -> 1.24f
        PlayerAnimState.FATIGUED -> 1.08f
        PlayerAnimState.ATTACK -> if (actionT < 0.25f) 1.14f else 0.94f + sin(actionT * PI).toFloat() * 0.14f
        PlayerAnimState.MOVE -> 1f + cos(stridePhase * 2f) * 0.045f
        else -> 1f + sin(phase * 0.9f) * 0.025f
    }
    val squashY = when (animState) {
        PlayerAnimState.DODGE -> 0.80f
        PlayerAnimState.FATIGUED -> 0.91f
        PlayerAnimState.ATTACK -> if (actionT < 0.25f) 0.86f else 1.07f - sin(actionT * PI).toFloat() * 0.07f
        PlayerAnimState.MOVE -> 1f - cos(stridePhase * 2f) * 0.045f
        else -> 1f - sin(phase * 0.9f) * 0.025f
    }
    val leanDeg = when (animState) {
        PlayerAnimState.FATIGUED -> 7.5f
        PlayerAnimState.DODGE -> if (direction == Direction.LEFT) -18f else 18f
        PlayerAnimState.ATTACK -> when (direction) {
            Direction.LEFT -> -11f
            Direction.RIGHT -> 11f
            else -> if (actionT < 0.25f) -4f else 6f
        }
        PlayerAnimState.MOVE -> when (direction) {
            Direction.LEFT -> -7.5f + sin(stridePhase) * 2.5f
            Direction.RIGHT -> 7.5f + sin(stridePhase) * 2.5f
            else -> sin(stridePhase) * 3.8f
        }
        else -> sin(phase * 0.7f) * 1.2f
    }

    // Ground shadow scales subtly when Boxy hops
    val shadowScale = (1f - abs(bounce) * 0.022f).coerceIn(0.76f, 1.08f)
    drawOval(
        color = Color.Black.copy(alpha = 0.30f),
        topLeft = Offset(drawX - 24f * scale * shadowScale, groundY - 6f * scale),
        size = Size(48f * scale * shadowScale, 12.5f * scale)
    )

    // Animated cartoon dust puffs behind Boxy's footsteps during MOVE or DODGE
    if (animState == PlayerAnimState.MOVE || animState == PlayerAnimState.DODGE) {
        val dustAlpha = if (animState == PlayerAnimState.DODGE) 0.45f else 0.28f
        for (p in 0..2) {
            val puffAge = ((phase * 0.75f + p * 0.33f) % 1f)
            val px = drawX - dirSignX * (14f + puffAge * 26f) * scale + (p - 1) * 7f * scale
            val py = groundY - puffAge * 10f * scale - 2f * scale
            drawCircle(
                color = WarmParchment.copy(alpha = dustAlpha * (1f - puffAge)),
                radius = (3.5f + puffAge * 4.5f) * scale,
                center = Offset(px, py)
            )
        }
    }

    if (spriteBank == null) return

    // Hero pose mode (used on showcase / auth card with full original hero art)
    if (useHeroPose) {
        if (foskayEmergence > 0.01f) {
            drawFoskayInsideBox(
                x = drawX,
                drawerRimY = groundY - 56f * scale - foskayEmergence * 32f * scale,
                phase = phase,
                scale = scale,
                isHero = true,
                spriteBank = spriteBank
            )
        }
        drawOriginalSprite(
            bitmap = spriteBank.boxyHero,
            centerX = drawX,
            bottomY = groundY + bounce * scale,
            targetHeight = 82f * scale,
            flipX = direction == Direction.LEFT,
            squashX = squashX,
            squashY = squashY,
            rotateDeg = leanDeg
        )
        return
    }

    // Correct sprite orientation:
    // sprite_boxy_34 faces LEFT -> flip for RIGHT
    // sprite_boxy_profile faces RIGHT -> flip for LEFT
    // sprite_boxy_hero faces RIGHT -> flip for LEFT
    val isSideMove = (direction == Direction.LEFT || direction == Direction.RIGHT) &&
        (animState == PlayerAnimState.MOVE || animState == PlayerAnimState.DODGE)
    val torsoBmp = spriteBank.boxyTorsoForDirection(direction, useSideProfile = isSideMove)
    val flipTorso = when (direction) {
        Direction.LEFT -> isSideMove // 3/4 already faces LEFT; profile faces RIGHT so flip on LEFT
        Direction.RIGHT -> !isSideMove // 3/4 needs flip on RIGHT; profile already faces RIGHT
        else -> false
    }

    val torsoHeight = 60f * scale
    val legLength = 15.5f * scale
    val hipBaseY = groundY - legLength + bounce * scale
    val torsoAspect = torsoBmp.width.toFloat() / torsoBmp.height.toFloat().coerceAtLeast(1f)
    val torsoW = torsoHeight * torsoAspect
    val hipColorFilter = if (animState == PlayerAnimState.FATIGUED) {
        ColorFilter.tint(Color(0xFF7B8FA6).copy(alpha = 0.24f), BlendMode.SrcAtop)
    } else null

    // 1. Dodge motion-blur afterimage ghosts trailing behind Boxy
    if (animState == PlayerAnimState.DODGE) {
        listOf(0.24f to 24f, 0.14f to 44f).forEach { (alphaGhost, lag) ->
            drawOriginalSprite(
                bitmap = torsoBmp,
                centerX = drawX - dirSignX * lag * scale,
                bottomY = hipBaseY,
                targetHeight = torsoHeight,
                flipX = flipTorso,
                squashX = squashX,
                squashY = squashY,
                rotateDeg = leanDeg,
                colorFilter = ColorFilter.tint(BoxyLavender.copy(alpha = 0.5f), BlendMode.SrcAtop),
                alpha = alphaGhost
            )
        }
    }

    // 2. Warm golden glow + Foskay popping straight out from INSIDE Boxy's top open drawer
    if (foskayLightActive) {
        drawCircle(
            brush = Brush.radialGradient(
                listOf(SparkGold.copy(alpha = 0.56f), FlameOrange.copy(alpha = 0.24f), Color.Transparent),
                center = Offset(drawX, hipBaseY - torsoHeight * 0.85f),
                radius = 56f * scale
            ),
            radius = 56f * scale,
            center = Offset(drawX, hipBaseY - torsoHeight * 0.85f)
        )
    }
    if (foskayEmergence > 0.01f) {
        val popLift = foskayEmergence * 36f * scale
        drawFoskayInsideBox(
            x = drawX,
            drawerRimY = hipBaseY - torsoHeight * 0.82f - popLift,
            phase = phase,
            scale = scale,
            isHero = animState == PlayerAnimState.FOSKAY_FIRE,
            spriteBank = spriteBank
        )
    }

    // 3. Articulated rubber-hose legs + authentic original lilac shoes (animated stride!)
    drawBoxyAnimatedLegsAndShoes(
        centerX = drawX,
        hipY = hipBaseY - 2f * scale,
        groundY = groundY,
        direction = direction,
        animState = animState,
        stridePhase = stridePhase,
        actionT = actionT,
        scale = scale,
        spriteBank = spriteBank
    )

    // 4. Original Matchbox Torso + Matches + Arms
    drawOriginalSprite(
        bitmap = torsoBmp,
        centerX = drawX,
        bottomY = hipBaseY,
        targetHeight = torsoHeight,
        flipX = flipTorso,
        squashX = squashX,
        squashY = squashY,
        rotateDeg = leanDeg,
        colorFilter = hipColorFilter
    )

    // 5. Dynamic Original Expression Swapping on FRONT (and subtle expressive overlay)
    if (direction == Direction.FRONT) {
        val activeExpr = pickBoxyExpression(animState, phase)
        if (activeExpr != null) {
            val faceBmp = spriteBank.boxyFaces[activeExpr]
            if (faceBmp != null) {
                // Match exact face placement on sprite_boxy_torso_front (ratio of 164x172)
                val scaledW = torsoW * squashX
                val scaledH = torsoHeight * squashY
                val torsoLeft = drawX - scaledW * 0.5f
                val torsoTop = hipBaseY - scaledH
                val faceLeft = torsoLeft + scaledW * 0.356f
                val faceTop = torsoTop + scaledH * 0.304f
                val faceW = scaledW * 0.432f
                val faceH = scaledH * 0.452f
                withTransform({
                    rotate(leanDeg, pivot = Offset(drawX, hipBaseY - scaledH * 0.45f))
                }) {
                    drawRoundRect(
                        color = Color(0xFF9C90CE),
                        topLeft = Offset(faceLeft, faceTop),
                        size = Size(faceW, faceH),
                        cornerRadius = CornerRadius(4f * scale, 4f * scale)
                    )
                    drawImage(
                        image = faceBmp,
                        srcOffset = IntOffset.Zero,
                        srcSize = IntSize(faceBmp.width, faceBmp.height),
                        dstOffset = IntOffset(faceLeft.roundToInt(), faceTop.roundToInt()),
                        dstSize = IntSize(faceW.roundToInt().coerceAtLeast(1), faceH.roundToInt().coerceAtLeast(1)),
                        filterQuality = FilterQuality.High
                    )
                }
            }
        }
    }

    // 6. Status props (Consumable / Sweat drops) & Attack Spark Slash Arc
    withTransform({
        translate(drawX, hipBaseY - torsoHeight * 0.5f)
        scale(scale, scale, Offset.Zero)
    }) {
        when (animState) {
            PlayerAnimState.EAT -> {
                val tri = Path().apply {
                    moveTo(22f, -12f); lineTo(34f, -5f); lineTo(25f, 3f); close()
                }
                drawPath(tri, SparkGold)
                drawPath(tri, InkBlack, style = InkThin)
            }
            PlayerAnimState.DRINK -> {
                drawRoundRect(Color(0xFF6F4E37), Offset(20f, -16f), Size(11f, 13f), CornerRadius(3f, 3f))
                drawRoundRect(WarmParchment, Offset(19f, -19f), Size(13f, 3.5f), CornerRadius(1.5f, 1.5f))
                drawRoundRect(InkBlack, Offset(20f, -16f), Size(11f, 13f), CornerRadius(3f, 3f), style = InkThin)
            }
            PlayerAnimState.POTION -> {
                drawCircle(Color(0xFF6C63FF), 6.5f, Offset(26f, -10f))
                drawCircle(InkBlack, 6.5f, Offset(26f, -10f), style = InkThin)
                drawCircle(Color.White.copy(alpha = 0.75f), 1.8f, Offset(24f, -12f))
            }
            PlayerAnimState.FATIGUED -> {
                val dropY = -28f + ((phase * 8f) % 10f)
                drawDrop(Offset(-22f, dropY), 3.2f, Color(0xFF6FD0E8))
                drawDrop(Offset(23f, dropY - 4f), 2.7f, Color(0xFF6FD0E8))
            }
            else -> Unit
        }
    }

    if (animState == PlayerAnimState.ATTACK) {
        val slashCenterY = y + 2f * scale
        val slashAngle = when (direction) {
            Direction.RIGHT -> -52f + actionT * 24f
            Direction.LEFT -> 128f + actionT * 24f
            Direction.BACK -> -140f + actionT * 24f
            Direction.FRONT -> 38f + actionT * 24f
        }
        val sweep = 130f * sin(max(0.05f, actionT) * PI).toFloat().coerceIn(0.35f, 1f)
        drawArc(
            brush = Brush.radialGradient(
                listOf(Color.White, SparkGold, FlameOrange, Color.Transparent),
                center = Offset(drawX, slashCenterY),
                radius = 74f * scale
            ),
            startAngle = slashAngle,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = Offset(drawX - 54f * scale, slashCenterY - 54f * scale),
            size = Size(108f * scale, 108f * scale),
            style = Stroke(width = 9.5f * scale, cap = StrokeCap.Round)
        )
        drawArc(
            color = Color.White.copy(alpha = (1f - actionT) * 0.75f),
            startAngle = slashAngle + 14f,
            sweepAngle = sweep * 0.7f,
            useCenter = false,
            topLeft = Offset(drawX - 46f * scale, slashCenterY - 46f * scale),
            size = Size(92f * scale, 92f * scale),
            style = Stroke(width = 3.2f * scale, cap = StrokeCap.Round)
        )
    }
}

private fun pickBoxyExpression(animState: PlayerAnimState, phase: Float): BoxyFaceExpr? = when (animState) {
    PlayerAnimState.ATTACK -> BoxyFaceExpr.ANGRY
    PlayerAnimState.DODGE -> BoxyFaceExpr.SURPRISED
    PlayerAnimState.FATIGUED -> BoxyFaceExpr.TIRED
    PlayerAnimState.FOSKAY_FIRE, PlayerAnimState.FOSKAY_LIGHT -> BoxyFaceExpr.EXCITED
    PlayerAnimState.EAT, PlayerAnimState.DRINK, PlayerAnimState.POTION -> BoxyFaceExpr.WINK
    PlayerAnimState.READ_BOOK -> BoxyFaceExpr.HAPPY
    PlayerAnimState.MOVE -> BoxyFaceExpr.DETERMINED
    PlayerAnimState.IDLE -> {
        val cycle = (phase % 22f)
        when {
            cycle in 10f..12.2f -> BoxyFaceExpr.LAUGH
            cycle in 19.5f..21.2f -> BoxyFaceExpr.WINK
            else -> null // Uses native smiling sprite_boxy_torso_front face
        }
    }
}

private fun DrawScope.drawBoxyAnimatedLegsAndShoes(
    centerX: Float,
    hipY: Float,
    groundY: Float,
    direction: Direction,
    animState: PlayerAnimState,
    stridePhase: Float,
    actionT: Float,
    scale: Float,
    spriteBank: MokaySpriteBank
) {
    val isSide = direction == Direction.LEFT || direction == Direction.RIGHT
    val shoeBmp = if (isSide) spriteBank.boxyShoeSide else spriteBank.boxyShoeFront
    val shoeH = 11.5f * scale
    val stanceSpread = when (animState) {
        PlayerAnimState.ATTACK, PlayerAnimState.FOSKAY_FIRE -> 12.5f * scale
        PlayerAnimState.DODGE -> 14f * scale
        else -> 9.2f * scale
    }

    val leftOffset: Offset
    val rightOffset: Offset
    val leftTilt: Float
    val rightTilt: Float

    when (animState) {
        PlayerAnimState.MOVE -> {
            val lWave = sin(stridePhase)
            val rWave = sin(stridePhase + PI.toFloat())
            if (isSide) {
                val strideReach = 12.5f * scale
                leftOffset = Offset(centerX + lWave * strideReach, groundY - max(0f, cos(stridePhase)) * 8.5f * scale)
                rightOffset = Offset(centerX + rWave * strideReach, groundY - max(0f, cos(stridePhase + PI.toFloat())) * 8.5f * scale)
                leftTilt = lWave * 16f
                rightTilt = rWave * 16f
            } else {
                leftOffset = Offset(centerX - stanceSpread, groundY - max(0f, lWave) * 7.5f * scale)
                rightOffset = Offset(centerX + stanceSpread, groundY - max(0f, rWave) * 7.5f * scale)
                leftTilt = -max(0f, lWave) * 9f
                rightTilt = max(0f, rWave) * 9f
            }
        }
        PlayerAnimState.DODGE -> {
            val lagDir = if (direction == Direction.LEFT) 1f else -1f
            leftOffset = Offset(centerX - stanceSpread * 0.6f + lagDir * 7f * scale, groundY - 6f * scale)
            rightOffset = Offset(centerX + stanceSpread * 0.6f + lagDir * 2f * scale, groundY - 3f * scale)
            leftTilt = lagDir * 22f
            rightTilt = lagDir * 14f
        }
        PlayerAnimState.ATTACK -> {
            val punchShift = sin(actionT * PI).toFloat() * 6f * scale
            leftOffset = Offset(centerX - stanceSpread - punchShift * 0.4f, groundY)
            rightOffset = Offset(centerX + stanceSpread + punchShift * 0.4f, groundY)
            leftTilt = -8f
            rightTilt = 8f
        }
        else -> {
            leftOffset = Offset(centerX - (if (isSide) 3f * scale else stanceSpread), groundY)
            rightOffset = Offset(centerX + (if (isSide) 3f * scale else stanceSpread), groundY)
            leftTilt = 0f
            rightTilt = 0f
        }
    }

    val hipLeftX = centerX - (if (isSide) 2.5f * scale else 7.5f * scale)
    val hipRightX = centerX + (if (isSide) 2.5f * scale else 7.5f * scale)
    val legStroke = Stroke(width = 4.6f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)

    // Rubber-hose black legs bending smoothly at knees
    val leftKneeX = (hipLeftX + leftOffset.x) * 0.5f - (if (direction == Direction.RIGHT) 2f else -2f) * scale
    val leftLeg = Path().apply {
        moveTo(hipLeftX, hipY)
        quadraticTo(leftKneeX, (hipY + leftOffset.y) * 0.5f, leftOffset.x, leftOffset.y - shoeH * 0.45f)
    }
    val rightKneeX = (hipRightX + rightOffset.x) * 0.5f + (if (direction == Direction.LEFT) -2f else 2f) * scale
    val rightLeg = Path().apply {
        moveTo(hipRightX, hipY)
        quadraticTo(rightKneeX, (hipY + rightOffset.y) * 0.5f, rightOffset.x, rightOffset.y - shoeH * 0.45f)
    }
    drawPath(leftLeg, Color(0xFF141215), style = legStroke)
    drawPath(rightLeg, Color(0xFF141215), style = legStroke)

    // Left shoe (mirrored front shoe; side shoe points in movement direction)
    drawOriginalSprite(
        bitmap = shoeBmp,
        centerX = leftOffset.x,
        bottomY = leftOffset.y,
        targetHeight = shoeH,
        flipX = if (isSide) direction == Direction.LEFT else true,
        rotateDeg = leftTilt
    )
    // Right shoe
    drawOriginalSprite(
        bitmap = shoeBmp,
        centerX = rightOffset.x,
        bottomY = rightOffset.y,
        targetHeight = shoeH,
        flipX = if (isSide) direction == Direction.LEFT else false,
        rotateDeg = rightTilt
    )
}

fun DrawScope.drawFoskayInsideBox(
    x: Float,
    drawerRimY: Float,
    phase: Float,
    scale: Float = 1.0f,
    isHero: Boolean = false,
    spriteBank: MokaySpriteBank
) {
    val bob = sin(phase * 2.8f) * 2.2f * scale
    val flameRadius = 38f * scale
    drawCircle(
        brush = Brush.radialGradient(
            listOf(SparkGold.copy(alpha = 0.65f), FlameOrange.copy(alpha = 0.25f), Color.Transparent),
            center = Offset(x, drawerRimY - 20f * scale + bob),
            radius = flameRadius
        ),
        radius = flameRadius,
        center = Offset(x, drawerRimY - 20f * scale + bob)
    )
    val bmp = if (isHero) spriteBank.foskayHero else spriteBank.foskayFront
    drawOriginalSprite(
        bitmap = bmp,
        centerX = x,
        bottomY = drawerRimY + 12f * scale + bob,
        targetHeight = if (isHero) 56f * scale else 52f * scale,
        squashX = 1f + sin(phase * 3.4f) * 0.03f,
        squashY = 1f - sin(phase * 3.4f) * 0.03f
    )
}

fun DrawScope.drawLivingFlora(
    flora: LivingFloraSpec,
    phase: Float,
    wiggle: Float
) {
    val p = phase + flora.phaseOffset
    when (flora.kind) {
        FloraKind.TREE -> drawCupheadTree(flora.x, flora.y, flora.scale, flora.tint, p, wiggle)
        FloraKind.CUPHEAD_FLOWER -> drawCupheadFlower(flora.x, flora.y, flora.scale, flora.tint, p, wiggle)
        FloraKind.SHRUB, FloraKind.INK_FERN -> drawCupheadShrub(flora.x, flora.y, flora.scale, flora.tint, p, wiggle)
        FloraKind.CORAL, FloraKind.CRYSTAL_ROOT -> drawCupheadCoralOrCrystal(flora.x, flora.y, flora.scale, flora.tint, p, wiggle)
    }
}

fun DrawScope.drawCupheadFlower(
    x: Float,
    y: Float,
    scale: Float,
    petalColor: Color,
    phase: Float,
    wiggle: Float
) {
    val sway = sin(phase * 1.8f) * 6.5f + sin(wiggle * 26f) * wiggle * 18f
    val bounceY = sin(phase * 3.6f) * 2f
    withTransform({
        translate(x, y)
        scale(scale, scale, Offset.Zero)
    }) {
        drawOval(Color.Black.copy(alpha = 0.2f), Offset(-11f, -3f), Size(22f, 7f))
        val stem = Path().apply {
            moveTo(0f, 0f)
            quadraticTo(sway * 0.6f, -11f, sway * 0.85f, -23f + bounceY)
        }
        drawPath(stem, Color(0xFF2E8B57), style = Stroke(3.6f, cap = StrokeCap.Round))
        val headX = sway * 0.85f
        val headY = -24f + bounceY
        for (i in 0 until 6) {
            val ang = (i * 60f + sway * 1.5f) * (PI / 180.0).toFloat()
            val px = headX + cos(ang) * 7.8f
            val py = headY + sin(ang) * 7.8f
            drawCircle(petalColor, radius = 5.2f, center = Offset(px, py))
            drawCircle(InkBlack, radius = 5.2f, center = Offset(px, py), style = Stroke(1.4f))
        }
        drawCircle(SparkGold, radius = 5.8f, center = Offset(headX, headY))
        drawCircle(InkBlack, radius = 5.8f, center = Offset(headX, headY), style = Stroke(1.5f))
        drawCircle(InkBlack, radius = 1.2f, center = Offset(headX - 1.9f, headY - 1f))
        drawCircle(InkBlack, radius = 1.2f, center = Offset(headX + 1.9f, headY - 1f))
        drawArc(InkBlack, 15f, 150f, false, Offset(headX - 2.5f, headY + 0.2f), Size(5f, 2.8f), style = Stroke(1.1f))
    }
}

private fun DrawScope.drawCupheadTree(
    x: Float,
    y: Float,
    scale: Float,
    canopyColor: Color,
    phase: Float,
    wiggle: Float
) {
    val breathX = 1f + sin(phase * 1.5f) * 0.045f + sin(wiggle * 24f) * wiggle * 0.09f
    val breathY = 1f - sin(phase * 1.5f) * 0.04f
    val tilt = sin(phase * 1.2f) * 3.5f + sin(wiggle * 24f) * wiggle * 10f
    drawOval(Color.Black.copy(alpha = 0.25f), Offset(x - 28f * scale, y + 6f * scale), Size(56f * scale, 18f * scale))
    withTransform({
        translate(x, y)
        scale(scale * breathX, scale * breathY, Offset.Zero)
        rotate(tilt, Offset.Zero)
    }) {
        val trunk = Path().apply {
            moveTo(-11f, 8f)
            quadraticTo(-8f, -10f, -9f, -24f)
            lineTo(9f, -24f)
            quadraticTo(8f, -10f, 11f, 8f)
            close()
        }
        drawPath(trunk, Color(0xFF6B442A))
        drawPath(trunk, InkBlack, style = InkOutline)

        val puffs = listOf(Offset(-18f, -34f) to 20f, Offset(18f, -34f) to 20f, Offset(0f, -48f) to 24f, Offset(0f, -28f) to 22f)
        puffs.forEach { (off, r) ->
            drawCircle(canopyColor, radius = r, center = off)
            drawCircle(InkBlack, radius = r, center = off, style = InkOutline)
        }
        drawCircle(Color.White.copy(alpha = 0.88f), radius = 3.4f, center = Offset(-6f, -34f))
        drawCircle(Color.White.copy(alpha = 0.88f), radius = 3.4f, center = Offset(6f, -34f))
        drawCircle(InkBlack, radius = 1.7f, center = Offset(-5.5f, -34f))
        drawCircle(InkBlack, radius = 1.7f, center = Offset(5.5f, -34f))
    }
}

private fun DrawScope.drawCupheadShrub(
    x: Float,
    y: Float,
    scale: Float,
    color: Color,
    phase: Float,
    wiggle: Float
) {
    val sq = 1f + sin(phase * 2.1f) * 0.06f + sin(wiggle * 26f) * wiggle * 0.14f
    withTransform({
        translate(x, y)
        scale(scale * sq, scale * (2f - sq), Offset.Zero)
    }) {
        drawCircle(color, 13f, Offset(-9f, -7f))
        drawCircle(InkBlack, 13f, Offset(-9f, -7f), style = Stroke(1.8f))
        drawCircle(color, 13f, Offset(9f, -7f))
        drawCircle(InkBlack, 13f, Offset(9f, -7f), style = Stroke(1.8f))
        drawCircle(color, 15f, Offset(0f, -14f))
        drawCircle(InkBlack, 15f, Offset(0f, -14f), style = Stroke(2.1f))
    }
}

private fun DrawScope.drawCupheadCoralOrCrystal(
    x: Float,
    y: Float,
    scale: Float,
    color: Color,
    phase: Float,
    wiggle: Float
) {
    val sway = sin(phase * 1.9f) * 4f + sin(wiggle * 25f) * wiggle * 10f
    withTransform({
        translate(x, y)
        scale(scale, scale, Offset.Zero)
        rotate(sway, Offset.Zero)
    }) {
        val path = Path().apply {
            moveTo(-10f, 0f)
            quadraticTo(-9f, -14f, -6f, -24f)
            quadraticTo(-2f, -22f, 0f, -15f)
            quadraticTo(3f, -24f, 6f, -28f)
            quadraticTo(10f, -14f, 11f, 0f)
            close()
        }
        drawPath(path, color)
        drawPath(path, InkBlack, style = InkOutline)
    }
}

internal fun DrawScope.drawSmoothFlame(cx: Float, cy: Float, width: Float, height: Float) {
    val hw = width / 2f
    val flame = Path().apply {
        moveTo(cx, cy - height)
        cubicTo(cx + hw * 0.7f, cy - height * 0.55f, cx + hw * 1.15f, cy - height * 0.25f, cx + hw * 0.65f, cy)
        quadraticTo(cx, cy + height * 0.22f, cx - hw * 0.65f, cy)
        cubicTo(cx - hw * 1.15f, cy - height * 0.25f, cx - hw * 0.7f, cy - height * 0.55f, cx, cy - height)
        close()
    }
    drawPath(flame, FlameOrange)
    drawPath(flame, InkBlack, style = InkThin)
    drawCircle(SparkGold, width * 0.22f, Offset(cx, cy - height * 0.28f))
}

internal fun DrawScope.drawDrop(tip: Offset, r: Float, color: Color) {
    val p = Path().apply {
        moveTo(tip.x, tip.y - r * 1.6f)
        quadraticTo(tip.x + r * 1.3f, tip.y, tip.x, tip.y + r)
        quadraticTo(tip.x - r * 1.3f, tip.y, tip.x, tip.y - r * 1.6f)
        close()
    }
    drawPath(p, color)
    drawPath(p, InkBlack, style = Stroke(1f))
}
