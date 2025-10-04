package com.example.gearup.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gearup.R
import com.example.gearup.adapters.HabitsAdapter
import com.example.gearup.databinding.DialogAddHabitBinding
import com.example.gearup.databinding.FragmentHabitsBinding
import com.example.gearup.models.Habit
import com.example.gearup.models.HabitProgress
import com.example.gearup.utils.PreferencesManager
import com.example.gearup.widgets.HabitProgressWidgetProvider
import java.text.SimpleDateFormat
import java.util.*

class HabitsFragment : Fragment() {
    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var habitsAdapter: HabitsAdapter
    private var habits = mutableListOf<Habit>()
    private var habitProgress = mutableMapOf<String, HabitProgress>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesManager = PreferencesManager(requireContext())
        setupHabitTracker()
        loadData()
    }

    private fun setupHabitTracker() {
        // Setup RecyclerView
        habitsAdapter = HabitsAdapter(
            habits = habits,
            habitProgress = habitProgress,
            onHabitClick = { habit -> showHabitDetails(habit) },
            onCompleteClick = { habit -> markHabitComplete(habit) },
            onEditClick = { habit -> showAddEditDialog(habit) }
        )
        
        binding.rvHabits.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = habitsAdapter
        }

        // Setup FAB
        binding.fabAddHabit.setOnClickListener {
            showAddEditDialog()
        }

        // Setup share button
        binding.btnShareProgress.setOnClickListener {
            shareHabitProgress()
        }
    }

    private fun loadData() {
        // Load habits
        habits.clear()
        habits.addAll(preferencesManager.getHabits())
        
        // Load today's progress
        loadTodayProgress()
        
        // Update UI
        updateUI()
        
        // Initialize default habits if first launch
        if (preferencesManager.isFirstLaunch() && habits.isEmpty()) {
            habits.addAll(preferencesManager.getHabits()) // This will return default habits
            preferencesManager.saveHabits(habits)
            preferencesManager.setFirstLaunchComplete()
            habitsAdapter.updateHabits(habits)
        }
    }

    private fun loadTodayProgress() {
        val today = Habit.getTodayKey()
        habitProgress.clear()
        
        habits.forEach { habit ->
            val progress = preferencesManager.getHabitProgressForDate(habit.id, today)
                ?: HabitProgress(habitId = habit.id, date = today)
            habitProgress[habit.id] = progress
        }
    }

    private fun updateUI() {
        // Update progress summary
        val totalHabits = habits.size
        val completedHabits = habitProgress.values.count { it.isCompleted }
        val progressPercentage = if (totalHabits > 0) {
            (completedHabits * 100) / totalHabits
        } else 0
        
        binding.tvProgressPercentage.text = "$progressPercentage% Complete"
        
        // Update adapter
        habitsAdapter.updateHabits(habits)
        habitsAdapter.updateProgress(habitProgress)
        
        // Show/hide empty state
        if (habits.isEmpty()) {
            binding.rvHabits.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.rvHabits.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }
    }

    private fun markHabitComplete(habit: Habit) {
        val today = Habit.getTodayKey()
        val currentProgress = habitProgress[habit.id] ?: HabitProgress(
            habitId = habit.id,
            date = today
        )
        
        val newValue = (currentProgress.currentValue + 1).coerceAtMost(habit.targetValue)
        val isCompleted = newValue >= habit.targetValue
        
        val updatedProgress = currentProgress.copy(
            currentValue = newValue,
            isCompleted = isCompleted,
            completedAt = if (isCompleted) getCurrentTimestamp() else null
        )
        
        habitProgress[habit.id] = updatedProgress
        preferencesManager.updateHabitProgress(updatedProgress)
        
        updateUI()
        
        // Update widget
        HabitProgressWidgetProvider.updateAllWidgets(requireContext())
        
        if (isCompleted) {
            Toast.makeText(context, "🎉 ${habit.name} completed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showHabitDetails(habit: Habit) {
        val progress = habitProgress[habit.id]
        val message = buildString {
            append("${habit.icon} ${habit.name}\n\n")
            if (habit.description.isNotEmpty()) {
                append("${habit.description}\n\n")
            }
            append("Target: ${habit.targetValue} ${habit.unit}\n")
            append("Today's Progress: ${progress?.currentValue ?: 0}/${habit.targetValue}\n")
            if (progress?.isCompleted == true) {
                append("✅ Completed today!")
            }
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle("Habit Details")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showAddEditDialog(habit: Habit? = null) {
        val dialogBinding = DialogAddHabitBinding.inflate(layoutInflater)
        var selectedIcon = habit?.icon ?: "💪"
        
        // Setup dialog
        if (habit != null) {
            dialogBinding.tvDialogTitle.text = getString(R.string.edit_habit)
            dialogBinding.etHabitName.setText(habit.name)
            dialogBinding.etHabitDescription.setText(habit.description)
            dialogBinding.etTargetValue.setText(habit.targetValue.toString())
            dialogBinding.etUnit.setText(habit.unit)
            dialogBinding.btnDelete.visibility = View.VISIBLE
            selectedIcon = habit.icon
        } else {
            dialogBinding.btnDelete.visibility = View.GONE
        }
        
        // Setup icon selection
        val iconButtons = listOf(
            dialogBinding.btnIconWater to "💧",
            dialogBinding.btnIconExercise to "🏃",
            dialogBinding.btnIconMeditation to "🧘",
            dialogBinding.btnIconBook to "📚",
            dialogBinding.btnIconSleep to "😴",
            dialogBinding.btnIconStrength to "💪"
        )
        
        fun updateIconSelection() {
            iconButtons.forEach { (button, icon) ->
                button.setBackgroundColor(
                    if (icon == selectedIcon) {
                        resources.getColor(R.color.primary_green, null)
                    } else {
                        resources.getColor(android.R.color.transparent, null)
                    }
                )
            }
        }
        
        iconButtons.forEach { (button, icon) ->
            button.setOnClickListener {
                selectedIcon = icon
                updateIconSelection()
            }
        }
        
        updateIconSelection()
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()
        
        // Handle save
        dialogBinding.btnSave.setOnClickListener {
            val name = dialogBinding.etHabitName.text.toString().trim()
            val description = dialogBinding.etHabitDescription.text.toString().trim()
            val targetText = dialogBinding.etTargetValue.text.toString().trim()
            val unit = dialogBinding.etUnit.text.toString().trim()
            
            if (name.isEmpty() || targetText.isEmpty() || unit.isEmpty()) {
                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val targetValue = targetText.toIntOrNull()
            if (targetValue == null || targetValue <= 0) {
                Toast.makeText(context, "Please enter a valid target value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val newHabit = if (habit != null) {
                habit.copy(
                    name = name,
                    description = description,
                    targetValue = targetValue,
                    unit = unit,
                    icon = selectedIcon
                )
            } else {
                Habit(
                    name = name,
                    description = description,
                    targetValue = targetValue,
                    unit = unit,
                    icon = selectedIcon
                )
            }
            
            saveHabit(newHabit, habit != null)
            dialog.dismiss()
        }
        
        // Handle cancel
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        // Handle delete
        if (habit != null) {
            dialogBinding.btnDelete.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Habit")
                    .setMessage("Are you sure you want to delete this habit? This action cannot be undone.")
                    .setPositiveButton("Delete") { _, _ ->
                        deleteHabit(habit)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
        
        dialog.show()
    }

    private fun saveHabit(habit: Habit, isEdit: Boolean) {
        if (isEdit) {
            val index = habits.indexOfFirst { it.id == habit.id }
            if (index >= 0) {
                habits[index] = habit
            }
        } else {
            habits.add(habit)
            // Initialize today's progress for new habit
            val today = Habit.getTodayKey()
            habitProgress[habit.id] = HabitProgress(habitId = habit.id, date = today)
        }
        
        preferencesManager.saveHabits(habits)
        updateUI()
        
        // Update widget
        HabitProgressWidgetProvider.updateAllWidgets(requireContext())
        
        Toast.makeText(
            context, 
            if (isEdit) "Habit updated" else "Habit added", 
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun deleteHabit(habit: Habit) {
        habits.removeAll { it.id == habit.id }
        habitProgress.remove(habit.id)
        
        preferencesManager.saveHabits(habits)
        updateUI()
        
        // Update widget
        HabitProgressWidgetProvider.updateAllWidgets(requireContext())
        
        Toast.makeText(context, "Habit deleted", Toast.LENGTH_SHORT).show()
    }

    private fun getCurrentTimestamp(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(Date())
    }

    private fun shareHabitProgress() {
        if (habits.isEmpty()) {
            Toast.makeText(context, "No habits to share", Toast.LENGTH_SHORT).show()
            return
        }

        val today = Habit.getTodayKey()
        val dateFormatted = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
        
        val shareText = buildString {
            appendLine("📈 My GearUp Progress - $dateFormatted")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine()
            
            var totalCompleted = 0
            var totalHabits = habits.size
            
            habits.forEach { habit ->
                val progress = habitProgress[habit.id]
                val currentValue = progress?.currentValue ?: 0
                val targetValue = habit.targetValue
                val percentage = if (targetValue > 0) (currentValue * 100 / targetValue).coerceAtMost(100) else 0
                
                appendLine("${habit.icon} ${habit.name}")
                appendLine("   Progress: $currentValue/${targetValue} ${habit.unit} (${percentage}%)")
                
                if (percentage >= 100) totalCompleted++
                appendLine()
            }
            
            val overallPercentage = if (totalHabits > 0) (totalCompleted * 100 / totalHabits) else 0
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("🎯 Overall: $totalCompleted/$totalHabits habits completed ($overallPercentage%)")
            appendLine()
            appendLine("Shared from GearUp - Your Wellness Companion 🌱")
        }

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "My Daily Progress from GearUp")
        }

        startActivity(Intent.createChooser(shareIntent, "Share progress via"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}