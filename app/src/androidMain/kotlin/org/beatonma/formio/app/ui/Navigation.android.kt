package org.beatonma.formio.app.ui

import androidx.compose.ui.graphics.vector.ImageVector
import formio.app.generated.resources.Res
import formio.app.generated.resources.navigation_about
import formio.app.generated.resources.navigation_screensaver
import formio.app.generated.resources.navigation_wallpaper
import formio.app.generated.resources.navigation_widget
import kotlinx.serialization.Serializable
import org.beatonma.formio.app.ui.components.AndroidIcon
import org.beatonma.formio.app.ui.components.AppIcon
import org.jetbrains.compose.resources.StringResource

@Suppress("unused")
@Serializable
actual enum class NavigationMenuItem {
    LiveWallpaper {
        override val label: StringResource = Res.string.navigation_wallpaper
        override val contentDescription: StringResource = Res.string.navigation_wallpaper
        override val icon: ImageVector = AndroidIcon.LiveWallpaper
        override val type: NavigationMenuItemType = NavigationMenuItemType.DisplayContext
    },
    Screensaver {
        override val label: StringResource = Res.string.navigation_screensaver
        override val contentDescription: StringResource = Res.string.navigation_screensaver
        override val icon: ImageVector = AndroidIcon.Screensaver
        override val type: NavigationMenuItemType = NavigationMenuItemType.DisplayContext
    },
    Widget {
        override val label: StringResource = Res.string.navigation_widget
        override val contentDescription: StringResource = Res.string.navigation_widget
        override val icon: ImageVector = AndroidIcon.Widget
        override val type: NavigationMenuItemType = NavigationMenuItemType.DisplayContext
    },
    About {
        override val label: StringResource = Res.string.navigation_about
        override val contentDescription: StringResource = Res.string.navigation_about
        override val icon: ImageVector = AppIcon.About
        override val type: NavigationMenuItemType = NavigationMenuItemType.Overflow
    }
    ;

    actual abstract val label: StringResource
    actual abstract val contentDescription: StringResource
    actual abstract val icon: ImageVector
    actual abstract val type: NavigationMenuItemType
}
