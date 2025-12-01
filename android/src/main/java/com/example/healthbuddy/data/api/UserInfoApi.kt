package com.example.healthbuddy.data.api

import com.example.healthbuddy.data.model.HealthInfo
import com.example.healthbuddy.data.model.HealthInfoRequest
import com.example.healthbuddy.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserInfoApi {
    @GET("v1/user")
    suspend fun getUser(): User

    @GET("v1/health-info")
    suspend fun getLatest(): HealthInfo

    @POST("v1/health-info")
    suspend fun createHealthInfo(
        @Body body: HealthInfoRequest
    ): HealthInfo
}
