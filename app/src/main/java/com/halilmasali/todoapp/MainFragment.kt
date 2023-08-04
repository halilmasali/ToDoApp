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
        val todoList = ArrayList<TodoData>()
        for (i in 0..10) {
            todoList.add(
                TodoData(
                    title = "test",
                    description = "açıklama",
                    date = "02/02/0999",
                    reminderTime = "",
                    isDone = false
                )
            )
        }



        val customTodoItem = CustomTodoItemAdapter(todoList)
        binding.recyclerView.adapter = customTodoItem

        roomConnection.getAllDataFromDatabase().observe(viewLifecycleOwner) { data ->
            todoList.clear()
            todoList.addAll(data)
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().finish()
    }
}