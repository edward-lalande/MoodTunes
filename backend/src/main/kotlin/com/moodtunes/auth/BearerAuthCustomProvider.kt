package com.moodtunes.auth

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.http.*

class BearerAuthCustomProvider(config: Config) : AuthenticationProvider(config) {

    private val authenticate = config.authenticateFunction
    private val unauthorized = config.unauthorizedFunction

    class Config(name: String?) : AuthenticationProvider.Config(name) {
        lateinit var authenticateFunction: suspend (String?) -> Principal?
        lateinit var unauthorizedFunction: suspend (ApplicationCall) -> Unit

        fun validate(block: suspend (String?) -> Principal?) {
            authenticateFunction = block
        }

        fun challenge(block: suspend (ApplicationCall) -> Unit) {
            unauthorizedFunction = block
        }
    }

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val call = context.call
        val authHeader = call.request.headers["Authorization"]

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            unauthorized(call)
            context.challenge.complete()
            return
        }

        val token = authHeader.removePrefix("Bearer ").trim()
        val principal = authenticate(token)

        if (principal == null) {
            unauthorized(call)
            context.challenge.complete()
        } else {
            context.principal(principal)
        }
    }
}

fun AuthenticationConfig.customBearer(
    name: String? = null,
    configure: BearerAuthCustomProvider.Config.() -> Unit
) {
    val provider = BearerAuthCustomProvider(BearerAuthCustomProvider.Config(name).apply(configure))
    register(provider)
}
