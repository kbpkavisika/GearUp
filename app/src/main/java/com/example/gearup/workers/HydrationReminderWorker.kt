package com.example.gearup.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.gearup.MainActivity
import com.example.gearup.R
import kotlin.random.Random

class HydrationReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "hydration_reminders"
        const val NOTIFICATION_ID = 1001
        
        // Fun hydration reminder messages
        private val reminderMessages = listOf(
            "Time to hydrate! 💧 Your body will thank you!",
            "Drink up! 🥤 Stay refreshed and energized!",
            "Hydration check! 💦 Keep that water flowing!",
            "Your daily H2O reminder! 🌊 Sip sip hooray!",
            "Water break time! 💧 Stay healthy, stay hydrated!",
            "Thirsty? 🥛 Time for some refreshing water!",
            "Hydration station! 💦 Fuel your body with water!",
            "Drop what you're doing! 💧 It's water time!",
            "Stay cool, stay hydrated! 🧊 Drink some water!",
            "Water you waiting for? 💧 Time to hydrate!"
        )
    }

    override fun doWork(): Result {
        createNotificationChannel()
        showHydrationNotification()
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Hydration Reminders"
            val descriptionText = "Reminders to drink water throughout the day"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showHydrationNotification() {
        // Create intent to open the app when notification is tapped
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_habits", true) // Open habits tab to see water habit
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Get random reminder message
        val randomMessage = reminderMessages[Random.nextInt(reminderMessages.size)]

        // Build notification
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle(applicationContext.getString(R.string.hydration_notification_title))
            .setContentText(randomMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(randomMessage))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setColor(applicationContext.getColor(R.color.primary_green))
            .addAction(
                R.drawable.ic_check,
                "Mark Water Habit",
                createMarkWaterAction()
            )
            .build()

        val notificationManager = NotificationManager.from(applicationContext)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createMarkWaterAction(): PendingIntent {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("mark_water_habit", true)
        }

        return PendingIntent.getActivity(
            applicationContext,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}