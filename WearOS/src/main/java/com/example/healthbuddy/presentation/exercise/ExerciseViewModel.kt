package com.example.healthbuddy.presentation.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbuddy.data.DataSyncRepository
import com.example.healthbuddy.data.HealthServicesRepository
import com.example.healthbuddy.data.ServiceState
import com.example.healthbuddy.presentation.summary.SummaryScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ExerciseViewModel
@Inject
constructor(
    private val healthServicesRepository: HealthServicesRepository,
    private val dataSyncRepository: DataSyncRepository
) : ViewModel() {
    val uiState: StateFlow<ExerciseScreenState> =
        healthServicesRepository.serviceState
            .map {
                ExerciseScreenState(
                    hasExerciseCapabilities = healthServicesRepository.hasExerciseCapability(),
                    isTrackingAnotherExercise =
                    healthServicesRepository
                        .isTrackingExerciseInAnotherApp(),
                    serviceState = it,
                    exerciseState = (it as? ServiceState.Connected)?.exerciseServiceState
                )
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(3_000),
                healthServicesRepository.serviceState.value.let {
                    ExerciseScreenState(
                        hasExerciseCapabilities = true,
                        isTrackingAnotherExercise = false,
                        serviceState = it,
                        exerciseState = (it as? ServiceState.Connected)?.exerciseServiceState
                    )
                }
            )

    suspend fun isExerciseInProgress(): Boolean =
        healthServicesRepository.isExerciseInProgress()

    fun startExercise() {
        healthServicesRepository.startExercise()
    }

    fun pauseExercise() {
        healthServicesRepository.pauseExercise()
    }

    fun resumeExercise() {
        healthServicesRepository.resumeExercise()
    }

    fun endExercise() {
        healthServicesRepository.endExercise()
    }
    fun sendExerciseSummary(summaryScreenState: SummaryScreenState){
        dataSyncRepository.sendExerciseSummary(summaryScreenState)
    }
}

