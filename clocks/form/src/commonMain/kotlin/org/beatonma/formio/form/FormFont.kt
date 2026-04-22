package org.beatonma.formio.form

import org.beatonma.formio.core.ClockFont
import org.beatonma.formio.core.glyph.BaseClockGlyph
import org.beatonma.formio.core.glyph.GlyphRole
import org.beatonma.formio.core.options.TimeFormat


class FormFont(isAnimated: Boolean = true) : ClockFont<FormGlyph> {
    override val measurements: ClockFont.Measurements = getMeasurements(isAnimated)
    override fun getGlyphAt(
        index: Int,
        format: TimeFormat,
        secondsGlyphScale: Float,
        previous: FormGlyph?,
        currentTimeMillis: Long,
    ): FormGlyph {
        val role = format.getRole(index)
        val scale = when (role) {
            GlyphRole.Second -> secondsGlyphScale
            else -> 1f
        }

        val init = BaseClockGlyph.Init(role, scale, null, previous?.state, previous?.visibility, currentTimeMillis)

        return FormGlyph(init)
    }

    companion object {
        // Widest width of 00 -> 01 animation
        private const val ZeroOneWidth = 144f + 192f

        // Widest width of 10 -> 11 animation
        private const val TenElevenWidth = 100f + 192f

        private val animatedMeasurements = ClockFont.Measurements(
            lineHeight = FormGlyph.maxSize.y,
            separatorWidth = 48f,
            maxHours24ZeroPaddedWidth = ZeroOneWidth,
            maxHours12ZeroPaddedWidth = ZeroOneWidth,
            maxHours24Width = TenElevenWidth,
            maxHours12Width = TenElevenWidth,
            maxMinutesWidth = ZeroOneWidth,
            maxSecondsWidth = ZeroOneWidth,
        )

        private val staticMeasurements = ClockFont.Measurements(
            lineHeight = FormGlyph.maxSize.y,
            separatorWidth = 48f,
            maxHours24ZeroPaddedWidth = 144f + 144f, // e.g. 20:
            maxHours12ZeroPaddedWidth = 100f + 144f, // 10:
            maxHours24Width = 100f + 144f, // 10:
            maxHours12Width = 100f + 144f, // 10:
            maxMinutesWidth = 144f + 144f, // :00:
            maxSecondsWidth = 144f + 144f, // :00
        )

        fun getMeasurements(isAnimated: Boolean) = when (isAnimated) {
            true -> animatedMeasurements
            false -> staticMeasurements
        }
    }
}
