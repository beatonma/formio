package org.beatonma.formio.core.layout

import org.beatonma.formio.core.ClockFont
import org.beatonma.formio.core.geometry.ConstrainedLayout
import org.beatonma.formio.core.geometry.MeasureConstraints
import org.beatonma.formio.core.geometry.NativeSize
import org.beatonma.formio.core.geometry.ScaledSize
import org.beatonma.formio.core.glyph.ClockGlyph
import org.beatonma.formio.core.options.AnyOptions
import org.beatonma.formio.core.util.getCurrentTimeMillis
import kotlin.reflect.KClass
import kotlin.time.Instant


class ClockLayout<G : ClockGlyph>(
    font: ClockFont<G>,
    options: AnyOptions,
    state: State<G>? = null,
) : ConstrainedLayout {
    private val layout: Layout<G> = getLayout(options.layout, options.paints, font.measure(options))
    private val glyphController: GlyphsController<G> =
        GlyphsController(font, options, state?.glyphs, getCurrentTimeMillis())

    /**
     * The maximum size the clock can be with 1x scaling.
     * Based on the values of `options.format` and `options.layout`.
     */
    val nativeSize: NativeSize get() = layout.nativeSize

    val animationTimeMillis: Int get() = glyphController.animationTimeMillis.toInt()
    val scale: Float get() = layout.scale

    var isDrawable: Boolean = false
        private set

    val isSynchronizedVisibility: Boolean = glyphController.isSynchronizedVisibility
    val length: Int get() = glyphController.glyphs.size
    val glyphClass: KClass<out G> get() = glyphController.glyphs[0].glyph::class

    fun update(time: Instant) {
        glyphController.update(time)
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
        layout.layoutPass(glyphController.glyphs, callback)
    }

    internal fun measureFrame(callback: OnMeasure) {
        layout.measureFrame(glyphController.glyphs, callback)
    }

    internal fun forEachGlyph(block: (glyph: G) -> Unit) {
        glyphController.forEach(block)
    }

    internal fun <R> mapGlyphs(block: (index: Int, glyph: G) -> R) = glyphController.map(block)

    fun getGlyphAt(x: Float, y: Float): G? {
        var g: G? = null
        layout.measureFrame(glyphController.glyphs) { _, _, scale ->
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

    internal fun exportState() = State(this.glyphController)

    class State<G : ClockGlyph> internal constructor(
        internal val glyphs: GlyphsController<G>,
    )
}
