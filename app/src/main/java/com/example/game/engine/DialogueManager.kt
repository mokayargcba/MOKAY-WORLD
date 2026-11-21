package com.example.game.engine

import android.content.res.AssetManager
import com.example.game.model.NpcSpec
import org.json.JSONObject

data class DialogueChoice(
    val id: String,
    val text: String,
    val tone: String,
    val relationshipDelta: Int,
    val nextNodeId: String?,
    val minRelationship: Int? = null,
    val chispasBonus: Int = 0
)

data class DialogueNode(
    val id: String,
    val text: String,
    val mood: String,
    val choices: List<DialogueChoice>
)

data class NpcDialogueTree(
    val npcId: String,
    val startNodeId: String,
    val highAffinityStartNodeId: String? = null,
    val lowAffinityStartNodeId: String? = null,
    val nodes: Map<String, DialogueNode>
)

data class RelationshipTier(
    val label: String,
    val minScore: Int,
    val discountPercent: Int,
    val badgeColorHex: Long
)

data class DialogueApplyOutcome(
    val nextState: GameWorldState,
    val delta: Int,
    val awardedChispas: Int,
    val newScore: Int,
    val nextNodeId: String?
)

class DialogueManager(
    val trees: Map<String, NpcDialogueTree> = emptyMap()
) {

    fun getTreeForNpc(npc: NpcSpec): NpcDialogueTree {
        return trees[npc.id] ?: NpcDialogueTree(
            npcId = npc.id,
            startNodeId = "start",
            nodes = mapOf(
                "start" to DialogueNode(
                    id = "start",
                    text = npc.dialogue,
                    mood = "amigable",
                    choices = listOf(
                        DialogueChoice(
                            id = "fallback_greet",
                            text = "¡Es un gusto saludarte en Mokaya!",
                            tone = "amigable",
                            relationshipDelta = 5,
                            nextNodeId = null
                        )
                    )
                )
            )
        )
    }

    fun resolveStartNodeId(tree: NpcDialogueTree, relationshipScore: Int): String {
        val high = tree.highAffinityStartNodeId
        if (relationshipScore >= HIGH_AFFINITY_THRESHOLD && high != null && high in tree.nodes) {
            return high
        }
        val low = tree.lowAffinityStartNodeId
        if (relationshipScore <= LOW_AFFINITY_THRESHOLD && low != null && low in tree.nodes) {
            return low
        }
        return tree.startNodeId
    }

    fun getNode(tree: NpcDialogueTree, nodeId: String): DialogueNode? = tree.nodes[nodeId]

    fun availableChoices(node: DialogueNode, relationshipScore: Int): List<DialogueChoice> {
        return node.choices.filter { choice ->
            choice.minRelationship == null || relationshipScore >= choice.minRelationship
        }
    }

    fun relationshipTier(score: Int): RelationshipTier = when {
        score >= 70 -> RelationshipTier(
            label = "Vínculo Ancestral",
            minScore = 70,
            discountPercent = SHOP_DISCOUNT_PERCENT,
            badgeColorHex = 0xFFFFC24DL
        )
        score >= SHOP_DISCOUNT_THRESHOLD -> RelationshipTier(
            label = "Aliado",
            minScore = SHOP_DISCOUNT_THRESHOLD,
            discountPercent = SHOP_DISCOUNT_PERCENT,
            badgeColorHex = 0xFF2EC4B6L
        )
        score >= 15 -> RelationshipTier(
            label = "Amistoso",
            minScore = 15,
            discountPercent = 0,
            badgeColorHex = 0xFF4CC37AL
        )
        score > LOW_AFFINITY_THRESHOLD -> RelationshipTier(
            label = "Conocido",
            minScore = LOW_AFFINITY_THRESHOLD + 1,
            discountPercent = 0,
            badgeColorHex = 0xFFB7B0E6L
        )
        else -> RelationshipTier(
            label = "Distante",
            minScore = MIN_RELATIONSHIP,
            discountPercent = 0,
            badgeColorHex = 0xFFD94E4EL
        )
    }

    fun shopDiscountFor(score: Int): Int = relationshipTier(score).discountPercent

    fun applyChoice(
        state: GameWorldState,
        npc: NpcSpec,
        choice: DialogueChoice
    ): DialogueApplyOutcome {
        val prevScore = state.npcRelationships[npc.id] ?: 0
        val newScore = (prevScore + choice.relationshipDelta).coerceIn(MIN_RELATIONSHIP, MAX_RELATIONSHIP)
        val actualDelta = newScore - prevScore

        val claimKey = "${npc.id}:${choice.id}"
        val shouldAwardChispas = choice.chispasBonus > 0 && claimKey !in state.claimedDialogueChoices
        val awardedChispas = if (shouldAwardChispas) choice.chispasBonus else 0
        val nextClaimed = if (shouldAwardChispas) {
            state.claimedDialogueChoices + claimKey
        } else {
            state.claimedDialogueChoices
        }

        val nextRelationships = state.npcRelationships + (npc.id to newScore)
        val toast = buildToast(npc, prevScore, newScore, awardedChispas)

        val nextState = state.copy(
            npcRelationships = nextRelationships,
            claimedDialogueChoices = nextClaimed,
            chispas = state.chispas + awardedChispas,
            toastMessage = toast ?: state.toastMessage,
            hapticTick = if (actualDelta != 0 || awardedChispas > 0) state.hapticTick + 1 else state.hapticTick
        )

        return DialogueApplyOutcome(
            nextState = nextState,
            delta = actualDelta,
            awardedChispas = awardedChispas,
            newScore = newScore,
            nextNodeId = choice.nextNodeId
        )
    }

    private fun buildToast(
        npc: NpcSpec,
        prevScore: Int,
        newScore: Int,
        awardedChispas: Int
    ): String? {
        val unlockedDiscount = prevScore < SHOP_DISCOUNT_THRESHOLD && newScore >= SHOP_DISCOUNT_THRESHOLD
        return when {
            unlockedDiscount && npc.offersShop ->
                "¡${npc.name} es ahora Aliado! (-$SHOP_DISCOUNT_PERCENT% en Tienda)"
            unlockedDiscount ->
                "¡Vínculo Aliado alcanzado con ${npc.name}!"
            awardedChispas > 0 ->
                "${npc.name} te obsequia +$awardedChispas Chispas ✨"
            else -> null
        }
    }

    companion object {
        const val MIN_RELATIONSHIP = -100
        const val MAX_RELATIONSHIP = 100
        const val HIGH_AFFINITY_THRESHOLD = 30
        const val LOW_AFFINITY_THRESHOLD = -15
        const val SHOP_DISCOUNT_THRESHOLD = 35
        const val SHOP_DISCOUNT_PERCENT = 15

        fun fromAssets(
            assets: AssetManager,
            fileName: String = "npc_dialogues.json"
        ): DialogueManager {
            return runCatching {
                val json = assets.open(fileName).bufferedReader().use { it.readText() }
                fromJson(json)
            }.getOrElse { DialogueManager() }
        }

        fun fromJson(json: String): DialogueManager {
            val root = JSONObject(json)
            val treesObj = root.optJSONObject("trees") ?: return DialogueManager()
            val parsedTrees = linkedMapOf<String, NpcDialogueTree>()

            val npcKeys = treesObj.keys()
            while (npcKeys.hasNext()) {
                val npcId = npcKeys.next()
                val treeObj = treesObj.optJSONObject(npcId) ?: continue
                val startNodeId = treeObj.optString("startNodeId", "start")
                val highNodeId = treeObj.optNullableString("highAffinityStartNodeId")
                val lowNodeId = treeObj.optNullableString("lowAffinityStartNodeId")
                val nodesObj = treeObj.optJSONObject("nodes") ?: continue

                val parsedNodes = linkedMapOf<String, DialogueNode>()
                val nodeKeys = nodesObj.keys()
                while (nodeKeys.hasNext()) {
                    val nodeId = nodeKeys.next()
                    val nodeObj = nodesObj.optJSONObject(nodeId) ?: continue
                    val text = nodeObj.optString("text", "")
                    val mood = nodeObj.optString("mood", "amigable")
                    val choicesArr = nodeObj.optJSONArray("choices")
                    val choices = buildList {
                        if (choicesArr != null) {
                            for (i in 0 until choicesArr.length()) {
                                val cObj = choicesArr.optJSONObject(i) ?: continue
                                add(
                                    DialogueChoice(
                                        id = cObj.optString("id", "${nodeId}_$i"),
                                        text = cObj.optString("text", "Continuar"),
                                        tone = cObj.optString("tone", "amigable"),
                                        relationshipDelta = cObj.optInt("relationshipDelta", 0),
                                        nextNodeId = cObj.optNullableString("nextNodeId"),
                                        minRelationship = if (cObj.has("minRelationship") && !cObj.isNull("minRelationship")) {
                                            cObj.getInt("minRelationship")
                                        } else null,
                                        chispasBonus = cObj.optInt("chispasBonus", 0).coerceAtLeast(0)
                                    )
                                )
                            }
                        }
                    }
                    parsedNodes[nodeId] = DialogueNode(
                        id = nodeId,
                        text = text,
                        mood = mood,
                        choices = choices
                    )
                }

                parsedTrees[npcId] = NpcDialogueTree(
                    npcId = npcId,
                    startNodeId = startNodeId,
                    highAffinityStartNodeId = highNodeId,
                    lowAffinityStartNodeId = lowNodeId,
                    nodes = parsedNodes
                )
            }

            return DialogueManager(parsedTrees)
        }

        private fun JSONObject.optNullableString(key: String): String? {
            if (!has(key) || isNull(key)) return null
            val value = optString(key, "")
            return value.takeIf { it.isNotBlank() && it != "null" }
        }
    }
}
