package org.beatonma.gclocks.clocks


import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.createAnimator
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.form.FormClockRenderer
import org.beatonma.gclocks.form.FormFont
import org.beatonma.gclocks.io16.Io16ClockRenderer
import org.beatonma.gclocks.io16.Io16Font
import org.beatonma.gclocks.io16.Io16GlyphRenderer
import org.beatonma.gclocks.io18.Io18Font
import org.beatonma.gclocks.io18.Io18Renderer


/**
 * @param allowVariance When true, allow the clock to use randomised elements such as shuffling of paint colours or offsetting animations between glyphs. Disable when a consistent appearance is required.
 */
fun createAnimatorFromOptions(
    options: Options<*>,
    path: Path,
    allowVariance: Boolean,
    forcedState: GlyphState? = null,
    enableAnimation: Boolean = true,
    onScheduleNextFrame: (delayMillis: Int) -> Unit,
): ClockAnimator<*, *> {
    return whenOptions(
        options,
        form = { formOptions ->
            createAnimator(
                formOptions,
                FormFont(isAnimated = enableAnimation),
                FormClockRenderer(formOptions.paints),
                onScheduleNextFrame
            )
        },
        io16 = { io16Options ->
            createAnimator(
                io16Options,
                Io16Font(
                    isAnimated = enableAnimation,
                    debugGetGlyphAt = if (forcedState == null) null else ({ glyph ->
                        glyph.apply { setState(forcedState, force = true) }
                    }),
                    randomiseSegmentOffset = allowVariance,
                ),
                Io16ClockRenderer(
                    Io16GlyphRenderer(path, io16Options),
                    io16Options.paints
                ),
                onScheduleNextFrame
            )
        },
        io18 = { io18Options ->
            createAnimator(
                io18Options,
                Io18Font(path, isAnimated = enableAnimation, shuffleColors = allowVariance, offsetColors = true),
                Io18Renderer(io18Options.paints),
                onScheduleNextFrame
            )
        }
    )
}
