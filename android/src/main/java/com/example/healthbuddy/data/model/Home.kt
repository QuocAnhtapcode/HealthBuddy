package com.example.healthbuddy.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CaloriesStat(
    val userId: Long,
    val date: String,              // "2025-12-16"
    val burnedCalories: Float,
    val eatenCalories: Float,
    val eatenCarbs: Float,
    val eatenFats: Float,
    val eatenProteins: Float
)
