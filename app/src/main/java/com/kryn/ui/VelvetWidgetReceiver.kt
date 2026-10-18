package com.kryn.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VelvetWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val now = Date()

        val dayText = SimpleDateFormat("EEEE", Locale.US).format(now)
        val dateText = SimpleDateFormat("d MMMM yyyy", Locale.US).format(now).uppercase()

        // Detect Wallpaper / System Dark vs Light theme tone
        val isDarkMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        val baseAccentColor = if (isDarkMode) Color.WHITE else 0xFF111218.toInt()
        val dividerColor = if (isDarkMode) 0x66FFFFFF.toInt() else 0x44000000.toInt()

        // Load custom fonts safely from assets
        val scriptTf = try {
            Typeface.createFromAsset(context.assets, "fonts/script_day.otf")
        } catch (e: Exception) {
            Typeface.DEFAULT_BOLD
        }

        val sansMetaTf = try {
            Typeface.createFromAsset(context.assets, "fonts/sans_meta.ttf")
        } catch (e: Exception) {
            Typeface.SANS_SERIF
        }

        // 1. Hero Script Day (Permanently lavender-purple #B84DFF)
        val dayBmp = renderText(
            text = dayText,
            typeface = scriptTf,
            textSizeDp = 48f,
            letterSpacing = 0.05f,
            textColor = 0xFFB84DFF.toInt(),
            shadowColor = 0x66000000.toInt()
        )

        // 2. Date (Clean, wide-tracked sans)
        val dateBmp = renderText(
            text = dateText,
            typeface = sansMetaTf,
            textSizeDp = 13.5f,
            letterSpacing = 0.18f,
            textColor = baseAccentColor,
            shadowColor = if (isDarkMode) 0x66000000.toInt() else 0
        )

        // 3. Divider Bitmap
        val dividerBmp = Bitmap.createBitmap(120, 4, Bitmap.Config.ARGB_8888).apply {
            eraseColor(dividerColor)
        }

        // 4. Update RemoteViews (TextClock handles time automatically)
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_velvet)
            views.setImageViewBitmap(R.id.velvet_day_img, dayBmp)
            views.setImageViewBitmap(R.id.velvet_divider_img, dividerBmp)
            views.setImageViewBitmap(R.id.velvet_date_img, dateBmp)
            appWidgetManager.updateAppWidget(appWidgetId, views)
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
                setShadowLayer(3f * density, 0f, 1.5f * density, shadowColor)
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
