package org.beatonma.gclocks.io18.characters

import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.paths.PathDefinition
import org.beatonma.gclocks.io18.animation.AnimatedPath
import org.beatonma.gclocks.io18.animation.DiagonalLines
import org.beatonma.gclocks.io18.animation.drawEnter
import org.beatonma.gclocks.io18.animation.drawExit

private const val Width = GlyphCharacter.DefaultWidth
private const val Height = GlyphCharacter.DefaultHeight

internal class Five(
    private val animatedPath: AnimatedPath,
) : GlyphCharacter {
    companion object : GlyphCharacter.Companion {
        override val size = FloatSize(Width, Height)
    }

    override val companion = Companion
    private val topBar = PathDefinition {
        moveTo(136f, 16.2f)
        lineTo(4f, 16.2f)
    }
    private val upperVertical = PathDefinition {
        moveTo(17.1f, 55f)
        lineTo(17.1f, 29f)
    }
    private val lowerVertical = PathDefinition {
        moveTo(17.1f, 55f)
        lineTo(17.1f, 81f)
    }
    private val upperRightCurve = PathDefinition {
        moveTo(70.3f, 94f)
        cubicTo(100f, 94f, 124f, 118f, 124f, 148f)
    }
    private val middleHorizontal = PathDefinition {
        moveTo(30.1f, 94f)
        lineTo(70.3f, 94f)
    }
    private val bottomCurve = PathDefinition {
        moveTo(124f, 148f)
        cubicTo(124f, 176f, 100f, 200f, 70.3f, 200f)
        cubicTo(40.8f, 200f, 16.6f, 176f, 16.6f, 148f)
    }
    private val lines = DiagonalLines(4f, 5.6f, 4, 10, DiagonalLines.Direction.Backward)


    override fun drawEnter(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        animatedPath.drawEnter(canvas, glyphProgress, colors[3], topBar)
        animatedPath.drawEnter(canvas, glyphProgress, colors[3], upperVertical)
        animatedPath.drawEnter(canvas, glyphProgress, colors[1], lowerVertical)
        animatedPath.drawEnter(canvas, glyphProgress, colors[2], upperRightCurve)
        animatedPath.drawEnter(canvas, glyphProgress, colors[1], middleHorizontal)
        animatedPath.drawEnter(canvas, glyphProgress, colors[0], bottomCurve)
        lines.drawEnter(canvas, glyphProgress, colors[0])
    }

    override fun drawExit(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        animatedPath.drawExit(canvas, glyphProgress, colors[3], topBar)
        animatedPath.drawExit(canvas, glyphProgress, colors[3], upperVertical)
        animatedPath.drawExit(canvas, glyphProgress, colors[1], lowerVertical)
        animatedPath.drawExit(canvas, glyphProgress, colors[2], upperRightCurve)
        animatedPath.drawExit(canvas, glyphProgress, colors[1], middleHorizontal)
        animatedPath.drawExit(canvas, glyphProgress, colors[0], bottomCurve)
        lines.drawExit(canvas, glyphProgress, colors[0])
    }
}
