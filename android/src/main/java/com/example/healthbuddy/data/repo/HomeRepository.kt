package com.example.healthbuddy.data.repo

import com.example.healthbuddy.data.api.HomeApi
import com.example.healthbuddy.data.model.CaloriesStat
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val api: HomeApi
) {
    suspend fun getCaloriesStat(startDate: String, endDate: String): Result<List<CaloriesStat>> =
        runCatching { api.getCaloriesStat(startDate, endDate) }
}
