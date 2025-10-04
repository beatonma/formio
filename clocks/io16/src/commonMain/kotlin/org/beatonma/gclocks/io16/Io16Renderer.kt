package org.beatonma.gclocks.io16

import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.Glyph
import org.beatonma.gclocks.core.GlyphRenderer
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.graphics.PathMeasureScope
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.layout.ClockLayout
import org.beatonma.gclocks.core.types.ProgressFloat
import org.beatonma.gclocks.core.types.pf
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
    segmentPath: P,
    options: Io16Options,
    private val debugUpdateOnDraw: Boolean = false,
) : GlyphRenderer<Io16Paints, Io16Glyph> {
    private val options: Io16GlyphOptions = options.glyph
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
            ).pf
            field = value
        }

    private var segmentAnimationMillis: Int = 0
    private var segmentOffsetProgress: ProgressFloat = ProgressFloat.Zero

    private val pathRenderer = Io16PathRenderer(
        segmentPath,
        Stroke(
            options.paints.strokeWidth,
            cap = options.paints.strokeCap,
            join = options.paints.strokeJoin,
        )
    )

    private var rendererState = Io16PathRenderer.State.Appearing

    override fun draw(glyph: Io16Glyph, canvas: Canvas, paints: Io16Paints) {
        debug(false) {
            if (debugUpdateOnDraw) {
                now = getCurrentTimeMillis()
            }
        }
//
        if (now - glyph.stateChangedAt > options.activeStateDurationMillis) {
            glyph.setState(GlyphState.Inactive)
        }

        if (glyph.canonicalStartGlyph == ' ' && glyph.canonicalEndGlyph != ' ') {
            rendererState = Io16PathRenderer.State.Appearing
            glyph.setState(GlyphState.Appearing, force = true)
        }
        if (glyph.canonicalEndGlyph == ' ' && glyph.canonicalStartGlyph != ' ') {
            rendererState = Io16PathRenderer.State.Disappearing
            glyph.setState(GlyphState.Disappearing, force = true)
        }

        val transitionProgress = progress(
            (now - glyph.stateChangedAt).toFloat(),
            0f,
            options.stateChangeDurationMillis.toFloat()
        ).pf
        val disappearedSegmentSize = getDisappearedSegmentLength(glyph, transitionProgress)
        if (disappearedSegmentSize.isOne) {
            // Nothing to render
            return
        }

        val inactiveSegmentSize = getInactiveSegmentLength(glyph, transitionProgress)

        // If fully inactive, only one color is needed so just render the full path and return.
        if (disappearedSegmentSize.isZero && inactiveSegmentSize.isOne) {
            canvas.drawPath(paints.inactive, pathRenderer.style)
            return
        }

        val segmentOffsetProgress: Float = segmentOffsetProgress + glyph.animationOffset

        pathRenderer.drawSegments(
            canvas, paints,
            offset = segmentOffsetProgress,
            invisible = disappearedSegmentSize,
            inactive = inactiveSegmentSize,
            state = rendererState
        )
    }

    /** Portion of the total path length consumed by 'inactive' color. */
    private fun getInactiveSegmentLength(
        glyph: Glyph<*>,
        transitionProgress: ProgressFloat,
    ): ProgressFloat {
        if (glyph.lock == GlyphState.Inactive) return ProgressFloat.One

        return when (glyph.state) {
            GlyphState.Inactive,
            GlyphState.DisappearingFromInactive,
                -> ProgressFloat.One

            GlyphState.Active,
            GlyphState.Appearing,
            GlyphState.DisappearingFromActive,
            GlyphState.Disappearing,
            GlyphState.Disappeared,
                -> ProgressFloat.Zero

            GlyphState.Activating -> transitionProgress.reversed
            GlyphState.Deactivating -> transitionProgress
        }
    }

    /** Portion of the total path length that is not visible. */
    private fun getDisappearedSegmentLength(
        glyph: Glyph<*>,
        transitionProgress: ProgressFloat,
    ): ProgressFloat = when (glyph.state) {
        GlyphState.Disappeared -> ProgressFloat.One

        GlyphState.Inactive,
        GlyphState.Active,
        GlyphState.Activating,
        GlyphState.Deactivating,
            -> ProgressFloat.Zero

        GlyphState.Appearing -> transitionProgress.reversed

        GlyphState.Disappearing,
        GlyphState.DisappearingFromActive,
        GlyphState.DisappearingFromInactive,
            -> transitionProgress
    }
}


class Io16PathRenderer(
    private val segmentPath: Path,
    val style: Stroke,
) {
    enum class State {
        /** Invisible/inactive segments will be rendered at the start of the path*/
        Appearing,

        /** Invisible/inactive segments will be rendered at the end of the path*/
        Disappearing,
        ;
    }

    fun drawSegments(
        canvas: Canvas,
        paints: Io16Paints,
        offset: Float,
        invisible: ProgressFloat,
        inactive: ProgressFloat,
        state: State = State.Appearing,
    ) {
        // Lengths of each part relative to total path length
        val visibleLength = (1f - invisible.value).pf
        val inactiveLength = (visibleLength.value * inactive.value).pf
        val activeLength = (ProgressFloat.One - invisible.value - inactiveLength.value).pf

        val inactiveOffset: Float
        val activeOffset: Float
        when (state) {
            State.Appearing -> {
                inactiveOffset = offset + activeLength.value
                activeOffset = offset
            }

            State.Disappearing -> {
                inactiveOffset = offset + invisible.value
                activeOffset = offset + invisible.value + inactive.value
            }
        }

        canvas.measurePath { pm ->
            debug(false) {
                val offset: Float = offset * pm.length
                pm.getPosition(offset)?.let {
                    canvas.drawPoint(it.x, it.y, 8f, Color.Red)
                }
            }
            if (inactive.isNotZero) {
                drawSegment(pm, canvas, inactiveOffset, inactiveLength, paints.inactive)
            }

            // Split the remaining length between the 'active' colors.
            val segmentSize: ProgressFloat = activeLength / paints.active.size
            paints.active.forEachIndexed { index, color ->
                drawSegment(
                    pm,
                    canvas,
                    activeOffset + (index * segmentSize.value),
                    segmentSize,
                    color,
                )
            }
        }
    }

    private fun drawSegment(
        pathMeasure: PathMeasureScope,
        canvas: Canvas,
        start: Float,
        segmentLength: ProgressFloat,
        color: Color,
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
        } else {
            segmentPath.beginPath()
            pathMeasure.getSegment(startDistance, length, segmentPath)
            pathMeasure.getSegment(0f, endDistance, segmentPath)
        }
        canvas.drawPath(segmentPath, color, style)
    }
}
