package org.beatonma.formio.form.characters.transitional

import org.beatonma.formio.core.graphics.paths.Keyframe
import org.beatonma.formio.core.graphics.paths.KeyframeAnimation
import org.beatonma.formio.core.graphics.paths.KeyframeTransition
import org.beatonma.formio.form.characters.FormCharacter
import org.beatonma.formio.form.characters.canonical.Three
import org.beatonma.formio.form.characters.canonical.ThreeEnter
import org.beatonma.formio.form.characters.canonical.Two
import org.beatonma.formio.form.characters.canonical.TwoExit


internal val TwoThree = KeyframeAnimation(
    Two,
    easing = FormCharacter::ease,
    transitions = listOf(
        KeyframeTransition(
            Keyframe(0f, Two { translateNoop() }),
            Keyframe(0.5f, TwoExit { translate(-16f, 0f) })
        ),
        KeyframeTransition(
            Keyframe(0.5f, ThreeEnter { }),
            Keyframe(1f, Three { })
        )
    )
)
