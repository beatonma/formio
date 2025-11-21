package org.beatonma.gclocks.core.fixtures


import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.glyph.ClockGlyph
import org.beatonma.gclocks.core.glyph.ClockGlyphDesynchronizedVisibility
import org.beatonma.gclocks.core.glyph.ClockGlyphSynchronizedVisibility
import org.beatonma.gclocks.core.glyph.GlyphCompanion
import org.beatonma.gclocks.core.glyph.GlyphRole
import org.beatonma.gclocks.core.glyph.GlyphState
import org.beatonma.gclocks.core.glyph.RenderGlyph
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.getCurrentTimeMillis
import org.beatonma.gclocks.core.util.timeOfDay
import kotlin.time.Instant


fun TestGlyph(
    type: TestGlyph.Type,
    role: GlyphRole = GlyphRole.Default,
    scale: Float = 1f,
    separatorWidth: Float = 0f,
    lock: GlyphState? = null,
    currentTimeMillis: Long = getCurrentTimeMillis()
): TestGlyph = when (type) {
    TestGlyph.Type.Synchronized -> SynchronizedTestGlyph(
        role,
        scale,
        separatorWidth,
        lock,
        currentTimeMillis
    )

    TestGlyph.Type.Desynchronized -> DesynchronizedTestGlyph(
        role,
        scale,
        separatorWidth,
        lock,
        currentTimeMillis
    )
}

sealed interface TestGlyph : ClockGlyph {
    enum class Type {
        Synchronized,
        Desynchronized,
        ;
    }

    companion object : GlyphCompanion {
        override val maxSize: NativeSize = NativeSize(100f, 100f)
    }
}

private class DesynchronizedTestGlyph(
    role: GlyphRole,
    scale: Float = 1f,
    val separatorWidth: Float = 0f,
    lock: GlyphState? = null,
    currentTimeMillis: Long = getCurrentTimeMillis()
) : ClockGlyphDesynchronizedVisibility(
    role,
    scale,
    lock,
    currentTimeMillis = currentTimeMillis
),
    TestGlyph {
    override val companion: GlyphCompanion = TestGlyph

    override fun getWidthAtProgress(glyphProgress: Float): Float {
        return when (clockKey) {
            ClockGlyph.Key.Separator -> separatorWidth
            else -> companion.maxSize.width
        }
    }

    override fun Canvas.drawZeroOne(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawOneTwo(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawTwoThree(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawThreeFour(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawFourFive(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawFiveSix(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawSixSeven(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawSevenEight(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEightNine(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawNineZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawOneZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawTwoZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawTwoOne(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawThreeZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawFiveZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawZeroEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawOneEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawTwoEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawThreeEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawFourEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawFiveEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawSixEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawSevenEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEightEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawNineEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyOne(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyTwo(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyThree(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyFour(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyFive(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptySix(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptySeven(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyEight(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyNine(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawSeparator(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptySeparator(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawSeparatorEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawSpace(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }
}

private class SynchronizedTestGlyph(
    role: GlyphRole,
    scale: Float = 1f,
    val separatorWidth: Float,
    lock: GlyphState?,
    currentTimeMillis: Long
) : ClockGlyphSynchronizedVisibility(
    role,
    scale,
    lock,
    currentTimeMillis = currentTimeMillis
), TestGlyph {
    override val companion: GlyphCompanion = TestGlyph
    private var previousMillis: Int = 0

    override fun getWidthAtProgress(glyphProgress: Float): Float {
        return when (clockKey) {
            ClockGlyph.Key.Separator -> separatorWidth
            else -> companion.maxSize.width
        }
    }

    override fun tick(options: GlyphOptions, currentTimeMillis: Long) {
        val time = Instant.fromEpochMilliseconds(currentTimeMillis).timeOfDay
        debug(time.millisecond)
        if (time.millisecond < previousMillis) {
            onSecondChange(currentTimeMillis)
        }
        previousMillis = time.millisecond
        super.tick(options, currentTimeMillis)
    }

    override fun Canvas.drawZeroOne(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawOneTwo(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawTwoThree(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawThreeFour(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawFourFive(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawFiveSix(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawSixSeven(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawSevenEight(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEightNine(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawNineZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawOneZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawTwoZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawTwoOne(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawThreeZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawFiveZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawZeroEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawOneEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawTwoEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawThreeEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawFourEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawFiveEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawSixEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawSevenEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEightEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawNineEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyOne(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyTwo(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyThree(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyFour(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyFive(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptySix(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptySeven(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyEight(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptyNine(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawSeparator(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawEmptySeparator(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawSeparatorEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }

    override fun Canvas.drawSpace(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
    }
}
