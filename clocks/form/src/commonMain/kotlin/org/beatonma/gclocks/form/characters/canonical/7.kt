package org.beatonma.gclocks.form.characters.canonical

import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.form.characters.FormCharacter

internal const val SevenWidth = 144f

internal val Seven = Seven()
internal inline fun Seven(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(SevenWidth) {
        path(FormCharacter.White, transformation) {
            // upper left
            tetragon(
                left, top,
                centerX, top,
                quarterX, centerY,
                left, centerY
            )
        }

        path(FormCharacter.Yellow, transformation) {
            // parallelogram
            tetragon(
                centerX, top,
                right, top,
                centerX, bottom,
                left, bottom,
            )
        }
    }


internal fun SevenEnter(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(SevenWidth) {
        path(FormCharacter.White, transformation) {
            // upper left
            tetragon(
                left, centerY,
                centerX, centerY,
                centerX, bottom,
                left, bottom
            )
        }

        path(FormCharacter.Yellow, transformation) {
            // parallelogram
            tetragon(
                left, centerY,
                centerX, centerY,
                centerX, bottom,
                left, bottom
            )
        }
    }

internal fun SevenExit(transformation: PathTransformation = {}) =
    SevenEnter(transformation)
