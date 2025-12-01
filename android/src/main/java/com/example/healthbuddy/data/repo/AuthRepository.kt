package com.example.healthbuddy.data.repo

import android.util.Log
import com.example.healthbuddy.data.api.AuthApi
import com.example.healthbuddy.data.local.TokenStore
import com.example.healthbuddy.data.model.LoginRequest
import com.example.healthbuddy.data.model.SignUpRequest
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val tokenStore: TokenStore
) {
    val tokenFlow: Flow<String?> = tokenStore.tokenFlow

    suspend fun login(input: String, password: String): Result<Unit> =
        runCatching {
            val request = if (input.contains("@")) {
                LoginRequest(email = input, password = password)
            } else {
                LoginRequest(username = input, password = password)
            }

            Log.d("AuthRepo", "login() request=$request")

            val user = api.login(request)

            Log.d("AuthRepo", "login() success user=$user")

            tokenStore.saveToken(user.token)
        }.onFailure {
            Log.e("AuthRepo", "login() failed: ${it.message}", it)
        }

    suspend fun signUp(signUpRequest: SignUpRequest): Result<Unit> =
        runCatching {
            val res = api.signUp(signUpRequest)
            Log.d("AuthRepo", "signUp() success, user=$res")
            tokenStore.saveToken(res.token)
        }.onFailure { e ->
            Log.e("AuthRepo", "signUp() failed: ${e.message}", e)
        }
    suspend fun logout() = tokenStore.clear()
}
