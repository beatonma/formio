package org.beatonma.gclocks.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.beatonma.gclocks.clocks.createAnimatorFromOptions
import org.beatonma.gclocks.compose.ComposePath
import org.beatonma.gclocks.compose.rememberCanvasHost
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.core.util.getTime
import kotlin.time.Duration


@Composable
fun <Opts : Options<*>> Clock(
    options: Opts,
    modifier: Modifier = Modifier,
    getTickTime: () -> TimeOfDay = ::getTime,
    forcedState: GlyphState? = null,
) {
    val animator = rememberClockAnimator(options, forcedState)
    val frameDeltaMillis = currentFrameDelta()
    val canvasHost = rememberCanvasHost()

    ConstrainedCanvas(animator, modifier) {
        frameDeltaMillis
        animator.tick(getTickTime())
        canvasHost.withScope(this) { canvas ->
            animator.render(canvas)
        }
    }
}


@Composable
expect fun currentFrameDelta(): Duration


@Composable
private fun <Opts : Options<*>> rememberClockAnimator(
    options: Opts,
    forcedState: GlyphState?,
): ClockAnimator<*, *> {
    val path = remember { ComposePath() }
    return remember(options, forcedState) {
        createAnimatorFromOptions(
            options, path, forcedState = forcedState
        ) {
            // TODO schedule next frame
        }
    }
}
