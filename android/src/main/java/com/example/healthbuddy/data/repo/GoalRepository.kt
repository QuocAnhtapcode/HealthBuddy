package com.example.healthbuddy.data.repo

import com.example.healthbuddy.data.api.GoalApi
import com.example.healthbuddy.data.model.AddUserPlanRequest
import com.example.healthbuddy.data.model.GoalWithPlans
import javax.inject.Inject

class GoalRepository
@Inject constructor(
    private val api: GoalApi
){
    suspend fun getPlanByGoals(id: Int): Result<GoalWithPlans> =
        runCatching { api.getGoals(id) }

    suspend fun addUserPlan(id: Int): Result<Unit> =
        runCatching { api.addUserPlan(AddUserPlanRequest(id)) }
}
