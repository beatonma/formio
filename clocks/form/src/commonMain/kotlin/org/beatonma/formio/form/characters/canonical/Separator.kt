package org.beatonma.formio.form.characters.canonical

import org.beatonma.formio.core.graphics.Path
import org.beatonma.formio.core.graphics.paths.PathTransformation
import org.beatonma.formio.form.characters.FormCharacter

internal const val SeparatorWidth = 48f

internal val Separator = Separator()
internal inline fun Separator(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(SeparatorWidth) {
        path(FormCharacter.Yellow, transformation) {
            circle(centerX, radius, radius, Path.Direction.Clockwise)
        }
        path(FormCharacter.White, transformation) {
            circle(centerX, bottom - radius, radius, Path.Direction.Clockwise)
        }
    }

internal inline fun SeparatorEnter(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(SeparatorWidth) {
        path(FormCharacter.Yellow, transformation) {
            circle(centerX, bottom - radius, radius, Path.Direction.Clockwise)
        }
        path(FormCharacter.White, transformation) {
            circle(centerX, bottom - radius, radius, Path.Direction.Clockwise)
        }
    }

internal inline fun SeparatorExit(transformation: PathTransformation = {}) =
    SeparatorEnter(transformation)
