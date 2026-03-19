package org.beatonma.formio.form.characters.transitional

import org.beatonma.formio.core.graphics.paths.Keyframe
import org.beatonma.formio.core.graphics.paths.KeyframeAnimation
import org.beatonma.formio.core.graphics.paths.KeyframeTransition
import org.beatonma.formio.form.characters.FormCharacter
import org.beatonma.formio.form.characters.canonical.Five
import org.beatonma.formio.form.characters.canonical.FiveEnter
import org.beatonma.formio.form.characters.canonical.Four
import org.beatonma.formio.form.characters.canonical.FourExit

internal val FourFive = KeyframeAnimation(
    Four,
    FormCharacter::ease,
    transitions = listOf(
        KeyframeTransition(
            Keyframe(0f, Four { scaleNoop() }),
            Keyframe(0.5f, FourExit {
                // stretch to match width of collapsed FiveEnter
                scale(x = 1.11f, 1f)
            })
        ),
        KeyframeTransition(
            Keyframe(0.5f, FiveEnter {}),
            Keyframe(1f, Five {})
        )
    )
)
