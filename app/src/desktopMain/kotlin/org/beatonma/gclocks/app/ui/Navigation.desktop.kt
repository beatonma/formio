package org.beatonma.gclocks.app.ui

import androidx.compose.ui.graphics.vector.ImageVector
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.navigation_about
import gclocks_multiplatform.app.generated.resources.navigation_default
import kotlinx.serialization.Serializable
import org.beatonma.gclocks.compose.AppIcon
import org.jetbrains.compose.resources.StringResource

@Suppress("unused")
@Serializable
actual enum class NavigationMenuItem {
    Default {
        override val label: StringResource = Res.string.navigation_default
        override val contentDescription: StringResource = Res.string.navigation_default
        override val icon: ImageVector = AppIcon.Settings
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
