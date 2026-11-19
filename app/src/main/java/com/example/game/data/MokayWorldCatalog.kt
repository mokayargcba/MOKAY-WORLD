package com.example.game.data

import com.example.game.model.BookSpec
import com.example.game.model.CraftingRecipe
import com.example.game.model.ItemCategory
import com.example.game.model.ItemSpec
import com.example.game.model.QuestSpec
import com.example.game.model.RegionId

object MokayWorldCatalog {
    val items: Map<String, ItemSpec> = listOf(
        // Foods & Drinks (Vitality & Stamina / Anti-Fatigue)
        ItemSpec(
            id = "slice_pizza",
            name = "Porción Slice",
            category = ItemCategory.FOOD_DRINK,
            description = "Pizza crocante de Rhythm Hood. Recupera 35 de Vida y 20 de Estamina.",
            hpRestore = 35,
            staminaRestore = 20,
            tier = 1,
            colorHex = 0xFFFF9F1C
        ),
        ItemSpec(
            id = "fruto_creacion",
            name = "Fruto de Creación",
            category = ItemCategory.FOOD_DRINK,
            description = "Fruto luminoso de las raíces. Recupera 70 de Vida y 35 de Estamina.",
            hpRestore = 70,
            staminaRestore = 35,
            clearsFatigue = true,
            tier = 2,
            colorHex = 0xFFE97893
        ),
        ItemSpec(
            id = "cafe_bw",
            name = "Café B&W",
            category = ItemCategory.FOOD_DRINK,
            description = "Bebida clásica de Tintópolis. Quita el cansancio y restaura 65 de Estamina.",
            staminaRestore = 65,
            clearsFatigue = true,
            isDrink = true,
            tier = 1,
            colorHex = 0xFF8D6E63
        ),
        ItemSpec(
            id = "te_zen",
            name = "Té Zen de Zenkay",
            category = ItemCategory.FOOD_DRINK,
            description = "Infusión equilibrada. Restaura 100 de Estamina, 25 de Vida y quita la fatiga.",
            hpRestore = 25,
            staminaRestore = 100,
            clearsFatigue = true,
            isDrink = true,
            tier = 2,
            colorHex = 0xFF4CC37A
        ),
        // Magic Potions (Mana for Foskay!)
        ItemSpec(
            id = "elixir_chispa",
            name = "Elixir de Chispa",
            category = ItemCategory.POTION,
            description = "Poción mágica ámbar para invocar a Foskay (+55 de Magia).",
            manaRestore = 55,
            tier = 1,
            colorHex = 0xFF6C63FF
        ),
        ItemSpec(
            id = "esencia_cristal",
            name = "Poción Estelar",
            category = ItemCategory.POTION,
            description = "Poción maestra de Artelia y Constelaria (+100 Magia y +20 Vida).",
            hpRestore = 20,
            manaRestore = 100,
            tier = 3,
            colorHex = 0xFF2EC4B6
        ),
        // Crafting Materials (Tier 1 to Tier 3)
        ItemSpec(
            id = "tinta_antigua",
            name = "Tinta Antigua",
            category = ItemCategory.MATERIAL,
            description = "Material base recolectado en Tintópolis.",
            tier = 1,
            colorHex = 0xFFB2B2B2
        ),
        ItemSpec(
            id = "alga_luminosa",
            name = "Alga Luminosa",
            category = ItemCategory.MATERIAL,
            description = "Recurso fresco del Bosque de Algas de Marea Viva.",
            tier = 1,
            colorHex = 0xFF2EC4B6
        ),
        ItemSpec(
            id = "hierba_zen",
            name = "Hoja de Cactus Zen",
            category = ItemCategory.MATERIAL,
            description = "Esencia herbal que calma la fatiga en crafteos.",
            tier = 2,
            colorHex = 0xFF6BAE6B
        ),
        ItemSpec(
            id = "polvo_estelar",
            name = "Polvo Estelar",
            category = ItemCategory.MATERIAL,
            description = "Residuo brillante de Constelaria y Picasia para pociones.",
            tier = 2,
            colorHex = 0xFFFFC24D
        ),
        ItemSpec(
            id = "cristal_sombra",
            name = "Cristal de Eco",
            category = ItemCategory.MATERIAL,
            description = "Fragmento potente de Sombravía y enemigos de Tier 3.",
            tier = 3,
            colorHex = 0xFF9D4EDD
        ),
        // Quest & Gate Unlock Items
        ItemSpec(
            id = "llave_tinta",
            name = "Pluma de Tinta Dorada",
            category = ItemCategory.QUEST,
            description = "Reliquia de Tintópolis obtenida tras vencer a Laughing Cat.",
            tier = 1,
            colorHex = 0xFFFFC24D
        ),
        ItemSpec(
            id = "perla_coral",
            name = "Perla de Corrientes",
            category = ItemCategory.QUEST,
            description = "Perla ancestral de la Gruta en Marea Viva.",
            tier = 1,
            colorHex = 0xFF6FD0E8
        ),
        ItemSpec(
            id = "vinilo_chispa",
            name = "Vinilo Maestro",
            category = ItemCategory.QUEST,
            description = "Objeto clave protegido por Dinamita y Molotov en el Train Yard.",
            tier = 2,
            colorHex = 0xFFFF6A3D
        ),
        ItemSpec(
            id = "prisma_cubista",
            name = "Prisma Cubista",
            category = ItemCategory.QUEST,
            description = "Pieza geométrica liberada al resolver la Calle de la Ilusión.",
            tier = 2,
            colorHex = 0xFFB7B0E6
        ),
        ItemSpec(
            id = "corona_superacion",
            name = "Emblema del Reflejo",
            category = ItemCategory.QUEST,
            description = "Trofeo tras derrotar a Laughing Cat y Street Dino en Sombravía.",
            tier = 3,
            colorHex = 0xFFFF6A3D
        )
    ).associateBy { it.id }

    val recipes: List<CraftingRecipe> = listOf(
        CraftingRecipe(
            id = "recipe_cafe",
            resultItemId = "cafe_bw",
            resultQuantity = 2,
            requiredTier = 1,
            ingredients = mapOf("tinta_antigua" to 1, "alga_luminosa" to 1),
            hint = "Bebida energética básica (Tier 1) para quitar el cansancio."
        ),
        CraftingRecipe(
            id = "recipe_elixir",
            resultItemId = "elixir_chispa",
            resultQuantity = 2,
            requiredTier = 1,
            ingredients = mapOf("alga_luminosa" to 2, "tinta_antigua" to 1),
            hint = "Poción de Magia inicial (Tier 1) para usar las llamas de Foskay."
        ),
        CraftingRecipe(
            id = "recipe_te_zen",
            resultItemId = "te_zen",
            resultQuantity = 2,
            requiredTier = 2,
            ingredients = mapOf("hierba_zen" to 2, "alga_luminosa" to 1),
            hint = "Infusión intermedia (Tier 2): recupera toda la Estamina y Vida."
        ),
        CraftingRecipe(
            id = "recipe_fruto",
            resultItemId = "fruto_creacion",
            resultQuantity = 2,
            requiredTier = 2,
            ingredients = mapOf("hierba_zen" to 1, "polvo_estelar" to 2),
            hint = "Alimento potente (Tier 2) recomendado antes de enfrentar a Dinamita y Molotov."
        ),
        CraftingRecipe(
            id = "recipe_esencia",
            resultItemId = "esencia_cristal",
            resultQuantity = 2,
            requiredTier = 3,
            ingredients = mapOf("polvo_estelar" to 2, "cristal_sombra" to 2),
            hint = "Elixir supremo (Tier 3) para iluminar Sombravía y vencer a Street Dino."
        )
    )

    val books: Map<String, BookSpec> = listOf(
        BookSpec(
            id = "libro_origen",
            title = "Crónica de la Primera Chispa",
            regionId = RegionId.PLAZA_INFINITA,
            keyword = "CHISPA",
            excerpt = "Toda puerta de Mokaya responde al fuego que inicia una idea. Palabra clave inicial: CHISPA."
        ),
        BookSpec(
            id = "libro_tinta",
            title = "Códice de Tinta y Risas",
            regionId = RegionId.TINTOPOLIS,
            keyword = "TRAZO",
            forceGranted = "CREATIVIDAD",
            excerpt = "Las historias en blanco y negro abren el paso hacia los ritmos urbanos. Fuerza de Creatividad. Palabra clave: TRAZO."
        ),
        BookSpec(
            id = "libro_marea",
            title = "Tratado de las Mareas Vivas",
            regionId = RegionId.MAREA_VIVA,
            keyword = "FLUIR",
            forceGranted = "CURIOSIDAD",
            excerpt = "Adaptarse al agua revela las rutas ocultas de Arena Viva y Picasia. Fuerza de Curiosidad. Palabra clave: FLUIR."
        ),
        BookSpec(
            id = "libro_ritmo",
            title = "Compendio del Beat y la Pólvora",
            regionId = RegionId.RHYTHM_HOOD,
            keyword = "PULSO",
            forceGranted = "AMISTAD",
            excerpt = "Cuando el ritmo y la lealtad se unen, los muros urbanos ceden. Fuerza de Amistad. Palabra clave: PULSO."
        ),
        BookSpec(
            id = "libro_artelia",
            title = "Manuscrito de las Estrellas y Sombras",
            regionId = RegionId.ARTELIA,
            keyword = "LUZ",
            forceGranted = "VALENTIA",
            excerpt = "Solo quien abraza sus miedos despierta a Foskay en la oscuridad. Fuerza de Valentía. Palabra clave: LUZ."
        ),
        BookSpec(
            id = "libro_ancestral",
            title = "Tomo del Árbol Original",
            regionId = RegionId.BOSQUE_OLVIDADO,
            keyword = "MOKAYA",
            excerpt = "Con Creatividad, Valentía, Curiosidad y Amistad reunidas, recita la palabra sagrada frente al Círculo: MOKAYA."
        )
    ).associateBy { it.id }

    val quests: List<QuestSpec> = listOf(
        QuestSpec(
            id = "q_tintopolis_cat",
            title = "El Rugido en la Fuente",
            regionId = RegionId.TINTOPOLIS,
            giverName = "Bloom",
            description = "Derrota a Laughing Cat en la Fuente de la Tinta y recupera el Códice de Tinta y la Pluma Dorada.",
            requiredItemId = "llave_tinta",
            requiredBookId = "libro_tinta",
            requiredBossStageId = "boss_1_cat",
            rewardItemId = "cafe_bw",
            rewardForce = "CREATIVIDAD"
        ),
        QuestSpec(
            id = "q_marea_perla",
            title = "Secreto de la Gruta",
            regionId = RegionId.MAREA_VIVA,
            giverName = "Tula",
            description = "Activa las 3 placas de corrientes en Marea Viva para abrir la Gruta de las Perlas y leer el Tratado.",
            requiredItemId = "perla_coral",
            requiredBookId = "libro_marea",
            requiredPuzzleId = "puz_marea_plates",
            rewardItemId = "elixir_chispa",
            rewardForce = "CURIOSIDAD"
        ),
        QuestSpec(
            id = "q_rhythm_duo",
            title = "Ritmo Explosivo en el Train Yard",
            regionId = RegionId.RHYTHM_HOOD,
            giverName = "Clip",
            description = "Enfrenta a Dinamita y Molotov en el Train Yard para recuperar el Vinilo Maestro y el Compendio.",
            requiredItemId = "vinilo_chispa",
            requiredBookId = "libro_ritmo",
            requiredBossStageId = "boss_2_duo",
            rewardItemId = "te_zen",
            rewardForce = "AMISTAD"
        ),
        QuestSpec(
            id = "q_artelia_torches",
            title = "Las Antorchas de la Sabiduría",
            regionId = RegionId.ARTELIA,
            giverName = "Zenkay",
            description = "Usa la llama de Foskay para encender en orden las 3 antorchas de Artelia y liberar el Manuscrito.",
            requiredBookId = "libro_artelia",
            requiredPuzzleId = "puz_artelia_torches",
            rewardItemId = "esencia_cristal",
            rewardForce = "VALENTIA"
        ),
        QuestSpec(
            id = "q_sombravia_climax",
            title = "Faro contra las Sombras",
            regionId = RegionId.SOMBRAVIA,
            giverName = "Vivi Mokay",
            description = "Ilumina Sombravía con Foskay y derrota a Laughing Cat junto a Street Dino (T-Rex).",
            requiredItemId = "corona_superacion",
            requiredBossStageId = "boss_3_cat_dino",
            rewardItemId = "fruto_creacion"
        )
    )
}
