package org.beatonma.gclocks.form.characters.transitional

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.graphics.paths.Keyframe
import org.beatonma.gclocks.core.graphics.paths.KeyframeAnimation
import org.beatonma.gclocks.core.graphics.paths.KeyframeTransition
import org.beatonma.gclocks.form.characters.FormCharacter
import org.beatonma.gclocks.form.characters.FormCharacter.pill
import org.beatonma.gclocks.form.characters.canonical.One
import org.beatonma.gclocks.form.characters.canonical.OneEnter
import org.beatonma.gclocks.form.characters.canonical.OneExit
import org.beatonma.gclocks.form.characters.canonical.Zero
import org.beatonma.gclocks.form.characters.canonical.ZeroWidth


internal val ZeroOne = KeyframeAnimation(
    Zero,
    FormCharacter::ease,
    listOf(
        KeyframeTransition(
            // Zero stretch
            Keyframe(0f, FormCharacter.keyframe(Zero.width) {
                pill(
                    left,
                    top,
                    right,
                    bottom,
                    FormCharacter.Yellow,
                    FormCharacter.White,
                    split = 0f,
                    transformation = { rotate(Angle.FortyFive, radius, radius) })
            }),
            Keyframe(0.5f, FormCharacter.keyframe(192f) {
                val radius = radius * 0.7f
                val diameter = radius * 2f
                val pivotX = width - radius
                pill(
                    left,
                    bottom - diameter,
                    right,
                    bottom,
                    FormCharacter.Yellow,
                    FormCharacter.White,
                    split = 1f,
                    transformation = { rotate(Angle.Zero, pivotX, bottom) })
            })
        ),

        KeyframeTransition(
            // Zero contract
            Keyframe(0.5f, FormCharacter.keyframe(192f) {
                val radius = radius * 0.7f
                val diameter = radius * 2f

                pill(
                    left,
                    bottom - diameter,
                    right,
                    bottom,
                    FormCharacter.Yellow,
                    FormCharacter.White,
                    split = 1f,
                    transformation = { scaleNoop(centerX, height) })
            }),
            Keyframe(1f, FormCharacter.keyframe(100f) {
                val diameter = radius * 2f

                pill(
                    centerX,
                    bottom - diameter,
                    twoThirdX,
                    bottom,
                    FormCharacter.Yellow,
                    FormCharacter.White,
                    split = 1f,
                    transformation = { scale(0.7f, threeQuarterX, twoThirdY) })
            }),
        ),

        KeyframeTransition(
            // One
            Keyframe(0.5f, OneEnter()),
            Keyframe(1f, One()),
        )
    )
)

internal val OneZero = KeyframeAnimation(
    One,
    FormCharacter::ease,
    listOf(
        KeyframeTransition(
            // One
            Keyframe(0f, One),
            Keyframe(0.5f, OneExit()),
        ),
        KeyframeTransition(
            // Zero collapsed
            Keyframe(0.2f, FormCharacter.keyframe(ZeroWidth) {
                pill(
                    thirdX, centerY, twoThirdX, bottom,
                    FormCharacter.Yellow, FormCharacter.White,
                    1f,
                )
            }),
            Keyframe(0.5f, FormCharacter.keyframe(ZeroWidth) {
                pill(
                    left, centerY, right, bottom,
                    FormCharacter.Yellow, FormCharacter.White,
                    1f,
                )
            }),
        ),

        KeyframeTransition(
            // Zero collapsed
            Keyframe(0.5f, FormCharacter.keyframe(ZeroWidth) {
                pill(
                    left, centerY, right, bottom,
                    FormCharacter.Yellow, FormCharacter.White,
                    1f,
                    transformation = { rotateNoop(centerX, centerY) }
                )
            }),
            Keyframe(1f, FormCharacter.keyframe(ZeroWidth) {
                pill(
                    left, top, right, bottom,
                    FormCharacter.Yellow, FormCharacter.White,
                    1f,
                    transformation = { rotate(Angle.FortyFive, centerX, centerY) }
                )
            }),
        )
    )
)
