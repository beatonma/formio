package org.beatonma.gclocks.form.characters.transitional

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.graphics.paths.Keyframe
import org.beatonma.gclocks.core.graphics.paths.KeyframeAnimation
import org.beatonma.gclocks.core.graphics.paths.KeyframeTransition
import org.beatonma.gclocks.core.graphics.paths.MultipartAnimation
import org.beatonma.gclocks.core.graphics.paths.PathImage
import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.core.graphics.paths.StaticImage
import org.beatonma.gclocks.form.characters.FormCharacter
import org.beatonma.gclocks.form.characters.canonical.Five
import org.beatonma.gclocks.form.characters.canonical.FiveExit
import org.beatonma.gclocks.form.characters.canonical.Six
import org.beatonma.gclocks.form.characters.canonical.SixWidth

private fun PathImage.Builder.sharedBoundedArc(transformation: PathTransformation = {}) {
    path(FormCharacter.White, transformation) {
        boundedArc(80f, twoThirdY, thirdY, close = true)
    }
}

private val SharedBoundedArc = FormCharacter.keyframe(SixWidth) {
    sharedBoundedArc()
}

private const val SixRotationPivotX = SixWidth / 2f
private const val SixRotationPivotY = FormCharacter.Height / 2f
private const val SixScalePivotX = SixWidth / 2f
private const val SixScalePivotY = FormCharacter.Height

internal val FiveSix = MultipartAnimation(
    listOf(
        KeyframeAnimation(
            Five,
            FormCharacter::ease,
            transitions = listOf(
                KeyframeTransition(
                    Keyframe(0f, Five { scaleNoop(160f, 96f) }),
                    Keyframe(0.7f, FiveExit { scale(0.5f, 160f, 96f) })
                ),
            ),
        ),
        StaticImage(SharedBoundedArc, start = 0f, end = 0.1f),
        KeyframeAnimation(
            SharedBoundedArc,
            FormCharacter::ease,
            transitions = listOf(
                KeyframeTransition(
                    Keyframe(0.1f, FormCharacter.keyframe(SixWidth) {
                        val transformation: PathTransformation =
                            {
                                rotate(-Angle.Ninety, SixRotationPivotX, SixRotationPivotY)
                                scale(2f / 3f, SixScalePivotX, SixScalePivotY)
                                translate(8f, 0f)
                            }

                        path(FormCharacter.White, transformation) {
                            // lower
                            sector(
                                centerX,
                                centerY,
                                radius,
                                Angle.Zero,
                                Angle.OneEighty,
                            )
                        }

                        path(FormCharacter.Yellow, transformation) {
                            // upper
                            tetragon(
                                left, centerY,
                                centerX, centerY,
                                centerX, centerY,
                                left, centerY,
                            )
                        }
                    }),
                    Keyframe(
                        1f,
                        Six {
                            rotateNoop(SixRotationPivotX, SixRotationPivotY)
                            scaleNoop(SixScalePivotX, SixScalePivotY)
                            translateNoop()
                        }
                    )
                )
            )
        )
    )
)
