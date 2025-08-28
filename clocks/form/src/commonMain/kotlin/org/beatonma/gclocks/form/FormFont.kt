package org.beatonma.gclocks.form

import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.options.TimeFormat
import kotlin.Int

// Widest width of 00 -> 01 animation
private const val ZeroOneWidth = 144f + 192f

// Widest width of 10 -> 11 animation
private const val TenElevenWidth = 100f + 192f

class FormFont : ClockFont<FormPaints, FormGlyph> {
    override val lineHeight: Float = FormGlyph.maxSize.y
    override val separatorWidth: Float = 48f

    override val maxHours24ZeroPaddedWidth: Float = ZeroOneWidth
    override val maxHours12ZeroPaddedWidth: Float = ZeroOneWidth
    override val maxHours24Width: Float = TenElevenWidth
    override val maxHours12Width: Float = TenElevenWidth
    override val maxMinutesWidth: Float = ZeroOneWidth
    override val maxSecondsWidth: Float = ZeroOneWidth

    override fun getGlyphAt(index: Int, format: TimeFormat, secondsGlyphScale: Float): FormGlyph {
        val role = format.roles.getOrNull(index) ?: GlyphRole.Default
        val scale = when (role) {
            GlyphRole.Second -> secondsGlyphScale
            else -> 1f
        }
        return FormGlyph(role, scale)
    }
}
