package org.beatonma.gclocks.io18.characters

import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color

/**
 * Unlike other glyph styles, Io18 animations comprise one character animating
 * in while another animates out, with no interaction between them. We can
 * therefore have simple individual definitions for each character.
 */
interface GlyphCharacter {
    companion object Default {
        const val DefaultHeight = 213f
        const val DefaultWidth = 140f
    }

    interface Companion {
        val size: Size<Float>
    }

    val companion: Companion

    fun drawEnter(canvas: Canvas, glyphProgress: Float, colors: Array<Color>)
    fun drawExit(canvas: Canvas, glyphProgress: Float, colors: Array<Color>)
}
