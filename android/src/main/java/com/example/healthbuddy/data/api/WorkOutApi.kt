package com.example.healthbuddy.data.api

import com.example.healthbuddy.data.model.AddUserPlanRequest
import com.example.healthbuddy.data.model.ExercisePageResponse
import com.example.healthbuddy.data.model.GoalWithPlans
import com.example.healthbuddy.data.model.SessionExerciseCreateRequest
import com.example.healthbuddy.data.model.TodayWorkoutSession
import retrofit2.http.Body
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
    // 1) Lấy buổi tập hôm nay
    @GET("sessions/today")
    suspend fun getTodaySession(): TodayWorkoutSession

    // 2) Lọc exercises theo category + activityLevel + muscleGroups
    @GET("exercises")
    suspend fun getExercisesByFilter(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("category") category: String,
        @Query("activityLevel") activityLevel: String,
        @Query("muscleGroup") muscleGroup: Long
    ): ExercisePageResponse

    // 3) Thêm 1 bài tập vào session
    @POST("session-exercises")
    suspend fun addSessionExercise(
        @Body body: SessionExerciseCreateRequest
    )

    @PUT("session-exercises/{id}")
    suspend fun updateSessionExercise(
        @Path("id") id: Long,
        @Body body: SessionExerciseCreateRequest
    )
}
