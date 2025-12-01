package com.example.healthbuddy.data.model

import com.squareup.moshi.JsonClass

enum class GoalName {
    LOSS_WEIGHT,
    BUILD_MUSCLE,
    MAINTAIN_WEIGHT
}

@JsonClass(generateAdapter = true)
data class Goal(
    val id: Int,
    val name: GoalName,
    val description: String? = null,
    val plans: List<Plan> = emptyList()
)
