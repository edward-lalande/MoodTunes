package com.moodtunes.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class SignupRequest(val username: String, val email: String, val password: String)

@Serializable
data class TokenResponse(val token: String, val username: String)
