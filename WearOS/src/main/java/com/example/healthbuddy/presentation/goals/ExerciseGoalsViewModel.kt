package com.example.healthbuddy.presentation.goals

import androidx.compose.runtime.State
import androidx.compose.runtime.asDoubleState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.healthbuddy.data.ExerciseClientManager
import com.example.healthbuddy.data.Thresholds
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExerciseGoalsViewModel
@Inject
constructor(
    private val exerciseClientManager: ExerciseClientManager
) : ViewModel() {
    private val _distanceGoal = mutableDoubleStateOf(0.0)
    val distanceGoal: State<Double> = _distanceGoal.asDoubleState()

    private val _durationGoal = mutableStateOf(kotlin.time.Duration.ZERO)
    val durationGoal: State<kotlin.time.Duration> = _durationGoal

    fun setGoals(thresholds: Thresholds) {
        _distanceGoal.doubleValue = thresholds.distance
        _durationGoal.value = thresholds.duration
        exerciseClientManager.updateGoals(thresholds)
    }
}
