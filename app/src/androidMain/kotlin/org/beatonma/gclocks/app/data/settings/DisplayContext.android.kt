package org.beatonma.gclocks.app.data.settings

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.app.ui.LocalizedString
import org.beatonma.gclocks.compose.AndroidIcon
import org.beatonma.gclocks.core.geometry.MutableRectF
import org.beatonma.gclocks.core.geometry.RectF
import org.beatonma.gclocks.core.graphics.Color

private val DefaultPosition: RectF = MutableRectF(0f, 0f, 1f, 1f).inset(0.1f).toRect()

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
            override val backgroundColor: Color = Color(0xff222222),
            override val position: RectF = DefaultPosition,
        ) : WithBackground

        @Serializable
        data class Screensaver(
            override val backgroundColor: Color = Color(0xff222222),
            override val position: RectF = DefaultPosition,
        ) : WithBackground
    }
}

actual enum class DisplayContextScreens : DisplayContextScreen {
    Widget {
        override val displayContext = DisplayContext.Widget
        override val icon = AndroidIcon.Widget
        override val label = LocalizedString(literal = displayContext.name)
        override val contentDescription = LocalizedString(literal = displayContext.name)
    },
    LiveWallpaper {
        override val displayContext = DisplayContext.LiveWallpaper
        override val icon = AndroidIcon.LiveWallpaper
        override val label = LocalizedString(literal = displayContext.name)
        override val contentDescription = LocalizedString(literal = displayContext.name)
    },
    Screensaver {
        override val displayContext = DisplayContext.Screensaver
        override val icon = AndroidIcon.Screensaver
        override val label = LocalizedString(literal = displayContext.name)
        override val contentDescription = LocalizedString(literal = displayContext.name)
    },
}
