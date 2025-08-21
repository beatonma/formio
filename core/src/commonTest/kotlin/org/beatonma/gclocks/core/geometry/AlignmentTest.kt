package org.beatonma.gclocks.core.geometry

import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test

class HorizontalAlignmentTest {
    @Test
    fun `Start is correct`() {
        HorizontalAlignment.Start.apply(0f, 100f) shouldbe 0f
        HorizontalAlignment.Start.apply(50f, 100f) shouldbe 0f
        HorizontalAlignment.Start.apply(100f, 100f) shouldbe 0f
        HorizontalAlignment.Start.apply(200f, 100f) shouldbe 0f
        HorizontalAlignment.Start.apply(200f, Float.POSITIVE_INFINITY) shouldbe 0f
    }

    @Test
    fun `Center is correct`() {
        HorizontalAlignment.Center.apply(0f, 100f) shouldbe 50f
        HorizontalAlignment.Center.apply(50f, 100f) shouldbe 25f
        HorizontalAlignment.Center.apply(100f, 100f) shouldbe 0f
        HorizontalAlignment.Center.apply(200f, 100f) shouldbe -50f
        HorizontalAlignment.Center.apply(200f, Float.POSITIVE_INFINITY) shouldbe 0f
    }

    @Test
    fun `End is correct`() {
        HorizontalAlignment.End.apply(0f, 100f) shouldbe 100f
        HorizontalAlignment.End.apply(50f, 100f) shouldbe 50f
        HorizontalAlignment.End.apply(100f, 100f) shouldbe 0f
        HorizontalAlignment.End.apply(200f, 100f) shouldbe -100f
        HorizontalAlignment.End.apply(200f, Float.POSITIVE_INFINITY) shouldbe 0f
    }
}


class VerticalAlignmentTest {
    @Test
    fun `Top is correct`() {
        VerticalAlignment.Top.apply(0f, 100f) shouldbe 0f
        VerticalAlignment.Top.apply(50f, 100f) shouldbe 0f
        VerticalAlignment.Top.apply(100f, 100f) shouldbe 0f
        VerticalAlignment.Top.apply(200f, 100f) shouldbe 0f
        VerticalAlignment.Top.apply(200f, Float.POSITIVE_INFINITY) shouldbe 0f
    }

    @Test
    fun `Center is correct`() {
        VerticalAlignment.Center.apply(0f, 100f) shouldbe 50f
        VerticalAlignment.Center.apply(50f, 100f) shouldbe 25f
        VerticalAlignment.Center.apply(100f, 100f) shouldbe 0f
        VerticalAlignment.Center.apply(200f, 100f) shouldbe -50f
        VerticalAlignment.Center.apply(200f, Float.POSITIVE_INFINITY) shouldbe 0f
    }

    @Test
    fun `Bottom is correct`() {
        VerticalAlignment.Bottom.apply(0f, 100f) shouldbe 100f
        VerticalAlignment.Bottom.apply(50f, 100f) shouldbe 50f
        VerticalAlignment.Bottom.apply(100f, 100f) shouldbe 0f
        VerticalAlignment.Bottom.apply(200f, 100f) shouldbe -100f
        VerticalAlignment.Bottom.apply(200f, Float.POSITIVE_INFINITY) shouldbe 0f
    }
}