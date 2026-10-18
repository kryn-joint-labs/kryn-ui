package com.kryn.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.os.Build
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class VelvetWidgetReceiver : AppWidgetProvider() {

    companion object {
        const val ACTION_MINUTE_TICK = "com.kryn.ui.ACTION_VELVET_TICK"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_MINUTE_TICK || intent.action == Intent.ACTION_TIME_TICK || intent.action == Intent.ACTION_TIME_CHANGED) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, VelvetWidgetReceiver::class.java)
            val allIds = appWidgetManager.getAppWidgetIds(thisWidget)
            if (allIds.isNotEmpty()) {
                onUpdate(context, appWidgetManager, allIds)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val now = Date()

        val rawTime = SimpleDateFormat("h:mm", Locale.US).format(now)
        val amPmText = SimpleDateFormat("a", Locale.US).format(now).uppercase()
        val dayText = SimpleDateFormat("EEEE", Locale.US).format(now)
        val dateText = SimpleDateFormat("d MMMM yyyy", Locale.US).format(now).uppercase()

        // 1. Wallpaper Dynamic Tone Detection (Dark vs Light)
        val isNightMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        val baseAccentColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                ContextCompat.getColor(context, android.R.color.system_neutral1_100)
            } catch (e: Exception) {
                if (isNightMode) Color.WHITE else Color.BLACK
            }
        } else {
            if (isNightMode) Color.WHITE else Color.BLACK
        }

        val isLightText = Color.luminance(baseAccentColor) > 0.5f

        val ghostTimeColor = if (isLightText) 0x33FFFFFF.toInt() else 0x33000000.toInt()
        val amPmColor = if (isLightText) 0x66FFFFFF.toInt() else 0x66000000.toInt()
        val dividerColor = if (isLightText) 0x55FFFFFF.toInt() else 0x55000000.toInt()
        val dateColor = if (isLightText) 0xE6FFFFFF.toInt() else 0xDD000000.toInt()

        // 2. Load custom fonts safely from assets
        val scriptTf = try {
            Typeface.createFromAsset(context.assets, "fonts/script_day.otf")
        } catch (e: Exception) {
            Typeface.DEFAULT_BOLD
        }

        val clockDigitsTf = try {
            Typeface.createFromAsset(context.assets, "fonts/clock_digits.ttf")
        } catch (e: Exception) {
            Typeface.create("sans-serif-condensed", Typeface.BOLD)
        }

        val sansMetaTf = try {
            Typeface.createFromAsset(context.assets, "fonts/sans_meta.ttf")
        } catch (e: Exception) {
            Typeface.SANS_SERIF
        }

        // 3. Render High-Impact Bitmaps
        // Bebas Neue clock digits at 116dp size
        val timeBmp = renderText(
            text = rawTime,
            typeface = clockDigitsTf,
            textSizeDp = 116f,
            letterSpacing = 0.02f,
            textColor = ghostTimeColor
        )

        // AM/PM Indicator
        val ampmBmp = renderText(
            text = amPmText,
            typeface = sansMetaTf,
            textSizeDp = 12f,
            letterSpacing = 0.12f,
            textColor = amPmColor
        )

        // Hero Script Day (Permanently lavender-purple #B84DFF)
        val dayBmp = renderText(
            text = dayText,
            typeface = scriptTf,
            textSizeDp = 50f,
            letterSpacing = 0.04f,
            textColor = 0xFFB84DFF.toInt(),
            shadowColor = 0x55000000.toInt()
        )

        // Date (Clean, wide-tracked sans)
        val dateBmp = renderText(
            text = dateText,
            typeface = sansMetaTf,
            textSizeDp = 12.5f,
            letterSpacing = 0.18f,
            textColor = dateColor,
            shadowColor = if (isLightText) 0x66000000.toInt() else 0
        )

        // Divider Line Bitmap
        val dividerBmp = Bitmap.createBitmap(120, 4, Bitmap.Config.ARGB_8888).apply {
            eraseColor(dividerColor)
        }

        // 4. Update RemoteViews
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_velvet)
            views.setImageViewBitmap(R.id.velvet_time_img, timeBmp)
            views.setImageViewBitmap(R.id.velvet_ampm_img, ampmBmp)
            views.setImageViewBitmap(R.id.velvet_day_img, dayBmp)
            views.setImageViewBitmap(R.id.velvet_divider_img, dividerBmp)
            views.setImageViewBitmap(R.id.velvet_date_img, dateBmp)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        // 5. Schedule Next Minute Tick via AlarmManager (Guarantees zero-freeze)
        scheduleNextMinuteTick(context)
    }

    private fun scheduleNextMinuteTick(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, VelvetWidgetReceiver::class.java).apply {
            action = ACTION_MINUTE_TICK
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Target exactly the :00 second mark of the next minute
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            alarmManager.set(
                AlarmManager.RTC,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        // Cancel alarm ticker when widget is removed from homescreen
        val intent = Intent(context, VelvetWidgetReceiver::class.java).apply {
            action = ACTION_MINUTE_TICK
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun renderText(
        text: String,
        typeface: Typeface,
        textSizeDp: Float,
        letterSpacing: Float,
        textColor: Int,
        shadowColor: Int = 0
    ): Bitmap {
        val density = 3.0f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            this.textSize = textSizeDp * density
            this.letterSpacing = letterSpacing
            this.color = textColor
            this.textAlign = Paint.Align.CENTER
            if (shadowColor != 0) {
                setShadowLayer(3.5f * density, 0f, 1.5f * density, shadowColor)
            }
        }

        val fontMetrics = paint.fontMetrics
        val textWidth = paint.measureText(text)
        val width = (textWidth + 28f * density).toInt().coerceAtLeast(32)
        val height = ((fontMetrics.bottom - fontMetrics.top) + 16f * density).toInt().coerceAtLeast(16)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val xPos = width / 2f
        val yPos = (height / 2f) - ((fontMetrics.ascent + fontMetrics.descent) / 2f)

        canvas.drawText(text, xPos, yPos, paint)
        return bitmap
    }
}
