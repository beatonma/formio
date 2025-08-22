package org.beatonma.gclocks.core.layout

import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.geometry.ConstrainedLayout
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.geometry.ScaledSize
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.TimeOfDay


class ClockLayout<G : BaseClockGlyph>(
    private val font: ClockFont<G>,
    options: Options<*>,
) : ConstrainedLayout {
    var options: Options<*> = options
        set(value) {
            field = value
            onOptionsChange(value)
        }

    private lateinit var layout: Layout<G> // Initialized in onOptionsChange
    private lateinit var glyphs: Glyphs<G> // Initialized in onOptionsChange

    /**
     * The maximum size the clock can be with 1x scaling.
     * Based on the values of `options.format` and `options.layout`.
     */
    val nativeSize: NativeSize
        get() = layout.nativeSize

    init {
        onOptionsChange(options)
    }

    val animationTimeMillis get() = glyphs.animationTimeMillis.toInt()
    val scale: Float get() = layout.scale

    var isDrawable: Boolean = false
        private set

    private fun onOptionsChange(value: Options<*>) {
        layout = getLayout(
            value.layout,
            value.paints,
            font.measure(value)
        )
        glyphs = Glyphs(font, value)
    }

    fun update(time: TimeOfDay) {
        glyphs.update(time)
    }

    override fun setConstraints(constraints: MeasureConstraints): ScaledSize {
        layout.constraints = constraints

        return setScale(
            constraints.measureScale(nativeSize)
        )
    }

    private fun setScale(scale: Float): ScaledSize {
        val measuredSize = layout.setScale(scale)
        isDrawable = scale > 0f && !measuredSize.isZeroArea
        return measuredSize
    }

    internal fun layoutPass(callback: GlyphCallback<G>) {
        layout.layoutPass(glyphs.glyphs, callback)
    }

    internal fun measureFrame(callback: OnMeasure) {
        layout.measureFrame(glyphs.glyphs, callback)
    }
}
