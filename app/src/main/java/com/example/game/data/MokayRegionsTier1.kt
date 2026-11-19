package com.example.game.data

import androidx.compose.ui.graphics.Color
import com.example.game.model.CharacterVisual
import com.example.game.model.EnemySpawn
import com.example.game.model.EnemyVisual
import com.example.game.model.FloraKind
import com.example.game.model.GateSpec
import com.example.game.model.HouseSpec
import com.example.game.model.HouseStyle
import com.example.game.model.LandmarkKind
import com.example.game.model.LandmarkSpec
import com.example.game.model.LivingFloraSpec
import com.example.game.model.NpcSpec
import com.example.game.model.PropKind
import com.example.game.model.PropSpec
import com.example.game.model.PuzzleNode
import com.example.game.model.PuzzleSpec
import com.example.game.model.PuzzleType
import com.example.game.model.RegionId
import com.example.game.model.RegionSpec
import com.example.game.model.RegionThemeColors
import com.example.game.model.WorldPickup

internal fun buildTier1Regions(): List<RegionSpec> = listOf(
    // 1. PLAZA INFINITA (Hub Central)
    RegionSpec(
        id = RegionId.PLAZA_INFINITA,
        mapX = 0.50f,
        mapY = 0.34f,
        worldWidth = 1200f,
        worldHeight = 1100f,
        hasWaterMoat = true,
        cobblestonePlazaRadius = 235f,
        theme = RegionThemeColors(
            groundPrimary = Color(0xFF2D533C),
            groundSecondary = Color(0xFF3D6E50),
            pathColor = Color(0xFFD9C7A3),
            waterOrVoid = Color(0xFF238E96),
            accentColor = Color(0xFFFFC24D),
            citizenPrimary = Color(0xFFB7B0E6),
            citizenOutfit = Color(0xFFFF6A3D)
        ),
        landmarks = listOf(
            LandmarkSpec("plaza_center", "Fuente Infinita", "Corazón de Mokaya", 600f, 510f, 165f, 165f, Color(0xFF6C63FF), LandmarkKind.FOUNTAIN),
            LandmarkSpec("plaza_craft", "Taller de la Chispa", "Crafteo y descanso", 330f, 320f, 150f, 118f, Color(0xFFFF6A3D), LandmarkKind.WORKSHOP),
            LandmarkSpec("plaza_arch", "Arco de los Puentes", "Rutas a los distritos", 870f, 320f, 150f, 118f, Color(0xFF2EC4B6), LandmarkKind.CAFE)
        ),
        houses = listOf(
            HouseSpec("ph1", "Casa Creativa", 180f, 410f, 118f, 94f, Color(0xFFFF6A3D), Color(0xFFF4E7D1), HouseStyle.TOWNHOUSE),
            HouseSpec("ph2", "Café Estrella", 430f, 180f, 126f, 92f, Color(0xFF6C63FF), Color(0xFFEBDCCB), HouseStyle.CAFE_AWNING),
            HouseSpec("ph3", "Estudio Luz", 780f, 180f, 124f, 92f, Color(0xFF2EC4B6), Color(0xFFF4E7D1), HouseStyle.TOWNHOUSE),
            HouseSpec("ph4", "Ateliers Norte", 1030f, 420f, 118f, 94f, Color(0xFFE97893), Color(0xFFF4E7D1), HouseStyle.TOWNHOUSE),
            HouseSpec("ph5", "Posada Chispa", 250f, 840f, 128f, 96f, Color(0xFFFFC24D), Color(0xFFEBDCCB), HouseStyle.CAFE_AWNING),
            HouseSpec("ph6", "Casa Puentes", 940f, 850f, 122f, 94f, Color(0xFF8A65C9), Color(0xFFF4E7D1), HouseStyle.TOWNHOUSE),
            HouseSpec("ph7", "Mirador Sur", 430f, 920f, 114f, 88f, Color(0xFF4CC37A), Color(0xFFF4E7D1), HouseStyle.TOWNHOUSE),
            HouseSpec("ph8", "Librería Central", 780f, 920f, 118f, 88f, Color(0xFFD94E4E), Color(0xFFEBDCCB), HouseStyle.TOWNHOUSE)
        ),
        props = listOf(
            PropSpec("pp1", 490f, 430f, PropKind.LAMP, 1f, Color(0xFFFFC24D)),
            PropSpec("pp2", 710f, 430f, PropKind.LAMP, 1f, Color(0xFFFFC24D)),
            PropSpec("pp3", 490f, 640f, PropKind.LAMP, 1f, Color(0xFFFFC24D)),
            PropSpec("pp4", 710f, 640f, PropKind.LAMP, 1f, Color(0xFFFFC24D)),
            PropSpec("pp5", 450f, 480f, PropKind.BENCH),
            PropSpec("pp6", 750f, 480f, PropKind.BENCH),
            PropSpec("pp7", 860f, 410f, PropKind.UMBRELLA_TABLE, 1f, Color(0xFFFF6A3D)),
            PropSpec("pp8", 920f, 410f, PropKind.UMBRELLA_TABLE, 1f, Color(0xFF2EC4B6)),
            PropSpec("pp9", 330f, 415f, PropKind.CRATE_STACK),
            PropSpec("pp10", 545f, 175f, PropKind.BANNER, 1f, Color(0xFF6C63FF)),
            PropSpec("pp11", 655f, 175f, PropKind.BANNER, 1f, Color(0xFFFF6A3D)),
            PropSpec("pp12", 180f, 520f, PropKind.FENCE_H),
            PropSpec("pp13", 1020f, 525f, PropKind.FENCE_H),
            PropSpec("pp14", 530f, 860f, PropKind.BENCH),
            PropSpec("pp15", 670f, 860f, PropKind.BENCH)
        ),
        flora = listOf(
            LivingFloraSpec("pf1", 120f, 130f, FloraKind.TREE, 1.25f, 0.2f, Color(0xFF4CC37A)),
            LivingFloraSpec("pf2", 290f, 145f, FloraKind.TREE, 1.15f, 0.9f, Color(0xFF39A96B)),
            LivingFloraSpec("pf3", 910f, 145f, FloraKind.TREE, 1.15f, 1.3f, Color(0xFF4CC37A)),
            LivingFloraSpec("pf4", 1080f, 140f, FloraKind.TREE, 1.25f, 1.8f, Color(0xFF2EC4B6)),
            LivingFloraSpec("pf5", 110f, 320f, FloraKind.TREE, 1.1f, 0.5f, Color(0xFF4CC37A)),
            LivingFloraSpec("pf6", 1090f, 320f, FloraKind.TREE, 1.15f, 2.2f, Color(0xFF39A96B)),
            LivingFloraSpec("pf7", 115f, 750f, FloraKind.TREE, 1.2f, 1.1f, Color(0xFF4CC37A)),
            LivingFloraSpec("pf8", 1085f, 780f, FloraKind.TREE, 1.2f, 2.5f, Color(0xFF39A96B)),
            LivingFloraSpec("pf9", 150f, 960f, FloraKind.TREE, 1.2f, 0.7f, Color(0xFF4CC37A)),
            LivingFloraSpec("pf10", 1050f, 960f, FloraKind.TREE, 1.2f, 1.6f, Color(0xFF4CC37A)),
            LivingFloraSpec("pf11", 435f, 680f, FloraKind.CUPHEAD_FLOWER, 1.05f, 0.5f, Color(0xFFE97893)),
            LivingFloraSpec("pf12", 765f, 680f, FloraKind.CUPHEAD_FLOWER, 1.05f, 2.1f, Color(0xFFFFC24D)),
            LivingFloraSpec("pf13", 390f, 420f, FloraKind.CUPHEAD_FLOWER, 0.95f, 1.0f, Color(0xFF6FD0E8)),
            LivingFloraSpec("pf14", 810f, 420f, FloraKind.CUPHEAD_FLOWER, 0.95f, 1.7f, Color(0xFFE97893)),
            LivingFloraSpec("pf15", 240f, 540f, FloraKind.SHRUB, 1.1f, 0.8f, Color(0xFF2EC4B6)),
            LivingFloraSpec("pf16", 960f, 550f, FloraKind.SHRUB, 1.1f, 1.4f, Color(0xFF4CC37A)),
            LivingFloraSpec("pf17", 320f, 710f, FloraKind.SHRUB, 1.05f, 0.3f, Color(0xFF4CC37A)),
            LivingFloraSpec("pf18", 880f, 710f, FloraKind.CUPHEAD_FLOWER, 1f, 2.4f, Color(0xFFFFC24D)),
            LivingFloraSpec("pf19", 520f, 270f, FloraKind.SHRUB, 1f, 1.9f, Color(0xFF4CC37A)),
            LivingFloraSpec("pf20", 680f, 270f, FloraKind.SHRUB, 1f, 0.4f, Color(0xFF2EC4B6)),
            LivingFloraSpec("pf21", 210f, 960f, FloraKind.CUPHEAD_FLOWER, 1.05f, 1.2f, Color(0xFFFF6A3D)),
            LivingFloraSpec("pf22", 990f, 960f, FloraKind.CUPHEAD_FLOWER, 1.05f, 2.8f, Color(0xFFB7B0E6))
        ),
        pickups = listOf(
            WorldPickup("pp_book", "slice_pizza", bookId = "libro_origen", x = 520f, y = 400f, respawns = false),
            WorldPickup("pp_food1", "slice_pizza", x = 350f, y = 500f),
            WorldPickup("pp_drink1", "cafe_bw", x = 840f, y = 500f),
            WorldPickup("pp_potion1", "elixir_chispa", x = 600f, y = 730f)
        ),
        puzzles = emptyList(),
        npcs = listOf(
            NpcSpec("npc_llave", "La Llave", "Guía de Portales", CharacterVisual.LA_LLAVE, RegionId.PLAZA_INFINITA, 470f, 540f, "¡Bienvenido a Mokaya, Boxy! Lee libros para hallar palabras clave y abrir los puentes."),
            NpcSpec("npc_star", "Star", "Inspirador", CharacterVisual.STAR, RegionId.PLAZA_INFINITA, 730f, 540f, "Si te quedas sin estamina te cansarás: bebe Café B&W o Té Zen. ¡Usa a Foskay con tu Magia!"),
            NpcSpec("npc_cit_plaza", "Ratz Urbano", "Ciudadano de Plaza", CharacterVisual.CITIZEN_RATZ, RegionId.PLAZA_INFINITA, 390f, 760f, "Empieza por Tintópolis al noroeste o Marea Viva al sur.")
        ),
        enemies = emptyList(),
        gates = listOf(
            GateSpec("gate_plaza_tinto", "Puente a Tintópolis", RegionId.PLAZA_INFINITA, RegionId.TINTOPOLIS, 160f, 220f, 940f, 840f, requiredBookId = "libro_origen", requiredKeyword = "CHISPA", lockedMessage = "Lee la Crónica de la Primera Chispa e introduce la palabra clave."),
            GateSpec("gate_plaza_marea", "Muelle a Marea Viva", RegionId.PLAZA_INFINITA, RegionId.MAREA_VIVA, 600f, 980f, 600f, 180f),
            GateSpec("gate_plaza_rhythm", "Estación Rhythm Hood", RegionId.PLAZA_INFINITA, RegionId.RHYTHM_HOOD, 120f, 620f, 980f, 540f, requiredBookId = "libro_tinta", requiredKeyword = "TRAZO", requiredQuestItemId = "llave_tinta", lockedMessage = "Vence a Laughing Cat en Tintópolis para obtener el Códice de Tinta (TRAZO) y la Pluma Dorada."),
            GateSpec("gate_plaza_artelia", "Puente a Artelia", RegionId.PLAZA_INFINITA, RegionId.ARTELIA, 1020f, 220f, 200f, 820f, requiredBookId = "libro_marea", requiredKeyword = "FLUIR", requiredQuestItemId = "perla_coral", lockedMessage = "Completa la Gruta de las Perlas en Marea Viva (FLUIR) para abrir Artelia."),
            GateSpec("gate_plaza_constelaria", "Portal Constelaria", RegionId.PLAZA_INFINITA, RegionId.CONSTELARIA, 600f, 120f, 600f, 860f, requiredBookId = "libro_ritmo", requiredKeyword = "PULSO", lockedMessage = "Obtén el Compendio de Rhythm Hood (PULSO) para ascender a Constelaria."),
            GateSpec("gate_plaza_sombravia", "Estación Niebla (Sombravía)", RegionId.PLAZA_INFINITA, RegionId.SOMBRAVIA, 1040f, 680f, 200f, 540f, requiredBookId = "libro_artelia", requiredKeyword = "LUZ", lockedMessage = "Resuelve el puzzle de antorchas en Artelia para aprender la palabra clave LUZ.")
        )
    ),

    // 2. TINTOPOLIS (Distrito Monocroma - Nivel 1 + JEFE 1: LAUGHING CAT)
    RegionSpec(
        id = RegionId.TINTOPOLIS,
        mapX = 0.30f,
        mapY = 0.16f,
        worldWidth = 1200f,
        worldHeight = 1050f,
        hasTrainTrack = true,
        cobblestonePlazaRadius = 225f,
        theme = RegionThemeColors(
            groundPrimary = Color(0xFF2E2E2E),
            groundSecondary = Color(0xFF474747),
            pathColor = Color(0xFFDADADA),
            waterOrVoid = Color(0xFF141414),
            accentColor = Color(0xFFF5F5F5),
            citizenPrimary = Color(0xFFC8C8C8),
            citizenOutfit = Color(0xFF424242)
        ),
        landmarks = listOf(
            LandmarkSpec("tm_cine", "Cine Rubber", "Clásicos animados", 260f, 220f, 185f, 132f, Color(0xFFBDBDBD), LandmarkKind.CINEMA),
            LandmarkSpec("tm_estudio", "Estudio Ink", "Torre de tinta vintage", 600f, 200f, 185f, 132f, Color(0xFF9E9E9E), LandmarkKind.STUDIO),
            LandmarkSpec("tm_teatro", "Teatro Mokay", "Espectáculos años 30", 935f, 235f, 175f, 130f, Color(0xFFE0E0E0), LandmarkKind.THEATER),
            LandmarkSpec("tm_fuente", "Fuente de la Tinta", "Corazón creativo", 600f, 515f, 150f, 150f, Color(0xFF212121), LandmarkKind.FOUNTAIN),
            LandmarkSpec("tm_cafe", "Café B & W", "Descanso y cacao", 225f, 700f, 165f, 122f, Color(0xFFBDBDBD), LandmarkKind.CAFE),
            LandmarkSpec("tm_imprenta", "Imprenta Mokay", "Cómics y periódicos", 950f, 680f, 165f, 122f, Color(0xFF9E9E9E), LandmarkKind.PRINT_SHOP)
        ),
        houses = listOf(
            HouseSpec("th1", "Clásicos Mokay", 420f, 195f, 118f, 92f, Color(0xFF333333), Color(0xFFE0E0E0), HouseStyle.TOWNHOUSE),
            HouseSpec("th2", "Calle del Cartoon", 130f, 410f, 112f, 90f, Color(0xFF545454), Color(0xFFD6D6D6), HouseStyle.TOWNHOUSE),
            HouseSpec("th3", "Estudio 1928", 1070f, 410f, 114f, 90f, Color(0xFF3D3D3D), Color(0xFFE5E5E5), HouseStyle.TOWNHOUSE),
            HouseSpec("th4", "Bistro Tinta", 450f, 835f, 124f, 88f, Color(0xFF424242), Color(0xFFD9D9D9), HouseStyle.CAFE_AWNING),
            HouseSpec("th5", "Archivo MoNews", 740f, 835f, 122f, 88f, Color(0xFF2B2B2B), Color(0xFFE0E0E0), HouseStyle.TOWNHOUSE),
            HouseSpec("th6", "Taller Pluma", 785f, 195f, 116f, 88f, Color(0xFF4A4A4A), Color(0xFFD8D8D8), HouseStyle.TOWNHOUSE)
        ),
        props = listOf(
            PropSpec("tp1", 495f, 425f, PropKind.LAMP, 1f, Color(0xFFF5F5F5)),
            PropSpec("tp2", 705f, 425f, PropKind.LAMP, 1f, Color(0xFFF5F5F5)),
            PropSpec("tp3", 495f, 615f, PropKind.LAMP, 1f, Color(0xFFF5F5F5)),
            PropSpec("tp4", 705f, 615f, PropKind.LAMP, 1f, Color(0xFFF5F5F5)),
            PropSpec("tp5", 335f, 705f, PropKind.UMBRELLA_TABLE, 1f, Color(0xFF616161)),
            PropSpec("tp6", 335f, 755f, PropKind.UMBRELLA_TABLE, 1f, Color(0xFF9E9E9E)),
            PropSpec("tp7", 520f, 675f, PropKind.BENCH),
            PropSpec("tp8", 680f, 675f, PropKind.BENCH),
            PropSpec("tp9", 870f, 770f, PropKind.CRATE_STACK),
            PropSpec("tp10", 260f, 325f, PropKind.BANNER, 1f, Color(0xFFE0E0E0)),
            PropSpec("tp11", 930f, 330f, PropKind.BANNER, 1f, Color(0xFFBDBDBD)),
            PropSpec("tp12", 140f, 540f, PropKind.FENCE_H),
            PropSpec("tp13", 1055f, 540f, PropKind.FENCE_H)
        ),
        flora = listOf(
            LivingFloraSpec("tf1", 120f, 170f, FloraKind.TREE, 1.15f, 0.3f, Color(0xFFBDBDBD)),
            LivingFloraSpec("tf2", 1070f, 165f, FloraKind.TREE, 1.15f, 1.1f, Color(0xFF9E9E9E)),
            LivingFloraSpec("tf3", 415f, 345f, FloraKind.TREE, 1.05f, 1.7f, Color(0xFFB0B0B0)),
            LivingFloraSpec("tf4", 785f, 345f, FloraKind.TREE, 1.05f, 2.0f, Color(0xFF9E9E9E)),
            LivingFloraSpec("tf5", 130f, 595f, FloraKind.INK_FERN, 1.15f, 0.3f, Color(0xFFDADADA)),
            LivingFloraSpec("tf6", 1065f, 595f, FloraKind.TREE, 1.1f, 0.8f, Color(0xFFB8B8B8)),
            LivingFloraSpec("tf7", 440f, 680f, FloraKind.CUPHEAD_FLOWER, 1f, 0.6f, Color(0xFFE5E5E5)),
            LivingFloraSpec("tf8", 760f, 680f, FloraKind.INK_FERN, 1.1f, 1.8f, Color(0xFFCFCFCF)),
            LivingFloraSpec("tf9", 180f, 845f, FloraKind.SHRUB, 1.1f, 0.4f, Color(0xFFBDBDBD)),
            LivingFloraSpec("tf10", 360f, 840f, FloraKind.CUPHEAD_FLOWER, 1f, 1.4f, Color(0xFFF5F5F5)),
            LivingFloraSpec("tf11", 600f, 845f, FloraKind.SHRUB, 1.05f, 2.2f, Color(0xFFA6A6A6)),
            LivingFloraSpec("tf12", 835f, 845f, FloraKind.CUPHEAD_FLOWER, 1f, 0.9f, Color(0xFFDADADA)),
            LivingFloraSpec("tf13", 230f, 510f, FloraKind.TREE, 1.05f, 1.5f, Color(0xFF9E9E9E)),
            LivingFloraSpec("tf14", 970f, 510f, FloraKind.TREE, 1.05f, 2.4f, Color(0xFFB0B0B0)),
            LivingFloraSpec("tf15", 350f, 405f, FloraKind.INK_FERN, 1f, 0.5f, Color(0xFFCCCCCC)),
            LivingFloraSpec("tf16", 850f, 405f, FloraKind.INK_FERN, 1f, 1.9f, Color(0xFFCCCCCC)),
            LivingFloraSpec("tf17", 520f, 295f, FloraKind.CUPHEAD_FLOWER, 0.95f, 1.2f, Color(0xFFEAEAEA)),
            LivingFloraSpec("tf18", 680f, 295f, FloraKind.CUPHEAD_FLOWER, 0.95f, 2.7f, Color(0xFFD5D5D5))
        ),
        pickups = listOf(
            WorldPickup("tp_ink1", "tinta_antigua", x = 320f, y = 450f),
            WorldPickup("tp_ink2", "tinta_antigua", x = 860f, y = 450f),
            WorldPickup("tp_cafe", "cafe_bw", x = 250f, y = 820f)
        ),
        puzzles = emptyList(),
        npcs = listOf(
            NpcSpec("npc_bloom", "Bloom", "Artista Urbana", CharacterVisual.BLOOM, RegionId.TINTOPOLIS, 370f, 720f, "¡Laughing Cat tomó la Fuente de la Tinta! Derrótalo para recuperar el Códice de Tinta y devolver el color.", questId = "q_tintopolis_cat"),
            NpcSpec("npc_laflor", "La Flor", "Espíritu Creativo", CharacterVisual.LA_FLOR, RegionId.TINTOPOLIS, 820f, 790f, "Incluso en blanco y negro, nuestras raíces bailan al ritmo de tu chispa."),
            NpcSpec("npc_tinto_cit", "Beep Vintage", "Ciudadano Monocroma", CharacterVisual.CITIZEN_BEEP, RegionId.TINTOPOLIS, 480f, 320f, "El Teatro Mokay espera una nueva historia.")
        ),
        enemies = listOf(
            EnemySpawn("en_tinto_1", "Chispa Gris", EnemyVisual.CHISPA_ERRANTE, 440f, 450f, 45, 8, 95f, 1, dropItemId = "tinta_antigua"),
            EnemySpawn("en_tinto_2", "Box Rebelde", EnemyVisual.BOX_ANTAGONISTA, 780f, 450f, 55, 10, 100f, 1, dropItemId = "cafe_bw"),
            EnemySpawn("boss_cat_1", "Laughing Cat (El Gato)", EnemyVisual.BOSS_LAUGHING_CAT, 600f, 530f, 140, 14, 130f, 1, isBoss = true, bossStageId = "boss_1_cat", dropItemId = "llave_tinta", dropBookId = "libro_tinta")
        ),
        gates = listOf(
            GateSpec("gate_tinto_plaza", "Regresar a Plaza Infinita", RegionId.TINTOPOLIS, RegionId.PLAZA_INFINITA, 980f, 880f, 220f, 240f)
        )
    ),

    // 3. MAREA VIVA (Distrito Acuático - Nivel 1 + Puzzle Placas)
    RegionSpec(
        id = RegionId.MAREA_VIVA,
        mapX = 0.50f,
        mapY = 0.50f,
        worldWidth = 1200f,
        worldHeight = 1050f,
        hasWaterMoat = true,
        cobblestonePlazaRadius = 215f,
        theme = RegionThemeColors(
            groundPrimary = Color(0xFF175566),
            groundSecondary = Color(0xFF217488),
            pathColor = Color(0xFFD6B98C),
            waterOrVoid = Color(0xFF0E3E54),
            accentColor = Color(0xFF6FD0E8),
            citizenPrimary = Color(0xFF6FD0E8),
            citizenOutfit = Color(0xFFFF8C61)
        ),
        landmarks = listOf(
            LandmarkSpec("mv_arrecife", "Arrecife Musical", "Medusas DJs", 220f, 225f, 165f, 125f, Color(0xFF8BB3FF), LandmarkKind.STAGE_SHELL),
            LandmarkSpec("mv_plaza", "Plaza Coral", "Corazón de Marea Viva", 600f, 425f, 155f, 145f, Color(0xFFFF6A3D), LandmarkKind.FOUNTAIN),
            LandmarkSpec("mv_torre", "Torre de las Corrientes", "Observatorio oceánico", 955f, 225f, 150f, 130f, Color(0xFF2EC4B6), LandmarkKind.LIGHTHOUSE),
            LandmarkSpec("mv_gruta", "Gruta de las Perlas", "Tesoros ocultos", 200f, 740f, 165f, 130f, Color(0xFFB7B0E6), LandmarkKind.GROTTO),
            LandmarkSpec("mv_algas", "Bosque de Algas", "Hogar de tortugas sabias", 560f, 845f, 165f, 120f, Color(0xFF4CC37A), LandmarkKind.GARDEN),
            LandmarkSpec("mv_puerto", "Puerto Coral", "Travesías marinas", 975f, 740f, 165f, 125f, Color(0xFFFFC24D), LandmarkKind.PIER)
        ),
        houses = listOf(
            HouseSpec("mh1", "Choza Coralina", 415f, 185f, 118f, 90f, Color(0xFFFF6A3D), Color(0xFFF4E7D1), HouseStyle.CORAL_HUT),
            HouseSpec("mh2", "Cabina Perla", 785f, 185f, 118f, 90f, Color(0xFF8BB3FF), Color(0xFFE6F2FF), HouseStyle.CORAL_HUT),
            HouseSpec("mh3", "Taller Submarino", 1070f, 435f, 116f, 92f, Color(0xFFFFC24D), Color(0xFFF4E7D1), HouseStyle.CORAL_HUT),
            HouseSpec("mh4", "Villa Caracol", 130f, 420f, 112f, 88f, Color(0xFFE97893), Color(0xFFF4E7D1), HouseStyle.CORAL_HUT),
            HouseSpec("mh5", "Bistro Brisa", 360f, 885f, 124f, 88f, Color(0xFF2EC4B6), Color(0xFFE6F2FF), HouseStyle.CAFE_AWNING),
            HouseSpec("mh6", "Muelle Sur", 805f, 885f, 120f, 88f, Color(0xFFFF8C61), Color(0xFFF4E7D1), HouseStyle.CORAL_HUT)
        ),
        props = listOf(
            PropSpec("mvp1", 495f, 345f, PropKind.LAMP, 1f, Color(0xFF6FD0E8)),
            PropSpec("mvp2", 705f, 345f, PropKind.LAMP, 1f, Color(0xFF6FD0E8)),
            PropSpec("mvp3", 495f, 520f, PropKind.LAMP, 1f, Color(0xFF6FD0E8)),
            PropSpec("mvp4", 705f, 520f, PropKind.LAMP, 1f, Color(0xFF6FD0E8)),
            PropSpec("mvp5", 900f, 675f, PropKind.CRATE_STACK),
            PropSpec("mvp6", 940f, 830f, PropKind.CRATE_STACK),
            PropSpec("mvp7", 365f, 800f, PropKind.UMBRELLA_TABLE, 1f, Color(0xFF6FD0E8)),
            PropSpec("mvp8", 460f, 600f, PropKind.BENCH),
            PropSpec("mvp9", 740f, 600f, PropKind.BENCH),
            PropSpec("mvp10", 240f, 330f, PropKind.BANNER, 1f, Color(0xFF8BB3FF)),
            PropSpec("mvp11", 940f, 330f, PropKind.BANNER, 1f, Color(0xFF2EC4B6)),
            PropSpec("mvp12", 160f, 640f, PropKind.PILLAR, 1f, Color(0xFFB7B0E6))
        ),
        flora = listOf(
            LivingFloraSpec("mf1", 340f, 575f, FloraKind.CORAL, 1.2f, 0.4f, Color(0xFFE97893)),
            LivingFloraSpec("mf2", 780f, 580f, FloraKind.CORAL, 1.15f, 1.6f, Color(0xFF2EC4B6)),
            LivingFloraSpec("mf3", 480f, 765f, FloraKind.SHRUB, 1.2f, 0.9f, Color(0xFF4CC37A)),
            LivingFloraSpec("mf4", 720f, 765f, FloraKind.CUPHEAD_FLOWER, 1.05f, 2.2f, Color(0xFF6FD0E8)),
            LivingFloraSpec("mf5", 110f, 160f, FloraKind.CORAL, 1.3f, 0.2f, Color(0xFFFF6A3D)),
            LivingFloraSpec("mf6", 1090f, 160f, FloraKind.CORAL, 1.3f, 1.1f, Color(0xFF6FD0E8)),
            LivingFloraSpec("mf7", 515f, 240f, FloraKind.TREE, 1.1f, 0.7f, Color(0xFF2EC4B6)),
            LivingFloraSpec("mf8", 685f, 240f, FloraKind.TREE, 1.1f, 1.9f, Color(0xFF4CC37A)),
            LivingFloraSpec("mf9", 120f, 320f, FloraKind.SHRUB, 1.1f, 0.5f, Color(0xFF2EC4B6)),
            LivingFloraSpec("mf10", 1075f, 320f, FloraKind.CORAL, 1.2f, 2.3f, Color(0xFFE97893)),
            LivingFloraSpec("mf11", 120f, 880f, FloraKind.CORAL, 1.25f, 1.3f, Color(0xFFB7B0E6)),
            LivingFloraSpec("mf12", 1070f, 880f, FloraKind.CORAL, 1.25f, 0.6f, Color(0xFFFFC24D)),
            LivingFloraSpec("mf13", 315f, 470f, FloraKind.CUPHEAD_FLOWER, 1f, 1.5f, Color(0xFFE97893)),
            LivingFloraSpec("mf14", 885f, 470f, FloraKind.CUPHEAD_FLOWER, 1f, 2.7f, Color(0xFF2EC4B6)),
            LivingFloraSpec("mf15", 640f, 690f, FloraKind.CORAL, 1.1f, 0.9f, Color(0xFFFF8C61)),
            LivingFloraSpec("mf16", 440f, 700f, FloraKind.SHRUB, 1.05f, 2.1f, Color(0xFF4CC37A)),
            LivingFloraSpec("mf17", 270f, 920f, FloraKind.TREE, 1.1f, 0.4f, Color(0xFF2EC4B6)),
            LivingFloraSpec("mf18", 920f, 920f, FloraKind.TREE, 1.1f, 1.7f, Color(0xFF4CC37A))
        ),
        pickups = listOf(
            WorldPickup("mp_alga1", "alga_luminosa", x = 440f, y = 760f),
            WorldPickup("mp_alga2", "alga_luminosa", x = 740f, y = 760f),
            WorldPickup("mp_elixir", "elixir_chispa", x = 860f, y = 490f)
        ),
        puzzles = listOf(
            PuzzleSpec(
                id = "puz_marea_plates",
                title = "Placas de las Corrientes",
                regionId = RegionId.MAREA_VIVA,
                type = PuzzleType.PRESSURE_PLATES,
                hint = "Pisa las 3 placas coralinas cerca de la Gruta para obtener la Perla de Corrientes y el Tratado.",
                nodes = listOf(
                    PuzzleNode("mp_n1", 350f, 620f),
                    PuzzleNode("mp_n2", 240f, 540f),
                    PuzzleNode("mp_n3", 390f, 760f)
                ),
                rewardItemId = "perla_coral",
                rewardBookId = "libro_marea"
            )
        ),
        npcs = listOf(
            NpcSpec("npc_tula", "Tula", "Tortuga Sabia", CharacterVisual.TULA, RegionId.MAREA_VIVA, 520f, 680f, "Pisa las 3 placas brillantes junto a la Gruta de las Perlas para revelar el Tratado de Mareas.", questId = "q_marea_perla"),
            NpcSpec("npc_finn", "Finn", "Pez Artista", CharacterVisual.FINN, RegionId.MAREA_VIVA, 420f, 340f, "¡Cada ola trae algas luminosas para tus pociones!"),
            NpcSpec("npc_lumi", "Lumi", "Medusa DJ", CharacterVisual.LUMI, RegionId.MAREA_VIVA, 260f, 380f, "El Arrecife Musical vibra con luz bioluminiscente."),
            NpcSpec("npc_octo", "Octo", "Pulpo Inventor", CharacterVisual.OCTO, RegionId.MAREA_VIVA, 840f, 380f, "Mis máquinas marinas ayudan a cruzar hacia Artelia."),
            NpcSpec("npc_coraly", "Coraly", "Exploradora", CharacterVisual.CORALY, RegionId.MAREA_VIVA, 820f, 680f, "Puedes bajar por el Puerto hacia Arena Viva.")
        ),
        enemies = listOf(
            EnemySpawn("en_marea_1", "Chispa Marina", EnemyVisual.CHISPA_ERRANTE, 650f, 550f, 50, 9, 95f, 1, dropItemId = "alga_luminosa"),
            EnemySpawn("en_marea_2", "Box Pirata", EnemyVisual.BOX_ANTAGONISTA, 300f, 440f, 60, 10, 100f, 1, dropItemId = "alga_luminosa")
        ),
        gates = listOf(
            GateSpec("gate_marea_plaza", "Volver a Plaza Infinita", RegionId.MAREA_VIVA, RegionId.PLAZA_INFINITA, 600f, 130f, 600f, 900f),
            GateSpec("gate_marea_arena", "Paso a Arena Viva", RegionId.MAREA_VIVA, RegionId.ARENA_VIVA, 140f, 540f, 980f, 520f),
            GateSpec("gate_marea_bosque", "Descenso al Bosque Olvidado", RegionId.MAREA_VIVA, RegionId.BOSQUE_OLVIDADO, 600f, 960f, 600f, 160f, requiredBossStageId = "boss_3_cat_dino", lockedMessage = "El Bosque Olvidado está sellado por la niebla. Vence primero a Laughing Cat y Street Dino en Sombravía.")
        )
    )
)
