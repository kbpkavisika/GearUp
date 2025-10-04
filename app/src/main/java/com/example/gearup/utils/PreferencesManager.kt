package com.example.gearup.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.gearup.models.Habit
import com.example.gearup.models.HabitProgress
import com.example.gearup.models.MoodEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "gearup_prefs"
        private const val KEY_HABITS = "habits"
        private const val KEY_HABIT_PROGRESS = "habit_progress"
        private const val KEY_MOOD_ENTRIES = "mood_entries"
        private const val KEY_REMINDERS_ENABLED = "reminders_enabled"
        private const val KEY_REMINDER_INTERVAL = "reminder_interval"
        private const val KEY_REMINDER_START_TIME = "reminder_start_time"
        private const val KEY_REMINDER_END_TIME = "reminder_end_time"
        private const val KEY_FIRST_LAUNCH = "first_launch"
    }

    // Habit Management
    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        sharedPreferences.edit()
            .putString(KEY_HABITS, json)
            .apply()
    }

    fun getHabits(): List<Habit> {
        val json = sharedPreferences.getString(KEY_HABITS, null)
        return if (json != null) {
            val type = object : TypeToken<List<Habit>>() {}.type
            gson.fromJson(json, type)
        } else {
            getDefaultHabits()
        }
    }

    private fun getDefaultHabits(): List<Habit> {
        return listOf(
            Habit(
                name = "Drink Water",
                description = "Stay hydrated throughout the day",
                targetValue = 8,
                unit = "glasses",
                icon = "💧"
            ),
            Habit(
                name = "Exercise",
                description = "Daily physical activity",
                targetValue = 30,
                unit = "minutes",
                icon = "🏃"
            ),
            Habit(
                name = "Meditation",
                description = "Mindfulness and relaxation",
                targetValue = 15,
                unit = "minutes",
                icon = "🧘"
            ),
            Habit(
                name = "Reading",
                description = "Learn something new",
                targetValue = 30,
                unit = "minutes",
                icon = "📚"
            ),
            Habit(
                name = "Sleep",
                description = "Get quality rest",
                targetValue = 8,
                unit = "hours",
                icon = "😴"
            )
        )
    }

    // Habit Progress Management
    fun saveHabitProgress(habitProgress: List<HabitProgress>) {
        val json = gson.toJson(habitProgress)
        sharedPreferences.edit()
            .putString(KEY_HABIT_PROGRESS, json)
            .apply()
    }

    fun getHabitProgress(): List<HabitProgress> {
        val json = sharedPreferences.getString(KEY_HABIT_PROGRESS, null)
        return if (json != null) {
            val type = object : TypeToken<List<HabitProgress>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun getHabitProgressForDate(habitId: String, date: String): HabitProgress? {
        return getHabitProgress().find { it.habitId == habitId && it.date == date }
    }

    fun updateHabitProgress(habitProgress: HabitProgress) {
        val allProgress = getHabitProgress().toMutableList()
        val existingIndex = allProgress.indexOfFirst { 
            it.habitId == habitProgress.habitId && it.date == habitProgress.date 
        }
        
        if (existingIndex >= 0) {
            allProgress[existingIndex] = habitProgress
        } else {
            allProgress.add(habitProgress)
        }
        
        saveHabitProgress(allProgress)
    }

    // Mood Entry Management
    fun saveMoodEntries(moodEntries: List<MoodEntry>) {
        val json = gson.toJson(moodEntries)
        sharedPreferences.edit()
            .putString(KEY_MOOD_ENTRIES, json)
            .apply()
    }

    fun getMoodEntries(): List<MoodEntry> {
        val json = sharedPreferences.getString(KEY_MOOD_ENTRIES, null)
        return if (json != null) {
            val type = object : TypeToken<List<MoodEntry>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun addMoodEntry(moodEntry: MoodEntry) {
        val allEntries = getMoodEntries().toMutableList()
        allEntries.add(0, moodEntry) // Add to beginning for chronological order
        saveMoodEntries(allEntries)
    }

    fun getTodaysMoodEntry(): MoodEntry? {
        val today = MoodEntry.getCurrentDate()
        return getMoodEntries().find { it.date == today }
    }

    fun getMoodEntriesForDateRange(startDate: String, endDate: String): List<MoodEntry> {
        return getMoodEntries().filter { entry ->
            entry.date in startDate..endDate
        }
    }

    // Reminder Settings
    fun setRemindersEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_REMINDERS_ENABLED, enabled)
            .apply()
    }

    fun isRemindersEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMINDERS_ENABLED, true)
    }

    fun setReminderInterval(intervalMinutes: Int) {
        sharedPreferences.edit()
            .putInt(KEY_REMINDER_INTERVAL, intervalMinutes)
            .apply()
    }

    fun getReminderInterval(): Int {
        return sharedPreferences.getInt(KEY_REMINDER_INTERVAL, 60) // Default 1 hour
    }

    fun setReminderStartTime(timeInMinutes: Int) {
        sharedPreferences.edit()
            .putInt(KEY_REMINDER_START_TIME, timeInMinutes)
            .apply()
    }

    fun getReminderStartTime(): Int {
        return sharedPreferences.getInt(KEY_REMINDER_START_TIME, 8 * 60) // Default 8:00 AM
    }

    fun setReminderEndTime(timeInMinutes: Int) {
        sharedPreferences.edit()
            .putInt(KEY_REMINDER_END_TIME, timeInMinutes)
            .apply()
    }

    fun getReminderEndTime(): Int {
        return sharedPreferences.getInt(KEY_REMINDER_END_TIME, 22 * 60) // Default 10:00 PM
    }

    // First Launch
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchComplete() {
        sharedPreferences.edit()
            .putBoolean(KEY_FIRST_LAUNCH, false)
            .apply()
    }

    // Clear Data
    fun clearAllHabits() {
        sharedPreferences.edit()
            .remove(KEY_HABITS)
            .remove(KEY_HABIT_PROGRESS)
            .apply()
    }

    fun clearHabitProgress() {
        sharedPreferences.edit()
            .remove(KEY_HABIT_PROGRESS)
            .apply()
    }

    fun clearMoodEntries() {
        sharedPreferences.edit()
            .remove(KEY_MOOD_ENTRIES)
            .apply()
    }
}