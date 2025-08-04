package org.beatonma.gclocks.core.options

interface Alignment {
    fun apply(objectSize: Int, availableSize: Int, default: Int = 0): Int
    fun apply(objectSize: Float, availableSize: Float, default: Float = 0f): Float
}

enum class HorizontalAlignment : Alignment {
    Default,
    Start,
    Center,
    End,
    ;

    override fun apply(objectSize: Int, availableSize: Int, default: Int): Int = when (this) {
        Start -> 0
        End -> availableSize - objectSize
        Center -> (availableSize - objectSize) / 2
        Default -> default
    }

    override fun apply(objectSize: Float, availableSize: Float, default: Float): Float =
        when (this) {
            Start -> 0f
            End -> availableSize - objectSize
            Center -> (availableSize - objectSize) / 2f
            Default -> default
        }
}

enum class VerticalAlignment : Alignment {
    Default,
    Top,
    Center,
    Bottom,
    ;

    override fun apply(objectSize: Int, availableSize: Int, default: Int): Int = when (this) {
        Top -> 0
        Bottom -> availableSize - objectSize
        Center -> (availableSize - objectSize) / 2
        Default -> default
    }

    override fun apply(objectSize: Float, availableSize: Float, default: Float): Float =
        when (this) {
            Top -> 0f
            Bottom -> availableSize - objectSize
            Center -> (availableSize - objectSize) / 2f
            Default -> default
        }
}