package com.moodtunes.routes

import com.moodtunes.auth.JwtConfig
import com.moodtunes.database.RefreshTokens
import com.moodtunes.database.Users
import com.moodtunes.models.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.github.smiley4.ktorswaggerui.dsl.*
import io.ktor.http.*
import io.ktor.server.request.receive
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

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
            val req = call.receive<CreateUserRequest>()

            val user = transaction {
                val tempUser = Users.selectAll().where { Users.email eq req.email }

                if (tempUser.empty()) {
                    return@transaction null
                }
                return@transaction tempUser
            }

            if (user != null) {
                return@post call.respond(HttpStatusCode.BadRequest, "User already exists")
            }

            val hash = req.password.hashCode().toString()

            transaction {
                Users.insert {
                    it[username] = req.username
                    it[email] = req.email
                    it[passwordHash] = hash
                    it[createdAt] = LocalDateTime.now().toString()
                }
            }

            val userToken = transaction {
                Users.selectAll().where { Users.email eq req.email }.singleOrNull()
            }

            if (userToken == null) {
                return@post call.respond(HttpStatusCode.NotFound, "User couldn't be created")
            }

            val refreshToken = JwtConfig.createRefreshToken(userToken[Users.id])

            call.respond(
                CreateUserResponse(
                    token = refreshToken,
                )
            )
        }

        get("login", {
            tags = listOf("User")
            summary = "Login to a user"
            description = "Login to a specific user using an username and a password and retrieve a token"
            request { body<LoginUserRequest>() }
            response {
                HttpStatusCode.OK to {
                    description = "Successfully logged in"
                    body<LoginUserResponse>()
                }
                HttpStatusCode.NotFound to { description = "User not found" }
            }
        }) {
            val req = call.receive<LoginUserRequest>()

            val passwordHash = req.password.hashCode().toString()

            val user = transaction {
                Users.selectAll().where {
                    (Users.username eq req.username) and
                    (Users.passwordHash eq passwordHash)
                }.singleOrNull()
            }

            if (user == null) {
                return@get call.respond(HttpStatusCode.NotFound, "Invalid credentials")
            }

            val token = transaction {
                RefreshTokens.selectAll().where { RefreshTokens.userId eq user[Users.id] }.singleOrNull()
            }

            if (token == null) {
                return@get call.respond(HttpStatusCode.NotFound, "Internal error")
            }

            call.respond(
                LoginUserResponse(
                    token = token[RefreshTokens.id]
                )
            )
        }

        post("logout", {
            tags = listOf("User")
            summary = "Logout of a user"
            description = "Logout of a specific user using a token"
            request { body<LogoutUserRequest>() }
            response {
                HttpStatusCode.OK to {
                    description = "Successfully logged in"
                }
                HttpStatusCode.NotFound to { description = "User not found" }
            }
        }) {
            // logout
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
            val req = call.receive<GetUserRequest>()

            val userToken = transaction {
                RefreshTokens.selectAll().where { RefreshTokens.id eq req.token }.singleOrNull()
            }

            if (userToken == null) {
                return@get call.respond(HttpStatusCode.NotFound, "Invalid token")
            }

            val user = transaction {
                Users.selectAll().where { Users.id eq userToken[RefreshTokens.userId] }.singleOrNull()
            }

            if (user == null) {
                return@get call.respond(HttpStatusCode.NotFound, "Internal error")
            }

            call.respond(
                GetUserResponse(
                    username = user[Users.username],
                    email = user[Users.email],
                    createdAt = user[Users.createdAt]
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
