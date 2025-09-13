package org.beatonma.gclocks.core.layout

import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.ClockGlyph
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.geometry.ConstrainedLayout
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.geometry.ScaledSize
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.TimeOfDay


class ClockLayout<P : Paints, G : ClockGlyph<P>>(
    private val font: ClockFont<P, G>,
    options: Options<P>,
) : ConstrainedLayout {
    var options: Options<P> = options
        set(value) {
            field = value
            onOptionsChange(value)
        }

    private lateinit var layout: Layout<P, G> // Initialized in onOptionsChange
    private lateinit var glyphs: Glyphs<P, G> // Initialized in onOptionsChange

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

    private fun onOptionsChange(value: Options<P>) {
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

    fun setState(state: GlyphState, force: Boolean = true) {
        glyphs.setState(state, force)
    }

    override fun toString(): String {
        return "ClockLayout(Constraints=${layout.constraints}, scale=$scale)"
    }
}
