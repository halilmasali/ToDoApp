package com.halilmasali.todoapp.roomRepository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DataDao {

    @Query("SELECT * FROM todo_data")
    fun getAllTodoData(): Array<TodoData>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertTodoData(todoData: TodoData)

    @Update
    fun updateData(data: TodoData)

    @Delete
    fun deleteData(data: TodoData)
}