package org.beatonma.gclocks.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val direction = LocalLayoutDirection.current

    return PaddingValues(
        calculateStartPadding(direction) + other.calculateStartPadding(direction),
        calculateTopPadding() + other.calculateTopPadding(),
        calculateEndPadding(direction) + other.calculateEndPadding(direction),
        calculateBottomPadding() + other.calculateBottomPadding()
    )
}