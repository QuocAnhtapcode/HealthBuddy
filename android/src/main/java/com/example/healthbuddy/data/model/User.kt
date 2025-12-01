package com.example.healthbuddy.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val email: String? = null,
    val username: String? = null,
    val password: String
)

@JsonClass(generateAdapter = true)
data class SignUpRequest(
    val name: String? = null,
    var email: String,
    var username: String,
    var password: String,

    var birthDay: String? = null,
    var age: Int? = null,
    var gender: String? = null,
    val activityLevel: String? = null,
    val goal: Goal? = null,
    val isPremium: Boolean = false,
    val role: Role? = null
)

@JsonClass(generateAdapter = true)
data class User(
    val id: Long,
    val name: String?,
    val email: String,
    val username: String,
    val password: String,
    val birthDay: String?,
    val age: Int?,
    val gender: String?,
    val joinDate: String?,
    val lastLogin: String?,
    val activityLevel: String?,
    val goal: String?,
    val token: String?,
    val roles: List<Role> = emptyList(),
    val userPlans: List<UserPlan> = emptyList()
)
