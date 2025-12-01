package com.example.healthbuddy.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HealthInfo(
    val id: Int? = null,
    val height: Float,
    val weight: Float,
    val bmi: Float? = null,
    val bmr: Float? = null,
    val fatPercentage: Float,
    val createdDate: String? = null,
    val updatedDate: String? = null
)

@JsonClass(generateAdapter = true)
data class HealthInfoRequest(
    var height: Float,
    var weight: Float,
    val fatPercentage: Float
)

@JsonClass(generateAdapter = true)
data class ErrorDetail(
    val detail: String
)
