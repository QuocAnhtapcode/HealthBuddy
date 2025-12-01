package com.example.healthbuddy.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbuddy.data.model.SignUpRequest
import com.example.healthbuddy.data.model.User
import com.example.healthbuddy.data.repo.AuthRepository
import com.example.healthbuddy.data.repo.UserInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null
)
@HiltViewModel
class AuthViewModel
@Inject
constructor(
    private val authRepo: AuthRepository,
    private val userRepo: UserInfoRepository,
): ViewModel(){
    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui

    init {
        viewModelScope.launch {
            authRepo.tokenFlow.collect { token ->
                val user = userRepo.getUser()
                _ui.update {
                    it.copy(
                        isLoggedIn = user.isSuccess,
                        user = user.getOrNull()
                    )
                }
            }
        }
    }
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            val result = authRepo.login(email, password)
            _ui.update { s ->
                s.copy(isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
            Log.d("AuthVM", "login() done, error=${result.exceptionOrNull()?.message}")
        }
    }
    fun signUp(signUpRequest: SignUpRequest){
        viewModelScope.launch {
            val result = authRepo.signUp(signUpRequest)
            Log.d("AuthVM", "signUp() done, error=${result.exceptionOrNull()?.message}")
        }
    }
    fun logout() {
        viewModelScope.launch {
            authRepo.logout()
            _ui.update { AuthUiState() }
        }
    }
}

