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
import com.example.game.model.RegionId
import com.example.game.model.RegionSpec
import com.example.game.model.RegionThemeColors
import com.example.game.model.WorldPickup

internal fun buildTier3Regions(): List<RegionSpec> = listOf(
    // 9. SOMBRAVIA (Reino de las Sombras Interiores - Nivel 3 + JEFE 3: LAUGHING CAT + T-REX STREET DINO)
    RegionSpec(
        id = RegionId.SOMBRAVIA,
        mapX = 0.76f,
        mapY = 0.48f,
        worldWidth = 1280f,
        worldHeight = 1100f,
        hasTrainTrack = true,
        cobblestonePlazaRadius = 230f,
        theme = RegionThemeColors(
            groundPrimary = Color(0xFF1E1330),
            groundSecondary = Color(0xFF311F4D),
            pathColor = Color(0xFF6B5399),
            waterOrVoid = Color(0xFF0D0817),
            accentColor = Color(0xFF9D4EDD),
            citizenPrimary = Color(0xFF9D4EDD),
            citizenOutfit = Color(0xFF3C096C),
            darknessAlpha = 0.76f
        ),
        landmarks = listOf(
            LandmarkSpec("sm_reflejo", "Plaza del Reflejo", "Duelo contra Cat & Street Dino", 640f, 520f, 175f, 165f, Color(0xFF9D4EDD), LandmarkKind.FOUNTAIN),
            LandmarkSpec("sm_dudas", "Calle de las Dudas", "Territorio de Douty", 230f, 235f, 175f, 132f, Color(0xFF7B2CBF), LandmarkKind.GOTHIC_MANOR),
            LandmarkSpec("sm_laberinto", "Laberinto del Miedo", "Pruebas interiores", 1020f, 260f, 175f, 138f, Color(0xFFE0AAFF), LandmarkKind.MAZE),
            LandmarkSpec("sm_teatro", "Teatro de las Máscaras", "Masky y Static", 920f, 825f, 180f, 132f, Color(0xFFC77DFF), LandmarkKind.THEATER),
            LandmarkSpec("sm_faro", "Faro de la Superación", "Llama violeta de esperanza", 240f, 815f, 160f, 135f, Color(0xFFFFC24D), LandmarkKind.BEACON_TOWER)
        ),
        houses = listOf(
            HouseSpec("smh1", "Mansión Eco", 430f, 195f, 120f, 92f, Color(0xFF7B2CBF), Color(0xFF2D1E42), HouseStyle.GOTHIC_HOUSE),
            HouseSpec("smh2", "Torre Niebla", 785f, 195f, 120f, 92f, Color(0xFF9D4EDD), Color(0xFF362452), HouseStyle.GOTHIC_HOUSE),
            HouseSpec("smh3", "Casa Máscara", 1110f, 470f, 118f, 92f, Color(0xFFC77DFF), Color(0xFF2D1E42), HouseStyle.GOTHIC_HOUSE),
            HouseSpec("smh4", "Posada Penumbra", 125f, 395f, 114f, 88f, Color(0xFF7B2CBF), Color(0xFF362452), HouseStyle.GOTHIC_HOUSE),
            HouseSpec("smh5", "Estación Sombravía", 435f, 865f, 126f, 88f, Color(0xFF9D4EDD), Color(0xFF2B1C3D), HouseStyle.GOTHIC_HOUSE),
            HouseSpec("smh6", "Archivo Sombras", 675f, 865f, 122f, 88f, Color(0xFF7B2CBF), Color(0xFF33214A), HouseStyle.GOTHIC_HOUSE)
        ),
        props = listOf(
            PropSpec("smp1", 515f, 430f, PropKind.LAMP, 1.05f, Color(0xFFE0AAFF)),
            PropSpec("smp2", 765f, 430f, PropKind.LAMP, 1.05f, Color(0xFFE0AAFF)),
            PropSpec("smp3", 515f, 625f, PropKind.LAMP, 1.05f, Color(0xFFFFC24D)),
            PropSpec("smp4", 765f, 625f, PropKind.LAMP, 1.05f, Color(0xFFFFC24D)),
            PropSpec("smp5", 375f, 680f, PropKind.PILLAR, 1f, Color(0xFF9D4EDD)),
            PropSpec("smp6", 905f, 680f, PropKind.PILLAR, 1f, Color(0xFF9D4EDD)),
            PropSpec("smp7", 230f, 465f, PropKind.BANNER, 1f, Color(0xFF9D4EDD)),
            PropSpec("smp8", 1045f, 415f, PropKind.BANNER, 1f, Color(0xFFE0AAFF)),
            PropSpec("smp9", 470f, 750f, PropKind.BENCH),
            PropSpec("smp10", 805f, 750f, PropKind.BENCH),
            PropSpec("smp11", 390f, 320f, PropKind.FENCE_H),
            PropSpec("smp12", 890f, 380f, PropKind.FENCE_H)
        ),
        flora = listOf(
            LivingFloraSpec("sf1", 395f, 385f, FloraKind.TREE, 1.2f, 0.3f, Color(0xFF5A189A)),
            LivingFloraSpec("sf2", 885f, 440f, FloraKind.TREE, 1.2f, 1.5f, Color(0xFF7B2CBF)),
            LivingFloraSpec("sf3", 440f, 755f, FloraKind.INK_FERN, 1.15f, 0.9f, Color(0xFF9D4EDD)),
            LivingFloraSpec("sf4", 780f, 690f, FloraKind.CRYSTAL_ROOT, 1.15f, 2.0f, Color(0xFFE0AAFF)),
            LivingFloraSpec("sf5", 110f, 165f, FloraKind.TREE, 1.2f, 0.2f, Color(0xFF7B2CBF)),
            LivingFloraSpec("sf6", 1155f, 165f, FloraKind.TREE, 1.2f, 1.4f, Color(0xFF5A189A)),
            LivingFloraSpec("sf7", 115f, 695f, FloraKind.TREE, 1.15f, 0.7f, Color(0xFF7B2CBF)),
            LivingFloraSpec("sf8", 1145f, 720f, FloraKind.TREE, 1.15f, 2.2f, Color(0xFF9D4EDD)),
            LivingFloraSpec("sf9", 345f, 625f, FloraKind.CRYSTAL_ROOT, 1.1f, 0.5f, Color(0xFFC77DFF)),
            LivingFloraSpec("sf10", 925f, 595f, FloraKind.CRYSTAL_ROOT, 1.1f, 1.8f, Color(0xFFFFC24D)),
            LivingFloraSpec("sf11", 550f, 290f, FloraKind.INK_FERN, 1.05f, 1.1f, Color(0xFF9D4EDD)),
            LivingFloraSpec("sf12", 730f, 290f, FloraKind.INK_FERN, 1.05f, 2.4f, Color(0xFFC77DFF)),
            LivingFloraSpec("sf13", 315f, 895f, FloraKind.CUPHEAD_FLOWER, 1f, 0.8f, Color(0xFFE0AAFF)),
            LivingFloraSpec("sf14", 985f, 895f, FloraKind.CUPHEAD_FLOWER, 1f, 1.9f, Color(0xFFFFC24D)),
            LivingFloraSpec("sf15", 205f, 520f, FloraKind.SHRUB, 1.05f, 0.4f, Color(0xFF7B2CBF)),
            LivingFloraSpec("sf16", 1045f, 645f, FloraKind.SHRUB, 1.05f, 1.6f, Color(0xFF9D4EDD))
        ),
        pickups = listOf(
            WorldPickup("smp_cristal1", "cristal_sombra", x = 360f, y = 520f),
            WorldPickup("smp_cristal2", "cristal_sombra", x = 960f, y = 620f),
            WorldPickup("smp_esencia", "esencia_cristal", x = 280f, y = 700f)
        ),
        puzzles = emptyList(),
        npcs = listOf(
            NpcSpec("npc_douty", "Douty", "La Duda Eterna", CharacterVisual.DOUTY, RegionId.SOMBRAVIA, 280f, 390f, "¿Estás seguro? ¡Usa a Foskay para iluminar la oscuridad y avanzar sin miedo!"),
            NpcSpec("npc_shade", "Shade", "Eco del Miedo", CharacterVisual.SHADE, RegionId.SOMBRAVIA, 820f, 700f, "Cuando iluminas tus sombras, se convierten en valor."),
            NpcSpec("npc_vivi_sombra", "Vivi Mokay", "Exploradora", CharacterVisual.VIVI, RegionId.SOMBRAVIA, 240f, 620f, "Laughing Cat volvió junto a Street Dino en la Plaza del Reflejo. ¡Derrótalos!", questId = "q_sombravia_climax")
        ),
        enemies = listOf(
            EnemySpawn("en_sm_1", "Sombra Glitch", EnemyVisual.SOMBRA_MENOR, 460f, 340f, 110, 16, 115f, 3, dropItemId = "cristal_sombra"),
            EnemySpawn("boss_cat_3", "Laughing Cat (Revancha)", EnemyVisual.BOSS_LAUGHING_CAT, 580f, 520f, 170, 18, 138f, 3, isBoss = true, bossStageId = "boss_3_cat_dino", dropItemId = "cristal_sombra"),
            EnemySpawn("boss_dino_3", "Street Dino (T-Rex)", EnemyVisual.BOSS_STREET_DINO, 700f, 520f, 240, 22, 112f, 3, isBoss = true, bossStageId = "boss_3_cat_dino", dropItemId = "corona_superacion")
        ),
        gates = listOf(
            GateSpec("gate_sombra_plaza", "Estación a Plaza Infinita", RegionId.SOMBRAVIA, RegionId.PLAZA_INFINITA, 140f, 540f, 960f, 680f),
            GateSpec("gate_sombra_picasia", "Subir a Picasia", RegionId.SOMBRAVIA, RegionId.PICASIA, 580f, 120f, 580f, 800f)
        )
    ),

    // 10. BOSQUE OLVIDADO (Territorio Restringido + Portal Ancestral)
    RegionSpec(
        id = RegionId.BOSQUE_OLVIDADO,
        mapX = 0.50f,
        mapY = 0.69f,
        worldWidth = 1200f,
        worldHeight = 1050f,
        hasWaterMoat = true,
        cobblestonePlazaRadius = 195f,
        theme = RegionThemeColors(
            groundPrimary = Color(0xFF14332B),
            groundSecondary = Color(0xFF1F4B3F),
            pathColor = Color(0xFF7D6B91),
            waterOrVoid = Color(0xFF0A1D19),
            accentColor = Color(0xFF9D4EDD),
            citizenPrimary = Color(0xFF9D4EDD),
            citizenOutfit = Color(0xFF2EC4B6),
            darknessAlpha = 0.52f
        ),
        landmarks = listOf(
            LandmarkSpec("bo_raices", "Raíces Gigantes", "Árboles milenarios", 230f, 235f, 175f, 135f, Color(0xFF4CC37A), LandmarkKind.CRYSTAL_HEART),
            LandmarkSpec("bo_ruinas", "Ruinas Susurrantes", "Civilización antigua", 945f, 250f, 175f, 135f, Color(0xFFB7B0E6), LandmarkKind.CATHEDRAL_LIBRARY),
            LandmarkSpec("bo_lagos", "Lagos Luminosos", "Aguas de recuerdos", 915f, 780f, 185f, 130f, Color(0xFF2EC4B6), LandmarkKind.GLOWING_LAKE),
            LandmarkSpec("bo_circulo", "Círculo de las Raíces", "Portal Ancestral", 600f, 620f, 185f, 175f, Color(0xFF9D4EDD), LandmarkKind.ANCESTRAL_PORTAL)
        ),
        houses = listOf(
            HouseSpec("boh1", "Santuario Musgo", 400f, 210f, 118f, 90f, Color(0xFF2EC4B6), Color(0xFF2B4C3F), HouseStyle.MOSS_SHRINE),
            HouseSpec("boh2", "Altar Raíz", 785f, 210f, 118f, 90f, Color(0xFF9D4EDD), Color(0xFF334A45), HouseStyle.MOSS_SHRINE),
            HouseSpec("boh3", "Refugio Guardián", 160f, 515f, 114f, 88f, Color(0xFF4CC37A), Color(0xFF2B4C3F), HouseStyle.MOSS_SHRINE),
            HouseSpec("boh4", "Templo Eco", 1040f, 505f, 114f, 88f, Color(0xFF9D4EDD), Color(0xFF334A45), HouseStyle.MOSS_SHRINE),
            HouseSpec("boh5", "Ermita Niebla", 260f, 855f, 120f, 88f, Color(0xFF2EC4B6), Color(0xFF2B4C3F), HouseStyle.MOSS_SHRINE)
        ),
        props = listOf(
            PropSpec("bop1", 495f, 535f, PropKind.PILLAR, 1f, Color(0xFF9D4EDD)),
            PropSpec("bop2", 705f, 535f, PropKind.PILLAR, 1f, Color(0xFF9D4EDD)),
            PropSpec("bop3", 480f, 715f, PropKind.LAMP, 1f, Color(0xFF2EC4B6)),
            PropSpec("bop4", 720f, 715f, PropKind.LAMP, 1f, Color(0xFFE0AAFF)),
            PropSpec("bop5", 415f, 645f, PropKind.MUSHROOM_CLUSTER, 1.15f, Color(0xFF9D4EDD)),
            PropSpec("bop6", 775f, 600f, PropKind.MUSHROOM_CLUSTER, 1.15f, Color(0xFF2EC4B6)),
            PropSpec("bop7", 290f, 440f, PropKind.MUSHROOM_CLUSTER, 1.1f, Color(0xFFFFC24D)),
            PropSpec("bop8", 550f, 345f, PropKind.WELL, 1f, Color(0xFF2EC4B6)),
            PropSpec("bop9", 680f, 845f, PropKind.MUSHROOM_CLUSTER, 1.2f, Color(0xFFE0AAFF))
        ),
        flora = listOf(
            LivingFloraSpec("bf1", 345f, 415f, FloraKind.TREE, 1.3f, 0.2f, Color(0xFF2EC4B6)),
            LivingFloraSpec("bf2", 855f, 415f, FloraKind.TREE, 1.3f, 1.1f, Color(0xFF4CC37A)),
            LivingFloraSpec("bf3", 380f, 755f, FloraKind.CRYSTAL_ROOT, 1.2f, 2.0f, Color(0xFF9D4EDD)),
            LivingFloraSpec("bf4", 760f, 820f, FloraKind.CUPHEAD_FLOWER, 1.1f, 0.6f, Color(0xFFE0AAFF)),
            LivingFloraSpec("bf5", 115f, 165f, FloraKind.TREE, 1.35f, 0.4f, Color(0xFF2E8B57)),
            LivingFloraSpec("bf6", 1080f, 165f, FloraKind.TREE, 1.35f, 1.5f, Color(0xFF2EC4B6)),
            LivingFloraSpec("bf7", 110f, 340f, FloraKind.TREE, 1.25f, 0.9f, Color(0xFF4CC37A)),
            LivingFloraSpec("bf8", 1085f, 355f, FloraKind.TREE, 1.25f, 2.1f, Color(0xFF2E8B57)),
            LivingFloraSpec("bf9", 115f, 705f, FloraKind.TREE, 1.3f, 1.3f, Color(0xFF4CC37A)),
            LivingFloraSpec("bf10", 1075f, 675f, FloraKind.TREE, 1.3f, 0.5f, Color(0xFF2EC4B6)),
            LivingFloraSpec("bf11", 435f, 870f, FloraKind.TREE, 1.25f, 1.7f, Color(0xFF2E8B57)),
            LivingFloraSpec("bf12", 825f, 895f, FloraKind.TREE, 1.25f, 2.6f, Color(0xFF4CC37A)),
            LivingFloraSpec("bf13", 230f, 635f, FloraKind.INK_FERN, 1.15f, 0.8f, Color(0xFF2EC4B6)),
            LivingFloraSpec("bf14", 940f, 595f, FloraKind.INK_FERN, 1.15f, 1.9f, Color(0xFF9D4EDD)),
            LivingFloraSpec("bf15", 480f, 345f, FloraKind.CRYSTAL_ROOT, 1.1f, 0.3f, Color(0xFFE0AAFF)),
            LivingFloraSpec("bf16", 700f, 345f, FloraKind.CRYSTAL_ROOT, 1.1f, 1.4f, Color(0xFF2EC4B6)),
            LivingFloraSpec("bf17", 545f, 860f, FloraKind.CUPHEAD_FLOWER, 1f, 2.2f, Color(0xFF2EC4B6)),
            LivingFloraSpec("bf18", 665f, 860f, FloraKind.CUPHEAD_FLOWER, 1f, 0.7f, Color(0xFF9D4EDD))
        ),
        pickups = listOf(
            WorldPickup("bp_tomo", "fruto_creacion", bookId = "libro_ancestral", x = 860f, y = 380f, respawns = false),
            WorldPickup("bp_esencia", "esencia_cristal", x = 320f, y = 620f)
        ),
        puzzles = emptyList(),
        npcs = listOf(
            NpcSpec("npc_bo_guard", "Rooty Explorador", "Guardián del Bosque", CharacterVisual.ROOTY, RegionId.BOSQUE_OLVIDADO, 520f, 480f, "Para activar el Portal Ancestral necesitas el Tomo del Árbol Original (MOKAYA) y las 4 fuerzas: Creatividad, Valentía, Curiosidad y Amistad.")
        ),
        enemies = listOf(
            EnemySpawn("en_bo_1", "Sombra Antigua", EnemyVisual.SOMBRA_MENOR, 740f, 340f, 120, 17, 115f, 3, dropItemId = "cristal_sombra")
        ),
        gates = listOf(
            GateSpec("gate_bosque_marea", "Volver a Marea Viva", RegionId.BOSQUE_OLVIDADO, RegionId.MAREA_VIVA, 600f, 120f, 600f, 860f),
            GateSpec(
                id = "gate_portal_ancestral",
                label = "Portal Ancestral → El Árbol Original",
                fromRegion = RegionId.BOSQUE_OLVIDADO,
                toRegion = RegionId.ARBOL_ORIGINAL,
                x = 600f,
                y = 700f,
                spawnX = 600f,
                spawnY = 840f,
                requiredBookId = "libro_ancestral",
                requiredKeyword = "MOKAYA",
                requiredForces = listOf("CREATIVIDAD", "VALENTIA", "CURIOSIDAD", "AMISTAD"),
                lockedMessage = "Reúne las 4 fuerzas (Creatividad, Valentía, Curiosidad, Amistad) y lee el Tomo del Árbol Original (MOKAYA)."
            )
        )
    ),

    // 11. EL ARBOL ORIGINAL (Corazón Primigenio de Mokaya - Clímax)
    RegionSpec(
        id = RegionId.ARBOL_ORIGINAL,
        mapX = 0.50f,
        mapY = 0.89f,
        worldWidth = 1150f,
        worldHeight = 1000f,
        hasCloudsBorder = true,
        cobblestonePlazaRadius = 235f,
        theme = RegionThemeColors(
            groundPrimary = Color(0xFF28133D),
            groundSecondary = Color(0xFF48236E),
            pathColor = Color(0xFFFFC24D),
            waterOrVoid = Color(0xFF140824),
            accentColor = Color(0xFFFF6A3D),
            citizenPrimary = Color(0xFFFFC24D),
            citizenOutfit = Color(0xFFFF6A3D)
        ),
        landmarks = listOf(
            LandmarkSpec("ao_corazon", "Corazón de Mokaya", "Cristal Creativo Primigenio", 580f, 435f, 220f, 185f, Color(0xFFFF6A3D), LandmarkKind.CRYSTAL_HEART),
            LandmarkSpec("ao_corona", "Corona Creadora", "Llama Eterna", 580f, 185f, 190f, 130f, Color(0xFFFFC24D), LandmarkKind.DREAM_PALACE)
        ),
        houses = listOf(
            HouseSpec("aoh1", "Pabellón Luz", 240f, 250f, 120f, 92f, Color(0xFFFFC24D), Color(0xFF422163), HouseStyle.CRYSTAL_PAVILION),
            HouseSpec("aoh2", "Pabellón Chispa", 915f, 250f, 120f, 92f, Color(0xFFFF6A3D), Color(0xFF422163), HouseStyle.CRYSTAL_PAVILION),
            HouseSpec("aoh3", "Altar Raíces", 210f, 755f, 120f, 92f, Color(0xFF2EC4B6), Color(0xFF361B52), HouseStyle.CRYSTAL_PAVILION),
            HouseSpec("aoh4", "Santuario Estelar", 945f, 755f, 120f, 92f, Color(0xFFE97893), Color(0xFF361B52), HouseStyle.CRYSTAL_PAVILION)
        ),
        props = listOf(
            PropSpec("aop1", 435f, 355f, PropKind.PILLAR, 1.05f, Color(0xFFFFC24D)),
            PropSpec("aop2", 725f, 355f, PropKind.PILLAR, 1.05f, Color(0xFFFFC24D)),
            PropSpec("aop3", 435f, 575f, PropKind.LAMP, 1.05f, Color(0xFFFF6A3D)),
            PropSpec("aop4", 725f, 575f, PropKind.LAMP, 1.05f, Color(0xFFFFC24D)),
            PropSpec("aop5", 500f, 800f, PropKind.BANNER, 1f, Color(0xFFFF6A3D)),
            PropSpec("aop6", 660f, 800f, PropKind.BANNER, 1f, Color(0xFFFFC24D))
        ),
        flora = listOf(
            LivingFloraSpec("aof1", 280f, 385f, FloraKind.CRYSTAL_ROOT, 1.3f, 0.2f, Color(0xFFFF6A3D)),
            LivingFloraSpec("aof2", 880f, 385f, FloraKind.CRYSTAL_ROOT, 1.3f, 1.2f, Color(0xFFFFC24D)),
            LivingFloraSpec("aof3", 320f, 700f, FloraKind.CUPHEAD_FLOWER, 1.2f, 2.0f, Color(0xFFE97893)),
            LivingFloraSpec("aof4", 840f, 700f, FloraKind.CUPHEAD_FLOWER, 1.2f, 0.7f, Color(0xFF2EC4B6)),
            LivingFloraSpec("aof5", 130f, 470f, FloraKind.TREE, 1.25f, 0.5f, Color(0xFF9D4EDD)),
            LivingFloraSpec("aof6", 1020f, 470f, FloraKind.TREE, 1.25f, 1.6f, Color(0xFFFF6A3D)),
            LivingFloraSpec("aof7", 360f, 195f, FloraKind.CRYSTAL_ROOT, 1.2f, 0.9f, Color(0xFF2EC4B6)),
            LivingFloraSpec("aof8", 800f, 195f, FloraKind.CRYSTAL_ROOT, 1.2f, 2.3f, Color(0xFFE97893)),
            LivingFloraSpec("aof9", 180f, 600f, FloraKind.CUPHEAD_FLOWER, 1.1f, 0.4f, Color(0xFFFFC24D)),
            LivingFloraSpec("aof10", 970f, 600f, FloraKind.CUPHEAD_FLOWER, 1.1f, 1.8f, Color(0xFF9D4EDD)),
            LivingFloraSpec("aof11", 400f, 835f, FloraKind.TREE, 1.15f, 1.1f, Color(0xFF2EC4B6)),
            LivingFloraSpec("aof12", 760f, 835f, FloraKind.TREE, 1.15f, 2.5f, Color(0xFFFFC24D))
        ),
        pickups = listOf(
            WorldPickup("aop_fruto", "fruto_creacion", x = 440f, y = 620f),
            WorldPickup("aop_esencia", "esencia_cristal", x = 720f, y = 620f)
        ),
        puzzles = emptyList(),
        npcs = listOf(
            NpcSpec("npc_lumen", "Lumen", "Guardián de la Luz", CharacterVisual.LUMEN, RegionId.ARBOL_ORIGINAL, 420f, 480f, "¡Bienvenido al Árbol Original, Boxy! Tu chispa y la de Foskay restauraron los 7 colores de la creatividad."),
            NpcSpec("npc_sparky", "Sparky", "Guardián de la Chispa", CharacterVisual.SPARKY, RegionId.ARBOL_ORIGINAL, 740f, 480f, "De aquí nacen todas las ideas, personajes y regiones de Mokaya."),
            NpcSpec("npc_rooty", "Rooty", "Guardián de las Raíces", CharacterVisual.ROOTY, RegionId.ARBOL_ORIGINAL, 420f, 740f, "Estamos conectados por raíces que atraviesan el tiempo y el espacio."),
            NpcSpec("npc_nova", "Nova", "Guardiana de las Estrellas", CharacterVisual.NOVA, RegionId.ARBOL_ORIGINAL, 740f, 740f, "¡Una chispa puede cambiarlo todo!")
        ),
        enemies = emptyList(),
        gates = listOf(
            GateSpec("gate_arbol_bosque", "Regresar al Bosque Olvidado", RegionId.ARBOL_ORIGINAL, RegionId.BOSQUE_OLVIDADO, 580f, 920f, 600f, 760f)
        )
    )
)
