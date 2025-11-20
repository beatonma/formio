package org.beatonma.gclocks.form.characters.canonical

import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.form.characters.FormCharacter

internal const val ThreeWidth = 128f

private const val KeylineX2 = 56f
private const val KeylineX3 = 80f

internal val Three = Three()
internal inline fun Three(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(ThreeWidth) {
        path(FormCharacter.White, transformation) {
            // half-circle
            boundedArc(quarterX, thirdY, right, bottom, close = true)
        }
        path(FormCharacter.Orange, transformation) {
            // bottom rectangle
            rect(left, twoThirdY, KeylineX3, bottom)
        }
        path(FormCharacter.White, transformation) {
            // top part with triangle
            tetragon(
                left, top,
                right, top,
                KeylineX3, thirdY,
                left, thirdY
            )
        }
        path(FormCharacter.Yellow, transformation) {
            // middle rectangle
            rect(quarterX, thirdY, KeylineX3, twoThirdY)
        }
        path(FormCharacter.White, transformation) {
            // rectangle used for transition to ThreeExit
            rect(left, top, KeylineX3, thirdY)
        }
    }

internal inline fun ThreeEnter(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(ThreeWidth) {
        path(FormCharacter.White, transformation) {
            // half-circle
            boundedArc(KeylineX2, centerY, right, bottom, close = true)
        }
        path(FormCharacter.Orange, transformation) {
            // bottom rectangle
            rect(KeylineX2, centerY, right, bottom)
        }
        path(FormCharacter.White, transformation) {
            // top part with triangle

            tetragon(
                KeylineX2, centerY,
                right, centerY,
                KeylineX3, bottom,
                KeylineX2, bottom
            )
        }
        path(FormCharacter.Yellow, transformation) {
            // middle rectangle
            rect(KeylineX2, centerY, right, bottom)
        }
        path(FormCharacter.White, transformation) {
            // rectangle used for transition to ThreeExit
            rect(KeylineX2, centerY, right, centerY)
        }
    }

internal inline fun ThreeExit(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(ThreeWidth) {
        path(FormCharacter.White, transformation) {
            // half-circle
            boundedArc(KeylineX2, centerY, right, bottom, close = true)
        }
        path(FormCharacter.Orange, transformation) {
            // bottom rectangle
            rect(KeylineX2, centerY, right, bottom)
        }
        path(FormCharacter.White, transformation) {
            // top part with triangle
            tetragon(
                KeylineX2, centerY,
                right, centerY,
                KeylineX3, bottom,
                KeylineX2, bottom,
            )
        }
        path(FormCharacter.Yellow, transformation) {
            // middle rectangle
            rect(KeylineX2, centerY, right, bottom)
        }
        path(FormCharacter.White, transformation) {
            rect(KeylineX2, centerY, right, bottom)
        }
    }


internal inline fun ThreeExitHalfPill(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(ThreeWidth) {
        path(FormCharacter.White, transformation) {
            // half-circle
            boundedArc(quarterX, thirdY, right, bottom, close = true)
        }
        path(FormCharacter.Orange, transformation) {
            // bottom rectangle
            rect(quarterX, twoThirdY, KeylineX3, bottom)
        }
        path(FormCharacter.White, transformation) {
            // top part with triangle
            tetragon(
                quarterX, thirdY,
                KeylineX3, thirdY,
                KeylineX3, twoThirdY,
                quarterX, twoThirdY
            )
        }
        path(FormCharacter.Yellow, transformation) {
            // middle rectangle
            rect(quarterX, thirdY, KeylineX3, twoThirdY)
        }
        path(FormCharacter.White, transformation) {
            // rectangle used for transition to ThreeExit
            rect(quarterX, thirdY, quarterX, thirdY)
        }
    }
