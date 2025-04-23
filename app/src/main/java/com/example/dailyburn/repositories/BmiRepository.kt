package com.example.dailyburn.repositories

import com.example.dailyburn.models.BmiRecord
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID

class BmiRepository {
    private val db = FirebaseFirestore.getInstance()
    private val bmiCollection = db.collection("bmi_records")

    suspend fun saveBmiRecord(bmiRecord: BmiRecord): Boolean {
        return try {
            val id = bmiRecord.id.ifEmpty { UUID.randomUUID().toString() }
            val record = bmiRecord.copy(id = id)
            bmiCollection.document(id).set(record).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getBmiHistory(userId: String, limit: Int = 10): List<BmiRecord> {
        return try {
            val snapshot =bmiCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            val result = snapshot.toObjects(BmiRecord::class.java)
            result
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getLatestBmi(userId: String): BmiRecord? {
        return try {
            bmiCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()
                .toObjects(BmiRecord::class.java)
                .firstOrNull()
        } catch (e: Exception) {
            null
        }
    }
}