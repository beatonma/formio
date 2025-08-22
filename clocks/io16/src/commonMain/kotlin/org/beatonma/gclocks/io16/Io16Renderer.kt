package org.beatonma.gclocks.io16

import androidx.annotation.FloatRange
import org.beatonma.gclocks.core.layout.ClockLayout
import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.GlyphRenderer
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.DrawStyle
import org.beatonma.gclocks.core.graphics.GenericCanvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.graphics.PathMeasureScope
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.getCurrentTimeMillis
import org.beatonma.gclocks.core.util.progress


class Io16ClockRenderer<P : Path>(
    override val renderer: Io16GlyphRenderer<P>,
    override var paints: Io16Paints,
) : ClockRenderer<Io16Glyph, Io16Paints> {
    override fun draw(canvas: GenericCanvas, layout: ClockLayout<Io16Glyph>) {
        renderer.now = getCurrentTimeMillis()
        super.draw(canvas, layout)
    }
}


class Io16GlyphRenderer<P : Path>(
    private val segmentPath: P,
    options: Io16Options,
    private val updateOnDraw: Boolean = false,
) : GlyphRenderer<Io16Glyph> {
    private var previousNow: Long = getCurrentTimeMillis()
    internal var now: Long = getCurrentTimeMillis()
        set(value) {
            previousNow = field

            val delta = value - previousNow
            segmentAnimationMillis =
                (segmentAnimationMillis + delta.toInt()) % options.colorCycleDurationMillis
            segmentOffsetProgress = progress(
                segmentAnimationMillis.toFloat(),
                0f,
                options.colorCycleDurationMillis.toFloat()
            )
            field = value
        }

    private var segmentAnimationMillis: Int = 0

    @FloatRange(0.0, 1.0)
    private var segmentOffsetProgress: Float = 0f

    private val style: Stroke = Stroke(
        options.paints.strokeWidth,
        cap = options.paints.strokeCap,
        join = options.paints.strokeJoin,
    )
    private val options: Io16GlyphOptions = options.glyph

    override fun draw(
        glyph: Io16Glyph,
        canvas: GenericCanvas,
        glyphProgress: Float,
        paints: Paints,
    ) {
        // Plot path without drawing anything.
        super.draw(glyph, canvas, glyphProgress, paints)
        paints as Io16Paints

        if (glyph.role.isSeparator) {
            canvas.drawPath(paints.inactive, style)
            return
        }

        debug {
            if (updateOnDraw) {
                now = getCurrentTimeMillis()
            }
        }

        if (now - glyph.stateChangedAt > options.activeStateDurationMillis) {
            glyph.setState(GlyphState.Inactive)
        }

        val inactiveSegmentSize = getInactiveSegmentLength(
            glyph,
            options.stateChangeDurationMillis.toFloat()
        )
        // If fully inactive, only one color is needed so just render the full path and return.
        if (inactiveSegmentSize == 1f) {
            canvas.drawPath(paints.inactive, style)
            return
        }

        // Otherwise, split the path and render each segment individually
        canvas.measurePath { pm ->
            debug(false) {
                val offset: Float = segmentOffsetProgress * pm.length
                pm.getPosition(offset)?.let {
                    canvas.drawPoint(it.x, it.y, Color.Red)
                }
            }
            // Draw 'inactive' section first, taking as much of the length as needed.
            drawSegment(
                pm,
                canvas,
                segmentOffsetProgress,
                inactiveSegmentSize,
                paints.inactive,
                style
            )

            val remaining = 1f - inactiveSegmentSize
            val segmentSize = remaining / paints.active.size.toFloat()

            // Split the remaining length between the 'active' colors.
            paints.active.forEachIndexed { index, color ->
                drawSegment(
                    pm,
                    canvas,
                    segmentOffsetProgress + inactiveSegmentSize + (index * segmentSize),
                    segmentSize,
                    color,
                    style
                )
            }
        }
    }

    /** Portion of the total path length consumed by 'inactive' color. */
    @FloatRange(from = 0.0, to = 1.0)
    private fun getInactiveSegmentLength(
        glyph: Io16Glyph,
        stateChangeDurationMillis: Float,
    ): Float {
        val transitionProgress = progress(
            (now - glyph.stateChangedAt).toFloat(),
            0f,
            stateChangeDurationMillis
        )
        return when (glyph.state) {
            GlyphState.Active -> 0f
            GlyphState.Inactive, GlyphState.Disappeared -> 1f

            GlyphState.Appearing, GlyphState.Activating -> 1f - transitionProgress
            GlyphState.Deactivating -> transitionProgress

            GlyphState.Disappearing,
            GlyphState.DisappearingFromActive,
            GlyphState.DisappearingFromInactive,
                -> {
                // TODO
                transitionProgress
            }
        }
    }

    private fun drawSegment(
        pathMeasure: PathMeasureScope,
        canvas: GenericCanvas,
        @FloatRange(0.0, 1.0) start: Float,
        @FloatRange(0.0, 1.0) segmentLength: Float,
        color: Color,
        style: DrawStyle,
    ) {
        if (segmentLength == 0f) return
        if (segmentLength == 1f) {
            canvas.drawPath(color, style)
            return
        }
        val length = pathMeasure.length
        val start = start % 1f
        val end = (start + segmentLength) % 1f

        val startDistance = start * length
        val endDistance = end * length

        if (start < end) {
            segmentPath.beginPath()
            pathMeasure.getSegment(startDistance, endDistance, segmentPath)
            canvas.drawPath(segmentPath, color, style)
        } else {
            segmentPath.beginPath()
            pathMeasure.getSegment(startDistance, length, segmentPath)
            pathMeasure.getSegment(0f, endDistance, segmentPath)
        }
        canvas.drawPath(segmentPath, color, style)
    }
}