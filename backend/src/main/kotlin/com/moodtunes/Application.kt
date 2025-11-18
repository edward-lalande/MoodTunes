package com.moodtunes

import com.moodtunes.database.DatabaseFactory
import com.moodtunes.routes.musicRoutes
import com.moodtunes.routes.userRoutes
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.github.smiley4.ktorswaggerui.dsl.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.http.*
import io.github.smiley4.ktorswaggerui.*
import io.github.smiley4.ktorswaggerui.data.AuthScheme
import io.github.smiley4.ktorswaggerui.data.AuthType

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

    routing {
        get("/") { call.respondText("MoodTunes API is running", ContentType.Text.Plain) }

        musicRoutes()
        userRoutes()
    }
}
