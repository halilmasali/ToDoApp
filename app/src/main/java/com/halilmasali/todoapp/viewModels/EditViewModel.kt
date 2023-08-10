package com.halilmasali.todoapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.halilmasali.todoapp.roomRepository.TodoData

class EditViewModel : ViewModel() {
    private val mutableSelectedItem = MutableLiveData<TodoData?>()
    val selectedItem: MutableLiveData<TodoData?> get() = mutableSelectedItem

    fun selectItem(item: TodoData) {
        mutableSelectedItem.value = item
    }

    fun clearSelectedItem() {
        mutableSelectedItem.value = null
    }
}