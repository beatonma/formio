package org.beatonma.gclocks.form.characters.canonical

import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.form.characters.FormCharacter

internal const val FiveWidth = 128f
private const val KeylineX = 80f

internal val Five = Five()
internal inline fun Five(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(FiveWidth) {
        path(FormCharacter.Yellow, transformation) {
            // upper right rect
            rect(KeylineX, top, right, thirdY)
        }

        path(FormCharacter.White, transformation) {
            boundedArc(KeylineX, twoThirdY, thirdY, close = true)
        }

        path(FormCharacter.Yellow, transformation) {
            // bottom left rect
            rect(left, twoThirdY, KeylineX, bottom)
        }

        path(FormCharacter.Orange, transformation) {
            // upper left rect
            rect(left, top, KeylineX, twoThirdY)
        }
    }


internal inline fun FiveEnter(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(FiveWidth) {
        path(FormCharacter.Yellow, transformation) {
            // upper right rect
            rect(KeylineX, centerY, KeylineX, bottom)
        }
        path(FormCharacter.White, transformation) {
            boundedArc(30f, threeQuarterY, quarterY, close = true)
        }

        path(FormCharacter.Yellow, transformation) {
            // bottom left rect
            rect(left, twoThirdY, KeylineX, bottom)
        }

        path(FormCharacter.Orange, transformation) {
            // upper left rect
            rect(left, centerY, KeylineX, bottom)
        }
    }

internal inline fun FiveExit(transformation: PathTransformation = {}) =
    FiveEnter(transformation)


internal inline fun FiveExitHalfPill(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(FiveWidth) {
        path(FormCharacter.Yellow, transformation) {
            // upper right rect
            rect(KeylineX, thirdY, KeylineX, bottom)
        }

        path(FormCharacter.White, transformation) {
            boundedArc(KeylineX, twoThirdY, thirdY, close = true)
        }

        path(FormCharacter.Yellow, transformation) {
            // bottom left rect
            rect(left, bottom, KeylineX, bottom)
        }

        path(FormCharacter.Orange, transformation) {
            // upper left rect
            rect(left, thirdY, KeylineX, bottom)
        }
    }
