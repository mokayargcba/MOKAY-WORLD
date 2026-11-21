package com.example.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.game.data.MokayRegionsCatalog
import com.example.game.data.MokayWorldCatalog
import com.example.game.engine.ActiveEnemy
import com.example.game.engine.GameLoopEngine
import com.example.game.engine.GameWorldState
import com.example.game.model.GateSpec
import com.example.game.model.HouseSpec
import com.example.game.model.LandmarkKind
import com.example.game.model.LandmarkSpec
import com.example.game.model.LivingFloraSpec
import com.example.game.model.NpcSpec
import com.example.game.model.PropSpec
import com.example.game.model.PuzzleType
import com.example.game.model.RegionId
import com.example.game.model.WorldPickup
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.InkBlack
import com.example.ui.theme.RunicTeal
import com.example.ui.theme.SparkGold
import com.example.ui.theme.StaminaEmerald
import com.example.ui.theme.WarmParchment
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

private sealed class YSortedDrawable(val sortY: Float) {
    class HouseItem(val spec: HouseSpec) : YSortedDrawable(spec.y + spec.height * 0.42f)
    class LandmarkItem(val spec: LandmarkSpec) : YSortedDrawable(spec.y + spec.height * 0.40f)
    class PropItem(val spec: PropSpec) : YSortedDrawable(spec.y)
    class FloraItem(val spec: LivingFloraSpec) : YSortedDrawable(spec.y)
    class PickupItem(val spec: WorldPickup) : YSortedDrawable(spec.y)
    class NpcItem(val spec: NpcSpec) : YSortedDrawable(spec.y + 22f)
    class EnemyItem(val spec: ActiveEnemy) : YSortedDrawable(spec.y + 24f)
    class PlayerItem(sortY: Float) : YSortedDrawable(sortY)
}

@Composable
fun GameCanvasView(
    state: GameWorldState,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val spriteBank = rememberMokaySpriteBank()
    val region = MokayRegionsCatalog.regions.getValue(state.currentRegionId)

    Canvas(modifier = modifier.fillMaxSize()) {
        // Calibrated landscape zoom showing rich Mokaya proportions (Boxy crisp & close alongside ~2.4x tall houses & grand monuments)
        val worldZoom = (size.height / 470f).coerceIn(1.52f, 1.92f)

        withTransform({
            translate(size.width / 2f, size.height / 2f)
            scale(worldZoom, worldZoom, Offset.Zero)
            translate(-state.playerX, -state.playerY)
        }) {
            // 1. Multi-layered Environment Ground (water/clouds, cobblestone plaza, avenues, sports courts, animated train, swaying grass)
            drawWorldEnvironmentBase(region = region, animPhase = state.animPhase)

            // Flat ground-level landmarks (fountains, glowing lakes, ancestral portal, courts) drawn beneath characters
            val groundLandmarkKinds = setOf(
                LandmarkKind.FOUNTAIN,
                LandmarkKind.GLOWING_LAKE,
                LandmarkKind.ANCESTRAL_PORTAL,
                LandmarkKind.SKATE_PARK,
                LandmarkKind.BASKET_COURT,
                LandmarkKind.MAZE,
                LandmarkKind.GARDEN,
                LandmarkKind.PIER
            )
            region.landmarks
                .filter { it.kind in groundLandmarkKinds }
                .forEach { lm ->
                    drawLandmark2D(lm, state.animPhase, textMeasurer)
                }

            // 2. Puzzles (Pressure plates, Torch sequences, Cubist movable blocks)
            region.puzzles.forEach { puz ->
                val solved = puz.id in state.solvedPuzzleIds
                val activeNodes = state.activePuzzleProgress[puz.id].orEmpty()
                puz.nodes.forEach { node ->
                    when (puz.type) {
                        PuzzleType.PRESSURE_PLATES -> {
                            val on = solved || node.id in activeNodes
                            drawCircle(
                                color = if (on) RunicTeal else Color(0xFF455A64),
                                radius = 25f,
                                center = Offset(node.x, node.y)
                            )
                            drawCircle(
                                color = if (on) SparkGold else Color(0xFF8BE0E6).copy(alpha = 0.45f),
                                radius = 15f,
                                center = Offset(node.x, node.y),
                                style = Stroke(2.2f)
                            )
                            drawCircle(InkBlack, 25f, Offset(node.x, node.y), style = Stroke(2.5f))
                        }
                        PuzzleType.TORCH_SEQUENCE -> {
                            val lit = solved || node.id in activeNodes
                            drawCircle(Color(0xFF3E352B), 17f, Offset(node.x, node.y))
                            drawCircle(InkBlack, 17f, Offset(node.x, node.y), style = Stroke(2.2f))
                            if (lit) {
                                drawCircle(FlameOrange.copy(alpha = 0.35f), 28f, Offset(node.x, node.y - 6f))
                                drawSmoothFlame(node.x, node.y, 18f, 24f)
                            }
                            drawText(
                                textMeasurer = textMeasurer,
                                text = "${node.orderIndex}",
                                topLeft = Offset(node.x - 5f, node.y + 18f),
                                style = TextStyle(color = WarmParchment, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                        PuzzleType.CUBIST_BLOCKS -> {
                            drawRoundRect(
                                color = SparkGold.copy(alpha = 0.38f),
                                topLeft = Offset(node.targetX - 24f, node.targetY - 24f),
                                size = Size(48f, 48f),
                                cornerRadius = CornerRadius(8f, 8f),
                                style = Stroke(3f)
                            )
                            val pos = state.blockPositions[node.id] ?: (node.x to node.y)
                            drawRoundRect(
                                color = if (solved) RunicTeal else Color(0xFFFFB703),
                                topLeft = Offset(pos.first - 20f, pos.second - 20f),
                                size = Size(40f, 40f),
                                cornerRadius = CornerRadius(7f, 7f)
                            )
                            drawRoundRect(
                                color = InkBlack,
                                topLeft = Offset(pos.first - 20f, pos.second - 20f),
                                size = Size(40f, 40f),
                                cornerRadius = CornerRadius(7f, 7f),
                                style = Stroke(2.6f)
                            )
                        }
                    }
                }
            }

            // 3. Regional Gates / Portals / Bridges
            region.gates.forEach { gate ->
                val isUnlocked = gate.id in state.unlockedGateIds ||
                    (gate.requiredBookId == null && gate.requiredQuestItemId == null && gate.requiredBossStageId == null && gate.requiredForces.isEmpty())
                drawGateArchway(gate, isUnlocked, state.animPhase, textMeasurer)
            }

            // 4. Y-Depth Sorted Foreground Layer (Houses, Standing Monuments, Props, Trees/Flowers, Pickups, NPCs, Enemies, Boxy+Foskay)
            val renderQueue = buildList {
                region.houses.forEach { add(YSortedDrawable.HouseItem(it)) }
                region.landmarks
                    .filter { it.kind !in groundLandmarkKinds }
                    .forEach { add(YSortedDrawable.LandmarkItem(it)) }
                region.props.forEach { add(YSortedDrawable.PropItem(it)) }
                region.flora.forEach { add(YSortedDrawable.FloraItem(it)) }
                region.pickups
                    .filter { it.id !in state.collectedPickupIds }
                    .forEach { add(YSortedDrawable.PickupItem(it)) }
                region.npcs.forEach { add(YSortedDrawable.NpcItem(it)) }
                state.activeEnemies.forEach { add(YSortedDrawable.EnemyItem(it)) }
                add(YSortedDrawable.PlayerItem(state.playerY + 26f))
            }.sortedBy { it.sortY }

            val emergeCurve = if (state.foskayActiveTimer > 0f) {
                sin(((state.foskayActiveTimer / 0.85f).coerceIn(0f, 1f)) * PI).toFloat()
            } else 0f

            renderQueue.forEach { item ->
                when (item) {
                    is YSortedDrawable.HouseItem -> drawHouse2D(item.spec, state.animPhase, textMeasurer)
                    is YSortedDrawable.LandmarkItem -> drawLandmark2D(item.spec, state.animPhase, textMeasurer)
                    is YSortedDrawable.PropItem -> drawProp2D(item.spec, state.animPhase)
                    is YSortedDrawable.FloraItem -> drawLivingFlora(
                        item.spec,
                        state.animPhase,
                        state.floraWiggleTimers[item.spec.id] ?: 0f
                    )
                    is YSortedDrawable.PickupItem -> drawWorldPickup(item.spec, state.animPhase)
                    is YSortedDrawable.NpcItem -> {
                        val npc = item.spec
                        drawNpcCharacter(npc, region.theme, state.animPhase, spriteBank)

                        // Quest indicator (! / ?) or shop icon above NPC head
                        val qSpec = npc.questId?.let { qid ->
                            MokayWorldCatalog.quests.firstOrNull { it.id == qid }
                        }
                        val bob = sin(state.animPhase * 2.2f + npc.x * 0.02f) * 3f
                        if (qSpec != null && qSpec.id !in state.completedQuests) {
                            val ready = GameLoopEngine.isQuestReadyToComplete(state, qSpec)
                            val badgeColor = if (ready) StaminaEmerald else SparkGold
                            val symbol = if (ready) "?" else "!"
                            drawCircle(badgeColor, 10f, Offset(npc.x, npc.y - 54f + bob))
                            drawCircle(InkBlack, 10f, Offset(npc.x, npc.y - 54f + bob), style = Stroke(2f))
                            val qm = textMeasurer.measure(
                                symbol,
                                TextStyle(color = InkBlack, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            )
                            drawText(qm, topLeft = Offset(npc.x - qm.size.width / 2f, npc.y - 54f + bob - qm.size.height / 2f))
                        } else if (npc.offersShop) {
                            drawCircle(RunicTeal, 8f, Offset(npc.x, npc.y - 52f + bob))
                            drawCircle(InkBlack, 8f, Offset(npc.x, npc.y - 52f + bob), style = Stroke(1.8f))
                        }

                        val m = textMeasurer.measure(
                            npc.name,
                            TextStyle(color = WarmParchment, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        )
                        drawRoundRect(
                            InkBlack.copy(alpha = 0.58f),
                            Offset(npc.x - m.size.width / 2f - 4f, npc.y + 32f),
                            Size(m.size.width + 8f, m.size.height + 2f),
                            CornerRadius(5f, 5f)
                        )
                        drawText(m, topLeft = Offset(npc.x - m.size.width / 2f, npc.y + 33f))

                        val distToPlayer = hypot((npc.x - state.playerX).toDouble(), (npc.y - state.playerY).toDouble())
                        if (distToPlayer < 220.0 && npc.villageName.isNotEmpty()) {
                            val vm = textMeasurer.measure(
                                npc.villageName,
                                TextStyle(color = SparkGold, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                            )
                            drawRoundRect(
                                InkBlack.copy(alpha = 0.65f),
                                Offset(npc.x - vm.size.width / 2f - 4f, npc.y + 48f),
                                Size(vm.size.width + 8f, vm.size.height + 2f),
                                CornerRadius(4f, 4f)
                            )
                            drawText(vm, topLeft = Offset(npc.x - vm.size.width / 2f, npc.y + 49f))
                        }
                    }
                    is YSortedDrawable.EnemyItem -> drawEnemyOrBoss(item.spec, state.animPhase, spriteBank)
                    is YSortedDrawable.PlayerItem -> drawBoxyCharacter(
                        x = state.playerX,
                        y = state.playerY,
                        direction = state.direction,
                        animState = state.animState,
                        phase = state.animPhase,
                        actionProgress = if (state.actionDuration > 0f) state.actionTimer / state.actionDuration else 0f,
                        scale = 1.0f,
                        invulnerableBlink = state.invulnerableTimer > 0f,
                        spriteBank = spriteBank,
                        foskayLightActive = state.foskayLightTimer > 0f,
                        foskayEmergence = emergeCurve
                    )
                }
            }

            // 5. Biome Ambient Floating Particles (fireflies, petals, bubbles, stardust, ink sparkles)
            drawAmbientBiomeParticles(region.id, region.worldWidth, region.worldHeight, state.animPhase)

            // 6. Combat & Action Particles
            state.particles.forEach { p ->
                val alpha = (p.life / p.maxLife).coerceIn(0f, 1f)
                drawCircle(Color(p.colorHex).copy(alpha = alpha), p.radius, Offset(p.x, p.y))
            }

            // 7. Floating Combat / Pickup Texts
            state.floatingTexts.forEach { ft ->
                drawText(
                    textMeasurer = textMeasurer,
                    text = ft.text,
                    topLeft = Offset(ft.x - 36f, ft.y),
                    style = TextStyle(
                        color = Color(ft.colorHex).copy(alpha = ft.life.coerceIn(0f, 1f)),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Dynamic Darkness / Foskay Light Radius Overlay for Sombravía & Bosque Olvidado
        if (region.theme.darknessAlpha > 0.05f) {
            val lightRadius = if (state.foskayLightTimer > 0f) 460f else 205f
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF0A0614).copy(alpha = region.theme.darknessAlpha)
                    ),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = lightRadius
                )
            )
        }
    }
}

private fun DrawScope.drawGateArchway(
    gate: GateSpec,
    isUnlocked: Boolean,
    animPhase: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val gateColor = if (isUnlocked) RunicTeal else FlameOrange
    val pulse = 44f + sin(animPhase * 2.4f) * 5f
    drawCircle(
        brush = Brush.radialGradient(
            listOf(gateColor.copy(alpha = 0.65f), Color.Transparent),
            center = Offset(gate.x, gate.y),
            radius = pulse
        ),
        radius = pulse,
        center = Offset(gate.x, gate.y)
    )
    // Arch pillars flanking portal pad
    drawRoundRect(Color(0xFF423A4E), Offset(gate.x - 28f, gate.y - 26f), Size(10f, 34f), CornerRadius(3f, 3f))
    drawRoundRect(Color(0xFF423A4E), Offset(gate.x + 18f, gate.y - 26f), Size(10f, 34f), CornerRadius(3f, 3f))
    drawCircle(gateColor, 20f, Offset(gate.x, gate.y))
    drawCircle(Color.White.copy(alpha = 0.45f), 11f, Offset(gate.x, gate.y), style = Stroke(2.2f))
    drawCircle(InkBlack, 20f, Offset(gate.x, gate.y), style = Stroke(2.5f))

    val m = textMeasurer.measure(
        gate.label,
        TextStyle(color = WarmParchment, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    )
    val bx = gate.x - m.size.width / 2f - 6f
    val by = gate.y + 24f
    drawRoundRect(InkBlack.copy(alpha = 0.72f), Offset(bx, by), Size(m.size.width + 12f, m.size.height + 4f), CornerRadius(6f, 6f))
    drawText(m, topLeft = Offset(bx + 6f, by + 2f))
}

private fun DrawScope.drawWorldPickup(pickup: WorldPickup, animPhase: Float) {
    val isBook = pickup.bookId != null
    val color = if (isBook) SparkGold else Color(MokayWorldCatalog.items[pickup.itemId]?.colorHex ?: 0xFF4CC37A)
    val bob = sin(animPhase * 2.2f + pickup.x * 0.03f) * 3.5f
    drawCircle(color.copy(alpha = 0.34f), 20f, Offset(pickup.x, pickup.y + bob))
    if (isBook) {
        drawRoundRect(color, Offset(pickup.x - 11f, pickup.y - 13f + bob), Size(22f, 26f), CornerRadius(4f, 4f))
        drawRoundRect(InkBlack, Offset(pickup.x - 11f, pickup.y - 13f + bob), Size(22f, 26f), CornerRadius(4f, 4f), style = Stroke(2f))
        drawLine(InkBlack, Offset(pickup.x - 5f, pickup.y - 13f + bob), Offset(pickup.x - 5f, pickup.y + 13f + bob), strokeWidth = 1.6f)
    } else {
        drawCircle(color, 10.5f, Offset(pickup.x, pickup.y + bob))
        drawCircle(Color.White.copy(alpha = 0.6f), 3.2f, Offset(pickup.x - 3f, pickup.y - 3f + bob))
        drawCircle(InkBlack, 10.5f, Offset(pickup.x, pickup.y + bob), style = Stroke(2f))
    }
}

private fun DrawScope.drawAmbientBiomeParticles(
    regionId: RegionId,
    w: Float,
    h: Float,
    animPhase: Float
) {
    val moteColor = when (regionId) {
        RegionId.PLAZA_INFINITA, RegionId.ARENA_VIVA -> SparkGold
        RegionId.TINTOPOLIS -> Color(0xFFE0E0E0)
        RegionId.MAREA_VIVA -> Color(0xFF8BE0E6)
        RegionId.RHYTHM_HOOD -> Color(0xFFFF3D9A)
        RegionId.ARTELIA, RegionId.PICASIA -> Color(0xFFE97893)
        RegionId.CONSTELARIA -> Color(0xFFE6D9FF)
        RegionId.SOMBRAVIA, RegionId.BOSQUE_OLVIDADO -> Color(0xFFC77DFF)
        RegionId.ARBOL_ORIGINAL -> FlameOrange
    }
    for (i in 0 until 24) {
        val baseX = 70f + ((i * 217 + regionId.ordinal * 31) % (w - 140f).toInt())
        val baseY = 70f + ((i * 179 + regionId.ordinal * 53) % (h - 140f).toInt())
        val px = baseX + cos(animPhase * 0.8f + i) * 18f
        val py = baseY + sin(animPhase * 1.1f + i * 0.7f) * 16f
        val alpha = 0.25f + 0.35f * ((sin(animPhase * 2.2f + i) + 1f) * 0.5f)
        drawCircle(moteColor.copy(alpha = alpha), radius = if (i % 3 == 0) 3.4f else 2.2f, center = Offset(px, py))
    }
}
