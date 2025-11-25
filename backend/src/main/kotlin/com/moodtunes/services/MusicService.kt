package com.moodtunes.services

import com.moodtunes.ai.MusicGenerator
import com.moodtunes.models.MusicDetailed

object MusicService {
    suspend fun generatePlaylist(mood: String, count: Int, genreHint: String? = null): List<MusicDetailed> {
        val prompt = buildPrompt(mood, count, genreHint)

        val pairs = MusicGenerator.generateMultipleTracks(prompt, count)

        return pairs.map { (title, artist) ->
            MusicGenerator.enrichWithSpotify(mood, title, artist)
        }
    }

    suspend fun generateSingleTrack(mood: String, genreHint: String? = null): MusicDetailed {
        return generatePlaylist(mood, 1, genreHint).first()
    }

    private fun buildPrompt(mood: String, count: Int, genreHint: String?): String {
        val userHintBlock = genreHint?.let {
            """
            Contexte utilisateur Spotify :
            $it

            Adapte impérativement la sélection pour coller aux goûts musicaux de l'utilisateur.
            """.trimIndent()
        } ?: ""

        return """
            Génère $count titres de musique connus pour une playlist avec le mood "$mood".
            $userHintBlock

            Format STRICT :
            TITLE - ARTIST
            TITLE - ARTIST
            ...
            (exactement $count lignes)
        """.trimIndent()
    }
}
