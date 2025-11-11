package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.ConstrainedLayout
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.geometry.ScaledSize
import org.beatonma.gclocks.core.glyph.ClockGlyph
import org.beatonma.gclocks.core.glyph.GlyphState
import org.beatonma.gclocks.core.glyph.GlyphVisibility
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.layout.ClockLayout
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.currentTimeMillis
import org.beatonma.gclocks.core.util.fastForEach
import org.beatonma.gclocks.core.util.getInstant
import kotlin.math.max
import kotlin.time.Instant

interface ClockAnimator<P : Paints, G : ClockGlyph<P>> : ConstrainedLayout {
    val layout: ClockLayout<P, G>

    fun scheduleNextFrame(delayMillis: Int)
    fun tick(instant: Instant = getInstant())
    fun render(canvas: Canvas)

    /**
     * One-shot utility to render a single frame.
     *
     * Generally better to choreograph each of these steps separately,
     * but if you just need to render a single frame then this will do the job.
     */
    fun renderOnce(
        canvas: Canvas,
        width: Float,
        height: Float,
        state: GlyphState = GlyphState.Active,
        instant: Instant = getInstant(),
    ) {
        setState(state, force = true)
        setConstraints(MeasureConstraints(width, height))
        tick(instant)
        render(canvas)
    }

    fun setState(state: GlyphState, visibility: GlyphVisibility, force: Boolean) {
        layout.setState(state, visibility, force)
    }

    fun setState(state: GlyphState, force: Boolean) {
        layout.setState(state, force)
    }

    fun setState(visibility: GlyphVisibility, force: Boolean) {
        layout.setState(visibility, force)
    }

    fun getGlyphAt(x: Float, y: Float): G? =
        layout.getGlyphAt(x, y)

    override fun setConstraints(constraints: MeasureConstraints): ScaledSize =
        layout.setConstraints(constraints)
}

private interface SingleRendererClockAnimator<P : Paints, G : ClockGlyph<P>> : ClockAnimator<P, G> {
    val renderer: ClockRenderer<P, G>

    override fun tick(instant: Instant) {
        layout.update(instant)
        renderer.update(instant.currentTimeMillis)
    }

    override fun render(canvas: Canvas) {
        renderer.draw(canvas, layout)

        scheduleNextFrame(max(0, 1000 - layout.animationTimeMillis))
    }
}

private interface MultiRendererClockAnimator<P : Paints, G : ClockGlyph<P>> : ClockAnimator<P, G> {
    val renderers: List<ClockRenderer<P, G>>

    override fun tick(instant: Instant) {
        layout.update(instant)
        val millis = instant.currentTimeMillis
        renderers.fastForEach { it.update(millis) }
    }

    override fun render(canvas: Canvas) {
        renderers.fastForEach { it.draw(canvas, layout) }

        scheduleNextFrame(max(0, 1000 - layout.animationTimeMillis))
    }
}

fun <P : Paints, O : Options<P>, G : ClockGlyph<P>> createAnimator(
    options: O,
    font: ClockFont<P, G>,
    renderer: ClockRenderer<P, G>,
    onScheduleNextFrame: (delayMillis: Int) -> Unit,
): ClockAnimator<P, G> {
    return object : SingleRendererClockAnimator<P, G> {
        override val layout = ClockLayout(font = font, options = options)
        override val renderer: ClockRenderer<P, G> = renderer
        override fun scheduleNextFrame(delayMillis: Int) {
            onScheduleNextFrame(delayMillis)
        }
    }
}

fun <P : Paints, O : Options<P>, G : ClockGlyph<P>> createAnimator(
    options: O,
    font: ClockFont<P, G>,
    renderers: List<ClockRenderer<P, G>>,
    onScheduleNextFrame: (delayMillis: Int) -> Unit,
): ClockAnimator<P, G> {
    if (renderers.size == 1) {
        return createAnimator(options, font, renderers.first(), onScheduleNextFrame)
    }

    return object : MultiRendererClockAnimator<P, G> {
        override val layout = ClockLayout(font = font, options = options)
        override val renderers: List<ClockRenderer<P, G>> = renderers.toList()
        override fun scheduleNextFrame(delayMillis: Int) {
            onScheduleNextFrame(delayMillis)
        }
    }
}
