package com.moodtunes.routes

import com.moodtunes.models.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.github.smiley4.ktorswaggerui.dsl.*
import io.ktor.http.*

fun Route.userRoutes() {
    route("/user") {
        get("/data", {
            tags = listOf("User")
            response { HttpStatusCode.OK to { body<UserResponse>() } }
        }) {
            call.respond(
                UserResponse(
                    username = "mockUser",
                    email = "mock@user.com",
                    createdAt = "2025-01-01"
                )
            )
        }
    }
}
