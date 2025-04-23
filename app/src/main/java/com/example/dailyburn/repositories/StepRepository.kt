package com.example.dailyburn.repositories


import com.example.dailyburn.models.StepData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StepRepository {
    private val db = FirebaseFirestore.getInstance()
    private val stepsCollection = db.collection("steps")

    suspend fun saveSteps(userId: String, steps: Int) {
        val today = getTodayFormatted()
        val docId = "$userId-$today"

        // Get the existing data for today if it exists
        val existingData = getTodaySteps(userId)

        // Update or create new step data
        val stepData = existingData?.copy(steps = steps) ?: StepData(
            id = docId,
            userId = userId,
            date = Calendar.getInstance().timeInMillis,
            steps = steps,
            calories = calculateCalories(steps),
            distance = calculateDistance(steps)
        )

        stepsCollection.document(docId).set(stepData).await()
    }

    suspend fun getTodaySteps(userId: String): StepData? {
        val today = getTodayFormatted()
        val docId = "$userId-$today"

        return try {
            val document = stepsCollection.document(docId).get().await()
            document.toObject(StepData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getWeeklySteps(userId: String): List<StepData> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7) // Go back 7 days
        val weekAgo = calendar.timeInMillis

        return try {
            val documents = stepsCollection
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("date", weekAgo)
                .get()
                .await()

            documents.toObjects(StepData::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateGoal(userId: String, goal: Int) {
        val today = getTodayFormatted()
        val docId = "$userId-$today"

        val existingData = getTodaySteps(userId)

        if (existingData != null) {
            stepsCollection.document(docId)
                .update("goal", goal)
                .await()
        } else {
            // Create a new step data with the goal if none exists
            val newStepData = StepData(
                id = docId,
                userId = userId,
                date = Calendar.getInstance().timeInMillis,
                steps = 0,
                goal = goal
            )
            stepsCollection.document(docId).set(newStepData).await()
        }
    }

    private fun getTodayFormatted(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun calculateCalories(steps: Int, weight: Float = 70f): Int {
        // Average calories burned per step (varies by weight)
        val caloriesPerStep = 0.04 * (weight / 70f)
        return (steps * caloriesPerStep).toInt()
    }

    private fun calculateDistance(steps: Int): Float {
        // Average stride length is about 0.762 meters (30 inches)
        val averageStrideInKm = 0.000762f
        return steps * averageStrideInKm
    }
}