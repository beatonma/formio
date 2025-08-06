package org.beatonma.gclocks.core.geometry

import kotlin.test.Test
import org.beatonma.gclocks.test.shouldbe

class AngleTest {
    @Test
    fun testAngle() {
        0f.positiveDegrees.asRadians shouldbe 0f
        0f.radians.asDegrees shouldbe 0f

        45f.positiveDegrees.asRadians shouldbe 0.78f
        1f.radians.asDegrees shouldbe 57.29f

        180f.positiveDegrees.asRadians shouldbe 3.14f
        270f.positiveDegrees.asRadians shouldbe 4.71f

        355f.positiveDegrees.asDegrees shouldbe 355f

        // Check values outside 0..360 degree are interpreted inside those limits.
        365f.positiveDegrees.asDegrees shouldbe 5f
        730f.positiveDegrees.asDegrees shouldbe 10f
        (-5f).positiveDegrees.asDegrees shouldbe 355f
        (-370f).positiveDegrees.asDegrees shouldbe 350f

        // rawDegrees should allow angles outside the normal limits
        365f.degrees.asDegrees shouldbe 365f
        730f.degrees.asDegrees shouldbe 730f
        (-5f).degrees.asDegrees shouldbe -5f
        (-370f).degrees.asDegrees shouldbe -370f


        // Conversion to radians and back to degrees should be lossless
        45f.positiveDegrees.asRadians.radians.asDegrees shouldbe 45f
        225f.positiveDegrees.asRadians.radians.asDegrees shouldbe 225f

        // Conversion to degrees and back to radians should be lossless
        1f.radians.asDegrees.positiveDegrees.asRadians shouldbe 1f
    }
}
