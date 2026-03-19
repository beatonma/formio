package org.beatonma.formio.form.characters.transitional

import org.beatonma.formio.core.geometry.Angle
import org.beatonma.formio.core.graphics.paths.Keyframe
import org.beatonma.formio.core.graphics.paths.KeyframeAnimation
import org.beatonma.formio.core.graphics.paths.KeyframeTransition
import org.beatonma.formio.core.graphics.paths.PathTransformation
import org.beatonma.formio.form.characters.FormCharacter
import org.beatonma.formio.form.characters.canonical.Nine
import org.beatonma.formio.form.characters.canonical.NineWidth
import org.beatonma.formio.form.characters.canonical.ZeroWidth

internal val NineZero = KeyframeAnimation(
    Nine,
    FormCharacter::ease,
    transitions = listOf(
        KeyframeTransition(
            Keyframe(0f, FormCharacter.keyframe(NineWidth) {
                val transformation: PathTransformation = {
                    rotateNoop(centerX, centerY)
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
                path(FormCharacter.White, transformation) {
                    // top
                    boundedArc(centerX, centerY, radius, startAngle = Angle.OneEighty, close = true)
                }
                path(FormCharacter.Yellow, transformation) {
                    // top
                    boundedArc(centerX, centerY, radius, startAngle = Angle.OneEighty, close = true)
                }
            }),
            Keyframe(1f, FormCharacter.keyframe(ZeroWidth) {
                val transformation: PathTransformation = {
                    rotate(-Angle.FortyFive, centerX, centerY)
                }

                path(FormCharacter.White, transformation) {
                    // bottom parallelogram
                    tetragon(
                        centerX, centerY,
                        right, centerY,
                        right, centerY,
                        centerX, centerY,
                    )
                }
                path(FormCharacter.White, transformation) {
                    // top
                    boundedArc(
                        centerX,
                        centerY,
                        radius,
                        startAngle = Angle.Zero,
                        close = true
                    )
                }
                path(FormCharacter.Yellow, transformation) {
                    // top
                    boundedArc(centerX, centerY, radius, startAngle = Angle.OneEighty, close = true)
                }
            }),
        )
    )
)
