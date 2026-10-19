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
        // Fixed Dark Black for the clock dock shape
        const val HEX_DARK_BLACK = 0xFF08090D.toInt()
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

        // 1. Slim Text Bitmap calibrated for the reduced capsule height
        val textBmp = renderTextBitmap(customText.uppercase(), palette.accentColor, customTypeface)
        // 2. Slim 34dp Pill
        val pillBmp = if (showBackground) renderPillBitmap(palette.pillColor) else Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        // 3. Dark Black Hexagon with drop shadow
        val hexBmp = renderDarkHexWithShadow(HEX_DARK_BLACK)

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_solis)
            views.setImageViewBitmap(R.id.solis_pill_bg_img, pillBmp)
            views.setImageViewBitmap(R.id.solis_custom_text_img, textBmp)
            views.setImageViewBitmap(R.id.solis_hex_bg_img, hexBmp)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private data class Palette(val pillColor: Int, val accentColor: Int)
    private data class Anchor(val minuteOfDay: Int, val palette: Palette)

    private fun computeInterpolatedPalette(currentMinute: Int): Palette {
        val anchors = listOf(
            // Midnight (02:00)
            Anchor(120,  Palette(0xFF07080F.toInt(), 0xFF818CF8.toInt())),
            // Dawn (06:30) - Luminous dawn mist
            Anchor(390,  Palette(0xFFFFF7ED.toInt(), 0xFFC2410C.toInt())),
            // High Noon (12:30) - Crisp white
            Anchor(750,  Palette(0xFFFFFFFF.toInt(), 0xFF0284C7.toInt())),
            // Golden Sunset (18:30) - Deep sunset wine
            Anchor(1110, Palette(0xFF4A1525.toInt(), 0xFFFDBA74.toInt())),
            // Night (22:00)
            Anchor(1320, Palette(0xFF0F0C1B.toInt(), 0xFF6366F1.toInt())),
            // Cycle Loop
            Anchor(1560, Palette(0xFF07080F.toInt(), 0xFF818CF8.toInt()))
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
            textSize = 15f * density // Calibrated for the slim 34dp capsule
            letterSpacing = 0.16f
            color = textColor
            textAlign = Paint.Align.LEFT
        }

        val fontMetrics = paint.fontMetrics
        val textWidth = paint.measureText(text)
        val width = (textWidth + 18f * density).toInt().coerceAtLeast(32)
        val height = (26f * density).toInt()

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val yPos = (height / 2f) - ((fontMetrics.ascent + fontMetrics.descent) / 2f)
        canvas.drawText(text, 2f * density, yPos, paint)
        return bitmap
    }

    private fun renderPillBitmap(color: Int): Bitmap {
        val density = 3.5f
        val width = (320 * density).toInt()
        val height = (34 * density).toInt() // Exactly 1/2 height
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

    private fun renderDarkHexWithShadow(color: Int): Bitmap {
        val density = 3.5f
        val size = (72 * density).toInt()
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val cx = size / 2f
        val cy = size / 2f
        val radius = (30 * density)

        // Multi-stage elevation drop shadow (dark feathering cast over the capsule)
        for (i in 5 downTo 1) {
            val shadowPath = Path()
            val shadowRadius = radius + (i * 1.6f * density)
            val shadowCy = cy + (i * 1.4f * density)
            for (j in 0 until 6) {
                val angle = Math.toRadians((60 * j - 30).toDouble())
                val x = (cx + shadowRadius * Math.cos(angle)).toFloat()
                val y = (shadowCy + shadowRadius * Math.sin(angle)).toFloat()
                if (j == 0) shadowPath.moveTo(x, y) else shadowPath.lineTo(x, y)
            }
            shadowPath.close()

            val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = Color.argb(22 / i, 0, 0, 0)
                style = Paint.Style.FILL
            }
            canvas.drawPath(shadowPath, shadowPaint)
        }

        // Solid Dark Black Hexagon base
        val hexPath = Path()
        for (i in 0 until 6) {
            val angle = Math.toRadians((60 * i - 30).toDouble())
            val x = (cx + radius * Math.cos(angle)).toFloat()
            val y = (cy + radius * Math.sin(angle)).toFloat()
            if (i == 0) hexPath.moveTo(x, y) else hexPath.lineTo(x, y)
        }
        hexPath.close()

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.FILL
            isDither = true
        }
        canvas.drawPath(hexPath, fillPaint)

        return bitmap
    }
}
