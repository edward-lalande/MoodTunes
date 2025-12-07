package com.moodtunes.clients

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.text.Charsets.UTF_8

object SpotifyOAuthService {

    private const val AUTH_URL = "https://accounts.spotify.com/authorize"
    private const val TOKEN_URL = "https://accounts.spotify.com/api/token"

    private val clientId: String by lazy {
        com.moodtunes.ENV["SPOTIFY_CLIENT_ID"] ?: error("SPOTIFY_CLIENT_ID missing")
    }
    private val redirectUri: String by lazy {
        com.moodtunes.ENV["SPOTIFY_REDIRECT_URI"] ?: error("SPOTIFY_REDIRECT_URI missing")
    }

    private val verifierStore = ConcurrentHashMap<String, String>()

    fun getVerifierForState(state: String): String? {
        return verifierStore[state]
    }

    private val http = HttpClient(io.ktor.client.engine.cio.CIO) {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json()
        }
    }

    data class PkceBundle(
        val state: String,
        val codeVerifier: String,
        val codeChallenge: String
    )

    fun createPkceBundle(): PkceBundle {
        val state = UUID.randomUUID().toString()
        val codeVerifier = base64UrlRandom(64)
        val codeChallenge = sha256Base64Url(codeVerifier)

        verifierStore[state] = codeVerifier
        return PkceBundle(state, codeVerifier, codeChallenge)
    }

    fun buildAuthorizeUrl(state: String, codeChallenge: String): String {
        val scopes = listOf(
            "user-read-email",
            "user-read-private",
            "user-library-read"
        ).joinToString(" ")

        return URLBuilder(AUTH_URL).apply {
            parameters.append("client_id", clientId)
            parameters.append("response_type", "code")
            parameters.append("redirect_uri", redirectUri)
            parameters.append("state", state)
            parameters.append("scope", scopes)
            parameters.append("code_challenge_method", "S256")
            parameters.append("code_challenge", codeChallenge)
        }.buildString()
    }

    suspend fun exchangeCode(code: String, state: String): SpotifyToken {
        val verifier = verifierStore.remove(state)
            ?: error("Invalid/expired state")

        val resp: SpotifyTokenResponse = http.submitForm(
            url = TOKEN_URL,
            formParameters = Parameters.build {
                append("client_id", clientId)
                append("grant_type", "authorization_code")
                append("code", code)
                append("redirect_uri", redirectUri)
                append("code_verifier", verifier)
            }
        ).body()

        val now = System.currentTimeMillis()
        return SpotifyToken(
            accessToken = resp.accessToken,
            refreshToken = resp.refreshToken,
            expiresAt = now + (resp.expiresIn * 1000L)
        )
    }

    suspend fun refresh(refreshToken: String): SpotifyToken {
        val resp: SpotifyTokenResponse = http.submitForm(
            url = TOKEN_URL,
            formParameters = Parameters.build {
                append("client_id", clientId)
                append("grant_type", "refresh_token")
                append("refresh_token", refreshToken)
            }
        ).body()

        val now = System.currentTimeMillis()
        return SpotifyToken(
            accessToken = resp.accessToken,
            refreshToken = refreshToken,
            expiresAt = now + (resp.expiresIn * 1000L)
        )
    }

    @Serializable
    private data class SpotifyTokenResponse(
        @SerialName("access_token") val accessToken: String,
        @SerialName("token_type") val tokenType: String,
        @SerialName("scope") val scope: String? = null,
        @SerialName("expires_in") val expiresIn: Int,
        @SerialName("refresh_token") val refreshToken: String? = null
    )

    data class SpotifyToken(
        val accessToken: String,
        val refreshToken: String?,
        val expiresAt: Long
    )

    private fun sha256Base64Url(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(UTF_8))
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    private fun base64UrlRandom(size: Int): String {
        val bytes = ByteArray(size)
        Random().nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}
