package com.example.gearup.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

class WidgetUtils {
    companion object {
        fun createProgressCircle(context: Context, percentage: Int, size: Int = 160): Drawable {
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            
            val centerX = size / 2f
            val centerY = size / 2f
            val radius = size / 2f - 20f
            
            // Background circle
            val backgroundPaint = Paint().apply {
                color = Color.parseColor("#E0E0E0")
                style = Paint.Style.STROKE
                strokeWidth = 16f
                isAntiAlias = true
            }
            canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
            
            // Progress arc
            val progressPaint = Paint().apply {
                color = Color.parseColor("#8EFF00") // primary_green
                style = Paint.Style.STROKE
                strokeWidth = 16f
                strokeCap = Paint.Cap.ROUND
                isAntiAlias = true
            }
            
            val sweepAngle = (percentage / 100f) * 360f
            val rect = RectF(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
            )
            
            canvas.drawArc(rect, -90f, sweepAngle, false, progressPaint)
            
            return BitmapDrawable(context.resources, bitmap)
        }
        
        fun getProgressColor(percentage: Int): Int {
            return when {
                percentage >= 80 -> Color.parseColor("#4CAF50") // Green
                percentage >= 60 -> Color.parseColor("#8EFF00") // Primary green
                percentage >= 40 -> Color.parseColor("#FF9800") // Orange
                percentage >= 20 -> Color.parseColor("#FFC107") // Amber
                else -> Color.parseColor("#F44336") // Red
            }
        }
        
        fun getMotivationalMessage(percentage: Int): String {
            return when {
                percentage >= 100 -> "🎉 Perfect day! All habits completed!"
                percentage >= 80 -> "🌟 Excellent progress! Keep it up!"
                percentage >= 60 -> "💪 Good work! You're on track!"
                percentage >= 40 -> "📈 Making progress! Don't give up!"
                percentage >= 20 -> "🚀 Getting started! Every step counts!"
                else -> "🌱 New day, new opportunities!"
            }
        }
    }
}