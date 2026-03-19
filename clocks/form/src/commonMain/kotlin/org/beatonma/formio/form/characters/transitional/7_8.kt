package org.beatonma.formio.form.characters.transitional

import org.beatonma.formio.core.geometry.Angle
import org.beatonma.formio.core.graphics.paths.Keyframe
import org.beatonma.formio.core.graphics.paths.KeyframeAnimation
import org.beatonma.formio.core.graphics.paths.KeyframeTransition
import org.beatonma.formio.form.characters.FormCharacter
import org.beatonma.formio.form.characters.canonical.Eight
import org.beatonma.formio.form.characters.canonical.EightEnter
import org.beatonma.formio.form.characters.canonical.Seven
import org.beatonma.formio.form.characters.canonical.SevenExit

internal val SevenEight = KeyframeAnimation(
    Seven,
    FormCharacter::ease,
    transitions = listOf(
        KeyframeTransition(
            Keyframe(0f, Seven {
                scaleNoop(0f, FormCharacter.Height)
                translateNoop()
            }),
            Keyframe(0.4f, SevenExit {
                scale(2f / 3f, 0f, FormCharacter.Height)
                translate(48f, 0f)
            })
        ),
        KeyframeTransition(
            Keyframe(0.2f, FormCharacter.keyframe(144f) {
                path(FormCharacter.Orange) {
                    boundedArc(
                        thirdX,
                        twoThirdY,
                        twoThirdX,
                        bottom,
                        startAngle = Angle.Ninety,
                        close = true
                    )
                }
                path(FormCharacter.Yellow) {
                    boundedArc(thirdX, twoThirdY, twoThirdX, bottom, close = true)
                }
                path(FormCharacter.Yellow) {
                    rect(thirdX, twoThirdY, twoThirdX, bottom)
                }
            }),
            Keyframe(0.5f, FormCharacter.keyframe(144f) {
                val sixthX = width / 6f
                path(FormCharacter.Orange) {
                    boundedArc(
                        sixthX,
                        twoThirdY,
                        centerX,
                        bottom,
                        startAngle = Angle.Ninety,
                        close = true
                    )
                }
                path(FormCharacter.Yellow) {
                    boundedArc(centerX, twoThirdY, width - sixthX, bottom, close = true)
                }
                path(FormCharacter.Yellow) {
                    rect(thirdX, twoThirdY, twoThirdX, bottom)
                }
            })
        ),
        KeyframeTransition(
            Keyframe(0.5f, EightEnter { }),
            Keyframe(1f, Eight { })
        ),
    )
)
