package com.halilmasali.todoapp.roomRepository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_data")
data class TodoData(
    @PrimaryKey(autoGenerate = true) val id:Int? = null,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "reminder_time") val reminderTime: String,
    @ColumnInfo(name = "is_done") val isDone: Boolean
)
