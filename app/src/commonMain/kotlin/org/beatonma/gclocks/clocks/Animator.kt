package org.beatonma.gclocks.clocks


import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.createAnimator
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.form.FormClockRenderer
import org.beatonma.gclocks.form.FormFont
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16ClockRenderer
import org.beatonma.gclocks.io16.Io16Font
import org.beatonma.gclocks.io16.Io16GlyphRenderer
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Font
import org.beatonma.gclocks.io18.Io18Options
import org.beatonma.gclocks.io18.Io18Renderer

fun createAnimatorFromOptions(
    options: Options<*>,
    path: Path,
    forcedState: GlyphState? = null,
    onScheduleNextFrame: (delayMillis: Int) -> Unit,
): ClockAnimator<*, *> {
    return when (options) {
        is FormOptions -> createAnimator(
            options,
            FormFont(),
            FormClockRenderer(options.paints),
            onScheduleNextFrame
        )

        is Io16Options -> createAnimator(
            options,
            Io16Font(
                debugGetGlyphAt = if (forcedState == null) null else ({ glyph ->
                    glyph.apply { setState(forcedState, force = true) }
                }),
                randomiseSegmentOffset = false,
            ),
            Io16ClockRenderer(
                Io16GlyphRenderer(path, options),
                options.paints
            ),
            onScheduleNextFrame
        )

        is Io18Options -> createAnimator(
            options,
            Io18Font(path, shuffleColors = false, offsetColors = true),
            Io18Renderer(options.paints),
            onScheduleNextFrame
        )

        else -> throw IllegalStateException(
            "Unhandled Option type: ${options::class.simpleName}"
        )
    }
}
