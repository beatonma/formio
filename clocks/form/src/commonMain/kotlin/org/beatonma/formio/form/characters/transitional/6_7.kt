package org.beatonma.formio.form.characters.transitional

import org.beatonma.formio.core.geometry.Angle
import org.beatonma.formio.core.geometry.degrees
import org.beatonma.formio.core.graphics.paths.Keyframe
import org.beatonma.formio.core.graphics.paths.KeyframeAnimation
import org.beatonma.formio.core.graphics.paths.KeyframeTransition
import org.beatonma.formio.form.characters.FormCharacter
import org.beatonma.formio.form.characters.canonical.SevenWidth
import org.beatonma.formio.form.characters.canonical.Six
import org.beatonma.formio.form.characters.canonical.SixWidth


internal val SixSeven = KeyframeAnimation(
    Six,
    FormCharacter::ease,
    transitions = listOf(
        KeyframeTransition(
            Keyframe(0f, start()),
            Keyframe(1f, end())
        ),
    )
)


private fun start() = FormCharacter.keyframe(SixWidth) {
    path(FormCharacter.White, {
        rotateNoop(centerX, centerY)
        translateNoop()
    }) {
        // 6 lower
        sector(centerX, centerY, radius, Angle.OneEighty, -Angle.OneEighty)
    }

    path(FormCharacter.White) {
        // 7 upper left
        tetragon(
            centerX, top,
            centerX, top,
            centerX, centerY,
            centerX, centerY
        )
    }

    path(FormCharacter.Yellow) {
        // 6+7 parallelogram
        tetragon(
            36f, top,
            threeQuarterX, top,
            centerX, centerY,
            left, centerY,
        )
    }
}

private fun end() = FormCharacter.keyframe(SevenWidth) {
    path(FormCharacter.White, {
        rotate(-(244f).degrees, centerX, centerY)
        translate(36f, 0f)
    }) {
        // 6 lower
        sector(centerX, centerY, radius, Angle.OneEighty, -Angle.OneEighty)
    }

    path(FormCharacter.White) {
        // 7 upper left
        tetragon(
            left, top,
            centerX, top,
            quarterX, centerY,
            left, centerY
        )
    }

    path(FormCharacter.Yellow) {
        // 6+7 parallelogram
        tetragon(
            centerX, top,
            right, top,
            centerX, bottom,
            left, bottom,
        )
    }
}
