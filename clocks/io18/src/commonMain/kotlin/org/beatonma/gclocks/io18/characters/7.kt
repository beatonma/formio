package org.beatonma.gclocks.io18.characters

import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.PathDefinition
import org.beatonma.gclocks.io18.Io18Paints
import org.beatonma.gclocks.io18.animation.AnimatedPath
import org.beatonma.gclocks.io18.animation.DiagonalLines
import org.beatonma.gclocks.io18.animation.Rect
import org.beatonma.gclocks.io18.animation.Square
import org.beatonma.gclocks.io18.animation.drawEnter
import org.beatonma.gclocks.io18.animation.drawExit


private const val Width = GlyphCharacter.DefaultWidth
private const val Height = GlyphCharacter.DefaultHeight

internal class Seven(
    private val animatedPath: AnimatedPath,
) : GlyphCharacter {
    companion object : GlyphCharacter.Companion {
        override val size = FloatSize(Width, Height)
    }

    override val companion = Companion

    private val topBar = PathDefinition {
        moveTo(136f, 14f)
        lineTo(4f, 14f)
    }
    private val curve = PathDefinition {
        moveTo(123f, 27f)
        cubicTo(123f, 49.3f, 105f, 67.4f, 82.5f, 67.4f)
    }
    private val upperSquare = Square(56.5f, 80.4f)
    private val lowerSquare = Square(30.5f, 106.4f)
    private val verticalRect = Rect(4.5f, 132.4f, Io18Paints.ThickStrokeWidth, 80f)
    private val lines = DiagonalLines(4.5f, 80.4f, 10, 4, DiagonalLines.Direction.Forward)

    override fun drawEnter(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        animatedPath.drawEnter(canvas, glyphProgress, colors[1], topBar)
        animatedPath.drawEnter(canvas, glyphProgress, colors[0], curve)
        upperSquare.drawEnter(canvas, glyphProgress, colors[3])
        lowerSquare.drawEnter(canvas, glyphProgress, colors[3])
        verticalRect.drawEnter(canvas, glyphProgress, colors[3])
        lines.drawEnter(canvas, glyphProgress, colors[2])
    }

    override fun drawExit(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        animatedPath.drawExit(canvas, glyphProgress, colors[1], topBar)
        animatedPath.drawExit(canvas, glyphProgress, colors[0], curve)
        upperSquare.drawExit(canvas, glyphProgress, colors[3])
        lowerSquare.drawExit(canvas, glyphProgress, colors[3])
        verticalRect.drawExit(canvas, glyphProgress, colors[3])
        lines.drawExit(canvas, glyphProgress, colors[2])
    }
}