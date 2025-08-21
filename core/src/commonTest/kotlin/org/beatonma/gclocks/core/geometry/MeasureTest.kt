package org.beatonma.gclocks.core.geometry

import org.beatonma.gclocks.test.assertThrows
import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test

class MeasureTest {
    @Test
    fun testMeasure() {
        MeasureConstraints(
            maxWidth = 10f,
            maxHeight = 10f,
        ).measureScale(
            NativeSize(10f, 10f)
        ) shouldbe 1f

        MeasureConstraints(
            maxWidth = 10f,
            maxHeight = 10f,
        ).measureScale(
            NativeSize(20f, 20f)
        ) shouldbe 0.5f

        MeasureConstraints(
            maxWidth = 10f,
            maxHeight = 10f,
        ).measureScale(
            NativeSize(5f, 5f)
        ) shouldbe 2f

        MeasureConstraints(
            maxWidth = 10f,
            maxHeight = MeasureConstraints.Infinity,
        ).measureScale(
            NativeSize(5f, 5f)
        ) shouldbe 2f

        MeasureConstraints(
            maxWidth = MeasureConstraints.Infinity,
            maxHeight = 10f,
        ).measureScale(
            NativeSize(20f, 20f)
        ) shouldbe 0.5f

        assertThrows<IllegalStateException> {
            MeasureConstraints(
                maxWidth = MeasureConstraints.Infinity,
                maxHeight = MeasureConstraints.Infinity,
            ).measureScale(
                NativeSize(5f, 5f)
            )
        }
    }
}