package org.beatonma.gclocks.io18.characters

import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.paths.PathDefinition
import org.beatonma.gclocks.io18.animation.AnimatedPath
import org.beatonma.gclocks.io18.animation.RectangularDotGroup
import org.beatonma.gclocks.io18.animation.Square
import org.beatonma.gclocks.io18.animation.Tube
import org.beatonma.gclocks.io18.animation.drawEnter
import org.beatonma.gclocks.io18.animation.drawExit

private const val Width = GlyphCharacter.DefaultWidth
private const val Height = GlyphCharacter.DefaultHeight

internal class Three(
    private val animatedPath: AnimatedPath,
    private val tube: Tube,
) : GlyphCharacter {
    companion object : GlyphCharacter.Companion {
        override val size = FloatSize(Width, Height)
    }

    override val companion = Companion

    private val topCurve = PathDefinition {
        moveTo(16.55f, 66.3f)
        cubicTo(16.55f, 36.8f, 40.35f, 13f, 69.85f, 13f)
        cubicTo(99.55f, 13f, 123.55f, 36.8f, 123.55f, 66.3f)
        cubicTo(123.55f, 96f, 99.55f, 120f, 69.85f, 120f)
    }
    private val middleRightCurve = PathDefinition {
        moveTo(70.05f, 94f)
        cubicTo(99.55f, 94f, 123.55f, 118f, 123.55f, 147f)
    }
    private val upperSquare = Square(96f, 161f)
    private val lowerSquare = Square(70f, 187f)
    private val bottomCurve = PathDefinition {
        moveTo(69.75f, 200f)
        cubicTo(40.35f, 200f, 16.45f, 176f, 16.45f, 147f)
    }
    private val dots = RectangularDotGroup(8.55f, 60.8f, 4, 4, 35f, 3f)

    override fun drawEnter(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        animatedPath.drawEnter(canvas, glyphProgress, colors[2], topCurve)
        tube.drawEnter(canvas, glyphProgress, colors[3], colors[1], path = middleRightCurve)
        upperSquare.drawEnter(canvas, glyphProgress, colors[2])
        lowerSquare.drawEnter(canvas, glyphProgress, colors[0])
        animatedPath.drawEnter(canvas, glyphProgress, colors[2], bottomCurve)
        dots.drawEnterLTR(canvas, glyphProgress, colors[0])
    }

    override fun drawExit(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        animatedPath.drawExit(canvas, glyphProgress, colors[2], topCurve)
        tube.drawExit(canvas, glyphProgress, colors[3], colors[1], path = middleRightCurve)
        upperSquare.drawExit(canvas, glyphProgress, colors[2])
        lowerSquare.drawExit(canvas, glyphProgress, colors[0])
        animatedPath.drawExit(canvas, glyphProgress, colors[2], bottomCurve)
        dots.drawExitLTR(canvas, glyphProgress, colors[0])
    }
}
