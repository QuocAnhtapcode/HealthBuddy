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
    val id: Long
)

@JsonClass(generateAdapter = true)
data class Plan(
    val id: Long,
    val name: String,
    val startDate: String?,
    val endDate: String?,
    val planSessions: List<PlanSession> = emptyList()
)

@JsonClass(generateAdapter = true)
data class Exercise(
    val id: Long,
    val name: String,
    val description: String?,
    val category: String,
    val muscleGroups: List<MuscleGroup> = emptyList(),
    val defaultCaloriesPerUnit: Float,
    val unit: String,
    val imageUrl: String?,
    val imageId: String?,
    val difficulty: String
)


@JsonClass(generateAdapter = true)
data class SessionExercise(
    val id: Long,
    val exercise: Exercise,
    val estimatedCalories: Float,
    val reps: Int?,
    val sets: Int?,
    val weightUsed: Float?,
    val hours: Float?,
    val unit: String?
)

// ---------- PLAN SESSION (trong TodaySession) ----------

@JsonClass(generateAdapter = true)
data class PlanSession(
    val id: Long,
    val sessionDayOfWeek: String,
    val targetCalories: Float,
    val category: String,
    val muscleGroups: List<MuscleGroup> = emptyList()
)
@JsonClass(generateAdapter = true)
data class MuscleGroup(
    val id: Long,
    val name: String
)
// ---------- TODAY WORKOUT SESSION ----------

@JsonClass(generateAdapter = true)
data class TodayWorkoutSession(
    val id: Long,
    val planSession: PlanSession,
    val estimatedCalories: Float,
    val actualDurationMinutes: Float,
    val actualCaloriesBurned: Float,
    val notes: String?,
    val status: String,
    val sessionExercises: List<SessionExercise>,
    val totalExerciseCalories: Float
)
// Body gửi lên /exercises
@JsonClass(generateAdapter = true)
data class ExerciseFilterRequest(
    val category: String,
    val activityLevel: String,
    val muscleGroups: List<MuscleGroupId>
)

@JsonClass(generateAdapter = true)
data class MuscleGroupId(
    val id: Long
)

@JsonClass(generateAdapter = true)
data class ExercisePageResponse(
    val content: List<Exercise>,
    val page: Page
)

@JsonClass(generateAdapter = true)
data class SessionExerciseCreateRequest(
    val exercise: IdRef,
    val session: IdRef,
    val reps: Int? = null,
    val sets: Int? = null,
    val weightUsed: Float? = null,
    val hours: Float? = null
)
