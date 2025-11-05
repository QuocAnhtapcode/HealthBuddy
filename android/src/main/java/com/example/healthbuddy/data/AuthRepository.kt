package com.example.healthbuddy.data

import android.util.Log
import com.example.healthbuddy.data.local.TokenStore
import com.example.healthbuddy.screens.auth.AuthApi
import com.example.healthbuddy.screens.auth.LoginRequest
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
            Log.d("AuthRepo", "login() success, token=${res.token.take(16)}...")
            tokenStore.saveToken(res.token)
        }.onFailure { e ->
            Log.e("AuthRepo", "login() failed: ${e.message}", e)
        }

    suspend fun logout() = tokenStore.clear()
}
