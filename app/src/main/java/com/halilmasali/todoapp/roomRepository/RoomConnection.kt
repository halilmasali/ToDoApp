package com.halilmasali.todoapp.roomRepository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomConnection(context: Context) {

    private val database = Room.databaseBuilder(
        context,
        TodoDatabase::class.java, TodoDatabase.DATABASE_NAME
    ).build()

    fun insertDataToDatabase(data: TodoData) {
        CoroutineScope(Dispatchers.IO).launch {
            val dataDao = database.dataDao()
            dataDao.insertTodoData(data)
        }
    }

    fun updateDataInDatabase(data: TodoData) {
        CoroutineScope(Dispatchers.IO).launch {
            val dataDao = database.dataDao()
            dataDao.updateData(data)
        }
    }

    fun getAllDataFromDatabase(): LiveData<Array<TodoData>> {
        val dataDao = database.dataDao()
        val data = MutableLiveData<Array<TodoData>>()
        CoroutineScope(Dispatchers.IO).launch {
            data.postValue(dataDao.getAllTodoData())
        }
        return data
    }

    fun deleteDataFromDatabase(data: TodoData) {
        CoroutineScope(Dispatchers.IO).launch {
            val dataDao = database.dataDao()
            dataDao.deleteData(data)
        }
    }
}