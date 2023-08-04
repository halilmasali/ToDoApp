package com.halilmasali.todoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.halilmasali.todoapp.databinding.FragmentMainBinding
import com.halilmasali.todoapp.roomRepository.RoomConnection
import com.halilmasali.todoapp.roomRepository.TodoData

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var roomConnection: RoomConnection
    private lateinit var todoList: ArrayList<TodoData>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        roomConnection = RoomConnection(requireContext())

        binding.floatingActionButton.setOnClickListener {
            (activity as MainActivity).customFragmentManager.replaceFragment(EditFragment())
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        todoList = ArrayList<TodoData>()
        val customTodoItemAdapter = CustomTodoItemAdapter(todoList)
        binding.recyclerView.adapter = customTodoItemAdapter
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
        })
        getAllDataFromDatabase()


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().finish()
    }

    private fun getAllDataFromDatabase() {
        val roomConnection = RoomConnection(requireContext())
        roomConnection.getAllDataFromDatabase().observe(viewLifecycleOwner) { data ->
            checkIfDatabaseIsEmpty(data)
            todoList.clear()
            todoList.addAll(data)
            binding.recyclerView.adapter?.notifyDataSetChanged()
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
}