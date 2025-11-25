package com.moodtunes.routes

import com.moodtunes.clients.SpotifyOAuthService
import com.moodtunes.clients.SpotifyProfileService
import com.moodtunes.database.SpotifyTokens
import com.moodtunes.models.*
import com.moodtunes.utils.UserHelpers
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

        get("/login", {
            tags = listOf("Spotify")
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

        get("/callback", {
            tags = listOf("Spotify")
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
                    body<LoginUserResponse>()
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
            val code = call.parameters["code"] ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing code"))
            val state = call.parameters["state"] ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing state"))

            val tokenSpotify = SpotifyOAuthService.exchangeCode(code, state)
            val profile = SpotifyProfileService.getSpotifyUserProfile(tokenSpotify.accessToken)

            val email = profile.email ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Spotify account has no email.")
            )
            val usernameFallback = profile.displayName ?: email.substringBefore("@")
            val userId = UserHelpers.getOrCreateUserByEmail(email, usernameFallback)
            val refreshToken = UserHelpers.getOrCreateRefreshToken(userId)

            UserHelpers.saveSpotifyTokens(
                userId = userId,
                accessToken = tokenSpotify.accessToken,
                refreshToken = tokenSpotify.refreshToken,
                expiresAt = tokenSpotify.expiresAt
            )

            println("[PKCE-CALLBACK] state received = $state")
            println("[PKCE-CALLBACK] verifier found = ${SpotifyOAuthService.getVerifierForState(state)}")

            call.respond(LoginUserResponse(token = refreshToken))
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
