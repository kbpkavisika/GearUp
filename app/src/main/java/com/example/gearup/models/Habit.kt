package com.example.gearup.models

import java.text.SimpleDateFormat
import java.util.*

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val targetValue: Int = 1,
    val unit: String = "times",
    val icon: String = "💪",
    val isActive: Boolean = true,
    val createdDate: String = getCurrentDate()
) {
    companion object {
        fun getCurrentDate(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return formatter.format(Date())
        }
        
        fun getTodayKey(): String {
            return getCurrentDate()
        }
    }
}

data class HabitProgress(
    val habitId: String,
    val date: String,
    val currentValue: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: String? = null
) {
    fun getProgressPercentage(targetValue: Int): Int {
        return if (targetValue > 0) {
            ((currentValue.toFloat() / targetValue) * 100).toInt().coerceAtMost(100)
        } else 0
    }
}