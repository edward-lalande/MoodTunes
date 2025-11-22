package com.moodtunes.services

import com.moodtunes.ai.MusicGenerator
import com.moodtunes.models.MusicDetailed

object MusicService {

    suspend fun generatePlaylist(mood: String, count: Int): List<MusicDetailed> {
        val pairs = MusicGenerator.generateMultipleTracks(mood, count)

        return pairs.map { (title, artist) ->
            MusicGenerator.enrichWithSpotify(mood, title, artist)
        }
    }

    suspend fun generateSingleTrack(mood: String): MusicDetailed {
        return generatePlaylist(mood, 1).first()
    }
}
