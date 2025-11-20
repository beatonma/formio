package org.beatonma.gclocks.form.characters.transitional

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.graphics.paths.Keyframe
import org.beatonma.gclocks.core.graphics.paths.KeyframeAnimation
import org.beatonma.gclocks.core.graphics.paths.KeyframeTransition
import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.form.characters.FormCharacter
import org.beatonma.gclocks.form.characters.FormCharacter.pill
import org.beatonma.gclocks.form.characters.canonical.Eight
import org.beatonma.gclocks.form.characters.canonical.EightWidth
import org.beatonma.gclocks.form.characters.canonical.NineWidth

internal val EightNine = KeyframeAnimation(
    Eight,
    FormCharacter::ease,
    transitions = listOf(
        KeyframeTransition(
            Keyframe(0f, Eight {}),
            Keyframe(0.5f, FormCharacter.keyframe(EightWidth) {
                path(FormCharacter.White) {
                    // top
                    roundRect(width / 6f, thirdY, width * 5f / 6f, twoThirdY, radius = 24f)
                }

                // bottom
                pill(
                    left,
                    top,
                    right,
                    bottom,
                    FormCharacter.Orange,
                    FormCharacter.Yellow,
                    split = 0.5f
                )
            }),
        ),
        KeyframeTransition(
            Keyframe(0.5f, FormCharacter.keyframe(NineWidth) {
                val transformation: PathTransformation = {
                    rotateNoop(centerX, centerY)
                }
                path(FormCharacter.White, transformation) {
                    tetragon(
                        centerX, centerY,
                        centerX, bottom,
                        centerX, bottom,
                        centerX, centerY,
                    )
                }

                path(FormCharacter.Orange, transformation) {
                    boundedArc(left, top, right, bottom, Angle.Ninety, close = true)
                }

                path(FormCharacter.Yellow, transformation) {
                    boundedArc(left, top, right, bottom, Angle.TwoSeventy, close = true)
                }
            }),
            Keyframe(1f, FormCharacter.keyframe(NineWidth) {
                val transformation: PathTransformation = {
                    rotate(-Angle.Ninety, centerX, centerY)
                }

                path(FormCharacter.White, transformation) {
                    tetragon(
                        centerX, centerY,
                        centerX, bottom,
                        left, threeQuarterY,
                        left, quarterX
                    )
                }

                path(FormCharacter.Orange, transformation) {
                    boundedArc(left, top, right, bottom, -Angle.Ninety, close = true)
                }

                path(FormCharacter.Yellow, transformation) {
                    boundedArc(left, top, right, bottom, Angle.TwoSeventy, close = true)
                }
            })
        )
    )
)
