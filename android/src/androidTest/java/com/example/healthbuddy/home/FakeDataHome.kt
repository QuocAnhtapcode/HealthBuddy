package com.example.healthbuddy.home

import com.example.healthbuddy.data.model.CaloriesStat
import com.example.healthbuddy.data.model.HealthInfo
import com.example.healthbuddy.data.model.HealthInfoPage
import com.example.healthbuddy.data.model.Page
import com.example.healthbuddy.data.model.RunSession

fun fakeCaloriesStats() = listOf(
    CaloriesStat(
        userId = 1,
        date = "2025-12-20",
        burnedCalories = 500f,
        eatenCalories = 2000f,
        eatenCarbs = 250f,
        eatenFats = 70f,
        eatenProteins = 120f
    ),
    CaloriesStat(
        userId = 1,
        date = "2025-12-21",
        burnedCalories = 600f,
        eatenCalories = 2200f,
        eatenCarbs = 270f,
        eatenFats = 75f,
        eatenProteins = 130f
    )
)

fun fakeRunSessions() = listOf(
    RunSession(
        id = 1,
        averageHeartRate = 140,
        totalDistanceMeters = 5000,
        totalCalories = 350,
        durationMillis = 30 * 60 * 1000L,
        timestampMillis = 1700000000000
    ),
    RunSession(
        id = 2,
        averageHeartRate = 145,
        totalDistanceMeters = 6000,
        totalCalories = 400,
        durationMillis = 35 * 60 * 1000L,
        timestampMillis = 1700001000000
    )
)

fun fakeHealthInfoPage() = HealthInfoPage(
    content = listOf(
        HealthInfo(
            id = 1,
            height = 170f,
            weight = 70f,
            bmi = 24.2f,
            bmr = 1600f,
            fatPercentage = 18f,
            createdDate = "2025-12-01T10:00:00"
        ),
        HealthInfo(
            id = 2,
            height = 170f,
            weight = 71f,
            bmi = 24.6f,
            bmr = 1610f,
            fatPercentage = 18.5f,
            createdDate = "2025-12-10T10:00:00"
        )
    ),
    page = Page(10, 0, 2, 1)
)
