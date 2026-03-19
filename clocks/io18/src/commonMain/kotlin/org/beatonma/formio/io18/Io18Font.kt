package org.beatonma.formio.io18

import org.beatonma.formio.core.ClockFont
import org.beatonma.formio.core.glyph.GlyphRole
import org.beatonma.formio.core.glyph.GlyphState
import org.beatonma.formio.core.options.TimeFormat

private val BaseWidth = Io18Glyph.maxSize.width

class Io18Font(
    isAnimated: Boolean = true,
    private val shuffleColors: Boolean = true,
    private val offsetColors: Boolean = true,
) : ClockFont<Io18Glyph> {
    override val measurements: ClockFont.Measurements = getMeasurements(isAnimated)
    private val animations = GlyphAnimations()

    override fun getGlyphAt(
        index: Int,
        format: TimeFormat,
        secondsGlyphScale: Float,
    ): Io18Glyph {
        val role = format.roles.getOrNull(index) ?: GlyphRole.Default
        val lock = when (role.isSeparator) {
            true -> GlyphState.Inactive
            false -> null
        }
        val scale = when (role) {
            GlyphRole.Second -> secondsGlyphScale
            else -> 1f
        }

        return Io18Glyph(
            animations,
            role,
            scale,
            lock,
            shuffleColors = shuffleColors,
            colorsOffset = if (offsetColors) index else 0
        )
    }

    companion object {
        /* All widths are at their maximum when progress==0, so static and animated measurements are the same */
        fun getMeasurements(isAnimated: Boolean) = ClockFont.Measurements(
            lineHeight = Io18Glyph.maxSize.height,
            separatorWidth = 24f,
            maxHours24ZeroPaddedWidth = BaseWidth * 2f,
            maxHours12ZeroPaddedWidth = BaseWidth * 2f,
            maxHours24Width = BaseWidth * 2f,
            maxHours12Width = BaseWidth * 2f,
            maxMinutesWidth = BaseWidth * 2f,
            maxSecondsWidth = BaseWidth * 2f,
        )
    }
}
