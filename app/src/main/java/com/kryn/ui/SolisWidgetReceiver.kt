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
import androidx.core.graphics.ColorUtils
import java.util.Calendar

class SolisWidgetReceiver : AppWidgetProvider() {

    companion object {
        const val ACTION_SOLIS_TICK = "com.kryn.ui.ACTION_SOLIS_TICK"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisWidget = ComponentName(context, SolisWidgetReceiver::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        if (appWidgetIds.isNotEmpty()) {
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        scheduleNextDaylightTick(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelDaylightTick(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        scheduleNextDaylightTick(context)

        val calendar = Calendar.getInstance()
        val totalMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

        // Mathematical continuous gradient interpolation across the 24-hour cycle
        val palette = computeInterpolatedPalette(totalMinutes)

        val prefs = context.getSharedPreferences("kryn_solis_prefs", Context.MODE_PRIVATE)
        val customText = prefs.getString("solis_custom_text", "EVELESKE") ?: "EVELESKE"
        val showBackground = prefs.getBoolean("solis_show_bg", true)

        val customTypeface = try {
            Typeface.createFromAsset(context.assets, "fonts/brick_sans.ttf")
        } catch (_: Exception) {
            try {
                Typeface.createFromAsset(context.assets, "fonts/brick_sans.otf")
            } catch (_: Exception) {
                Typeface.create("sans-serif-condensed", Typeface.BOLD)
            }
        }

        // Render anti-aliased subpixel bitmaps
        val textBmp = renderTextBitmap(customText.uppercase(), palette.accentColor, customTypeface)
        val pillBmp = if (showBackground) renderPillBitmap(palette.pillColor) else Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val hexBmp = renderHexBitmap(palette.hexColor)

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_solis)
            views.setImageViewBitmap(R.id.solis_pill_bg_img, pillBmp)
            views.setImageViewBitmap(R.id.solis_custom_text_img, textBmp)
            views.setImageViewBitmap(R.id.solis_hex_bg_img, hexBmp)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private data class Palette(val pillColor: Int, val hexColor: Int, val accentColor: Int)
    private data class Anchor(val minuteOfDay: Int, val palette: Palette)

    private fun computeInterpolatedPalette(currentMinute: Int): Palette {
        // 6 Peak Anchors across the 24-hour cycle (1440 minutes)
        val anchors = listOf(
            Anchor(120,  Palette(0xFF07080F.toInt(), 0xFF0B0C15.toInt(), 0xFF4F46E5.toInt())), // 02:00 Midnight
            Anchor(360,  Palette(0xFF34223E.toInt(), 0xFF572F55.toInt(), 0xFFFB7185.toInt())), // 06:00 Sunrise
            Anchor(540,  Palette(0xFFDCE8F7.toInt(), 0xFF4D5C7A.toInt(), 0xFF0284C7.toInt())), // 09:00 Morning
            Anchor(750,  Palette(0xFFFFFFFF.toInt(), 0xFF232730.toInt(), 0xFFFBBF24.toInt())), // 12:30 Apex Noon
            Anchor(1080, Palette(0xFF371F32.toInt(), 0xFF69354E.toInt(), 0xFFF97316.toInt())), // 18:00 Golden Hour
            Anchor(1320, Palette(0xFF0F0C1B.toInt(), 0xFF1A152F.toInt(), 0xFF7C3AED.toInt())), // 22:00 Twilight Night
            Anchor(1560, Palette(0xFF07080F.toInt(), 0xFF0B0C15.toInt(), 0xFF4F46E5.toInt()))  // 26:00 (Wraps to 02:00)
        )

        val adjustedMinute = if (currentMinute < 120) currentMinute + 1440 else currentMinute

        var fromAnchor = anchors[0]
        var toAnchor = anchors[1]

        for (i in 0 until anchors.size - 1) {
            if (adjustedMinute >= anchors[i].minuteOfDay && adjustedMinute <= anchors[i + 1].minuteOfDay) {
                fromAnchor = anchors[i]
                toAnchor = anchors[i + 1]
                break
            }
        }

        val range = (toAnchor.minuteOfDay - fromAnchor.minuteOfDay).toFloat().coerceAtLeast(1f)
        val ratio = ((adjustedMinute - fromAnchor.minuteOfDay) / range).coerceIn(0f, 1f)

        return Palette(
            pillColor = ColorUtils.blendARGB(fromAnchor.palette.pillColor, toAnchor.palette.pillColor, ratio),
            hexColor = ColorUtils.blendARGB(fromAnchor.palette.hexColor, toAnchor.palette.hexColor, ratio),
            accentColor = ColorUtils.blendARGB(fromAnchor.palette.accentColor, toAnchor.palette.accentColor, ratio)
        )
    }

    private fun scheduleNextDaylightTick(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, SolisWidgetReceiver::class.java).apply {
            action = ACTION_SOLIS_TICK
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            2001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance()
        val minute = calendar.get(Calendar.MINUTE)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (minute < 30) {
            calendar.set(Calendar.MINUTE, 30)
        } else {
            calendar.add(Calendar.HOUR_OF_DAY, 1)
            calendar.set(Calendar.MINUTE, 0)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (_: Exception) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun cancelDaylightTick(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, SolisWidgetReceiver::class.java).apply {
            action = ACTION_SOLIS_TICK
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            2001,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun renderTextBitmap(text: String, textColor: Int, tf: Typeface): Bitmap {
        val density = 3.5f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            typeface = tf
            textSize = 21f * density
            letterSpacing = 0.16f
            color = textColor
            textAlign = Paint.Align.LEFT
        }

        val fontMetrics = paint.fontMetrics
        val textWidth = paint.measureText(text)
        val width = (textWidth + 24f * density).toInt().coerceAtLeast(32)
        val height = ((fontMetrics.bottom - fontMetrics.top) + 12f * density).toInt().coerceAtLeast(16)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val yPos = (height / 2f) - ((fontMetrics.ascent + fontMetrics.descent) / 2f)
        canvas.drawText(text, 4f * density, yPos, paint)
        return bitmap
    }

    private fun renderPillBitmap(color: Int): Bitmap {
        val density = 3.5f
        val width = (320 * density).toInt()
        val height = (64 * density).toInt()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.FILL
            isDither = true
        }

        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rect, height / 2f, height / 2f, paint)
        return bitmap
    }

    private fun renderHexBitmap(color: Int): Bitmap {
        val density = 3.5f
        val size = (68 * density).toInt()
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.FILL
            isDither = true
        }

        val cx = size / 2f
        val cy = size / 2f
        val radius = (size / 2f) * 0.94f

        val path = Path()
        for (i in 0 until 6) {
            val angle = Math.toRadians((60 * i - 30).toDouble())
            val x = (cx + radius * Math.cos(angle)).toFloat()
            val y = (cy + radius * Math.sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        canvas.drawPath(path, paint)
        return bitmap
    }
}
