package org.beatonma.gclocks.form.characters.transitional

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.graphics.paths.Keyframe
import org.beatonma.gclocks.core.graphics.paths.KeyframeAnimation
import org.beatonma.gclocks.core.graphics.paths.KeyframeTransition
import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.form.characters.FormCharacter
import org.beatonma.gclocks.form.characters.canonical.Three
import org.beatonma.gclocks.form.characters.canonical.ThreeExitHalfPill
import org.beatonma.gclocks.form.characters.canonical.ZeroWidth

private const val Width = ZeroWidth


internal val ThreeZero = KeyframeAnimation(
    Three,
    FormCharacter::ease,
    listOf(
        KeyframeTransition(
            Keyframe(0f, Three { translateNoop() }),
            Keyframe(0.5f, ThreeExitHalfPill { translate(16f, 0f) })
        ),
        KeyframeTransition(
            Keyframe(0.5f, FormCharacter.keyframe(Width) {
                val transformation: PathTransformation = {
                    rotateNoop(centerX, centerY)
                }

                path(FormCharacter.Orange, transformation) {
                    // bottom rectangle
                    rect(thirdX, twoThirdY, twoThirdX, bottom)
                }
                path(FormCharacter.Yellow, transformation) {
                    // middle rectangle
                    rect(thirdX, thirdY, twoThirdX, twoThirdY)
                }

                path(FormCharacter.Yellow, transformation) {
                    // upper left
                    boundedArc(twoThirdX, twoThirdY, thirdY, startAngle = -Angle.Ninety)
                }
                path(FormCharacter.White, transformation) {
                    // lower right
                    boundedArc(twoThirdX, twoThirdY, thirdY, startAngle = Angle.TwoSeventy)
                }
            }),
            Keyframe(1f, FormCharacter.keyframe(Width) {
                val transformation: PathTransformation = {
                    rotate(Angle.FortyFive, centerX, centerY)
                }

                path(FormCharacter.Orange, transformation) {
                    // bottom rectangle
                    rect(centerX, twoThirdY, centerX, bottom)
                }
                path(FormCharacter.Yellow, transformation) {
                    // middle rectangle
                    rect(centerX, thirdY, centerX, twoThirdY)
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
