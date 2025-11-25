package com.moodtunes.utils

import com.moodtunes.database.RefreshTokens
import com.moodtunes.database.Users
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import com.moodtunes.auth.JwtConfig
import com.moodtunes.database.SpotifyTokens
import com.moodtunes.clients.SpotifyOAuthService
import com.moodtunes.clients.SpotifyProfileService
import com.moodtunes.models.ErrorResponse
import com.moodtunes.models.SpotifyConnectedResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

object UserHelpers {

    fun getOrCreateUserByEmail(email: String, usernameFallback: String): Int {
        val existing = transaction {
            Users.selectAll().where { Users.email eq email }.singleOrNull()
        }

        return if (existing != null) {
            existing[Users.id]
        } else {
            transaction {
                Users.insert {
                    it[username] = usernameFallback
                    it[Users.email] = email
                    it[passwordHash] = ""
                    it[createdAt] = LocalDateTime.now().toString()
                } get Users.id
            }
        }
    }

    fun getOrCreateRefreshToken(userId: Int): String {
        val newToken = UUID.randomUUID().toString()
        val expiresAt = System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30

        return transaction {
            val existing = RefreshTokens
                .selectAll().where { RefreshTokens.userId eq userId }
                .singleOrNull()

            if (existing != null) {
                existing[RefreshTokens.id]
            } else {
                RefreshTokens.insert {
                    it[id] = newToken
                    it[RefreshTokens.userId] = userId
                    it[RefreshTokens.expiresAt] = expiresAt
                }
                newToken
            }
        }
    }

    fun saveSpotifyTokens(
        userId: Int,
        accessToken: String,
        refreshToken: String?,
        expiresAt: Long
    ) {
        val existing = transaction {
            SpotifyTokens.selectAll().where { SpotifyTokens.userId eq userId }.singleOrNull()
        }

        transaction {
            if (existing == null) {
                SpotifyTokens.insert {
                    it[SpotifyTokens.userId] = userId
                    it[SpotifyTokens.accessToken] = accessToken
                    it[SpotifyTokens.refreshToken] = refreshToken
                    it[SpotifyTokens.expiresAt] = expiresAt
                }
            } else {
                SpotifyTokens.update({ SpotifyTokens.userId eq userId }) {
                    it[SpotifyTokens.accessToken] = accessToken
                    it[SpotifyTokens.refreshToken] = refreshToken
                    it[SpotifyTokens.expiresAt] = expiresAt
                }
            }
        }
    }

    suspend fun fetchSpotifyProfile(accessToken: String): SpotifyProfileService.SpotifyUserProfile {
        return SpotifyProfileService.getSpotifyUserProfile(accessToken)
    }

    fun updateUser(userId: Int, block: Users.(UpdateStatement) -> Unit): Boolean {
        val updated = transaction {
            Users.update({ Users.id eq userId }) {
                Users.block(it)
            }
        }
        return updated > 0
    }
}
