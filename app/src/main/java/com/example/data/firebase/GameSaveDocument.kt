package com.example.data.firebase

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class GameSaveDocument(
    @DocumentId val id: String = "",
    val userId: String = "",
    val currentRegionId: String = "plaza_infinita",
    val playerX: Double = 600.0,
    val playerY: Double = 560.0,
    val hp: Long = 100L,
    val stamina: Long = 100L,
    val mana: Long = 80L,
    val inventory: Map<String, Long> = emptyMap(),
    val quickSlots: List<String> = listOf("slice_pizza", "cafe_bw", "elixir_chispa"),
    val collectedBookIds: List<String> = emptyList(),
    val discoveredKeywords: List<String> = emptyList(),
    val discoveredRegions: List<String> = listOf("plaza_infinita"),
    val completedQuests: List<String> = emptyList(),
    val solvedPuzzleIds: List<String> = emptyList(),
    val defeatedBossStages: List<String> = emptyList(),
    val unlockedGateIds: List<String> = emptyList(),
    val collectedForces: List<String> = emptyList(),
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "currentRegionId" to currentRegionId.take(64).ifBlank { "plaza_infinita" },
        "playerX" to playerX.coerceIn(-10000.0, 10000.0),
        "playerY" to playerY.coerceIn(-10000.0, 10000.0),
        "hp" to hp.coerceIn(0L, 500L),
        "stamina" to stamina.coerceIn(0L, 500L),
        "mana" to mana.coerceIn(0L, 500L),
        "inventory" to inventory.entries.take(100).associate { it.key to it.value.coerceAtLeast(0L) },
        "quickSlots" to quickSlots.take(5),
        "collectedBookIds" to collectedBookIds.take(30),
        "discoveredKeywords" to discoveredKeywords.take(30),
        "discoveredRegions" to discoveredRegions.take(20),
        "completedQuests" to completedQuests.take(50),
        "solvedPuzzleIds" to solvedPuzzleIds.take(50),
        "defeatedBossStages" to defeatedBossStages.take(20),
        "unlockedGateIds" to unlockedGateIds.take(30),
        "collectedForces" to collectedForces.take(10),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )

    fun toUpdateMap(): Map<String, Any> = mapOf(
        "currentRegionId" to currentRegionId.take(64).ifBlank { "plaza_infinita" },
        "playerX" to playerX.coerceIn(-10000.0, 10000.0),
        "playerY" to playerY.coerceIn(-10000.0, 10000.0),
        "hp" to hp.coerceIn(0L, 500L),
        "stamina" to stamina.coerceIn(0L, 500L),
        "mana" to mana.coerceIn(0L, 500L),
        "inventory" to inventory.entries.take(100).associate { it.key to it.value.coerceAtLeast(0L) },
        "quickSlots" to quickSlots.take(5),
        "collectedBookIds" to collectedBookIds.take(30),
        "discoveredKeywords" to discoveredKeywords.take(30),
        "discoveredRegions" to discoveredRegions.take(20),
        "completedQuests" to completedQuests.take(50),
        "solvedPuzzleIds" to solvedPuzzleIds.take(50),
        "defeatedBossStages" to defeatedBossStages.take(20),
        "unlockedGateIds" to unlockedGateIds.take(30),
        "collectedForces" to collectedForces.take(10),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}
