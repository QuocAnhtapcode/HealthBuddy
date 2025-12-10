package com.example.healthbuddy.screens.goal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbuddy.data.model.GoalWithPlans
import com.example.healthbuddy.data.repo.WorkOutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.onFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
data class GoalUiState(
    val loading: Boolean = false,
    val goalWithPlans: GoalWithPlans? = null,
    val error: String? = null
)
@HiltViewModel
class GoalViewModel
@Inject
constructor(private val repo: WorkOutRepository
): ViewModel(){

    private val _ui = MutableStateFlow(GoalUiState())
    val ui = _ui.asStateFlow()

    fun getGoalWithPlan(id: Int){
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)

            repo.getPlanByGoals(id)
                .onSuccess { goalWithPlans ->
                    _ui.value = _ui.value.copy(
                        loading = false,
                        goalWithPlans = goalWithPlans,
                        error = null
                    )
                    Log.d("GoalViewModel", goalWithPlans.toString())
                }
                .onFailure { e ->
                    _ui.value = _ui.value.copy(
                        loading = false,
                        error = e.message ?: "Lỗi khi tải goals"
                    )
                    Log.d("GoalViewModel", e.message.toString())
                }
        }
    }
    fun addUserPlan(id: Long){
        viewModelScope.launch {
            repo.addUserPlan(id)
        }
    }
}
