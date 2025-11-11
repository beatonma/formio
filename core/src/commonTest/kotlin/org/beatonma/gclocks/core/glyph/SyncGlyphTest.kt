package org.beatonma.gclocks.core.glyph

import org.beatonma.gclocks.core.fixtures.TestGlyph
import org.beatonma.gclocks.core.fixtures.TestGlyphOptions
import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test

private class SyncTimeProvider(private val values: List<Long>) : Iterator<Long> {
    private var index = 0
    override fun hasNext(): Boolean = index < values.size

    override fun next(): Long {
        return values[index++]
    }
}

private val options = TestGlyphOptions(
    activeStateDurationMillis = 100,
    stateChangeDurationMillis = 100,
    visibilityChangeDurationMillis = 100, // Unused
    glyphMorphMillis = 100,
)

private fun testGlyph(key: String? = null, lock: GlyphState? = null) =
    TestGlyph(TestGlyph.Type.Synchronized, lock = lock, currentTimeMillis = 0).apply {
        key?.let(::setKey)
    }


class SyncGlyphTest {
    @Test
    fun `setState is correct`() {
        val time = SyncTimeProvider(listOf(101, 1001))
        with(testGlyph(key = "1")) {
            visibility shouldbe GlyphVisibility.Appearing
            key shouldbe " _1"

            tick(options, time.next())
            visibility shouldbe GlyphVisibility.Appearing

            tick(options, time.next())
            visibility shouldbe GlyphVisibility.Visible
        }
    }
}
