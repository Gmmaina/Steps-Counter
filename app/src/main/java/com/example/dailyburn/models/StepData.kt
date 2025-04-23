package com.example.dailyburn.models


import java.util.Date

data class StepData(
    val id: String = "",
    val userId: String = "",
    val date: Long = Date().time,
    val steps: Int = 0,
    val goal: Int = 10000,
    val calories: Int = 0,
    val distance: Float = 0f
) {
    // Empty constructor for Firebase
    constructor() : this("", "", Date().time, 0, 10000, 0, 0f)

    // Calculate calories based on steps (rough estimation)
    fun calculateCalories(weight: Float = 70f): Int {
        // Average calories burned per step (varies by weight)
        val caloriesPerStep = 0.04 * (weight / 70f)
        return (steps * caloriesPerStep).toInt()
    }

    // Calculate distance based on steps (rough estimation)
    fun calculateDistance(): Float {
        // Average stride length is about 0.762 meters (30 inches)
        val averageStrideInKm = 0.000762f
        return steps * averageStrideInKm
    }
}