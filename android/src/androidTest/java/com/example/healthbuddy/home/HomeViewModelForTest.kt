package com.example.healthbuddy.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthbuddy.data.repo.HomeRepository
import com.example.healthbuddy.screens.home.CaloriesRange
import com.example.healthbuddy.screens.home.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModelForTest(
    private val repository: HomeRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(HomeUiState())
    val ui = _ui.asStateFlow()

    fun loadCaloriesStats(startDate: String, endDate: String) {
        viewModelScope.launch {
            _ui.update { it.copy(loadingStats = true, statsError = null) }

            repository.getCaloriesStat(startDate, endDate)
                .onSuccess { list ->
                    _ui.update {
                        it.copy(
                            loadingStats = false,
                            caloriesStats = list,
                            statsError = null
                        )
                    }
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            loadingStats = false,
                            statsError = e.message ?: "Load stats failed"
                        )
                    }
                }
        }
    }

    fun setRange(startDate: String, endDate: String) {
        _ui.update {
            it.copy(range = CaloriesRange(startDate, endDate))
        }
    }

    fun applyRange() {
        val r = _ui.value.range
        loadCaloriesStats(r.startDate, r.endDate)
    }

    fun loadRunHistory() {
        viewModelScope.launch {
            _ui.update { it.copy(loadingRunHistory = true, runError = null) }

            repository.getRunSessions()
                .onSuccess { list ->
                    val sorted = list.sortedByDescending { it.timestampMillis }
                    _ui.update {
                        it.copy(
                            loadingRunHistory = false,
                            runHistory = sorted,
                            latestRun = sorted.firstOrNull(),
                            runError = null
                        )
                    }
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            loadingRunHistory = false,
                            runError = e.message ?: "Load run failed"
                        )
                    }
                }
        }
    }

    fun loadHealthInfoHistory(page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            _ui.update { it.copy(loadingHealth = true, healthError = null) }

            repository.getAllHealthInfo(page, size)
                .onSuccess { pageResult ->
                    val sorted = pageResult.content
                        .filter { it.createdDate != null }
                        .sortedBy {
                            LocalDate.parse(it.createdDate!!.substring(0, 10))
                        }

                    _ui.update {
                        it.copy(
                            loadingHealth = false,
                            healthInfos = sorted,
                            healthError = null
                        )
                    }
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            loadingHealth = false,
                            healthError = e.message ?: "Load health failed"
                        )
                    }
                }
        }
    }
}
