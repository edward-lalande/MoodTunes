package com.moodtunes.clients

import io.github.cdimascio.dotenv.dotenv
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class SpotifySearchTrackItem(val id: String, val external_urls: Map<String, String>, val album: SpotifyAlbum)

@Serializable
data class SpotifyAlbum(
    val images: List<SpotifyImage> = emptyList(),

    @SerialName("release_date")
    val releaseDate: String? = null,

    @SerialName("release_date_precision")
    val releaseDatePrecision: String? = null,

    val name: String? = null
)

@Serializable
data class SpotifyImage(val url: String, val width: Int? = null, val height: Int? = null)

@Serializable
data class SpotifySearchTrackResponse(val tracks: SpotifySearchTracks)

@Serializable
data class SpotifySearchTracks(val items: List<SpotifySearchTrackItem>)

object SpotifyClient {
    private val json = Json { ignoreUnknownKeys = true }
    private val http = HttpClient.newHttpClient()

    private val clientId = com.moodtunes.ENV["SPOTIFY_CLIENT_ID"]
        ?: error("Missing SPOTIFY_CLIENT_ID")

    private val clientSecret = com.moodtunes.ENV["SPOTIFY_CLIENT_SECRET"]
        ?: error("Missing SPOTIFY_CLIENT_SECRET")

    private var accessToken: String? = null
    private var tokenExpiry: Long = 0

    private fun ensureToken() {
        val now = System.currentTimeMillis()
        if (accessToken == null || now >= tokenExpiry) {
            val creds = java.util.Base64.getEncoder()
                .encodeToString("$clientId:$clientSecret".toByteArray())
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Authorization", "Basic $creds")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build()
            val response = http.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() !in 200..299) {
                error("Spotify token request failed: ${response.statusCode()} ${response.body()}")
            }
            val obj = json.parseToJsonElement(response.body()).jsonObject
            accessToken = obj["access_token"]!!.jsonPrimitive.content
            val expiresIn = obj["expires_in"]!!.jsonPrimitive.int
            tokenExpiry = now + (expiresIn - 60) * 1000L
        }
    }

    suspend fun fetchTrackInfo(title: String, artist: String): Triple<String, String, String>? {
        ensureToken()

        val query = java.net.URLEncoder.encode("track:$title artist:$artist", "UTF-8")
        val requestSearch = HttpRequest.newBuilder()
            .uri(URI.create("https://api.spotify.com/v1/search?q=$query&type=track&limit=1"))
            .header("Authorization", "Bearer $accessToken")
            .build()

        val responseSearch = http.send(requestSearch, HttpResponse.BodyHandlers.ofString())

        if (responseSearch.statusCode() !in 200..299) {
            println("Spotify search error: ${responseSearch.body()}")
            return null
        }

        val searchResp = json.decodeFromString<SpotifySearchTrackResponse>(responseSearch.body())
        println("response: ${responseSearch.body()}")
        val item = searchResp.tracks.items.firstOrNull() ?: return null

        val spotifyUrl = item.external_urls["spotify"] ?: return null
        val coverImageUrl = item.album.images.firstOrNull()?.url ?: return null

        val releaseDate = item.album.releaseDate ?: "unknown"

        return Triple(spotifyUrl, coverImageUrl, releaseDate)
    }

}
