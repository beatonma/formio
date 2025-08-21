package org.beatonma.gclocks.core.geometry


interface Alignment {
    fun apply(objectSize: Int, availableSize: Int): Int
    fun apply(objectSize: Float, availableSize: Float): Float
}

enum class HorizontalAlignment : Alignment {
    Start,
    Center,
    End,
    ;

    override fun apply(objectSize: Int, availableSize: Int): Int = when (this) {
        Start -> 0
        End -> availableSize - objectSize
        Center -> (availableSize - objectSize) / 2
    }

    override fun apply(objectSize: Float, availableSize: Float): Float =
        when (this) {
            Start -> 0f
            End -> availableSize - objectSize
            Center -> (availableSize - objectSize) / 2f
        }
}

enum class VerticalAlignment : Alignment {
    Top,
    Center,
    Bottom,
    ;

    override fun apply(objectSize: Int, availableSize: Int): Int = when (this) {
        Top -> 0
        Bottom -> availableSize - objectSize
        Center -> (availableSize - objectSize) / 2
    }

    override fun apply(objectSize: Float, availableSize: Float): Float =
        when (this) {
            Top -> 0f
            Bottom -> availableSize - objectSize
            Center -> (availableSize - objectSize) / 2f
        }
}