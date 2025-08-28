package org.beatonma.gclocks.io18

import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.core.graphics.StrokeJoin
import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.LayoutOptions
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat

data class Io18Paints(
    override val colors: Array<Color> = DefaultColors,
    override val strokeCap: StrokeCap = StrokeCap.Butt,
    override val strokeJoin: StrokeJoin = StrokeJoin.Round,
) : Paints {
    init {
        require(colors.size == 4)
    }

    // Stroke width is not configurable.
    override val strokeWidth: Float = 0f

    companion object {
        const val ThickStrokeWidth: Float = 26f
        const val HalfStrokeWidth: Float = 13f
        const val ThinStrokeWidth: Float = 3f

        val thickStroke = Stroke(ThickStrokeWidth)
        val thinStroke = Stroke(ThinStrokeWidth, cap = StrokeCap.Round)

        val DefaultColors = arrayOf(
            Color(0xff1de9b6), // Turquoiseish
            Color(0xffff6c00), // Orangeish
            Color(0xfffdd835), // Yellowish
            Color(0xff536dfe), // Blueish
        )

        fun getRandomPaintIndices(): IntArray = IntArray(4) { it }.apply { shuffle() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Io18Paints

        if (strokeWidth != other.strokeWidth) return false
        if (!colors.contentEquals(other.colors)) return false
        if (strokeCap != other.strokeCap) return false
        if (strokeJoin != other.strokeJoin) return false

        return true
    }

    override fun hashCode(): Int {
        var result = strokeWidth.hashCode()
        result = 31 * result + colors.contentHashCode()
        result = 31 * result + strokeCap.hashCode()
        result = 31 * result + strokeJoin.hashCode()
        return result
    }
}

data class Io18Options(
    override val paints: Io18Paints = Io18Paints(),
    override val layout: Io18LayoutOptions = Io18LayoutOptions(),
    override val glyph: Io18GlyphOptions = Io18GlyphOptions(),
) : Options<Io18Paints>

data class Io18LayoutOptions(
    override val layout: Layout = Layout.Wrapped,
    override val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.End,
    override val verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
    override val format: TimeFormat = TimeFormat.HH_MM_SS_24,
    override val spacingPx: Int = 13,
    override val secondsGlyphScale: Float = Options.DefaultSecondsGlyphScale,
) : LayoutOptions

data class Io18GlyphOptions(
    override val activeStateDurationMillis: Int = 5000,
    override val stateChangeDurationMillis: Int = 1200,
    override val glyphMorphMillis: Int = 600,
) : GlyphOptions