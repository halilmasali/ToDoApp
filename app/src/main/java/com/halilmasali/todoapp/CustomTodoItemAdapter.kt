package com.halilmasali.todoapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.halilmasali.todoapp.databinding.CustomTodoItemBinding
import com.halilmasali.todoapp.roomRepository.TodoData

class CustomTodoItemAdapter(private val items: List<TodoData>) : RecyclerView.Adapter<CustomTodoItemAdapter.ViewHolder>() {

    private lateinit var binding: CustomTodoItemBinding

    inner class ViewHolder(binding: CustomTodoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.textTitle
        val date = binding.textDate
        val notification = binding.imageNotification
        val checkBox = binding.checkBoxTodoItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = CustomTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.date.text = item.date
        holder.checkBox.isChecked = item.isDone
        if (item.reminderTime.isEmpty()) {
            holder.notification.setImageResource(R.drawable.ic_notifications_off)
        } else {
            holder.notification.setImageResource(R.drawable.ic_notifications_active)
        }
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChangeListener?.onCheckedChanged(isChecked,item)
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(isChecked: Boolean,data: TodoData)
    }
    private var onCheckedChangeListener: OnCheckedChangeListener? = null

    fun setOnCheckedChangeListener(onCheckedChangeListener: OnCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener
    }

}
