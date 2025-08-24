package org.beatonma.gclocks.io16

import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.core.types.NormalFloat
import org.beatonma.gclocks.core.types.nf
import org.beatonma.gclocks.core.util.debug
import kotlin.random.Random


private val ZeroWidth = Io16GlyphPath.Zero.canonical.width


class Io16Font(
    private val debugGetGlyphAt: ((defaultGlyph: Io16Glyph) -> Io16Glyph)? = null,

    /**
     * If true, segment animation will have a random offset for each glyph.
     * If false, segment animation will be synchronised across all glyphs.
     */
    private val randomiseSegmentOffset: Boolean = true,
) : ClockFont<Io16Paints, Io16Glyph> {
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
        val lock = when (role.isSeparator) {
            true -> GlyphState.Inactive
            false -> null
        }
        val scale = when (role) {
            GlyphRole.Second -> secondsGlyphScale
            else -> 1f
        }
        val animationOffset: () -> NormalFloat = when (randomiseSegmentOffset) {
            true -> {
                { Random.nextFloat().nf }
            }

            false -> {
                { NormalFloat.Zero }
            }
        }

        debug {
            val glyph = Io16Glyph(role, scale, lock, animationOffset())
            return debugGetGlyphAt?.invoke(glyph) ?: glyph
        }

        return Io16Glyph(role, scale, lock, animationOffset())
    }
}
