package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.ConstrainedLayout
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.geometry.ScaledSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.layout.ClockLayout
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.core.util.fastForEach
import org.beatonma.gclocks.core.util.getTime
import kotlin.math.max

interface ClockAnimator<P : Paints, G : ClockGlyph<P>> : ConstrainedLayout {
    val layout: ClockLayout<P, G>
    val renderers: List<ClockRenderer<P, G>>

    fun scheduleNextFrame(delayMillis: Int)

    fun tick(time: TimeOfDay = getTime()) {
        layout.update(time)
    }

    fun render(canvas: Canvas) {
        renderers.fastForEach { it.draw(canvas, layout) }

        scheduleNextFrame(max(0, 1000 - layout.animationTimeMillis))
    }

    /**
     * One-shot utility to render a single frame.
     *
     * Generally better to choreograph each of these steps separately,
     * but if you just need to render a single frame then this will do the job.
     */
    fun renderOnce(canvas: Canvas, width: Float, height: Float, time: TimeOfDay = getTime()) {
        setConstraints(MeasureConstraints(width, height))
        tick(time)
        render(canvas)
    }

    override fun setConstraints(constraints: MeasureConstraints): ScaledSize =
        layout.setConstraints(constraints)
}

fun <P : Paints, O : Options<P>, G : ClockGlyph<P>> createAnimator(
    options: O,
    font: ClockFont<P, G>,
    renderer: ClockRenderer<P, G>,
    onScheduleNextFrame: (delayMillis: Int) -> Unit,
) = object : ClockAnimator<P, G> {
    override val layout = ClockLayout(font = font, options = options)
    override val renderers: List<ClockRenderer<P, G>> = listOf(renderer)

    override fun scheduleNextFrame(delayMillis: Int) {
        onScheduleNextFrame(delayMillis)
    }
}