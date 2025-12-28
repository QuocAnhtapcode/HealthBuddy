package com.example.healthbuddy.home

import com.example.healthbuddy.data.api.HomeApi
import com.example.healthbuddy.data.model.*

class FakeHomeApi : HomeApi {

    var caloriesResult: Result<List<CaloriesStat>> =
        Result.success(emptyList())

    var runSessionsResult: Result<List<RunSession>> =
        Result.success(emptyList())

    var createRunResult: Result<RunSession> =
        Result.failure(IllegalStateException("Not set"))

    var healthInfoResult: Result<HealthInfoPage> =
        Result.success(
            HealthInfoPage(
                content = emptyList(),
                page = Page(10, 0, 0, 0)
            )
        )

    override suspend fun getCaloriesStat(
        startDate: String,
        endDate: String
    ): List<CaloriesStat> {
        return caloriesResult.getOrElse { throw it }
    }

    override suspend fun getRunSessions(): List<RunSession> {
        return runSessionsResult.getOrElse { throw it }
    }

    override suspend fun createRunSession(
        createRunSessionRequest: CreateRunSessionRequest
    ): RunSession {
        return createRunResult.getOrElse { throw it }
    }

    override suspend fun getAllHealthInfo(
        page: Int,
        size: Int
    ): HealthInfoPage {
        return healthInfoResult.getOrElse { throw it }
    }
}
