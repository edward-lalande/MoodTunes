package com.moodtunes.auth

import com.moodtunes.database.RefreshTokens
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object JwtConfig {

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
