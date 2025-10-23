package com.example.healthbuddy.presentation.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.healthbuddy.app.Screen
import com.example.healthbuddy.data.DataSyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow

@HiltViewModel
class SummaryViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val uiState =
        MutableStateFlow(
            SummaryScreenState(
                averageHeartRate =
                savedStateHandle
                    .get<Float>(Screen.Summary.averageHeartRateArg)!!
                    .toDouble(),
                totalDistance =
                savedStateHandle
                    .get<Float>(Screen.Summary.totalDistanceArg)!!
                    .toDouble(),
                totalCalories =
                savedStateHandle
                    .get<Float>(Screen.Summary.totalCaloriesArg)!!
                    .toDouble(),
                elapsedTime =
                Duration.parse(
                    savedStateHandle[Screen.Summary.elapsedTimeArg]!!
                )
            )
        )
}
