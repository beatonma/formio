package org.beatonma.gclocks.core.geometry

import kotlin.math.cos
import kotlin.math.sin

fun getPointOnCircle(centerX: Float, centerY: Float, radius: Float, angle: Angle): FloatPoint {
    val x = centerX + radius * cos(angle.asRadians)
    val y = centerY + radius * sin(angle.asRadians)

    return FloatPoint(x, y)
}

fun getPointOnEllipse(left: Float, top: Float, right: Float, bottom: Float, angle: Angle): FloatPoint {
    val centerX = left + (right - left) / 2f
    val centerY = top + (bottom - top) / 2f

    val radiusX = (right - left) / 2f
    val radiusY = (bottom - top) / 2f

    val x = centerX + radiusX * cos(angle.asRadians)
    val y = centerY + radiusY * sin(angle.asRadians)

    return FloatPoint(x, y)
}
