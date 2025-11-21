package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.util.getCurrentTimeMillis


/**
 * Indeterminate loading animation that renders within a square of [size].
 */
interface LoadingSpinner {
    val paints: Paints
    val size: Float

    fun draw(canvas: Canvas, currentTimeMillis: Long = getCurrentTimeMillis())
}
