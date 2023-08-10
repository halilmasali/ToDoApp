package com.halilmasali.todoapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.halilmasali.todoapp.ui.MainActivity
import com.halilmasali.todoapp.R
import com.halilmasali.todoapp.databinding.FragmentEditBinding
import com.halilmasali.todoapp.notification.Notification
import com.halilmasali.todoapp.roomRepository.RoomConnection
import com.halilmasali.todoapp.roomRepository.TodoData
import com.halilmasali.todoapp.viewModels.EditViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditViewModel by activityViewModels()
    private lateinit var reminder: Notification
    private var selectedItemId = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        binding.textDate.setStartIconOnClickListener {
            datePicker(binding.textDate.editText)
        }
        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.textReminderTime.setStartIconOnClickListener {
            timePicker(binding.textReminderTime.editText)
        }
        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            binding.textReminderTime.isEnabled = isChecked
            binding.textReminderTime.editText?.setText("")
        }
        binding.buttonSave.setOnClickListener {
            saveDataToDatabase()
        }

        viewModel.selectedItem.observe(viewLifecycleOwner) { todoData ->
            if (todoData != null) {
                selectedItemId = todoData.id!!
                binding.textTitle.editText?.setText(todoData.title)
                binding.textDescription.editText?.setText(todoData.description)
                binding.textDate.editText?.setText(todoData.date)
                binding.switchReminder.isChecked = todoData.reminderTime != ""
                binding.textReminderTime.isEnabled = todoData.reminderTime != ""
                binding.textReminderTime.editText?.setText(todoData.reminderTime)
                binding.buttonSave.text = getText(R.string.button_update)
            } else {
                binding.textTitle.editText?.setText("")
                binding.textDescription.editText?.setText("")
                binding.textDate.editText?.setText("")
                binding.textReminderTime.editText?.setText("")
                binding.switchReminder.isChecked = false
                binding.buttonSave.text = getText(R.string.button_save)
            }

        }
        reminder = Notification(requireContext())
        return binding.root
    }

    private fun timePicker(editText: EditText?) {
        val picker =
            MaterialTimePicker.Builder()
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Time")
                .build()
        picker.show(parentFragmentManager, "tag")
        picker.addOnPositiveButtonClickListener {
            val hour = if (picker.hour < 10) "0${picker.hour}" else picker.hour
            val minute = if (picker.minute < 10) "0${picker.minute}" else picker.minute
            val time = "$hour:$minute"
            editText?.setText(time)
        }
    }

    private fun datePicker(editText: EditText?) {
        // set calendar constraints
        val calendarConstraints = CalendarConstraints.Builder()
            .setStart(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(calendarConstraints)
                .build()
        datePicker.show(parentFragmentManager, "tag")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDate = dateFormat.format(calendar.time)
            editText?.setText(selectedDate)
        }
    }

    private fun checkDataFromUser(): Boolean {
        return binding.textTitle.editText?.text.toString().isNotEmpty() &&
                binding.textDescription.editText?.text.toString().isNotEmpty() &&
                binding.textDate.editText?.text.toString().isNotEmpty()
    }

    private fun checkReminderTime(): Boolean {
        return binding.switchReminder.isChecked &&
                binding.textReminderTime.editText?.text.toString().isEmpty()
    }

    private fun saveDataToDatabase() {
        if (checkDataFromUser()) {
            if (checkReminderTime()) {
                Toast.makeText(
                    requireContext(), getString(R.string.reminder_check), Toast.LENGTH_SHORT
                ).show()
                return
            }
            val data = createTodoDataFromInput()
            val roomConnection = RoomConnection(requireContext())
            if (binding.buttonSave.text == getText(R.string.button_update)) {
                roomConnection.updateDataInDatabase(data)
                Toast.makeText(requireContext(), "Data updated", Toast.LENGTH_SHORT).show()
            } else {
                roomConnection.insertDataToDatabase(data)
                Toast.makeText(requireContext(), "Data saved", Toast.LENGTH_SHORT).show()
            }
            if (binding.switchReminder.isChecked) {
                val time = getTime()
                if (time > System.currentTimeMillis())
                    println(
                        "Time is bigger than current time." +
                                " Selected: $time  Current: ${System.currentTimeMillis()}"
                    )
                // FIXME notification is not working
                reminder.scheduleNotification("Reminder", "You have a reminder", time)
            }
            // go back to main fragment
            (activity as MainActivity).customFragmentManager.replaceFragment(MainFragment())
        } else
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
    }

    private fun createTodoDataFromInput(): TodoData {
        return TodoData(
            id = if (selectedItemId == 0) null else selectedItemId,
            title = binding.textTitle.editText?.text.toString(),
            description = binding.textDescription.editText?.text.toString(),
            date = binding.textDate.editText?.text.toString(),
            reminderTime = binding.textReminderTime.editText?.text.toString(),
            isDone = false
        )
    }

    private fun getTime(): Long {
        val (hour, minute) = binding.textReminderTime.editText?.text.toString().split(":")
        val (day, month, year) = binding.textDate.editText?.text.toString().split("/")
        val calendar = Calendar.getInstance()
        calendar.set(year.toInt(), month.toInt(), day.toInt(), hour.toInt(), minute.toInt())
        return calendar.timeInMillis
    }
}