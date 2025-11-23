package com.moodtunes.routes

import com.moodtunes.auth.JwtConfig
import com.moodtunes.database.RefreshTokens
import com.moodtunes.database.Users
import com.moodtunes.models.*
import com.moodtunes.utils.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.github.smiley4.ktorswaggerui.dsl.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
                HttpStatusCode.BadRequest to {
                    description = "Invalid request body"
                    body<ErrorResponse>()
                }
            }
        }) {
            val req = call.receive<CreateUserRequest>()

            transaction {
                Users.selectAll().where { Users.email eq req.email }.singleOrNull()
            } ?.let {
                return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("User already exists"))
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
            } ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("User couldn't be created"))

            val refreshToken = JwtConfig.createRefreshToken(userToken[Users.id])

            call.respond(
                HttpStatusCode.Created,
                CreateUserResponse(
                    token = refreshToken,
                )
            )
        }

        post("login", {
            tags = listOf("User")
            summary = "Login to a user"
            description = "Login to a specific user using an username and a password and retrieve a token"
            request { body<LoginUserRequest>() }
            response {
                HttpStatusCode.OK to {
                    description = "Successfully logged in"
                    body<LoginUserResponse>()
                }
                HttpStatusCode.NotFound to {
                    description = "User not found"
                    body<ErrorResponse>()
                }
            }
        }) {
            val req = call.receive<LoginUserRequest>()

            val passwordHash = req.password.hashCode().toString()

            val user = transaction {
                Users.selectAll().where {
                    (Users.username eq req.username) and
                    (Users.passwordHash eq passwordHash)
                }.singleOrNull()
            } ?: return@post call.respond(HttpStatusCode.NotFound, ErrorResponse("Invalid credentials"))

            val token = transaction {
                RefreshTokens.selectAll().where { RefreshTokens.userId eq user[Users.id] }.singleOrNull()
            } ?: return@post call.respond(HttpStatusCode.NotFound, ErrorResponse("Internal error"))

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
                    description = "Successfully logged out"
                    body<SuccessResponse>()
                }
                HttpStatusCode.NotFound to {
                    description = "User not found"
                    body<ErrorResponse>()
                }
            }
        }) {
            // logout
        }

        authenticate("auth-bearer") {
            get("", {
                tags = listOf("User")
                securitySchemeName = "bearerAuth"
                summary = "Get user data"
                description = "Returns basic information about the current user."
                response {
                    HttpStatusCode.OK to {
                        description = "User data retrieved successfully"
                        body<GetUserResponse>()
                    }
                    HttpStatusCode.Unauthorized to {
                        description = "Missing or invalid token (handled automatically by Bearer auth)"
                        body<ErrorResponse>()
                    }
                    HttpStatusCode.NotFound to {
                        description = "User not found. The token belongs to a deleted user."
                        body<ErrorResponse>()
                    }
                }
            }) {
                val userId = call.principal<UserIdPrincipal>()!!.name.toInt()

                val user = transaction {
                    Users.selectAll().where { Users.id eq userId }.singleOrNull()
                } ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("User not found"))

                call.respond(
                    GetUserResponse(
                        username = user[Users.username],
                        email = user[Users.email],
                        createdAt = user[Users.createdAt]
                    )
                )
            }
        }

        authenticate("auth-bearer") {
            patch("", {
                tags = listOf("User")
                securitySchemeName = "bearerAuth"
                summary = "Modify user information"
                description = "Modify the authenticated user's username, email, or password. Only non-null fields are updated."
                request { body<ModifyUserRequest>() }
                response {
                    HttpStatusCode.OK to {
                        description = "User updated successfully"
                        body<SuccessResponse>()
                    }
                    HttpStatusCode.Unauthorized to {
                        description = "Missing or invalid token (handled automatically by Bearer auth)"
                        body<ErrorResponse>()
                    }
                    HttpStatusCode.NotFound to {
                        description = "User not found. The token belongs to a deleted user."
                        body<ErrorResponse>()
                    }
                }
            }) {
                val userId = call.principal<UserIdPrincipal>()!!.name.toInt()

                val body = call.receive<ModifyUserRequest>()

                val ok = updateUser(userId) {
                    body.username?.let { newUsername -> it[username] = newUsername }
                    body.email?.let { newEmail -> it[email] = newEmail }
                    body.password?.let { newPassword ->
                        val hashed = newPassword.hashCode().toString()
                        it[passwordHash] = hashed
                    }
                }

                if (!ok) {
                    return@patch call.respond(HttpStatusCode.NotFound, ErrorResponse("User not found"))
                }

                call.respond(HttpStatusCode.OK, SuccessResponse("User updated successfully"))
            }
        }

        authenticate("auth-bearer") {
            delete("", {
                tags = listOf("User")
                securitySchemeName = "bearerAuth"
                summary = "Delete the authenticated user"
                description = "Deletes the user associated with the provided token."

                response {
                    HttpStatusCode.OK to {
                        description = "User deleted successfully"
                        body<SuccessResponse>()
                    }
                    HttpStatusCode.Unauthorized to {
                        description = "Missing or invalid token (handled automatically by Bearer auth)"
                        body<ErrorResponse>()
                    }
                    HttpStatusCode.NotFound to {
                        description = "User not found. The token belongs to a deleted user."
                        body<ErrorResponse>()
                    }
                }
            }) {
                val userId = call.principal<UserIdPrincipal>()!!.name.toInt()

                transaction { RefreshTokens.deleteWhere { RefreshTokens.userId eq userId } }
                transaction { Users.deleteWhere { Users.id eq userId } }

                call.respond(HttpStatusCode.OK, SuccessResponse("User deleted"))
            }
        }
    }
}
