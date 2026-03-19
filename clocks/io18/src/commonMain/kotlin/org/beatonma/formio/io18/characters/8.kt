package org.beatonma.formio.io18.characters

import org.beatonma.formio.core.geometry.Angle
import org.beatonma.formio.core.geometry.FloatSize
import org.beatonma.formio.core.graphics.Canvas
import org.beatonma.formio.core.graphics.Color
import org.beatonma.formio.core.graphics.paths.PathDefinition
import org.beatonma.formio.io18.animation.AnimatedPath
import org.beatonma.formio.io18.animation.ConcentricCircles
import org.beatonma.formio.io18.animation.drawEnter
import org.beatonma.formio.io18.animation.drawExit


private const val Width = GlyphCharacter.DefaultWidth
private const val Height = GlyphCharacter.DefaultHeight

internal class Eight(
    private val animatedPath: AnimatedPath,
) : GlyphCharacter {
    companion object : GlyphCharacter.Companion {
        override val size = FloatSize(Width, Height)
    }

    override val companion = Companion

    private val upperRings = ConcentricCircles(70f, 69.5f, listOf(9f, 34f, 62f))
    private val lowerLoop = PathDefinition {
        boundedArc(70f, 147f, 53f, Angle.OneEighty, Angle.ThreeSixty)
    }

    override fun drawEnter(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        upperRings.drawEnter(canvas, glyphProgress, colors)
        animatedPath.drawEnter(canvas, glyphProgress, colors[1], lowerLoop)
    }

    override fun drawExit(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        upperRings.drawExit(canvas, glyphProgress, colors)
        animatedPath.drawExit(canvas, glyphProgress, colors[1], lowerLoop)
    }
}
