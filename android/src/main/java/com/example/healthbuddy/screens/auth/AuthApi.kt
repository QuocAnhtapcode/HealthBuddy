package com.example.healthbuddy.screens.auth

import com.example.healthbuddy.data.model.LoginRequest
import com.example.healthbuddy.data.model.LoginResponse
import com.example.healthbuddy.data.model.SignUpRequest
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): LoginResponse

    @POST("auth/signup")
    suspend fun signUp(
        @Body body: SignUpRequest
    ): LoginResponse
}
