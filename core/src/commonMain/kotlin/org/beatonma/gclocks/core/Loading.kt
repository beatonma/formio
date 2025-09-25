package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.util.getCurrentTimeMillis
import org.beatonma.gclocks.core.util.progress


/**
 * Indeterminate loading animation that renders within a square of [size].
 */
interface LoadingSpinner<P : Paints> {
    val paints: P
    val size: Float

    fun draw(canvas: Canvas)
}

inline fun LoadingSpinner<*>.getProgressMillis(durationMillis: Long): Long {
    return getCurrentTimeMillis() % durationMillis
}

inline fun LoadingSpinner<*>.getProgress(durationMillis: Long): Float {
    return progress(getProgressMillis(durationMillis).toFloat(), 0f, durationMillis.toFloat())
}
