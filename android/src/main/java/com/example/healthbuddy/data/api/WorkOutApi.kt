package com.example.healthbuddy.data.api

import com.example.healthbuddy.data.model.AddUserPlanRequest
import com.example.healthbuddy.data.model.ExercisePageResponse
import com.example.healthbuddy.data.model.GoalWithPlans
import com.example.healthbuddy.data.model.SessionExerciseCreateRequest
import com.example.healthbuddy.data.model.TodayWorkoutSession
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WorkOutApi {

    @GET("goals/{id}")
    suspend fun getGoals(
        @Path("id") id: Int
    ): GoalWithPlans

    @POST("user-plans")
    suspend fun addUserPlan(
        @Body body: AddUserPlanRequest
    )

    @GET("sessions/today")
    suspend fun getTodaySession(): TodayWorkoutSession

    @GET("exercises")
    suspend fun getExercisesByFilter(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("category") category: String,
        @Query("activityLevel") activityLevel: String,
        @Query("muscleGroup") muscleGroup: Long
    ): ExercisePageResponse

    @POST("session-exercises")
    suspend fun addSessionExercise(
        @Body body: SessionExerciseCreateRequest
    )
    @DELETE("session-exercises/{id}")
    suspend fun deleteSessionExercise(
        @Path("id") id: Long
    )
    @PUT("session-exercises/{id}")
    suspend fun updateSessionExercise(
        @Path("id") id: Long,
        @Body body: SessionExerciseCreateRequest
    )
}
