package com.example.healthbuddy.data.repo

import com.example.healthbuddy.data.api.UserInfoApi
import com.example.healthbuddy.data.model.HealthInfo
import com.example.healthbuddy.data.model.HealthInfoRequest
import com.example.healthbuddy.data.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserInfoRepository
@Inject
constructor(
    private val api: UserInfoApi
){
    suspend fun getUser(): Result<User> =
        runCatching {
            api.getUser()
        }

    suspend fun getLatest(): Result<HealthInfo?> =
        runCatching {
            api.getLatest()
        }.recoverCatching { e ->
            if (e is retrofit2.HttpException && e.code() == 404) {
                // backend trả lỗi not found
                null
            } else {
                throw e
            }
        }

    suspend fun createHealthInfo(body: HealthInfoRequest): Result<HealthInfo> =
        runCatching {
            api.createHealthInfo(body)
        }
}
