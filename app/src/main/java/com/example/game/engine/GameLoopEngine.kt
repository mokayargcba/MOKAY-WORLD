package com.example.game.engine

import com.example.game.data.MokayRegionsCatalog
import com.example.game.data.MokayWorldCatalog
import com.example.game.model.Direction
import com.example.game.model.EnemyVisual
import com.example.game.model.GateSpec
import com.example.game.model.NpcSpec
import com.example.game.model.PlayerAnimState
import com.example.game.model.PuzzleNode
import com.example.game.model.PuzzleSpec
import com.example.game.model.PuzzleType
import com.example.game.model.QuestSpec
import com.example.game.model.RegionId
import com.example.game.model.ShopUpgradeKind
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

data class ActiveEnemy(
    val id: String,
    val name: String,
    val visual: EnemyVisual,
    val x: Float,
    val y: Float,
    val hp: Int,
    val maxHp: Int,
    val attackDamage: Int,
    val speed: Float,
    val tier: Int,
    val isBoss: Boolean,
    val bossStageId: String?,
    val dropItemId: String?,
    val dropBookId: String?,
    val hitFlashTimer: Float = 0f,
    val attackCooldown: Float = 1.1f,
    val knockX: Float = 0f,
    val knockY: Float = 0f
)

data class ActiveParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val life: Float,
    val maxLife: Float,
    val colorHex: Long,
    val radius: Float
)

data class FloatingBanner(
    val text: String,
    val x: Float,
    val y: Float,
    val life: Float,
    val colorHex: Long
)

data class PlayerInput(
    val moveX: Float = 0f,
    val moveY: Float = 0f
)

data class GameWorldState(
    val currentRegionId: RegionId = RegionId.PLAZA_INFINITA,
    val playerX: Float = 900f,
    val playerY: Float = 740f,
    val direction: Direction = Direction.FRONT,
    val animState: PlayerAnimState = PlayerAnimState.IDLE,
    val animPhase: Float = 0f,
    val actionTimer: Float = 0f,
    val actionDuration: Float = 0.25f,
    val hitStopTimer: Float = 0f,
    val invulnerableTimer: Float = 0f,
    val hp: Int = 100,
    val maxHp: Int = 100,
    val stamina: Float = 100f,
    val maxStamina: Float = 100f,
    val mana: Float = 80f,
    val maxMana: Float = 100f,
    val chispas: Int = 60,
    val exhausted: Boolean = false,
    val foskayLightTimer: Float = 0f,
    val foskayActiveTimer: Float = 0f,
    val foskayX: Float = 900f,
    val foskayY: Float = 740f,
    val inventory: Map<String, Int> = mapOf(
        "slice_pizza" to 2,
        "cafe_bw" to 2,
        "elixir_chispa" to 2
    ),
    val quickSlots: List<String> = listOf("slice_pizza", "cafe_bw", "elixir_chispa"),
    val collectedBookIds: Set<String> = emptySet(),
    val discoveredKeywords: Set<String> = emptySet(),
    val discoveredRegions: Set<RegionId> = setOf(RegionId.PLAZA_INFINITA),
    val discoveredLandmarkIds: Set<String> = setOf("plaza_center"),
    val visitedVillages: Set<String> = emptySet(),
    val activeQuestIds: Set<String> = setOf("q_plaza_spark", "q_tintopolis_cat"),
    val completedQuests: Set<String> = emptySet(),
    val trackedQuestId: String = "q_tintopolis_cat",
    val solvedPuzzleIds: Set<String> = emptySet(),
    val defeatedBossStages: Set<String> = emptySet(),
    val unlockedGateIds: Set<String> = emptySet(),
    val collectedForces: Set<String> = emptySet(),
    val collectedPickupIds: Set<String> = emptySet(),
    val npcRelationships: Map<String, Int> = emptyMap(),
    val claimedDialogueChoices: Set<String> = emptySet(),
    val unlockedAchievementIds: Set<String> = emptySet(),
    val activePuzzleProgress: Map<String, Set<String>> = emptyMap(),
    val blockPositions: Map<String, Pair<Float, Float>> = emptyMap(),
    val floraWiggleTimers: Map<String, Float> = emptyMap(),
    val activeEnemies: List<ActiveEnemy> = emptyList(),
    val particles: List<ActiveParticle> = emptyList(),
    val floatingTexts: List<FloatingBanner> = emptyList(),
    val nearestLandmarkName: String = "Fuente Infinita",
    val nearbyNpc: NpcSpec? = null,
    val nearbyGate: GateSpec? = null,
    val toastMessage: String? = null,
    val hapticTick: Int = 0
)

object GameLoopEngine {

    fun spawnRegionEntities(
        state: GameWorldState,
        regionId: RegionId,
        refreshRespawningPickups: Boolean = false
    ): GameWorldState {
        val region = MokayRegionsCatalog.regions.getValue(regionId)
        val enemies = region.enemies
            .filter { e -> e.bossStageId == null || e.bossStageId !in state.defeatedBossStages }
            .map { e ->
                ActiveEnemy(
                    id = e.id,
                    name = e.name,
                    visual = e.visual,
                    x = e.x,
                    y = e.y,
                    hp = e.maxHp,
                    maxHp = e.maxHp,
                    attackDamage = e.attackDamage,
                    speed = e.speed,
                    tier = e.tier,
                    isBoss = e.isBoss,
                    bossStageId = e.bossStageId,
                    dropItemId = e.dropItemId,
                    dropBookId = e.dropBookId
                )
            }
        val blocks = buildMap {
            region.puzzles.filter { it.type == PuzzleType.CUBIST_BLOCKS }.forEach { puz ->
                puz.nodes.forEach { node ->
                    if (puz.id in state.solvedPuzzleIds) {
                        put(node.id, node.targetX to node.targetY)
                    } else {
                        put(node.id, node.x to node.y)
                    }
                }
            }
        }
        val landmark = region.landmarks.minByOrNull {
            hypot((it.x - state.playerX).toDouble(), (it.y - state.playerY).toDouble())
        }?.name ?: region.id.title

        val nextPicked = if (refreshRespawningPickups) {
            val respawningInDest = region.pickups.filter { it.respawns }.map { it.id }.toSet()
            state.collectedPickupIds - respawningInDest
        } else {
            state.collectedPickupIds
        }

        val initialLandmarks = region.landmarks.filter { lm ->
            val threshold = max(lm.width, lm.height) * 0.60f + 90f
            hypot((lm.x - state.playerX).toDouble(), (lm.y - state.playerY).toDouble()).toFloat() <= threshold
        }.map { it.id }.toSet()

        val initialVillages = region.npcs.filter { npc ->
            npc.villageName.isNotBlank() &&
                hypot((npc.x - state.playerX).toDouble(), (npc.y - state.playerY).toDouble()).toFloat() <= 210f
        }.map { it.villageName }.toSet()

        return state.copy(
            currentRegionId = regionId,
            discoveredRegions = state.discoveredRegions + regionId,
            discoveredLandmarkIds = state.discoveredLandmarkIds + initialLandmarks,
            visitedVillages = state.visitedVillages + initialVillages,
            activeEnemies = enemies,
            blockPositions = state.blockPositions + blocks,
            nearestLandmarkName = landmark,
            collectedPickupIds = nextPicked
        )
    }

    fun step(state: GameWorldState, input: PlayerInput, rawDt: Float): GameWorldState {
        val dt = rawDt.coerceIn(0.001f, 0.05f)
        if (state.hitStopTimer > 0f) {
            return state.copy(hitStopTimer = max(0f, state.hitStopTimer - dt))
        }

        val region = MokayRegionsCatalog.regions.getValue(state.currentRegionId)
        var s = state.copy(
            animPhase = (state.animPhase + dt * 5.5f) % 62.83f,
            invulnerableTimer = max(0f, state.invulnerableTimer - dt),
            foskayLightTimer = max(0f, state.foskayLightTimer - dt),
            foskayActiveTimer = max(0f, state.foskayActiveTimer - dt),
            mana = min(state.maxMana, state.mana + dt * 2.4f)
        )

        // Update particles & floating banners
        s = s.copy(
            particles = s.particles.mapNotNull { p ->
                val nl = p.life - dt
                if (nl <= 0f) null else p.copy(
                    x = p.x + p.vx * dt,
                    y = p.y + p.vy * dt,
                    life = nl
                )
            },
            floatingTexts = s.floatingTexts.mapNotNull { f ->
                val nl = f.life - dt
                if (nl <= 0f) null else f.copy(y = f.y - 26f * dt, life = nl)
            },
            floraWiggleTimers = s.floraWiggleTimers.mapValues { max(0f, it.value - dt) }.filterValues { it > 0f }
        )

        val lockedByAction = s.actionTimer > 0f
        if (lockedByAction) {
            val nextTimer = max(0f, s.actionTimer - dt)
            val dodgePush = if (s.animState == PlayerAnimState.DODGE) 420f * dt else 0f
            val (dx, dy) = dirVector(s.direction)
            val nx = (s.playerX + dx * dodgePush).coerceIn(60f, region.worldWidth - 60f)
            val ny = (s.playerY + dy * dodgePush).coerceIn(60f, region.worldHeight - 60f)
            s = s.copy(
                actionTimer = nextTimer,
                playerX = nx,
                playerY = ny,
                animState = if (nextTimer == 0f) {
                    if (s.exhausted) PlayerAnimState.FATIGUED else PlayerAnimState.IDLE
                } else s.animState
            )
        } else {
            val mag = hypot(input.moveX.toDouble(), input.moveY.toDouble()).toFloat().coerceAtMost(1f)
            val isMoving = mag > 0.08f
            val speed = if (s.exhausted) 128f else 265f
            val moveVx = if (isMoving) (input.moveX / max(mag, 0.001f)) * mag * speed else 0f
            val moveVy = if (isMoving) (input.moveY / max(mag, 0.001f)) * mag * speed else 0f

            var nextX = (s.playerX + moveVx * dt).coerceIn(55f, region.worldWidth - 55f)
            var nextY = (s.playerY + moveVy * dt).coerceIn(55f, region.worldHeight - 55f)

            // Smooth collision push-out around solid house footprints
            region.houses.forEach { house ->
                val halfW = house.width * 0.44f + 16f
                val halfH = house.height * 0.34f + 14f
                val dxH = nextX - house.x
                val dyH = nextY - house.y
                if (kotlin.math.abs(dxH) < halfW && kotlin.math.abs(dyH) < halfH) {
                    val overlapX = halfW - kotlin.math.abs(dxH)
                    val overlapY = halfH - kotlin.math.abs(dyH)
                    if (overlapX < overlapY) {
                        nextX = house.x + if (dxH >= 0f) halfW else -halfW
                    } else {
                        nextY = house.y + if (dyH >= 0f) halfH else -halfH
                    }
                }
            }

            // Pushable Cubist blocks in Picasia
            val updatedBlocks = s.blockPositions.toMutableMap()
            val newlySolved = s.solvedPuzzleIds.toMutableSet()
            val inv = s.inventory.toMutableMap()
            val banners = s.floatingTexts.toMutableList()

            region.puzzles.filter { it.type == PuzzleType.CUBIST_BLOCKS }.forEach { puz ->
                var allAligned = true
                puz.nodes.forEach { node ->
                    val pos = updatedBlocks[node.id] ?: (node.x to node.y)
                    val distToBlock = hypot((nextX - pos.first).toDouble(), (nextY - pos.second).toDouble()).toFloat()
                    if (distToBlock < 38f && isMoving) {
                        val pushX = (pos.first + moveVx * dt * 0.9f).coerceIn(90f, region.worldWidth - 90f)
                        val pushY = (pos.second + moveVy * dt * 0.9f).coerceIn(90f, region.worldHeight - 90f)
                        updatedBlocks[node.id] = pushX to pushY
                    }
                    val finalPos = updatedBlocks[node.id] ?: pos
                    if (hypot((finalPos.first - node.targetX).toDouble(), (finalPos.second - node.targetY).toDouble()) > 34f) {
                        allAligned = false
                    }
                }
                if (allAligned && puz.id !in newlySolved) {
                    newlySolved.add(puz.id)
                    puz.rewardItemId?.let { id ->
                        inv[id] = (inv[id] ?: 0) + 1
                        val name = MokayWorldCatalog.items[id]?.name ?: id
                        banners.add(FloatingBanner("¡Puzzle resuelto! +$name", nextX, nextY - 40f, 2.2f, 0xFF2EC4B6))
                    }
                }
            }

            // Stamina recovery & fatigue clearing
            val regenRate = if (isMoving) 12f else 22f
            val nextStamina = min(s.maxStamina, s.stamina + regenRate * dt)
            val stillExhausted = if (s.exhausted && nextStamina >= 42f) false else s.exhausted

            val nextDir = if (isMoving) {
                if (kotlin.math.abs(input.moveX) > kotlin.math.abs(input.moveY)) {
                    if (input.moveX > 0) Direction.RIGHT else Direction.LEFT
                } else {
                    if (input.moveY > 0) Direction.FRONT else Direction.BACK
                }
            } else s.direction

            s = s.copy(
                playerX = nextX,
                playerY = nextY,
                direction = nextDir,
                stamina = nextStamina,
                exhausted = stillExhausted,
                animState = when {
                    stillExhausted -> PlayerAnimState.FATIGUED
                    isMoving -> PlayerAnimState.MOVE
                    else -> PlayerAnimState.IDLE
                },
                blockPositions = updatedBlocks,
                solvedPuzzleIds = newlySolved,
                inventory = inv,
                floatingTexts = banners
            )
        }

        // Cuphead flora reactive wobble + automatic pickups + pressure plates
        s = handlePickupsPlatesAndFlora(s, region)

        // Track newly discovered landmarks & visited villages
        s = handleExplorationDiscovery(s, region)

        // Update enemies & boss AI
        s = updateEnemies(s, region.worldWidth, region.worldHeight, dt)

        // Keep Foskay inside Boxy's matchbox
        val fTargetX = s.playerX
        val fTargetY = s.playerY
        val landmark = region.landmarks.minByOrNull {
            hypot((it.x - s.playerX).toDouble(), (it.y - s.playerY).toDouble())
        }?.name ?: region.id.title
        val nearNpc = region.npcs.firstOrNull {
            hypot((it.x - s.playerX).toDouble(), (it.y - s.playerY).toDouble()) < 86f
        }
        val nearGate = region.gates.firstOrNull {
            hypot((it.x - s.playerX).toDouble(), (it.y - s.playerY).toDouble()) < 88f
        }

        return s.copy(
            foskayX = fTargetX,
            foskayY = fTargetY,
            nearestLandmarkName = landmark,
            nearbyNpc = nearNpc,
            nearbyGate = nearGate
        )
    }

    private fun handlePickupsPlatesAndFlora(
        state: GameWorldState,
        region: com.example.game.model.RegionSpec
    ): GameWorldState {
        var s = state
        val wiggles = s.floraWiggleTimers.toMutableMap()
        region.flora.forEach { flora ->
            val d = hypot((flora.x - s.playerX).toDouble(), (flora.y - s.playerY).toDouble())
            if (d < 52.0 && flora.id !in wiggles) {
                wiggles[flora.id] = 0.55f
            }
        }
        s = s.copy(floraWiggleTimers = wiggles)

        // Pickups
        val inv = s.inventory.toMutableMap()
        val picked = s.collectedPickupIds.toMutableSet()
        val books = s.collectedBookIds.toMutableSet()
        val keywords = s.discoveredKeywords.toMutableSet()
        val forces = s.collectedForces.toMutableSet()
        val banners = s.floatingTexts.toMutableList()

        var chispasGained = 0
        region.pickups.forEach { p ->
            if (p.id !in picked) {
                val d = hypot((p.x - s.playerX).toDouble(), (p.y - s.playerY).toDouble())
                if (d < 48.0) {
                    picked.add(p.id)
                    chispasGained += if (p.bookId != null) 25 else 10
                    if (p.bookId != null) {
                        val book = MokayWorldCatalog.books[p.bookId]
                        if (book != null) {
                            books.add(book.id)
                            keywords.add(book.keyword)
                            book.forceGranted?.let { forces.add(it) }
                            banners.add(FloatingBanner("Libro: ${book.title} (${book.keyword})", p.x, p.y - 24f, 2.5f, 0xFFFFC24D))
                        }
                    } else {
                        inv[p.itemId] = (inv[p.itemId] ?: 0) + p.quantity
                        val itemName = MokayWorldCatalog.items[p.itemId]?.name ?: p.itemId
                        banners.add(FloatingBanner("+${p.quantity} $itemName • +10 Chispas", p.x, p.y - 20f, 1.9f, 0xFF4CC37A))
                    }
                }
            }
        }

        // Pressure plates puzzle
        val progress = s.activePuzzleProgress.toMutableMap()
        val solved = s.solvedPuzzleIds.toMutableSet()
        region.puzzles.filter { it.type == PuzzleType.PRESSURE_PLATES && it.id !in solved }.forEach { puz ->
            val currentSet = (progress[puz.id] ?: emptySet()).toMutableSet()
            puz.nodes.forEach { node ->
                val d = hypot((node.x - s.playerX).toDouble(), (node.y - s.playerY).toDouble())
                if (d < 36.0 && node.id !in currentSet) {
                    currentSet.add(node.id)
                    banners.add(FloatingBanner("Placa activada (${currentSet.size}/${puz.nodes.size})", node.x, node.y - 24f, 1.4f, 0xFF6FD0E8))
                }
            }
            progress[puz.id] = currentSet
            if (currentSet.size == puz.nodes.size) {
                solved.add(puz.id)
                chispasGained += 45
                puz.rewardItemId?.let { id ->
                    inv[id] = (inv[id] ?: 0) + 1
                    banners.add(FloatingBanner("¡Gruta abierta! +${MokayWorldCatalog.items[id]?.name ?: id}", s.playerX, s.playerY - 40f, 2.4f, 0xFF2EC4B6))
                }
                puz.rewardBookId?.let { bid ->
                    MokayWorldCatalog.books[bid]?.let { book ->
                        books.add(book.id)
                        keywords.add(book.keyword)
                        book.forceGranted?.let { forces.add(it) }
                    }
                }
            }
        }

        return s.copy(
            inventory = inv,
            chispas = s.chispas + chispasGained,
            collectedPickupIds = picked,
            collectedBookIds = books,
            discoveredKeywords = keywords,
            collectedForces = forces,
            floatingTexts = banners,
            activePuzzleProgress = progress,
            solvedPuzzleIds = solved,
            hapticTick = if (chispasGained > 0) s.hapticTick + 1 else s.hapticTick
        )
    }

    private fun handleExplorationDiscovery(
        state: GameWorldState,
        region: com.example.game.model.RegionSpec
    ): GameWorldState {
        val discoveredLm = state.discoveredLandmarkIds.toMutableSet()
        val visited = state.visitedVillages.toMutableSet()
        val banners = state.floatingTexts.toMutableList()
        var foundSomething = false

        region.landmarks.forEach { lm ->
            if (lm.id !in discoveredLm) {
                val threshold = max(lm.width, lm.height) * 0.55f + 72f
                val d = hypot((lm.x - state.playerX).toDouble(), (lm.y - state.playerY).toDouble()).toFloat()
                if (d <= threshold) {
                    discoveredLm.add(lm.id)
                    foundSomething = true
                    banners.add(FloatingBanner("✦ Descubierto: ${lm.name}", lm.x, lm.y - 38f, 2.2f, 0xFFFFC24D))
                }
            }
        }

        region.npcs.forEach { npc ->
            if (npc.villageName.isNotBlank() && npc.villageName !in visited) {
                val d = hypot((npc.x - state.playerX).toDouble(), (npc.y - state.playerY).toDouble()).toFloat()
                if (d <= 195f) {
                    visited.add(npc.villageName)
                    foundSomething = true
                    banners.add(FloatingBanner("⚑ Aldea: ${npc.villageName}", npc.x, npc.y - 44f, 2.0f, 0xFF4CC37A))
                }
            }
        }

        return if (!foundSomething) state else state.copy(
            discoveredLandmarkIds = discoveredLm,
            visitedVillages = visited,
            floatingTexts = banners,
            hapticTick = state.hapticTick + 1
        )
    }

    private fun updateEnemies(
        state: GameWorldState,
        worldW: Float,
        worldH: Float,
        dt: Float
    ): GameWorldState {
        if (state.activeEnemies.isEmpty()) return state

        var hp = state.hp
        var invuln = state.invulnerableTimer
        val banners = state.floatingTexts.toMutableList()
        val particles = state.particles.toMutableList()

        val updated = state.activeEnemies.map { e ->
            var nx = e.x + e.knockX * dt
            var ny = e.y + e.knockY * dt
            val d = hypot((state.playerX - nx).toDouble(), (state.playerY - ny).toDouble()).toFloat()
            val aggroRange = if (e.isBoss) 440f else 330f
            if (d in 44f..aggroRange) {
                nx += ((state.playerX - nx) / d) * e.speed * dt
                ny += ((state.playerY - ny) / d) * e.speed * dt
            }
            nx = nx.coerceIn(70f, worldW - 70f)
            ny = ny.coerceIn(70f, worldH - 70f)

            var cd = max(0f, e.attackCooldown - dt)
            if (d < 54f && cd <= 0f && invuln <= 0f) {
                hp = max(0, hp - e.attackDamage)
                invuln = 0.75f
                cd = if (e.isBoss) 1.0f else 1.35f
                banners.add(FloatingBanner("-${e.attackDamage}", state.playerX, state.playerY - 34f, 1.0f, 0xFFD94E4E))
                repeat(6) { idx ->
                    val angle = idx * 1.047f
                    particles.add(
                        ActiveParticle(
                            x = state.playerX,
                            y = state.playerY,
                            vx = kotlin.math.cos(angle) * 90f,
                            vy = kotlin.math.sin(angle) * 90f,
                            life = 0.35f,
                            maxLife = 0.35f,
                            colorHex = 0xFFD94E4E,
                            radius = 6f
                        )
                    )
                }
            }
            e.copy(
                x = nx,
                y = ny,
                hitFlashTimer = max(0f, e.hitFlashTimer - dt),
                attackCooldown = cd,
                knockX = e.knockX * (1f - min(1f, dt * 8f)),
                knockY = e.knockY * (1f - min(1f, dt * 8f))
            )
        }

        // Respawn safely at Plaza Infinita if HP hits 0
        if (hp <= 0) {
            val plaza = spawnRegionEntities(
                state.copy(
                    playerX = 900f,
                    playerY = 740f,
                    hp = state.maxHp,
                    stamina = state.maxStamina,
                    mana = state.maxMana,
                    exhausted = false,
                    invulnerableTimer = 1.2f,
                    toastMessage = "Boxy regresó a Plaza Infinita para recuperar su chispa."
                ),
                RegionId.PLAZA_INFINITA
            )
            return plaza
        }

        return state.copy(
            hp = hp,
            invulnerableTimer = invuln,
            activeEnemies = updated,
            floatingTexts = banners,
            particles = particles
        )
    }

    fun triggerAttack(state: GameWorldState): GameWorldState {
        if (state.actionTimer > 0f) return state
        val staminaCost = 14f
        val nextStamina = max(0f, state.stamina - staminaCost)
        val exhaustedNow = nextStamina <= 0f
        val (dx, dy) = dirVector(state.direction)
        val attackX = state.playerX + dx * 58f
        val attackY = state.playerY + dy * 58f
        return applyDamageInArea(
            state.copy(
                animState = PlayerAnimState.ATTACK,
                actionTimer = 0.24f,
                actionDuration = 0.24f,
                stamina = nextStamina,
                exhausted = state.exhausted || exhaustedNow,
                toastMessage = if (exhaustedNow) "¡Boxy está cansado! Toma Café B&W o descansa." else state.toastMessage
            ),
            centerX = attackX,
            centerY = attackY,
            radius = 78f,
            damage = 28
        )
    }

    fun triggerDodge(state: GameWorldState): GameWorldState {
        if (state.actionTimer > 0f || state.exhausted) {
            return if (state.exhausted) {
                state.copy(toastMessage = "Sin energía para esquivar. Recupera estamina o bebe algo.")
            } else state
        }
        val cost = 24f
        val nextStamina = max(0f, state.stamina - cost)
        val exhaustedNow = nextStamina <= 0f
        return state.copy(
            animState = PlayerAnimState.DODGE,
            actionTimer = 0.26f,
            actionDuration = 0.26f,
            invulnerableTimer = 0.32f,
            stamina = nextStamina,
            exhausted = exhaustedNow,
            toastMessage = if (exhaustedNow) "¡Energía agotada! Boxy entró en estado de cansancio." else null
        )
    }

    fun triggerFoskayAbility(state: GameWorldState): GameWorldState {
        if (state.actionTimer > 0f) return state
        if (state.mana < 18f) {
            return state.copy(toastMessage = "Poca Magia para invocar a Foskay. Usa una Poción.")
        }

        val region = MokayRegionsCatalog.regions.getValue(state.currentRegionId)
        val progress = state.activePuzzleProgress.toMutableMap()
        val solved = state.solvedPuzzleIds.toMutableSet()
        val books = state.collectedBookIds.toMutableSet()
        val keywords = state.discoveredKeywords.toMutableSet()
        val forces = state.collectedForces.toMutableSet()
        val banners = state.floatingTexts.toMutableList()
        var litTorch = false

        // 1. Check nearby Torch Puzzle first
        region.puzzles.filter { it.type == PuzzleType.TORCH_SEQUENCE && it.id !in solved }.forEach { puz ->
            val currentSet = (progress[puz.id] ?: emptySet()).toMutableSet()
            val expectedOrder = currentSet.size + 1
            val nearNode = puz.nodes.firstOrNull { node ->
                hypot((node.x - state.playerX).toDouble(), (node.y - state.playerY).toDouble()) < 90.0
            }
            if (nearNode != null) {
                litTorch = true
                if (nearNode.orderIndex == expectedOrder) {
                    currentSet.add(nearNode.id)
                    banners.add(FloatingBanner("Antorcha $expectedOrder encendida por Foskay!", nearNode.x, nearNode.y - 30f, 1.8f, 0xFFFF6A3D))
                } else if (nearNode.id !in currentSet) {
                    currentSet.clear()
                    banners.add(FloatingBanner("Orden incorrecto. ¡Empieza por la 1!", nearNode.x, nearNode.y - 30f, 1.8f, 0xFFD94E4E))
                }
                progress[puz.id] = currentSet
                if (currentSet.size == puz.nodes.size) {
                    solved.add(puz.id)
                    puz.rewardBookId?.let { bid ->
                        MokayWorldCatalog.books[bid]?.let { book ->
                            books.add(book.id)
                            keywords.add(book.keyword)
                            book.forceGranted?.let { forces.add(it) }
                            banners.add(FloatingBanner("¡${book.title} desbloqueado! Clave: ${book.keyword}", state.playerX, state.playerY - 46f, 2.8f, 0xFFFFC24D))
                        }
                    }
                }
            }
        }

        val inDarkRegion = region.theme.darknessAlpha > 0.1f
        val nextState = state.copy(
            mana = max(0f, state.mana - 18f),
            animState = if (inDarkRegion && !litTorch) PlayerAnimState.FOSKAY_LIGHT else PlayerAnimState.FOSKAY_FIRE,
            actionTimer = 0.34f,
            actionDuration = 0.34f,
            foskayActiveTimer = 0.85f,
            foskayLightTimer = if (inDarkRegion) 14f else max(state.foskayLightTimer, 6f),
            activePuzzleProgress = progress,
            solvedPuzzleIds = solved,
            collectedBookIds = books,
            discoveredKeywords = keywords,
            collectedForces = forces,
            floatingTexts = banners
        )

        return applyDamageInArea(
            nextState,
            centerX = state.playerX,
            centerY = state.playerY,
            radius = 140f,
            damage = 42,
            isFire = true
        )
    }

    fun consumeItem(state: GameWorldState, itemId: String): GameWorldState {
        val qty = state.inventory[itemId] ?: 0
        if (qty <= 0) return state.copy(toastMessage = "No te quedan unidades de ese objeto.")
        val spec = MokayWorldCatalog.items[itemId] ?: return state
        if (spec.hpRestore == 0 && spec.staminaRestore == 0 && spec.manaRestore == 0) return state

        val nextInv = state.inventory.toMutableMap()
        if (qty <= 1) nextInv.remove(itemId) else nextInv[itemId] = qty - 1

        val nextHp = min(state.maxHp, state.hp + spec.hpRestore)
        val nextStam = min(state.maxStamina, state.stamina + spec.staminaRestore)
        val nextMana = min(state.maxMana, state.mana + spec.manaRestore)
        val nextExhausted = if (spec.clearsFatigue) false else state.exhausted

        val anim = when {
            spec.manaRestore > 0 -> PlayerAnimState.POTION
            spec.isDrink -> PlayerAnimState.DRINK
            else -> PlayerAnimState.EAT
        }
        val color = when (anim) {
            PlayerAnimState.POTION -> 0xFF6C63FFL
            PlayerAnimState.DRINK -> 0xFF4CC37AL
            else -> 0xFFD94E4EL
        }
        val burst = List(10) { idx ->
            val a = idx * 0.628f
            ActiveParticle(
                x = state.playerX + kotlin.math.cos(a) * 14f,
                y = state.playerY + kotlin.math.sin(a) * 10f,
                vx = kotlin.math.cos(a) * 28f,
                vy = -48f - (idx % 3) * 18f,
                life = 0.65f,
                maxLife = 0.65f,
                colorHex = color,
                radius = 5.5f
            )
        }

        return state.copy(
            inventory = nextInv,
            hp = nextHp,
            stamina = nextStam,
            mana = nextMana,
            exhausted = nextExhausted,
            animState = anim,
            actionTimer = 0.42f,
            actionDuration = 0.42f,
            particles = state.particles + burst,
            floatingTexts = state.floatingTexts + FloatingBanner(spec.name, state.playerX, state.playerY - 36f, 1.5f, color)
        )
    }

    private fun applyDamageInArea(
        state: GameWorldState,
        centerX: Float,
        centerY: Float,
        radius: Float,
        damage: Int,
        isFire: Boolean = false
    ): GameWorldState {
        val inv = state.inventory.toMutableMap()
        val books = state.collectedBookIds.toMutableSet()
        val keywords = state.discoveredKeywords.toMutableSet()
        val forces = state.collectedForces.toMutableSet()
        val defeatedStages = state.defeatedBossStages.toMutableSet()
        val banners = state.floatingTexts.toMutableList()
        val particles = state.particles.toMutableList()
        var hitAny = false
        var chispasEarned = 0

        val survivors = mutableListOf<ActiveEnemy>()
        state.activeEnemies.forEach { enemy ->
            val d = hypot((enemy.x - centerX).toDouble(), (enemy.y - centerY).toDouble()).toFloat()
            if (d <= radius) {
                hitAny = true
                val newHp = enemy.hp - damage
                val dx = if (d > 1f) (enemy.x - state.playerX) / d else 1f
                val dy = if (d > 1f) (enemy.y - state.playerY) / d else 0f
                banners.add(FloatingBanner("-$damage", enemy.x, enemy.y - 32f, 0.9f, if (isFire) 0xFFFF6A3D else 0xFFFFC24D))
                repeat(7) { i ->
                    val ang = i * 0.9f
                    particles.add(
                        ActiveParticle(
                            x = enemy.x,
                            y = enemy.y,
                            vx = kotlin.math.cos(ang) * 110f,
                            vy = kotlin.math.sin(ang) * 110f,
                            life = 0.35f,
                            maxLife = 0.35f,
                            colorHex = if (isFire) 0xFFFF6A3DL else 0xFFFFC24DL,
                            radius = 6f
                        )
                    )
                }

                if (newHp <= 0) {
                    val chispaDrop = if (enemy.isBoss) enemy.tier * 65 else 18 + enemy.tier * 6
                    chispasEarned += chispaDrop
                    banners.add(FloatingBanner("+$chispaDrop Chispas", enemy.x, enemy.y - 38f, 1.8f, 0xFFFFC24D))
                    enemy.dropItemId?.let { itemId ->
                        inv[itemId] = (inv[itemId] ?: 0) + 1
                        val itemName = MokayWorldCatalog.items[itemId]?.name ?: itemId
                        banners.add(FloatingBanner("+$itemName", enemy.x, enemy.y - 54f, 2.2f, 0xFF4CC37A))
                    }
                    enemy.dropBookId?.let { bookId ->
                        MokayWorldCatalog.books[bookId]?.let { book ->
                            books.add(book.id)
                            keywords.add(book.keyword)
                            book.forceGranted?.let { forces.add(it) }
                            banners.add(FloatingBanner("¡Libro! Clave: ${book.keyword}", enemy.x, enemy.y - 76f, 2.8f, 0xFFFFC24D))
                        }
                    }
                } else {
                    survivors.add(
                        enemy.copy(
                            hp = newHp,
                            hitFlashTimer = 0.18f,
                            knockX = dx * 220f,
                            knockY = dy * 220f
                        )
                    )
                }
            } else {
                survivors.add(enemy)
            }
        }

        // Check if any BossStage in current region is now completely cleared
        val region = MokayRegionsCatalog.regions.getValue(state.currentRegionId)
        region.enemies.mapNotNull { it.bossStageId }.distinct().forEach { stageId ->
            if (survivors.none { it.bossStageId == stageId }) {
                defeatedStages.add(stageId)
            }
        }

        return state.copy(
            hitStopTimer = if (hitAny) 0.045f else 0f,
            activeEnemies = survivors,
            inventory = inv,
            chispas = state.chispas + chispasEarned,
            collectedBookIds = books,
            discoveredKeywords = keywords,
            collectedForces = forces,
            defeatedBossStages = defeatedStages,
            floatingTexts = banners,
            particles = particles,
            hapticTick = if (hitAny) state.hapticTick + 1 else state.hapticTick
        )
    }

    fun isQuestReadyToComplete(state: GameWorldState, quest: QuestSpec): Boolean {
        if (quest.id in state.completedQuests) return false
        val hasItem = quest.requiredItemId == null || (state.inventory[quest.requiredItemId] ?: 0) >= quest.requiredCount
        val hasBook = quest.requiredBookId == null || quest.requiredBookId in state.collectedBookIds
        val hasBoss = quest.requiredBossStageId == null || quest.requiredBossStageId in state.defeatedBossStages
        val hasPuzzle = quest.requiredPuzzleId == null || quest.requiredPuzzleId in state.solvedPuzzleIds
        val hasRegion = quest.requiredRegionVisit == null || quest.requiredRegionVisit in state.discoveredRegions
        val hasForces = quest.requiredForces.all { it in state.collectedForces }
        return hasItem && hasBook && hasBoss && hasPuzzle && hasRegion && hasForces
    }

    fun acceptQuest(state: GameWorldState, questId: String): GameWorldState {
        val quest = MokayWorldCatalog.quests.firstOrNull { it.id == questId } ?: return state
        if (quest.id in state.completedQuests || quest.id in state.activeQuestIds) return state
        return state.copy(
            activeQuestIds = state.activeQuestIds + quest.id,
            trackedQuestId = quest.id,
            toastMessage = "Misión aceptada: ${quest.title}"
        )
    }

    fun completeQuest(state: GameWorldState, questId: String): GameWorldState {
        val quest = MokayWorldCatalog.quests.firstOrNull { it.id == questId } ?: return state
        if (!isQuestReadyToComplete(state, quest)) return state

        val inv = state.inventory.toMutableMap()
        if (quest.consumeRequiredItem && quest.requiredItemId != null) {
            val rem = (inv[quest.requiredItemId] ?: 0) - quest.requiredCount
            if (rem <= 0) inv.remove(quest.requiredItemId) else inv[quest.requiredItemId] = rem
        }
        inv[quest.rewardItemId] = (inv[quest.rewardItemId] ?: 0) + quest.rewardQuantity

        val nextMaxHp = (state.maxHp + quest.rewardMaxHpBoost).coerceAtMost(300)
        val nextMaxStam = (state.maxStamina + quest.rewardMaxStaminaBoost).coerceAtMost(260f)
        val nextMaxMana = (state.maxMana + quest.rewardMaxManaBoost).coerceAtMost(260f)
        val nextForces = if (quest.rewardForce != null) state.collectedForces + quest.rewardForce else state.collectedForces
        val nextCompleted = state.completedQuests + quest.id
        val nextActive = state.activeQuestIds - quest.id
        val nextTracked = nextActive.firstOrNull()
            ?: MokayWorldCatalog.quests.firstOrNull { it.id !in nextCompleted }?.id
            ?: quest.id

        return state.copy(
            activeQuestIds = nextActive,
            completedQuests = nextCompleted,
            trackedQuestId = nextTracked,
            inventory = inv,
            chispas = state.chispas + quest.rewardChispas,
            collectedForces = nextForces,
            maxHp = nextMaxHp,
            hp = min(nextMaxHp, state.hp + quest.rewardMaxHpBoost),
            maxStamina = nextMaxStam,
            stamina = min(nextMaxStam, state.stamina + quest.rewardMaxStaminaBoost),
            maxMana = nextMaxMana,
            mana = min(nextMaxMana, state.mana + quest.rewardMaxManaBoost),
            toastMessage = "¡Misión completada: ${quest.title}! (+${quest.rewardChispas} Chispas)",
            hapticTick = state.hapticTick + 1
        )
    }

    fun purchaseShopOffer(
        state: GameWorldState,
        offerId: String,
        discountPercent: Int = 0
    ): GameWorldState {
        val offer = MokayWorldCatalog.shopOffers.firstOrNull { it.id == offerId } ?: return state
        val effectivePrice = (offer.priceChispas * (100 - discountPercent.coerceIn(0, 50)) / 100)
            .coerceAtLeast(1)
        if (state.chispas < effectivePrice) {
            return state.copy(toastMessage = "Faltan Chispas ($effectivePrice requeridas).")
        }
        if (offer.itemId != null) {
            val inv = state.inventory.toMutableMap()
            inv[offer.itemId] = (inv[offer.itemId] ?: 0) + offer.quantity
            return state.copy(
                chispas = state.chispas - effectivePrice,
                inventory = inv,
                toastMessage = "Comprado: x${offer.quantity} ${offer.title}",
                hapticTick = state.hapticTick + 1
            )
        }
        return when (offer.upgradeKind) {
            ShopUpgradeKind.MAX_HP -> {
                if (state.maxHp >= 220) return state.copy(toastMessage = "Ya alcanzaste el máximo de Vida de tienda.")
                val newMax = min(220, state.maxHp + offer.upgradeAmount)
                state.copy(
                    chispas = state.chispas - effectivePrice,
                    maxHp = newMax,
                    hp = newMax,
                    toastMessage = "¡Vida máxima aumentada a $newMax!",
                    hapticTick = state.hapticTick + 1
                )
            }
            ShopUpgradeKind.MAX_STAMINA -> {
                if (state.maxStamina >= 200f) return state.copy(toastMessage = "Ya alcanzaste el máximo de Estamina de tienda.")
                val newMax = min(200f, state.maxStamina + offer.upgradeAmount)
                state.copy(
                    chispas = state.chispas - effectivePrice,
                    maxStamina = newMax,
                    stamina = newMax,
                    toastMessage = "¡Estamina máxima aumentada a ${newMax.toInt()}!",
                    hapticTick = state.hapticTick + 1
                )
            }
            ShopUpgradeKind.MAX_MANA -> {
                if (state.maxMana >= 200f) return state.copy(toastMessage = "Ya alcanzaste el máximo de Magia de tienda.")
                val newMax = min(200f, state.maxMana + offer.upgradeAmount)
                state.copy(
                    chispas = state.chispas - effectivePrice,
                    maxMana = newMax,
                    mana = newMax,
                    toastMessage = "¡Magia máxima aumentada a ${newMax.toInt()}!",
                    hapticTick = state.hapticTick + 1
                )
            }
            null -> state
        }
    }

    fun discardItem(state: GameWorldState, itemId: String, amount: Int = 1): GameWorldState {
        val spec = MokayWorldCatalog.items[itemId] ?: return state
        if (spec.category == com.example.game.model.ItemCategory.QUEST) {
            return state.copy(toastMessage = "Las reliquias de misión no se pueden descartar.")
        }
        val current = state.inventory[itemId] ?: 0
        if (current <= 0) return state
        val inv = state.inventory.toMutableMap()
        val rem = current - amount
        if (rem <= 0) inv.remove(itemId) else inv[itemId] = rem
        return state.copy(
            inventory = inv,
            toastMessage = "Descartado: ${spec.name}"
        )
    }

    private fun dirVector(dir: Direction): Pair<Float, Float> = when (dir) {
        Direction.FRONT -> 0f to 1f
        Direction.BACK -> 0f to -1f
        Direction.LEFT -> -1f to 0f
        Direction.RIGHT -> 1f to 0f
    }
}
