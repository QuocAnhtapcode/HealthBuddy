package com.example.healthbuddy.data.repo

import com.example.healthbuddy.data.api.WorkOutApi
import com.example.healthbuddy.data.model.AddUserPlanRequest
import com.example.healthbuddy.data.model.ExercisePageResponse
import com.example.healthbuddy.data.model.GoalWithPlans
import com.example.healthbuddy.data.model.SessionExerciseCreateRequest
import com.example.healthbuddy.data.model.TodayWorkoutSession
import javax.inject.Inject

class WorkOutRepository
@Inject constructor(
    private val api: WorkOutApi
){
    suspend fun getPlanByGoals(id: Int): Result<GoalWithPlans> =
        runCatching { api.getGoals(id) }

    suspend fun addUserPlan(id: Long): Result<Unit> =
        runCatching { api.addUserPlan(AddUserPlanRequest(id)) }

    suspend fun getTodaySession(): Result<TodayWorkoutSession> =
        runCatching { api.getTodaySession() }

    suspend fun getExerciseByFilter(
        category: String,
        activityLevel: String,
        muscleGroup: Long,
        page: Int = 0,
        size: Int = 20
    ): Result<ExercisePageResponse> =
        runCatching {
            api.getExercisesByFilter(
                page = page,
                size = size,
                category = category,
                activityLevel = activityLevel,
                muscleGroup = muscleGroup
            )
        }
    suspend fun addSessionExercise(body: SessionExerciseCreateRequest): Result<Unit> =
        runCatching { api.addSessionExercise(body) }
}
