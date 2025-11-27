package org.beatonma.gclocks.core.glyph

import org.beatonma.gclocks.core.fixtures.TestGlyph
import org.beatonma.gclocks.core.fixtures.TestGlyphOptions
import org.beatonma.gclocks.core.util.getCurrentTimeMillis
import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test

private class DesyncTimeProvider : Iterator<Long> {
    private var index = 0
    override fun hasNext(): Boolean = index < 100

    override fun next(): Long {
        return ++index * 101L
    }
}

private val options = TestGlyphOptions(
    activeStateDurationMillis = 100,
    stateChangeDurationMillis = 100,
    visibilityChangeDurationMillis = 100,
    glyphMorphMillis = 100,
)

private fun testGlyph(lock: GlyphState? = null) =
    TestGlyph(TestGlyph.Type.Desynchronized, lock = lock, currentTimeMillis = 0)


class DesyncGlyphTest {
    @Test
    fun `setState is correct`() {
        with(testGlyph()) {
            state shouldbe GlyphState.Active

            setState(GlyphState.Inactive, force = true, currentTimeMillis = getCurrentTimeMillis())
            state shouldbe GlyphState.Inactive

            setState(
                GlyphState.Activating,
                force = true,
                currentTimeMillis = getCurrentTimeMillis()
            )
            state shouldbe GlyphState.Activating

            setState(
                GlyphState.Deactivating,
                force = true,
                currentTimeMillis = getCurrentTimeMillis()
            )
            state shouldbe GlyphState.Deactivating

            setState(GlyphState.Active, force = true, currentTimeMillis = getCurrentTimeMillis())
            state shouldbe GlyphState.Active
        }

        with(testGlyph()) {
            state shouldbe GlyphState.Active

            setState(GlyphState.Inactive, force = false, currentTimeMillis = getCurrentTimeMillis())
            state shouldbe GlyphState.Deactivating
        }

        with(testGlyph()) {
            setState(GlyphState.Inactive, force = true, currentTimeMillis = getCurrentTimeMillis())

            setState(GlyphState.Active, force = false, currentTimeMillis = getCurrentTimeMillis())
            state shouldbe GlyphState.Activating
        }
    }

    @Test
    fun `setState with visibility is correct`() {
        with(testGlyph()) {
            visibility shouldbe GlyphVisibility.Appearing

            setState(
                GlyphVisibility.Disappearing,
                force = true,
                currentTimeMillis = getCurrentTimeMillis()
            )
            visibility shouldbe GlyphVisibility.Disappearing

            setState(
                GlyphVisibility.Appearing,
                force = true,
                currentTimeMillis = getCurrentTimeMillis()
            )
            visibility shouldbe GlyphVisibility.Appearing

            setState(
                GlyphVisibility.Hidden,
                force = true,
                currentTimeMillis = getCurrentTimeMillis()
            )
            visibility shouldbe GlyphVisibility.Hidden
        }

        with(testGlyph()) {
            visibility shouldbe GlyphVisibility.Appearing

            setState(
                GlyphVisibility.Hidden,
                force = false,
                currentTimeMillis = getCurrentTimeMillis()
            )
            visibility shouldbe GlyphVisibility.Disappearing
        }

        with(testGlyph()) {
            setState(
                GlyphVisibility.Hidden,
                force = true,
                currentTimeMillis = getCurrentTimeMillis()
            )

            setState(
                GlyphVisibility.Visible,
                force = false,
                currentTimeMillis = getCurrentTimeMillis()
            )
            visibility shouldbe GlyphVisibility.Appearing
        }
    }

    @Test
    fun `tickState is correct`() {
        val time = DesyncTimeProvider()
        with(testGlyph()) {
            state shouldbe GlyphState.Active

            tick(options, time.next())
            state shouldbe GlyphState.Deactivating

            tick(options, time.next())
            state shouldbe GlyphState.Inactive

            tick(options, time.next())
            state shouldbe GlyphState.Inactive // no change

            setState(GlyphState.Active, currentTimeMillis = time.next())
            state shouldbe GlyphState.Activating

            tick(options, time.next())
            state shouldbe GlyphState.Active
        }
    }

    @Test
    fun `lock is correct`() {
        val time = DesyncTimeProvider()
        with(testGlyph(lock = GlyphState.Active)) {
            state shouldbe GlyphState.Active

            tick(options, time.next())
            state shouldbe GlyphState.Active
            tick(options, time.next())
            state shouldbe GlyphState.Active

            setState(GlyphState.Inactive, currentTimeMillis = time.next())
            state shouldbe GlyphState.Active
        }

        with(testGlyph(lock = GlyphState.Inactive)) {
            state shouldbe GlyphState.Inactive

            tick(options, time.next())
            state shouldbe GlyphState.Inactive
            tick(options, time.next())
            state shouldbe GlyphState.Inactive

            setState(GlyphState.Active, currentTimeMillis = time.next())
            state shouldbe GlyphState.Inactive
        }


        with(testGlyph(lock = GlyphState.Activating)) {
            state shouldbe GlyphState.Activating

            tick(options, time.next())
            state shouldbe GlyphState.Active
            tick(options, time.next())
            state shouldbe GlyphState.Active

            setState(GlyphState.Inactive, currentTimeMillis = time.next())
            state shouldbe GlyphState.Active
        }
    }
}
