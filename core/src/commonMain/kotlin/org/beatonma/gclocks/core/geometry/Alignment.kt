package org.beatonma.gclocks.core.geometry


interface Alignment {
    fun apply(objectSize: Float, availableSize: Float): Float
    fun apply(objectSize: Int, availableSize: Int): Int
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

    override fun apply(objectSize: Int, availableSize: Int): Int {
        if (availableSize == Int.MAX_VALUE) return 0
        return when (this) {
            Start -> 0
            End -> availableSize - objectSize
            Center -> (availableSize - objectSize) / 2
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

    override fun apply(objectSize: Int, availableSize: Int): Int {
        if (availableSize == Int.MAX_VALUE) return 0
        return when (this) {
            Top -> 0
            Bottom -> availableSize - objectSize
            Center -> (availableSize - objectSize) / 2
        }
    }
}
