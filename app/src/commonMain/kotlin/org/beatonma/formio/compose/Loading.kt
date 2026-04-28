package org.beatonma.formio.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.beatonma.formio.app.data.settings.ClockType
import org.beatonma.formio.app.theme.tokens.LoadingTokens
import org.beatonma.formio.compose.animation.currentFrameDelta
import org.beatonma.formio.core.LoadingSpinner
import org.beatonma.formio.form.FormLoadingSpinner
import org.beatonma.formio.form.FormPaints
import org.beatonma.formio.io16.Io16LoadingSpinner
import org.beatonma.formio.io16.Io16Paints
import org.beatonma.formio.io18.Io18LoadingSpinner
import org.beatonma.formio.io18.Io18Paints


@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier.fillMaxWidth(),
    clock: ClockType = remember { ClockType.entries.random() },
    maxSize: Dp = LoadingTokens.SpinnerSize,
) {
    val animation = rememberLoadingSpinner(clock)
    val canvasHost = rememberCanvasHost()
    val frameDelta = currentFrameDelta()

    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(
            Modifier
                .sizeIn(maxWidth = maxSize, maxHeight = maxSize)
                .aspectRatio(1f)
        ) {
            frameDelta.value // redraw when value changes
            val scale = minOf(size.width, size.height) / animation.size
            canvasHost.withScope(this) { canvas ->
                canvas.withScale(scale) {
                    animation.draw(canvas)
                }
            }
        }
    }
}


@Composable
private fun rememberLoadingSpinner(clock: ClockType): LoadingSpinner {
    return remember(clock) {
        when (clock) {
            ClockType.Io16 -> Io16LoadingSpinner(Io16Paints())
            ClockType.Io18 -> Io18LoadingSpinner(Io18Paints())
            ClockType.Form -> FormLoadingSpinner(FormPaints())
        }
    }
}
