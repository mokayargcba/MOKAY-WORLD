package com.example.game.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.data.MokayWorldCatalog
import com.example.game.engine.GameWorldState
import com.example.game.model.GateSpec
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BoxyLavender
import com.example.ui.theme.CardSlate
import com.example.ui.theme.DeepTwilight
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.RunicTeal
import com.example.ui.theme.SparkGold
import com.example.ui.theme.StaminaEmerald
import com.example.ui.theme.VitalCrimson
import com.example.ui.theme.WarmParchment

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GrimoireDialog(
    state: GameWorldState,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 720.dp)
                .heightIn(max = 390.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DeepTwilight),
            border = BorderStroke(2.dp, BoxyLavender.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Grimorio de Mokaya",
                        style = MaterialTheme.typography.titleLarge,
                        color = WarmParchment
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar grimorio", tint = WarmParchment)
                    }
                }

                Text(
                    text = "Palabras clave descubiertas (${state.discoveredKeywords.size}/${MokayWorldCatalog.books.size}):",
                    style = MaterialTheme.typography.labelLarge,
                    color = BoxyLavender
                )
                Spacer(Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (state.discoveredKeywords.isEmpty()) {
                        Text(
                            text = "Recoge libros brillantes en las regiones para revelar palabras clave.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = WarmParchment.copy(alpha = 0.65f)
                        )
                    } else {
                        state.discoveredKeywords.forEach { kw ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = FlameOrange.copy(alpha = 0.22f),
                                border = BorderStroke(1.dp, SparkGold)
                            ) {
                                Text(
                                    text = kw,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = SparkGold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(MokayWorldCatalog.books.values.toList(), key = { it.id }) { book ->
                        val collected = book.id in state.collectedBookIds
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = CardSlate,
                            border = BorderStroke(
                                1.2.dp,
                                if (collected) AntiqueGold.copy(alpha = 0.7f) else BoxyLavender.copy(alpha = 0.2f)
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
                                    Text(
                                        text = if (collected) book.title else "Libro Sellado (${book.regionId.title})",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (collected) SparkGold else WarmParchment.copy(alpha = 0.6f),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = if (collected) Icons.Filled.VpnKey else Icons.Filled.Lock,
                                        contentDescription = null,
                                        tint = if (collected) RunicTeal else Color.Gray
                                    )
                                }

                                if (collected) {
                                    Text(
                                        text = book.excerpt,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                        color = WarmParchment.copy(alpha = 0.9f)
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = FlameOrange.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "CLAVE: ${book.keyword}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = SparkGold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        if (book.forceGranted != null) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = RunicTeal.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = "FUERZA: ${book.forceGranted}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = RunicTeal,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "Explora ${book.regionId.title} para encontrar este tomo ancestral.",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                        color = WarmParchment.copy(alpha = 0.55f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SealedGateDialog(
    gate: GateSpec,
    state: GameWorldState,
    onAttemptUnlock: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var keywordText by remember(gate.id) { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .widthIn(max = 580.dp)
                .heightIn(max = 385.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DeepTwilight),
            border = BorderStroke(2.dp, FlameOrange)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.LockOpen, contentDescription = null, tint = SparkGold)
                        Text(
                            text = gate.label,
                            style = MaterialTheme.typography.titleLarge,
                            color = WarmParchment
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = WarmParchment)
                    }
                }

                Text(
                    text = gate.lockedMessage.ifEmpty { "Introduce la palabra clave del libro correspondiente para abrir el paso hacia ${gate.toRegion.title}." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = WarmParchment.copy(alpha = 0.88f)
                )

                // Requirements Checklist
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    gate.requiredBookId?.let { bookId ->
                        val ok = bookId in state.collectedBookIds
                        val title = MokayWorldCatalog.books[bookId]?.title ?: bookId
                        RequirementLine(label = "Libro: $title", met = ok)
                    }
                    gate.requiredQuestItemId?.let { itemId ->
                        val ok = (state.inventory[itemId] ?: 0) > 0
                        val name = MokayWorldCatalog.items[itemId]?.name ?: itemId
                        RequirementLine(label = "Reliquia: $name", met = ok)
                    }
                    gate.requiredBossStageId?.let { bossId ->
                        val ok = bossId in state.defeatedBossStages
                        RequirementLine(label = "Jefe derrotado", met = ok)
                    }
                    if (gate.requiredForces.isNotEmpty()) {
                        val ok = gate.requiredForces.all { it in state.collectedForces }
                        RequirementLine(
                            label = "Fuerzas: ${gate.requiredForces.joinToString()}",
                            met = ok
                        )
                    }
                }

                if (gate.requiredKeyword != null) {
                    if (state.discoveredKeywords.isNotEmpty()) {
                        Text(
                            text = "Toca una palabra clave de tu Grimorio:",
                            style = MaterialTheme.typography.labelSmall,
                            color = BoxyLavender
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            state.discoveredKeywords.forEach { kw ->
                                AssistChip(
                                    onClick = { keywordText = kw },
                                    label = { Text(kw, fontWeight = FontWeight.Bold) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = CardSlate,
                                        labelColor = SparkGold
                                    )
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = keywordText,
                        onValueChange = { keywordText = it.uppercase() },
                        label = { Text("Palabra Clave") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SparkGold,
                            unfocusedBorderColor = BoxyLavender,
                            focusedTextColor = WarmParchment,
                            unfocusedTextColor = WarmParchment,
                            focusedLabelColor = SparkGold,
                            unfocusedLabelColor = BoxyLavender
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gate_keyword_input")
                    )
                }

                Button(
                    onClick = { onAttemptUnlock(keywordText) },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("unlock_gate_button")
                ) {
                    Text("Abrir Paso", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun RequirementLine(label: String, met: Boolean) {
    Text(
        text = (if (met) "✓ " else "• ") + label,
        style = MaterialTheme.typography.labelSmall,
        color = if (met) StaminaEmerald else VitalCrimson
    )
}
