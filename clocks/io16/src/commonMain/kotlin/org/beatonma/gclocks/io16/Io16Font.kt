package org.beatonma.gclocks.io16

import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.options.TimeFormat


private val ZeroWidth = Io16GlyphPath.Zero.canonical.width


class Io16Font : ClockFont<Io16Glyph> {
    override val lineHeight: Float = Io16Glyph.maxSize.y
    override val separatorWidth: Float = Io16GlyphPath.Separator.canonical.width
    override val maxHours24ZeroPaddedWidth: Float = ZeroWidth * 2f // 00:xx
    override val maxHours12ZeroPaddedWidth: Float =
        ZeroWidth + Io16GlyphPath.Four.canonical.width // 04:xx
    override val maxHours24Width: Float = Io16GlyphPath.Two.canonical.width + ZeroWidth // 20:xx
    override val maxHours12Width: Float = Io16GlyphPath.One.canonical.width + ZeroWidth // 10:xx
    override val maxMinutesWidth: Float = ZeroWidth * 2f // :00:
    override val maxSecondsWidth: Float = ZeroWidth * 2f // :00

    override fun getGlyphAt(index: Int, format: TimeFormat, secondsGlyphScale: Float): Io16Glyph {
        val role = format.roles.getOrNull(index) ?: GlyphRole.Default
        val scale = when (role) {
            GlyphRole.Second -> secondsGlyphScale
            else -> 1f
        }
        return Io16Glyph(role, scale)
    }
}
