package com.example.dailyburn.models


data class FitnessTip(
    val id: Int,
    val title: String,
    val content: String,
    val category: String,
    val iconResId: Int? = null  // Optional icon resource ID
)
