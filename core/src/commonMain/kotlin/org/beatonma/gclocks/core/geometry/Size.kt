package org.beatonma.gclocks.core.geometry

interface Size<T: Number> {
    val width: T
    val height: T
}

interface MutableSize<T: Number>: Size<T> {
    override var width: T
    override var height: T
}
