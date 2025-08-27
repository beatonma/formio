package org.beatonma.gclocks.io18.characters

import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.PathDefinition
import org.beatonma.gclocks.io18.animation.AnimatedPath
import org.beatonma.gclocks.io18.animation.Square
import org.beatonma.gclocks.io18.animation.SquareGrid
import org.beatonma.gclocks.io18.animation.drawEnter
import org.beatonma.gclocks.io18.animation.drawExit

private const val Width = GlyphCharacter.DefaultWidth
private const val Height = GlyphCharacter.DefaultHeight


internal class Two(
    private val animatedPath: AnimatedPath,
) : GlyphCharacter {
    companion object : GlyphCharacter.Companion {
        override val size = FloatSize(Width, Height)
    }

    override val companion = Companion

    private val grid = SquareGrid(34.5f, 172.5f, 3, 8)
    private val diagonal = PathDefinition {
        moveTo(8.99f, 201f)
        lineTo(84.55f, 118.3f)
    }
    private val curve = PathDefinition {
        moveTo(16.55f, 66.3f)
        cubicTo(16.55f, 36.8f, 40.35f, 13f, 69.85f, 13f)
        cubicTo(99.55f, 13f, 123.55f, 36.8f, 123.55f, 66.3f)
    }
    private val upperSquare = Square(84.55f, 92.3f)
    private val lowerSquare = Square(110.55f, 66.3f)

    override fun drawEnter(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        animatedPath.drawEnter(canvas, glyphProgress, colors[1], curve)
        upperSquare.drawEnter(canvas, glyphProgress, colors[3])
        lowerSquare.drawEnter(canvas, glyphProgress, colors[3])
        animatedPath.drawEnter(canvas, glyphProgress, colors[2], diagonal)
        grid.drawEnter(canvas, glyphProgress, colors[0])
    }

    override fun drawExit(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        animatedPath.drawExit(canvas, glyphProgress, colors[1], curve)
        upperSquare.drawExit(canvas, glyphProgress, colors[3])
        lowerSquare.drawExit(canvas, glyphProgress, colors[3])
        animatedPath.drawExit(canvas, glyphProgress, colors[2], diagonal)
        grid.drawExit(canvas, glyphProgress, colors[0])
    }
}