package com.example.game.ui

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import com.example.R
import com.example.game.model.CharacterVisual
import com.example.game.model.Direction
import com.example.game.model.EnemyVisual

enum class BoxyFaceExpr {
    HAPPY,
    EXCITED,
    ANGRY,
    SURPRISED,
    WINK,
    TIRED,
    DETERMINED,
    LAUGH
}

@Immutable
class MokaySpriteBank(
    val boxyFront: ImageBitmap,
    val boxyBack: ImageBitmap,
    val boxyThreeQuarter: ImageBitmap,
    val boxyProfile: ImageBitmap,
    val boxyHero: ImageBitmap,
    val boxyTorsoFront: ImageBitmap,
    val boxyTorsoBack: ImageBitmap,
    val boxyTorsoThreeQuarter: ImageBitmap,
    val boxyTorsoProfile: ImageBitmap,
    val boxyShoeFront: ImageBitmap,
    val boxyShoeSide: ImageBitmap,
    val boxyFaces: Map<BoxyFaceExpr, ImageBitmap>,
    val foskayFront: ImageBitmap,
    val foskayHero: ImageBitmap,
    val npcSprites: Map<CharacterVisual, ImageBitmap>,
    val enemySprites: Map<EnemyVisual, ImageBitmap>
) {
    fun boxyForDirection(direction: Direction): ImageBitmap = when (direction) {
        Direction.FRONT -> boxyFront
        Direction.BACK -> boxyBack
        Direction.LEFT, Direction.RIGHT -> boxyThreeQuarter
    }

    fun boxyTorsoForDirection(direction: Direction, useSideProfile: Boolean): ImageBitmap = when (direction) {
        Direction.FRONT -> boxyTorsoFront
        Direction.BACK -> boxyTorsoBack
        Direction.LEFT, Direction.RIGHT -> if (useSideProfile) boxyTorsoProfile else boxyTorsoThreeQuarter
    }
}

@Composable
fun rememberMokaySpriteBank(): MokaySpriteBank {
    val resources: Resources = LocalContext.current.resources
    return remember(resources) {
        MokaySpriteBank(
            boxyFront = ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_front),
            boxyBack = ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_back),
            boxyThreeQuarter = ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_34),
            boxyProfile = ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_profile),
            boxyHero = ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_hero),
            boxyTorsoFront = ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_torso_front),
            boxyTorsoBack = ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_torso_back),
            boxyTorsoThreeQuarter = ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_torso_34),
            boxyTorsoProfile = ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_torso_profile),
            boxyShoeFront = ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_shoe_front),
            boxyShoeSide = ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_shoe_side),
            boxyFaces = mapOf(
                BoxyFaceExpr.HAPPY to ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_face_happy),
                BoxyFaceExpr.EXCITED to ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_face_excited),
                BoxyFaceExpr.ANGRY to ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_face_angry),
                BoxyFaceExpr.SURPRISED to ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_face_surprised),
                BoxyFaceExpr.WINK to ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_face_wink),
                BoxyFaceExpr.TIRED to ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_face_tired),
                BoxyFaceExpr.DETERMINED to ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_face_determined),
                BoxyFaceExpr.LAUGH to ImageBitmap.imageResource(resources, R.drawable.sprite_boxy_face_laugh)
            ),
            foskayFront = ImageBitmap.imageResource(resources, R.drawable.sprite_foskay_front),
            foskayHero = ImageBitmap.imageResource(resources, R.drawable.sprite_foskay_hero),
            npcSprites = mapOf(
                CharacterVisual.BLOOM to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_bloom),
                CharacterVisual.FLOKO to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_floko),
                CharacterVisual.ZENKAY to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_zenkay),
                CharacterVisual.NUBI to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_nubi),
                CharacterVisual.BIRDY to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_birdy),
                CharacterVisual.VIVI to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_vivi),
                CharacterVisual.STAR to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_star),
                CharacterVisual.COIM to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_coim),
                CharacterVisual.RATT to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_ratt),
                CharacterVisual.CLIP to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_clip),
                CharacterVisual.LA_LLAVE to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_llave),
                CharacterVisual.LA_FLOR to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_la_flor),
                CharacterVisual.FINN to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_finn),
                CharacterVisual.LUMI to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_lumi),
                CharacterVisual.OCTO to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_octo),
                CharacterVisual.CORALY to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_coraly),
                CharacterVisual.TULA to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_tula),
                CharacterVisual.SHADE to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_shade),
                CharacterVisual.GLITCH to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_glitch),
                CharacterVisual.STATIC to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_static),
                CharacterVisual.MASKY to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_masky),
                CharacterVisual.VOID to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_void),
                CharacterVisual.DOUTY to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_douty),
                CharacterVisual.LUMEN to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_lumen),
                CharacterVisual.SPARKY to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_sparky),
                CharacterVisual.ROOTY to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_rooty),
                CharacterVisual.NOVA to ImageBitmap.imageResource(resources, R.drawable.sprite_npc_nova),
                CharacterVisual.CITIZEN_RATZ to ImageBitmap.imageResource(resources, R.drawable.sprite_cit_ratz),
                CharacterVisual.CITIZEN_TERRA to ImageBitmap.imageResource(resources, R.drawable.sprite_cit_terra),
                CharacterVisual.CITIZEN_PUFF to ImageBitmap.imageResource(resources, R.drawable.sprite_cit_puff),
                CharacterVisual.CITIZEN_BEEP to ImageBitmap.imageResource(resources, R.drawable.sprite_cit_beep),
                CharacterVisual.CITIZEN_SPRAY to ImageBitmap.imageResource(resources, R.drawable.sprite_cit_spray),
                CharacterVisual.CITIZEN_SHROOM to ImageBitmap.imageResource(resources, R.drawable.sprite_cit_shroom),
                CharacterVisual.CITIZEN_BREW to ImageBitmap.imageResource(resources, R.drawable.sprite_cit_brew),
                CharacterVisual.CITIZEN_SLICE to ImageBitmap.imageResource(resources, R.drawable.sprite_cit_slice),
                CharacterVisual.CITIZEN_SPIKE to ImageBitmap.imageResource(resources, R.drawable.sprite_cit_spike)
            ),
            enemySprites = mapOf(
                EnemyVisual.BOSS_LAUGHING_CAT to ImageBitmap.imageResource(resources, R.drawable.sprite_boss_cat),
                EnemyVisual.BOSS_DINAMITA to ImageBitmap.imageResource(resources, R.drawable.sprite_boss_dinamita),
                EnemyVisual.BOSS_MOLOTOV to ImageBitmap.imageResource(resources, R.drawable.sprite_boss_molotov),
                EnemyVisual.BOSS_STREET_DINO to ImageBitmap.imageResource(resources, R.drawable.sprite_boss_dino),
                EnemyVisual.BOX_ANTAGONISTA to ImageBitmap.imageResource(resources, R.drawable.sprite_enemy_box_antag)
            )
        )
    }
}
