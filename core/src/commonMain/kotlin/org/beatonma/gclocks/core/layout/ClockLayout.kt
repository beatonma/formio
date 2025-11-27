package org.beatonma.gclocks.core.layout

import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.geometry.ConstrainedLayout
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.geometry.ScaledSize
import org.beatonma.gclocks.core.glyph.ClockGlyph
import org.beatonma.gclocks.core.glyph.GlyphState
import org.beatonma.gclocks.core.glyph.GlyphVisibility
import org.beatonma.gclocks.core.options.AnyOptions
import kotlin.time.Instant


class ClockLayout<G : ClockGlyph>(
    private val font: ClockFont<G>,
    options: AnyOptions,
) : ConstrainedLayout {
    var options: AnyOptions = options
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

    private fun onOptionsChange(value: AnyOptions) {
        layout = getLayout(
            value.layout,
            value.paints,
            font.measure(value)
        )
        glyphs = Glyphs(font, value)
    }

    fun update(time: Instant) {
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

    fun setState(
        state: GlyphState,
        visibility: GlyphVisibility,
        force: Boolean = true,
        currentTimeMillis: Long
    ) {
        glyphs.setState(state, visibility, force, currentTimeMillis)
    }

    fun setState(state: GlyphState, force: Boolean = true, currentTimeMillis: Long) {
        glyphs.setState(state, force, currentTimeMillis)
    }

    fun setState(visibility: GlyphVisibility, force: Boolean = true, currentTimeMillis: Long) {
        glyphs.setState(visibility, force, currentTimeMillis)
    }

    fun getGlyphAt(x: Float, y: Float): G? {
        var g: G? = null
        layout.measureFrame(glyphs.glyphs) { _, _, scale ->
            layoutPass { glyph, _, rect ->
                if (rect.contains(x / scale, y / scale)) {
                    g = glyph
                    return@layoutPass
                }
            }
            if (g != null) return@measureFrame
        }
        return g
    }

    override fun toString(): String {
        return "ClockLayout(Constraints=${layout.constraints}, scale=$scale)"
    }
}
