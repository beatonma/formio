package org.beatonma.gclocks.form.characters.canonical

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.form.characters.FormCharacter

internal const val NineWidth = 144f

internal val Nine = Nine()
internal inline fun Nine(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(NineWidth) {
        path(FormCharacter.Yellow, transformation) {
            // top
            sector(centerX, centerY, radius, startAngle = Angle.Zero, sweepAngle = -Angle.OneEighty)
        }

        path(FormCharacter.White, transformation) {
            // bottom parallelogram
            tetragon(
                centerX, centerY,
                right, centerY,
                threeQuarterX, bottom,
                36f, bottom,
            )
        }
    }


internal inline fun NineEnter(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(NineWidth) {
        path(FormCharacter.Yellow, transformation) {
            // top
            sector(centerX, centerY, radius, startAngle = Angle.Zero, sweepAngle = Angle.Zero)
        }

        path(FormCharacter.White, transformation) {
            // bottom parallelogram
            tetragon(
                centerX, centerY,
                right, centerY,
                right, bottom,
                centerX, bottom,
            )
        }
    }
