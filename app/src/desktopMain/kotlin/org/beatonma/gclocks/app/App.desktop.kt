package org.beatonma.gclocks.app

import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.compose.AppIcon

actual enum class Screens : Screen {
    Widget {
        override val displayContext = DisplayContext.Default
        override val icon = AppIcon.Checkmark
        override val label = LocalizedString(literal = displayContext.name)
        override val contentDescription = LocalizedString(literal = displayContext.name)
    },
    LiveWallpaper {
        override val displayContext = DisplayContext.Alternative
        override val icon = AppIcon.Checkmark
        override val label = LocalizedString(literal = displayContext.name)
        override val contentDescription = LocalizedString(literal = displayContext.name)
    },
}