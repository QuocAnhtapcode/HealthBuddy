package com.example.healthbuddy.screens.userinfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbuddy.data.model.HealthInfo
import com.example.healthbuddy.data.model.HealthInfoRequest
import com.example.healthbuddy.data.model.User
import com.example.healthbuddy.data.repo.UserInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class EntryRoute {
    WEIGHT_HEIGHT,   // chưa có health info → vào Weight/Height
    QUIZ,            // có health info, nhưng activityLevel null → vào Quiz
    GOAL,            // có health info + activityLevel, nhưng goal null → vào Goal
    MAIN             // đầy đủ → vào Main
}

@HiltViewModel
class UserInfoViewModel
@Inject
constructor(
    private val repo: UserInfoRepository
): ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val healthInfo: HealthInfo? = null,
        var user: User? = null,
        val error: String? = null,
        val needInput: Boolean = true
    )

    private val _ui = MutableStateFlow(UiState())
    val ui = _ui.asStateFlow()

    init {
        getUser()
        loadLatest()
    }

    fun getUser() {
        viewModelScope.launch {
            repo.getUser()
                .onSuccess { user ->
                    _ui.update { it.copy(user = user) }
                }
                .onFailure { e ->
                    e.printStackTrace()
                    _ui.update { it.copy(error = e.message) }
                }
        }
    }

    fun loadLatest() {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }

            val result = repo.getLatest()

            result.fold(
                onSuccess = { info ->
                    if (info == null) {
                        // không có health info
                        _ui.update {
                            it.copy(
                                loading = false,
                                healthInfo = null,
                                needInput = true
                            )
                        }
                    } else {
                        _ui.update {
                            it.copy(
                                loading = false,
                                healthInfo = info,
                                needInput = false
                            )
                        }
                    }
                },
                onFailure = { e ->
                    _ui.update { it.copy(loading = false, error = e.message) }
                }
            )
        }
    }

    fun submit(healthInfoRequest: HealthInfoRequest) {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }
            val result = repo.createHealthInfo(healthInfoRequest)
            result.fold(
                onSuccess = { info ->
                    Log.d("Submit health info",info.toString())
                    _ui.update {
                        it.copy(
                            loading = false,
                            healthInfo = info,
                            needInput = false
                        )
                    }
                },
                onFailure = { e ->
                    Log.d("Submit health info",e.message.toString())
                    _ui.update {
                        it.copy(
                            loading = false,
                            error = e.message
                        )
                    }
                }
            )
        }
    }

    suspend fun resolveEntryRoute(): EntryRoute {
        val userResult = repo.getUser()
        val user = userResult.getOrThrow()

        val healthResult = repo.getLatest()
        val healthInfo = healthResult.getOrNull()

        _ui.update {
            it.copy(
                user = user,
                healthInfo = healthInfo,
                needInput = (healthInfo == null),
                loading = false,
                error = null
            )
        }

        return when {
            healthInfo == null -> EntryRoute.WEIGHT_HEIGHT

            user.activityLevel == null -> EntryRoute.QUIZ

            user.userPlans.isEmpty() -> EntryRoute.GOAL

            else -> EntryRoute.MAIN
            //else -> EntryRoute.GOAL
        }
    }
}
