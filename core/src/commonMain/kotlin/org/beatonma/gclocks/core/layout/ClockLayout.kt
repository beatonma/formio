package org.beatonma.gclocks.core.layout

import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.MeasureStrategy
import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.geometry.ScaledSize
import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.core.util.debug


class ClockLayout<G : BaseClockGlyph>(
    private val font: ClockFont<G>,
    options: Options,
    private val measureStrategy: MeasureStrategy,
) {
    var options: Options = options
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

    private fun onOptionsChange(value: Options) {
        val layoutOptions = value.layout
        layout = getLayout(
            layoutOptions,
            font.measure(
                layoutOptions.format,
                layoutOptions.layout,
                layoutOptions.spacingPx,
                layoutOptions.secondsGlyphScale,
            )
        )
        glyphs = Glyphs(font, value)
    }

    fun update(time: TimeOfDay) {
        glyphs.update(time)
    }

    fun setAvailableSize(available: Size<Float>): ScaledSize {
        layout.availableSize = available
        debug("setAvailableSize $available")
        if (available.isEmpty) {
            setScale(0f)
            return ScaledSize.Zero
        }

        return setScale(
            measureStrategy.measureScale(nativeSize, available)
        )
    }

    private fun setScale(scale: Float): ScaledSize {
        val measuredSize = layout.setScale(scale)
        isDrawable = scale > 0f && !measuredSize.isEmpty
        return measuredSize
    }

    internal fun layoutPass(callback: GlyphCallback<G>) {
        layout.layoutPass(glyphs.glyphs, callback)
    }

    internal fun measureFrame(callback: OnMeasure) {
        layout.measureFrame(glyphs.glyphs, callback)
    }
}
