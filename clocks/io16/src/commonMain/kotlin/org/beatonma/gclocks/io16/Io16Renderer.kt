package org.beatonma.gclocks.io16

import androidx.annotation.FloatRange
import org.beatonma.gclocks.core.ClockLayout
import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.GlyphRenderer
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.DrawStyle
import org.beatonma.gclocks.core.graphics.GenericCanvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.getCurrentTimeMillis
import org.beatonma.gclocks.core.util.progress


class Io16Paints : Paints {
    override val colors: Array<Color>
        get() = arrayOf(
            Color(0xffef5350), // Reddish
            Color(0xff8cf2f2), // Cyanish
            Color(0xff33c9dc), // DarkCyanish
            Color(0xff5c6bc0), // Indigoish
            Color(0xff78909c), // Inactive
        )
    val active: Array<Color> = colors.copyOfRange(0, 4)
    val inactive: Color = colors.last()

    init {
        require(colors.size == 5)
    }
}

class Io16ClockRenderer<P : Path>(
    override val renderer: Io16GlyphRenderer<P>,
    override var paints: Paints = Io16Paints(),
) : ClockRenderer<Io16Glyph> {
    override fun draw(canvas: GenericCanvas, layout: ClockLayout<Io16Glyph>) {
        renderer.now = getCurrentTimeMillis()
        super.draw(canvas, layout)
    }
}


class Io16GlyphRenderer<P : Path>(
    private val segmentPath: P,
    private val options: Io16Options = Io16Options(),
    private val updateOnDraw: Boolean = false,
) : GlyphRenderer<Io16Glyph> {
    private var previousNow: Long = getCurrentTimeMillis()
    internal var now: Long = getCurrentTimeMillis()
        set(value) {
            previousNow = field

            val delta = value - previousNow
            segmentAnimationMillis =
                (segmentAnimationMillis + delta.toInt()) % options.colorCycleDurationMillis
            segmentAnimationOffset =
                progress(
                    segmentAnimationMillis.toFloat(),
                    0f,
                    options.colorCycleDurationMillis.toFloat()
                )
            field = value
        }

    private var segmentAnimationMillis: Int = 0

    /* 0..1 progress of the segment animation */
    private var segmentAnimationOffset: Float = 0f

    private val style: Stroke = Stroke(options.strokeWidth)

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

        val length: Float = canvas.pathMeasure.length
        var remainingLength: Float = length
        var offset: Float = segmentAnimationOffset * length

        val inactiveSegmentSize =
            getInactiveSegmentLength(glyph, options.stateChangeDurationMillis.toFloat())
        if (inactiveSegmentSize > 0f) {
            val inactiveLength = inactiveSegmentSize * length
            drawSegment(canvas, offset, offset + inactiveLength, paints.inactive, style)
            offset += inactiveLength
            remainingLength -= inactiveLength
        }

        val segmentLength: Float = remainingLength / paints.active.size.toFloat()
        paints.active.forEach { color ->
            drawSegment(canvas, offset, offset + segmentLength, color, style)
            offset += segmentLength
        }
    }

    /** 0..1 portion of the total path length consumed by 'inactive' color. */
    @FloatRange(from = 0.0, to = 1.0)
    private fun getInactiveSegmentLength(
        glyph: Io16Glyph,
        stateChangeDurationMillis: Float,
    ): Float {
        return when (glyph.state) {
            GlyphState.Active -> 0f
            GlyphState.Inactive, GlyphState.Disappeared -> 1f

            GlyphState.Appearing, GlyphState.Activating -> progress(
                (now - glyph.stateChangedAt).toFloat() / stateChangeDurationMillis,
                0f,
                stateChangeDurationMillis
            )

            GlyphState.Disappearing, GlyphState.Deactivating -> -progress(
                (now - glyph.stateChangedAt).toFloat() / stateChangeDurationMillis,
                0f,
                stateChangeDurationMillis
            )

            GlyphState.DisappearingFromActive, GlyphState.DisappearingFromInactive -> {
                // TODO
                0.5f
            }
        }
    }

    private fun drawSegment(
        canvas: GenericCanvas,
        start: Float,
        end: Float,
        color: Color,
        style: DrawStyle = Stroke.Default,
    ) {
        val pathMeasure = canvas.pathMeasure
        var start = start
        var end = end
        val length = pathMeasure.length

        if (start > length) {
            start = start % length
        }
        if (end > length) {
            end = end % length
        }

        segmentPath.beginPath()
        canvas.measurePath()
        if (end < start) {
            pathMeasure.getSegment(start, length, segmentPath)
            pathMeasure.getSegment(0f, end, segmentPath)
        } else {
            pathMeasure.getSegment(start, end, segmentPath)
        }
        canvas.drawPath(segmentPath, color, style)
    }
}