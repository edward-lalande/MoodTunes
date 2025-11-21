package com.moodtunes.models

import kotlinx.serialization.Serializable

@Serializable
data class MusicDetailed(
    val id: String,
    val title: String,
    val artist: String,
    val mood: String,
    val albumCoverUrl: String,
    val spotifyUrl: String,
    val releaseDate: String
)

@Serializable
data class MoodRequest(
    val mood: String,
    val kind: String // playlist/track/album
)

@Serializable
data class MusicResponse(
    val playlist: List<MusicDetailed>? = null,
)

@Serializable
data class MusicHistoryEntry(
    val id: String,
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
    val title: String,
    val artist: String,
    val mood: String,
    val spotifyUrl: String
)

@Serializable
data class DeleteHistoryRequest(
    val id: String
)
