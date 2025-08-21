package org.beatonma.gclocks.core.geometry

import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test

class SizeTest {
    @Test
    fun `FloatSize value-packing is correct`() {
        with(FloatSize(0f, 0f)) {
            x shouldbe 0f
            y shouldbe 0f
        }

        with(FloatSize(16f, 132f)) {
            x shouldbe 16f
            y shouldbe 132f
        }

        with(FloatSize(16f, 132f)) {
            x shouldbe 16f
            y shouldbe 132f
        }

        with(FloatSize(1024f, 2048f)) {
            x shouldbe 1024f
            y shouldbe 2048f
        }
    }

    fun `FloatSize scaledBy is correct`() {
        with(FloatSize(1f, 2f) * 2f) {
            x shouldbe 2f
            y shouldbe 4f
        }
    }
}