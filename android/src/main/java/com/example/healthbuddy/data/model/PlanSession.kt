package com.example.healthbuddy.data.model

import com.squareup.moshi.JsonClass

//Own by Plan
@JsonClass(generateAdapter = true)
data class PlanSession(
    val id: Long,
    val sessionDayOfWeek: String,
    val targetCalories: Float,
    val sessionOrder: Int,
    val category: String,
    val muscleGroup: String?
)
