package org.beatonma.gclocks.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.beatonma.R
import org.beatonma.gclocks.android.AppContext
import org.beatonma.gclocks.android.appContext
import org.beatonma.gclocks.app.data.AppSettingsRepository
import org.beatonma.gclocks.app.data.deserialize
import org.beatonma.gclocks.app.data.loadWidgetSettings
import org.beatonma.gclocks.app.data.serialize
import org.beatonma.gclocks.app.data.settings.DefaultAppSettings
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.data.settingsRepository
import org.beatonma.gclocks.core.util.debug


class ClockWidgetProvider : AppWidgetProvider(), ClockWidget {
    @SuppressLint("UnsafeProtectedBroadcastReceiver") // Intent action handled in super calls.
    override fun onReceive(context: Context, intent: Intent) {
        super<ClockWidget>.onReceive(context, intent)
        super<AppWidgetProvider>.onReceive(context, intent)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        enableUpdates(context.appContext)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelUpdates(context.appContext)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)

        val repository = context.settingsRepository
        runBlocking {
            appWidgetIds.forEach { id ->
                repository.forgetString(WidgetSize.key(id))
            }
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle,
    ) {
        val size = WidgetSize(
            minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH),
            minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT),
            maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH),
            maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT),
        )
        runBlocking {
            size.save(context.settingsRepository, appWidgetId)
        }
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        refreshWidgets(context.appContext)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val repository = context.settingsRepository
        val widgetSettings = runBlocking {
            repository.loadWidgetSettings().firstOrNull()
                ?: DefaultAppSettings.getContextOptions(DisplayContext.Widget)
        }
        val remoteViews = RemoteViews(context.packageName, R.layout.widget)

        appWidgetIds.forEach { widgetId ->
            val size: WidgetSize = runBlocking {
                WidgetSize.load(repository, widgetId) ?: WidgetSize.Default
            }
            val bitmap = ClockWidget.createBitmap(
                widgetSettings.clockOptions,
                size.maxWidth,
                size.maxHeight
            )
            remoteViews.setImageViewBitmap(R.id.image, bitmap)
            appWidgetManager.updateAppWidget(widgetId, remoteViews)
            debug("Updated widget #$widgetId")
        }
    }

    companion object {
        fun refreshWidgets(context: AppContext) {
            ClockWidget.refreshWidgets(context, ClockWidgetProvider::class)
        }
    }
}


@Serializable
private data class WidgetSize(
    val minWidth: Int,
    val minHeight: Int,
    val maxWidth: Int,
    val maxHeight: Int,
) {
    suspend fun save(repository: AppSettingsRepository, widgetId: Int) {
        repository.save(key(widgetId), serialize(this))
    }

    companion object {
        fun key(widgetId: Int) = "widgetsize_$widgetId"

        suspend fun load(repository: AppSettingsRepository, widgetId: Int): WidgetSize? {
            repository.loadString(key(widgetId)).firstOrNull()?.let {
                return deserialize<WidgetSize>(it)
            }
            return null
        }

        val Default get() = WidgetSize(128, 128, 128, 128)
    }
}
