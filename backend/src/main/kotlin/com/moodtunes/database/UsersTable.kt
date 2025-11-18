package com.moodtunes.database

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val createdAt = varchar("created_at", 255)

    override val primaryKey = PrimaryKey(id)
}

object RefreshTokens : Table() {
    val id = varchar("id", 100).uniqueIndex() // token lui-même
    val userId = integer("user_id") references Users.id
    val expiresAt = long("expires_at")

    override val primaryKey = PrimaryKey(id)
}