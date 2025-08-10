package org.beatonma.gclocks.io16

import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.core.options.TimeResolution


class Io16Font : ClockFont<Io16Glyph> {
    override fun getGlyphAt(index: Int, format: TimeFormat, secondsGlyphScale: Float): Io16Glyph {
        val role = format.roles.getOrNull(index) ?: GlyphRole.Default
        val scale = when (role) {
            GlyphRole.Second -> secondsGlyphScale
            else -> 1f
        }
        return Io16Glyph(role, scale)
    }

    override fun measure(
        format: TimeFormat,
        layout: Layout,
        spacingPx: Int,
        secondsGlyphScale: Float,
    ): Size<Float> {
        val hasSeconds = format.resolution == TimeResolution.Seconds
        val lineHeight = Io16Glyph.maxSize.y
        val separatorWidth = 16f

        // TODO numbers here are from FORM clock implementation
        val pairWidth = 352f // max width of a pair of digits

        return when (layout) {
            Layout.Horizontal -> {
                val digitsWidth = 816f
                val spacingWidth =
                    spacingPx.toFloat() * (if (hasSeconds) 5f + secondsGlyphScale else 4f)
                return FloatSize(
                    digitsWidth + separatorWidth + spacingWidth, lineHeight
                )
            }

            Layout.Vertical -> FloatSize(
                (pairWidth + spacingPx),
                lineHeight * (2f + (if (hasSeconds) secondsGlyphScale else 0f) + (spacingPx.toFloat() * 2f))
            )

            Layout.Wrapped -> FloatSize(
                (pairWidth * 2f) + (spacingPx * 4f),
                (lineHeight * (1f + (if (hasSeconds) secondsGlyphScale else 0f))) + spacingPx.toFloat()
            )
        }
    }
}
