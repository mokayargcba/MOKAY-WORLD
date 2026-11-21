package com.example.game.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.data.MokayRegionsCatalog
import com.example.game.data.MokayWorldCatalog
import com.example.game.engine.AchievementCategory
import com.example.game.engine.AchievementManager
import com.example.game.engine.GameWorldState
import com.example.game.viewmodel.CloudSyncUiState
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
fun AdventureJournalDialog(
    state: GameWorldState,
    syncState: CloudSyncUiState,
    achievementManager: AchievementManager = AchievementManager(),
    onManualSave: () -> Unit,
    onResetAdventure: () -> Unit,
    onSignOut: () -> Unit,
    onDismiss: () -> Unit
) {
    var confirmReset by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf<AchievementCategory?>(null) }

    val (unlockedAchievementsCount, totalAchievementsCount) = remember(state.unlockedAchievementIds) {
        achievementManager.summaryCounts(state)
    }

    val allLandmarksCount = MokayRegionsCatalog.regions.values.sumOf { it.landmarks.size }
    val allVillagesCount = MokayRegionsCatalog.regions.values
        .flatMap { reg -> reg.npcs.mapNotNull { it.villageName.takeIf(String::isNotBlank) } }
        .distinct()
        .size

    val totalMilestones = MokayRegionsCatalog.regions.size +
        allLandmarksCount +
        allVillagesCount +
        MokayWorldCatalog.books.size +
        MokayWorldCatalog.quests.size +
        4 // Forces
    val achievedMilestones = state.discoveredRegions.size +
        state.discoveredLandmarkIds.size +
        state.visitedVillages.size +
        state.collectedBookIds.size +
        state.completedQuests.size +
        state.collectedForces.size
    val progressFrac = (achievedMilestones.toFloat() / totalMilestones.coerceAtLeast(1)).coerceIn(0f, 1f)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .widthIn(max = 700.dp)
                .heightIn(max = 395.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DeepTwilight),
            border = BorderStroke(2.dp, AntiqueGold.copy(alpha = 0.7f))
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 18.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Diario y Guardado en la Nube",
                            style = MaterialTheme.typography.titleLarge,
                            color = WarmParchment
                        )
                        Text(
                            text = "Progreso total de Mokaya: ${(progressFrac * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = SparkGold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar diario", tint = WarmParchment)
                    }
                }

                LinearProgressIndicator(
                    progress = { progressFrac },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = RunicTeal,
                    trackColor = CardSlate
                )

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CardSlate,
                    contentColor = SparkGold,
                    modifier = Modifier.clip(RoundedCornerShape(14.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.testTag("journal_tab_summary"),
                        text = { Text("Resumen y Nube", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.testTag("journal_tab_achievements"),
                        text = { Text("Hitos ($unlockedAchievementsCount/$totalAchievementsCount)", fontWeight = FontWeight.Bold) }
                    )
                }

                if (selectedTab == 1) {
                    AchievementsSection(
                        state = state,
                        achievementManager = achievementManager,
                        selectedCategory = selectedCategory,
                        onSelectCategory = { selectedCategory = it }
                    )
                } else {
                    // Stats & Milestones Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        JournalStatPill("Regiones", "${state.discoveredRegions.size}/${MokayRegionsCatalog.regions.size}", RunicTeal, Modifier.weight(1f))
                        JournalStatPill("Lugares", "${state.discoveredLandmarkIds.size}/$allLandmarksCount", SparkGold, Modifier.weight(1f))
                        JournalStatPill("Aldeas", "${state.visitedVillages.size}/$allVillagesCount", StaminaEmerald, Modifier.weight(1f))
                        JournalStatPill("Misiones", "${state.completedQuests.size}/${MokayWorldCatalog.quests.size}", BoxyLavender, Modifier.weight(1f))
                        JournalStatPill("Hitos", "$unlockedAchievementsCount/$totalAchievementsCount", FlameOrange, Modifier.weight(1f))
                    }

                // Cloud Sync Status & Save Actions
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CardSlate,
                    border = BorderStroke(1.2.dp, RunicTeal.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            when (syncState) {
                                is CloudSyncUiState.Syncing -> {
                                    Icon(Icons.Filled.CloudSync, contentDescription = null, tint = SparkGold)
                                    Text("Sincronizando con Firebase Firestore...", color = SparkGold, style = MaterialTheme.typography.bodyMedium)
                                }
                                is CloudSyncUiState.Synced -> {
                                    Icon(Icons.Filled.CloudDone, contentDescription = null, tint = StaminaEmerald)
                                    Text("Guardado en la nube (${syncState.savedAtLabel}) • ${state.chispas} Chispas", color = WarmParchment, style = MaterialTheme.typography.bodyMedium)
                                }
                                is CloudSyncUiState.Error -> {
                                    Icon(Icons.Filled.CloudOff, contentDescription = null, tint = VitalCrimson)
                                    Text("Error de red: ${syncState.message}", color = VitalCrimson, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp))
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onManualSave,
                                colors = ButtonDefaults.buttonColors(containerColor = RunicTeal, contentColor = Color(0xFF0E131F)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("manual_save_button")
                            ) {
                                Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.size(6.dp))
                                Text("Guardar Ahora", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    if (confirmReset) {
                                        confirmReset = false
                                        onResetAdventure()
                                        onDismiss()
                                    } else {
                                        confirmReset = true
                                    }
                                },
                                border = BorderStroke(1.2.dp, if (confirmReset) VitalCrimson else BoxyLavender.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("reset_save_button")
                            ) {
                                Icon(Icons.Filled.DeleteForever, contentDescription = null, tint = VitalCrimson, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.size(6.dp))
                                Text(
                                    text = if (confirmReset) "Confirmar Reinicio" else "Reiniciar Partida",
                                    color = if (confirmReset) VitalCrimson else WarmParchment,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Controls Legend + Sign Out
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CardSlate.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Controles: Joystick mueve • Atacar / Esquivar • Foskay ilumina y enciende antorchas • Hablar abre aldeas, misiones y tiendas.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                            color = WarmParchment.copy(alpha = 0.8f),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                        OutlinedButton(
                            onClick = onSignOut,
                            border = BorderStroke(1.2.dp, FlameOrange.copy(alpha = 0.7f)),
                            modifier = Modifier.height(48.dp).testTag("journal_sign_out_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = FlameOrange, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(6.dp))
                            Text("Salir", color = FlameOrange)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementsSection(
    state: GameWorldState,
    achievementManager: AchievementManager,
    selectedCategory: AchievementCategory?,
    onSelectCategory: (AchievementCategory?) -> Unit
) {
    val items = remember(state, selectedCategory) {
        achievementManager.getAchievementsProgress(state, selectedCategory)
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onSelectCategory(null) },
                label = { Text("Todos") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = FlameOrange,
                    selectedLabelColor = InkBlack
                )
            )
            AchievementCategory.entries.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { onSelectCategory(cat) },
                    label = { Text(cat.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FlameOrange,
                        selectedLabelColor = InkBlack
                    )
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { entry ->
                val spec = entry.spec
                val accent = Color(spec.accentHex)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CardSlate,
                    border = BorderStroke(
                        1.2.dp,
                        if (entry.isUnlocked) accent else BoxyLavender.copy(alpha = 0.28f)
                    ),
                    modifier = Modifier.testTag("achievement_card_${spec.id}")
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (entry.isUnlocked) Icons.Filled.CheckCircle else Icons.Filled.EmojiEvents,
                                    contentDescription = if (entry.isUnlocked) "Hito desbloqueado" else "Hito en progreso",
                                    tint = if (entry.isUnlocked) StaminaEmerald else accent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = spec.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (entry.isUnlocked) SparkGold else WarmParchment,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = spec.description,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                        color = WarmParchment.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (entry.isUnlocked) StaminaEmerald.copy(alpha = 0.2f) else SparkGold.copy(alpha = 0.16f)
                            ) {
                                Text(
                                    text = if (entry.isUnlocked) "✓ +${spec.rewardChispas} ✨" else "+${spec.rewardChispas} ✨",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (entry.isUnlocked) StaminaEmerald else SparkGold,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LinearProgressIndicator(
                                progress = { entry.progressFraction },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (entry.isUnlocked) StaminaEmerald else accent,
                                trackColor = DeepTwilight
                            )
                            Text(
                                text = "${entry.currentProgress}/${entry.targetProgress}",
                                style = MaterialTheme.typography.labelSmall,
                                color = BoxyLavender,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JournalStatPill(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CardSlate,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.55f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium, color = accent, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = WarmParchment)
        }
    }
}

@Composable
fun MokayaVictoryDialog(
    state: GameWorldState,
    onContinueExploring: () -> Unit
) {
    Dialog(onDismissRequest = onContinueExploring) {
        Card(
            modifier = Modifier.widthIn(max = 540.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DeepTwilight),
            border = BorderStroke(2.5.dp, SparkGold)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = SparkGold, modifier = Modifier.size(40.dp))
                Text(
                    text = "¡Los 7 Colores de Mokaya Resplandecen!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = SparkGold,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Boxy y Foskay restauraron la armonía del Árbol Original con Creatividad, Valentía, Curiosidad y Amistad. Completaste ${state.completedQuests.size} de ${MokayWorldCatalog.quests.size} misiones.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WarmParchment,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onContinueExploring,
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Seguir Explorando Mokaya", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
