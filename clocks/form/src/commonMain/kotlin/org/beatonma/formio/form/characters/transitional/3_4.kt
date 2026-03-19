package org.beatonma.formio.form.characters.transitional

import org.beatonma.formio.core.graphics.paths.Keyframe
import org.beatonma.formio.core.graphics.paths.KeyframeAnimation
import org.beatonma.formio.core.graphics.paths.KeyframeTransition
import org.beatonma.formio.form.characters.FormCharacter
import org.beatonma.formio.form.characters.canonical.Four
import org.beatonma.formio.form.characters.canonical.FourEnter
import org.beatonma.formio.form.characters.canonical.Three
import org.beatonma.formio.form.characters.canonical.ThreeExit

internal val ThreeFour = KeyframeAnimation(
    Three,
    FormCharacter::ease,
    transitions = listOf(
        KeyframeTransition(
            Keyframe(0f, Three { translateNoop() }),
            Keyframe(0.5f, ThreeExit { translate(16f, 0f) })
        ),
        KeyframeTransition(
            Keyframe(0.5f, FourEnter {}),
            Keyframe(1f, Four {})
        )
    )
)
