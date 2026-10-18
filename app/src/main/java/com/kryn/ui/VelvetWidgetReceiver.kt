package com.kryn.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.widget.RemoteViews
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

        // 1. Pull user-selected custom colors from SharedPreferences
        val prefs = context.getSharedPreferences("kryn_velvet_prefs", Context.MODE_PRIVATE)
        val dayColorHex = prefs.getLong("velvet_color_day", 0xFFB84DFF).toInt()
        val timeColorHex = prefs.getLong("velvet_color_time", 0xFFFFFFFF).toInt()

        // Ghost watermark opacity applied to user-chosen time color
        val alphaTime = (Color.alpha(timeColorHex) * 0.28f).toInt().coerceIn(20, 255)
        val ghostTimeColor = Color.argb(
            alphaTime,
            Color.red(timeColorHex),
            Color.green(timeColorHex),
            Color.blue(timeColorHex)
        )

        val alphaAmPm = (Color.alpha(timeColorHex) * 0.55f).toInt().coerceIn(30, 255)
        val amPmColor = Color.argb(
            alphaAmPm,
            Color.red(timeColorHex),
            Color.green(timeColorHex),
            Color.blue(timeColorHex)
        )

        val alphaDivider = (Color.alpha(timeColorHex) * 0.45f).toInt().coerceIn(25, 255)
        val dividerColor = Color.argb(
            alphaDivider,
            Color.red(timeColorHex),
            Color.green(timeColorHex),
            Color.blue(timeColorHex)
        )

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

        // 3. Render Prominent 2x Scale Bitmaps
        // Bebas Neue clock digits scaled up to 140dp
        val timeBmp = renderText(
            text = rawTime,
            typeface = clockDigitsTf,
            textSizeDp = 140f,
            letterSpacing = 0.02f,
            textColor = ghostTimeColor
        )

        // AM/PM Indicator pinned to right
        val ampmBmp = renderText(
            text = amPmText,
            typeface = sansMetaTf,
            textSizeDp = 13f,
            letterSpacing = 0.12f,
            textColor = amPmColor
        )

        // Hero Script Day scaled to 62dp
        val dayBmp = renderText(
            text = dayText,
            typeface = scriptTf,
            textSizeDp = 62f,
            letterSpacing = 0.04f,
            textColor = dayColorHex,
            shadowColor = 0x55000000.toInt()
        )

        // Date in wide-tracked sans
        val dateBmp = renderText(
            text = dateText,
            typeface = sansMetaTf,
            textSizeDp = 13.5f,
            letterSpacing = 0.18f,
            textColor = timeColorHex
        )

        // Divider Line
        val dividerBmp = Bitmap.createBitmap(140, 5, Bitmap.Config.ARGB_8888).apply {
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

        // 5. Schedule Next Minute Tick via AlarmManager
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
        } catch (_: Exception) {
            alarmManager.set(
                AlarmManager.RTC,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
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
