package com.moodtunes.routes

import com.moodtunes.models.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.github.smiley4.ktorswaggerui.dsl.*
import io.ktor.http.*
import io.ktor.server.request.receive
import java.time.LocalDate
import java.util.UUID

fun Route.userRoutes() {

    route("/user") {

        post("/create", {
            tags = listOf("User")
            summary = "Create a new user"
            description = "Create a new user and return its token"
            request { body<CreateUserRequest>() }
            response {
                HttpStatusCode.Created to {
                    description = "User successfully created"
                    body<CreateUserResponse>()
                }
                HttpStatusCode.BadRequest to { description = "Invalid request body" }
            }
        }) {
            // user creation

            call.respond(
                CreateUserResponse(
                    token = "mockToken"
                )
            )
        }

        get("", {
            tags = listOf("User")
            summary = "Get user data"
            description = "Returns basic information about the current user."
            request { body<GetUserRequest>() }
            response {
                HttpStatusCode.OK to {
                    description = "User data retrieved successfully"
                    body<GetUserResponse>()
                }
                HttpStatusCode.NotFound to { description = "User not found" }
            }
        }) {
            call.respond(
                GetUserResponse(
                    username = "mockUser",
                    email = "mock@user.com",
                    createdAt = "2025-01-01"
                )
            )
        }


        patch("/username", {
            tags = listOf("User")
            summary = "Modify a specific username"
            description = "Modify a specific user's username given its token."
            request { body<ModifyUserUsernameRequest>() }
            response {
                HttpStatusCode.OK to { description = "User username modified successfully" }
                HttpStatusCode.NotFound to { description = "User not found" }
            }
        }) {
            // ça patch fort
        }

        patch("/email", {
            tags = listOf("User")
            summary = "Modify a specific email"
            description = "Modify a specific user's email given its token."
            request { body<ModifyUserEmailRequest>() }
            response {
                HttpStatusCode.OK to { description = "User email modified successfully" }
                HttpStatusCode.NotFound to { description = "User not found" }
            }
        }) {
            // ça patch fort
        }

        patch("/password", {
            tags = listOf("User")
            summary = "Modify a specific password"
            description = "Modify a specific user's password given its token."
            request { body<ModifyUserPasswordRequest>() }
            response {
                HttpStatusCode.OK to { description = "User password modified successfully" }
                HttpStatusCode.NotFound to { description = "User not found" }
            }
        }) {
            // ça patch fort
        }

        delete("", {
            tags = listOf("User")
            summary = "Delete a specific user"
            description = "Delete a specific user given it's token."
            request { body<DeleteUserRequest>() }
            response {
                HttpStatusCode.OK to { description = "User deleted successfully" }
                HttpStatusCode.NotFound to { description = "User not found" }
            }
        }) {
            // ça delete fort
        }
    }
}
