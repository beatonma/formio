package org.beatonma.gclocks.app.settings

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.geometry.MutableRectF
import org.beatonma.gclocks.core.geometry.RectF
import org.beatonma.gclocks.core.graphics.Color

private val DefaultPosition: RectF = MutableRectF(0f, 0f, 1f, 1f).inset(0.0f).toRect()

actual enum class DisplayContext {
    Widget {
        override fun defaultOptions(): Options.Widget = Options.Widget
    },
    LiveWallpaper {
        override fun defaultOptions(): Options.Wallpaper = Options.Wallpaper()
    },
    Screensaver {
        override fun defaultOptions(): Options.Screensaver = Options.Screensaver()
    },
    ;

    actual abstract fun defaultOptions(): Options

    @Serializable
    actual sealed interface Options {
        @Serializable
        object Widget : Options

        @Serializable
        data class Wallpaper(
            val backgroundColor: Color = Color.Blue,
            val position: RectF = DefaultPosition,
        ) : Options

        @Serializable
        data class Screensaver(
            val backgroundColor: Color = Color.Green,
            val position: RectF = DefaultPosition,
        ) : Options
    }
}