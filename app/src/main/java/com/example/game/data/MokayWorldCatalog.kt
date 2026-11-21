package com.example.game.data

import com.example.game.model.BookSpec
import com.example.game.model.CraftingRecipe
import com.example.game.model.ItemCategory
import com.example.game.model.ItemSpec
import com.example.game.model.QuestSpec
import com.example.game.model.RegionId
import com.example.game.model.ShopOfferSpec
import com.example.game.model.ShopUpgradeKind

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
            id = "recipe_pizza",
            resultItemId = "slice_pizza",
            resultQuantity = 2,
            requiredTier = 1,
            ingredients = mapOf("tinta_antigua" to 1, "hierba_zen" to 1),
            hint = "Ración rápida (Tier 1-2): recupera Vida y Estamina en combate."
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
            id = "q_plaza_spark",
            title = "Provisiones de la Posada",
            regionId = RegionId.PLAZA_INFINITA,
            giverName = "Ratz Urbano",
            villageName = "Aldea Posada Chispa",
            description = "Consigue la Crónica de la Primera Chispa (CHISPA) en la fuente y lleva x2 Porción Slice a Ratz en la Posada Chispa.",
            requiredItemId = "slice_pizza",
            requiredCount = 2,
            requiredBookId = "libro_origen",
            rewardItemId = "cafe_bw",
            rewardQuantity = 2,
            rewardChispas = 55,
            rewardMaxStaminaBoost = 10,
            consumeRequiredItem = false
        ),
        QuestSpec(
            id = "q_tintopolis_cat",
            title = "El Rugido en la Fuente",
            regionId = RegionId.TINTOPOLIS,
            giverName = "Bloom",
            villageName = "Barrio Café B&W",
            isMainStory = true,
            description = "Derrota a Laughing Cat en la Fuente de la Tinta y recupera el Códice de Tinta y la Pluma Dorada.",
            requiredItemId = "llave_tinta",
            requiredBookId = "libro_tinta",
            requiredBossStageId = "boss_1_cat",
            rewardItemId = "cafe_bw",
            rewardQuantity = 2,
            rewardForce = "CREATIVIDAD",
            rewardChispas = 90
        ),
        QuestSpec(
            id = "q_tinto_press",
            title = "Tinta Fresca para la Imprenta",
            regionId = RegionId.TINTOPOLIS,
            giverName = "La Flor",
            villageName = "Paseo de la Imprenta",
            description = "Recolecta x2 Tinta Antigua por Tintópolis y entrégaselas a La Flor para imprimir las noticias de Mokaya.",
            requiredItemId = "tinta_antigua",
            requiredCount = 2,
            rewardItemId = "elixir_chispa",
            rewardQuantity = 2,
            rewardChispas = 65,
            rewardMaxManaBoost = 15,
            consumeRequiredItem = true
        ),
        QuestSpec(
            id = "q_marea_perla",
            title = "Secreto de la Gruta",
            regionId = RegionId.MAREA_VIVA,
            giverName = "Tula",
            villageName = "Bosque de Algas",
            isMainStory = true,
            description = "Activa las 3 placas de corrientes en Marea Viva para abrir la Gruta de las Perlas y leer el Tratado.",
            requiredItemId = "perla_coral",
            requiredBookId = "libro_marea",
            requiredPuzzleId = "puz_marea_plates",
            rewardItemId = "elixir_chispa",
            rewardQuantity = 2,
            rewardForce = "CURIOSIDAD",
            rewardChispas = 90
        ),
        QuestSpec(
            id = "q_marea_algae",
            title = "Motores Bioluminiscentes",
            regionId = RegionId.MAREA_VIVA,
            giverName = "Octo",
            villageName = "Torre de las Corrientes",
            description = "Reúne x2 Alga Luminosa del arrecife y entrégaselas a Octo para alimentar las boyas de Marea Viva.",
            requiredItemId = "alga_luminosa",
            requiredCount = 2,
            rewardItemId = "te_zen",
            rewardQuantity = 2,
            rewardChispas = 70,
            rewardMaxHpBoost = 15,
            consumeRequiredItem = true
        ),
        QuestSpec(
            id = "q_rhythm_duo",
            title = "Ritmo Explosivo en el Train Yard",
            regionId = RegionId.RHYTHM_HOOD,
            giverName = "Clip",
            villageName = "Beat Labs Central",
            isMainStory = true,
            description = "Enfrenta a Dinamita y Molotov en el Train Yard para recuperar el Vinilo Maestro y el Compendio.",
            requiredItemId = "vinilo_chispa",
            requiredBookId = "libro_ritmo",
            requiredBossStageId = "boss_2_duo",
            rewardItemId = "te_zen",
            rewardQuantity = 2,
            rewardForce = "AMISTAD",
            rewardChispas = 110
        ),
        QuestSpec(
            id = "q_arena_zen",
            title = "Infusión del Campeonato",
            regionId = RegionId.ARENA_VIVA,
            giverName = "Coim",
            villageName = "Villa Basket & Tenis",
            description = "Recoge x2 Hoja de Cactus Zen en Arena Viva y llévalas a Coim para hidratar a los atletas.",
            requiredItemId = "hierba_zen",
            requiredCount = 2,
            rewardItemId = "fruto_creacion",
            rewardQuantity = 2,
            rewardChispas = 80,
            rewardMaxStaminaBoost = 15,
            consumeRequiredItem = true
        ),
        QuestSpec(
            id = "q_picasia_blocks",
            title = "Simetría de la Ilusión",
            regionId = RegionId.PICASIA,
            giverName = "Vivi Mokay",
            villageName = "Calle de la Ilusión",
            description = "Empuja los bloques cubistas sobre las runas doradas de Picasia y obtén el Prisma Cubista.",
            requiredItemId = "prisma_cubista",
            requiredPuzzleId = "puz_picasia_blocks",
            rewardItemId = "polvo_estelar",
            rewardQuantity = 2,
            rewardChispas = 90,
            rewardMaxManaBoost = 15
        ),
        QuestSpec(
            id = "q_artelia_torches",
            title = "Las Antorchas de la Sabiduría",
            regionId = RegionId.ARTELIA,
            giverName = "Zenkay",
            villageName = "Jardines Geométricos",
            isMainStory = true,
            description = "Usa la llama de Foskay para encender en orden 1→2→3 las antorchas frente a la Biblioteca de Artelia.",
            requiredBookId = "libro_artelia",
            requiredPuzzleId = "puz_artelia_torches",
            rewardItemId = "esencia_cristal",
            rewardQuantity = 2,
            rewardForce = "VALENTIA",
            rewardChispas = 110
        ),
        QuestSpec(
            id = "q_constelaria_stars",
            title = "Polvo de Sueños Altos",
            regionId = RegionId.CONSTELARIA,
            giverName = "Birdy",
            villageName = "Mirador de Nubes",
            description = "Reúne x2 Polvo Estelar entre las cúpulas de Constelaria y compártelo con Birdy.",
            requiredItemId = "polvo_estelar",
            requiredCount = 2,
            rewardItemId = "esencia_cristal",
            rewardQuantity = 2,
            rewardChispas = 95,
            rewardMaxHpBoost = 15,
            consumeRequiredItem = true
        ),
        QuestSpec(
            id = "q_sombravia_climax",
            title = "Faro contra las Sombras",
            regionId = RegionId.SOMBRAVIA,
            giverName = "Vivi Mokay",
            villageName = "Faro de la Superación",
            isMainStory = true,
            description = "Ilumina Sombravía con Foskay y derrota a Laughing Cat junto a Street Dino (T-Rex).",
            requiredItemId = "corona_superacion",
            requiredBossStageId = "boss_3_cat_dino",
            rewardItemId = "fruto_creacion",
            rewardQuantity = 3,
            rewardChispas = 160,
            rewardMaxHpBoost = 20
        ),
        QuestSpec(
            id = "q_bosque_echo",
            title = "Raíces Despiertas",
            regionId = RegionId.BOSQUE_OLVIDADO,
            giverName = "Rooty Explorador",
            villageName = "Círculo de las Raíces",
            description = "Encuentra el Tomo del Árbol Original (MOKAYA) y entrega x1 Cristal de Eco a Rooty.",
            requiredItemId = "cristal_sombra",
            requiredCount = 1,
            requiredBookId = "libro_ancestral",
            rewardItemId = "esencia_cristal",
            rewardQuantity = 2,
            rewardChispas = 140,
            rewardMaxManaBoost = 20,
            consumeRequiredItem = true
        ),
        QuestSpec(
            id = "q_arbol_harmony",
            title = "Armonía de los 7 Colores",
            regionId = RegionId.ARBOL_ORIGINAL,
            giverName = "Lumen",
            villageName = "Pabellón Luz",
            isMainStory = true,
            description = "Alcanza El Árbol Original portando las 4 Fuerzas Ancestrales para restaurar el corazón de Mokaya.",
            requiredRegionVisit = RegionId.ARBOL_ORIGINAL,
            requiredForces = listOf("CREATIVIDAD", "VALENTIA", "CURIOSIDAD", "AMISTAD"),
            rewardItemId = "fruto_creacion",
            rewardQuantity = 3,
            rewardChispas = 250,
            rewardMaxHpBoost = 25
        )
    )

    val shopOffers: List<ShopOfferSpec> = listOf(
        ShopOfferSpec(
            id = "shop_pizza",
            title = "Porción Slice",
            description = "+35 Vida y +20 Estamina al instante.",
            priceChispas = 25,
            itemId = "slice_pizza",
            quantity = 1,
            minTier = 1
        ),
        ShopOfferSpec(
            id = "shop_cafe",
            title = "Café B&W",
            description = "Quita el cansancio y recarga +65 Estamina.",
            priceChispas = 30,
            itemId = "cafe_bw",
            quantity = 1,
            minTier = 1
        ),
        ShopOfferSpec(
            id = "shop_elixir",
            title = "Elixir de Chispa",
            description = "Restaura +55 Magia para invocar a Foskay.",
            priceChispas = 35,
            itemId = "elixir_chispa",
            quantity = 1,
            minTier = 1
        ),
        ShopOfferSpec(
            id = "shop_te_zen",
            title = "Té Zen de Zenkay",
            description = "+100 Estamina, +25 Vida y elimina fatiga.",
            priceChispas = 55,
            itemId = "te_zen",
            quantity = 1,
            minTier = 2
        ),
        ShopOfferSpec(
            id = "shop_esencia",
            title = "Poción Estelar",
            description = "+100 Magia y +20 Vida para zonas oscuras.",
            priceChispas = 70,
            itemId = "esencia_cristal",
            quantity = 1,
            minTier = 2
        ),
        ShopOfferSpec(
            id = "shop_fruto",
            title = "Fruto de Creación",
            description = "+70 Vida, +35 Estamina y quita fatiga.",
            priceChispas = 75,
            itemId = "fruto_creacion",
            quantity = 1,
            minTier = 2
        ),
        ShopOfferSpec(
            id = "shop_upg_hp",
            title = "Amuleto Corazón (+20 Vida Máx)",
            description = "Aumenta permanentemente tu Vida máxima (hasta 220).",
            priceChispas = 90,
            upgradeKind = com.example.game.model.ShopUpgradeKind.MAX_HP,
            upgradeAmount = 20,
            minTier = 1
        ),
        ShopOfferSpec(
            id = "shop_upg_stam",
            title = "Bota Brisa (+20 Estamina Máx)",
            description = "Aumenta permanentemente tu Estamina máxima (hasta 200).",
            priceChispas = 90,
            upgradeKind = com.example.game.model.ShopUpgradeKind.MAX_STAMINA,
            upgradeAmount = 20,
            minTier = 1
        ),
        ShopOfferSpec(
            id = "shop_upg_mana",
            title = "Broche de Foskay (+20 Magia Máx)",
            description = "Aumenta permanentemente tu Magia máxima (hasta 200).",
            priceChispas = 95,
            upgradeKind = com.example.game.model.ShopUpgradeKind.MAX_MANA,
            upgradeAmount = 20,
            minTier = 1
        )
    )
}
