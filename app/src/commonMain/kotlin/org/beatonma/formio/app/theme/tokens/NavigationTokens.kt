package org.beatonma.formio.app.theme.tokens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.DrawerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal object NavigationTokens {
    object TopAppBar {
        val NavigationIconSize: Dp = 24.dp
        val NavigationIconPadding: PaddingValues = PaddingValues(start = 4.dp, top = 8.dp)
    }

    /* https://m3.material.io/components/navigation-drawer/specs */
    object Drawer {
        val MaxWidth: Dp = 280.dp
        val windowInsets: WindowInsets @Composable get() = DrawerDefaults.windowInsets
        val SeparatorVerticalPadding: Dp = 16.dp
    }

    /* https://m3.material.io/components/navigation-rail/specs */
    object WideRail {
        /** Padding applied to WideNavigationRail */
        val HeaderIconButtonStartPadding: Dp = 24.dp
    }
}