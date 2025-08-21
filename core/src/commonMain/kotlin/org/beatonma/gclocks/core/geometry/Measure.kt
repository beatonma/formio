package org.beatonma.gclocks.core.geometry

import kotlin.math.min


interface ConstrainedLayout {
    fun setConstraints(constraints: MeasureConstraints): ScaledSize
}

data class MeasureConstraints(
    val maxWidth: Float,
    val maxHeight: Float,
) {
    private enum class ConstrainedDimension {
        Width,
        Height,
        Both,
        ;
    }

    private val constrainedDimension: ConstrainedDimension = when {
        maxWidth < Infinity && maxHeight < Infinity -> ConstrainedDimension.Both
        maxWidth < Infinity -> ConstrainedDimension.Width
        maxHeight < Infinity -> ConstrainedDimension.Height
        else -> throw IllegalStateException("MeasureConstraints: at least one dimension must be constrained.")
    }

    fun measureScale(native: NativeSize): Float {
        if (native.width == 0f || native.height == 0f) {
            return 0f
        }

        return when (constrainedDimension) {
            ConstrainedDimension.Width -> maxWidth / native.width
            ConstrainedDimension.Height -> maxHeight / native.height
            ConstrainedDimension.Both -> {
                val widthRatio = maxWidth / native.width
                val heightRatio = maxHeight / native.height

                min(widthRatio, heightRatio)
            }
        }
    }

    companion object {
        const val Infinity = Float.POSITIVE_INFINITY
    }
}


