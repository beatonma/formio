package org.beatonma.gclocks.widget
//
//import android.app.Service
//import android.appwidget.AppWidgetManager
//import android.content.ComponentName
//import android.content.Intent
//import android.os.IBinder
//
//class ClockWidgetService : Service() {
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    override fun onStartCommand(
//        intent: Intent?,
//        flags: Int,
//        startId: Int,
//    ): Int {
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//    private fun updateWidget() {
//        val component = ComponentName(packageName, ClockWidgetReceiver.ClassPath)
//        val widgetManager = AppWidgetManager.getInstance(this)
//
//
//    }
//}