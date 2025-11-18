package com.moodtunes.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val username: String,
    val email: String,
    val password: String,
)

@Serializable
data class LoginUserRequest(
    val username: String,
    val password: String,
)

@Serializable
data class LogoutUserRequest(
    val token: String,
)

@Serializable
data class ModifyUserUsernameRequest(
    val newUsername: String,
)

@Serializable
data class ModifyUserEmailRequest(
    val newEmail: String,
)

@Serializable
data class ModifyUserPasswordRequest(
    val newPassword: String,
)

@Serializable
data class CreateUserResponse(
    val token: String,
)

@Serializable
data class LoginUserResponse(
    val token: String,
)

@Serializable
data class GetUserResponse(
    val username: String,
    val email: String,
    val createdAt: String
)
