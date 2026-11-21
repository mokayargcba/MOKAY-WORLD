package com.example.game.engine

import com.example.game.data.MokayWorldCatalog

enum class AchievementCategory(val label: String) {
    EXPLORATION("Exploración"),
    PUZZLES("Puzzles"),
    COMBAT("Combate"),
    COLLECTION("Colección"),
    SOCIAL("Vínculos")
}

data class AchievementSpec(
    val id: String,
    val title: String,
    val description: String,
    val category: AchievementCategory,
    val targetProgress: Int,
    val rewardChispas: Int,
    val accentHex: Long,
    val progressSelector: (GameWorldState) -> Int
)

data class AchievementProgress(
    val spec: AchievementSpec,
    val currentProgress: Int,
    val targetProgress: Int,
    val isUnlocked: Boolean
) {
    val progressFraction: Float
        get() = if (targetProgress <= 0) 1f else (currentProgress.toFloat() / targetProgress.toFloat()).coerceIn(0f, 1f)
}

data class AchievementEvaluationOutcome(
    val nextState: GameWorldState,
    val newlyUnlocked: List<AchievementSpec>,
    val awardedChispas: Int
)

/**
 * Gestor de hitos y logros de Boxy y Foskay en Mokaya.
 * Evalúa exploración de regiones, aldeas, puzzles, jefes, libros, misiones y afinidad NPC,
 * persistiendo los IDs desbloqueados en Firestore para evitar recompensas duplicadas.
 */
class AchievementManager(
    val catalog: List<AchievementSpec> = DEFAULT_ACHIEVEMENTS
) {

    fun evaluateAndUnlock(state: GameWorldState): AchievementEvaluationOutcome {
        val newlyUnlocked = catalog.filter { spec ->
            spec.id !in state.unlockedAchievementIds &&
                spec.progressSelector(state) >= spec.targetProgress
        }

        if (newlyUnlocked.isEmpty()) {
            return AchievementEvaluationOutcome(
                nextState = state,
                newlyUnlocked = emptyList(),
                awardedChispas = 0
            )
        }

        val totalChispas = newlyUnlocked.sumOf { it.rewardChispas }
        val nextIds = state.unlockedAchievementIds + newlyUnlocked.map { it.id }
        val first = newlyUnlocked.first()
        val bannerText = if (newlyUnlocked.size == 1) {
            "🏆 Hito: ${first.title} (+$totalChispas Chispas)"
        } else {
            "🏆 ${newlyUnlocked.size} Hitos desbloqueados (+$totalChispas Chispas)"
        }

        val nextState = state.copy(
            unlockedAchievementIds = nextIds,
            chispas = state.chispas + totalChispas,
            toastMessage = bannerText,
            floatingTexts = state.floatingTexts + FloatingBanner(
                text = "🏆 ${first.title}!",
                x = state.playerX,
                y = state.playerY - 52f,
                life = 2.4f,
                colorHex = first.accentHex
            ),
            hapticTick = state.hapticTick + 1
        )

        return AchievementEvaluationOutcome(
            nextState = nextState,
            newlyUnlocked = newlyUnlocked,
            awardedChispas = totalChispas
        )
    }

    fun getAchievementsProgress(
        state: GameWorldState,
        category: AchievementCategory? = null
    ): List<AchievementProgress> {
        return catalog
            .filter { category == null || it.category == category }
            .map { spec ->
                val rawProgress = spec.progressSelector(state).coerceAtLeast(0)
                val unlocked = spec.id in state.unlockedAchievementIds || rawProgress >= spec.targetProgress
                AchievementProgress(
                    spec = spec,
                    currentProgress = rawProgress.coerceAtMost(spec.targetProgress),
                    targetProgress = spec.targetProgress,
                    isUnlocked = unlocked
                )
            }
            .sortedWith(
                compareByDescending<AchievementProgress> { it.isUnlocked }
                    .thenByDescending { it.progressFraction }
                    .thenBy { it.spec.category.ordinal }
            )
    }

    fun summaryCounts(state: GameWorldState): Pair<Int, Int> {
        val unlockedCount = catalog.count { it.id in state.unlockedAchievementIds }
        return unlockedCount to catalog.size
    }

    fun totalChispasEarned(state: GameWorldState): Int =
        catalog.filter { it.id in state.unlockedAchievementIds }.sumOf { it.rewardChispas }

    companion object {
        val DEFAULT_ACHIEVEMENTS: List<AchievementSpec> = listOf(
            AchievementSpec(
                id = "ach_explorer_3",
                title = "Primeros Horizontes",
                description = "Explora 3 regiones distintas de Mokaya.",
                category = AchievementCategory.EXPLORATION,
                targetProgress = 3,
                rewardChispas = 25,
                accentHex = 0xFF2EC4B6L,
                progressSelector = { it.discoveredRegions.size }
            ),
            AchievementSpec(
                id = "ach_explorer_all",
                title = "Cartógrafo de Mokaya",
                description = "Descubre las 11 regiones del archipiélago.",
                category = AchievementCategory.EXPLORATION,
                targetProgress = 11,
                rewardChispas = 120,
                accentHex = 0xFFFFC24DL,
                progressSelector = { it.discoveredRegions.size }
            ),
            AchievementSpec(
                id = "ach_landmarks_10",
                title = "Ojo de Halcón",
                description = "Descubre 10 lugares emblemáticos en el mapa.",
                category = AchievementCategory.EXPLORATION,
                targetProgress = 10,
                rewardChispas = 40,
                accentHex = 0xFF4CC37AL,
                progressSelector = { it.discoveredLandmarkIds.size }
            ),
            AchievementSpec(
                id = "ach_villages_6",
                title = "Viajero de Aldeas",
                description = "Visita 6 aldeas o barrios distintos.",
                category = AchievementCategory.EXPLORATION,
                targetProgress = 6,
                rewardChispas = 40,
                accentHex = 0xFFB7B0E6L,
                progressSelector = { it.visitedVillages.size }
            ),
            AchievementSpec(
                id = "ach_puzzle_first",
                title = "Mente Iluminada",
                description = "Completa tu primer puzzle ancestral.",
                category = AchievementCategory.PUZZLES,
                targetProgress = 1,
                rewardChispas = 30,
                accentHex = 0xFF6C63FFL,
                progressSelector = { it.solvedPuzzleIds.size }
            ),
            AchievementSpec(
                id = "ach_puzzle_all",
                title = "Maestro de los Acertijos",
                description = "Resuelve los 3 puzzles regionales de Mokaya.",
                category = AchievementCategory.PUZZLES,
                targetProgress = 3,
                rewardChispas = 85,
                accentHex = 0xFFFFC24DL,
                progressSelector = { it.solvedPuzzleIds.size }
            ),
            AchievementSpec(
                id = "ach_boss_first",
                title = "Chispa Valiente",
                description = "Derrota a tu primer jefe regional.",
                category = AchievementCategory.COMBAT,
                targetProgress = 1,
                rewardChispas = 50,
                accentHex = 0xFFFF6A3DL,
                progressSelector = { it.defeatedBossStages.size }
            ),
            AchievementSpec(
                id = "ach_boss_all",
                title = "Campeón de Mokaya",
                description = "Supera los 3 combates contra jefes de la historia.",
                category = AchievementCategory.COMBAT,
                targetProgress = 3,
                rewardChispas = 140,
                accentHex = 0xFFD94E4EL,
                progressSelector = { it.defeatedBossStages.size }
            ),
            AchievementSpec(
                id = "ach_harmony_forces",
                title = "Los 7 Colores",
                description = "Reúne las 4 Fuerzas Ancestrales de Mokaya.",
                category = AchievementCategory.COMBAT,
                targetProgress = 4,
                rewardChispas = 150,
                accentHex = 0xFFFFC24DL,
                progressSelector = { it.collectedForces.size }
            ),
            AchievementSpec(
                id = "ach_books_all",
                title = "Guardián del Grimorio",
                description = "Colecciona los ${MokayWorldCatalog.books.size} libros antiguos.",
                category = AchievementCategory.COLLECTION,
                targetProgress = MokayWorldCatalog.books.size,
                rewardChispas = 80,
                accentHex = 0xFF2EC4B6L,
                progressSelector = { it.collectedBookIds.size }
            ),
            AchievementSpec(
                id = "ach_quests_5",
                title = "Corazón Solidario",
                description = "Completa 5 misiones de los habitantes de Mokaya.",
                category = AchievementCategory.SOCIAL,
                targetProgress = 5,
                rewardChispas = 60,
                accentHex = 0xFF4CC37AL,
                progressSelector = { it.completedQuests.size }
            ),
            AchievementSpec(
                id = "ach_ally_3",
                title = "Alma Diplomática",
                description = "Alcanza el rango Aliado (+${DialogueManager.SHOP_DISCOUNT_THRESHOLD}) con 3 NPCs.",
                category = AchievementCategory.SOCIAL,
                targetProgress = 3,
                rewardChispas = 65,
                accentHex = 0xFFE0A94FL,
                progressSelector = { state ->
                    state.npcRelationships.values.count { it >= DialogueManager.SHOP_DISCOUNT_THRESHOLD }
                }
            )
        )
    }
}
