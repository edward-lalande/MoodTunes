package com.moodtunes.routes

import com.moodtunes.database.MusicHistory
import com.moodtunes.database.Users
import com.moodtunes.models.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.github.smiley4.ktorswaggerui.dsl.*
import io.ktor.http.*
import io.ktor.server.auth.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import java.time.LocalDate

fun Route.musicRoutes() {

    route("/music") {

        authenticate("auth-bearer") {
            post("/mood", {
                tags = listOf("Music")
                summary = "Generate a playlist based on a given mood"
                securitySchemeName = "bearerAuth"
                description =
                    "Takes a mood and returns either a playlist URL or a detailed playlist based on the 'kind' field."
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
        }


        authenticate("auth-bearer") {
            get("/history", {
                tags = listOf("Music")
                securitySchemeName = "bearerAuth"
                summary = "Retrieve the user's listening history"
                description = "Returns all previously added songs for the current user."
                response {
                    HttpStatusCode.OK to {
                        description = "A list of music history entries"
                        body<MusicHistoryResponse>()
                    }
                    HttpStatusCode.Unauthorized to {
                        description = "Missing or invalid token (handled automatically by Bearer auth)"
                    }
                }
            }) {
                val userId = call.principal<UserIdPrincipal>()!!.name.toInt()

                val entries = transaction {
                    MusicHistory.selectAll().where { MusicHistory.userId eq userId }
                        .map {
                            MusicHistoryEntry(
                                id = it[MusicHistory.id],
                                title = it[MusicHistory.title],
                                artist = it[MusicHistory.artist],
                                mood = it[MusicHistory.mood],
                                date = it[MusicHistory.date]
                            )
                        }
                }

                call.respond(MusicHistoryResponse("user-$userId", entries))
            }
        }

        authenticate("auth-bearer") {
            post("/history", {
                tags = listOf("Music")
                summary = "Add a song to the user's history"
                securitySchemeName = "bearerAuth"
                description = "Stores a new music entry with its mood and Spotify link."
                request { body<AddHistoryRequest>() }
                response {
                    HttpStatusCode.Created to { description = "Song successfully added to history" }
                    HttpStatusCode.Unauthorized to {
                        description = "Missing or invalid token (handled automatically by Bearer auth)"
                    }
                }
            }) {
                val userId = call.principal<UserIdPrincipal>()!!.name.toInt()
                val req = call.receive<AddHistoryRequest>()
                val newId = UUID.randomUUID().toString()

                transaction {
                    MusicHistory.insert {
                        it[id] = newId
                        it[MusicHistory.userId] = userId
                        it[title] = req.title
                        it[artist] = req.artist
                        it[mood] = req.mood
                        it[date] = LocalDate.now().toString()
                    }
                }

                call.respond(
                    HttpStatusCode.Created,
                    mapOf("status" to "added", "id" to newId)
                )
            }
        }

        authenticate("auth-bearer") {
            delete("/history", {
                tags = listOf("Music")
                securitySchemeName = "bearerAuth"
                summary = "Delete a song from the user's history"
                description = "Deletes a specific song from the history using its unique ID."
                request { body<DeleteHistoryRequest>() }
                response {
                    HttpStatusCode.OK to { description = "Song successfully deleted" }
                    HttpStatusCode.NotFound to { description = "Song ID not found" }
                    HttpStatusCode.Unauthorized to {
                        description = "Missing or invalid token (handled automatically by Bearer auth)"
                    }
                }
            }) {
                val userId = call.principal<UserIdPrincipal>()!!.name.toInt()
                val req = call.receive<DeleteHistoryRequest>()

                val removed = transaction {
                    MusicHistory.deleteWhere {
                        (MusicHistory.id eq req.id) and (MusicHistory.userId eq userId)
                    }
                }

                if (removed > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("status" to "deleted", "id" to req.id))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "ID not found"))
                }
            }
        }

    }
}
