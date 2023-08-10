package com.halilmasali.todoapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.halilmasali.todoapp.R
import com.halilmasali.todoapp.databinding.CustomTodoItemBinding
import com.halilmasali.todoapp.roomRepository.RoomConnection
import com.halilmasali.todoapp.roomRepository.TodoData

class CustomTodoItemAdapter(private val items: MutableList<TodoData>) :
    RecyclerView.Adapter<CustomTodoItemAdapter.ViewHolder>() {

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
            onCheckedChangeListener?.onCheckedChanged(isChecked, item)
        }
        holder.itemView.setOnClickListener {
            onCheckedChangeListener?.onItemClicked(item)
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(isChecked: Boolean, data: TodoData)
        fun onItemClicked(data: TodoData)
    }

    private var onCheckedChangeListener: OnCheckedChangeListener? = null

    fun setOnCheckedChangeListener(onCheckedChangeListener: OnCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener
    }

    fun removeAt(position: Int) {
        MaterialAlertDialogBuilder(binding.root.context)
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete this item?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                val roomConnection = RoomConnection(binding.root.context)
                roomConnection.deleteDataFromDatabase(items[position])
                items.removeAt(position)
                notifyItemRemoved(position)
            }
            .setNegativeButton("No") { _, _ ->
                notifyItemChanged(position)
            }
            .show()
    }
}
