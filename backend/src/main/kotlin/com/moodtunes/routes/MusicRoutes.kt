package com.moodtunes.routes

import com.moodtunes.models.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.github.smiley4.ktorswaggerui.dsl.*
import io.ktor.http.*
import java.util.UUID
import java.time.LocalDate

val musicHistoryStorage = mutableListOf<MusicHistoryEntry>()

fun Route.musicRoutes() {

    route("/music") {

        post("/mood", {
            tags = listOf("Music")
            summary = "Generate a playlist based on a given mood"
            description = "Takes a mood and returns either a playlist URL or a detailed playlist based on the 'kind' field."
            request { body<MoodRequest>() }
            response {
                HttpStatusCode.OK to {
                    description = "A playlist matching the provided mood"
                    body<MusicResponse>()
                }
            }
        }) {
            val req = call.receive<MoodRequest>()

            val playlist = listOf(
                MusicDetailed(
                    id = UUID.randomUUID().toString(),
                    title = "Mock Song",
                    artist = "Mock Artist",
                    albumCoverUrl = "https://placehold.co/300x300",
                    mood = req.mood,
                    spotifyUrl = "https://spotify.com/placehold",
                    releaseDate = "2004-08-06"
                ),
                MusicDetailed(
                    id = UUID.randomUUID().toString(),
                    title = "Chill Vibes",
                    artist = "Lofi Beats",
                    albumCoverUrl = "https://placehold.co/300x300?text=Chill",
                    mood = req.mood,
                    spotifyUrl = "https://spotify.com/chill",
                    releaseDate = "2004-08-06"
                )
            )

            if (req.kind == "playlist") {
                call.respond(MusicResponse(playlistUrl = "https://open.spotify.com/playlist/mock-${req.mood}"))
            } else {
                call.respond(MusicResponse(playlist = playlist))
            }
        }


        get("/history", {
            tags = listOf("Music")
            summary = "Retrieve the user's listening history"
            description = "Returns all previously added songs for the current user."
            response {
                HttpStatusCode.OK to {
                    description = "A list of music history entries"
                    body<MusicHistoryResponse>()
                }
            }
        }) {
            val user = "mockUser"
            call.respond(MusicHistoryResponse(user, musicHistoryStorage))
        }

        post("/history", {
            tags = listOf("Music")
            summary = "Add a song to the user's history"
            description = "Stores a new music entry with its mood and Spotify link."
            request { body<AddHistoryRequest>() }
            response {
                HttpStatusCode.Created to {
                    description = "Song successfully added to history"
                    body<SuccessResponse>()
                }
                HttpStatusCode.BadRequest to {
                    description = "Invalid request body"
                    body<ErrorResponse>()
                }
            }
        }) {
            val req = call.receive<AddHistoryRequest>()

            val newEntry = MusicHistoryEntry(
                id = UUID.randomUUID().toString(),
                title = req.title,
                artist = req.artist,
                mood = req.mood,
                date = LocalDate.now().toString()
            )

            musicHistoryStorage.add(newEntry)

            call.respond(
                HttpStatusCode.Created,
                SuccessResponse(newEntry.id)
            )
        }

        delete("/history", {
            tags = listOf("Music")
            summary = "Delete a song from the user's history"
            description = "Deletes a specific song from the history using its unique ID."
            request { body<DeleteHistoryRequest>() }
            response {
                HttpStatusCode.OK to {
                    description = "Song successfully deleted"
                    body<SuccessResponse>()
                }
                HttpStatusCode.NotFound to {
                    description = "Song ID not found"
                    body<ErrorResponse>()
                }
            }
        }) {
            val req = call.receive<DeleteHistoryRequest>()
            val removed = musicHistoryStorage.removeIf { it.id == req.id }

            if (removed) {
                call.respond(HttpStatusCode.OK, SuccessResponse(req.id))
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("ID not found"))
            }
        }
    }
}
