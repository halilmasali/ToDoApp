package com.halilmasali.todoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.halilmasali.todoapp.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.floatingActionButton.setOnClickListener{
            (activity as MainActivity).customFragmentManager.replaceFragment(EditFragment())
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().finish()
    }
}