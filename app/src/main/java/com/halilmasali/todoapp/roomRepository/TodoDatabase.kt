package com.halilmasali.todoapp.roomRepository

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TodoData::class], version = 1)
abstract class TodoDatabase: RoomDatabase() {
    abstract fun dataDao(): DataDao

    companion object {
        const val DATABASE_NAME = "todo_database"
    }
}