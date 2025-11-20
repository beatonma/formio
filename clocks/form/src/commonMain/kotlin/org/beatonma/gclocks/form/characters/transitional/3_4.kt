package org.beatonma.gclocks.form.characters.transitional

import org.beatonma.gclocks.core.graphics.paths.Keyframe
import org.beatonma.gclocks.core.graphics.paths.KeyframeAnimation
import org.beatonma.gclocks.core.graphics.paths.KeyframeTransition
import org.beatonma.gclocks.form.characters.FormCharacter
import org.beatonma.gclocks.form.characters.canonical.Four
import org.beatonma.gclocks.form.characters.canonical.FourEnter
import org.beatonma.gclocks.form.characters.canonical.Three
import org.beatonma.gclocks.form.characters.canonical.ThreeExit

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
