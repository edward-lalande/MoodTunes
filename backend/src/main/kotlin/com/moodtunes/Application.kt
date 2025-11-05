package com.moodtunes

import com.moodtunes.routes.authRoutes
import com.moodtunes.routes.musicRoutes
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

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
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
    }

    routing {
        get("/") { call.respondText("MoodTunes API is running", ContentType.Text.Plain) }

        authRoutes()
        musicRoutes()
        userRoutes()
    }
}
