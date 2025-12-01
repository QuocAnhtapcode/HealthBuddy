package com.example.healthbuddy.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoalWithPlans(
    val id: Int,
    val name: String,
    val description: String,
    val plans: List<Plan>
)
