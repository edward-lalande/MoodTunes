package com.moodtunes.clients

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object SpotifyProfileService {

    private const val API = "https://api.spotify.com/v1"
    private val http = HttpClient(io.ktor.client.engine.cio.CIO) {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) { json(Json {
            ignoreUnknownKeys = true
        }) }
    }

    suspend fun getTopGenresFromLikes(
        accessToken: String,
        sampleTracks: Int = 50
    ): List<Pair<String, Double>> {

        val liked = fetchSavedTracks(accessToken, limit = sampleTracks)

        val artistIds = liked
            .mapNotNull { it.track.artists.firstOrNull()?.id }
            .distinct()

        if (artistIds.isEmpty()) return emptyList()

        val genres = mutableListOf<String>()
        artistIds.chunked(50).forEach { chunk ->
            val artistsResp: SeveralArtistsResponse = http.get("$API/artists") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                parameter("ids", chunk.joinToString(","))
            }.body()

            artistsResp.artists.forEach { artist ->
                genres.addAll(artist.genres)
            }
        }

        if (genres.isEmpty()) return emptyList()

        val counts = genres.groupingBy { it }.eachCount()
        val total = counts.values.sum().toDouble()

        return counts.entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key to (it.value / total) } // 0..1
    }

    private suspend fun fetchSavedTracks(accessToken: String, limit: Int): List<SavedItem> {
        val resp: SavedTracksResponse = http.get("$API/me/tracks") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            parameter("limit", minOf(limit, 50))
        }.body()
        return resp.items
    }

    @Serializable
    data class SavedTracksResponse(
        val href: String? = null,
        val items: List<SavedItem>,
        val limit: Int? = null,
        val next: String? = null,
        val offset: Int? = null,
        val previous: String? = null,
        val total: Int? = null
    )
    @Serializable data class SavedItem(val track: Track)

    @Serializable data class Track(val id: String, val artists: List<ArtistRef>)
    @Serializable data class ArtistRef(val id: String, val name: String)

    @Serializable data class SeveralArtistsResponse(val artists: List<ArtistFull>)
    @Serializable data class ArtistFull(val id: String, val genres: List<String>)

    @Serializable
    data class SpotifyUserProfile(
        val email: String? = null,
        @SerialName("display_name") val displayName: String? = null
    )

    suspend fun getSpotifyUserProfile(accessToken: String): SpotifyUserProfile {
        return http.get("https://api.spotify.com/v1/me") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }.body()
    }
}
