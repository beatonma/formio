package org.beatonma.gclocks.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.color.ColorProvider
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import kotlinx.coroutines.flow.mapLatest
import org.beatonma.gclocks.android.AndroidCanvasHost
import org.beatonma.gclocks.android.AndroidPath
import org.beatonma.gclocks.app.settings.SettingsContext
import org.beatonma.gclocks.app.settings.settingsRepository
import org.beatonma.gclocks.clocks.createAnimatorFromOptions
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.getTime
import kotlin.math.roundToInt


class ClockWidgetReceiver : GlanceAppWidgetReceiver() {
    companion object {
        const val ClassPath = "org.beatonma.gclocks.widget.ClockWidgetReceiver"
    }

    override val glanceAppWidget: GlanceAppWidget = ClockWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        debug("ClockWidgetReceiver.onUpdate")
    }
}

class ClockWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        val density = context.resources.displayMetrics.density
        provideContent {
            val optionsState = context.settingsRepository.load()
                .mapLatest { it.getOptions(SettingsContext.Widget) }
                .collectAsState(null)
            val options = optionsState.value ?: return@provideContent

            val size = LocalSize.current

            Text(
                "Clock!",
                style = TextStyle(
                    color = ColorProvider(Color.Red, Color.Green)
                )
            )

            Image(
                ImageProvider(
                    createBitmap(
                        options,
                        (size.width.value * density).roundToInt(),
                        (size.height.value * density).roundToInt()
                    )
                ),
                contentDescription = "Clock widget"
            )
        }
    }
}


private fun createBitmap(options: Options<*>, width: Int, height: Int): Bitmap {
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