package com.moodtunes.models

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val username: String,
    val email: String,
    val createdAt: String
)
