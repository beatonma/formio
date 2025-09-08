package org.beatonma.gclocks.app.settings

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.graphics.Color


actual enum class DisplayContext {
    Widget {
        override fun defaultOptions(): Options = Options.Widget
    },
    LiveWallpaper {
        override fun defaultOptions(): Options = Options.Wallpaper()
    },
    Screensaver {
        override fun defaultOptions(): Options = Options.Screensaver()
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
        ) : Options

        @Serializable
        data class Screensaver(
            val backgroundColor: Color = Color.Green,
        ) : Options
    }
}