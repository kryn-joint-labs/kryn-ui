package com.kryn.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.widget.RemoteViews
import java.util.Calendar

class SolisWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // NAP Engine: Calculate the 30-minute progression phase (0 to 47)
        val phaseIndex = (hour * 2) + if (minute >= 30) 1 else 0

        // 48-Phase Daylight Chromatic Matrix (Pill Color, Hex Color, Accent Color)
        val daylightPalette = getDaylightColors(phaseIndex)

        // Pull Custom Text from SharedPreferences
        val prefs = context.getSharedPreferences("kryn_solis_prefs", Context.MODE_PRIVATE)
        val customText = prefs.getString("solis_custom_text", "KRYN // 01") ?: "KRYN // 01"
        val showBackground = prefs.getBoolean("solis_show_bg", true)

        // Render Custom Text Bitmap in Accent Color
        val textBmp = renderTextBitmap(
            text = customText.uppercase(),
            textColor = daylightPalette.accentColor
        )

        // Generate Tinted Pill Background
        val pillBmp = if (showBackground) {
            renderPillBitmap(daylightPalette.pillColor)
        } else {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }

        // Generate Tinted Hexagon Background
        val hexBmp = renderHexBitmap(daylightPalette.hexColor)

        // Push directly to RemoteViews
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_solis)
            views.setImageViewBitmap(R.id.solis_pill_bg_img, pillBmp)
            views.setImageViewBitmap(R.id.solis_custom_text_img, textBmp)
            views.setImageViewBitmap(R.id.solis_hex_bg_img, hexBmp)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private data class DaylightColors(val pillColor: Int, val hexColor: Int, val accentColor: Int)

    private fun getDaylightColors(index: Int): DaylightColors {
        // Complete 48 chromatic Daylight color steps
        return when (index) {
            // Midnight & Cosmic Indigo (00:00 - 04:30)
            0 -> DaylightColors(0xFF0D0F18.toInt(), 0xFF161926.toInt(), 0xFF818CF8.toInt())
            1 -> DaylightColors(0xFF0C0E17.toInt(), 0xFF141724.toInt(), 0xFF7C83FD.toInt())
            2 -> DaylightColors(0xFF0A0B13.toInt(), 0xFF11131E.toInt(), 0xFF6366F1.toInt())
            3 -> DaylightColors(0xFF090A11.toInt(), 0xFF0E101A.toInt(), 0xFF5B5EE0.toInt())
            4 -> DaylightColors(0xFF07080F.toInt(), 0xFF0B0C15.toInt(), 0xFF4F46E5.toInt())
            5 -> DaylightColors(0xFF070810.toInt(), 0xFF0C0D18.toInt(), 0xFF5850EC.toInt())
            6 -> DaylightColors(0xFF090A13.toInt(), 0xFF0F111D.toInt(), 0xFF6366F1.toInt())
            7 -> DaylightColors(0xFF0B0C18.toInt(), 0xFF131525.toInt(), 0xFF7075F7.toInt())
            8 -> DaylightColors(0xFF0E1020.toInt(), 0xFF181B2E.toInt(), 0xFF818CF8.toInt())
            9 -> DaylightColors(0xFF121427.toInt(), 0xFF1F223B.toInt(), 0xFF939BF9.toInt())

            // Dawn & Sunrise (05:00 - 09:30)
            10 -> DaylightColors(0xFF181932.toInt(), 0xFF2A2749.toInt(), 0xFFC084FC.toInt())
            11 -> DaylightColors(0xFF241E38.toInt(), 0xFF3D2D52.toInt(), 0xFFE879F9.toInt())
            12 -> DaylightColors(0xFF34223E.toInt(), 0xFF572F55.toInt(), 0xFFFB7185.toInt())
            13 -> DaylightColors(0xFF45293E.toInt(), 0xFF6B3751.toInt(), 0xFFF43F5E.toInt())
            14 -> DaylightColors(0xFF52323D.toInt(), 0xFF7C4148.toInt(), 0xFFFB923C.toInt())
            15 -> DaylightColors(0xFFF1E7E3.toInt(), 0xFF8C4C4A.toInt(), 0xFFEA580C.toInt())
            16 -> DaylightColors(0xFFE5ECF6.toInt(), 0xFF785966.toInt(), 0xFFE11D48.toInt())
            17 -> DaylightColors(0xFFDAE5F4.toInt(), 0xFF60647D.toInt(), 0xFFD97706.toInt())
            18 -> DaylightColors(0xFFD4E3F8.toInt(), 0xFF4B5978.toInt(), 0xFFB45309.toInt())
            19 -> DaylightColors(0xFFE0ECFA.toInt(), 0xFF3C4E6D.toInt(), 0xFF0284C7.toInt())

            // High Noon & Solar Luminance (10:00 - 14:30)
            20 -> DaylightColors(0xFFEAF2FC.toInt(), 0xFF324563.toInt(), 0xFF0369A1.toInt())
            21 -> DaylightColors(0xFFF1F6FD.toInt(), 0xFF2D3E57.toInt(), 0xFF0284C7.toInt())
            22 -> DaylightColors(0xFFF7FAFE.toInt(), 0xFF2B3648.toInt(), 0xFF0EA5E9.toInt())
            23 -> DaylightColors(0xFFFAFCFF.toInt(), 0xFF262F3D.toInt(), 0xFFEAB308.toInt())
            24 -> DaylightColors(0xFFFFFFFF.toInt(), 0xFF232730.toInt(), 0xFFFBBF24.toInt()) // Apex Noon
            25 -> DaylightColors(0xFFFFFFFF.toInt(), 0xFF252830.toInt(), 0xFFFACC15.toInt())
            26 -> DaylightColors(0xFFFDFFFF.toInt(), 0xFF282A33.toInt(), 0xFFF59E0B.toInt())
            27 -> DaylightColors(0xFFF8FBFE.toInt(), 0xFF2B2D38.toInt(), 0xFF38BDF8.toInt())
            28 -> DaylightColors(0xFFF2F8FE.toInt(), 0xFF2E313D.toInt(), 0xFF0EA5E9.toInt())
            29 -> DaylightColors(0xFFEDF4FD.toInt(), 0xFF323644.toInt(), 0xFF0284C7.toInt())

            // Golden Hour & Sunset (15:00 - 19:30)
            30 -> DaylightColors(0xFFE5F0FA.toInt(), 0xFF363C4D.toInt(), 0xFFF59E0B.toInt())
            31 -> DaylightColors(0xFFE2EDF8.toInt(), 0xFF3D4353.toInt(), 0xFFD97706.toInt())
            32 -> DaylightColors(0xFFDEE6F2.toInt(), 0xFF464654.toInt(), 0xFFEA580C.toInt())
            33 -> DaylightColors(0xFFDFDFEE.toInt(), 0xFF524653.toInt(), 0xFFF97316.toInt())
            34 -> DaylightColors(0xFFE2D9E4.toInt(), 0xFF5F4550.toInt(), 0xFFFB923C.toInt())
            35 -> DaylightColors(0xFF452A36.toInt(), 0xFF70404C.toInt(), 0xFFF43F5E.toInt())
            36 -> DaylightColors(0xFF371F32.toInt(), 0xFF69354E.toInt(), 0xFFE11D48.toInt())
            37 -> DaylightColors(0xFF2B172E.toInt(), 0xFF5A2C51.toInt(), 0xFFC026D3.toInt())
            38 -> DaylightColors(0xFF201229.toInt(), 0xFF492452.toInt(), 0xFFA855F7.toInt())
            39 -> DaylightColors(0xFF180F25.toInt(), 0xFF391F4F.toInt(), 0xFF9333EA.toInt())

            // Twilight & Nightfall (20:00 - 23:30)
            40 -> DaylightColors(0xFF140D22.toInt(), 0xFF2D1C48.toInt(), 0xFF8B5CF6.toInt())
            41 -> DaylightColors(0xFF120C1F.toInt(), 0xFF25193F.toInt(), 0xFF7C3AED.toInt())
            42 -> DaylightColors(0xFF100C1D.toInt(), 0xFF1F1737.toInt(), 0xFF6D28D9.toInt())
            43 -> DaylightColors(0xFF0F0C1B.toInt(), 0xFF1A152F.toInt(), 0xFF6366F1.toInt())
            44 -> DaylightColors(0xFF0E0C1A.toInt(), 0xFF17152A.toInt(), 0xFF4F46E5.toInt())
            45 -> DaylightColors(0xFF0D0C19.toInt(), 0xFF161427.toInt(), 0xFF4338CA.toInt())
            46 -> DaylightColors(0xFF0D0D19.toInt(), 0xFF161526.toInt(), 0xFF3730A3.toInt())
            47 -> DaylightColors(0xFF0C0E18.toInt(), 0xFF151726.toInt(), 0xFF6366F1.toInt())
            else -> DaylightColors(0xFFFFFFFF.toInt(), 0xFF232730.toInt(), 0xFFFBBF24.toInt())
        }
    }

    private fun renderTextBitmap(text: String, textColor: Int): Bitmap {
        val density = 3.0f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
            textSize = 21f * density
            letterSpacing = 0.22f
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
        canvas.drawText(text, 0f, yPos, paint)
        return bitmap
    }

    private fun renderPillBitmap(color: Int): Bitmap {
        val density = 3.0f
        val width = (300 * density).toInt()
        val height = (64 * density).toInt()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.FILL
        }

        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rect, 999f, 999f, paint)
        return bitmap
    }

    private fun renderHexBitmap(color: Int): Bitmap {
        val density = 3.0f
        val size = (76 * density).toInt()
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.FILL
        }

        val cx = size / 2f
        val cy = size / 2f
        val radius = size / 2f

        val path = Path()
        for (i in 0 until 6) {
            val angle = Math.toRadians((60 * i).toDouble())
            val x = (cx + radius * Math.cos(angle)).toFloat()
            val y = (cy + radius * Math.sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        canvas.drawPath(path, paint)
        return bitmap
    }
}
