package com.example.healthbuddy.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class Gender {
    @Json(name = "male") MALE,
    @Json(name = "female") FEMALE
}

enum class ActivityLevel {
    @Json(name = "sedentary") SEDENTARY,
    @Json(name = "light") LIGHT,
    @Json(name = "moderate") MODERATE,
    @Json(name = "active") ACTIVE,
    @Json(name = "very_active") VERY_ACTIVE
}

enum class GoalType {
    @Json(name = "fat_loss") FAT_LOSS,
    @Json(name = "muscle_gain") MUSCLE_GAIN,
    @Json(name = "maintain") MAINTAIN,
    @Json(name = "endurance") ENDURANCE
}
@JsonClass(generateAdapter = true)
data class LoginRequest(
    val email: String,
    val password: String
)
@JsonClass(generateAdapter = true)
data class SignUpRequest(
    val name: String? = null,
    val email: String,
    val username: String,
    val password: String,

    @Json(name = "birthDay") val birthDay: String? = null,
    val age: Int? = null,
    val gender: Gender? = null,
    val activityLevel: ActivityLevel? = null,
    val goal: GoalType? = null,
    @Json(name = "isPremium") val isPremium: Boolean = false,
    val role: Role? = null
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val id: Long,
    val name: String? = null,
    val email: String,
    val username: String,
    val password: String? = null,

    @Json(name = "birthDay") val birthDay: String? = null,
    val age: Int? = null,
    val gender: Gender? = null,
    val joinDate: String? = null,
    val lastLogin: String? = null,
    val activityLevel: ActivityLevel? = null,
    val goal: GoalType? = null,

    val token: String? = null,

    val roles: List<Role> = emptyList(),

    val premium: Boolean = false
)

@JsonClass(generateAdapter = true)
data class Role(
    val id: Int,
    val role: String,
    val permissions: List<Permission> = emptyList()
)

@JsonClass(generateAdapter = true)
data class Permission(
    val id: Long,
    val permission: String
)

fun fromGenderString(str: String?): Gender? =
    Gender.entries.find { it.name.equals(str, ignoreCase = true) }

fun fromActivityString(str: String?): ActivityLevel? =
    ActivityLevel.entries.find { it.name.equals(str, ignoreCase = true) }

fun fromGoalString(str: String?): GoalType? =
    GoalType.entries.find { it.name.equals(str, ignoreCase = true) }
