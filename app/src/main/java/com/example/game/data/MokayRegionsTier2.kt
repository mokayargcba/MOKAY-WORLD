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

internal fun buildTier2Regions(): List<RegionSpec> = listOf(
    // 4. RHYTHM HOOD (Distrito Urbano - Nivel 2 + JEFE 2: DINAMITA & MOLOTOV)
    RegionSpec(
        id = RegionId.RHYTHM_HOOD,
        mapX = 0.17f,
        mapY = 0.31f,
        worldWidth = 1250f,
        worldHeight = 1050f,
        hasTrainTrack = true,
        isSportsZone = true,
        cobblestonePlazaRadius = 210f,
        theme = RegionThemeColors(
            groundPrimary = Color(0xFF281938),
            groundSecondary = Color(0xFF3E2257),
            pathColor = Color(0xFF8B6F99),
            waterOrVoid = Color(0xFF150E20),
            accentColor = Color(0xFFFF3D9A),
            citizenPrimary = Color(0xFFFF66B3),
            citizenOutfit = Color(0xFF5D2E8C)
        ),
        landmarks = listOf(
            LandmarkSpec("rh_muro", "Muro Infinito", "Mural vivo del barrio", 230f, 220f, 185f, 128f, Color(0xFFFF3D9A), LandmarkKind.GRAFFITI_WALL),
            LandmarkSpec("rh_train", "Train Yard", "Pistas de Dinamita y Molotov", 910f, 225f, 210f, 145f, Color(0xFFFF6A3D), LandmarkKind.TRAIN_YARD),
            LandmarkSpec("rh_beats", "Beat Labs", "Estudio de Clip", 600f, 470f, 175f, 130f, Color(0xFF8A65C9), LandmarkKind.STUDIO),
            LandmarkSpec("rh_skate", "Skate Plaza", "Rieles y rampas", 270f, 700f, 185f, 138f, Color(0xFFFFC24D), LandmarkKind.SKATE_PARK),
            LandmarkSpec("rh_arena", "Freestyle Arena", "Batallas musicales", 960f, 700f, 175f, 138f, Color(0xFF2EC4B6), LandmarkKind.THEATER)
        ),
        houses = listOf(
            HouseSpec("rhh1", "Sneaks & Style", 450f, 180f, 122f, 92f, Color(0xFFFF3D9A), Color(0xFF3E2A56), HouseStyle.NEON_LOFT),
            HouseSpec("rhh2", "Vinyl Records", 660f, 180f, 122f, 92f, Color(0xFFFFC24D), Color(0xFF36254A), HouseStyle.NEON_LOFT),
            HouseSpec("rhh3", "Loft Graffiti", 125f, 415f, 112f, 92f, Color(0xFF2EC4B6), Color(0xFF422C5B), HouseStyle.NEON_LOFT),
            HouseSpec("rhh4", "Estudio Hip-Hop", 1110f, 385f, 118f, 92f, Color(0xFFFF6A3D), Color(0xFF3E2A56), HouseStyle.NEON_LOFT),
            HouseSpec("rhh5", "Café Neon", 160f, 845f, 122f, 88f, Color(0xFFE97893), Color(0xFF422C5B), HouseStyle.CAFE_AWNING),
            HouseSpec("rhh6", "Club Flow", 815f, 850f, 122f, 88f, Color(0xFF8A65C9), Color(0xFF36254A), HouseStyle.NEON_LOFT)
        ),
        props = listOf(
            PropSpec("rhp1", 440f, 495f, PropKind.LAMP, 1f, Color(0xFFFF3D9A)),
            PropSpec("rhp2", 760f, 495f, PropKind.LAMP, 1f, Color(0xFF2EC4B6)),
            PropSpec("rhp3", 380f, 650f, PropKind.LAMP, 1f, Color(0xFFFFC24D)),
            PropSpec("rhp4", 845f, 640f, PropKind.LAMP, 1f, Color(0xFFFF3D9A)),
            PropSpec("rhp5", 205f, 325f, PropKind.CRATE_STACK),
            PropSpec("rhp6", 940f, 435f, PropKind.CRATE_STACK),
            PropSpec("rhp7", 545f, 680f, PropKind.BENCH),
            PropSpec("rhp8", 745f, 600f, PropKind.BANNER, 1f, Color(0xFFFF3D9A)),
            PropSpec("rhp9", 470f, 345f, PropKind.BANNER, 1f, Color(0xFFFFC24D)),
            PropSpec("rhp10", 390f, 825f, PropKind.FENCE_H),
            PropSpec("rhp11", 675f, 825f, PropKind.FENCE_H)
        ),
        flora = listOf(
            LivingFloraSpec("rf1", 165f, 540f, FloraKind.CUPHEAD_FLOWER, 1.1f, 0.2f, Color(0xFFFF3D9A)),
            LivingFloraSpec("rf2", 470f, 325f, FloraKind.SHRUB, 1.15f, 1.1f, Color(0xFF8A65C9)),
            LivingFloraSpec("rf3", 715f, 830f, FloraKind.CUPHEAD_FLOWER, 1.05f, 2.0f, Color(0xFFFFC24D)),
            LivingFloraSpec("rf4", 110f, 175f, FloraKind.TREE, 1.15f, 0.3f, Color(0xFF7B2CBF)),
            LivingFloraSpec("rf5", 1130f, 175f, FloraKind.TREE, 1.15f, 1.2f, Color(0xFFFF3D9A)),
            LivingFloraSpec("rf6", 345f, 290f, FloraKind.TREE, 1.05f, 1.8f, Color(0xFF9D4EDD)),
            LivingFloraSpec("rf7", 755f, 285f, FloraKind.SHRUB, 1.1f, 0.6f, Color(0xFF2EC4B6)),
            LivingFloraSpec("rf8", 1120f, 670f, FloraKind.TREE, 1.15f, 2.5f, Color(0xFF8A65C9)),
            LivingFloraSpec("rf9", 105f, 690f, FloraKind.SHRUB, 1.1f, 0.9f, Color(0xFFFF3D9A)),
            LivingFloraSpec("rf10", 360f, 560f, FloraKind.CUPHEAD_FLOWER, 1f, 1.5f, Color(0xFF2EC4B6)),
            LivingFloraSpec("rf11", 830f, 550f, FloraKind.CUPHEAD_FLOWER, 1f, 2.2f, Color(0xFFFFC24D)),
            LivingFloraSpec("rf12", 470f, 855f, FloraKind.TREE, 1.1f, 0.4f, Color(0xFF7B2CBF)),
            LivingFloraSpec("rf13", 990f, 855f, FloraKind.TREE, 1.1f, 1.7f, Color(0xFF9D4EDD)),
            LivingFloraSpec("rf14", 600f, 315f, FloraKind.CUPHEAD_FLOWER, 0.95f, 2.8f, Color(0xFFFF3D9A)),
            LivingFloraSpec("rf15", 625f, 825f, FloraKind.SHRUB, 1.05f, 1.1f, Color(0xFF8A65C9))
        ),
        pickups = listOf(
            WorldPickup("rp_pizza", "slice_pizza", x = 360f, y = 520f),
            WorldPickup("rp_polvo", "polvo_estelar", x = 520f, y = 780f),
            WorldPickup("rp_elixir", "elixir_chispa", x = 760f, y = 740f)
        ),
        puzzles = emptyList(),
        npcs = listOf(
            NpcSpec(
                id = "npc_clip",
                name = "Clip",
                role = "Beatmaker Mokay",
                visual = CharacterVisual.CLIP,
                regionId = RegionId.RHYTHM_HOOD,
                x = 580f,
                y = 575f,
                dialogue = "¡Dinamita y Molotov tomaron el Train Yard! Dales ritmo en su propio terreno y recupera el Vinilo Maestro.",
                questId = "q_rhythm_duo",
                villageName = "Beat Labs Central"
            ),
            NpcSpec(
                id = "npc_ratt",
                name = "Ratt",
                role = "Graffitero Mokay",
                visual = CharacterVisual.RATT,
                regionId = RegionId.RHYTHM_HOOD,
                x = 240f,
                y = 335f,
                dialogue = "Mi arte en el Muro Infinito no sigue reglas, pero con tu chispa pintamos todo Mokaya.",
                villageName = "Aldea Muro Infinito"
            ),
            NpcSpec(
                id = "npc_rh_cit",
                name = "Slice Urbano",
                role = "Chef de Skate Plaza",
                visual = CharacterVisual.CITIZEN_SLICE,
                regionId = RegionId.RHYTHM_HOOD,
                x = 270f,
                y = 805f,
                dialogue = "Una buena rebanada en Skate Plaza recarga el ánimo al instante. ¡Mira mi puesto!",
                villageName = "Villa Skate Plaza",
                offersShop = true
            ),
            NpcSpec(
                id = "npc_rh_static",
                name = "Static Beat",
                role = "DJ de Freestyle",
                visual = CharacterVisual.STATIC,
                regionId = RegionId.RHYTHM_HOOD,
                x = 960f,
                y = 795f,
                dialogue = "En la Freestyle Arena mezclamos electricidad y compases para motivar a todo el distrito.",
                villageName = "Freestyle Arena"
            )
        ),
        enemies = listOf(
            EnemySpawn("en_rh_1", "Box Punk", EnemyVisual.BOX_ANTAGONISTA, 680f, 360f, 80, 13, 110f, 2, dropItemId = "slice_pizza"),
            EnemySpawn("boss_dinamita", "Dinamita (Stick Mokay)", EnemyVisual.BOSS_DINAMITA, 840f, 340f, 150, 16, 125f, 2, isBoss = true, bossStageId = "boss_2_duo", dropItemId = "vinilo_chispa"),
            EnemySpawn("boss_molotov", "Molotov (Literal Punk)", EnemyVisual.BOSS_MOLOTOV, 960f, 370f, 160, 17, 118f, 2, isBoss = true, bossStageId = "boss_2_duo", dropBookId = "libro_ritmo")
        ),
        gates = listOf(
            GateSpec("gate_rhythm_plaza", "Tren a Plaza Infinita", RegionId.RHYTHM_HOOD, RegionId.PLAZA_INFINITA, 1080f, 540f, 180f, 620f),
            GateSpec("gate_rhythm_arena", "Paso a Arena Viva", RegionId.RHYTHM_HOOD, RegionId.ARENA_VIVA, 540f, 940f, 540f, 160f)
        )
    ),

    // 5. ARENA VIVA (Distrito Deportivo - Nivel 2)
    RegionSpec(
        id = RegionId.ARENA_VIVA,
        mapX = 0.24f,
        mapY = 0.48f,
        worldWidth = 1150f,
        worldHeight = 1000f,
        hasWaterMoat = true,
        hasTrainTrack = true,
        isSportsZone = true,
        cobblestonePlazaRadius = 200f,
        theme = RegionThemeColors(
            groundPrimary = Color(0xFF2D5E34),
            groundSecondary = Color(0xFF3E7D46),
            pathColor = Color(0xFFE6C88C),
            waterOrVoid = Color(0xFF1D7FA8),
            accentColor = Color(0xFFFFC24D),
            citizenPrimary = Color(0xFFFFC24D),
            citizenOutfit = Color(0xFF1E88E5)
        ),
        landmarks = listOf(
            LandmarkSpec("av_estadio", "Estadio Arena Viva", "Torneos y gloria", 560f, 430f, 200f, 150f, Color(0xFFFFC24D), LandmarkKind.STADIUM),
            LandmarkSpec("av_bmx", "BMX Park", "Rampas extremas", 230f, 235f, 165f, 120f, Color(0xFFFF6A3D), LandmarkKind.SKATE_PARK),
            LandmarkSpec("av_playa", "Playa Activa", "Energía marina", 280f, 775f, 180f, 120f, Color(0xFF2EC4B6), LandmarkKind.CAFE),
            LandmarkSpec("av_basket", "Basket Courts", "Competencias 1v1", 890f, 250f, 165f, 120f, Color(0xFFFF9F1C), LandmarkKind.BASKET_COURT)
        ),
        houses = listOf(
            HouseSpec("avh1", "Gimnasio Olimpo", 405f, 180f, 118f, 88f, Color(0xFF1E88E5), Color(0xFFF4E7D1), HouseStyle.TOWNHOUSE),
            HouseSpec("avh2", "Pabellón Trofeo", 720f, 180f, 118f, 88f, Color(0xFFFF6A3D), Color(0xFFF4E7D1), HouseStyle.TOWNHOUSE),
            HouseSpec("avh3", "Tienda Deportiva", 145f, 425f, 112f, 88f, Color(0xFFFFC24D), Color(0xFFEBDCCB), HouseStyle.CAFE_AWNING),
            HouseSpec("avh4", "Villa Surf", 145f, 650f, 112f, 86f, Color(0xFF2EC4B6), Color(0xFFF4E7D1), HouseStyle.TOWNHOUSE),
            HouseSpec("avh5", "Cafetería Zen", 600f, 800f, 124f, 86f, Color(0xFF4CC37A), Color(0xFFEBDCCB), HouseStyle.CAFE_AWNING),
            HouseSpec("avh6", "Club Tenis", 990f, 740f, 116f, 86f, Color(0xFFFF9F1C), Color(0xFFF4E7D1), HouseStyle.TOWNHOUSE)
        ),
        props = listOf(
            PropSpec("avp1", 720f, 580f, PropKind.SOCCER_GOAL),
            PropSpec("avp2", 970f, 580f, PropKind.SOCCER_GOAL),
            PropSpec("avp3", 230f, 700f, PropKind.UMBRELLA_TABLE, 1.05f, Color(0xFFFF6A3D)),
            PropSpec("avp4", 350f, 705f, PropKind.UMBRELLA_TABLE, 1.05f, Color(0xFF2EC4B6)),
            PropSpec("avp5", 450f, 720f, PropKind.UMBRELLA_TABLE, 1.05f, Color(0xFFFFC24D)),
            PropSpec("avp6", 390f, 340f, PropKind.BANNER, 1f, Color(0xFFFFC24D)),
            PropSpec("avp7", 730f, 340f, PropKind.BANNER, 1f, Color(0xFF1E88E5)),
            PropSpec("avp8", 415f, 540f, PropKind.BENCH),
            PropSpec("avp9", 685f, 540f, PropKind.BENCH),
            PropSpec("avp10", 320f, 495f, PropKind.LAMP, 1f, Color(0xFFFFC24D)),
            PropSpec("avp11", 840f, 375f, PropKind.LAMP, 1f, Color(0xFFFFC24D))
        ),
        flora = listOf(
            LivingFloraSpec("af1", 115f, 145f, FloraKind.TREE, 1.2f, 0.3f, Color(0xFF4CC37A)),
            LivingFloraSpec("af2", 1030f, 145f, FloraKind.TREE, 1.2f, 1.4f, Color(0xFF4CC37A)),
            LivingFloraSpec("af3", 180f, 540f, FloraKind.TREE, 1.15f, 0.6f, Color(0xFF39A96B)),
            LivingFloraSpec("af4", 980f, 415f, FloraKind.TREE, 1.15f, 1.9f, Color(0xFF4CC37A)),
            LivingFloraSpec("af5", 1040f, 645f, FloraKind.TREE, 1.15f, 0.8f, Color(0xFF2EC4B6)),
            LivingFloraSpec("af6", 690f, 710f, FloraKind.CUPHEAD_FLOWER, 1f, 2.3f, Color(0xFFFFC24D)),
            LivingFloraSpec("af7", 510f, 710f, FloraKind.CUPHEAD_FLOWER, 1f, 0.9f, Color(0xFFFF6A3D)),
            LivingFloraSpec("af8", 355f, 335f, FloraKind.SHRUB, 1.1f, 1.2f, Color(0xFF4CC37A)),
            LivingFloraSpec("af9", 745f, 335f, FloraKind.SHRUB, 1.1f, 2.1f, Color(0xFF4CC37A)),
            LivingFloraSpec("af10", 480f, 285f, FloraKind.CUPHEAD_FLOWER, 0.95f, 0.4f, Color(0xFF2EC4B6)),
            LivingFloraSpec("af11", 640f, 285f, FloraKind.CUPHEAD_FLOWER, 0.95f, 1.7f, Color(0xFFFFC24D)),
            LivingFloraSpec("af12", 865f, 800f, FloraKind.TREE, 1.1f, 0.2f, Color(0xFF4CC37A)),
            LivingFloraSpec("af13", 105f, 825f, FloraKind.TREE, 1.1f, 1.5f, Color(0xFF2EC4B6)),
            LivingFloraSpec("af14", 390f, 825f, FloraKind.SHRUB, 1.05f, 2.4f, Color(0xFF4CC37A))
        ),
        pickups = listOf(
            WorldPickup("ap_h1", "hierba_zen", x = 420f, y = 660f),
            WorldPickup("ap_h2", "hierba_zen", x = 800f, y = 620f),
            WorldPickup("ap_te", "te_zen", x = 560f, y = 240f)
        ),
        puzzles = emptyList(),
        npcs = listOf(
            NpcSpec(
                id = "npc_floko",
                name = "Floko",
                role = "Entrenador BMX",
                visual = CharacterVisual.FLOKO,
                regionId = RegionId.ARENA_VIVA,
                x = 250f,
                y = 335f,
                dialogue = "¡Cada idea puede crecer! Entrena tu esquiva en el BMX Park y fortalece tu resistencia en mi tienda.",
                villageName = "Aldea BMX Park",
                offersShop = true
            ),
            NpcSpec(
                id = "npc_coim",
                name = "Coim",
                role = "Generadora de Oportunidades",
                visual = CharacterVisual.COIM,
                regionId = RegionId.ARENA_VIVA,
                x = 920f,
                y = 360f,
                dialogue = "Recolecta x2 Hoja de Cactus Zen por las canchas para preparar bebidas de campeonato.",
                questId = "q_arena_zen",
                villageName = "Villa Basket & Tenis"
            ),
            NpcSpec(
                id = "npc_av_cit",
                name = "Kickz Veloz",
                role = "Atleta de Playa",
                visual = CharacterVisual.CITIZEN_SPIKE,
                regionId = RegionId.ARENA_VIVA,
                x = 290f,
                y = 795f,
                dialogue = "Después de correr junto al mar en Playa Activa, nada supera un buen Té Zen.",
                villageName = "Playa Activa"
            )
        ),
        enemies = listOf(
            EnemySpawn("en_av_1", "Chispa Atleta", EnemyVisual.CHISPA_ERRANTE, 780f, 480f, 80, 12, 125f, 2, dropItemId = "hierba_zen")
        ),
        gates = listOf(
            GateSpec("gate_arena_rhythm", "Subir a Rhythm Hood", RegionId.ARENA_VIVA, RegionId.RHYTHM_HOOD, 540f, 120f, 540f, 860f),
            GateSpec("gate_arena_marea", "Ir a Marea Viva", RegionId.ARENA_VIVA, RegionId.MAREA_VIVA, 1020f, 520f, 220f, 540f)
        )
    ),

    // 6. PICASIA (Distrito Cubista - Nivel 2 + Puzzle de Bloques Cubistas)
    RegionSpec(
        id = RegionId.PICASIA,
        mapX = 0.83f,
        mapY = 0.31f,
        worldWidth = 1150f,
        worldHeight = 1000f,
        hasTrainTrack = true,
        cobblestonePlazaRadius = 210f,
        theme = RegionThemeColors(
            groundPrimary = Color(0xFF34274E),
            groundSecondary = Color(0xFF4A376D),
            pathColor = Color(0xFFF3B749),
            waterOrVoid = Color(0xFF1F1633),
            accentColor = Color(0xFFFFB703),
            citizenPrimary = Color(0xFFFFB703),
            citizenOutfit = Color(0xFF2EC4B6)
        ),
        landmarks = listOf(
            LandmarkSpec("pc_formas", "Plaza de las Formas", "Esculturas vivas", 565f, 475f, 165f, 150f, Color(0xFFFFB703), LandmarkKind.FOUNTAIN),
            LandmarkSpec("pc_ilusion", "Calle de la Ilusión", "Puzzle cubista", 955f, 515f, 160f, 130f, Color(0xFF2EC4B6), LandmarkKind.ILLUSION_HALL),
            LandmarkSpec("pc_museo", "Museo de lo Imposible", "Arte surrealista", 255f, 235f, 175f, 132f, Color(0xFFE97893), LandmarkKind.MUSEUM)
        ),
        houses = listOf(
            HouseSpec("pch1", "Estudio Diseño", 770f, 200f, 126f, 92f, Color(0xFFFF6A3D), Color(0xFF6C63FF), HouseStyle.CUBIST_STUDIO),
            HouseSpec("pch2", "Galería Cubo", 415f, 195f, 118f, 90f, Color(0xFF2EC4B6), Color(0xFFE97893), HouseStyle.CUBIST_STUDIO),
            HouseSpec("pch3", "Torre Geometría", 145f, 455f, 114f, 92f, Color(0xFFFFB703), Color(0xFF5A3F8C), HouseStyle.CUBIST_STUDIO),
            HouseSpec("pch4", "Anfiteatro Sur", 390f, 800f, 124f, 88f, Color(0xFFE97893), Color(0xFF6C63FF), HouseStyle.CUBIST_STUDIO),
            HouseSpec("pch5", "Laboratorio Ideas", 785f, 800f, 124f, 88f, Color(0xFF2EC4B6), Color(0xFFFFB703), HouseStyle.CUBIST_STUDIO),
            HouseSpec("pch6", "Estación Cubo", 1015f, 755f, 118f, 86f, Color(0xFF8A65C9), Color(0xFF4A376D), HouseStyle.CUBIST_STUDIO)
        ),
        props = listOf(
            PropSpec("pcp1", 435f, 385f, PropKind.PILLAR, 1f, Color(0xFFFFB703)),
            PropSpec("pcp2", 695f, 385f, PropKind.PILLAR, 1f, Color(0xFF2EC4B6)),
            PropSpec("pcp3", 435f, 645f, PropKind.LAMP, 1f, Color(0xFFFFB703)),
            PropSpec("pcp4", 695f, 645f, PropKind.LAMP, 1f, Color(0xFFE97893)),
            PropSpec("pcp5", 220f, 600f, PropKind.BENCH),
            PropSpec("pcp6", 940f, 260f, PropKind.BANNER, 1f, Color(0xFFFFB703)),
            PropSpec("pcp7", 170f, 780f, PropKind.UMBRELLA_TABLE, 1f, Color(0xFFE97893)),
            PropSpec("pcp8", 495f, 700f, PropKind.BENCH),
            PropSpec("pcp9", 680f, 700f, PropKind.CRATE_STACK)
        ),
        flora = listOf(
            LivingFloraSpec("pcf1", 215f, 680f, FloraKind.CUPHEAD_FLOWER, 1.15f, 0.4f, Color(0xFFFFB703)),
            LivingFloraSpec("pcf2", 955f, 240f, FloraKind.TREE, 1.15f, 1.7f, Color(0xFF2EC4B6)),
            LivingFloraSpec("pcf3", 110f, 175f, FloraKind.TREE, 1.15f, 0.3f, Color(0xFFE97893)),
            LivingFloraSpec("pcf4", 1030f, 180f, FloraKind.TREE, 1.15f, 1.2f, Color(0xFFFFB703)),
            LivingFloraSpec("pcf5", 130f, 345f, FloraKind.CRYSTAL_ROOT, 1.1f, 0.9f, Color(0xFFFF6A3D)),
            LivingFloraSpec("pcf6", 1030f, 360f, FloraKind.SHRUB, 1.1f, 2.0f, Color(0xFF2EC4B6)),
            LivingFloraSpec("pcf7", 300f, 520f, FloraKind.CUPHEAD_FLOWER, 1.05f, 1.3f, Color(0xFF2EC4B6)),
            LivingFloraSpec("pcf8", 470f, 310f, FloraKind.SHRUB, 1.05f, 0.7f, Color(0xFFFFB703)),
            LivingFloraSpec("pcf9", 675f, 310f, FloraKind.CRYSTAL_ROOT, 1.05f, 2.3f, Color(0xFFE97893)),
            LivingFloraSpec("pcf10", 240f, 820f, FloraKind.TREE, 1.1f, 0.5f, Color(0xFF2EC4B6)),
            LivingFloraSpec("pcf11", 920f, 820f, FloraKind.TREE, 1.1f, 1.4f, Color(0xFFFFB703)),
            LivingFloraSpec("pcf12", 455f, 665f, FloraKind.CUPHEAD_FLOWER, 1f, 2.6f, Color(0xFFE97893)),
            LivingFloraSpec("pcf13", 685f, 665f, FloraKind.CUPHEAD_FLOWER, 1f, 0.2f, Color(0xFF6C63FF))
        ),
        pickups = listOf(
            WorldPickup("pcp_polvo", "polvo_estelar", x = 350f, y = 440f),
            WorldPickup("pcp_elixir", "elixir_chispa", x = 580f, y = 760f)
        ),
        puzzles = listOf(
            PuzzleSpec(
                id = "puz_picasia_blocks",
                title = "Bloques de la Ilusión",
                regionId = RegionId.PICASIA,
                type = PuzzleType.CUBIST_BLOCKS,
                hint = "Empuja los 2 bloques cubistas sobre sus marcas doradas en la Calle de la Ilusión.",
                nodes = listOf(
                    PuzzleNode("pb1", x = 760f, y = 420f, targetX = 880f, targetY = 420f),
                    PuzzleNode("pb2", x = 760f, y = 620f, targetX = 880f, targetY = 620f)
                ),
                rewardItemId = "prisma_cubista"
            )
        ),
        npcs = listOf(
            NpcSpec(
                id = "npc_vivi",
                name = "Vivi Mokay",
                role = "Exploradora Curiosa",
                visual = CharacterVisual.VIVI,
                regionId = RegionId.PICASIA,
                x = 910f,
                y = 520f,
                dialogue = "¡Empuja los 2 bloques cubistas hacia las runas doradas en la Calle de la Ilusión para liberar el Prisma Cubista!",
                questId = "q_picasia_blocks",
                villageName = "Calle de la Ilusión"
            ),
            NpcSpec(
                id = "npc_pc_cit",
                name = "Idea Cubista",
                role = "Curadora del Anfiteatro",
                visual = CharacterVisual.CITIZEN_SPRAY,
                regionId = RegionId.PICASIA,
                x = 265f,
                y = 785f,
                dialogue = "En el Anfiteatro Sur las formas se rompen y crean algo nuevo. Pasa por mi galería de intercambio.",
                villageName = "Anfiteatro Sur",
                offersShop = true
            ),
            NpcSpec(
                id = "npc_pc_glitch",
                name = "Glitch Geométrico",
                role = "Ilusionista del Museo",
                visual = CharacterVisual.GLITCH,
                regionId = RegionId.PICASIA,
                x = 255f,
                y = 340f,
                dialogue = "Si el mundo parece torcido en el Museo de lo Imposible, es señal de que estás mirando desde otro ángulo.",
                villageName = "Museo de lo Imposible"
            )
        ),
        enemies = listOf(
            EnemySpawn("en_pc_1", "Cubo Rebelde", EnemyVisual.CUBO_REBELDE, 620f, 280f, 85, 13, 105f, 2, dropItemId = "polvo_estelar")
        ),
        gates = listOf(
            GateSpec("gate_picasia_artelia", "Puente a Artelia", RegionId.PICASIA, RegionId.ARTELIA, 560f, 120f, 560f, 860f),
            GateSpec("gate_picasia_sombravia", "Bajar a Sombravía", RegionId.PICASIA, RegionId.SOMBRAVIA, 580f, 880f, 580f, 160f)
        )
    ),

    // 7. ARTELIA (Distrito Renacentista - Nivel 2 + Puzzle Antorchas Foskay)
    RegionSpec(
        id = RegionId.ARTELIA,
        mapX = 0.70f,
        mapY = 0.16f,
        worldWidth = 1200f,
        worldHeight = 1050f,
        hasCloudsBorder = true,
        cobblestonePlazaRadius = 225f,
        theme = RegionThemeColors(
            groundPrimary = Color(0xFF47573C),
            groundSecondary = Color(0xFF607351),
            pathColor = Color(0xFFF4E7D1),
            waterOrVoid = Color(0xFF2874A6),
            accentColor = Color(0xFFE0A94F),
            citizenPrimary = Color(0xFFF4E7D1),
            citizenOutfit = Color(0xFF6E69A6)
        ),
        landmarks = listOf(
            LandmarkSpec("ar_biblio", "Gran Biblioteca de Artelia", "Manuscritos antiguos", 600f, 210f, 220f, 145f, Color(0xFF6C63FF), LandmarkKind.CATHEDRAL_LIBRARY),
            LandmarkSpec("ar_humanismo", "Plaza Humanismo", "Centro del distrito", 600f, 520f, 165f, 150f, Color(0xFFE0A94F), LandmarkKind.FOUNTAIN),
            LandmarkSpec("ar_observatorio", "Observatorio de Ideas", "Conexión con Constelaria", 225f, 235f, 160f, 135f, Color(0xFF6C63FF), LandmarkKind.OBSERVATORY),
            LandmarkSpec("ar_jardines", "Jardines Geométricos", "Calma y armonía", 230f, 700f, 175f, 130f, Color(0xFF4CC37A), LandmarkKind.GARDEN),
            LandmarkSpec("ar_galeria", "Galería de Maestros", "Obras eternas", 950f, 675f, 175f, 132f, Color(0xFFFFC24D), LandmarkKind.GALLERY)
        ),
        houses = listOf(
            HouseSpec("arh1", "Academia Da Vinci", 415f, 190f, 118f, 92f, Color(0xFF6C63FF), Color(0xFFF4E7D1), HouseStyle.RENAISSANCE_VILLA),
            HouseSpec("arh2", "Atelier Maestros", 790f, 190f, 118f, 92f, Color(0xFFE0A94F), Color(0xFFF4E7D1), HouseStyle.RENAISSANCE_VILLA),
            HouseSpec("arh3", "Taller Escultura", 990f, 240f, 120f, 94f, Color(0xFF6E69A6), Color(0xFFEBDCCB), HouseStyle.RENAISSANCE_VILLA),
            HouseSpec("arh4", "Café de Artistas", 430f, 845f, 126f, 90f, Color(0xFFD94E4E), Color(0xFFF4E7D1), HouseStyle.CAFE_AWNING),
            HouseSpec("arh5", "Anfiteatro Sabiduría", 770f, 845f, 124f, 90f, Color(0xFF6C63FF), Color(0xFFF4E7D1), HouseStyle.RENAISSANCE_VILLA),
            HouseSpec("arh6", "Ateliers Norte", 1070f, 455f, 114f, 90f, Color(0xFFE0A94F), Color(0xFFF4E7D1), HouseStyle.RENAISSANCE_VILLA),
            HouseSpec("arh7", "Estudio Clásico", 130f, 465f, 112f, 88f, Color(0xFF6E69A6), Color(0xFFF4E7D1), HouseStyle.RENAISSANCE_VILLA)
        ),
        props = listOf(
            PropSpec("arp1", 495f, 425f, PropKind.PILLAR, 1f, Color(0xFFE0A94F)),
            PropSpec("arp2", 705f, 425f, PropKind.PILLAR, 1f, Color(0xFFE0A94F)),
            PropSpec("arp3", 495f, 625f, PropKind.LAMP, 1f, Color(0xFFFFC24D)),
            PropSpec("arp4", 705f, 625f, PropKind.LAMP, 1f, Color(0xFFFFC24D)),
            PropSpec("arp5", 385f, 755f, PropKind.UMBRELLA_TABLE, 1f, Color(0xFFD94E4E)),
            PropSpec("arp6", 480f, 755f, PropKind.UMBRELLA_TABLE, 1f, Color(0xFFD94E4E)),
            PropSpec("arp7", 365f, 520f, PropKind.BENCH),
            PropSpec("arp8", 835f, 520f, PropKind.BENCH),
            PropSpec("arp9", 545f, 300f, PropKind.BANNER, 1f, Color(0xFF6C63FF)),
            PropSpec("arp10", 655f, 300f, PropKind.BANNER, 1f, Color(0xFF6C63FF)),
            PropSpec("arp11", 230f, 600f, PropKind.FENCE_H),
            PropSpec("arp12", 855f, 355f, PropKind.PILLAR, 1f, Color(0xFF6C63FF))
        ),
        flora = listOf(
            LivingFloraSpec("arf1", 375f, 390f, FloraKind.TREE, 1.2f, 0.3f, Color(0xFF4F9D5A)),
            LivingFloraSpec("arf2", 825f, 390f, FloraKind.TREE, 1.2f, 1.6f, Color(0xFF4F9D5A)),
            LivingFloraSpec("arf3", 400f, 690f, FloraKind.CUPHEAD_FLOWER, 1f, 0.9f, Color(0xFFB7B0E6)),
            LivingFloraSpec("arf4", 790f, 690f, FloraKind.CUPHEAD_FLOWER, 1f, 2.2f, Color(0xFFE0A94F)),
            LivingFloraSpec("arf5", 110f, 160f, FloraKind.TREE, 1.15f, 0.4f, Color(0xFF5FA85B)),
            LivingFloraSpec("arf6", 1080f, 165f, FloraKind.TREE, 1.15f, 1.2f, Color(0xFF5FA85B)),
            LivingFloraSpec("arf7", 115f, 355f, FloraKind.SHRUB, 1.1f, 0.7f, Color(0xFF4CC37A)),
            LivingFloraSpec("arf8", 1075f, 355f, FloraKind.SHRUB, 1.1f, 1.9f, Color(0xFF4CC37A)),
            LivingFloraSpec("arf9", 120f, 610f, FloraKind.TREE, 1.15f, 2.5f, Color(0xFF4F9D5A)),
            LivingFloraSpec("arf10", 1065f, 610f, FloraKind.TREE, 1.15f, 0.8f, Color(0xFF4F9D5A)),
            LivingFloraSpec("arf11", 315f, 540f, FloraKind.SHRUB, 1.05f, 1.4f, Color(0xFF6BAE6B)),
            LivingFloraSpec("arf12", 890f, 540f, FloraKind.SHRUB, 1.05f, 2.3f, Color(0xFF6BAE6B)),
            LivingFloraSpec("arf13", 310f, 895f, FloraKind.TREE, 1.15f, 0.2f, Color(0xFF4F9D5A)),
            LivingFloraSpec("arf14", 930f, 895f, FloraKind.TREE, 1.15f, 1.8f, Color(0xFF4F9D5A)),
            LivingFloraSpec("arf15", 525f, 695f, FloraKind.CUPHEAD_FLOWER, 0.95f, 0.5f, Color(0xFFE97893)),
            LivingFloraSpec("arf16", 675f, 695f, FloraKind.CUPHEAD_FLOWER, 0.95f, 1.6f, Color(0xFF6C63FF))
        ),
        pickups = listOf(
            WorldPickup("arp_hierba", "hierba_zen", x = 240f, y = 520f),
            WorldPickup("arp_polvo", "polvo_estelar", x = 940f, y = 420f)
        ),
        puzzles = listOf(
            PuzzleSpec(
                id = "puz_artelia_torches",
                title = "Antorchas de la Sabiduría",
                regionId = RegionId.ARTELIA,
                type = PuzzleType.TORCH_SEQUENCE,
                hint = "Usa el botón FOSKAY cerca de las 3 antorchas (1→2→3) frente a la Gran Biblioteca para recibir el Manuscrito de Sombras.",
                nodes = listOf(
                    PuzzleNode("at1", x = 470f, y = 370f, orderIndex = 1),
                    PuzzleNode("at2", x = 600f, y = 340f, orderIndex = 2),
                    PuzzleNode("at3", x = 730f, y = 370f, orderIndex = 3)
                ),
                rewardBookId = "libro_artelia"
            )
        ),
        npcs = listOf(
            NpcSpec(
                id = "npc_zenkay",
                name = "Zenkay",
                role = "El Cactus Zen",
                visual = CharacterVisual.ZENKAY,
                regionId = RegionId.ARTELIA,
                x = 240f,
                y = 760f,
                dialogue = "La paz no es alejarse. Invoca a Foskay frente a cada antorcha de la Biblioteca en orden 1, 2 y 3.",
                questId = "q_artelia_torches",
                villageName = "Jardines Geométricos"
            ),
            NpcSpec(
                id = "npc_ar_cit",
                name = "Shroom Maestro",
                role = "Archivista de Galería",
                visual = CharacterVisual.CITIZEN_SHROOM,
                regionId = RegionId.ARTELIA,
                x = 950f,
                y = 755f,
                dialogue = "La Galería de Maestros y la Biblioteca guardan secretos de luz. Aquí puedes abastecerte antes de Sombravía.",
                villageName = "Galería de Maestros",
                offersShop = true
            ),
            NpcSpec(
                id = "npc_ar_obs",
                name = "Brew Astrónomo",
                role = "Guardián del Observatorio",
                visual = CharacterVisual.CITIZEN_BREW,
                regionId = RegionId.ARTELIA,
                x = 235f,
                y = 340f,
                dialogue = "Desde este Observatorio trazamos la ruta estelar que une Artelia con Constelaria.",
                villageName = "Observatorio de Ideas"
            )
        ),
        enemies = listOf(
            EnemySpawn("en_ar_1", "Cubo Guardián", EnemyVisual.CUBO_REBELDE, 760f, 560f, 85, 13, 105f, 2, dropItemId = "polvo_estelar")
        ),
        gates = listOf(
            GateSpec("gate_artelia_plaza", "Volver a Plaza Infinita", RegionId.ARTELIA, RegionId.PLAZA_INFINITA, 160f, 840f, 960f, 260f),
            GateSpec("gate_artelia_picasia", "Ir a Picasia", RegionId.ARTELIA, RegionId.PICASIA, 560f, 920f, 560f, 180f)
        )
    ),

    // 8. CONSTELARIA (Sueños e Imaginación - Nivel 2)
    RegionSpec(
        id = RegionId.CONSTELARIA,
        mapX = 0.50f,
        mapY = 0.10f,
        worldWidth = 1150f,
        worldHeight = 1000f,
        hasCloudsBorder = true,
        cobblestonePlazaRadius = 215f,
        theme = RegionThemeColors(
            groundPrimary = Color(0xFF281F4A),
            groundSecondary = Color(0xFF423275),
            pathColor = Color(0xFFD6C8FF),
            waterOrVoid = Color(0xFF150F2B),
            accentColor = Color(0xFFB7B0E6),
            citizenPrimary = Color(0xFFE6F2FF),
            citizenOutfit = Color(0xFF8BB3FF)
        ),
        landmarks = listOf(
            LandmarkSpec("cn_palacio", "Palacio de los Sueños", "Cúpulas estelares", 580f, 215f, 210f, 145f, Color(0xFFB7B0E6), LandmarkKind.DREAM_PALACE),
            LandmarkSpec("cn_nubes", "Mirador de Nubes", "Refugio de Birdy", 230f, 520f, 165f, 128f, Color(0xFF6FD0E8), LandmarkKind.OBSERVATORY),
            LandmarkSpec("cn_tormenta", "Nube Tormenta", "Travesuras de Nubi", 910f, 520f, 165f, 128f, Color(0xFFFFC24D), LandmarkKind.FOUNTAIN)
        ),
        houses = listOf(
            HouseSpec("cnh1", "Templo Estrella", 370f, 200f, 120f, 90f, Color(0xFFFFC24D), Color(0xFFE6D9FF), HouseStyle.CLOUD_TEMPLE),
            HouseSpec("cnh2", "Cúpula Lunar", 790f, 200f, 120f, 90f, Color(0xFF6FD0E8), Color(0xFFE6D9FF), HouseStyle.CLOUD_TEMPLE),
            HouseSpec("cnh3", "Casa Cometa", 160f, 325f, 112f, 88f, Color(0xFFE97893), Color(0xFFF4E7D1), HouseStyle.CLOUD_TEMPLE),
            HouseSpec("cnh4", "Villa Aurora", 990f, 325f, 112f, 88f, Color(0xFF9D4EDD), Color(0xFFE6D9FF), HouseStyle.CLOUD_TEMPLE),
            HouseSpec("cnh5", "Observatorio Sur", 340f, 795f, 122f, 88f, Color(0xFF6FD0E8), Color(0xFFF4E7D1), HouseStyle.CLOUD_TEMPLE),
            HouseSpec("cnh6", "Jardín Onírico", 815f, 795f, 122f, 88f, Color(0xFFFFC24D), Color(0xFFE6D9FF), HouseStyle.CLOUD_TEMPLE)
        ),
        props = listOf(
            PropSpec("cnp1", 460f, 445f, PropKind.PILLAR, 1f, Color(0xFFFFC24D)),
            PropSpec("cnp2", 700f, 445f, PropKind.PILLAR, 1f, Color(0xFFFFC24D)),
            PropSpec("cnp3", 460f, 610f, PropKind.LAMP, 1f, Color(0xFF6FD0E8)),
            PropSpec("cnp4", 700f, 610f, PropKind.LAMP, 1f, Color(0xFFFFC24D)),
            PropSpec("cnp5", 500f, 340f, PropKind.BANNER, 1f, Color(0xFFB7B0E6)),
            PropSpec("cnp6", 660f, 340f, PropKind.BANNER, 1f, Color(0xFFFFC24D)),
            PropSpec("cnp7", 455f, 705f, PropKind.BENCH),
            PropSpec("cnp8", 705f, 705f, PropKind.BENCH)
        ),
        flora = listOf(
            LivingFloraSpec("cnf1", 345f, 375f, FloraKind.CUPHEAD_FLOWER, 1.1f, 0.2f, Color(0xFFB7B0E6)),
            LivingFloraSpec("cnf2", 805f, 375f, FloraKind.CUPHEAD_FLOWER, 1.1f, 1.4f, Color(0xFF6FD0E8)),
            LivingFloraSpec("cnf3", 580f, 675f, FloraKind.CRYSTAL_ROOT, 1.15f, 0.8f, Color(0xFFFFC24D)),
            LivingFloraSpec("cnf4", 110f, 175f, FloraKind.TREE, 1.15f, 0.4f, Color(0xFF9D4EDD)),
            LivingFloraSpec("cnf5", 1030f, 175f, FloraKind.TREE, 1.15f, 1.5f, Color(0xFF6FD0E8)),
            LivingFloraSpec("cnf6", 120f, 700f, FloraKind.TREE, 1.15f, 2.1f, Color(0xFFB7B0E6)),
            LivingFloraSpec("cnf7", 1020f, 700f, FloraKind.TREE, 1.15f, 0.9f, Color(0xFFE97893)),
            LivingFloraSpec("cnf8", 440f, 520f, FloraKind.CRYSTAL_ROOT, 1.05f, 1.2f, Color(0xFF6FD0E8)),
            LivingFloraSpec("cnf9", 720f, 520f, FloraKind.CRYSTAL_ROOT, 1.05f, 2.4f, Color(0xFFE97893)),
            LivingFloraSpec("cnf10", 230f, 700f, FloraKind.CUPHEAD_FLOWER, 1.05f, 0.7f, Color(0xFFFFC24D)),
            LivingFloraSpec("cnf11", 920f, 700f, FloraKind.CUPHEAD_FLOWER, 1.05f, 1.8f, Color(0xFFB7B0E6)),
            LivingFloraSpec("cnf12", 480f, 820f, FloraKind.SHRUB, 1.05f, 0.3f, Color(0xFF8BB3FF)),
            LivingFloraSpec("cnf13", 680f, 820f, FloraKind.SHRUB, 1.05f, 1.4f, Color(0xFF9D4EDD))
        ),
        pickups = listOf(
            WorldPickup("cnp_polvo1", "polvo_estelar", x = 360f, y = 650f),
            WorldPickup("cnp_polvo2", "polvo_estelar", x = 800f, y = 650f),
            WorldPickup("cnp_fruto", "fruto_creacion", x = 580f, y = 440f)
        ),
        puzzles = emptyList(),
        npcs = listOf(
            NpcSpec(
                id = "npc_birdy",
                name = "Birdy",
                role = "El Soñador",
                visual = CharacterVisual.BIRDY,
                regionId = RegionId.CONSTELARIA,
                x = 230f,
                y = 560f,
                dialogue = "¿Y si volamos un poco más alto? Reúne x2 Polvo Estelar en las nubes y craftea Pociones Estelares.",
                questId = "q_constelaria_stars",
                villageName = "Mirador de Nubes"
            ),
            NpcSpec(
                id = "npc_nubi",
                name = "Nubi",
                role = "La Nube Bromista",
                visual = CharacterVisual.NUBI,
                regionId = RegionId.CONSTELARIA,
                x = 915f,
                y = 560f,
                dialogue = "¡JA JA JA! ¡Desde Nube Tormenta veo Sombravía donde esperan Laughing Cat y Street Dino!",
                villageName = "Nube Tormenta"
            ),
            NpcSpec(
                id = "npc_cn_cit",
                name = "Puff Soñador",
                role = "Custodio del Palacio",
                visual = CharacterVisual.CITIZEN_PUFF,
                regionId = RegionId.CONSTELARIA,
                x = 580f,
                y = 315f,
                dialogue = "En el Palacio de los Sueños intercambiamos reliquias luminosas para el último tramo de tu viaje.",
                villageName = "Palacio de los Sueños",
                offersShop = true
            )
        ),
        enemies = listOf(
            EnemySpawn("en_cn_1", "Chispa Astral", EnemyVisual.CHISPA_ERRANTE, 580f, 360f, 95, 14, 120f, 2, dropItemId = "polvo_estelar")
        ),
        gates = listOf(
            GateSpec("gate_const_plaza", "Bajar a Plaza Infinita", RegionId.CONSTELARIA, RegionId.PLAZA_INFINITA, 580f, 900f, 600f, 190f)
        )
    )
)
