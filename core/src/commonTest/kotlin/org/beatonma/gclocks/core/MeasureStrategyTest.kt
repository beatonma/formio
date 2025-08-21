package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test

class MeasureStrategyTest {
    @Test
    fun testMeasureFit() {
        with(MeasureStrategy.Fit) {
            measureScale(
                NativeSize(100f, 50f),
                FloatSize(100f, 50f)
            ) shouldbe 1f

            measureScale(
                NativeSize(100f, 50f),
                FloatSize(200f, 50f)
            ) shouldbe 1f

            measureScale(
                NativeSize(100f, 50f),
                FloatSize(100f, 100f)
            ) shouldbe 1f

            measureScale(
                NativeSize(100f, 50f),
                FloatSize(200f, 100f)
            ) shouldbe 2f

            measureScale(
                NativeSize(100f, 50f),
                FloatSize(50f, 100f)
            ) shouldbe 0.5f

            measureScale(
                NativeSize(100f, 50f),
                FloatSize(200f, 0f)
            ) shouldbe 2f // Fallback to MeasureStrategy.FillWidth
        }
    }

    @Test
    fun testMeasureFill() {
        with(MeasureStrategy.FillWidth) {
            measureScale(
                NativeSize(100f, 50f),
                FloatSize(100f, 50f)
            ) shouldbe 1f

            measureScale(
                NativeSize(100f, 50f),
                FloatSize(200f, 50f)
            ) shouldbe 2f
        }
    }
}