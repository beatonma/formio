package org.beatonma.gclocks.form.characters.transitional

import org.beatonma.gclocks.core.graphics.paths.Keyframe
import org.beatonma.gclocks.core.graphics.paths.KeyframeAnimation
import org.beatonma.gclocks.core.graphics.paths.KeyframeTransition
import org.beatonma.gclocks.core.graphics.paths.MultipartAnimation
import org.beatonma.gclocks.form.characters.FormCharacter
import org.beatonma.gclocks.form.characters.canonical.One
import org.beatonma.gclocks.form.characters.canonical.OneEnter
import org.beatonma.gclocks.form.characters.canonical.OneExit
import org.beatonma.gclocks.form.characters.canonical.Two
import org.beatonma.gclocks.form.characters.canonical.TwoEnter
import org.beatonma.gclocks.form.characters.canonical.TwoExit


internal val OneTwo = MultipartAnimation(
    listOf(
        TwoEnter { },
        KeyframeAnimation(
            One,
            FormCharacter::ease,
            listOf(
                KeyframeTransition(
                    Keyframe(0f, One { translateNoop() }),
                    Keyframe(0.5f, OneExit { translate(44f, 0f) }),
                ),
            )
        ),
    )
)

internal val TwoOne = KeyframeAnimation(
    Two,
    FormCharacter::ease,
    listOf(
        KeyframeTransition(
            Keyframe(0f, Two { }),
            Keyframe(0.5f, TwoExit { }),
        ),

        KeyframeTransition(
            Keyframe(0.5f, OneEnter { translate(44f, 0f) }),
            Keyframe(1f, One { translateNoop() }),
        )
    )
)
