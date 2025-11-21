package com.moodtunes.ai

import com.moodtunes.clients.SpotifyClient
import com.moodtunes.models.MusicDetailed
import java.util.UUID

object MusicGenerator {

    suspend fun generateMultipleTracks(mood: String, count: Int): List<Pair<String, String>> {
        val prompt = """
            Génère $count titres de musique connus pour une playlist mood "$mood".
            Format STRICT :
            TITLE - ARTIST
            TITLE - ARTIST
            ...
            (exactement $count lignes)
        """.trimIndent()

        val raw = GeminiClient.generate(prompt)

        return raw.lines()
            .mapNotNull { line ->
                val parts = line.split("-", limit = 2)
                if (parts.size == 2) {
                    val title = parts[0].trim()
                    val artist = parts[1].trim()
                    if (title.isNotBlank() && artist.isNotBlank()) title to artist else null
                } else null
            }
            .take(count)
    }

    suspend fun enrichWithSpotify(mood: String, title: String, artist: String): MusicDetailed {
        val info = SpotifyClient.fetchTrackInfo(title, artist)

        val (spotifyUrl, coverUrl, releaseDate) = info
            ?: Triple("https://open.spotify.com", "https://placehold.co/300x300", "unknown")

        return MusicDetailed(
            id = UUID.randomUUID().toString(),
            title = title,
            artist = artist,
            albumCoverUrl = coverUrl,
            mood = mood,
            spotifyUrl = spotifyUrl,
            releaseDate = releaseDate
        )
    }
}
