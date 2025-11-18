package com.moodtunes.utils

import com.moodtunes.database.RefreshTokens
import com.moodtunes.database.Users
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction

fun updateUser(userId: Int, block: Users.(UpdateStatement) -> Unit): Boolean {
    val updated = transaction {
        Users.update({ Users.id eq userId }) {
            Users.block(it)
        }
    }
    return updated > 0
}
