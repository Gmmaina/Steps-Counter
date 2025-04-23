package com.example.dailyburn.utils

object BmiCalculator {
    fun calculateBmi(weightKg: Float, heightCm: Float): Float {
        // Convert height from cm to meters
        val heightM = heightCm / 100
        // BMI formula: weight (kg) / (height (m) * height (m))
        return weightKg / (heightM * heightM)
    }

    fun getBmiCategory(bmi: Float): String {
        return when {
            bmi < 16.0 -> "Severe Thinness"
            bmi < 17.0 -> "Moderate Thinness"
            bmi < 18.5 -> "Mild Thinness"
            bmi < 25.0 -> "Normal"
            bmi < 30.0 -> "Overweight"
            bmi < 35.0 -> "Obese Class I"
            bmi < 40.0 -> "Obese Class II"
            else -> "Obese Class III"
        }
    }

    fun getHealthRisk(category: String): String {
        return when (category) {
            "Severe Thinness", "Moderate Thinness", "Mild Thinness" ->
                "Increased risk for health problems"

            "Normal" ->
                "Low risk for health problems"

            "Overweight" ->
                "Increased risk for heart disease, high blood pressure, and diabetes"

            "Obese Class I" ->
                "High risk for heart disease, high blood pressure, and diabetes"

            "Obese Class II", "Obese Class III" ->
                "Very high risk for heart disease, high blood pressure, and diabetes"

            else -> "Unknown risk"
        }
    }

    fun getCategoryColor(category: String): Int {
        return when (category) {
            "Severe Thinness", "Moderate Thinness", "Mild Thinness" -> 0xFFE57373.toInt() // Light Red
            "Normal" -> 0xFF81C784.toInt() // Light Green
            "Overweight" -> 0xFFFFD54F.toInt() // Amber
            "Obese Class I" -> 0xFFFF8A65.toInt() // Light Orange
            "Obese Class II", "Obese Class III" -> 0xFFE53935.toInt() // Red
            else -> 0xFF9E9E9E.toInt() // Grey
        }
    }
}