package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.ConstrainedLayout
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.geometry.ScaledSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.layout.ClockLayout
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.core.util.fastForEach
import org.beatonma.gclocks.core.util.getTime
import kotlin.math.max

interface ClockAnimator<P : Paints, G : BaseClockGlyph<P>> : ConstrainedLayout {
    val layout: ClockLayout<P, G>
    val renderers: List<ClockRenderer<P, G>>

    fun scheduleNextFrame(delayMillis: Int)

    fun tick(time: TimeOfDay = getTime()) {
        layout.update(time)
    }

    fun render(canvas: Canvas) {
        canvas.clear()
        renderers.fastForEach { it.draw(canvas, layout) }

        scheduleNextFrame(max(0, 1000 - layout.animationTimeMillis))
    }

    override fun setConstraints(constraints: MeasureConstraints): ScaledSize =
        layout.setConstraints(constraints)
}