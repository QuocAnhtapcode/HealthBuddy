package com.example.healthbuddy.screens.workout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbuddy.data.model.*
import com.example.healthbuddy.data.repo.WorkOutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutUiState(
    val loadingSession: Boolean = false,
    val todaySession: TodayWorkoutSession? = null,

    val loadingExercises: Boolean = false,
    val exercises: List<Exercise> = emptyList(),
    val exercisePage: Page? = null,

    val addingExercise: Boolean = false,

    val selectedExercise: Exercise? = null,
    val error: String? = null
)

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val repo: WorkOutRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(WorkoutUiState())
    val ui = _ui.asStateFlow()

    // ---- 1) Load session hôm nay ----
    fun loadTodaySession() {
        viewModelScope.launch {
            _ui.update { it.copy(loadingSession = true, error = null) }

            repo.getTodaySession()
                .onSuccess { session ->
                    _ui.update {
                        it.copy(
                            loadingSession = false,
                            todaySession = session,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            loadingSession = false,
                            error = e.message ?: "Cannot load today's session"
                        )
                    }
                }
        }
    }

    // ---- 2) Load exercises theo planSession + activityLevel user ----
    fun loadExercisesForToday(activityLevel: String) {
        val session = _ui.value.todaySession ?: return

        val filter = ExerciseFilterRequest(
            category = session.planSession.category,
            activityLevel = activityLevel,
            muscleGroups = session.planSession.muscleGroups.map {
                MuscleGroupId(it.id)
            }
        )
        Log.d("Filter",filter.toString())

        viewModelScope.launch {
            _ui.update { it.copy(loadingExercises = true, error = null) }

            repo.getExerciseByFilter(filter)
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
                            error = e.message ?: "Cannot load exercises"
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

    fun clearError() {
        _ui.update { it.copy(error = null) }
    }

    // ---- 3) Add exercise dạng thời gian (hours) ----
    fun addExerciseAsDuration(
        exerciseId: Long,
        hours: Float,
        onDone: () -> Unit = {}
    ) {
        val sessionId = _ui.value.todaySession?.id ?: return

        viewModelScope.launch {
            _ui.update { it.copy(addingExercise = true, error = null) }

            val body = SessionExerciseCreateRequest(
                exercise = IdRef(exerciseId),
                session = IdRef(sessionId),
                hours = hours
            )

            repo.addSessionExercise(body)
                .onSuccess {
                    // reload session để sync lại danh sách + calories
                    reloadAfterAdd(onDone)
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            addingExercise = false,
                            error = e.message ?: "Cannot add exercise"
                        )
                    }
                }
        }
    }

    // ---- 4) Add exercise dạng reps / sets / weight ----
    fun addExerciseAsStrength(
        exerciseId: Long,
        reps: Int,
        sets: Int,
        weight: Float,
        onDone: () -> Unit = {}
    ) {
        val sessionId = _ui.value.todaySession?.id ?: return

        viewModelScope.launch {
            _ui.update { it.copy(addingExercise = true, error = null) }

            val body = SessionExerciseCreateRequest(
                exercise = IdRef(exerciseId),
                session = IdRef(sessionId),
                reps = reps,
                sets = sets,
                weightUsed = weight
            )

            repo.addSessionExercise(body)
                .onSuccess {
                    reloadAfterAdd(onDone)
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            addingExercise = false,
                            error = e.message ?: "Cannot add exercise"
                        )
                    }
                }
        }
    }

    private fun reloadAfterAdd(onDone: () -> Unit) {
        viewModelScope.launch {
            repo.getTodaySession()
                .onSuccess { session ->
                    _ui.update {
                        it.copy(
                            addingExercise = false,
                            todaySession = session,
                            selectedExercise = null
                        )
                    }
                    onDone()
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            addingExercise = false,
                            error = e.message ?: "Cannot reload session"
                        )
                    }
                }
        }
    }
}
