package com.example.gearup.fragments

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gearup.R
import com.example.gearup.databinding.FragmentSettingsBinding
import com.example.gearup.utils.HydrationReminderManager
import com.example.gearup.utils.PreferencesManager
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var hydrationReminderManager: HydrationReminderManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesManager = PreferencesManager(requireContext())
        hydrationReminderManager = HydrationReminderManager(requireContext())
        
        setupSettings()
        loadCurrentSettings()
    }

    private fun setupSettings() {
        // Hydration reminder switch
        binding.switchReminders.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.setRemindersEnabled(isChecked)
            updateReminderSettingsVisibility()
            updateHydrationReminders()
            
            val message = if (isChecked) "Hydration reminders enabled! 💧" else "Hydration reminders disabled"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        // Reminder interval spinner
        binding.spinnerInterval.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val intervals = resources.getIntegerArray(R.array.reminder_interval_values)
                if (position < intervals.size) {
                    preferencesManager.setReminderInterval(intervals[position])
                    updateHydrationReminders()
                }
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Start time button
        binding.btnStartTime.setOnClickListener {
            showTimePickerDialog(true)
        }

        // End time button
        binding.btnEndTime.setOnClickListener {
            showTimePickerDialog(false)
        }

        // Reset habits button
        binding.btnResetHabits.setOnClickListener {
            showResetHabitsDialog()
        }

        // Clear moods button
        binding.btnClearMoods.setOnClickListener {
            showClearMoodsDialog()
        }
    }

    private fun loadCurrentSettings() {
        // Load reminder settings
        binding.switchReminders.isChecked = preferencesManager.isRemindersEnabled()
        
        // Load interval setting
        val currentInterval = preferencesManager.getReminderInterval()
        val intervals = resources.getIntegerArray(R.array.reminder_interval_values)
        val position = intervals.indexOf(currentInterval)
        if (position >= 0) {
            binding.spinnerInterval.setSelection(position)
        }

        // Load time settings
        updateTimeButtons()
        updateReminderSettingsVisibility()
    }

    private fun updateTimeButtons() {
        val startTime = preferencesManager.getReminderStartTime()
        val endTime = preferencesManager.getReminderEndTime()
        
        binding.btnStartTime.text = formatTime(startTime)
        binding.btnEndTime.text = formatTime(endTime)
    }

    private fun updateReminderSettingsVisibility() {
        val isEnabled = preferencesManager.isRemindersEnabled()
        binding.reminderSettingsLayout.visibility = if (isEnabled) View.VISIBLE else View.GONE
    }

    private fun showTimePickerDialog(isStartTime: Boolean) {
        val currentTime = if (isStartTime) {
            preferencesManager.getReminderStartTime()
        } else {
            preferencesManager.getReminderEndTime()
        }
        
        val hour = currentTime / 60
        val minute = currentTime % 60
        
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val timeInMinutes = selectedHour * 60 + selectedMinute
                
                if (isStartTime) {
                    preferencesManager.setReminderStartTime(timeInMinutes)
                } else {
                    preferencesManager.setReminderEndTime(timeInMinutes)
                }
                
                updateTimeButtons()
                updateHydrationReminders()
                
                val timeString = formatTime(timeInMinutes)
                val message = if (isStartTime) {
                    "Start time updated to $timeString"
                } else {
                    "End time updated to $timeString"
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            hour,
            minute,
            false // Use 12-hour format
        )
        
        timePickerDialog.setTitle(
            if (isStartTime) "Select Start Time" else "Select End Time"
        )
        timePickerDialog.show()
    }

    private fun formatTime(timeInMinutes: Int): String {
        val hour = timeInMinutes / 60
        val minute = timeInMinutes % 60
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        
        val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        return formatter.format(calendar.time)
    }

    private fun updateHydrationReminders() {
        if (preferencesManager.isRemindersEnabled()) {
            hydrationReminderManager.scheduleReminders()
        } else {
            hydrationReminderManager.cancelReminders()
        }
    }

    private fun showResetHabitsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Reset All Habits")
            .setMessage("Are you sure you want to reset all habits and their progress? This action cannot be undone.")
            .setIcon(R.drawable.ic_habits)
            .setPositiveButton("Reset") { _, _ ->
                preferencesManager.clearAllHabits()
                Toast.makeText(context, "All habits have been reset", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showClearMoodsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear Mood History")
            .setMessage("Are you sure you want to clear all mood entries? This action cannot be undone.")
            .setIcon(R.drawable.ic_mood)
            .setPositiveButton("Clear") { _, _ ->
                preferencesManager.clearMoodEntries()
                Toast.makeText(context, "Mood history has been cleared", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}