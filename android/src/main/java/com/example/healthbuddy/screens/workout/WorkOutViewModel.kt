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
import retrofit2.HttpException

data class WorkoutUiState(
    val loadingSession: Boolean = false,
    val todaySession: TodayWorkoutSession? = null,

    val loadingExercises: Boolean = false,
    val exercises: List<Exercise> = emptyList(),
    val exercisePage: Page? = null,

    val addingExercise: Boolean = false,

    val selectedExercise: Exercise? = null,
    val isRestDay: Boolean = false,
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
            _ui.update { it.copy(loadingSession = true, error = null, isRestDay = false) }

            repo.getTodaySession()
                .onSuccess { session ->
                    _ui.update {
                        it.copy(
                            loadingSession = false,
                            todaySession   = session,
                            isRestDay      = false,
                            error          = null
                        )
                    }
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            loadingSession = false,
                            todaySession   = null,
                            isRestDay      = true,
                            error          = null
                        )
                    }
                }
        }
    }

    // ---- 2) Load exercises theo planSession + activityLevel user + muscle group ----
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
                            exercisePage = page.page,
                            error = null
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

    fun updateExerciseAsDuration(
        sessionExerciseId: Long,
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

            repo.updateSessionExercise(sessionExerciseId, body)
                .onSuccess { reloadAfterAdd(onDone) }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            addingExercise = false,
                            error = e.message ?: "Cannot update exercise"
                        )
                    }
                }
        }
    }

    fun updateExerciseAsStrength(
        sessionExerciseId: Long,
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

            repo.updateSessionExercise(sessionExerciseId, body)
                .onSuccess { reloadAfterAdd(onDone) }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            addingExercise = false,
                            error = e.message ?: "Cannot update exercise"
                        )
                    }
                }
        }
    }
}
