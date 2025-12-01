package com.example.healthbuddy.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Role(
    val id: Int,
    val role: String,
    val permissions: List<UserPermission> = emptyList()
)
