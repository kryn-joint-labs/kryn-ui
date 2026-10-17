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

        // 1. Hero Day Bitmap (Wide, prominent stencil)
        val dayBmp = renderTextToBitmap(
            text = dayText,
            typeface = anuratiTf,
            textSizeDp = 32f,
            letterSpacing = 0.24f,
            textColor = Color.WHITE,
            shadowColor = 0xAA000000.toInt()
        )

        // 2. Date Bitmap (Scaled down, elegant, delicate proportion)
        val dateBmp = renderTextToBitmap(
            text = dateText,
            typeface = aquaticoTf,
            textSizeDp = 5.5f,
            letterSpacing = 0.26f,
            textColor = 0xE6FFFFFF.toInt(),
            shadowColor = 0x88000000.toInt()
        )

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_lunar)
            views.setImageViewBitmap(R.id.mond_day_img, dayBmp)
            views.setImageViewBitmap(R.id.mond_date_img, dateBmp)
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
        val density = 3.0f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            this.textSize = textSizeDp * density
            this.letterSpacing = letterSpacing
            this.color = textColor
            this.textAlign = Paint.Align.CENTER
            setShadowLayer(3.5f * density, 0f, 1.5f * density, shadowColor)
        }

        val fontMetrics = paint.fontMetrics
        val textWidth = paint.measureText(text)
        val width = (textWidth + 24f * density).toInt().coerceAtLeast(1)
        val height = ((fontMetrics.bottom - fontMetrics.top) + 10f * density).toInt().coerceAtLeast(1)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val xPos = width / 2f
        val yPos = (height / 2f) - ((fontMetrics.ascent + fontMetrics.descent) / 2f)

        canvas.drawText(text, xPos, yPos, paint)
        return bitmap
    }
}
