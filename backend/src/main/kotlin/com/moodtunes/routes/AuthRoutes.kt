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
            request { body<LoginRequest>() }
            response { HttpStatusCode.OK to { body<TokenResponse>() } }
        }) {
            val body = call.receive<LoginRequest>()
            call.respond(TokenResponse("token_${Random.nextInt(1000)}", body.username))
        }

        post("/signup", {
            tags = listOf("Auth")
            request { body<SignupRequest>() }
            response { HttpStatusCode.Created to { body<TokenResponse>() } }
        }) {
            val body = call.receive<SignupRequest>()
            call.respond(HttpStatusCode.Created, TokenResponse("token_${Random.nextInt(1000)}", body.username))
        }

        post("/logout", {
            tags = listOf("Auth")
            response { HttpStatusCode.OK to { description = "User logged out" } }
        }) {
            call.respond(mapOf("status" to "logged_out"))
        }
    }
}
