package org.beatonma.formio.io16

import org.beatonma.formio.core.ClockFont
import org.beatonma.formio.core.glyph.BaseClockGlyph
import org.beatonma.formio.core.glyph.GlyphRole
import org.beatonma.formio.core.glyph.GlyphState
import org.beatonma.formio.core.options.TimeFormat
import org.beatonma.formio.core.types.ProgressFloat
import org.beatonma.formio.core.types.pf
import org.beatonma.formio.core.util.debug
import kotlin.random.Random


class Io16Font(
    isAnimated: Boolean = true,
    private val debugGetGlyphAt: ((defaultGlyph: Io16Glyph) -> Io16Glyph)? = null,

    /**
     * If true, segment animation will have a random offset for each glyph.
     * If false, segment animation will be synchronised across all glyphs.
     */
    private val randomiseSegmentOffset: Boolean = true,
) : ClockFont<Io16Glyph> {
    override val measurements: ClockFont.Measurements = getMeasurements(isAnimated)

    override fun getGlyphAt(
        index: Int,
        format: TimeFormat,
        secondsGlyphScale: Float,
        previous: Io16Glyph?,
        currentTimeMillis: Long,
    ): Io16Glyph {
        val role = format.getRole(index)
        val lock = when (role.isSeparator) {
            true -> GlyphState.Inactive
            false -> null
        }
        val scale = when (role) {
            GlyphRole.Second -> secondsGlyphScale
            else -> 1f
        }
        val animationOffset = previous?.animationOffset ?: getAnimationOffset(randomiseSegmentOffset)

        val init = BaseClockGlyph.Init(
            role, scale, lock, previous?.state, previous?.visibility, currentTimeMillis
        )

        debug {
            val glyph = Io16Glyph(init, animationOffset)
            return debugGetGlyphAt?.invoke(glyph) ?: glyph
        }

        return Io16Glyph(init, animationOffset)
    }

    private fun getAnimationOffset(randomise: Boolean): ProgressFloat =
        when {
            randomise -> Random.nextFloat().pf
            else -> ProgressFloat.Zero
        }


    companion object {
        private val ZeroWidth = Io16GlyphPath.Zero.canonical.width

        /* All widths are at their maximum when progress==0, so static and animated measurements are the same */
        fun getMeasurements(isAnimated: Boolean) = ClockFont.Measurements(
            lineHeight = Io16Glyph.maxSize.y,
            separatorWidth = Io16GlyphPath.Separator.canonical.width,
            maxHours24ZeroPaddedWidth = ZeroWidth * 2f, // 00:xx
            maxHours12ZeroPaddedWidth = ZeroWidth + Io16GlyphPath.Four.canonical.width, // 04:xx
            maxHours24Width = Io16GlyphPath.Two.canonical.width + ZeroWidth, // 20:xx
            maxHours12Width = Io16GlyphPath.One.canonical.width + ZeroWidth, // 10:xx
            maxMinutesWidth = ZeroWidth * 2f, // :00:
            maxSecondsWidth = ZeroWidth * 2f, // :00
        )
    }
}
