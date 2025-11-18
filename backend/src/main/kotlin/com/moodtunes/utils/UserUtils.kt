package com.moodtunes.utils

import com.moodtunes.database.RefreshTokens
import com.moodtunes.database.Users
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction

fun getUserIdFromToken(call: ApplicationCall): Int? {
    val token = call.request.headers["Authorization"]
        ?.removePrefix("Bearer ")
        ?.trim()
        ?: return null

    val refreshTokenRow = transaction {
        RefreshTokens
            .selectAll().where { RefreshTokens.id eq token }
            .singleOrNull()
    } ?: return null

    return refreshTokenRow[RefreshTokens.userId]
}

fun updateUser(userId: Int, block: Users.(UpdateStatement) -> Unit): Boolean {
    val updated = transaction {
        Users.update({ Users.id eq userId }) {
            Users.block(it)
        }
    }
    return updated > 0
}
