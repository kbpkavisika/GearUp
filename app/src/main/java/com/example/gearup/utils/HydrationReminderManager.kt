package com.example.gearup.utils

import android.content.Context
import androidx.work.*
import com.example.gearup.workers.HydrationReminderWorker
import java.util.*
import java.util.concurrent.TimeUnit

class HydrationReminderManager(private val context: Context) {
    
    companion object {
        private const val HYDRATION_WORK_TAG = "hydration_reminder"
        private const val HYDRATION_WORK_NAME = "hydration_reminder_work"
    }
    
    private val preferencesManager = PreferencesManager(context)
    private val workManager = WorkManager.getInstance(context)

    fun scheduleReminders() {
        if (!preferencesManager.isRemindersEnabled()) {
            cancelReminders()
            return
        }

        val intervalMinutes = preferencesManager.getReminderInterval()
        val startTimeMinutes = preferencesManager.getReminderStartTime()
        val endTimeMinutes = preferencesManager.getReminderEndTime()

        // Cancel existing reminders
        cancelReminders()

        // Calculate initial delay to start at the specified start time
        val initialDelay = calculateInitialDelay(startTimeMinutes)
        
        // Create constraints to only run during active hours
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .build()

        // Create the periodic work request
        val hydrationWorkRequest = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
            intervalMinutes.toLong(), TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MINUTES)
            .addTag(HYDRATION_WORK_TAG)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        // Enqueue the work
        workManager.enqueueUniquePeriodicWork(
            HYDRATION_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            hydrationWorkRequest
        )
    }

    fun cancelReminders() {
        workManager.cancelUniqueWork(HYDRATION_WORK_NAME)
        workManager.cancelAllWorkByTag(HYDRATION_WORK_TAG)
    }

    fun isReminderScheduled(): Boolean {
        val workInfos = workManager.getWorkInfosForUniqueWork(HYDRATION_WORK_NAME).get()
        return workInfos.any { workInfo ->
            workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING
        }
    }

    private fun calculateInitialDelay(startTimeMinutes: Int): Long {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentTotalMinutes = currentHour * 60 + currentMinute

        return if (currentTotalMinutes < startTimeMinutes) {
            // If it's before start time today, delay until start time
            (startTimeMinutes - currentTotalMinutes).toLong()
        } else {
            // If it's after start time today, delay until start time tomorrow
            val minutesUntilMidnight = (24 * 60) - currentTotalMinutes
            (minutesUntilMidnight + startTimeMinutes).toLong()
        }
    }

    fun shouldShowNotification(): Boolean {
        if (!preferencesManager.isRemindersEnabled()) {
            return false
        }

        val currentTime = Calendar.getInstance()
        val currentMinutes = currentTime.get(Calendar.HOUR_OF_DAY) * 60 + currentTime.get(Calendar.MINUTE)
        val startTime = preferencesManager.getReminderStartTime()
        val endTime = preferencesManager.getReminderEndTime()

        return currentMinutes in startTime..endTime
    }

    fun getNextReminderTime(): String {
        if (!preferencesManager.isRemindersEnabled()) {
            return "Reminders disabled"
        }

        val intervalMinutes = preferencesManager.getReminderInterval()
        val currentTime = Calendar.getInstance()
        currentTime.add(Calendar.MINUTE, intervalMinutes)
        
        val formatter = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatter.format(currentTime.time)
    }
}