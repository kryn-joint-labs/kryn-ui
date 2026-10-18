package com.kryn.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
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

        // Load script font safely from assets
        val scriptTf = try {
            Typeface.createFromAsset(context.assets, "fonts/script_day.otf")
        } catch (e: Exception) {
            Typeface.DEFAULT_BOLD
        }

        // Hero Script Day (Permanently lavender-purple #B84DFF with subtle shadow)
        val density = 3.0f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = scriptTf
            this.textSize = 52f * density
            this.letterSpacing = 0.04f
            this.color = 0xFFB84DFF.toInt()
            this.textAlign = Paint.Align.CENTER
            setShadowLayer(4f * density, 0f, 2f * density, 0x55000000.toInt())
        }

        val fontMetrics = paint.fontMetrics
        val textWidth = paint.measureText(dayText)
        val width = (textWidth + 32f * density).toInt().coerceAtLeast(32)
        val height = ((fontMetrics.bottom - fontMetrics.top) + 16f * density).toInt().coerceAtLeast(16)

        val dayBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(dayBmp)
        val xPos = width / 2f
        val yPos = (height / 2f) - ((fontMetrics.ascent + fontMetrics.descent) / 2f)
        canvas.drawText(dayText, xPos, yPos, paint)

        // Push to RemoteViews (Hardware TextClocks handle time/date/contrast automatically)
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_velvet)
            views.setImageViewBitmap(R.id.velvet_day_img, dayBmp)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
