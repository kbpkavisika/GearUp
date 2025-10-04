package com.example.gearup.models

import java.text.SimpleDateFormat
import java.util.*

data class MoodEntry(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String,
    val moodName: String,
    val note: String = "",
    val timestamp: String = getCurrentTimestamp(),
    val date: String = getCurrentDate()
) {
    companion object {
        fun getCurrentTimestamp(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return formatter.format(Date())
        }
        
        fun getCurrentDate(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return formatter.format(Date())
        }
        
        fun getDisplayTime(timestamp: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val date = inputFormat.parse(timestamp)
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                "Unknown"
            }
        }
        
        fun getDisplayDate(timestamp: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = inputFormat.parse(timestamp) ?: Date()
                val today = Calendar.getInstance()
                val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
                val entryDate = Calendar.getInstance().apply { time = date }
                
                when {
                    isSameDay(today, entryDate) -> "Today"
                    isSameDay(yesterday, entryDate) -> "Yesterday"
                    else -> {
                        val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                        outputFormat.format(date)
                    }
                }
            } catch (e: Exception) {
                "Unknown"
            }
        }
        
        private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }
    }
}

enum class MoodType(val emoji: String, val displayName: String) {
    VERY_HAPPY("😄", "Very Happy"),
    HAPPY("😊", "Happy"),
    EXCITED("🤩", "Excited"),
    NEUTRAL("😐", "Neutral"),
    TIRED("😴", "Tired"),
    SAD("😢", "Sad"),
    ANGRY("😡", "Angry");
    
    companion object {
        fun fromEmoji(emoji: String): MoodType? {
            return values().find { it.emoji == emoji }
        }
        
        fun getAllMoods(): List<MoodType> {
            return values().toList()
        }
    }
}