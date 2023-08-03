package com.halilmasali.todoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.halilmasali.todoapp.databinding.FragmentEditBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        binding.textDate.setStartIconOnClickListener {
            datePicker(binding.textDate.editText)
        }
        binding.textTime.setStartIconOnClickListener {
            timePicker(binding.textTime.editText)
        }
        binding.textReminderTime.setStartIconOnClickListener {
            timePicker(binding.textReminderTime.editText)
        }
        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            binding.textReminderTime.isEnabled = isChecked
        }
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
}