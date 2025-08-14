package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.Size
import kotlin.math.min

enum class MeasureStrategy {
    Fit, // Respect the existing boundaries of the container
    FillWidth, // Use existing value for width to determine the height
    ;

    fun measureScale(
        nativeSize: Size<Float>,
        availableSize: Size<Float>,
    ): Float = measureScale(
        nativeSize.x, nativeSize.y,
        availableSize.x, availableSize.y
    )

    fun measureScale(
        nativeWidth: Float, nativeHeight: Float,
        availableWidth: Float, availableHeight: Float,
    ): Float {
        val strategy = when (availableHeight) {
            0f -> FillWidth
            else -> this
        }

        return when (strategy) {
            Fit -> {
                val widthRatio = availableWidth / nativeWidth
                val heightRatio = availableHeight / nativeHeight

                min(widthRatio, heightRatio)
            }

            FillWidth -> {
                if (availableWidth > 0f) {
                    availableWidth / nativeWidth
                } else {
                    availableHeight / nativeHeight
                }
            }
        }
    }
}