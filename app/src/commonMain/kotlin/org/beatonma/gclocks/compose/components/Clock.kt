package org.beatonma.gclocks.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.pointer.pointerInput
import org.beatonma.gclocks.clocks.createAnimatorFromOptions
import org.beatonma.gclocks.compose.ComposePath
import org.beatonma.gclocks.compose.debugKeyEvent
import org.beatonma.gclocks.compose.rememberCanvasHost
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.glyph.GlyphState
import org.beatonma.gclocks.core.glyph.GlyphVisibility
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.getInstant
import kotlin.time.Duration
import kotlin.time.Instant


@Composable
expect fun currentFrameDelta(): Duration

@Composable
fun <Opts : Options<*>> Clock(
    options: Opts,
    modifier: Modifier = Modifier,
    getInstant: () -> Instant = ::getInstant,
    allowVariance: Boolean = true,
    forcedState: GlyphState? = null,
    visibility: GlyphVisibility? = null,
) {
    val animator = rememberClockAnimator(options, allowVariance, forcedState)

    LaunchedEffect(visibility) {
        if (visibility != null) animator.setState(visibility, false)
    }

    Clock(animator, modifier, getInstant)
}

@Composable
fun Clock(
    animator: ClockAnimator<*, *>,
    modifier: Modifier = Modifier,
    getInstant: () -> Instant = ::getInstant,
) {
    val frameDeltaMillis = currentFrameDelta()
    val canvasHost = rememberCanvasHost()

    ConstrainedCanvas(
        animator,
        modifier
            .debugKeyEvent { event ->
                return@debugKeyEvent when (event.key) {
                    Key.One -> {
                        animator.setState(GlyphState.Inactive, false)
                        true
                    }

                    Key.Two -> {
                        animator.setState(GlyphState.Active, false)
                        true
                    }

                    Key.Three -> {
                        animator.setState(GlyphVisibility.Hidden, false)
                        true
                    }

                    Key.Four -> {
                        animator.setState(GlyphVisibility.Visible, false)
                        true
                    }

                    else -> false
                }
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()

                        val pointer = event.changes.firstOrNull() ?: continue
                        val (x, y) = pointer.position

                        animator.getGlyphAt(x, y)?.setState(GlyphState.Active)
                    }
                }
            }
    ) {
        frameDeltaMillis
        animator.tick(getInstant())

        canvasHost.withScope(this) { canvas ->
            animator.render(canvas)
        }
    }
}

@Composable
private fun <Opts : Options<*>> rememberClockAnimator(
    options: Opts,
    allowVariance: Boolean,
    forcedState: GlyphState?,
): ClockAnimator<*, *> {
    return remember(options, forcedState) {
        createAnimatorFromOptions(
            options,
            ComposePath(),
            allowVariance = allowVariance,
            forcedState = forcedState
        ) {
            // TODO schedule next frame
        }
    }
}
