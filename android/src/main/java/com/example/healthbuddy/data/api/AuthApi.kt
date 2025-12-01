package com.example.healthbuddy.data.api

import com.example.healthbuddy.data.model.LoginRequest
import com.example.healthbuddy.data.model.SignUpRequest
import com.example.healthbuddy.data.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("v1/auth/login")
    suspend fun login(@Body body: LoginRequest): User

    @POST("v1/auth/signup")
    suspend fun signUp(
        @Body body: SignUpRequest
    ): User
}
