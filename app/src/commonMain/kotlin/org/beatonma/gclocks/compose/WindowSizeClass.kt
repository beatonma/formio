package org.beatonma.gclocks.compose

import androidx.window.core.layout.WindowSizeClass

fun WindowSizeClass.isWidthAtLeastExtraLarge() =
    isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXTRA_LARGE_LOWER_BOUND)

fun WindowSizeClass.isWidthAtLeastLarge() = isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_LARGE_LOWER_BOUND)
fun WindowSizeClass.isWidthAtLeastExpanded() = isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
fun WindowSizeClass.isWidthAtLeastMedium() = isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

fun WindowSizeClass.isHeightAtLeastExpanded() =
    isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND)

fun WindowSizeClass.isHeightAtLeastMedium() = isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
