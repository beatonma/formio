package org.beatonma.gclocks.io18.characters

import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.core.graphics.paths.PathDefinition
import org.beatonma.gclocks.io18.animation.ConcentricCircles
import org.beatonma.gclocks.io18.animation.Tube
import org.beatonma.gclocks.io18.animation.drawEnter
import org.beatonma.gclocks.io18.animation.drawExit


private const val Width = GlyphCharacter.DefaultWidth
private const val Height = GlyphCharacter.DefaultHeight

internal class Six(
    private val tube: Tube,
) : GlyphCharacter {
    companion object : GlyphCharacter.Companion {
        override val size = FloatSize(Width, Height)
    }

    override val companion = Companion

    private val tubePath = PathDefinition {
        moveTo(68.1f, 22f)
        lineTo(13.5f, 128f)
    }
    private val rings = ConcentricCircles(70.4f, 148f, listOf(9f, 34f, 62f))

    override fun drawEnter(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        rings.drawEnter(canvas, glyphProgress, colors)
        tube.drawEnter(canvas, glyphProgress, colors[1], colors[3], StrokeCap.Round, tubePath)
    }

    override fun drawExit(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        rings.drawExit(canvas, glyphProgress, colors)
        tube.drawExit(canvas, glyphProgress, colors[1], colors[3], StrokeCap.Round, tubePath)
    }
}
