package com.moodtunes.ai

import io.github.cdimascio.dotenv.dotenv
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

@Serializable
data class GeminiTextPart(val text: String)

@Serializable
data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiTextPart>
)

@Serializable
data class GeminiRequest(val contents: List<GeminiContent>)

@Serializable
data class GeminiCandidate(val content: GeminiContent)

@Serializable
data class GeminiResponse(val candidates: List<GeminiCandidate>)

object GeminiClient {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val http = HttpClient.newHttpClient()

    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1"
    private const val MODEL = "gemini-2.5-flash"

    private val apiKey: String by lazy {
        com.moodtunes.ENV["GEMINI_API_KEY"]
            ?: error("GEMINI_API_KEY is missing")
    }

    suspend fun generate(prompt: String): String {
        val body = json.encodeToString(
            GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiTextPart(prompt))
                    )
                )
            )
        )

        val request = HttpRequest.newBuilder()
            .uri(URI.create("$BASE_URL/models/$MODEL:generateContent?key=$apiKey"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()

        val response = http.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() !in 200..299) {
            throw RuntimeException("Gemini error: ${response.statusCode()} - ${response.body()}")
        }

        val parsed = json.decodeFromString<GeminiResponse>(response.body())

        return parsed.candidates.first()
            .content
            .parts
            .first()
            .text
    }
}
