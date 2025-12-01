package com.example.healthbuddy.data.model

import com.squareup.moshi.JsonClass

//Own by User
@JsonClass(generateAdapter = true)
data class UserPlan(
    val id: Long,
    val plan: Plan,
    val startDate: String?,
    val endDate: String?,
    val status: String,
    val menuPlans: List<MenuPlan>
)

@JsonClass(generateAdapter = true)
data class AddUserPlanRequest(
    val id: Int
)

