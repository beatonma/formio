package org.beatonma.gclocks.io18.characters

import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.core.graphics.paths.PathDefinition
import org.beatonma.gclocks.core.util.progress
import org.beatonma.gclocks.io18.animation.AnimatedPath
import org.beatonma.gclocks.io18.animation.TriangleDotGroup
import org.beatonma.gclocks.io18.animation.Tube
import org.beatonma.gclocks.io18.animation.drawEnter
import org.beatonma.gclocks.io18.animation.drawExit

private const val Width = GlyphCharacter.DefaultWidth
private const val Height = GlyphCharacter.DefaultHeight


internal class Four(
    private val animatedPath: AnimatedPath,
    private val tube: Tube,
) : GlyphCharacter {
    companion object : GlyphCharacter.Companion {
        override val size = FloatSize(Width, Height)
    }

    override val companion = Companion

    private val diagonal = PathDefinition {
        moveTo(15.21f, 122f)
        lineTo(121.97f, 14.9f)
    }
    private val upperVertical = PathDefinition {
        moveTo(123.55f, 68f)
        lineTo(123.55f, 148f)
    }
    private val lowerVertical = PathDefinition {
        moveTo(123.55f, 213f)
        lineTo(123.55f, 148f)
    }
    private val dots = TriangleDotGroup(43f, 94f, 5, 6.5f, 3f)

    override fun drawEnter(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        tube.drawEnter(canvas, glyphProgress, colors[3], colors[2], StrokeCap.Round, diagonal)
        animatedPath.drawEnter(canvas, glyphProgress, colors[1], upperVertical)
        animatedPath.drawEnter(canvas, glyphProgress, colors[0], lowerVertical)
        dots.drawEnter(canvas, progress(glyphProgress, 0f, 0.4f), colors[2])
    }

    override fun drawExit(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        tube.drawExit(canvas, glyphProgress, colors[3], colors[2], StrokeCap.Round, diagonal)
        animatedPath.drawExit(canvas, glyphProgress, colors[1], upperVertical)
        animatedPath.drawExit(canvas, glyphProgress, colors[0], lowerVertical)
        dots.drawExit(canvas, progress(glyphProgress, 0f, 0.4f), colors[2])
    }
}
