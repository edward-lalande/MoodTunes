package com.example.moodtunes.DataObject

import android.R

data class UserData(
    val username: String,
    val password: String
)

data class TokenResponse(
    val token: String
)

data class SignupRequest(
    val email: String,
    val password: String,
    val username: String
)

data class ErrorResponse(
    val error: String
)

data class GetUserResponse(
    val username: String,
    val email: String,
    val createdAt: String
)
