package com.moodtunes.models

import kotlinx.serialization.Serializable

@Serializable
data class MoodRequest(val mood: String)

@Serializable
data class MusicResponse(
    val title: String,
    val artist: String,
    val albumCoverUrl: String,
    val mood: String,
    val spotifyUrl: String
)

@Serializable
data class MusicHistoryEntry(
    val title: String,
    val artist: String,
    val mood: String,
    val date: String
)

@Serializable
data class MusicHistoryResponse(
    val user: String,
    val history: List<MusicHistoryEntry>
)

@Serializable
data class AddHistoryRequest(
    val token: String,
    val title: String,
    val artist: String,
    val mood: String,
    val spotifyUrl: String
)

@Serializable
data class DeleteHistoryRequest(
    val token: String,
    val title: String,
    val artist: String
)
