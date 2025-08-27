package org.beatonma.gclocks.io18.characters

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.PathDefinition
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.io18.animation.Tube
import org.beatonma.gclocks.io18.animation.drawEnter
import org.beatonma.gclocks.io18.animation.drawExit


private const val Width = GlyphCharacter.DefaultWidth
private const val Height = GlyphCharacter.DefaultHeight

internal class Nine(
    private val tube: Tube,
) : GlyphCharacter {
    companion object : GlyphCharacter.Companion {
        override val size = FloatSize(Width, Height)
    }

    override val companion = Companion


    val leftLoop = PathDefinition {
        boundedArc(14.7f, 12.7f, 120f, 120f, Angle.Ninety, Angle.OneEighty)
    }
    val rightLoop = PathDefinition {
        boundedArc(14.7f, 12.7f, 120f, 120f, Angle.TwoSeventy, Angle.OneEighty)
    }
    val diagonal = PathDefinition {
        moveTo(126f, 93f)
        lineTo(71.2f, 200f)
    }


    override fun drawEnter(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        tube.drawEnter(canvas, glyphProgress, colors[3], colors[3], StrokeCap.Round, leftLoop)
        tube.drawEnter(canvas, glyphProgress, colors[0], colors[2], StrokeCap.Round, rightLoop)
        tube.drawEnter(canvas, glyphProgress, colors[1], colors[3], StrokeCap.Round, diagonal)
    }

    override fun drawExit(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        tube.drawExit(canvas, glyphProgress, colors[3], colors[3], StrokeCap.Round, leftLoop)
        tube.drawExit(canvas, glyphProgress, colors[0], colors[2], StrokeCap.Round, rightLoop)
        tube.drawExit(canvas, glyphProgress, colors[1], colors[3], StrokeCap.Round, diagonal)
    }
}