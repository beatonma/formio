package org.beatonma.gclocks.core.geometry


interface Alignment {
    fun apply(objectSize: Float, availableSize: Float): Float
}

enum class HorizontalAlignment : Alignment {
    Start,
    Center,
    End,
    ;

    override fun apply(objectSize: Float, availableSize: Float): Float {
        if (availableSize.isInfinite()) return 0f
        return when (this) {
            Start -> 0f
            End -> availableSize - objectSize
            Center -> (availableSize - objectSize) / 2f
        }
    }
}

enum class VerticalAlignment : Alignment {
    Top,
    Center,
    Bottom,
    ;


    override fun apply(objectSize: Float, availableSize: Float): Float {
        if (availableSize.isInfinite()) return 0f
        return when (this) {
            Top -> 0f
            Bottom -> availableSize - objectSize
            Center -> (availableSize - objectSize) / 2f
        }
    }
}