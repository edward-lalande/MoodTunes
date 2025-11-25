package com.moodtunes.routes

import com.moodtunes.clients.SpotifyOAuthService
import com.moodtunes.database.SpotifyTokens
import com.moodtunes.models.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.github.smiley4.ktorswaggerui.dsl.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import io.github.smiley4.ktorswaggerui.dsl.*

fun Route.spotifyOAuthRoutes() {

    route("/spotify") {

        authenticate("auth-bearer") {
            get("/login", {
                tags = listOf("Spotify")
                securitySchemeName = "bearerAuth"
                summary = "Start Spotify OAuth (PKCE)"
                description =
                    "Generates an authorization URL that the client app must open to start the Spotify OAuth flow."
                response {
                    HttpStatusCode.OK to {
                        description = "Authorization URL generated successfully"
                        body<SpotifyAuthUrlResponse>()
                    }
                    HttpStatusCode.Unauthorized to {
                        description = "Missing or invalid token"
                        body<ErrorResponse>()
                    }
                }
            }) {
                val userId = call.principal<UserIdPrincipal>()!!.name.toInt()

                val pkce = SpotifyOAuthService.createPkceBundle()
                val url = SpotifyOAuthService.buildAuthorizeUrl(pkce.state, pkce.codeChallenge)

                println("[PKCE-LOGIN] state = ${pkce.state}")
                println("[PKCE-LOGIN] verifier = ${pkce.codeVerifier}")

                call.respond(
                    SpotifyAuthUrlResponse(
                        authUrl = url,
                        state = pkce.state
                    )
                )
            }
        }

        authenticate("auth-bearer") {
            get("/callback", {
                tags = listOf("Spotify")
                securitySchemeName = "bearerAuth"
                summary = "Spotify OAuth callback"
                description =
                    "Receives the 'code' and 'state' parameters from Spotify and exchanges them for access+refresh tokens. Automatically stores them for the authenticated user."
                request {
                    queryParameter<String>("code") {
                        description = "Authorization code returned by Spotify"
                        required = true
                    }
                    queryParameter<String>("state") {
                        description = "State returned by Spotify"
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "Spotify account successfully linked"
                        body<SpotifyConnectedResponse>()
                    }
                    HttpStatusCode.BadRequest to {
                        description = "Invalid or missing parameters"
                        body<ErrorResponse>()
                    }
                    HttpStatusCode.Unauthorized to {
                        description = "Missing or invalid token"
                        body<ErrorResponse>()
                    }
                }
            }) {
                val userId = call.principal<UserIdPrincipal>()!!.name.toInt()

                val code = call.parameters["code"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing code"))
                val state = call.parameters["state"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing state"))


                val token = SpotifyOAuthService.exchangeCode(code, state)

                transaction {
                    val existing = SpotifyTokens
                        .select { SpotifyTokens.userId eq userId }
                        .singleOrNull()

                    if (existing == null) {
                        SpotifyTokens.insert {
                            it[SpotifyTokens.userId] = userId
                            it[accessToken] = token.accessToken
                            it[refreshToken] = token.refreshToken
                            it[expiresAt] = token.expiresAt
                        }
                    } else {
                        SpotifyTokens.update({ SpotifyTokens.userId eq userId }) {
                            it[accessToken] = token.accessToken
                            it[refreshToken] = token.refreshToken
                            it[expiresAt] = token.expiresAt
                        }
                    }
                }

                println("[PKCE-CALLBACK] state received = $state")
                println("[PKCE-CALLBACK] verifier found = ${SpotifyOAuthService.getVerifierForState(state)}")

                call.respond(SpotifyConnectedResponse())
            }
        }

        authenticate("auth-bearer") {
            get("/status", {
                tags = listOf("Spotify")
                securitySchemeName = "bearerAuth"
                summary = "Get Spotify connection status"
                description = "Returns whether the user has linked their Spotify account."
                response {
                    HttpStatusCode.OK to {
                        description = "Whether Spotify is linked or not"
                        body<SpotifyStatusResponse>()
                    }
                    HttpStatusCode.Unauthorized to {
                        description = "Missing or invalid token"
                        body<ErrorResponse>()
                    }
                }
            }) {
                val userId = call.principal<UserIdPrincipal>()!!.name.toInt()

                val connected = transaction {
                    SpotifyTokens
                        .selectAll().where { SpotifyTokens.userId eq userId }
                        .any()
                }

                call.respond(SpotifyStatusResponse(connected))
            }
        }
    }
}
