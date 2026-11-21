package com.example.game.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.data.MokayWorldCatalog
import com.example.game.engine.DialogueChoice
import com.example.game.engine.DialogueManager
import com.example.game.engine.GameLoopEngine
import com.example.game.engine.GameWorldState
import com.example.game.model.QuestSpec
import com.example.game.viewmodel.ActiveDialogueSession
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BoxyLavender
import com.example.ui.theme.CardSlate
import com.example.ui.theme.DeepTwilight
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.InkBlack
import com.example.ui.theme.RunicTeal
import com.example.ui.theme.SparkGold
import com.example.ui.theme.StaminaEmerald
import com.example.ui.theme.VitalCrimson
import com.example.ui.theme.WarmParchment

@Composable
fun QuestLogDialog(
    state: GameWorldState,
    onTrackQuest: (String) -> Unit,
    onCompleteQuest: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var filterTab by remember { mutableIntStateOf(0) } // 0=Activas, 1=Completadas, 2=Todas

    val filteredQuests = remember(filterTab, state.completedQuests) {
        when (filterTab) {
            0 -> MokayWorldCatalog.quests.filter { it.id !in state.completedQuests }
            1 -> MokayWorldCatalog.quests.filter { it.id in state.completedQuests }
            else -> MokayWorldCatalog.quests
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 740.dp)
                .heightIn(max = 405.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DeepTwilight),
            border = BorderStroke(2.dp, RunicTeal.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Bitácora de Misiones",
                            style = MaterialTheme.typography.titleLarge,
                            color = WarmParchment
                        )
                        Text(
                            text = "Completadas: ${state.completedQuests.size}/${MokayWorldCatalog.quests.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = RunicTeal
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar misiones", tint = WarmParchment)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Activas", "Completadas", "Todas").forEachIndexed { idx, label ->
                        FilterChip(
                            selected = filterTab == idx,
                            onClick = { filterTab = idx },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RunicTeal,
                                selectedLabelColor = InkBlack
                            )
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(filteredQuests, key = { it.id }) { quest ->
                        val done = quest.id in state.completedQuests
                        val ready = !done && GameLoopEngine.isQuestReadyToComplete(state, quest)
                        val isTracked = state.trackedQuestId == quest.id
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = CardSlate,
                            border = BorderStroke(
                                1.4.dp,
                                when {
                                    done -> StaminaEmerald
                                    ready -> SparkGold
                                    isTracked -> RunicTeal
                                    else -> AntiqueGold.copy(alpha = 0.35f)
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = (if (quest.isMainStory) "★ " else "") + quest.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (done) StaminaEmerald else SparkGold
                                        )
                                        Text(
                                            text = "${quest.regionId.title} • ${quest.villageName.ifEmpty { quest.giverName }}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = BoxyLavender
                                        )
                                    }
                                    if (done) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "Completada",
                                            tint = StaminaEmerald
                                        )
                                    }
                                }

                                Text(
                                    text = quest.description,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                    color = WarmParchment.copy(alpha = 0.9f)
                                )

                                QuestChecklist(quest = quest, state = state)

                                val rewardName = MokayWorldCatalog.items[quest.rewardItemId]?.name ?: quest.rewardItemId
                                val boosts = buildList {
                                    add("+${quest.rewardChispas} Chispas")
                                    add("x${quest.rewardQuantity} $rewardName")
                                    if (quest.rewardForce != null) add("Fuerza ${quest.rewardForce}")
                                    if (quest.rewardMaxHpBoost > 0) add("+${quest.rewardMaxHpBoost} Vida Máx")
                                    if (quest.rewardMaxStaminaBoost > 0) add("+${quest.rewardMaxStaminaBoost} Estamina Máx")
                                    if (quest.rewardMaxManaBoost > 0) add("+${quest.rewardMaxManaBoost} Magia Máx")
                                }.joinToString(" • ")

                                Text(
                                    text = "Recompensa: $boosts",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RunicTeal
                                )

                                if (!done) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { onTrackQuest(quest.id) },
                                            border = BorderStroke(
                                                1.dp,
                                                if (isTracked) RunicTeal else BoxyLavender.copy(alpha = 0.45f)
                                            ),
                                            modifier = Modifier.height(44.dp)
                                        ) {
                                            Icon(Icons.Filled.GpsFixed, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (isTracked) RunicTeal else WarmParchment)
                                            Spacer(Modifier.size(4.dp))
                                            Text(
                                                text = if (isTracked) "Fijada en HUD" else "Fijar en HUD",
                                                color = if (isTracked) RunicTeal else WarmParchment,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                        if (ready) {
                                            Button(
                                                onClick = { onCompleteQuest(quest.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = StaminaEmerald, contentColor = InkBlack),
                                                modifier = Modifier
                                                    .height(44.dp)
                                                    .testTag("complete_quest_${quest.id}")
                                            ) {
                                                Text("Completar", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestChecklist(quest: QuestSpec, state: GameWorldState) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        quest.requiredItemId?.let { itemId ->
            val owned = state.inventory[itemId] ?: 0
            val itemName = MokayWorldCatalog.items[itemId]?.name ?: itemId
            val ok = owned >= quest.requiredCount
            Text(
                text = (if (ok) "✓ " else "• ") + "$itemName ($owned/${quest.requiredCount})",
                style = MaterialTheme.typography.labelSmall,
                color = if (ok) StaminaEmerald else VitalCrimson
            )
        }
        quest.requiredBookId?.let { bookId ->
            val ok = bookId in state.collectedBookIds
            val title = MokayWorldCatalog.books[bookId]?.title ?: bookId
            Text(
                text = (if (ok) "✓ " else "• ") + "Libro: $title",
                style = MaterialTheme.typography.labelSmall,
                color = if (ok) StaminaEmerald else VitalCrimson
            )
        }
        quest.requiredBossStageId?.let { bossId ->
            val ok = bossId in state.defeatedBossStages
            Text(
                text = (if (ok) "✓ " else "• ") + "Derrotar al Jefe de la región",
                style = MaterialTheme.typography.labelSmall,
                color = if (ok) StaminaEmerald else VitalCrimson
            )
        }
        quest.requiredPuzzleId?.let { puzId ->
            val ok = puzId in state.solvedPuzzleIds
            Text(
                text = (if (ok) "✓ " else "• ") + "Resolver acertijo de la zona",
                style = MaterialTheme.typography.labelSmall,
                color = if (ok) StaminaEmerald else VitalCrimson
            )
        }
        if (quest.requiredForces.isNotEmpty()) {
            val ok = quest.requiredForces.all { it in state.collectedForces }
            Text(
                text = (if (ok) "✓ " else "• ") + "Fuerzas: ${quest.requiredForces.joinToString()}",
                style = MaterialTheme.typography.labelSmall,
                color = if (ok) StaminaEmerald else VitalCrimson
            )
        }
    }
}

@Composable
fun NpcDialogueDialog(
    session: ActiveDialogueSession,
    dialogueManager: DialogueManager,
    state: GameWorldState,
    onSelectChoice: (DialogueChoice) -> Unit,
    onAcceptQuest: (String) -> Unit,
    onCompleteQuest: (String) -> Unit,
    onOpenShop: () -> Unit,
    onDismiss: () -> Unit
) {
    val npc = session.npc
    val relationshipScore = state.npcRelationships[npc.id] ?: 0
    val tier = dialogueManager.relationshipTier(relationshipScore)
    val tierColor = Color(tier.badgeColorHex)
    val currentNode = dialogueManager.getNode(session.tree, session.currentNodeId)
    val choices = currentNode?.let { dialogueManager.availableChoices(it, relationshipScore) }.orEmpty()
    val quest = npc.questId?.let { qid -> MokayWorldCatalog.quests.firstOrNull { it.id == qid } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 640.dp)
                .heightIn(max = 415.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DeepTwilight),
            border = BorderStroke(2.dp, AntiqueGold)
        ) {
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = npc.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = SparkGold
                        )
                        Text(
                            text = "${npc.role} • ${npc.villageName.ifEmpty { npc.regionId.title }}",
                            style = MaterialTheme.typography.labelSmall,
                            color = BoxyLavender
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        session.lastDelta?.takeIf { it != 0 }?.let { delta ->
                            val deltaColor = if (delta > 0) StaminaEmerald else VitalCrimson
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = deltaColor.copy(alpha = 0.22f),
                                border = BorderStroke(1.dp, deltaColor),
                                modifier = Modifier.testTag("dialogue_delta_badge")
                            ) {
                                Text(
                                    text = if (delta > 0) "+$delta Afinidad" else "$delta Afinidad",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = deltaColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = tierColor.copy(alpha = 0.18f),
                            border = BorderStroke(1.2.dp, tierColor),
                            modifier = Modifier.testTag("npc_relationship_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "Nivel de relación",
                                    tint = tierColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${tier.label} ($relationshipScore)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = tierColor
                                )
                            }
                        }
                    }
                }

                // Affinity meter + Shop perk indicator
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    val normalizedProgress = ((relationshipScore + 100f) / 200f).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { normalizedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = tierColor,
                        trackColor = CardSlate
                    )
                    if (npc.offersShop) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalOffer,
                                contentDescription = null,
                                tint = if (tier.discountPercent > 0) RunicTeal else BoxyLavender,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (tier.discountPercent > 0) {
                                    "Beneficio Aliado: -${tier.discountPercent}% en su Tienda de Aldea"
                                } else {
                                    "Alcanza +${DialogueManager.SHOP_DISCOUNT_THRESHOLD} de afinidad para -${DialogueManager.SHOP_DISCOUNT_PERCENT}% en tienda"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (tier.discountPercent > 0) RunicTeal else BoxyLavender.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                // NPC Dialogue Bubble
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CardSlate,
                    border = BorderStroke(1.dp, BoxyLavender.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val moodLabel = currentNode?.mood?.replaceFirstChar { it.uppercase() } ?: "Amigable"
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AntiqueGold.copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = "Ánimo: $moodLabel",
                                style = MaterialTheme.typography.labelSmall,
                                color = AntiqueGold,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "\"${currentNode?.text ?: npc.dialogue}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            color = WarmParchment
                        )
                    }
                }

                // Player Response Choices
                if (choices.isNotEmpty()) {
                    Text(
                        text = "Elige tu respuesta:",
                        style = MaterialTheme.typography.labelMedium,
                        color = SparkGold,
                        fontWeight = FontWeight.Bold
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        choices.forEach { choice ->
                            val toneColor = when (choice.tone.lowercase()) {
                                "valiente", "amigable", "disculpa", "entusiasta", "empatico" -> StaminaEmerald
                                "curioso", "sabio", "reflexivo" -> RunicTeal
                                "impaciente", "burlon", "rudo" -> VitalCrimson
                                else -> BoxyLavender
                            }
                            val claimKey = "${npc.id}:${choice.id}"
                            val hasUnclaimedBonus = choice.chispasBonus > 0 && claimKey !in state.claimedDialogueChoices
                            OutlinedButton(
                                onClick = { onSelectChoice(choice) },
                                border = BorderStroke(1.2.dp, toneColor.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                                    .testTag("dialogue_choice_${choice.id}")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = choice.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = WarmParchment,
                                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (hasUnclaimedBonus) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = SparkGold.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = "+${choice.chispasBonus} ✨",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = SparkGold,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = toneColor.copy(alpha = 0.16f)
                                        ) {
                                            Text(
                                                text = choice.tone.replaceFirstChar { it.uppercase() },
                                                style = MaterialTheme.typography.labelSmall,
                                                color = toneColor,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Quest Panel
                if (quest != null) {
                    val done = quest.id in state.completedQuests
                    val ready = !done && GameLoopEngine.isQuestReadyToComplete(state, quest)
                    val active = quest.id in state.activeQuestIds
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (done) StaminaEmerald.copy(alpha = 0.16f) else FlameOrange.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, if (done) StaminaEmerald else FlameOrange)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = when {
                                    done -> "¡Completada!: ${quest.title}"
                                    ready -> "¡Lista para entregar!: ${quest.title}"
                                    else -> "Misión: ${quest.title}"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = if (done) StaminaEmerald else SparkGold
                            )
                            Text(
                                text = quest.description,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                color = WarmParchment
                            )
                            QuestChecklist(quest = quest, state = state)

                            if (!done) {
                                when {
                                    ready -> Button(
                                        onClick = { onCompleteQuest(quest.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = StaminaEmerald, contentColor = InkBlack),
                                        modifier = Modifier.fillMaxWidth().height(48.dp)
                                    ) {
                                        Text("Entregar y Completar (+${quest.rewardChispas} Chispas)", fontWeight = FontWeight.Bold)
                                    }
                                    !active -> OutlinedButton(
                                        onClick = { onAcceptQuest(quest.id) },
                                        border = BorderStroke(1.dp, SparkGold),
                                        modifier = Modifier.fillMaxWidth().height(48.dp)
                                    ) {
                                        Text("Aceptar Misión", color = SparkGold, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (npc.offersShop) {
                        OutlinedButton(
                            onClick = onOpenShop,
                            border = BorderStroke(1.2.dp, RunicTeal),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("npc_open_shop_button")
                        ) {
                            Icon(Icons.Filled.Storefront, contentDescription = null, tint = RunicTeal, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(6.dp))
                            Text(
                                text = if (tier.discountPercent > 0) "Tienda (-${tier.discountPercent}%)" else "Tienda de Aldea",
                                color = RunicTeal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("npc_dialogue_close_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                    ) {
                        Text("Despedirse", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
