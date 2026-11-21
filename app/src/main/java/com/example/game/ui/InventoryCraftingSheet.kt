package com.example.game.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.data.MokayWorldCatalog
import com.example.game.engine.CraftingManager
import com.example.game.engine.GameWorldState
import com.example.game.model.ItemCategory
import com.example.game.model.ShopUpgradeKind
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
fun InventoryCraftingSheet(
    state: GameWorldState,
    initialTab: Int = 0,
    shopDiscountPercent: Int = 0,
    craftingManager: CraftingManager = CraftingManager(),
    onConsumeItem: (String) -> Unit,
    onDiscardItem: (String) -> Unit,
    onAssignQuickSlot: (Int, String) -> Unit,
    onCraftRecipe: (String) -> Unit,
    onPurchaseOffer: (String) -> Unit,
    onOpenDedicatedWorkshop: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab.coerceIn(0, 2)) }
    var selectedCategory by remember { mutableStateOf<ItemCategory?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 760.dp)
                .heightIn(max = 405.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DeepTwilight),
            border = BorderStroke(2.dp, BoxyLavender.copy(alpha = 0.55f))
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (selectedTab) {
                            0 -> "Mochila de Boxy"
                            1 -> "Recetas de Crafteo"
                            else -> "Tienda y Mejoras de Aldea"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = WarmParchment
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CardSlate,
                            border = BorderStroke(1.2.dp, SparkGold.copy(alpha = 0.7f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = SparkGold, modifier = Modifier.size(15.dp))
                                Text(
                                    text = "${state.chispas} Chispas",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SparkGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(48.dp).testTag("close_inventory_button")
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Cerrar mochila", tint = WarmParchment)
                        }
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CardSlate,
                    contentColor = SparkGold,
                    modifier = Modifier.clip(RoundedCornerShape(14.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.testTag("tab_mochila"),
                        text = { Text("Mochila", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Filled.Inventory2, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.testTag("tab_crafteo"),
                        text = { Text("Crafteo", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Filled.Build, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        modifier = Modifier.testTag("tab_tienda"),
                        text = { Text("Tienda", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Filled.Storefront, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                Spacer(Modifier.height(10.dp))

                when (selectedTab) {
                    0 -> InventoryBagTab(
                        state = state,
                        selectedCategory = selectedCategory,
                        onSelectCategory = { selectedCategory = it },
                        onConsumeItem = onConsumeItem,
                        onDiscardItem = onDiscardItem,
                        onAssignQuickSlot = onAssignQuickSlot
                    )
                    1 -> CraftingListTab(
                        state = state,
                        craftingManager = craftingManager,
                        onCraftRecipe = onCraftRecipe,
                        onOpenDedicatedWorkshop = onOpenDedicatedWorkshop
                    )
                    else -> VillageShopTab(
                        state = state,
                        shopDiscountPercent = shopDiscountPercent,
                        onPurchaseOffer = onPurchaseOffer
                    )
                }
            }
        }
    }
}

@Composable
private fun InventoryBagTab(
    state: GameWorldState,
    selectedCategory: ItemCategory?,
    onSelectCategory: (ItemCategory?) -> Unit,
    onConsumeItem: (String) -> Unit,
    onDiscardItem: (String) -> Unit,
    onAssignQuickSlot: (Int, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onSelectCategory(null) },
            label = { Text("Todos") },
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange, selectedLabelColor = Color.White)
        )
        ItemCategory.entries.forEach { cat ->
            FilterChip(
                selected = selectedCategory == cat,
                onClick = { onSelectCategory(cat) },
                label = { Text(cat.label) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange, selectedLabelColor = Color.White)
            )
        }
    }

    Spacer(Modifier.height(8.dp))

    val ownedItems = state.inventory.entries
        .filter { it.value > 0 }
        .mapNotNull { entry -> MokayWorldCatalog.items[entry.key]?.let { spec -> spec to entry.value } }
        .filter { (spec, _) -> selectedCategory == null || spec.category == selectedCategory }
        .sortedBy { it.first.category.ordinal }

    if (ownedItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aún no tienes objetos en esta categoría. Explora Mokaya para recolectar.",
                style = MaterialTheme.typography.bodyMedium,
                color = BoxyLavender
            )
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(ownedItems, key = { it.first.id }) { (spec, qty) ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CardSlate,
                    border = BorderStroke(1.dp, Color(spec.colorHex).copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                ItemVectorBadge(itemId = spec.id, accentColor = Color(spec.colorHex), size = 34.dp)
                                Column {
                                    Text(spec.name, style = MaterialTheme.typography.titleMedium, color = WarmParchment)
                                    Text("${spec.category.label} • Nivel ${spec.tier}", style = MaterialTheme.typography.labelSmall, color = BoxyLavender)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(shape = RoundedCornerShape(10.dp), color = InkBlack.copy(alpha = 0.55f)) {
                                    Text(
                                        text = "x$qty",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = SparkGold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                if (spec.category != ItemCategory.QUEST) {
                                    IconButton(
                                        onClick = { onDiscardItem(spec.id) },
                                        modifier = Modifier.size(48.dp).testTag("discard_${spec.id}")
                                    ) {
                                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Descartar ${spec.name}", tint = VitalCrimson.copy(alpha = 0.85f))
                                    }
                                }
                            }
                        }

                        Text(
                            text = spec.description,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                            color = WarmParchment.copy(alpha = 0.85f)
                        )

                        if (spec.category == ItemCategory.FOOD_DRINK || spec.category == ItemCategory.POTION) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { onConsumeItem(spec.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                                    modifier = Modifier.height(48.dp).testTag("consume_${spec.id}")
                                ) {
                                    Text("Usar")
                                }
                                (0..2).forEach { slot ->
                                    val active = state.quickSlots.getOrNull(slot) == spec.id
                                    OutlinedButton(
                                        onClick = { onAssignQuickSlot(slot, spec.id) },
                                        border = BorderStroke(1.2.dp, if (active) RunicTeal else BoxyLavender.copy(alpha = 0.45f)),
                                        contentPadding = PaddingValues(horizontal = 8.dp),
                                        modifier = Modifier.height(48.dp)
                                    ) {
                                        Text(
                                            text = if (active) "✓ Slot ${slot + 1}" else "Slot ${slot + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (active) RunicTeal else WarmParchment
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
}

@Composable
private fun CraftingListTab(
    state: GameWorldState,
    craftingManager: CraftingManager,
    onCraftRecipe: (String) -> Unit,
    onOpenDedicatedWorkshop: () -> Unit
) {
    val statuses = remember(state.inventory, state.currentRegionId) {
        craftingManager.evaluateRecipes(state.inventory, state.currentRegionId.tier)
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
            onClick = onOpenDedicatedWorkshop,
            border = BorderStroke(1.2.dp, FlameOrange),
            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("open_dedicated_workshop_from_tab")
        ) {
            Text("Abrir Mesa de Alquimia Completa (Lotes y Filtros)", color = SparkGold, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(statuses, key = { it.recipe.id }) { status ->
                val recipe = status.recipe
                val resultItem = status.resultItem
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CardSlate,
                    border = BorderStroke(
                        1.2.dp,
                        if (status.canCraft) RunicTeal.copy(alpha = 0.7f) else BoxyLavender.copy(alpha = 0.25f)
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                ItemVectorBadge(
                                    itemId = recipe.resultItemId,
                                    accentColor = Color(resultItem?.colorHex ?: 0xFFFFC24D),
                                    size = 32.dp
                                )
                                Column {
                                    Text(
                                        text = "${resultItem?.name ?: recipe.resultItemId} x${recipe.resultQuantity}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = WarmParchment
                                    )
                                    Text(
                                        text = "Nivel de Desafío ${recipe.requiredTier}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AntiqueGold
                                    )
                                }
                            }
                            Button(
                                onClick = { onCraftRecipe(recipe.id) },
                                enabled = status.canCraft,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FlameOrange,
                                    disabledContainerColor = Color(0xFF39364C)
                                ),
                                modifier = Modifier.height(48.dp).testTag("craft_${recipe.id}")
                            ) {
                                Text("Craftear")
                            }
                        }

                        Text(
                            text = recipe.hint,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = WarmParchment.copy(alpha = 0.8f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            recipe.ingredients.forEach { (ingId, req) ->
                                val owned = state.inventory[ingId] ?: 0
                                val ingName = MokayWorldCatalog.items[ingId]?.name ?: ingId
                                val ok = owned >= req
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = InkBlack.copy(alpha = 0.45f),
                                    border = BorderStroke(1.dp, if (ok) StaminaEmerald else VitalCrimson.copy(alpha = 0.7f))
                                ) {
                                    Text(
                                        text = "$ingName: $owned/$req",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (ok) StaminaEmerald else VitalCrimson,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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

@Composable
private fun VillageShopTab(
    state: GameWorldState,
    shopDiscountPercent: Int,
    onPurchaseOffer: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (shopDiscountPercent > 0) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = RunicTeal.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, RunicTeal)
            ) {
                Text(
                    text = "Descuento de Amistad activo: -$shopDiscountPercent% en todas las compras",
                    style = MaterialTheme.typography.labelMedium,
                    color = RunicTeal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(MokayWorldCatalog.shopOffers, key = { it.id }) { offer ->
                val maxReached = when (offer.upgradeKind) {
                    ShopUpgradeKind.MAX_HP -> state.maxHp >= 220
                    ShopUpgradeKind.MAX_STAMINA -> state.maxStamina >= 200f
                    ShopUpgradeKind.MAX_MANA -> state.maxMana >= 200f
                    null -> false
                }
                val effectivePrice = (offer.priceChispas * (100 - shopDiscountPercent.coerceIn(0, 50)) / 100)
                    .coerceAtLeast(1)
                val canAfford = state.chispas >= effectivePrice && !maxReached
                val accent = offer.itemId?.let { MokayWorldCatalog.items[it]?.colorHex }?.let { Color(it) } ?: SparkGold
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CardSlate,
                    border = BorderStroke(1.2.dp, if (canAfford) SparkGold.copy(alpha = 0.65f) else BoxyLavender.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ItemVectorBadge(
                                itemId = offer.itemId ?: "corona_superacion",
                                accentColor = accent,
                                size = 34.dp
                            )
                            Column {
                                Text(offer.title, style = MaterialTheme.typography.titleMedium, color = WarmParchment)
                                Text(
                                    text = offer.description,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                    color = WarmParchment.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Button(
                            onClick = { onPurchaseOffer(offer.id) },
                            enabled = canAfford,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RunicTeal,
                                contentColor = InkBlack,
                                disabledContainerColor = Color(0xFF39364C)
                            ),
                            modifier = Modifier.height(48.dp).testTag("buy_${offer.id}")
                        ) {
                            Text(
                                text = if (maxReached) "Máximo" else "$effectivePrice ✨",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
