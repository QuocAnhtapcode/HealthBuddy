package com.example.healthbuddy.home

import app.cash.turbine.test
import com.example.healthbuddy.MainDispatcherRule
import com.example.healthbuddy.data.repo.HomeRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun applyRangeLoadsCaloriesBasedOnUiRange() = runTest {
        // Given
        val api = FakeHomeApi().apply {
            caloriesResult = Result.success(fakeCaloriesStats())
        }
        val repo = HomeRepository(api)
        val vm = HomeViewModelForTest(repo)

        // When
        vm.setRange("2025-12-20", "2025-12-21")
        vm.applyRange()
        advanceUntilIdle()

        // Then
        val ui = vm.ui.value
        assertThat(ui.loadingStats).isFalse()
        assertThat(ui.statsError).isNull()
        assertThat(ui.caloriesStats).hasSize(2)
    }

    @Test
    fun loadCaloriesStatsSuccessUpdatesUiAndClearsError() = runTest {
        // Given
        val api = FakeHomeApi().apply {
            caloriesResult = Result.success(fakeCaloriesStats())
            runSessionsResult = Result.success(fakeRunSessions())
        }
        val repo = HomeRepository(api)

        val vm = HomeViewModelForTest(repo)
        // When
        vm.loadCaloriesStats("2025-12-20", "2025-12-21")
        advanceUntilIdle()

        // Then
        val ui = vm.ui.value
        assertThat(ui.loadingStats).isFalse()
        assertThat(ui.statsError).isNull()
        assertThat(ui.caloriesStats).hasSize(2)
        assertThat(ui.caloriesStats[0].date).isEqualTo("2025-12-20")
    }

    @Test
    fun loadCaloriesStatsFailSetsStatsError() = runTest {
        // Given
        val api = FakeHomeApi().apply {
            caloriesResult = Result.failure(RuntimeException("Network error"))
        }
        val repo = HomeRepository(api)
        val vm = HomeViewModelForTest(repo)

        // When
        vm.loadCaloriesStats("2025-12-20", "2025-12-21")
        advanceUntilIdle()

        // Then
        val ui = vm.ui.value
        assertThat(ui.loadingStats).isFalse()
        assertThat(ui.caloriesStats).isEmpty()
        assertThat(ui.statsError).isEqualTo("Network error")
    }

    @Test
    fun loadRunHistorySuccessSortsByTimestampDescAndSetsLatestRun() = runTest {
        // Given
        val api = FakeHomeApi().apply {
            runSessionsResult = Result.success(fakeRunSessions())
        }
        val repo = HomeRepository(api)
        val vm = HomeViewModelForTest(repo)

        // When
        vm.loadRunHistory()
        advanceUntilIdle()

        // Then
        val ui = vm.ui.value
        assertThat(ui.loadingRunHistory).isFalse()
        assertThat(ui.runError).isNull()
        assertThat(ui.runHistory).hasSize(2)

        assertThat(ui.runHistory[0].timestampMillis)
            .isGreaterThan(ui.runHistory[1].timestampMillis)

        assertThat(ui.latestRun?.id)
            .isEqualTo(ui.runHistory.first().id)
    }
    @Test
    fun loadHealthInfoHistorySuccessSortsByCreatedDateAsc() = runTest {
        // Given
        val api = FakeHomeApi().apply {
            healthInfoResult = Result.success(fakeHealthInfoPage())
        }
        val repo = HomeRepository(api)
        val vm = HomeViewModelForTest(repo)

        // When
        vm.loadHealthInfoHistory()
        advanceUntilIdle()

        // Then
        val ui = vm.ui.value
        assertThat(ui.loadingHealth).isFalse()
        assertThat(ui.healthError).isNull()
        assertThat(ui.healthInfos).hasSize(2)

        val d1 = ui.healthInfos[0].createdDate!!
        val d2 = ui.healthInfos[1].createdDate!!
        assertThat(d1).isLessThan(d2)
    }

    @Test
    fun applyRangeUsesUiRangeToLoadCalories() = runTest {
        val api = FakeHomeApi().apply {
            caloriesResult = Result.success(fakeCaloriesStats())
        }
        val repo = HomeRepository(api)
        val vm = HomeViewModelForTest(repo)

        vm.setRange("2025-12-20", "2025-12-21")
        vm.applyRange()
        advanceUntilIdle()

        assertThat(vm.ui.value.caloriesStats).isNotEmpty()
    }
    @Test
    fun uiFlowEmitsLoadingThenSuccessForCalories() = runTest {
        val api = FakeHomeApi().apply {
            caloriesResult = Result.success(fakeCaloriesStats())
        }
        val repo = HomeRepository(api)
        val vm = HomeViewModelForTest(repo)

        vm.ui.test {
            // initial
            val initial = awaitItem()
            assertThat(initial.loadingStats).isFalse()

            vm.loadCaloriesStats("2025-12-20", "2025-12-21")

            val loading = awaitItem()
            assertThat(loading.loadingStats).isTrue()

            val done = awaitItem()
            assertThat(done.loadingStats).isFalse()
            assertThat(done.caloriesStats).isNotEmpty()

            cancelAndIgnoreRemainingEvents()
        }
    }
    @Test
    fun loadRunHistoryFailSetsRunError() = runTest {
        val api = FakeHomeApi().apply {
            runSessionsResult = Result.failure(RuntimeException("Server down"))
        }
        val repo = HomeRepository(api)
        val vm = HomeViewModelForTest(repo)

        vm.loadRunHistory()
        advanceUntilIdle()

        val ui = vm.ui.value
        assertThat(ui.loadingRunHistory).isFalse()
        assertThat(ui.runError).isEqualTo("Server down")
    }
    @Test
    fun loadHealthInfoHistoryFailSetsHealthError() = runTest {
        val api = FakeHomeApi().apply {
            healthInfoResult = Result.failure(RuntimeException("Timeout"))
        }
        val repo = HomeRepository(api)
        val vm = HomeViewModelForTest(repo)

        vm.loadHealthInfoHistory()
        advanceUntilIdle()

        val ui = vm.ui.value
        assertThat(ui.loadingHealth).isFalse()
        assertThat(ui.healthError).isEqualTo("Timeout")
    }
    @Test
    fun setRangeOnlyUpdatesUiWithoutTriggeringLoad() = runTest {
        val api = FakeHomeApi()
        val repo = HomeRepository(api)
        val vm = HomeViewModelForTest(repo)

        vm.setRange("2025-01-01", "2025-01-07")

        val ui = vm.ui.value
        assertThat(ui.range.startDate).isEqualTo("2025-01-01")
        assertThat(ui.range.endDate).isEqualTo("2025-01-07")
        assertThat(ui.loadingStats).isFalse()
    }

}

