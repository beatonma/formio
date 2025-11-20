package org.beatonma.gclocks.form.characters.transitional

import org.beatonma.gclocks.core.graphics.paths.Keyframe
import org.beatonma.gclocks.core.graphics.paths.KeyframeAnimation
import org.beatonma.gclocks.core.graphics.paths.KeyframeTransition
import org.beatonma.gclocks.form.characters.FormCharacter
import org.beatonma.gclocks.form.characters.canonical.Five
import org.beatonma.gclocks.form.characters.canonical.FiveEnter
import org.beatonma.gclocks.form.characters.canonical.Four
import org.beatonma.gclocks.form.characters.canonical.FourExit

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
