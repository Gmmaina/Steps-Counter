package com.example.dailyburn.models

import java.util.Date

data class BmiRecord(
    val id: String = "",
    val userId: String = "",
    val timestamp: Long = Date().time,
    val height: Float = 0f,  // in cm
    val weight: Float = 0f,  // in kg
    val bmi: Float = 0f,
    val category: String = ""
) {
    // Empty constructor for Firebase
    constructor() : this("", "", Date().time, 0f, 0f, 0f, "")
}