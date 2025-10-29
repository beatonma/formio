import org.beatonma.gclocks.core.Glyph
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.GlyphVisibility
import org.beatonma.gclocks.core.fixtures.TestGlyph
import org.beatonma.gclocks.core.fixtures.TestGlyphOptions
import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test

private class TimeProvider : Iterator<Long> {
    private var index = 0
    override fun hasNext(): Boolean = index < 100

    override fun next(): Long {
        return ++index * 101L
    }
}

private val options = TestGlyphOptions(
    activeStateDurationMillis = 100,
    stateChangeDurationMillis = 100,
    glyphMorphMillis = 100,
)

private fun testGlyph(): Glyph<*> {
    val timeProvider = TimeProvider()
    return object : TestGlyph(GlyphRole.Default) {
        override fun getCurrentTimeMillis(): Long = timeProvider.next()
    }
}

class GlyphTest {
    @Test
    fun `setState is correct`() {
        with(testGlyph()) {
            state shouldbe GlyphState.Active

            setState(GlyphState.Inactive, force = true)
            state shouldbe GlyphState.Inactive

            setState(GlyphState.Activating, force = true)
            state shouldbe GlyphState.Activating

            setState(GlyphState.Deactivating, force = true)
            state shouldbe GlyphState.Deactivating

            setState(GlyphState.Active, force = true)
            state shouldbe GlyphState.Active
        }

        with(testGlyph()) {
            state shouldbe GlyphState.Active

            setState(GlyphState.Inactive, force = false)
            state shouldbe GlyphState.Deactivating
        }

        with(testGlyph()) {
            setState(GlyphState.Inactive, force = true)

            setState(GlyphState.Active, force = false)
            state shouldbe GlyphState.Activating
        }
    }

    @Test
    fun `setState with visibility is correct`() {
        with(testGlyph()) {
            visibility shouldbe GlyphVisibility.Visible

            setState(GlyphVisibility.Disappearing, force = true)
            visibility shouldbe GlyphVisibility.Disappearing

            setState(GlyphVisibility.Appearing, force = true)
            visibility shouldbe GlyphVisibility.Appearing

            setState(GlyphVisibility.Hidden, force = true)
            visibility shouldbe GlyphVisibility.Hidden
        }

        with(testGlyph()) {
            visibility shouldbe GlyphVisibility.Visible

            setState(GlyphVisibility.Hidden, force = false)
            visibility shouldbe GlyphVisibility.Disappearing
        }

        with(testGlyph()) {
            setState(GlyphVisibility.Hidden, force = true)

            setState(GlyphVisibility.Visible, force = false)
            visibility shouldbe GlyphVisibility.Appearing
        }
    }

    @Test
    fun `tickState is correct`() {
        with(testGlyph()) {
            state shouldbe GlyphState.Active

            tickState(options)
            state shouldbe GlyphState.Deactivating

            tickState(options)
            state shouldbe GlyphState.Inactive

            tickState(options)
            state shouldbe GlyphState.Inactive // no change

            setState(GlyphState.Active)
            state shouldbe GlyphState.Activating

            tickState(options)
            state shouldbe GlyphState.Active
        }
    }
}
