package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.game.data.MokayRegionsCatalog
import com.example.game.data.MokayWorldCatalog
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
    fun `all 11 mokaya regions and 3 boss stages are configured`() {
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
        assertEquals(5, MokayWorldCatalog.recipes.size)
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
}
