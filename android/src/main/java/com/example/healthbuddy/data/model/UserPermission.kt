package com.example.healthbuddy.data.model

import com.squareup.moshi.JsonClass

//Own by Role
@JsonClass(generateAdapter = true)
data class UserPermission(
    val id: Long,
    val permission: String
)
