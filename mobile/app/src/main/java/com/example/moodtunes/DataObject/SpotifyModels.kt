package com.example.moodtunes.DataObject

data class SpotifyAuthResponse(
    val authUrl: String,
    val state: String
)

data class SpotifyCallbackResponse(
    val token: String
)

data class SpotifyStatusResponse(
    val connected: Boolean
)