package com.example.healthbuddy.workout

import com.example.healthbuddy.data.model.Exercise
import com.example.healthbuddy.data.model.ExercisePageResponse
import com.example.healthbuddy.data.model.MuscleGroup
import com.example.healthbuddy.data.model.Page
import com.example.healthbuddy.data.model.PlanSession
import com.example.healthbuddy.data.model.SessionExercise
import com.example.healthbuddy.data.model.TodayWorkoutSession
fun fakePlanSession() = PlanSession(
    id = 1,
    sessionDayOfWeek = "MONDAY",
    targetCalories = 300f,
    category = "CARDIO",
    muscleGroups = listOf(
        MuscleGroup(1, "Leg"),
        MuscleGroup(2, "Core")
    )
)

fun fakeTodayWorkoutSession() = TodayWorkoutSession(
    id = 10,
    planSession = fakePlanSession(),
    estimatedCalories = 320f,
    actualDurationMinutes = 30f,
    actualCaloriesBurned = 280f,
    notes = null,
    status = "IN_PROGRESS",
    sessionExercises = emptyList(),
    totalExerciseCalories = 280f
)

fun fakeExercises() = listOf(
    Exercise(
        id = 1,
        name = "Running",
        description = "Outdoor running",
        category = "CARDIO",
        muscleGroups = listOf(MuscleGroup(1, "Leg")),
        defaultCaloriesPerUnit = 10f,
        unit = "minutes",
        imageUrl = null,
        imageId = null,
        difficulty = "beginner"
    ),
    Exercise(
        id = 2,
        name = "Plank",
        description = "Core plank",
        category = "CARDIO",
        muscleGroups = listOf(MuscleGroup(2, "Core")),
        defaultCaloriesPerUnit = 8f,
        unit = "minutes",
        imageUrl = null,
        imageId = null,
        difficulty = "beginner"
    )
)

fun fakeExercisePage() = ExercisePageResponse(
    content = fakeExercises(),
    page = Page(size = 20, number = 0, totalElements = 2, totalPages = 1)
)

