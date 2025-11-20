package org.beatonma.gclocks.form.characters.transitional

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.graphics.paths.Keyframe
import org.beatonma.gclocks.core.graphics.paths.KeyframeAnimation
import org.beatonma.gclocks.core.graphics.paths.KeyframeTransition
import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.form.characters.FormCharacter
import org.beatonma.gclocks.form.characters.canonical.Five
import org.beatonma.gclocks.form.characters.canonical.FiveExitHalfPill
import org.beatonma.gclocks.form.characters.canonical.ZeroWidth

private const val Width = ZeroWidth

internal val FiveZero = KeyframeAnimation(
    Five,
    FormCharacter::ease,
    listOf(
        KeyframeTransition(
            Keyframe(0f, Five {}),
            Keyframe(0.5f, FiveExitHalfPill {})
        ),
        KeyframeTransition(
            Keyframe(0.5f, FormCharacter.keyframe(Width) {
                val transformation: PathTransformation = {
                    rotateNoop(centerX, centerY)
                }

                path(FormCharacter.Orange, transformation) {
                    rect(left, thirdY, 80f, bottom)
                }

                path(FormCharacter.Yellow, transformation) {
                    // upper left
                    boundedArc(80f, twoThirdY, thirdY, startAngle = -Angle.Ninety)
                }
                path(FormCharacter.White, transformation) {
                    // lower right
                    boundedArc(80f, twoThirdY, thirdY, startAngle = Angle.TwoSeventy)
                }
            }),
            Keyframe(1f, FormCharacter.keyframe(Width) {
                val transformation: PathTransformation = {
                    rotate(Angle.FortyFive, centerX, centerY)
                }

                path(FormCharacter.Orange, transformation) {
                    rect(centerX, thirdY, centerX, bottom)
                }

                path(FormCharacter.Yellow, transformation) {
                    // upper left
                    boundedArc(centerX, centerY, radius, startAngle = Angle.Ninety)
                }
                path(FormCharacter.White, transformation) {
                    // lower right
                    boundedArc(centerX, centerY, radius, startAngle = Angle.TwoSeventy)
                }
            })
        )
    )
)
