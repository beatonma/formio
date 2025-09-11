package org.beatonma.gclocks.app

import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.compose.AndroidIcon

actual enum class Screens : Screen {
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