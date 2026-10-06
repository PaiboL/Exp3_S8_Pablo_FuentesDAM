package cl.duoc.senalsegura.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import cl.duoc.senalsegura.MainActivity
import cl.duoc.senalsegura.R
import cl.duoc.senalsegura.data.AlertRepository

class SignalSafeWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val emergencyAlert = AlertRepository.alerts.first()
        val openAppIntent = Intent(context, MainActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        appWidgetIds.forEach { widgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget_signal_safe).apply {
                setTextViewText(R.id.widget_alert_title, emergencyAlert.title)
                setTextViewText(R.id.widget_alert_message, emergencyAlert.visibleMessage())
                setOnClickPendingIntent(R.id.widget_open_button, openAppPendingIntent)
            }
            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}
