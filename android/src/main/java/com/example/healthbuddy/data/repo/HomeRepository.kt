package com.example.healthbuddy.data.repo

import com.example.healthbuddy.data.api.HomeApi
import com.example.healthbuddy.data.model.CaloriesStat
import com.example.healthbuddy.data.model.CreateRunSessionRequest
import com.example.healthbuddy.data.model.RunSession
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val api: HomeApi
) {
    suspend fun getCaloriesStat(startDate: String, endDate: String): Result<List<CaloriesStat>> =
        runCatching { api.getCaloriesStat(startDate, endDate) }

    suspend fun getRunSessions(): Result<List<RunSession>> =
        runCatching { api.getRunSessions() }

    suspend fun createRunSession(createRunSessionRequest: CreateRunSessionRequest): Result<RunSession> =
        runCatching { api.createRunSession(createRunSessionRequest) }
}
