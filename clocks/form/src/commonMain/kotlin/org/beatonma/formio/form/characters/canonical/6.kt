package org.beatonma.formio.form.characters.canonical

import org.beatonma.formio.core.geometry.Angle
import org.beatonma.formio.core.graphics.paths.PathTransformation
import org.beatonma.formio.form.characters.FormCharacter

internal const val SixWidth = 144f

internal val Six = Six()
internal inline fun Six(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(SixWidth) {
        path(FormCharacter.White, transformation) {
            // lower
            sector(centerX, centerY, radius, Angle.Zero, Angle.OneEighty)
        }

        path(FormCharacter.Yellow, transformation) {
            // upper
            tetragon(
                36f, top,
                threeQuarterX, top,
                centerX, centerY,
                left, centerY,
            )
        }
    }

internal inline fun SixEnter(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(SixWidth) {
        path(FormCharacter.White, transformation) {
            // lower
            sector(centerX, centerY, radius, Angle.OneEighty, Angle.Zero)
        }

        path(FormCharacter.Yellow, transformation) {
            // upper
            tetragon(
                left, top,
                centerX, top,
                centerX, centerY,
                left, centerY,
            )
        }
    }
