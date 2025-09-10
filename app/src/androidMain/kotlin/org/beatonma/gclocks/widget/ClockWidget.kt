package org.beatonma.gclocks.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms
import org.beatonma.gclocks.android.AndroidCanvasHost
import org.beatonma.gclocks.android.AndroidPath
import org.beatonma.gclocks.android.AppContext
import org.beatonma.gclocks.android.alarmManager
import org.beatonma.gclocks.android.appContext
import org.beatonma.gclocks.android.componentNameOf
import org.beatonma.gclocks.android.widgetManager
import org.beatonma.gclocks.clocks.createAnimatorFromOptions
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.getTime
import org.beatonma.gclocks.core.util.warn
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.reflect.KClass


interface ClockWidget {
    fun enableUpdates(context: AppContext) {
        scheduleNextUpdate(context)
    }

    fun cancelUpdates(context: AppContext) {
        val pendingIntent = getBroadcastIntent(
            context,
            Intent(context.value, this::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
        )
        context.alarmManager.cancel(pendingIntent)
        debug("Cancelled update schedule.")
    }

    /**
     * Call from onReceive(Context, Intent) in implementing AppWidgetProvider.
     */
    fun onReceive(context: Context, intent: Intent) {
        debug("handleReceivedIntent: ${this::class.java} ${intent.action}")
        when (intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE,
            Intent.ACTION_USER_PRESENT,
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED,
                -> {
                enableUpdates(context.appContext)
            }
        }
    }

    private fun scheduleNextUpdate(context: AppContext) {
        val nextMinute = LocalDateTime.now().plusMinutes(1).withSecond(0).withNano(0)
        scheduleUpdateAt(context, nextMinute)
    }

    private fun scheduleUpdateAt(context: AppContext, time: LocalDateTime) {
        // There should never be more than one update scheduled at once.
        cancelUpdates(context)

        val alarmManager = context.alarmManager.also {
            if (!canScheduleExactAlarms(it)) {
                warn("Cannot schedule alarm: Permission SCHEDULE_EXACT_ALARM has not been granted")
                return
            }
        }
        val widgetIds: IntArray = getWidgetIds(context).also {
            if (it.isEmpty()) {
                debug("Aborting scheduleNextUpdate: There are no active widgets to update.")
                return
            }
        }

        val timeEpochMillis = time.atZone(ZoneOffset.systemDefault()).toEpochSecond() * 1000L
        val intent = Intent(context.value, this::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        }
        val pendingIntent = getBroadcastIntent(context, intent)
        alarmManager.setExact(AlarmManager.RTC, timeEpochMillis, pendingIntent)
        debug("Update scheduled for $time | $timeEpochMillis")
    }

    private fun getWidgetIds(context: AppContext): IntArray {
        return context.widgetManager.getAppWidgetIds(context.componentNameOf(this::class))
    }

    companion object {
        fun createBitmap(options: Options<*>, width: Int, height: Int): Bitmap {
            val animator = createAnimatorFromOptions(options, AndroidPath()) {
                // no frame scheduling needed
            }

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvasHandler = AndroidCanvasHost()

            canvasHandler.withCanvas(Canvas(bitmap)) { canvas ->
                animator.renderOnce(
                    canvas,
                    width.toFloat(),
                    height.toFloat(),
                    getTime().copy(millisecond = 0)
                )
            }

            return bitmap
        }

        fun refreshWidgets(context: AppContext, widgetProviderClass: KClass<*>) {
            getBroadcastIntent(
                context,
                Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                    val widgetIds = context.widgetManager.getAppWidgetIds(
                        context.componentNameOf(widgetProviderClass)
                    )
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
                }
            ).send()
        }
    }
}


private fun getBroadcastIntent(context: AppContext, intent: Intent) = PendingIntent.getBroadcast(
    context.value, 0, intent,
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
)