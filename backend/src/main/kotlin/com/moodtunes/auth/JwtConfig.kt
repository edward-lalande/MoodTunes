package com.moodtunes.auth

import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.moodtunes.database.RefreshTokens
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object JwtConfig {

//    private const val secret = "YOUR_SECRET_KEY_256BITS" // should be in a .env I guess
//    private const val issuer = "ktor-moodtunes"
//    private const val validityInMs = 15 * 60 * 1000 // 15 minutes
//
//    private val algorithm = Algorithm.HMAC256(secret)
//
//    fun generateToken(userId: Int): String =
//        JWT.create()
//            .withIssuer(issuer)
//            .withClaim("id", userId)
//            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
//            .sign(algorithm)
//
//    fun configure(config: JWTAuthenticationProvider.Config) {
//        config.verifier(
//            JWT
//                .require(algorithm)
//                .withIssuer(issuer)
//                .build()
//        )
//        config.validate { credential ->
//            if (credential.payload.getClaim("id").asInt() != null)
//                JWTPrincipal(credential.payload)
//            else null
//        }
//    }

    fun createRefreshToken(userId: Int): String {
        val token = UUID.randomUUID().toString()
        val expiration = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000 // 30 jours

        transaction {
            RefreshTokens.insert {
                it[id] = token
                it[RefreshTokens.userId] = userId
                it[expiresAt] = expiration
            }
        }

        return token
    }

}
