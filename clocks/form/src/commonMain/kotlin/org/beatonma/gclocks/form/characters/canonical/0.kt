package org.beatonma.gclocks.form.characters.canonical

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.form.characters.FormCharacter
import org.beatonma.gclocks.form.characters.FormCharacter.pill

internal const val ZeroWidth = 144f

internal val Zero = Zero()
internal inline fun Zero(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(ZeroWidth) {
        path(FormCharacter.Yellow, transformation) {
            // upper left
            boundedArc(centerX, centerY, radius, startAngle = Angle.OneThreeFive)
        }

        path(FormCharacter.White, transformation) {
            // lower right
            boundedArc(centerX, centerY, radius, startAngle = Angle.ThreeFifteen)
        }
    }

/* Yellow half is rotated beneath the white half */
internal inline fun ZeroEnter(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(ZeroWidth) {
        path(FormCharacter.Yellow, transformation) {
            // upper left
            boundedArc(centerX, centerY, radius, startAngle = -Angle.Ninety)
        }

        path(FormCharacter.White, transformation) {
            // lower right
            boundedArc(centerX, centerY, radius, startAngle = Angle.TwoSeventy)
        }
    }

internal inline fun ZeroPill(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(ZeroWidth) {
        pill(
            left,
            top,
            right,
            bottom,
            FormCharacter.Yellow,
            FormCharacter.White,
            0f,
            {
                transformation()
                rotate(Angle.FortyFive, centerX, centerY)
            }
        )
    }

internal inline fun ZeroEnterSmallPill(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(ZeroWidth) {
        pill(
            quarterX,
            twoThirdY,
            threeQuarterX,
            bottom,
            FormCharacter.Yellow,
            FormCharacter.White,
            1f,
            {
                transformation()
                rotateNoop(centerX, centerY)
            }
        )
    }

internal inline fun ZeroExitSmallPill(transformation: PathTransformation = {}) =
    ZeroEnterSmallPill(transformation)
