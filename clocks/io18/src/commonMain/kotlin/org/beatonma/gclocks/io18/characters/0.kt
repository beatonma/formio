package org.beatonma.gclocks.io18.characters

import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.paths.PathDefinition
import org.beatonma.gclocks.io18.animation.AnimatedPath
import org.beatonma.gclocks.io18.animation.SquareGrid
import org.beatonma.gclocks.io18.animation.drawEnter
import org.beatonma.gclocks.io18.animation.drawExit

private const val Width = GlyphCharacter.DefaultWidth
private const val Height = GlyphCharacter.DefaultHeight

internal class Zero(
    private val animatedPath: AnimatedPath,
) : GlyphCharacter {
    companion object : GlyphCharacter.Companion {
        override val size = FloatSize(Width, Height)
    }

    override val companion = Companion

    private val grid = SquareGrid(4.8f, 70f, 6, 10)
    private val path = PathDefinition(Width, Height) {
        moveTo(123.7f, 147f)
        cubicTo(123.7f, 176f, 99.7f, 200f, 70f, 200f)
        cubicTo(40.5f, 200f, 16.3f, 176f, 16.3f, 147f)
        lineTo(16.3f, 68f)
        cubicTo(16.3f, 39f, 40.5f, 15f, 70f, 15f)
        cubicTo(99.7f, 15f, 123.7f, 39f, 123.7f, 68f)
        closePath()
    }

    override fun drawEnter(canvas: Canvas, glyphProgress: Float, colors: Array<Color>) {
        animatedPath.drawEnter(canvas, glyphProgress, colors[2], path)
        grid.drawEnter(canvas, glyphProgress, colors[3])
    }

    override fun drawExit(canvas: Canvas, glyphProgress: Float, colors: Array<Color>) {
        animatedPath.drawExit(canvas, glyphProgress, colors[2], path)
        grid.drawExit(canvas, glyphProgress, colors[3])
    }
}
