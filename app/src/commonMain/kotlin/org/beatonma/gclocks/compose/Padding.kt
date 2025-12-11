package org.beatonma.gclocks.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
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
fun PaddingValues.top(): PaddingValues = PaddingValues(
    top = calculateTopPadding(),
)

@Composable
fun PaddingValues.bottom(): PaddingValues = PaddingValues(
    bottom = calculateBottomPadding(),
)

@Composable
fun PaddingValues.start(direction: LayoutDirection = LocalLayoutDirection.current): PaddingValues =
    PaddingValues(
        start = calculateStartPadding(direction),
    )

@Composable
fun PaddingValues.end(direction: LayoutDirection = LocalLayoutDirection.current): PaddingValues =
    PaddingValues(
        end = calculateEndPadding(direction),
    )

@Composable
fun PaddingValues.horizontal(direction: LayoutDirection = LocalLayoutDirection.current): PaddingValues =
    run {
        PaddingValues(
            start = calculateStartPadding(direction),
            end = calculateEndPadding(direction)
        )
    }

@Composable
fun PaddingValues.copy(
    start: Dp? = null,
    top: Dp? = null,
    end: Dp? = null,
    bottom: Dp? = null,
    direction: LayoutDirection = LocalLayoutDirection.current,
) = PaddingValues(
    start ?: calculateStartPadding(direction),
    top ?: calculateTopPadding(),
    end ?: calculateEndPadding(direction),
    bottom ?: calculateBottomPadding(),
)


val VerticalBottomContentPadding = PaddingValues(bottom = 96.dp)
