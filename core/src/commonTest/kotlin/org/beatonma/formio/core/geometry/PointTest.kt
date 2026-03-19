package org.beatonma.formio.core.geometry

import org.beatonma.formio.test.shouldbe
import kotlin.test.Test

class PointTest {
    @Test
    fun `FloatPoint value-packing is correct`() {
        with(FloatPoint(42f, 137f)) {
            x shouldbe 42f
            y shouldbe 137f
        }

        with(FloatPoint(-41f, -1037f)) {
            x shouldbe -41f
            y shouldbe -1037f
        }

        with(FloatPoint(41f, -1037f)) {
            x shouldbe 41f
            y shouldbe -1037f
        }

        with(FloatPoint(-41f, 1037f)) {
            x shouldbe -41f
            y shouldbe 1037f
        }
    }
}