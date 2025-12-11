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

data class MusicPlaylistResponse(
    val playlist: List<MusicDetailed>
)

data class NormalMoodRequest(
    val mood: String,
    val kind: String
)

data class DeleteMusicHistoryResp(
    val id: String,
    val status: String
)

data class DeleteHistoryRequest(
    val id: String
)

data class MusicHistory(
    val id: String,
    val title: String,
    val artist: String,
    val spotifyTrackUrl: String,
    val albumCoverUrl: String,
    val mood: String,
    val date: String
)

data class MusicHistoryList(
    val user: String,
    val history: List<MusicHistory>
)
