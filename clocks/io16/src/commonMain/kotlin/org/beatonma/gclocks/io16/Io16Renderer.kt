package org.beatonma.gclocks.io16

import org.beatonma.gclocks.core.layout.ClockLayout
import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.Glyph
import org.beatonma.gclocks.core.GlyphRenderer
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.DrawStyle
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.graphics.PathMeasureScope
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.types.NormalFloat
import org.beatonma.gclocks.core.types.nf
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.getCurrentTimeMillis
import org.beatonma.gclocks.core.util.progress


class Io16ClockRenderer<P : Path>(
    override val renderer: Io16GlyphRenderer<P>,
    override var paints: Io16Paints,
) : ClockRenderer<Io16Paints, Io16Glyph> {
    override fun draw(canvas: Canvas, layout: ClockLayout<Io16Paints, Io16Glyph>) {
        renderer.now = getCurrentTimeMillis()
        super.draw(canvas, layout)
    }
}


class Io16GlyphRenderer<P : Path>(
    private val segmentPath: P,
    options: Io16Options,
    private val debugUpdateOnDraw: Boolean = false,
) : GlyphRenderer<Io16Paints> {
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
            ).nf
            field = value
        }

    private var segmentAnimationMillis: Int = 0

    private var segmentOffsetProgress: NormalFloat = NormalFloat.Zero

    private val style: Stroke = Stroke(
        options.paints.strokeWidth,
        cap = options.paints.strokeCap,
        join = options.paints.strokeJoin,
    )
    private val options: Io16GlyphOptions = options.glyph

    override fun draw(
        glyph: Glyph<Io16Paints>,
        canvas: Canvas,
        paints: Io16Paints,
    ) {
        debug {
            if (debugUpdateOnDraw) {
                now = getCurrentTimeMillis()
            }
        }
//
        if (now - glyph.stateChangedAt > options.activeStateDurationMillis) {
            glyph.setState(GlyphState.Inactive)
        }

        val transitionProgress = progress(
            (now - glyph.stateChangedAt).toFloat(),
            0f,
            options.stateChangeDurationMillis.toFloat()
        ).nf
        val disappearedSegmentSize = getDisappearedSegmentLength(glyph, transitionProgress)
        if (disappearedSegmentSize.isOne) {
            // Nothing to render
            return
        }

        val inactiveSegmentSize = getInactiveSegmentLength(glyph, transitionProgress)

        // If fully inactive, only one color is needed so just render the full path and return.
        if (disappearedSegmentSize.isZero && inactiveSegmentSize.isOne) {
            canvas.drawPath(paints.inactive, style)
            return
        }

        // Otherwise, split the path and render each segment individually
        canvas.measurePath { pm ->
            debug(false) {
                val offset: Float = segmentOffsetProgress * pm.length
                pm.getPosition(offset)?.let {
                    canvas.drawPoint(it.x, it.y, 8f, Color.Red)
                }
            }

            var remainingFraction: NormalFloat = disappearedSegmentSize.reversed

            if (inactiveSegmentSize.isNotZero) {
                // Draw 'inactive' section first, taking as much of the length as needed.
                drawSegment(
                    pm,
                    canvas,
                    segmentOffsetProgress.value,
                    inactiveSegmentSize * remainingFraction,
                    paints.inactive,
                    style
                )
                remainingFraction =
                    (remainingFraction - (remainingFraction * inactiveSegmentSize)).nf
            }

            val segmentSize: NormalFloat = remainingFraction / paints.active.size

            // Split the remaining length between the 'active' colors.
            paints.active.forEachIndexed { index, color ->
                drawSegment(
                    pm,
                    canvas,
                    segmentOffsetProgress + inactiveSegmentSize.value + (index * segmentSize.value),
                    segmentSize,
                    color,
                    style
                )
            }
        }
    }

    /** Portion of the total path length consumed by 'inactive' color. */
    private fun getInactiveSegmentLength(
        glyph: Glyph<*>,
        transitionProgress: NormalFloat,
    ): NormalFloat {
        if (glyph.lock == GlyphState.Inactive) return NormalFloat.One

        return when (glyph.state) {
            GlyphState.Inactive,
            GlyphState.DisappearingFromInactive,
                -> NormalFloat.One

            GlyphState.Active,
            GlyphState.Appearing,
            GlyphState.DisappearingFromActive,
            GlyphState.Disappearing,
            GlyphState.Disappeared,
                -> NormalFloat.Zero

            GlyphState.Activating -> transitionProgress.reversed
            GlyphState.Deactivating -> transitionProgress
        }
    }

    /** Portion of the total path length that is not visible. */
    private fun getDisappearedSegmentLength(
        glyph: Glyph<*>,
        transitionProgress: NormalFloat,
    ): NormalFloat = when (glyph.state) {
        GlyphState.Disappeared -> NormalFloat.One

        GlyphState.Inactive,
        GlyphState.Active,
        GlyphState.Activating,
        GlyphState.Deactivating,
            -> NormalFloat.Zero

        GlyphState.Appearing -> transitionProgress.reversed

        GlyphState.Disappearing,
        GlyphState.DisappearingFromActive,
        GlyphState.DisappearingFromInactive,
            -> transitionProgress
    }

    private fun drawSegment(
        pathMeasure: PathMeasureScope,
        canvas: Canvas,
        start: Float,
        segmentLength: NormalFloat,
        color: Color,
        style: DrawStyle,
    ) {
        if (segmentLength.isZero) return
        if (segmentLength.isOne) {
            canvas.drawPath(color, style)
            return
        }
        val length = pathMeasure.length
        val start = start % 1f
        val end = (segmentLength + start) % 1f

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