package com.example.gearup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gearup.databinding.ItemHabitBinding
import com.example.gearup.models.Habit
import com.example.gearup.models.HabitProgress

class HabitsAdapter(
    private var habits: MutableList<Habit>,
    private var habitProgress: Map<String, HabitProgress>,
    private val onHabitClick: (Habit) -> Unit,
    private val onCompleteClick: (Habit) -> Unit,
    private val onEditClick: (Habit) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount(): Int = habits.size

    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }

    fun updateProgress(newProgress: Map<String, HabitProgress>) {
        habitProgress = newProgress
        notifyDataSetChanged()
    }

    inner class HabitViewHolder(private val binding: ItemHabitBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            val progress = habitProgress[habit.id]
            val currentValue = progress?.currentValue ?: 0
            val isCompleted = progress?.isCompleted ?: false
            val progressPercentage = progress?.getProgressPercentage(habit.targetValue) ?: 0

            binding.apply {
                // Set habit info
                tvHabitName.text = "${habit.icon} ${habit.name}"
                tvProgress.text = "$currentValue/${habit.targetValue} ${habit.unit}"
                progressBar.progress = progressPercentage

                // Update completion button
                if (isCompleted) {
                    btnComplete.text = "✓ Completed"
                    btnComplete.isEnabled = false
                    btnComplete.alpha = 0.6f
                } else {
                    btnComplete.text = "Mark Complete"
                    btnComplete.isEnabled = true
                    btnComplete.alpha = 1.0f
                }

                // Set click listeners
                root.setOnClickListener { onHabitClick(habit) }
                btnComplete.setOnClickListener { 
                    if (!isCompleted) {
                        onCompleteClick(habit)
                    }
                }
                btnEdit.setOnClickListener { onEditClick(habit) }
            }
        }
    }
}