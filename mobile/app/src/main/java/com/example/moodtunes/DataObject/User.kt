package com.example.moodtunes.DataObject

data class UserData(
    val email: String,
    val password: String
)

data class TokenResponse(
    val token: String,
    val username: String
)
