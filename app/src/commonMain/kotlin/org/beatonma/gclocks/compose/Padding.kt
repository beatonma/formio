package org.beatonma.gclocks.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp

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

@Composable
fun PaddingValues.vertical(): PaddingValues = PaddingValues(
    top = calculateTopPadding(),
    bottom = calculateBottomPadding()
)

@Composable
fun PaddingValues.horizontal(): PaddingValues = run {
    val direction = LocalLayoutDirection.current
    PaddingValues(
        start = calculateStartPadding(direction),
        end = calculateEndPadding(direction)
    )
}

val VerticalBottomContentPadding = PaddingValues(bottom = 128.dp)
