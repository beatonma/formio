package org.beatonma.gclocks.io16

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
    options: Io16Options = Io16Options(),
    private val updateOnDraw: Boolean = false,
) : GlyphRenderer<Io16Glyph> {
    private var previousNow: Long = getCurrentTimeMillis()
    internal var now: Long = getCurrentTimeMillis()
        set(value) {
            previousNow = field

            val delta = value - previousNow
            segmentAnimationMillis =
                (segmentAnimationMillis + delta.toInt()) % colorCycleDurationMillis
            segmentAnimationOffset =
                progress(
                    segmentAnimationMillis.toFloat(),
                    0f,
                    colorCycleDurationMillis.toFloat()
                )
            field = value
        }

    private val colorCycleDurationMillis = options.colorCycleDurationMillis
    private val stateChangeDurationMillis: Float = options.stateChangeDurationMillis.toFloat()

    private var segmentAnimationMillis: Int = 0

    /* 0..1 progress of the segment animation */
    private var segmentAnimationOffset: Float = 0f

    /* 0..1 portion of the total path length consumed by 'inactive' color. */
    private var inactiveSegmentSize: Float = 0f

    private val style: Stroke = Stroke(options.strokeWidth)

    override fun draw(
        glyph: Io16Glyph,
        canvas: GenericCanvas,
        glyphProgress: Float,
        paints: Paints,
    ) {
        if (updateOnDraw) {
            now = getCurrentTimeMillis()
        }
        paints as Io16Paints

        // Plot path without drawing anything.
        super.draw(glyph, canvas, glyphProgress, paints)

        canvas.drawPath(Color.Red, Stroke())

        when (glyph.state) {
            GlyphState.Appearing, GlyphState.Activating -> {
                inactiveSegmentSize =
                    progress(
                        (now - glyph.stateChangedAt).toFloat() / stateChangeDurationMillis,
                        0f,
                        stateChangeDurationMillis
                    )
            }

            GlyphState.Disappearing, GlyphState.Deactivating -> {
                inactiveSegmentSize =
                    -progress(
                        (now - glyph.stateChangedAt).toFloat() / stateChangeDurationMillis,
                        0f,
                        stateChangeDurationMillis
                    )
            }

            GlyphState.Inactive, GlyphState.Disappeared -> inactiveSegmentSize = 1f
            GlyphState.Active -> 0f
            GlyphState.DisappearingFromActive, GlyphState.DisappearingFromInactive -> {
                // TODO
            }
        }

        var length: Float = canvas.pathMeasure.length
        var remainingLength: Float = length
        var offset: Float = segmentAnimationOffset * length
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
        canvas.measure()
        if (end < start) {
            pathMeasure.getSegment(start, length, segmentPath)
            pathMeasure.getSegment(0f, end, segmentPath)
        } else {
            pathMeasure.getSegment(start, end, segmentPath)
        }
        canvas.drawPath(segmentPath, color, style)
    }
}