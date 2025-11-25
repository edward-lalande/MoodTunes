package com.moodtunes.models

import kotlinx.serialization.Serializable

@Serializable
data class SpotifyAuthUrlResponse(
    val authUrl: String,
    val state: String
)

@Serializable
data class SpotifyConnectedResponse(
    val status: String = "spotify_connected"
)

@Serializable
data class SpotifyStatusResponse(
    val connected: Boolean
)
