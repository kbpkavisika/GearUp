package com.example.gearup.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.gearup.MainActivity
import com.example.gearup.R
import com.example.gearup.models.Habit
import com.example.gearup.utils.PreferencesManager
import com.example.gearup.utils.WidgetUtils
import java.text.SimpleDateFormat
import java.util.*

class HabitProgressWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_UPDATE_WIDGET = "com.example.gearup.UPDATE_WIDGET"
        
        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, HabitProgressWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            
            val intent = Intent(context, HabitProgressWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds)
            }
            context.sendBroadcast(intent)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            ACTION_UPDATE_WIDGET -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val thisWidget = ComponentName(context, HabitProgressWidgetProvider::class.java)
                val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
                onUpdate(context, appWidgetManager, allWidgetIds)
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val preferencesManager = PreferencesManager(context)
        val habits = preferencesManager.getHabits()
        val today = Habit.getTodayKey()
        
        // Calculate progress
        var completedHabits = 0
        var totalHabits = habits.size
        
        habits.forEach { habit ->
            val progress = preferencesManager.getHabitProgressForDate(habit.id, today)
            if (progress?.isCompleted == true) {
                completedHabits++
            }
        }
        
        val progressPercentage = if (totalHabits > 0) {
            (completedHabits * 100) / totalHabits
        } else 0
        
        // Create RemoteViews
        val views = RemoteViews(context.packageName, R.layout.widget_habit_progress)
        
        // Update widget content
        views.setTextViewText(R.id.widget_percentage, "$progressPercentage%")
        views.setTextViewText(
            R.id.widget_status, 
            "$completedHabits of $totalHabits habits completed"
        )
        
        // Update last updated time
        val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        views.setTextViewText(R.id.widget_last_updated, "Updated $currentTime")
        
        // Set progress circle color based on percentage
        val progressColor = WidgetUtils.getProgressColor(progressPercentage)
        views.setInt(R.id.progress_foreground, "setColorFilter", progressColor)
        
        // Set click intent to open app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_habits", true)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        views.setOnClickPendingIntent(R.id.progress_container, pendingIntent)
        
        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}