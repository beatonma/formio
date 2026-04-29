package org.beatonma.formio.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import formio.app.generated.resources.Res
import formio.app.generated.resources.navigation_cd_back
import formio.app.generated.resources.navigation_cd_modal_close
import formio.app.generated.resources.navigation_cd_modal_open
import org.beatonma.formio.app.ui.theme.tokens.NavigationTokens
import org.jetbrains.compose.resources.stringResource

data class AppBarVisibility(
    val isTransparent: Boolean,
    val color: Color,
)

@Composable
fun appBarVisibility(
    lazyState: LazyStaggeredGridState,
    scrolledColor: Color = TopAppBarDefaults.topAppBarColors().containerColor,
): AppBarVisibility {
    val density = LocalDensity.current
    val isTransparent by derivedStateOf {
        lazyState.firstVisibleItemIndex == 0
                && lazyState.firstVisibleItemScrollOffset < with(density) {
            TopAppBarDefaults.MediumAppBarCollapsedHeight.toPx() / 2f
        }
    }

    val animatedColor by animateColorAsState(if (isTransparent) scrolledColor.copy(alpha = 0f) else scrolledColor)

    return AppBarVisibility(isTransparent, animatedColor)
}

/**
 * Back icon, intended for use in the `TopAppBar` `navigationIcon` slot - otherwise wrap with `NavigationIconContainer`
 * for correct placement.
 */
@Composable
fun BackNavigationIcon(onClick: () -> Unit, modifier: Modifier = Modifier, contentDescription: String? = null) {
    NavigationIcon(onClick, AppIcon.Back, contentDescription ?: stringResource(Res.string.navigation_cd_back), modifier)
}

/**
 * Hamburger icon, intended for use in the `TopAppBar` `navigationIcon` slot - otherwise wrap with `NavigationIconContainer`
 * for correct placement.
 */
@Composable
fun HamburgerNavigationIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOpen: Boolean = false,
) {
    val icon = when (isOpen) {
        true -> AppIcon.HamburgerClose
        false -> AppIcon.Hamburger
    }
    val contentDescription = when (isOpen) {
        true -> stringResource(Res.string.navigation_cd_modal_close)
        false -> stringResource(Res.string.navigation_cd_modal_open)
    }

    val rotation by animateFloatAsState(if (isOpen) 180f else 0f)

    NavigationIcon(onClick, icon, contentDescription, modifier.rotate(rotation))
}

/**
 * Container for correct positioning of navigation icons when `TopAppBar` is not suitable.
 *
 * The resulting position (when placed in a full-window container) should be the same as if using the `navigationIcon`
 * slot of the `TopAppBar` component.
 */
@Composable
fun NavigationIconContainer(contentColor: Color? = null, content: @Composable () -> Unit) {
    val content = remember(contentColor) {
        movableContentOf {
            if (contentColor == null) content()
            else {
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    content()
                }
            }
        }
    }

    Box(
        Modifier
            .windowInsetsPadding(NavigationTokens.Drawer.windowInsets)
            .padding(NavigationTokens.TopAppBar.NavigationIconPadding)
    ) {
        content()
    }
}


@Composable
private fun NavigationIcon(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick, modifier) {
        Icon(icon, contentDescription, modifier.size(NavigationTokens.TopAppBar.NavigationIconSize))
    }
}