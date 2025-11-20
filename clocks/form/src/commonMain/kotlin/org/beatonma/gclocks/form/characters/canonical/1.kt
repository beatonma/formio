package org.beatonma.gclocks.form.characters.canonical

import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.form.characters.FormCharacter

internal const val OneWidth = 100f
private const val KeylineX = 28f

internal val One = One()
internal inline fun One(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(OneWidth) {
        path(FormCharacter.Yellow, transformation) {
            // upper
            rect(left, top, right, thirdY)
        }

        path(FormCharacter.White, transformation) {
            // lower
            rect(KeylineX, thirdY, right, bottom)
        }
    }

internal inline fun OneEnter(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(OneWidth) {
        path(FormCharacter.Yellow, transformation) {
            // upper
            rect(KeylineX, centerY, right, bottom)
        }

        path(FormCharacter.White, transformation) {
            // lower
            rect(KeylineX, bottom, right, bottom)
        }
    }

internal inline fun OneExit(transformation: PathTransformation = {}) =
    OneEnter(transformation)
