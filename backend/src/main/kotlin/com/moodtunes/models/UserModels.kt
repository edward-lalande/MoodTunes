package com.moodtunes.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val username: String,
    val email: String,
    val password: String,
)

@Serializable
data class GetUserRequest(
    val token: String,
)

@Serializable
data class ModifyUserUsernameRequest(
    val token: String,
    val newUsername: String,
)

@Serializable
data class ModifyUserEmailRequest(
    val token: String,
    val newEmail: String,
)

@Serializable
data class ModifyUserPasswordRequest(
    val token: String,
    val newPassword: String,
)

@Serializable
data class DeleteUserRequest(
    val token: String,
)

@Serializable
data class CreateUserResponse(
    val token: String,
)

@Serializable
data class GetUserResponse(
    val username: String,
    val email: String,
    val createdAt: String
)
