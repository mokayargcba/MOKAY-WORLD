package com.example.game.engine

import com.example.game.data.MokayWorldCatalog
import com.example.game.model.CraftingRecipe
import com.example.game.model.ItemCategory
import com.example.game.model.ItemSpec
import kotlin.math.min

data class CraftingRecipeStatus(
    val recipe: CraftingRecipe,
    val resultItem: ItemSpec?,
    val canCraft: Boolean,
    val craftableBatches: Int,
    val missingIngredients: Map<String, Int>,
    val regionTierRecommended: Boolean
)

sealed interface CraftExecutionResult {
    data class Success(
        val nextState: GameWorldState,
        val craftedItemId: String,
        val craftedQuantity: Int
    ) : CraftExecutionResult

    data class Failure(
        val reason: String,
        val state: GameWorldState
    ) : CraftExecutionResult
}

/**
 * Gestor dedicado de crafteo para Boxy y Foskay.
 * Evalúa materiales recolectados, calcula lotes disponibles,
 * valida ingredientes y aplica resultados listos para persistirse en Firestore.
 */
class CraftingManager(
    private val recipes: List<CraftingRecipe> = MokayWorldCatalog.recipes,
    private val items: Map<String, ItemSpec> = MokayWorldCatalog.items
) {

    fun getCollectedMaterials(inventory: Map<String, Int>): List<Pair<ItemSpec, Int>> =
        items.values
            .filter { it.category == ItemCategory.MATERIAL }
            .sortedWith(compareBy<ItemSpec> { it.tier }.thenBy { it.name })
            .map { spec -> spec to (inventory[spec.id] ?: 0) }

    fun maxCraftableBatches(inventory: Map<String, Int>, recipe: CraftingRecipe): Int {
        if (recipe.ingredients.isEmpty()) return 0
        var maxBatches = Int.MAX_VALUE
        recipe.ingredients.forEach { (ingId, req) ->
            if (req <= 0) return@forEach
            val owned = inventory[ingId] ?: 0
            maxBatches = min(maxBatches, owned / req)
        }
        return if (maxBatches == Int.MAX_VALUE) 0 else maxBatches.coerceAtLeast(0)
    }

    fun canCraft(inventory: Map<String, Int>, recipe: CraftingRecipe, batches: Int = 1): Boolean {
        if (batches <= 0) return false
        return recipe.ingredients.all { (ingId, req) ->
            (inventory[ingId] ?: 0) >= req * batches
        }
    }

    fun getMissingIngredients(inventory: Map<String, Int>, recipe: CraftingRecipe, batches: Int = 1): Map<String, Int> =
        buildMap {
            recipe.ingredients.forEach { (ingId, req) ->
                val needed = req * batches
                val owned = inventory[ingId] ?: 0
                if (owned < needed) {
                    put(ingId, needed - owned)
                }
            }
        }

    fun evaluateRecipes(
        inventory: Map<String, Int>,
        currentRegionTier: Int = 3,
        tierFilter: Int? = null,
        onlyCraftable: Boolean = false
    ): List<CraftingRecipeStatus> {
        return recipes
            .filter { tierFilter == null || it.requiredTier == tierFilter }
            .map { recipe ->
                val batches = maxCraftableBatches(inventory, recipe)
                val missing = getMissingIngredients(inventory, recipe, 1)
                CraftingRecipeStatus(
                    recipe = recipe,
                    resultItem = items[recipe.resultItemId],
                    canCraft = batches >= 1,
                    craftableBatches = batches,
                    missingIngredients = missing,
                    regionTierRecommended = recipe.requiredTier <= currentRegionTier
                )
            }
            .filter { !onlyCraftable || it.canCraft }
            .sortedWith(
                compareByDescending<CraftingRecipeStatus> { it.canCraft }
                    .thenBy { it.recipe.requiredTier }
                    .thenBy { it.resultItem?.name ?: it.recipe.id }
            )
    }

    fun craft(state: GameWorldState, recipeId: String, batches: Int = 1): CraftExecutionResult {
        val recipe = recipes.firstOrNull { it.id == recipeId }
            ?: return CraftExecutionResult.Failure("Receta no encontrada.", state)
        if (batches <= 0) {
            return CraftExecutionResult.Failure("Cantidad inválida.", state)
        }
        if (!canCraft(state.inventory, recipe, batches)) {
            val missingText = getMissingIngredients(state.inventory, recipe, batches)
                .entries
                .joinToString(", ") { (id, qty) ->
                    val name = items[id]?.name ?: id
                    "$qty $name"
                }
            val msg = "Faltan materiales: $missingText"
            return CraftExecutionResult.Failure(
                reason = msg,
                state = state.copy(toastMessage = msg)
            )
        }

        val nextInv = state.inventory.toMutableMap()
        recipe.ingredients.forEach { (ingId, req) ->
            val rem = (nextInv[ingId] ?: 0) - (req * batches)
            if (rem <= 0) nextInv.remove(ingId) else nextInv[ingId] = rem
        }

        val totalProduced = recipe.resultQuantity * batches
        nextInv[recipe.resultItemId] = (nextInv[recipe.resultItemId] ?: 0) + totalProduced

        val itemName = items[recipe.resultItemId]?.name ?: recipe.resultItemId
        val nextState = state.copy(
            inventory = nextInv,
            floatingTexts = state.floatingTexts + FloatingBanner(
                text = "¡Crafteado x$totalProduced $itemName!",
                x = state.playerX,
                y = state.playerY - 40f,
                life = 2.0f,
                colorHex = items[recipe.resultItemId]?.colorHex ?: 0xFFFFC24DL
            ),
            toastMessage = "¡Crafteado x$totalProduced $itemName y guardado en Firestore!",
            hapticTick = state.hapticTick + 1
        )
        return CraftExecutionResult.Success(
            nextState = nextState,
            craftedItemId = recipe.resultItemId,
            craftedQuantity = totalProduced
        )
    }
}
