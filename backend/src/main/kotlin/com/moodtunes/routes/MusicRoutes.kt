package com.moodtunes.routes

import com.moodtunes.models.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.github.smiley4.ktorswaggerui.dsl.*
import io.ktor.http.*

fun Route.musicRoutes() {

    route("/music") {

        post("/mood", {
            tags = listOf("Music")
            request { body<MoodRequest>() }
            response { HttpStatusCode.OK to { body<MusicResponse>() } }
        }) {
            val mood = call.receive<MoodRequest>().mood
            call.respond(
                MusicResponse(
                    title = "Mock Song",
                    artist = "Mock Artist",
                    albumCoverUrl = "https://placehold.co/300x300",
                    mood = mood,
                    spotifyUrl = "https://spotify.com/placehold"
                )
            )
        }

        get("/history", {
            tags = listOf("Music")
            response { HttpStatusCode.OK to { body<MusicHistoryResponse>() } }
        }) {
            val history = listOf(
                MusicHistoryEntry("Song1", "Artist1", "happy", "2025-11-04"),
                MusicHistoryEntry("Song2", "Artist2", "sad", "2025-11-03"),
            )
            call.respond(MusicHistoryResponse("mockUser", history))
        }

        post("/history", {
            tags = listOf("Music")
            request { body<AddHistoryRequest>() }
            response { HttpStatusCode.Created to { description = "History added" } }
        }) {
            val req = call.receive<AddHistoryRequest>()
            call.respond(HttpStatusCode.Created, mapOf("status" to "added", "title" to req.title))
        }

        delete("/history", {
            tags = listOf("Music")
            request { body<DeleteHistoryRequest>() }
            response {
                HttpStatusCode.OK to { description = "Title deleted successfully" }
                HttpStatusCode.NotFound to { description = "Title not found" }
            }
        }) {
            val req = call.receive<DeleteHistoryRequest>()
            val deleted = listOf("Song1", "Song2").contains(req.title)

            if (deleted) {
                call.respond(HttpStatusCode.OK, mapOf("status" to "deleted", "title" to req.title))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Title not found"))
            }
        }
    }
}
