package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.core.util.fastForEach
import org.beatonma.gclocks.core.util.getTime
import kotlin.math.max

interface ClockAnimator<G : BaseClockGlyph> {
    val layout: ClockLayout<G>
    val renderers: List<ClockRenderer<G>>
    val primaryRenderer: ClockRenderer<G>
        get() = this.renderers.last()

    val options get() = layout.options
    val paints get() = primaryRenderer.paints

    fun scheduleNextFrame(delayMillis: Int)

    fun tick(time: TimeOfDay = getTime()) {
        layout.update(time)
    }

    fun render(canvas: Canvas<*>) {
        canvas.clear()
        renderers.fastForEach { it.draw(canvas, layout) }

        scheduleNextFrame(max(0, 1000 - layout.animationTimeMillis))
    }

    fun setAvailableSize(available: Size<Float>): Size<Float> {
        return layout.setAvailableSize(available)
    }
}