package org.beatonma.gclocks.io18

import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.glyph.GlyphRole
import org.beatonma.gclocks.core.glyph.GlyphState
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.options.TimeFormat

private val BaseWidth = Io18Glyph.maxSize.width

class Io18Font(
    path: Path,
    isAnimated: Boolean = true,
    private val shuffleColors: Boolean = true,
    private val offsetColors: Boolean = true,
) : ClockFont<Io18Paints, Io18Glyph> {
    override val measurements: ClockFont.Measurements = getMeasurements(isAnimated)
    private val animations = GlyphAnimations(path)

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
