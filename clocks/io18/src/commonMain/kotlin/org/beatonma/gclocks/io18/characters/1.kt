package org.beatonma.gclocks.io18.characters

import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.paths.PathDefinition
import org.beatonma.gclocks.core.util.progress
import org.beatonma.gclocks.io18.animation.AnimatedPath
import org.beatonma.gclocks.io18.animation.drawEnter
import org.beatonma.gclocks.io18.animation.drawExit

private const val Width = 80f
private const val Height = GlyphCharacter.DefaultHeight

internal class One(
    private val animatedPath: AnimatedPath,
) : GlyphCharacter {
    companion object : GlyphCharacter.Companion {
        override val size = FloatSize(Width, Height)
    }

    override val companion = Companion

    private val topSerif = PathDefinition {
        moveTo(41.4f, 3f)
        cubicTo(41.4f, 25.3f, 23.3f, 43.4f, 1f, 43.4f)
    }
    private val vertical1 = PathDefinition {
        moveTo(40f, 55f)
        lineTo(40f, 121f)
    }
    private val vertical2 = PathDefinition {
        moveTo(40f, 136f)
        lineTo(40f, 149f)
    }
    private val vertical3 = PathDefinition {
        moveTo(40f, 162f)
        lineTo(40f, 175f)
    }
    private val bottomSerif = PathDefinition {
        moveTo(80f, 200f)
        lineTo(0f, 200f)
    }

    override fun drawEnter(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        animatedPath.drawEnter(canvas, glyphProgress, colors[1], topSerif)
        animatedPath.drawEnter(canvas, glyphProgress, colors[2], vertical1)
        animatedPath.drawEnter(canvas, progress(glyphProgress, 0f, 0.5f), colors[0], vertical2)
        animatedPath.drawEnter(canvas, progress(glyphProgress, 0.5f, 1f), colors[0], vertical3)
        animatedPath.drawEnter(canvas, glyphProgress, colors[3], bottomSerif)
    }

    override fun drawExit(
        canvas: Canvas,
        glyphProgress: Float,
        colors: Array<Color>,
    ) {
        animatedPath.drawExit(canvas, glyphProgress, colors[1], topSerif)
        animatedPath.drawExit(canvas, glyphProgress, colors[2], vertical1)
        animatedPath.drawExit(canvas, progress(glyphProgress, 0f, 0.5f), colors[0], vertical2)
        animatedPath.drawExit(canvas, progress(glyphProgress, 0.5f, 1f), colors[0], vertical3)
        animatedPath.drawExit(canvas, glyphProgress, colors[3], bottomSerif)
    }
}
