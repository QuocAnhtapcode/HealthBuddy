package com.example.healthbuddy.workout

import com.example.healthbuddy.data.api.WorkOutApi
import com.example.healthbuddy.data.model.*

class FakeWorkOutApi : WorkOutApi {

    var todaySessionResult: Result<TodayWorkoutSession> =
        Result.success(fakeTodayWorkoutSession())

    var exercisesResult: Result<ExercisePageResponse> =
        Result.success(fakeExercisePage())

    var addExerciseResult: Result<Unit> =
        Result.success(Unit)

    var deleteExerciseResult: Result<Unit> =
        Result.success(Unit)

    var updateExerciseResult: Result<Unit> =
        Result.success(Unit)

    override suspend fun getTodaySession(): TodayWorkoutSession {
        return todaySessionResult.getOrElse { throw it }
    }

    override suspend fun getExercisesByFilter(
        page: Int,
        size: Int,
        category: String,
        activityLevel: String,
        muscleGroup: Long
    ): ExercisePageResponse {
        return exercisesResult.getOrElse { throw it }
    }

    override suspend fun addSessionExercise(body: SessionExerciseCreateRequest) {
        return addExerciseResult.getOrElse { throw it }
    }

    override suspend fun deleteSessionExercise(id: Long) {
        return deleteExerciseResult.getOrElse { throw it }
    }

    override suspend fun updateSessionExercise(
        id: Long,
        body: SessionExerciseCreateRequest
    ) {
        return updateExerciseResult.getOrElse { throw it }
    }

    override suspend fun getGoals(id: Int): GoalWithPlans {
        error("Not used in Workout tests")
    }

    override suspend fun addUserPlan(body: AddUserPlanRequest) {
        error("Not used in Workout tests")
    }
}
