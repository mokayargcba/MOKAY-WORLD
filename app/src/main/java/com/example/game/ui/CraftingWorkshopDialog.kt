package com.example.game.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.data.MokayWorldCatalog
import com.example.game.engine.CraftingManager
import com.example.game.engine.CraftingRecipeStatus
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
fun CraftingWorkshopDialog(
    state: GameWorldState,
    syncState: CloudSyncUiState,
    craftingManager: CraftingManager,
    onCraftRecipe: (String, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var tierFilter by remember { mutableStateOf<Int?>(null) }
    var onlyCraftable by remember { mutableStateOf(false) }

    val collectedMaterials = remember(state.inventory) {
        craftingManager.getCollectedMaterials(state.inventory)
    }
    val recipeStatuses = remember(state.inventory, state.currentRegionId, tierFilter, onlyCraftable) {
        craftingManager.evaluateRecipes(
            inventory = state.inventory,
            currentRegionTier = state.currentRegionId.tier,
            tierFilter = tierFilter,
            onlyCraftable = onlyCraftable
        )
    }
    var selectedRecipeId by remember { mutableStateOf(recipeStatuses.firstOrNull()?.recipe?.id ?: "recipe_cafe") }
    val selectedStatus = recipeStatuses.firstOrNull { it.recipe.id == selectedRecipeId }
        ?: recipeStatuses.firstOrNull()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 820.dp)
                .heightIn(max = 420.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DeepTwilight),
            border = BorderStroke(2.dp, FlameOrange.copy(alpha = 0.72f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header with Cloud Status & Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = FlameOrange)
                        Column {
                            Text(
                                text = "Taller de Alquimia de Boxy y Foskay",
                                style = MaterialTheme.typography.titleLarge,
                                color = WarmParchment
                            )
                            Text(
                                text = "Transforma materiales recolectados en bebidas y elixires con respaldo en la nube.",
                                style = MaterialTheme.typography.labelSmall,
                                color = BoxyLavender
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CardSlate,
                            border = BorderStroke(1.dp, RunicTeal.copy(alpha = 0.45f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                val syncing = syncState is CloudSyncUiState.Syncing
                                Icon(
                                    imageVector = if (syncing) Icons.Filled.CloudSync else Icons.Filled.CloudDone,
                                    contentDescription = null,
                                    tint = if (syncing) SparkGold else StaminaEmerald,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = if (syncState is CloudSyncUiState.Synced) "Nube ${syncState.savedAtLabel}" else "Firestore",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarmParchment
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("close_crafting_workshop")
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Cerrar taller", tint = WarmParchment)
                        }
                    }
                }

                // Collected Materials Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    collectedMaterials.forEach { (mat, qty) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CardSlate,
                            border = BorderStroke(
                                1.dp,
                                if (qty > 0) Color(mat.colorHex).copy(alpha = 0.7f) else BoxyLavender.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ItemVectorBadge(itemId = mat.id, accentColor = Color(mat.colorHex), size = 22.dp)
                                Column {
                                    Text(
                                        text = mat.name,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = if (qty > 0) WarmParchment else WarmParchment.copy(alpha = 0.55f)
                                    )
                                    Text(
                                        text = "x$qty",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = if (qty > 0) SparkGold else Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Filters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = tierFilter == null && !onlyCraftable,
                        onClick = { tierFilter = null; onlyCraftable = false },
                        label = { Text("Todas (${MokayWorldCatalog.recipes.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FlameOrange,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = onlyCraftable,
                        onClick = { onlyCraftable = !onlyCraftable },
                        label = { Text("Listas para Craftear") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StaminaEmerald,
                            selectedLabelColor = InkBlack
                        )
                    )
                    (1..3).forEach { tier ->
                        FilterChip(
                            selected = tierFilter == tier,
                            onClick = { tierFilter = if (tierFilter == tier) null else tier },
                            label = { Text("Nivel $tier") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AntiqueGold,
                                selectedLabelColor = InkBlack
                            )
                        )
                    }
                }

                // Master-Detail Crafting Workbench
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Left Column: Recipe List
                    if (recipeStatuses.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay recetas en este filtro. Recolecta materiales en las regiones.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BoxyLavender
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1.05f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 4.dp)
                        ) {
                            items(recipeStatuses, key = { it.recipe.id }) { status ->
                                val isSelected = selectedStatus?.recipe?.id == status.recipe.id
                                RecipeListRow(
                                    status = status,
                                    selected = isSelected,
                                    onClick = { selectedRecipeId = status.recipe.id }
                                )
                            }
                        }
                    }

                    // Right Column: Selected Recipe Workbench
                    if (selectedStatus != null) {
                        RecipeWorkbenchPane(
                            status = selectedStatus,
                            inventory = state.inventory,
                            onCraft = { batches -> onCraftRecipe(selectedStatus.recipe.id, batches) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeListRow(
    status: CraftingRecipeStatus,
    selected: Boolean,
    onClick: () -> Unit
) {
    val resultItem = status.resultItem
    val accent = Color(resultItem?.colorHex ?: 0xFFFFC24D)
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (selected) Color(0xFF282D44) else CardSlate,
        border = BorderStroke(
            width = if (selected) 1.8.dp else 1.dp,
            color = when {
                selected -> FlameOrange
                status.canCraft -> RunicTeal.copy(alpha = 0.65f)
                else -> BoxyLavender.copy(alpha = 0.25f)
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("workshop_recipe_${status.recipe.id}")
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ItemVectorBadge(
                itemId = status.recipe.resultItemId,
                accentColor = accent,
                size = 34.dp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${resultItem?.name ?: status.recipe.resultItemId} x${status.recipe.resultQuantity}",
                    style = MaterialTheme.typography.titleSmall,
                    color = WarmParchment,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Nivel ${status.recipe.requiredTier}" +
                        if (status.canCraft) " • Puedes crear x${status.craftableBatches}" else " • Faltan materiales",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (status.canCraft) StaminaEmerald else BoxyLavender
                )
            }
            if (status.canCraft) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Disponible",
                    tint = StaminaEmerald,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun RecipeWorkbenchPane(
    status: CraftingRecipeStatus,
    inventory: Map<String, Int>,
    onCraft: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val resultItem = status.resultItem
    val accent = Color(resultItem?.colorHex ?: 0xFFFFC24D)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardSlate,
        border = BorderStroke(1.5.dp, AntiqueGold.copy(alpha = 0.45f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ItemVectorBadge(itemId = status.recipe.resultItemId, accentColor = accent, size = 40.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${resultItem?.name ?: status.recipe.resultItemId} (x${status.recipe.resultQuantity})",
                        style = MaterialTheme.typography.titleMedium,
                        color = SparkGold
                    )
                    Text(
                        text = resultItem?.description ?: status.recipe.hint,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                        color = WarmParchment.copy(alpha = 0.85f)
                    )
                }
            }

            Text(
                text = "Ingredientes necesarios:",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
                color = BoxyLavender
            )

            status.recipe.ingredients.forEach { (ingId, req) ->
                val ingSpec = MokayWorldCatalog.items[ingId]
                val owned = inventory[ingId] ?: 0
                val ok = owned >= req
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = InkBlack.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, if (ok) StaminaEmerald else VitalCrimson.copy(alpha = 0.65f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ItemVectorBadge(
                                itemId = ingId,
                                accentColor = Color(ingSpec?.colorHex ?: 0xFFB2B2B2),
                                size = 20.dp
                            )
                            Text(
                                text = ingSpec?.name ?: ingId,
                                style = MaterialTheme.typography.labelMedium,
                                color = WarmParchment
                            )
                        }
                        Text(
                            text = "$owned / $req",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (ok) StaminaEmerald else VitalCrimson
                        )
                    }
                }
            }

            Spacer(Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onCraft(1) },
                    enabled = status.canCraft,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FlameOrange,
                        disabledContainerColor = Color(0xFF39364C)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("workshop_craft_one_button")
                ) {
                    Icon(Icons.Filled.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Craftear x1", fontWeight = FontWeight.Bold)
                }

                if (status.craftableBatches > 1) {
                    OutlinedButton(
                        onClick = { onCraft(status.craftableBatches) },
                        border = BorderStroke(1.2.dp, RunicTeal),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("workshop_craft_max_button")
                    ) {
                        Text(
                            text = "Todo (x${status.craftableBatches})",
                            color = RunicTeal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
