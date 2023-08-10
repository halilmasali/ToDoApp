package com.halilmasali.todoapp.ui.fragments

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.halilmasali.todoapp.databinding.FragmentMainBinding
import com.halilmasali.todoapp.roomRepository.RoomConnection
import com.halilmasali.todoapp.roomRepository.TodoData
import com.halilmasali.todoapp.viewModels.EditViewModel
import androidx.fragment.app.activityViewModels
import com.halilmasali.todoapp.ui.adapters.CustomTodoItemAdapter
import com.halilmasali.todoapp.ui.MainActivity
import com.halilmasali.todoapp.R

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var roomConnection: RoomConnection
    private lateinit var todoList: ArrayList<TodoData>
    private lateinit var customTodoItemAdapter: CustomTodoItemAdapter
    private val viewModel: EditViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        roomConnection = RoomConnection(requireContext())
        binding.floatingActionButton.setOnClickListener {
            viewModel.clearSelectedItem()
            (activity as MainActivity).customFragmentManager.replaceFragment(EditFragment())
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        todoList = ArrayList<TodoData>()
        customTodoItemAdapter = CustomTodoItemAdapter(todoList)
        binding.recyclerView.adapter = customTodoItemAdapter
        swipeDeleteListener()
        checkedChangeListener()
        getAllDataFromDatabase()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!requireActivity().isChangingConfigurations) {
            if (roomConnection.isDatabaseOpen()) {
                roomConnection.closeDatabase()
            }
            requireActivity().finish()
        }
    }

    private fun getAllDataFromDatabase() {
        val roomConnection = RoomConnection(requireContext())
        roomConnection.getAllDataFromDatabase().observe(viewLifecycleOwner) { data ->
            checkIfDatabaseIsEmpty(data)
            todoList.clear()
            todoList.addAll(data)
            todoList.sortedByDescending { it.date }
            binding.recyclerView.adapter?.notifyItemRangeInserted(0, todoList.size)
        }
    }

    private fun checkIfDatabaseIsEmpty(data: Array<TodoData>) {
        if (data.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.imageViewEmpty.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.imageViewEmpty.visibility = View.GONE
        }
    }

    private fun swipeDeleteListener() {
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
            val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete_24)
            val intrinsicWidth = deleteIcon!!.intrinsicWidth
            val intrinsicHeight = deleteIcon!!.intrinsicHeight
            val background = ColorDrawable()

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                customTodoItemAdapter.removeAt(position)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top
                val isCanceled = dX == 0f && !isCurrentlyActive
                if (isCanceled) {
                    clearCanvas(
                        c,
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    return
                }
                // Draw the red delete background
                background.color = Color.parseColor("#f44336")
                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)

                // Calculate position of delete icon
                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
                val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom = deleteIconTop + intrinsicHeight

                // Draw the delete icon
                deleteIcon?.setBounds(
                    deleteIconLeft,
                    deleteIconTop,
                    deleteIconRight,
                    deleteIconBottom
                )
                deleteIcon?.draw(c)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

            private fun clearCanvas(
                c: Canvas?,
                left: Float,
                top: Float,
                right: Float,
                bottom: Float
            ) {
                c?.drawRect(left, top, right, bottom, clearPaint)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun checkedChangeListener() {
        customTodoItemAdapter.setOnCheckedChangeListener(object :
            CustomTodoItemAdapter.OnCheckedChangeListener {
            override fun onCheckedChanged(isChecked: Boolean, data: TodoData) {
                if (isChecked) {
                    val replacedData = TodoData(
                        data.id,
                        data.title,
                        data.description,
                        data.date,
                        data.reminderTime,
                        true
                    )
                    roomConnection.updateDataInDatabase(replacedData)
                } else {
                    val replacedData = TodoData(
                        data.id,
                        data.title,
                        data.description,
                        data.date,
                        data.reminderTime,
                        false
                    )
                    roomConnection.updateDataInDatabase(replacedData)
                }
            }

            override fun onItemClicked(data: TodoData) {
                viewModel.selectItem(data)
                (activity as MainActivity).customFragmentManager.replaceFragment(EditFragment())
            }
        })
    }
}