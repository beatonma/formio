package org.beatonma.gclocks.form.characters.transitional

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.degrees
import org.beatonma.gclocks.core.graphics.paths.Keyframe
import org.beatonma.gclocks.core.graphics.paths.KeyframeAnimation
import org.beatonma.gclocks.core.graphics.paths.KeyframeTransition
import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.form.characters.FormCharacter
import org.beatonma.gclocks.form.characters.canonical.Two
import org.beatonma.gclocks.form.characters.canonical.TwoExit
import org.beatonma.gclocks.form.characters.canonical.ZeroWidth

private const val Width = ZeroWidth

internal val TwoZero = KeyframeAnimation(
    Two,
    FormCharacter::ease,
    listOf(
        KeyframeTransition(
            Keyframe(0.0f, FormCharacter.keyframe(Width) {
                path(FormCharacter.White) {
                    boundedArc(centerX, centerY, right, bottom, Angle.Ninety)
                }
            }),
            Keyframe(0.5f, FormCharacter.keyframe(Width) {
                path(FormCharacter.White) {
                    boundedArc(centerX, centerY, right, bottom, Angle.TwoSeventy)
                }
            })
        ),
        KeyframeTransition(
            Keyframe(0f, Two { translateNoop() }),
            Keyframe(0.5f, TwoExit { translate(-Width / 4f, 0f) })
        ),
        KeyframeTransition(
            Keyframe(0.5f, FormCharacter.keyframe(Width) {
                val transformation: PathTransformation =
                    { rotateNoop(centerX, centerY) }

                path(FormCharacter.Yellow, transformation) {
                    rect(quarterX, centerY, threeQuarterX, right)
                }
                path(FormCharacter.Yellow, transformation) {
                    boundedArc(centerX, centerY, right, bottom, Angle.TwoSeventy)
                }
                path(FormCharacter.White, transformation) {
                    boundedArc(centerX, centerY, right, bottom, Angle.TwoSeventy)
                }
            }),
            Keyframe(1f, FormCharacter.keyframe(Width) {
                val transformation: PathTransformation =
                    {
                        rotate(Angle.FortyFive, centerX, centerY)
                    }
                path(FormCharacter.Yellow, transformation) {
                    rect(centerX, centerY, centerX, right)
                }
                path(FormCharacter.Yellow, transformation) {
                    boundedArc(left, top, right, bottom, 450f.degrees)
                }
                path(FormCharacter.White, transformation) {
                    boundedArc(left, top, right, bottom, Angle.TwoSeventy)
                }
            })
        ),
    )
)
