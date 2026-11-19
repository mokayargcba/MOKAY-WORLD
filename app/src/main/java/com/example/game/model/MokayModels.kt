package com.example.game.model

import androidx.compose.ui.graphics.Color

enum class RegionId(val id: String, val title: String, val subtitle: String, val tier: Int) {
    PLAZA_INFINITA("plaza_infinita", "Plaza Infinita", "El Corazón de Mokaya", 1),
    TINTOPOLIS("tintopolis", "Tintópolis", "Distrito Monocroma", 1),
    MAREA_VIVA("marea_viva", "Marea Viva", "Distrito Acuático", 1),
    RHYTHM_HOOD("rhythm_hood", "Rhythm Hood", "Distrito Urbano", 2),
    ARENA_VIVA("arena_viva", "Arena Viva", "Distrito Deportivo", 2),
    PICASIA("picasia", "Picasia", "Distrito Cubista", 2),
    ARTELIA("artelia", "Artelia", "Distrito Renacentista", 2),
    CONSTELARIA("constelaria", "Constelaria", "Sueños e Imaginación", 2),
    SOMBRAVIA("sombravia", "Sombravía", "Sombras Interiores", 3),
    BOSQUE_OLVIDADO("bosque_olvidado", "Bosque Olvidado", "Territorio Restringido", 3),
    ARBOL_ORIGINAL("arbol_original", "El Árbol Original", "Corazón Primigenio", 3);

    companion object {
        fun fromId(raw: String): RegionId =
            entries.firstOrNull { it.id == raw } ?: PLAZA_INFINITA
    }
}

enum class Direction { FRONT, BACK, LEFT, RIGHT }

enum class PlayerAnimState {
    IDLE,
    MOVE,
    ATTACK,
    DODGE,
    FATIGUED,
    EAT,
    DRINK,
    POTION,
    FOSKAY_FIRE,
    FOSKAY_LIGHT,
    READ_BOOK
}

enum class ItemCategory(val label: String) {
    FOOD_DRINK("Alimentos y Bebidas"),
    POTION("Pociones"),
    MATERIAL("Materiales"),
    QUEST("Misión")
}

data class ItemSpec(
    val id: String,
    val name: String,
    val category: ItemCategory,
    val description: String,
    val hpRestore: Int = 0,
    val staminaRestore: Int = 0,
    val manaRestore: Int = 0,
    val clearsFatigue: Boolean = false,
    val isDrink: Boolean = false,
    val tier: Int = 1,
    val colorHex: Long = 0xFFFFC24D
)

data class CraftingRecipe(
    val id: String,
    val resultItemId: String,
    val resultQuantity: Int,
    val requiredTier: Int,
    val ingredients: Map<String, Int>,
    val hint: String
)

data class BookSpec(
    val id: String,
    val title: String,
    val regionId: RegionId,
    val keyword: String,
    val forceGranted: String? = null,
    val excerpt: String
)

enum class LandmarkKind {
    FOUNTAIN,
    CINEMA,
    STUDIO,
    THEATER,
    CAFE,
    PRINT_SHOP,
    CATHEDRAL_LIBRARY,
    OBSERVATORY,
    GALLERY,
    GARDEN,
    STADIUM,
    SKATE_PARK,
    BASKET_COURT,
    BEACH,
    STAGE_SHELL,
    LIGHTHOUSE,
    GROTTO,
    PIER,
    GRAFFITI_WALL,
    TRAIN_YARD,
    MUSEUM,
    ILLUSION_HALL,
    DREAM_PALACE,
    CLOUD_LOOKOUT,
    STORM_TOWER,
    GOTHIC_MANOR,
    MAZE,
    BEACON_TOWER,
    GIANT_ROOTS,
    RUINS,
    GLOWING_LAKE,
    ANCESTRAL_PORTAL,
    CRYSTAL_HEART,
    CREATOR_CROWN,
    WORKSHOP,
    BRIDGE_ARCH
}

data class LandmarkSpec(
    val id: String,
    val name: String,
    val description: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val accentColor: Color,
    val kind: LandmarkKind = LandmarkKind.WORKSHOP
)

enum class HouseStyle {
    TOWNHOUSE,
    CAFE_AWNING,
    RENAISSANCE_VILLA,
    CORAL_HUT,
    NEON_LOFT,
    CUBIST_STUDIO,
    GOTHIC_HOUSE,
    CLOUD_TEMPLE,
    MOSS_SHRINE,
    CRYSTAL_PAVILION
}

data class HouseSpec(
    val id: String,
    val name: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val roofColor: Color,
    val wallColor: Color,
    val style: HouseStyle = HouseStyle.TOWNHOUSE
)

enum class PropKind {
    LAMP,
    BENCH,
    FENCE_H,
    CRATE_STACK,
    BANNER,
    WELL,
    UMBRELLA_TABLE,
    PILLAR,
    SOCCER_GOAL,
    MUSHROOM_CLUSTER
}

data class PropSpec(
    val id: String,
    val x: Float,
    val y: Float,
    val kind: PropKind,
    val scale: Float = 1f,
    val tint: Color = Color(0xFFFFC24D)
)

enum class FloraKind { TREE, CUPHEAD_FLOWER, SHRUB, CORAL, INK_FERN, CRYSTAL_ROOT }

data class LivingFloraSpec(
    val id: String,
    val x: Float,
    val y: Float,
    val kind: FloraKind,
    val scale: Float = 1f,
    val phaseOffset: Float = 0f,
    val tint: Color = Color(0xFF4CC37A)
)

data class WorldPickup(
    val id: String,
    val itemId: String,
    val bookId: String? = null,
    val x: Float,
    val y: Float,
    val quantity: Int = 1,
    val respawns: Boolean = true
)

enum class PuzzleType { PRESSURE_PLATES, TORCH_SEQUENCE, CUBIST_BLOCKS }

data class PuzzleNode(
    val id: String,
    val x: Float,
    val y: Float,
    val targetX: Float = x,
    val targetY: Float = y,
    val orderIndex: Int = 0
)

data class PuzzleSpec(
    val id: String,
    val title: String,
    val regionId: RegionId,
    val type: PuzzleType,
    val hint: String,
    val nodes: List<PuzzleNode>,
    val rewardItemId: String? = null,
    val rewardBookId: String? = null
)

enum class CharacterVisual(val isNamedOriginal: Boolean) {
    // 100% Original Named Characters
    BLOOM(true), FLOKO(true), ZENKAY(true), NUBI(true), BIRDY(true),
    VIVI(true), STAR(true), COIM(true), RATT(true), CLIP(true),
    LA_LLAVE(true), LA_FLOR(true),
    FINN(true), LUMI(true), OCTO(true), CORALY(true), TULA(true),
    SHADE(true), GLITCH(true), STATIC(true), MASKY(true), VOID(true), DOUTY(true),
    LUMEN(true), SPARKY(true), ROOTY(true), NOVA(true),
    // Region-Adapted Citizen Archetypes
    CITIZEN_RATZ(false), CITIZEN_TERRA(false), CITIZEN_PUFF(false),
    CITIZEN_BEEP(false), CITIZEN_SPRAY(false), CITIZEN_SHROOM(false),
    CITIZEN_BREW(false), CITIZEN_SLICE(false), CITIZEN_SPIKE(false)
}

data class NpcSpec(
    val id: String,
    val name: String,
    val role: String,
    val visual: CharacterVisual,
    val regionId: RegionId,
    val x: Float,
    val y: Float,
    val dialogue: String,
    val questId: String? = null
)

enum class EnemyVisual {
    CHISPA_ERRANTE,
    BOX_ANTAGONISTA,
    SOMBRA_MENOR,
    CUBO_REBELDE,
    BOSS_LAUGHING_CAT,
    BOSS_DINAMITA,
    BOSS_MOLOTOV,
    BOSS_STREET_DINO
}

data class EnemySpawn(
    val id: String,
    val name: String,
    val visual: EnemyVisual,
    val x: Float,
    val y: Float,
    val maxHp: Int,
    val attackDamage: Int,
    val speed: Float,
    val tier: Int,
    val isBoss: Boolean = false,
    val bossStageId: String? = null,
    val dropItemId: String? = null,
    val dropBookId: String? = null
)

data class GateSpec(
    val id: String,
    val label: String,
    val fromRegion: RegionId,
    val toRegion: RegionId,
    val x: Float,
    val y: Float,
    val spawnX: Float,
    val spawnY: Float,
    val requiredBookId: String? = null,
    val requiredKeyword: String? = null,
    val requiredQuestItemId: String? = null,
    val requiredBossStageId: String? = null,
    val requiredPuzzleId: String? = null,
    val requiredForces: List<String> = emptyList(),
    val lockedMessage: String = ""
)

data class QuestSpec(
    val id: String,
    val title: String,
    val regionId: RegionId,
    val giverName: String,
    val description: String,
    val requiredItemId: String? = null,
    val requiredCount: Int = 1,
    val requiredBookId: String? = null,
    val requiredBossStageId: String? = null,
    val requiredPuzzleId: String? = null,
    val rewardItemId: String,
    val rewardForce: String? = null
)

data class RegionThemeColors(
    val groundPrimary: Color,
    val groundSecondary: Color,
    val pathColor: Color,
    val waterOrVoid: Color,
    val accentColor: Color,
    val citizenPrimary: Color,
    val citizenOutfit: Color,
    val darknessAlpha: Float = 0f
)

data class RegionSpec(
    val id: RegionId,
    val mapX: Float,
    val mapY: Float,
    val worldWidth: Float,
    val worldHeight: Float,
    val theme: RegionThemeColors,
    val landmarks: List<LandmarkSpec>,
    val flora: List<LivingFloraSpec>,
    val pickups: List<WorldPickup>,
    val puzzles: List<PuzzleSpec>,
    val npcs: List<NpcSpec>,
    val enemies: List<EnemySpawn>,
    val gates: List<GateSpec>,
    val houses: List<HouseSpec> = emptyList(),
    val props: List<PropSpec> = emptyList(),
    val hasTrainTrack: Boolean = false,
    val hasWaterMoat: Boolean = false,
    val hasCloudsBorder: Boolean = false,
    val isSportsZone: Boolean = false,
    val cobblestonePlazaRadius: Float = 210f
)
