package org.beatonma.gclocks.form.characters.transitional

import org.beatonma.gclocks.core.graphics.paths.Keyframe
import org.beatonma.gclocks.core.graphics.paths.KeyframeAnimation
import org.beatonma.gclocks.core.graphics.paths.KeyframeTransition
import org.beatonma.gclocks.form.characters.FormCharacter
import org.beatonma.gclocks.form.characters.canonical.Three
import org.beatonma.gclocks.form.characters.canonical.ThreeEnter
import org.beatonma.gclocks.form.characters.canonical.Two
import org.beatonma.gclocks.form.characters.canonical.TwoExit


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
