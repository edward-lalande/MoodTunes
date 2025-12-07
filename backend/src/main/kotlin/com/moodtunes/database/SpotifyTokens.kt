package com.moodtunes.database

import org.jetbrains.exposed.sql.Table

object SpotifyTokens : Table("spotify_tokens") {
    val userId = integer("user_id").uniqueIndex()
    val accessToken = varchar("access_token", 512)
    val refreshToken = varchar("refresh_token", 512).nullable()
    val expiresAt = long("expires_at") // epoch millis
}
