package com.example.healthbuddy.workout

import app.cash.turbine.test
import com.example.healthbuddy.MainDispatcherRule
import com.example.healthbuddy.data.repo.WorkOutRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadTodaySession_success_setsSession() = runTest {
        val api = FakeWorkOutApi()
        val repo = WorkOutRepository(api)
        val vm = WorkoutViewModelForTest(repo)

        vm.loadTodaySession()
        advanceUntilIdle()

        val ui = vm.ui.value
        assertThat(ui.loadingSession).isFalse()
        assertThat(ui.todaySession).isNotNull()
        assertThat(ui.isRestDay).isFalse()
    }

    @Test
    fun loadTodaySession_fail_setsRestDay() = runTest {
        val api = FakeWorkOutApi().apply {
            todaySessionResult = Result.failure(RuntimeException("No session"))
        }
        val repo = WorkOutRepository(api)
        val vm = WorkoutViewModelForTest(repo)

        vm.loadTodaySession()
        advanceUntilIdle()

        val ui = vm.ui.value
        assertThat(ui.todaySession).isNull()
        assertThat(ui.isRestDay).isTrue()
    }

    @Test
    fun loadExercisesForToday_success_updatesExerciseList() = runTest {
        val api = FakeWorkOutApi()
        val repo = WorkOutRepository(api)
        val vm = WorkoutViewModelForTest(repo)

        vm.loadTodaySession()
        advanceUntilIdle()

        vm.loadExercisesForToday(userLevel = "beginner", groupId = 1)
        advanceUntilIdle()

        val ui = vm.ui.value
        assertThat(ui.loadingExercises).isFalse()
        assertThat(ui.exercises).hasSize(2)
    }

    @Test
    fun loadExercisesForToday_fail_setsError() = runTest {
        val api = FakeWorkOutApi().apply {
            exercisesResult = Result.failure(RuntimeException("Load fail"))
        }
        val repo = WorkOutRepository(api)
        val vm = WorkoutViewModelForTest(repo)

        vm.loadTodaySession()
        advanceUntilIdle()

        vm.loadExercisesForToday("beginner", 1)
        advanceUntilIdle()

        val ui = vm.ui.value
        assertThat(ui.error).isEqualTo("Load fail")
    }

    @Test
    fun selectExercise_setsSelectedExercise() = runTest {
        val vm = WorkoutViewModelForTest(
            WorkOutRepository(FakeWorkOutApi())
        )

        val exercise = fakeExercises().first()
        vm.selectExercise(exercise)

        assertThat(vm.ui.value.selectedExercise).isEqualTo(exercise)
    }

    @Test
    fun uiFlow_emitsLoadingThenSuccess_forTodaySession() = runTest {
        val api = FakeWorkOutApi()
        val vm = WorkoutViewModelForTest(WorkOutRepository(api))

        vm.ui.test {
            val initial = awaitItem()
            assertThat(initial.loadingSession).isFalse()

            vm.loadTodaySession()

            val loading = awaitItem()
            assertThat(loading.loadingSession).isTrue()

            val done = awaitItem()
            assertThat(done.loadingSession).isFalse()
            assertThat(done.todaySession).isNotNull()

            cancelAndIgnoreRemainingEvents()
        }
    }
}
