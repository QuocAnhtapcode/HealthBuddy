package com.example.healthbuddy.screens.auth

import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.POST

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val token: String
)
interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): AuthResponse
}
