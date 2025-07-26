package org.beatonma.gclocks.core.geometry


interface Rect<T: Number> {
    val left: T
    val top: T
    val right: T
    val bottom: T
}


interface MutableRect<T: Number>: Rect<T> {
    override var left: T
    override var top: T
    override var right: T
    override var bottom: T
}
