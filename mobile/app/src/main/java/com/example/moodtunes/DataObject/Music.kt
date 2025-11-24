package com.example.moodtunes.DataObject

data class MusicDetailed(
    val id: String,
    val title: String,
    val artist: String,
    val mood: String,
    val albumCoverUrl: String,
    val spotifyUrl: String
)

data class OldMoodRequest(
    val mood: String,
)

data class NormalMoodRequest(
    val mood: String,
    val kind: String
)
