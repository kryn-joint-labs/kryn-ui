package com.kryn.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.*
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LunarWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val now = Date()

        val dayText = SimpleDateFormat("EEEE", Locale.US).format(now).uppercase()
        val dateText = SimpleDateFormat("d MMMM, yyyy.", Locale.US).format(now).uppercase()
        val timeText = SimpleDateFormat("- hh:mm a -", Locale.US).format(now).uppercase()

        // Load Anurati & Aquatico directly from assets
        val anuratiTf = try {
            Typeface.createFromAsset(context.assets, "fonts/anurati.otf")
        } catch (e: Exception) {
            Typeface.DEFAULT_BOLD
        }

        val aquaticoTf = try {
            Typeface.createFromAsset(context.assets, "fonts/aquatico.otf")
        } catch (e: Exception) {
            Typeface.SANS_SERIF
        }

        // Render crisp, high-DPI text bitmaps with single-line width protection
        val dayBmp = renderTextToBitmap(
            text = dayText,
            typeface = anuratiTf,
            textSizeDp = 30f,
            letterSpacing = 0.22f,
            textColor = Color.WHITE,
            shadowColor = 0x99000000.toInt()
        )

        val dateBmp = renderTextToBitmap(
            text = dateText,
            typeface = aquaticoTf,
            textSizeDp = 12f,
            letterSpacing = 0.12f,
            textColor = 0xE6FFFFFF.toInt(),
            shadowColor = 0x80000000.toInt()
        )

        val timeBmp = renderTextToBitmap(
            text = timeText,
            typeface = aquaticoTf,
            textSizeDp = 11f,
            letterSpacing = 0.08f,
            textColor = 0xB3FFFFFF.toInt(),
            shadowColor = 0x80000000.toInt()
        )

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_lunar)
            views.setImageViewBitmap(R.id.mond_day_img, dayBmp)
            views.setImageViewBitmap(R.id.mond_date_img, dateBmp)
            views.setImageViewBitmap(R.id.mond_time_img, timeBmp)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun renderTextToBitmap(
        text: String,
        typeface: Typeface,
        textSizeDp: Float,
        letterSpacing: Float,
        textColor: Int,
        shadowColor: Int
    ): Bitmap {
        val density = 3.0f // High-density scaling for sharp rendering
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            this.textSize = textSizeDp * density
            this.letterSpacing = letterSpacing
            this.color = textColor
            this.textAlign = Paint.Align.CENTER
            setShadowLayer(3f * density, 0f, 1.5f * density, shadowColor)
        }

        val fontMetrics = paint.fontMetrics
        val textWidth = paint.measureText(text)
        val width = (textWidth + 20f * density).toInt().coerceAtLeast(1)
        val height = ((fontMetrics.bottom - fontMetrics.top) + 12f * density).toInt().coerceAtLeast(1)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val xPos = width / 2f
        val yPos = (height / 2f) - ((fontMetrics.ascent + fontMetrics.descent) / 2f)

        canvas.drawText(text, xPos, yPos, paint)
        return bitmap
    }
}
