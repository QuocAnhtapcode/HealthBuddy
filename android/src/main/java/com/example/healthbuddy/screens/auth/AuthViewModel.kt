package com.example.healthbuddy.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbuddy.data.AuthRepository
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
    val token: String? = null
)
@HiltViewModel
class AuthViewModel
@Inject
constructor(
    private val repo: AuthRepository
): ViewModel(){

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui
    init {
        viewModelScope.launch {
            repo.tokenFlow.collect { token ->
                val realToken = token.takeIf { !it.isNullOrBlank() }
                _ui.update {
                    it.copy(
                        isLoggedIn = realToken != null,
                        token = realToken
                    )
                }
            }
        }
    }
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            val result = repo.login(email, password)
            _ui.update { s ->
                s.copy(isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
            Log.d("AuthVM", "login() done, error=${result.exceptionOrNull()?.message}")
        }
    }
    fun signUp(username: String, email: String, password: String){
        viewModelScope.launch {
            val result = repo.signUp(username, email, password)
            Log.d("AuthVM", "signUp() done, error=${result.exceptionOrNull()?.message}")
        }
    }
    fun logout() {
        viewModelScope.launch {
            repo.logout()
            _ui.update { AuthUiState() }
        }
    }
}

