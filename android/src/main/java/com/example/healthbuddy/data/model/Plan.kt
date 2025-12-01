package com.example.healthbuddy.data.model

import com.squareup.moshi.JsonClass

//Own by UserPlan and GoalWithPlans
@JsonClass(generateAdapter = true)
data class Plan(
    val id: Int,
    val name: String,
    val startDate: String,
    val endDate: String,
    val planSessions: List<PlanSession> = emptyList()
)
