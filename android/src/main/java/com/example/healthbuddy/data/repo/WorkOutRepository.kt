package com.example.healthbuddy.data.repo

import com.example.healthbuddy.data.api.WorkOutApi
import com.example.healthbuddy.data.model.AddUserPlanRequest
import com.example.healthbuddy.data.model.ExerciseFilterRequest
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

    suspend fun getExerciseByFilter(filter: ExerciseFilterRequest): Result<ExercisePageResponse> =
        runCatching { api.getExercisesByFilter(filter) }

    suspend fun addSessionExercise(body: SessionExerciseCreateRequest): Result<Unit> =
        runCatching { api.addSessionExercise(body) }
}
