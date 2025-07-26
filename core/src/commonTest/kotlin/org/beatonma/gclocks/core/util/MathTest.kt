package org.beatonma.gclocks.core.util

import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class MathTest {
    @Test
    fun constrain() {
        assertEquals(0f, constrain(0f, 0f, 1f))
        assertEquals(0.5f, constrain(0.5f, 0f, 1f))
        assertEquals(1f, constrain(1f, 0f, 1f))

        assertEquals(0f, constrain(-5f, 0f, 1f))
        assertEquals(1f, constrain(5f, 0f, 1f))
    }

    @Test
    fun distance() {
        assertEquals(1f, distance(0f, 0f, 0f, 1f))
        assertEquals(1f, distance(0f, 0f, 1f, 0f))
        assertEquals(sqrt(2f), distance(0f, 0f, 1f, 1f))
    }

    @Test
    fun normalize() {
        assertEquals(0f, normalize(0f, 0f, 100f))
        assertEquals(0.5f, normalize(50f, 0f, 100f))
        assertEquals(1f, normalize(100f, 0f, 100f))
    }

    @Test
    fun interpolate() {
        assertEquals(0f, interpolate(0f, 0f, 1f))
        assertEquals(1f, interpolate(0f, 1f, 2f))
        assertEquals(50f, interpolate(0.5f, 0f, 100f))
    }
}