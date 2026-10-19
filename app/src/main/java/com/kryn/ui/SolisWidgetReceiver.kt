package com.kryn.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.VectorDrawable
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import java.util.Calendar

class SolisWidgetReceiver : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        
        // Listen to system minute ticks: update only on :00 and :30 boundaries for smooth daylight shifts
        if (action == Intent.ACTION_TIME_TICK) {
            val minute = Calendar.getInstance().get(Calendar.MINUTE)
            if (minute != 0 && minute != 30) {
                return // Skip unnecessary canvas redraws to save 0% battery
            }
        }

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisWidget = ComponentName(context, SolisWidgetReceiver::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        if (appWidgetIds.isNotEmpty()) {
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // 48-Phase Daylight Calculation (0 to 47)
        val phaseIndex = (hour * 2) + if (minute >= 30) 1 else 0
        val daylightPalette = getDaylightColors(phaseIndex)

        val prefs = context.getSharedPreferences("kryn_solis_prefs", Context.MODE_PRIVATE)
        val customText = prefs.getString("solis_custom_text", "EVELESKE") ?: "EVELESKE"
        val showBackground = prefs.getBoolean("solis_show_bg", true)

        val customTypeface = try {
            Typeface.createFromAsset(context.assets, "fonts/brick_sans.ttf")
        } catch (e: Exception) {
            try {
                Typeface.createFromAsset(context.assets, "fonts/brick_sans.otf")
            } catch (e2: Exception) {
                Typeface.create("sans-serif-condensed", Typeface.BOLD)
            }
        }

        // 1. Crisp anti-aliased subpixel bitmaps
        val textBmp = renderTextBitmap(customText.uppercase(), daylightPalette.accentColor, customTypeface)
        val pillBmp = if (showBackground) renderPillBitmap(daylightPalette.pillColor) else Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val hexBmp = renderHexBitmap(daylightPalette.hexColor)

        // 2. Render Tinted Hand Bitmaps so hands change color with the sun
        val hourHandBmp = renderTintedVector(context, R.drawable.solis_hand_hour, daylightPalette.accentColor)
        val minuteHandBmp = renderTintedVector(context, R.drawable.solis_hand_minute, daylightPalette.accentColor)
        val secondHandBmp = renderTintedVector(context, R.drawable.solis_hand_second, daylightPalette.accentColor)

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_solis)
            views.setImageViewBitmap(R.id.solis_pill_bg_img, pillBmp)
            views.setImageViewBitmap(R.id.solis_custom_text_img, textBmp)
            views.setImageViewBitmap(R.id.solis_hex_bg_img, hexBmp)

            // Dynamic Hand Tinting
            if (hourHandBmp != null) views.setImageViewBitmap(R.id.solis_analog_clock, null) // clear static fallback
            views.setInt(R.id.solis_analog_clock, "setHourHand", R.drawable.solis_hand_hour)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private data class DaylightColors(val pillColor: Int, val hexColor: Int, val accentColor: Int)

    private fun getDaylightColors(index: Int): DaylightColors {
        return when (index) {
            // Midnight & Indigo (00:00 - 04:30)
            in 0..9 -> DaylightColors(0xFF0D0F18.toInt(), 0xFF161926.toInt(), 0xFF818CF8.toInt())
            // Dawn & Sunrise (05:00 - 09:30)
            in 10..19 -> DaylightColors(0xFFF1E7E3.toInt(), 0xFF52323D.toInt(), 0xFFEA580C.toInt())
            // Noon Luminance (10:00 - 14:30)
            in 20..29 -> DaylightColors(0xFFF0F6FF.toInt(), 0xFF232730.toInt(), 0xFF0284C7.toInt())
            // Dusk & Golden Hour (15:00 - 19:30)
            in 30..39 -> DaylightColors(0xFF2B172E.toInt(), 0xFF492452.toInt(), 0xFFF97316.toInt())
            // Nightfall (20:00 - 23:30)
            else -> DaylightColors(0xFF0F0C1B.toInt(), 0xFF1A152F.toInt(), 0xFF6366F1.toInt())
        }
    }

    private fun renderTintedVector(context: Context, drawableId: Int, tintColor: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId)?.mutate() ?: return null
        drawable.setTint(tintColor)
        val density = 3.5f
        val size = (120 * density).toInt()
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun renderTextBitmap(text: String, textColor: Int, tf: Typeface): Bitmap {
        val density = 3.5f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            typeface = tf
            textSize = 22f * density
            letterSpacing = 0.16f
            color = textColor
            textAlign = Paint.Align.LEFT
        }

        val fontMetrics = paint.fontMetrics
        val textWidth = paint.measureText(text)
        val width = (textWidth + 20f * density).toInt().coerceAtLeast(1)
        val height = ((fontMetrics.bottom - fontMetrics.top) + 14f * density).toInt().coerceAtLeast(1)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val yPos = (height / 2f) - ((fontMetrics.ascent + fontMetrics.descent) / 2f)
        canvas.drawText(text, 6f * density, yPos, paint)
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
        val size = (76 * density).toInt()
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
