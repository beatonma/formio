package org.beatonma.gclocks.form

import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.LayoutOptions
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.core.graphics.StrokeJoin


data class FormOptions(
    override val glyph: FormGlyphOptions = FormGlyphOptions(),
    override val layout: FormLayoutOptions = FormLayoutOptions(),
    override val paints: FormPaints = FormPaints(),
) : Options<FormPaints>

data class FormGlyphOptions(
    override val activeStateDurationMillis: Int = 0,
    override val stateChangeDurationMillis: Int = 0,
    override val glyphMorphMillis: Int = 800,
) : GlyphOptions

data class FormLayoutOptions(
    override val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Start,
    override val verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
    override val layout: Layout = Layout.Horizontal,
    override val format: TimeFormat = TimeFormat.HH_MM_SS_24,
    override val spacingPx: Int = 8,
    override val secondsGlyphScale: Float = Options.DefaultSecondsGlyphScale,
) : LayoutOptions

data class FormPaints(
    override val colors: Array<Color> = DefaultColors,
) : Paints {
    init {
        require(colors.size == 3)
    }

    override val strokeWidth: Float = 0f
    override val strokeCap: StrokeCap = StrokeCap.Square
    override val strokeJoin: StrokeJoin = StrokeJoin.Miter

    companion object {
        val DefaultColors: Array<Color>
            get() = arrayOf(
                Color(0xffFF6D00), // Orange
                Color(0xffFFC400), // Yellow
                Color(0xffFFFFFF), // White
            )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FormPaints

        if (strokeWidth != other.strokeWidth) return false
        if (!colors.contentEquals(other.colors)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = strokeWidth.hashCode()
        result = 31 * result + colors.contentHashCode()
        return result
    }
}
