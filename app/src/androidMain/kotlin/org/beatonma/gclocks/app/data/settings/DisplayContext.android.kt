package org.beatonma.gclocks.app.data.settings

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.geometry.RectF
import org.beatonma.gclocks.core.graphics.Color

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
        actual sealed interface WithBackground : Options {
            actual val backgroundColor: Color
            actual val position: RectF
        }

        @Serializable
        object Widget : Options

        @Serializable
        data class Wallpaper(
            override val backgroundColor: Color = DisplayContextDefaults.DefaultBackgroundColor,
            override val position: RectF = DisplayContextDefaults.DefaultPosition,
        ) : WithBackground

        @Serializable
        data class Screensaver(
            override val backgroundColor: Color = DisplayContextDefaults.DefaultBackgroundColor,
            override val position: RectF = DisplayContextDefaults.DefaultPosition,
        ) : WithBackground
    }
}
