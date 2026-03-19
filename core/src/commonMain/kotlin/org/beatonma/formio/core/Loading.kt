package org.beatonma.formio.core

import org.beatonma.formio.core.graphics.Canvas
import org.beatonma.formio.core.graphics.Paints
import org.beatonma.formio.core.util.getCurrentTimeMillis


/**
 * Indeterminate loading animation that renders within a square of [size].
 */
interface LoadingSpinner {
    val paints: Paints
    val size: Float

    fun draw(canvas: Canvas, currentTimeMillis: Long = getCurrentTimeMillis())
}
