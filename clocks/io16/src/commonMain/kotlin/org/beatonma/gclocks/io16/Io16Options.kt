package org.beatonma.gclocks.io16

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

data class Io16Options(
    override val glyph: Io16GlyphOptions = Io16GlyphOptions(),
    override val layout: Io16LayoutOptions = Io16LayoutOptions(),
    override val paints: Io16Paints = Io16Paints(),
) : Options<Io16Paints>

data class Io16GlyphOptions(
    override val activeStateDurationMillis: Int = 5000,
    override val stateChangeDurationMillis: Int = 1200,
    override val glyphMorphMillis: Int = 600,
    // How long a path segment takes to complete a circuit
    val colorCycleDurationMillis: Int = 5000,
) : GlyphOptions

data class Io16LayoutOptions(
    override val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.End,
    override val verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
    override val layout: Layout = Layout.Vertical,
    override val format: TimeFormat = TimeFormat.HH_MM_SS_24,
    override val spacingPx: Int = 8,
    override val secondsGlyphScale: Float = Options.DefaultSecondsGlyphScale,
) : LayoutOptions

data class Io16Paints(
    override val colors: Array<Color> = DefaultColors,
    override val strokeWidth: Float = 8f,
) : Paints {
    init {
        require(colors.size == 5)
    }

    override val strokeCap: StrokeCap = StrokeCap.Round
    override val strokeJoin: StrokeJoin = StrokeJoin.Round

    val active: Array<Color> = colors.copyOfRange(0, 4)
    val inactive: Color = colors.last()

    companion object {
        val DefaultColors = arrayOf(
            Color(0xffef5350), // Reddish
            Color(0xff8cf2f2), // Cyanish
            Color(0xff33c9dc), // DarkCyanish
            Color(0xff5c6bc0), // Indigoish
            Color(0xff78909c), // Inactive
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Io16Paints

        if (strokeWidth != other.strokeWidth) return false
        if (!active.contentEquals(other.active)) return false
        if (inactive != other.inactive) return false

        return true
    }

    override fun hashCode(): Int {
        var result = strokeWidth.hashCode()
        result = 31 * result + active.contentHashCode()
        result = 31 * result + inactive.hashCode()
        return result
    }
}