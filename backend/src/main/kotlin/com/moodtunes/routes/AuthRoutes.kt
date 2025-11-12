package com.moodtunes.routes

import com.moodtunes.models.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.github.smiley4.ktorswaggerui.dsl.*
import kotlin.random.Random

fun Route.authRoutes() {

    route("/auth") {

        post("/login", {
            tags = listOf("Auth")
            summary = "User login"
            description = "Authenticates a user and returns an access token."
            request { body<LoginRequest>() }
            response {
                HttpStatusCode.OK to {
                    description = "User authenticated"
                    body<TokenResponse>()
                }
                HttpStatusCode.Unauthorized to { description = "Invalid credentials" }
            }
        }) {
            val body = call.receive<LoginRequest>()
            call.respond(TokenResponse("token_${Random.nextInt(1000)}", body.username))
        }

        post("/signup", {
            tags = listOf("Auth")
            summary = "User signup"
            description = "Creates a new user account and returns a token."
            request { body<SignupRequest>() }
            response {
                HttpStatusCode.Created to {
                    description = "User created"
                    body<TokenResponse>()
                }
                HttpStatusCode.Conflict to { description = "User already exists" }
            }
        }) {
            val body = call.receive<SignupRequest>()
            call.respond(
                HttpStatusCode.Created,
                TokenResponse("token_${Random.nextInt(1000)}", body.username)
            )
        }

        post("/logout", {
            tags = listOf("Auth")
            summary = "User logout"
            description = "Logs out the current user."
            response {
                HttpStatusCode.OK to { description = "User logged out" }
            }
        }) {
            call.respond(mapOf("status" to "logged_out"))
        }
    }
}
