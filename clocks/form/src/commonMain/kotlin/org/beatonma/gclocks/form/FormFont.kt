package org.beatonma.gclocks.form

import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.options.TimeFormat
import kotlin.Int


class FormFont : ClockFont<FormGlyph> {
    override val lineHeight: Float = FormGlyph.maxSize.y
    override val separatorWidth: Float = 48f

    // TODO check actual values
    override val maxHours24ZeroPaddedWidth: Float = 352f
    override val maxHours12ZeroPaddedWidth: Float = 352f
    override val maxHours24Width: Float = 352f
    override val maxHours12Width: Float = 352f
    override val maxMinutesWidth: Float = 352f
    override val maxSecondsWidth: Float = 352f

    override fun getGlyphAt(
        index: Int, format: TimeFormat,
        secondsGlyphScale: Float,
    ): FormGlyph {
        val role = format.roles.getOrNull(index) ?: GlyphRole.Default
        val scale = when (role) {
            GlyphRole.Second -> secondsGlyphScale
            else -> 1f
        }
        return FormGlyph(role, scale)
    }
}
