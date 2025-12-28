package com.example.healthbuddy.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbuddy.data.model.*
import com.example.healthbuddy.data.repo.WorkOutRepository
import com.example.healthbuddy.screens.workout.WorkoutUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutViewModelForTest(
    private val repo: WorkOutRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(WorkoutUiState())
    val ui = _ui.asStateFlow()

    fun loadTodaySession() {
        viewModelScope.launch {
            _ui.update { it.copy(loadingSession = true, error = null, isRestDay = false) }

            repo.getTodaySession()
                .onSuccess { session ->
                    _ui.update {
                        it.copy(
                            loadingSession = false,
                            todaySession = session,
                            isRestDay = false
                        )
                    }
                }
                .onFailure {
                    _ui.update {
                        it.copy(
                            loadingSession = false,
                            todaySession = null,
                            isRestDay = true
                        )
                    }
                }
        }
    }

    fun loadExercisesForToday(userLevel: String, groupId: Long) {
        val session = _ui.value.todaySession ?: return

        viewModelScope.launch {
            _ui.update { it.copy(loadingExercises = true, error = null) }

            repo.getExerciseByFilter(
                category = session.planSession.category,
                activityLevel = userLevel,
                muscleGroup = groupId
            )
                .onSuccess { page ->
                    _ui.update {
                        it.copy(
                            loadingExercises = false,
                            exercises = page.content,
                            exercisePage = page.page
                        )
                    }
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            loadingExercises = false,
                            error = e.message
                        )
                    }
                }
        }
    }

    fun selectExercise(exercise: Exercise) {
        _ui.update { it.copy(selectedExercise = exercise) }
    }

    fun clearSelectedExercise() {
        _ui.update { it.copy(selectedExercise = null) }
    }
}
