package com.example.healthbuddy.data.api

import com.example.healthbuddy.data.model.CaloriesStat
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {
    @GET("calories-stat")
    suspend fun getCaloriesStat(
        @Query("startDate") startDate: String,  // yyyy-MM-dd
        @Query("endDate") endDate: String
    ): List<CaloriesStat>
}
