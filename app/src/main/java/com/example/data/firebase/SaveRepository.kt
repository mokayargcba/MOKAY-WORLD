package com.example.data.firebase

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class SaveRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(expectedUserId: String): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("Debes iniciar sesión con Google para guardar en la nube.")
        check(uid == expectedUserId) { "La sesión activa cambió; escritura cancelada." }
        return uid
    }

    fun observeSave(userId: String): Flow<GameSaveDocument?> {
        val docPath = "users/$userId/saves/$MAIN_SAVE_ID"
        return db.collection("users").document(userId)
            .collection("saves").document(MAIN_SAVE_ID)
            .snapshots()
            .map { snapshot ->
                if (!snapshot.exists()) null
                else snapshot.toObject(
                    GameSaveDocument::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val toLog = firestoreError ?: (error as? Exception)
                if (toLog != null && auth.currentUser?.uid == userId) {
                    handleFirestoreError(toLog, OperationType.GET, docPath)
                }
                throw error
            }
    }

    suspend fun saveOrCreateProgress(userId: String, doc: GameSaveDocument): Result<Unit> {
        val docPath = "users/$userId/saves/$MAIN_SAVE_ID"
        return try {
            val uid = requireUserId(userId)
            val docRef = db.collection("users").document(uid)
                .collection("saves").document(MAIN_SAVE_ID)
            val existing = docRef.get().await()
            if (!existing.exists()) {
                docRef.set(doc.toCreateMap(uid)).await()
            } else {
                docRef.update(doc.toUpdateMap()).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun resetSave(userId: String, starterSave: GameSaveDocument): Result<Unit> {
        val docPath = "users/$userId/saves/$MAIN_SAVE_ID"
        return try {
            val uid = requireUserId(userId)
            val docRef = db.collection("users").document(uid)
                .collection("saves").document(MAIN_SAVE_ID)
            docRef.delete().await()
            docRef.set(starterSave.toCreateMap(uid)).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    companion object {
        const val MAIN_SAVE_ID = "main"
    }
}
