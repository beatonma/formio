package org.beatonma.gclocks.core.geometry

import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test

class RectTest {
    @Test
    fun `MutableRect boundaries are sorted with left lte right, top lte bottom`() {
        with(MutableFloatRect(1f, 2f, 3f, 4f)) {
            left shouldbe 1f
            top shouldbe 2f
            right shouldbe 3f
            bottom shouldbe 4f
            area shouldbe 4f
        }

        with(MutableFloatRect(3f, 4f, 1f, 2f)) {
            left shouldbe 1f
            top shouldbe 2f
            right shouldbe 3f
            bottom shouldbe 4f
            area shouldbe 4f
        }

        with(MutableFloatRect().apply {
            isEmpty shouldbe true
            set(3f, 4f, 1f, 2f)
            isEmpty shouldbe false
        }) {
            left shouldbe 1f
            top shouldbe 2f
            right shouldbe 3f
            bottom shouldbe 4f
            area shouldbe 4f
        }
    }

    @Test
    fun `MutableRect include is correct`() {
        with(MutableFloatRect(0f, 0f, 0f, 0f)) {
            include(MutableFloatRect(1f, 2f, 3f, 4f))
            left shouldbe 0f
            top shouldbe 0f
            right shouldbe 3f
            bottom shouldbe 4f
            area shouldbe 12f

            include(MutableFloatRect(-1f, -2f, -3f, -4f))
            left shouldbe -3f
            top shouldbe -4f
            right shouldbe 3f
            bottom shouldbe 4f
            area shouldbe 48f
        }
    }
}