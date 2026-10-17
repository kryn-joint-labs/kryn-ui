package com.kryn.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
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

        // 1. Convert Day into spaced uppercase: "F R I D A Y"
        val rawDay = SimpleDateFormat("EEEE", Locale.US).format(now).uppercase()
        val spacedDay = rawDay.toCharArray().joinToString(" ")

        // 2. Format Date: "17 OCTOBER, 2026."
        val formattedDate = SimpleDateFormat("d MMMM, yyyy.", Locale.US).format(now).uppercase()

        // 3. Format Time: "- 03:15 PM -"
        val formattedTime = SimpleDateFormat("'- 'hh:mm a' -'", Locale.US).format(now).uppercase()

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_lunar)
            views.setTextViewText(R.id.mond_day, spacedDay)
            views.setTextViewText(R.id.mond_date, formattedDate)
            views.setTextViewText(R.id.mond_time, formattedTime)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
