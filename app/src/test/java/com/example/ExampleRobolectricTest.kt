package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.firebase.GameSaveDocument
import com.example.game.data.MokayRegionsCatalog
import com.example.game.data.MokayWorldCatalog
import com.example.game.engine.AchievementCategory
import com.example.game.engine.AchievementManager
import com.example.game.engine.CraftExecutionResult
import com.example.game.engine.CraftingManager
import com.example.game.engine.DialogueManager
import com.example.game.engine.GameLoopEngine
import com.example.game.engine.GameWorldState
import com.example.game.model.EnemyVisual
import com.example.game.model.RegionId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.hypot

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Mokay World", appName)
    }

    @Test
    fun `all 11 mokaya regions 3 boss stages 6 books 6 recipes and 13 quests are configured`() {
        assertEquals(11, MokayRegionsCatalog.regions.size)

        val tintopolis = MokayRegionsCatalog.regions.getValue(RegionId.TINTOPOLIS)
        assertTrue(tintopolis.enemies.any { it.visual == EnemyVisual.BOSS_LAUGHING_CAT && it.bossStageId == "boss_1_cat" })

        val rhythm = MokayRegionsCatalog.regions.getValue(RegionId.RHYTHM_HOOD)
        assertTrue(rhythm.enemies.any { it.visual == EnemyVisual.BOSS_DINAMITA && it.bossStageId == "boss_2_duo" })
        assertTrue(rhythm.enemies.any { it.visual == EnemyVisual.BOSS_MOLOTOV && it.bossStageId == "boss_2_duo" })

        val sombravia = MokayRegionsCatalog.regions.getValue(RegionId.SOMBRAVIA)
        assertTrue(sombravia.enemies.any { it.visual == EnemyVisual.BOSS_LAUGHING_CAT && it.bossStageId == "boss_3_cat_dino" })
        assertTrue(sombravia.enemies.any { it.visual == EnemyVisual.BOSS_STREET_DINO && it.bossStageId == "boss_3_cat_dino" })

        assertEquals(6, MokayWorldCatalog.books.size)
        assertEquals(6, MokayWorldCatalog.recipes.size)
        assertEquals(13, MokayWorldCatalog.quests.size)

        val questRegions = MokayWorldCatalog.quests.map { it.regionId }.toSet()
        assertEquals(RegionId.entries.toSet(), questRegions)
    }

    @Test
    fun `npcs across all 11 regions are spaced into distinct villages without clustering`() {
        MokayRegionsCatalog.regions.values.forEach { reg ->
            assertTrue("${reg.id} must have >= 3 NPCs", reg.npcs.size >= 3)
            reg.npcs.forEach { npc ->
                assertTrue("NPC ${npc.id} in ${reg.id} must have villageName", npc.villageName.isNotBlank())
            }
            for (i in reg.npcs.indices) {
                for (j in i + 1 until reg.npcs.size) {
                    val a = reg.npcs[i]
                    val b = reg.npcs[j]
                    val dist = hypot((a.x - b.x).toDouble(), (a.y - b.y).toDouble()).toFloat()
                    assertTrue(
                        "NPCs ${a.id} and ${b.id} in ${reg.id} are too close ($dist < 230f)",
                        dist >= 230f
                    )
                }
            }
        }
    }

    @Test
    fun `crafting manager evaluates materials executes batch crafts and blocks missing ingredients`() {
        val manager = CraftingManager()
        val state = GameWorldState(
            inventory = mapOf(
                "tinta_antigua" to 2,
                "alga_luminosa" to 3
            )
        )

        val materials = manager.getCollectedMaterials(state.inventory)
        assertTrue(materials.any { (spec, qty) -> spec.id == "tinta_antigua" && qty == 2 })

        val statuses = manager.evaluateRecipes(state.inventory, currentRegionTier = 2)
        val cafeStatus = statuses.first { it.recipe.id == "recipe_cafe" }
        assertTrue(cafeStatus.canCraft)
        assertEquals(2, cafeStatus.craftableBatches)

        val batchResult = manager.craft(state, "recipe_cafe", batches = 2)
        assertTrue(batchResult is CraftExecutionResult.Success)
        val craftedState = (batchResult as CraftExecutionResult.Success).nextState
        assertEquals(4, craftedState.inventory["cafe_bw"])
        assertEquals(0, craftedState.inventory["tinta_antigua"] ?: 0)
        assertEquals(1, craftedState.inventory["alga_luminosa"] ?: 0)

        val failResult = manager.craft(craftedState, "recipe_esencia", batches = 1)
        assertTrue(failResult is CraftExecutionResult.Failure)
    }

    @Test
    fun `quests shop purchases and discard item behave correctly`() {
        val base = GameWorldState(
            chispas = 150,
            inventory = mapOf(
                "slice_pizza" to 2,
                "llave_tinta" to 1,
                "tinta_antigua" to 2
            ),
            collectedBookIds = setOf("libro_tinta"),
            defeatedBossStages = setOf("boss_1_cat")
        )

        val accepted = GameLoopEngine.acceptQuest(base, "q_tinto_press")
        assertTrue("q_tinto_press" in accepted.activeQuestIds)
        assertEquals("q_tinto_press", accepted.trackedQuestId)

        val completed = GameLoopEngine.completeQuest(accepted, "q_tinto_press")
        assertTrue("q_tinto_press" in completed.completedQuests)
        assertFalse("tinta_antigua" in completed.inventory)
        assertTrue(completed.maxMana > base.maxMana)
        assertTrue(completed.chispas > base.chispas)

        val afterBuy = GameLoopEngine.purchaseShopOffer(completed, "shop_upg_hp")
        assertEquals(120, afterBuy.maxHp)
        assertTrue(afterBuy.chispas < completed.chispas)

        val afterDiscardSlice = GameLoopEngine.discardItem(afterBuy, "slice_pizza")
        assertEquals(1, afterDiscardSlice.inventory["slice_pizza"])

        val protectQuestRelic = GameLoopEngine.discardItem(afterDiscardSlice, "llave_tinta")
        assertEquals(1, protectQuestRelic.inventory["llave_tinta"])
    }

    @Test
    fun `game save document maps match firestore rules schema fields`() {
        val doc = GameSaveDocument(userId = "user_123")
        val createMap = doc.toCreateMap("user_123")
        val updateMap = doc.toUpdateMap()

        assertFalse("id" in createMap)
        assertFalse("id" in updateMap)
        assertTrue("userId" in createMap)
        assertTrue("createdAt" in createMap)
        assertFalse("userId" in updateMap)
        assertFalse("createdAt" in updateMap)

        listOf(
            "currentRegionId", "playerX", "playerY",
            "hp", "maxHp", "stamina", "maxStamina", "mana", "maxMana",
            "chispas", "inventory", "quickSlots",
            "collectedBookIds", "discoveredKeywords", "discoveredRegions",
            "discoveredLandmarkIds", "visitedVillages",
            "activeQuestIds", "completedQuests", "trackedQuestId",
            "solvedPuzzleIds", "defeatedBossStages",
            "unlockedGateIds", "collectedForces", "collectedPickupIds",
            "npcRelationships", "unlockedAchievementIds",
            "updatedAt"
        ).forEach { key ->
            assertTrue("Create map missing $key", key in createMap)
            assertTrue("Update map missing $key", key in updateMap)
        }
    }

    @Test
    fun `world map exploration tracks discovered landmarks and visited villages across regions`() {
        val plaza = MokayRegionsCatalog.regions.getValue(RegionId.PLAZA_INFINITA)
        val firstNpc = plaza.npcs.first { it.villageName.isNotBlank() }
        val targetLandmark = plaza.landmarks.last()

        // Walking onto a village registers it in visitedVillages
        val nearVillageState = GameLoopEngine.spawnRegionEntities(
            GameWorldState(playerX = firstNpc.x, playerY = firstNpc.y),
            RegionId.PLAZA_INFINITA
        )
        val steppedVillage = GameLoopEngine.step(
            nearVillageState,
            com.example.game.engine.PlayerInput(moveX = 0f, moveY = 0f),
            0.02f
        )
        assertTrue(firstNpc.villageName in steppedVillage.visitedVillages)

        // Walking onto a landmark registers it in discoveredLandmarkIds
        val nearLandmarkState = steppedVillage.copy(
            playerX = targetLandmark.x,
            playerY = targetLandmark.y
        )
        val steppedLandmark = GameLoopEngine.step(
            nearLandmarkState,
            com.example.game.engine.PlayerInput(moveX = 0f, moveY = 0f),
            0.02f
        )
        assertTrue(targetLandmark.id in steppedLandmark.discoveredLandmarkIds)

        // All 11 regions expose landmarks and unique non-blank village names
        val allVillages = mutableSetOf<String>()
        MokayRegionsCatalog.regions.values.forEach { reg ->
            assertTrue("${reg.id} must have landmarks", reg.landmarks.isNotEmpty())
            val regVillages = reg.npcs.map { it.villageName }
            assertTrue("${reg.id} must have villages", regVillages.isNotEmpty())
            regVillages.forEach { v ->
                assertTrue("Village name must be unique ($v)", allVillages.add(v))
            }
        }
    }

    @Test
    fun `stamina exhaustion triggers fatigue and coffee restores it`() {
        var state = GameLoopEngine.spawnRegionEntities(
            GameWorldState(stamina = 20f),
            RegionId.PLAZA_INFINITA
        )
        state = GameLoopEngine.triggerDodge(state)
        assertTrue(state.exhausted)
        assertEquals(0f, state.stamina, 0.01f)

        state = GameLoopEngine.consumeItem(state, "cafe_bw")
        assertFalse(state.exhausted)
        assertTrue(state.stamina >= 60f)
    }

    @Test
    fun `foskay ability spends mana and lights dark regions`() {
        val state = GameLoopEngine.spawnRegionEntities(
            GameWorldState(mana = 50f),
            RegionId.SOMBRAVIA
        )
        val after = GameLoopEngine.triggerFoskayAbility(state)
        assertTrue(after.mana < 50f)
        assertTrue(after.foskayLightTimer > 0f)
        assertTrue(after.foskayActiveTimer > 0f)

        val stepped = GameLoopEngine.step(
            after,
            com.example.game.engine.PlayerInput(moveX = 1f, moveY = 0f),
            0.03f
        )
        assertEquals(stepped.playerX, stepped.foskayX, 0.01f)
        assertEquals(stepped.playerY, stepped.foskayY, 0.01f)
    }

    @Test
    fun `all 11 regions have dense houses flora props and clear walkable spawns`() {
        fun overlapsHouse(x: Float, y: Float, h: com.example.game.model.HouseSpec): Boolean {
            val halfW = h.width * 0.44f + 16f
            val halfH = h.height * 0.34f + 14f
            return kotlin.math.abs(x - h.x) < halfW && kotlin.math.abs(y - h.y) < halfH
        }

        MokayRegionsCatalog.regions.values.forEach { reg ->
            assertTrue("${reg.id} must have >=4 houses", reg.houses.size >= 4)
            assertTrue("${reg.id} must have >=12 flora", reg.flora.size >= 12)
            assertTrue("${reg.id} must have >=6 props", reg.props.size >= 6)

            reg.houses.forEach { house ->
                reg.npcs.forEach { npc ->
                    assertFalse("NPC ${npc.id} overlaps house ${house.id} in ${reg.id}", overlapsHouse(npc.x, npc.y, house))
                }
                reg.pickups.forEach { p ->
                    assertFalse("Pickup ${p.id} overlaps house ${house.id} in ${reg.id}", overlapsHouse(p.x, p.y, house))
                }
                reg.gates.forEach { g ->
                    assertFalse("Gate ${g.id} overlaps house ${house.id} in ${reg.id}", overlapsHouse(g.x, g.y, house))
                }
                reg.puzzles.flatMap { it.nodes }.forEach { node ->
                    assertFalse("Puzzle node ${node.id} overlaps house ${house.id} in ${reg.id}", overlapsHouse(node.x, node.y, house))
                    assertFalse("Puzzle target ${node.id} overlaps house ${house.id} in ${reg.id}", overlapsHouse(node.targetX, node.targetY, house))
                }
            }
        }

        // Verify all gate destinations spawn clear of houses
        MokayRegionsCatalog.regions.values.flatMap { it.gates }.forEach { gate ->
            val dest = MokayRegionsCatalog.regions.getValue(gate.toRegion)
            dest.houses.forEach { house ->
                assertFalse(
                    "Gate ${gate.id} spawns inside house ${house.id} in ${dest.id}",
                    overlapsHouse(gate.spawnX, gate.spawnY, house)
                )
            }
        }

        // Verify 3:2 panoramic Mokaya district proportions and house/landmark sizing
        MokayRegionsCatalog.regions.values.forEach { reg ->
            assertEquals(1800f, reg.worldWidth, 0.1f)
            assertEquals(1200f, reg.worldHeight, 0.1f)
            assertTrue(reg.houses.all { it.width >= 165f && it.height >= 125f })
            assertTrue(reg.landmarks.all { it.width >= 230f && it.height >= 170f })
        }

        val initialState = GameWorldState()
        val plaza = MokayRegionsCatalog.regions.getValue(RegionId.PLAZA_INFINITA)
        plaza.houses.forEach { h ->
            assertFalse(overlapsHouse(initialState.playerX, initialState.playerY, h))
        }

        // Verify MainActivity is configured for horizontal/landscape orientation
        val context = ApplicationProvider.getApplicationContext<Context>()
        val info = context.packageManager.getActivityInfo(
            android.content.ComponentName(context, MainActivity::class.java),
            0
        )
        assertEquals(
            android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
            info.screenOrientation
        )
    }

    @Test
    fun `npc_dialogues json covers all 41 npcs and resolves every branching node reference`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = DialogueManager.fromAssets(context.assets)

        val allNpcs = MokayRegionsCatalog.regions.values.flatMap { it.npcs }
        assertTrue("Expected at least 41 NPCs", allNpcs.size >= 41)

        allNpcs.forEach { npc ->
            val tree = manager.trees[npc.id]
            assertTrue("Missing JSON dialogue tree for NPC ${npc.id}", tree != null)
            val nonNullTree = tree!!
            assertTrue("Missing startNodeId for ${npc.id}", nonNullTree.startNodeId in nonNullTree.nodes)
            nonNullTree.highAffinityStartNodeId?.let { high ->
                assertTrue("Invalid highAffinityStartNodeId '$high' for ${npc.id}", high in nonNullTree.nodes)
            }
            nonNullTree.lowAffinityStartNodeId?.let { low ->
                assertTrue("Invalid lowAffinityStartNodeId '$low' for ${npc.id}", low in nonNullTree.nodes)
            }
            nonNullTree.nodes.values.forEach { node ->
                assertTrue("Empty text in ${npc.id}:${node.id}", node.text.isNotBlank())
                node.choices.forEach { choice ->
                    choice.nextNodeId?.let { target ->
                        assertTrue(
                            "Broken nextNodeId '$target' in ${npc.id}:${node.id}:${choice.id}",
                            target in nonNullTree.nodes
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `dialogue choices update npc relationship unlock discount prevent chispas farming and adapt greeting`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = DialogueManager.fromAssets(context.assets)
        val llave = MokayRegionsCatalog.regions.getValue(RegionId.PLAZA_INFINITA)
            .npcs.first { it.id == "npc_llave" }

        val tree = manager.getTreeForNpc(llave)
        assertEquals("start", manager.resolveStartNodeId(tree, 0))

        val startNode = manager.getNode(tree, "start")!!
        val positiveChoice = startNode.choices.first { it.id == "llave_foskay" }
        val negativeChoice = startNode.choices.first { it.id == "llave_rush" }

        // First positive choice boosts relationship (+15) and grants Chispas once
        val baseState = GameWorldState(chispas = 100)
        val firstOutcome = manager.applyChoice(baseState, llave, positiveChoice)
        assertEquals(15, firstOutcome.newScore)
        assertEquals(15, firstOutcome.delta)
        assertEquals(10, firstOutcome.awardedChispas)
        assertEquals(110, firstOutcome.nextState.chispas)
        assertEquals("Amistoso", manager.relationshipTier(firstOutcome.newScore).label)

        // Repeating the same chispasBonus choice does not farm extra Chispas
        val repeatOutcome = manager.applyChoice(firstOutcome.nextState, llave, positiveChoice)
        assertEquals(0, repeatOutcome.awardedChispas)
        assertEquals(110, repeatOutcome.nextState.chispas)
        assertEquals(30, repeatOutcome.newScore)
        assertEquals("warm_greeting", manager.resolveStartNodeId(tree, repeatOutcome.newScore))

        // Reaching >= 35 unlocks "Aliado" and 15% shop discount
        val warmNode = manager.getNode(tree, "warm_greeting")!!
        val allyOutcome = manager.applyChoice(repeatOutcome.nextState, llave, warmNode.choices.first())
        assertEquals(35, allyOutcome.newScore)
        assertEquals("Aliado", manager.relationshipTier(allyOutcome.newScore).label)
        assertEquals(15, manager.shopDiscountFor(allyOutcome.newScore))

        // Shop purchase applies 15% discount cleanly (offer shop_pizza = 25 -> 21 Chispas)
        val boughtWithDiscount = GameLoopEngine.purchaseShopOffer(
            allyOutcome.nextState,
            "shop_pizza",
            discountPercent = manager.shopDiscountFor(allyOutcome.newScore)
        )
        assertEquals(110 - 21, boughtWithDiscount.chispas)

        // Negative choice drops score and low affinity triggers guarded_greeting
        val hurtOutcome1 = manager.applyChoice(baseState, llave, negativeChoice)
        val hurtOutcome2 = manager.applyChoice(hurtOutcome1.nextState, llave, negativeChoice)
        assertTrue(hurtOutcome2.newScore <= -15)
        assertEquals("Distante", manager.relationshipTier(hurtOutcome2.newScore).label)
        assertEquals("guarded_greeting", manager.resolveStartNodeId(tree, hurtOutcome2.newScore))
    }

    @Test
    fun `achievement manager tracks exploration puzzles bosses and prevents duplicate rewards`() {
        val manager = AchievementManager()
        val initial = GameWorldState(chispas = 50)

        // Initially 0 achievements unlocked
        val emptyOutcome = manager.evaluateAndUnlock(initial)
        assertTrue(emptyOutcome.newlyUnlocked.isEmpty())
        assertEquals(0, emptyOutcome.awardedChispas)

        // Discover 3 regions and solve 1 puzzle -> unlocks ach_explorer_3 (+25) and ach_puzzle_first (+30)
        val progressed = initial.copy(
            discoveredRegions = setOf(RegionId.PLAZA_INFINITA, RegionId.TINTOPOLIS, RegionId.MAREA_VIVA),
            solvedPuzzleIds = setOf("puz_artelia_torches")
        )
        val firstUnlock = manager.evaluateAndUnlock(progressed)
        assertEquals(2, firstUnlock.newlyUnlocked.size)
        assertEquals(55, firstUnlock.awardedChispas)
        assertEquals(105, firstUnlock.nextState.chispas)
        assertTrue("ach_explorer_3" in firstUnlock.nextState.unlockedAchievementIds)
        assertTrue("ach_puzzle_first" in firstUnlock.nextState.unlockedAchievementIds)

        // Re-evaluating same state grants 0 extra Chispas (idempotent persistence)
        val reEvaluated = manager.evaluateAndUnlock(firstUnlock.nextState)
        assertTrue(reEvaluated.newlyUnlocked.isEmpty())
        assertEquals(105, reEvaluated.nextState.chispas)

        // Category filtering and summary counts behave accurately
        val puzzleProgress = manager.getAchievementsProgress(
            reEvaluated.nextState,
            category = AchievementCategory.PUZZLES
        )
        assertEquals(2, puzzleProgress.size)
        assertTrue(puzzleProgress.first { it.spec.id == "ach_puzzle_first" }.isUnlocked)
        assertFalse(puzzleProgress.first { it.spec.id == "ach_puzzle_all" }.isUnlocked)

        val (unlockedCount, totalCount) = manager.summaryCounts(reEvaluated.nextState)
        assertEquals(2, unlockedCount)
        assertEquals(12, totalCount)
    }
}
