package org.beatonma.gclocks.io18

import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.options.TimeFormat

private val BaseWidth = Io18Glyph.maxSize.width

class Io18Font(
    path: Path,
    private val shuffleColors: Boolean = true,
) :
    ClockFont<Io18Paints, Io18Glyph> {
    override val lineHeight: Float = Io18Glyph.maxSize.height
    override val separatorWidth: Float = 24f
    override val maxHours24ZeroPaddedWidth: Float = BaseWidth * 2f
    override val maxHours12ZeroPaddedWidth: Float = BaseWidth * 2f
    override val maxHours24Width: Float = BaseWidth * 2f
    override val maxHours12Width: Float = BaseWidth * 2f
    override val maxMinutesWidth: Float = BaseWidth * 2f
    override val maxSecondsWidth: Float = BaseWidth * 2f

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

        return Io18Glyph(animations, role, scale, lock, shuffleColors = shuffleColors)
    }
}
