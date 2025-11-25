package com.moodtunes

import com.moodtunes.database.DatabaseFactory
import com.moodtunes.database.RefreshTokens
import com.moodtunes.routes.musicRoutes
import com.moodtunes.routes.spotifyOAuthRoutes
import com.moodtunes.routes.userRoutes
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.http.*
import io.github.smiley4.ktorswaggerui.*
import io.github.smiley4.ktorswaggerui.data.AuthScheme
import io.github.smiley4.ktorswaggerui.data.AuthType
import io.ktor.server.auth.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import io.github.cdimascio.dotenv.dotenv
import com.moodtunes.auth.customBearer
import com.moodtunes.models.ErrorResponse

val ENV = dotenv {
    directory = "./"
    filename = ".env"
    ignoreIfMalformed = true
    ignoreIfMissing = false
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        DatabaseFactory.init()
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) { json() }

    install(SwaggerUI) {
        swagger {
            swaggerUrl = "swagger"
        }

        info {
            title = "MoodTunes API"
            version = "0.1.0"
            description = "Mock backend for MoodTunes"
        }

        securityScheme("bearerAuth") {
            type = AuthType.HTTP
            scheme = AuthScheme.BEARER
            bearerFormat = "JWT"
        }
    }
    install(Authentication) {
        customBearer("auth-bearer") {

            validate { token ->
                if (token == null) {
                    return@validate null
                }

                val refreshTokenRow = transaction {
                    RefreshTokens
                        .selectAll()
                        .where { RefreshTokens.id eq token }
                        .singleOrNull()
                } ?: return@validate null

                val userId = refreshTokenRow[RefreshTokens.userId]

                UserIdPrincipal(userId.toString())
            }

            challenge { call ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse("Missing or invalid token")
                )
            }
        }
    }

    routing {
        get("/") { call.respondText("MoodTunes API is running", ContentType.Text.Plain) }

        musicRoutes()
        spotifyOAuthRoutes()
        userRoutes()
    }
}
