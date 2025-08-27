package org.beatonma.gclocks.io18.animation

import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.util.decelerate2
import kotlin.math.pow

internal sealed interface Io18Animation {
    fun easeIn(f: Float): Float = decelerate2(f)
    fun easeOut(f: Float): Float = f.pow(3)

    interface ClassDraw : Io18Animation {
        fun drawEnter(canvas: Canvas, progress: Float, color: Color)
        fun drawExit(canvas: Canvas, progress: Float, color: Color)
    }

    interface InlineDraw : Io18Animation {
        fun drawEnter(
            canvas: Canvas,
            progress: Float,
            color: Color,
            block: Canvas.() -> Unit,
        )

        fun drawExit(
            canvas: Canvas,
            progress: Float,
            color: Color,
            block: Canvas.() -> Unit,
        )
    }
}

internal interface Io18GridAnimation : Io18Animation.ClassDraw {
    val left: Float
    val top: Float

    val rows: Int
    val columns: Int
    val spaceBetween: Float
}
