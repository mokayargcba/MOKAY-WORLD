package com.example.game.data

import com.example.game.model.FloraKind
import com.example.game.model.RegionId
import com.example.game.model.RegionSpec

object MokayRegionsCatalog {
    private const val MOKAYA_TARGET_WIDTH = 1800f
    private const val MOKAYA_TARGET_HEIGHT = 1200f

    private val rawRegions: Map<RegionId, RegionSpec> = (
        buildTier1Regions() +
            buildTier2Regions() +
            buildTier3Regions()
        ).associateBy { it.id }

    // Canonical world map coordinates matching the official Mokaya Archipelago map
    private val canonicalMapCoords: Map<RegionId, Pair<Float, Float>> = mapOf(
        RegionId.CONSTELARIA to (0.50f to 0.11f),
        RegionId.TINTOPOLIS to (0.29f to 0.18f),
        RegionId.ARTELIA to (0.71f to 0.18f),
        RegionId.RHYTHM_HOOD to (0.17f to 0.34f),
        RegionId.PLAZA_INFINITA to (0.50f to 0.34f),
        RegionId.PICASIA to (0.83f to 0.34f),
        RegionId.ARENA_VIVA to (0.26f to 0.49f),
        RegionId.MAREA_VIVA to (0.50f to 0.50f),
        RegionId.SOMBRAVIA to (0.74f to 0.49f),
        RegionId.BOSQUE_OLVIDADO to (0.50f to 0.68f),
        RegionId.ARBOL_ORIGINAL to (0.50f to 0.88f)
    )

    val regions: Map<RegionId, RegionSpec> = rawRegions.mapValues { (id, reg) ->
        scaleToMokayaProportions(reg, rawRegions, canonicalMapCoords.getValue(id))
    }

    private fun scaleToMokayaProportions(
        reg: RegionSpec,
        sourceMap: Map<RegionId, RegionSpec>,
        mapPos: Pair<Float, Float>
    ): RegionSpec {
        val sx = MOKAYA_TARGET_WIDTH / reg.worldWidth
        val sy = MOKAYA_TARGET_HEIGHT / reg.worldHeight

        return reg.copy(
            mapX = mapPos.first,
            mapY = mapPos.second,
            worldWidth = MOKAYA_TARGET_WIDTH,
            worldHeight = MOKAYA_TARGET_HEIGHT,
            cobblestonePlazaRadius = reg.cobblestonePlazaRadius * 1.38f,
            landmarks = reg.landmarks.map { lm ->
                lm.copy(
                    x = lm.x * sx,
                    y = lm.y * sy,
                    width = lm.width * 1.56f,
                    height = lm.height * 1.48f
                )
            },
            houses = reg.houses.map { h ->
                h.copy(
                    x = h.x * sx,
                    y = h.y * sy,
                    width = h.width * 1.54f,
                    height = h.height * 1.48f
                )
            },
            props = reg.props.map { p ->
                p.copy(
                    x = p.x * sx,
                    y = p.y * sy,
                    scale = p.scale * 1.20f
                )
            },
            flora = reg.flora.map { f ->
                val floraBoost = when (f.kind) {
                    FloraKind.TREE -> 1.46f
                    FloraKind.CRYSTAL_ROOT -> 1.30f
                    else -> 1.18f
                }
                f.copy(
                    x = f.x * sx,
                    y = f.y * sy,
                    scale = f.scale * floraBoost
                )
            },
            pickups = reg.pickups.map { pk ->
                pk.copy(x = pk.x * sx, y = pk.y * sy)
            },
            puzzles = reg.puzzles.map { puz ->
                puz.copy(
                    nodes = puz.nodes.map { n ->
                        n.copy(
                            x = n.x * sx,
                            y = n.y * sy,
                            targetX = n.targetX * sx,
                            targetY = n.targetY * sy
                        )
                    }
                )
            },
            npcs = reg.npcs.map { npc ->
                npc.copy(x = npc.x * sx, y = npc.y * sy)
            },
            enemies = reg.enemies.map { en ->
                en.copy(
                    x = en.x * sx,
                    y = en.y * sy,
                    speed = en.speed * 1.12f
                )
            },
            gates = reg.gates.map { g ->
                val dest = sourceMap.getValue(g.toRegion)
                val dsx = MOKAYA_TARGET_WIDTH / dest.worldWidth
                val dsy = MOKAYA_TARGET_HEIGHT / dest.worldHeight
                g.copy(
                    x = g.x * sx,
                    y = g.y * sy,
                    spawnX = g.spawnX * dsx,
                    spawnY = g.spawnY * dsy
                )
            }
        )
    }
}
