package com.example.moodtunes.DataObject

data class MusicDetailed(
    val id: String,
    val title: String,
    val artist: String,
    val mood: String,
    val albumCoverUrl: String,
    val spotifyUrl: String,
    val releaseDate: String
)

data class MusicDetailedResponse(
    val playlist: List<MusicDetailed>
)

data class NormalMoodRequest(
    val mood: String,
    val kind: String
)
