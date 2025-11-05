package com.example.healthbuddy.data

import android.util.Log
import com.example.healthbuddy.data.local.TokenStore
import com.example.healthbuddy.data.model.LoginRequest
import com.example.healthbuddy.data.model.SignUpRequest
import com.example.healthbuddy.screens.auth.AuthApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val tokenStore: TokenStore
) {
    val tokenFlow: Flow<String?> = tokenStore.tokenFlow

    suspend fun login(email: String, password: String): Result<Unit> =
        runCatching {
            Log.d("AuthRepo", "login() email=$email")
            val res = api.login(LoginRequest(email, password))
            Log.d("AuthRepo", "login() success, user=$res")
            tokenStore.saveToken(res.token)
        }.onFailure { e ->
            Log.e("AuthRepo", "login() failed: ${e.message}", e)
        }
    suspend fun signUp(username: String, email: String, password: String): Result<Unit> =
        runCatching {
            Log.d("AuthRepo", "signUp() email=$email")
            val res = api.signUp(
                SignUpRequest(
                    name = null,
                    email = email,
                    username = username,
                    password = password,
                    birthDay = null,
                    age = null,
                    gender = null,
                    activityLevel = null,
                    goal = null,
                    isPremium = false,
                    role = null
                )
            )
            Log.d("AuthRepo", "signUp() success, user=$res")
            tokenStore.saveToken(res.token)
        }.onFailure { e ->
            Log.e("AuthRepo", "signUp() failed: ${e.message}", e)
        }
    suspend fun logout() = tokenStore.clear()
}
