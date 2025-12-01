package com.example.healthbuddy.data.api

import com.example.healthbuddy.data.model.AddUserPlanRequest
import com.example.healthbuddy.data.model.GoalWithPlans
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GoalApi {

    @GET("goals/{id}")
    suspend fun getGoals(
        @Path("id") id: Int
    ): GoalWithPlans

    @POST("user-plans")
    suspend fun addUserPlan(
        @Body body: AddUserPlanRequest
    )
}
