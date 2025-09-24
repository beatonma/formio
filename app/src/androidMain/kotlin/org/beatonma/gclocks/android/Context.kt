package org.beatonma.gclocks.android

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import kotlin.reflect.KClass


/** Wrapper for [Context] which is always references [Context.applicationContext]
 * and not any particular service or activity. */
@JvmInline
value class AppContext private constructor(val value: Context) {
    internal companion object {
        fun of(context: Context) = AppContext(context.applicationContext)
    }
}

val Context.appContext: AppContext get() = AppContext.of(this)

val Context.alarmManager: AlarmManager inline get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager
val AppContext.alarmManager: AlarmManager inline get() = value.getSystemService(Context.ALARM_SERVICE) as AlarmManager

val Context.widgetManager: AppWidgetManager inline get() = AppWidgetManager.getInstance(this)
val AppContext.widgetManager: AppWidgetManager inline get() = AppWidgetManager.getInstance(value)

fun Context.componentNameOf(cls: KClass<*>): ComponentName = ComponentName(this, cls.java)
fun AppContext.componentNameOf(cls: KClass<*>): ComponentName = ComponentName(this.value, cls.java)

fun AppContext.getBroadcastPendingIntent(intent: Intent): PendingIntent {
    return PendingIntent.getBroadcast(
        this.value, 0, intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )
}
