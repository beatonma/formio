package org.beatonma.formio.clocks


import org.beatonma.formio.core.ClockAnimator
import org.beatonma.formio.core.createAnimator
import org.beatonma.formio.core.glyph.ClockGlyph
import org.beatonma.formio.core.glyph.GlyphState
import org.beatonma.formio.core.options.AnyOptions
import org.beatonma.formio.core.util.getCurrentTimeMillis
import org.beatonma.formio.form.FormClockRenderer
import org.beatonma.formio.form.FormFont
import org.beatonma.formio.io16.Io16ClockRenderer
import org.beatonma.formio.io16.Io16Font
import org.beatonma.formio.io16.Io16GlyphRenderer
import org.beatonma.formio.io18.Io18Font
import org.beatonma.formio.io18.Io18Renderer


/**
 * @param allowVariance When true, allow the clock to use randomised elements such as shuffling of paint colours or offsetting animations between glyphs. Disable when a consistent appearance is required.
 */
fun createAnimatorFromOptions(
    options: AnyOptions,
    allowVariance: Boolean,
    forcedState: GlyphState? = null,
    enableAnimation: Boolean = true,
    previous: ClockAnimator<*>? = null,
    onScheduleNextFrame: (delayMillis: Int) -> Unit,
): ClockAnimator<*> {
    return whenOptions(
        options,
        form = { formOptions ->
            createAnimator(
                formOptions,
                FormFont(isAnimated = enableAnimation),
                FormClockRenderer(formOptions.paints),
                castOrNull(previous),
                onScheduleNextFrame
            )
        },
        io16 = { io16Options ->
            createAnimator(
                io16Options,
                Io16Font(
                    isAnimated = enableAnimation,
                    debugGetGlyphAt = if (forcedState == null) null else ({ glyph ->
                        glyph.apply {
                            setState(
                                forcedState,
                                force = true,
                                currentTimeMillis = getCurrentTimeMillis()
                            )
                        }
                    }),
                    randomiseSegmentOffset = allowVariance,
                ),
                Io16ClockRenderer(
                    Io16GlyphRenderer(io16Options),
                    io16Options.paints
                ),
                castOrNull(previous),
                onScheduleNextFrame
            )
        },
        io18 = { io18Options ->
            createAnimator(
                io18Options,
                Io18Font(
                    isAnimated = enableAnimation,
                    shuffleColors = allowVariance,
                    offsetColors = true
                ),
                Io18Renderer(io18Options.paints),
                castOrNull(previous),
                onScheduleNextFrame
            )
        }
    )
}


/**
 * If the current type G matches the type used in [previousAnimator], cast and return [previousAnimator] safely.
 * Otherwise return null.
 */
private inline fun <reified G : ClockGlyph> castOrNull(previousAnimator: ClockAnimator<*>?): ClockAnimator<G>? {
    if (previousAnimator?.layout?.glyphClass == G::class) {
        @Suppress("UNCHECKED_CAST")
        return previousAnimator as? ClockAnimator<G>
    }
    return null
}
