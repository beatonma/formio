package org.beatonma.formio.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.pointerInput
import org.beatonma.formio.clocks.createAnimatorFromOptions
import org.beatonma.formio.compose.animation.currentFrameDelta
import org.beatonma.formio.compose.debugHotkey
import org.beatonma.formio.compose.rememberCanvasHost
import org.beatonma.formio.core.ClockAnimator
import org.beatonma.formio.core.glyph.GlyphState
import org.beatonma.formio.core.glyph.GlyphVisibility
import org.beatonma.formio.core.options.AnyOptions
import org.beatonma.formio.core.util.currentTimeMillis
import org.beatonma.formio.core.util.getInstant
import kotlin.time.Instant


@Composable
fun Clock(
    options: AnyOptions,
    modifier: Modifier = Modifier,
    getInstant: () -> Instant = ::getInstant,
    allowVariance: Boolean = true,
    forcedState: GlyphState? = null,
    visibility: GlyphVisibility? = null,
) {
    val frameDelta = currentFrameDelta()
    val animator = rememberClockAnimator(options, allowVariance, forcedState) {}
    val canvasHost = rememberCanvasHost()

    LaunchedEffect(visibility) {
        if (visibility != null) {
            animator.setState(visibility, false, getInstant().currentTimeMillis)
        }
    }

    ConstrainedCanvas(
        animator,
        modifier
            .debugClockHotkeys(animator)
            .clockPointerInput(animator)
    ) {
        frameDelta.value // redraw when value changes
        animator.tick(getInstant())

        canvasHost.withScope(this) { canvas ->
            animator.render(canvas)
        }
    }
}

@Composable
private fun rememberClockAnimator(
    options: AnyOptions,
    allowVariance: Boolean,
    forcedState: GlyphState?,
    onScheduleNextFrame: (delayMillis: Int) -> Unit,
): ClockAnimator<*> {
    var previous: ClockAnimator<*>? by remember { mutableStateOf(null) }
    val animator = remember(options, forcedState) {
        createAnimatorFromOptions(
            options,
            allowVariance = allowVariance,
            forcedState = forcedState,
            previous = previous.also { previous = null },
            onScheduleNextFrame = onScheduleNextFrame,
        )
    }
    LaunchedEffect(animator) {
        previous = animator
    }

    return animator
}


private fun Modifier.debugClockHotkeys(animator: ClockAnimator<*>) = debugHotkey { key ->
    when (key) {
        Key.One -> {
            animator.setState(
                GlyphState.Inactive,
                false,
                getInstant().currentTimeMillis
            )
            true
        }

        Key.Two -> {
            animator.setState(GlyphState.Active, false, getInstant().currentTimeMillis)
            true
        }

        Key.Three -> {
            animator.setState(
                GlyphVisibility.Hidden,
                false,
                getInstant().currentTimeMillis
            )
            true
        }

        Key.Four -> {
            animator.setState(
                GlyphVisibility.Visible,
                false,
                getInstant().currentTimeMillis
            )
            true
        }

        else -> false
    }
}

private fun Modifier.clockPointerInput(animator: ClockAnimator<*>) = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()

            val pointer = event.changes.firstOrNull() ?: continue
            val (x, y) = pointer.position

            animator.getGlyphAt(x, y)?.setState(
                GlyphState.Active,
                currentTimeMillis = getInstant().currentTimeMillis
            )
        }
    }
}
