package com.moodtunes.routes

import com.moodtunes.clients.SpotifyOAuthService
import com.moodtunes.clients.SpotifyProfileService
import com.moodtunes.database.MusicHistory
import com.moodtunes.database.SpotifyTokens
import com.moodtunes.models.*
import com.moodtunes.services.MusicService
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
import java.time.format.DateTimeFormatter

fun Route.musicRoutes() {

    route("/music") {

        authenticate("auth-bearer") {
            post("/mood", {
                tags = listOf("Music")
                securitySchemeName = "bearerAuth"
                summary = "Generate a playlist based on a given mood"
                description =
                    "Uses Gemini to generate tracks matching the mood and fetches Spotify metadata. " +
                            "If the user connected Spotify, the generation is influenced by the user's liked songs (top genres). " +
                            "Automatically stores the results into the user's history."
                request { body<MoodRequest>() }
                response {
                    HttpStatusCode.OK to {
                        description = "A playlist matching the provided mood"
                        body<MusicResponse>()
                    }
                    HttpStatusCode.Unauthorized to {
                        description = "Missing or invalid token"
                        body<ErrorResponse>()
                    }
                }
            }) {
                val userId = call.principal<UserIdPrincipal>()!!.name.toInt()
                val req = call.receive<MoodRequest>()
                val mood = req.mood

                val count = when (req.kind) {
                    "playlist" -> 6
                    "track" -> 1
                    else -> 1
                }

                var genreHint: String? = null

                val tokenRow = transaction {
                    SpotifyTokens.selectAll().where { SpotifyTokens.userId eq userId }.singleOrNull()
                }

                if (tokenRow != null) {
                    var accessToken = tokenRow[SpotifyTokens.accessToken]
                    val refreshToken = tokenRow[SpotifyTokens.refreshToken]
                    val expiresAt = tokenRow[SpotifyTokens.expiresAt]

                    if (System.currentTimeMillis() >= expiresAt && refreshToken != null) {
                        val newToken = SpotifyOAuthService.refresh(refreshToken)
                        accessToken = newToken.accessToken

                        transaction {
                            SpotifyTokens.update({ SpotifyTokens.userId eq userId }) {
                                it[SpotifyTokens.accessToken] = newToken.accessToken
                                it[SpotifyTokens.expiresAt] = newToken.expiresAt
                            }
                        }
                    }

                    val topGenres = SpotifyProfileService.getTopGenresFromLikes(
                        accessToken = accessToken,
                        sampleTracks = 50
                    )

                    if (topGenres.isNotEmpty()) {
                        genreHint = topGenres.joinToString(
                            prefix = "Top genres likés: ",
                            separator = ", "
                        ) { (genre, pct) ->
                            "${genre} ${(pct * 100).toInt()}%"
                        }
                    }
                    println("genreHint: $genreHint")
                }

                val playlist = MusicService.generatePlaylist(
                    mood = mood,
                    count = count,
                    genreHint = genreHint
                )

                transaction {
                    playlist.forEach { track ->
                        MusicHistory.insert {
                            it[MusicHistory.id] = track.id
                            it[MusicHistory.userId] = userId
                            it[MusicHistory.title] = track.title
                            it[MusicHistory.artist] = track.artist
                            it[MusicHistory.mood] = track.mood
                            it[MusicHistory.date] = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                            it[MusicHistory.albumCoverUrl] = track.albumCoverUrl
                            it[MusicHistory.spotifyTrackUrl] = track.spotifyUrl
                        }
                    }
                }

                call.respond(
                    MusicResponse(
                        playlist = playlist
                    )
                )
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
                        body<ErrorResponse>()
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
                                date = it[MusicHistory.date],
                                albumCoverUrl = it[MusicHistory.albumCoverUrl],
                                spotifyTrackUrl = it[MusicHistory.spotifyTrackUrl]
                            )
                        }
                }

                call.respond(MusicHistoryResponse("user-$userId", entries))
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
                    HttpStatusCode.OK to {
                        description = "Song successfully deleted"
                        body<EditHistoryResponse>()
                    }
                    HttpStatusCode.NotFound to {
                        description = "Song ID not found"
                        body<ErrorResponse>()
                    }
                    HttpStatusCode.Unauthorized to {
                        description = "Missing or invalid token (handled automatically by Bearer auth)"
                        body<ErrorResponse>()
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
                    call.respond(
                        HttpStatusCode.OK,
                        EditHistoryResponse("deleted", req.id)
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse("ID not found")
                    )
                }
            }
        }

    }
}
