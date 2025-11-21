package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.fixtures.TestFont
import org.beatonma.gclocks.core.fixtures.TestGlyph
import org.beatonma.gclocks.core.fixtures.TestLayoutOptions
import org.beatonma.gclocks.core.fixtures.TestPaints
import org.beatonma.gclocks.core.fixtures.createTestOptions
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test


abstract class FontLayoutTest {
    val glyphWidth = TestGlyph.maxSize.width
    val glyphHeight = TestGlyph.maxSize.height

    val hoursWidth = glyphWidth * 2f
    val minutesWidth = glyphWidth * 2f
    val secondsWidth = glyphWidth * 2f * (0.5f /* Options.DefaultSecondsGlyphScale */)

    val separator = 8f
    val spacing = 10
}

private fun options(
    format: TimeFormat,
    layout: Layout,
    spacingPx: Int,
) = createTestOptions(
    layout = TestLayoutOptions(
        format = format,
        layout = layout,
        spacingPx = spacingPx,
    ),
    paints = TestPaints(strokeWidth = 0f)
)

private fun testFont(separatorWidth: Float) = TestFont(
    TestFont.DefaultMeasurements.copy(separatorWidth = separatorWidth)
)

class HorizontalFontTest : FontLayoutTest() {
    @Test
    fun `measure horizontal layout without spacing`() {
        testFont(separatorWidth = 0f).measure(
            options(
                TimeFormat.HH_MM_SS_24,
                Layout.Horizontal,
                spacingPx = 0,
            )
        ).apply {
            width shouldbe hoursWidth + minutesWidth + secondsWidth
            height shouldbe glyphHeight
        }

        /* Without seconds */
        testFont(separatorWidth = 0f).measure(
            options(
                TimeFormat.HH_MM_24,
                Layout.Horizontal,
                spacingPx = 0,
            )
        ).apply {
            width shouldbe hoursWidth + minutesWidth
            height shouldbe glyphHeight
        }
    }

    @Test
    fun `measure horizontal layout with spacing`() {
        testFont(separatorWidth = separator).measure(
            options(
                TimeFormat.HH_MM_SS_24,
                Layout.Horizontal,
                spacingPx = spacing,
            )
        ).apply {
            width shouldbe hoursWidth + minutesWidth + secondsWidth + (spacing * 5.5f) + separator
            height shouldbe glyphHeight
        }

        /* Without seconds */
        testFont(separatorWidth = separator).measure(
            options(
                TimeFormat.HH_MM_24,
                Layout.Horizontal,
                spacingPx = spacing,
            )
        ).apply {
            width shouldbe hoursWidth + minutesWidth + (spacing * 4) + separator
            height shouldbe glyphHeight
        }
    }
}

class VerticalFontTest : FontLayoutTest() {
    @Test
    fun `measure vertical layout without spacing`() {
        testFont(separatorWidth = 0f).measure(
            options(
                TimeFormat.HH_MM_SS_24,
                Layout.Vertical,
                spacingPx = 0,
            )
        ).apply {
            width shouldbe hoursWidth
            height shouldbe (2.5f * glyphHeight)
        }

        /* Without seconds */
        testFont(separatorWidth = 0f).measure(
            options(
                TimeFormat.HH_MM_24,
                Layout.Vertical,
                spacingPx = 0,
            )
        ).apply {
            width shouldbe hoursWidth
            height shouldbe (2f * glyphHeight)
        }
    }

    @Test
    fun `measure vertical layout with spacing`() {
        testFont(separatorWidth = separator).measure(
            options(
                TimeFormat.HH_MM_SS_24,
                Layout.Vertical,
                spacingPx = spacing,
            )
        ).apply {
            width shouldbe hoursWidth + spacing
            height shouldbe (2.5f * glyphHeight) + (spacing * 2)
        }

        /* Without seconds */
        testFont(separatorWidth = separator).measure(
            options(
                TimeFormat.HH_MM_24,
                Layout.Vertical,
                spacingPx = spacing,
            )
        ).apply {
            width shouldbe hoursWidth + spacing
            height shouldbe (2f * glyphHeight) + spacing
        }
    }
}


class WrappedFontTest : FontLayoutTest() {
    @Test
    fun `measure wrapped layout without spacing`() {
        testFont(separatorWidth = 0f).measure(
            options(
                TimeFormat.HH_MM_SS_24,
                Layout.Wrapped,
                spacingPx = 0,
            )
        ).apply {
            width shouldbe hoursWidth + minutesWidth
            height shouldbe (1.5f * glyphHeight)
        }

        /* Without seconds */
        testFont(separatorWidth = 0f).measure(
            options(
                TimeFormat.HH_MM_24,
                Layout.Wrapped,
                spacingPx = 0,
            )
        ).apply {
            width shouldbe hoursWidth + minutesWidth
            height shouldbe glyphHeight
        }
    }

    @Test
    fun `measure wrapped layout with spacing`() {
        testFont(separatorWidth = separator).measure(
            options(
                TimeFormat.HH_MM_SS_24,
                Layout.Wrapped,
                spacingPx = spacing,
            )
        ).apply {
            width shouldbe hoursWidth + minutesWidth + (spacing * 4) + separator
            height shouldbe (1.5f * glyphHeight) + spacing
        }

        /* Without seconds */
        testFont(separatorWidth = separator).measure(
            options(
                TimeFormat.HH_MM_24,
                Layout.Wrapped,
                spacingPx = spacing,
            )
        ).apply {
            width shouldbe hoursWidth + minutesWidth + (spacing * 4) + separator
            height shouldbe glyphHeight
        }
    }
}
