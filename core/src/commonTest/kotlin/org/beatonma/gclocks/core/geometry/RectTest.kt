package org.beatonma.gclocks.core.geometry

import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test

class MutableRectTest {
    @Test
    fun `Boundaries are sorted with left lte right, top lte bottom`() {
        with(MutableRectF(1f, 2f, 3f, 4f)) {
            left shouldbe 1f
            top shouldbe 2f
            right shouldbe 3f
            bottom shouldbe 4f
            area shouldbe 4f
        }

        with(MutableRectF(3f, 4f, 1f, 2f)) {
            left shouldbe 1f
            top shouldbe 2f
            right shouldbe 3f
            bottom shouldbe 4f
            area shouldbe 4f
        }

        with(MutableRectF().apply {
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
    fun `include is correct`() {
        with(MutableRectF(0f, 0f, 0f, 0f)) {
            include(MutableRectF(1f, 2f, 3f, 4f)) shouldbe true
            left shouldbe 0f
            top shouldbe 0f
            right shouldbe 3f
            bottom shouldbe 4f
            area shouldbe 12f

            include(MutableRectF(-1f, -2f, -3f, -4f)) shouldbe true
            left shouldbe -3f
            top shouldbe -4f
            right shouldbe 3f
            bottom shouldbe 4f
            area shouldbe 48f

            include(MutableRectF(1f, 2f, 3f, 4f)) shouldbe false // Already contained
        }

        with(MutableRectF()) {
            isValid shouldbe false
            include(MutableRectF(1f, 2f, 3f, 4f)) shouldbe true
            isValid shouldbe true
            left shouldbe 1f
            top shouldbe 2f
            right shouldbe 3f
            bottom shouldbe 4f
            include(MutableRectF()) shouldbe false
        }
    }

    @Test
    fun `inset is correct`() {
        with(MutableRectF(-100f, -100f, 100f, 100f)) {
            inset(10f)
            left shouldbe -90f
            top shouldbe -90f
            right shouldbe 90f
            bottom shouldbe 90f

            inset(1f, 2f, 3f, 4f)
            left shouldbe -89f
            top shouldbe -88f
            right shouldbe 87f
            bottom shouldbe 86f
        }
    }

    @Test
    fun `validation is correct`() {
        with(MutableRectF()) {
            isValid shouldbe false
            isEmpty shouldbe true
        }

        with(MutableRectF(1f, 2f, 3f, 4f)) {
            isValid shouldbe true
            isEmpty shouldbe false
            clear()
            isValid shouldbe false
            isEmpty shouldbe true
        }
    }
}