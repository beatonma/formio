package org.beatonma.gclocks.compose.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity

data class AppBarVisibility(
    val isTransparent: Boolean,
    val color: Color,
)

@Composable
fun appBarVisibility(
    lazyState: LazyStaggeredGridState,
    scrolledColor: Color = TopAppBarDefaults.topAppBarColors().containerColor
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
