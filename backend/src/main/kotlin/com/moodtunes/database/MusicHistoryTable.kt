package com.moodtunes.database

import org.jetbrains.exposed.sql.Table

object MusicHistory : Table() {
    val id = varchar("id", 100) // UUID
    val userId = integer("user_id") references Users.id
    val title = varchar("title", 255)
    val artist = varchar("artist", 255)
    val mood = varchar("mood", 100)
    val date = varchar("date", 30)

    override val primaryKey = PrimaryKey(id)
}
