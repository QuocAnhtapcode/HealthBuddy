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

@JsonClass(generateAdapter = true)
data class WatchCaloriesStat(
    val date: String,          // MM-dd
    val burned: Float,
    val eaten: Float,
    val net: Float             // eaten - burned
)

@JsonClass(generateAdapter = true)
data class RunSession(
    val id: Int,
    val averageHeartRate: Int,
    val totalDistanceMeters: Int,
    val totalCalories: Int,
    val durationMillis: Long,
    val timestampMillis: Long
)

@JsonClass(generateAdapter = true)
data class CreateRunSessionRequest(
    val averageHeartRate: Int,
    val totalDistanceMeters: Int,
    val totalCalories: Int,
    val durationMillis: Long,
    val timestampMillis: Long
)
