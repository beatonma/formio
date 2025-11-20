package org.beatonma.gclocks.form.characters.canonical

import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.form.characters.FormCharacter
import org.beatonma.gclocks.form.characters.FormCharacter.pill

internal const val EightWidth = 144f

private const val KeylineMiniPillLeft = EightWidth / 6f
private const val KeylineMiniPillRight = KeylineMiniPillLeft * 5f

internal val Eight = Eight()
internal inline fun Eight(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(EightWidth) {
        path(FormCharacter.White, transformation) {
            // top
            roundRect(KeylineMiniPillLeft, top, KeylineMiniPillRight, thirdY, radius = 24f)
        }

        // bottom
        pill(
            left,
            thirdY,
            right,
            bottom,
            FormCharacter.Orange,
            FormCharacter.Yellow,
            split = 1f,
            transformation
        )
    }


internal inline fun EightEnter(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(EightWidth) {
        path(FormCharacter.White, transformation) {
            // top
            roundRect(KeylineMiniPillLeft, twoThirdY, KeylineMiniPillRight, bottom, radius = 24f)
        }

        // bottom
        pill(
            KeylineMiniPillLeft,
            twoThirdY,
            KeylineMiniPillRight,
            bottom,
            FormCharacter.Orange,
            FormCharacter.Yellow,
            split = 0f,
            transformation
        )
    }

internal inline fun EightExit(transformation: PathTransformation = {}) = EightEnter(transformation)
