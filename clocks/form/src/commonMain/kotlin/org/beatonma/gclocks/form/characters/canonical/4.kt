package org.beatonma.gclocks.form.characters.canonical

import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.form.characters.FormCharacter

internal const val FourWidth = 144f

internal val Four = Four()
internal inline fun Four(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(FourWidth) {
        path(FormCharacter.Yellow, transformation) {
            // bottom
            rect(centerX, threeQuarterY, right, bottom)
        }
        path(FormCharacter.Yellow, transformation) {
            triangle(
                centerX, top,
                centerX, centerY,
                left, centerY,
            )
            scale(1f, right, bottom)
        }
        path(FormCharacter.Orange, transformation) {
            // middle
            rect(left, centerY, right, threeQuarterY)
        }
        path(FormCharacter.White, transformation) {
            // upper
            rect(centerX, top, right, centerY)
        }
        path(FormCharacter.Orange, transformation) {
            // middle
            rect(left, centerY, right, threeQuarterY)
        }
    }


internal inline fun FourEnter(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(FourWidth) {
        path(FormCharacter.Yellow, transformation) {
            // bottom
            rect(centerX, bottom, right, bottom)
        }
        path(FormCharacter.Yellow, transformation) {
            triangle(
                centerX, top,
                centerX, centerY,
                left, centerY,
            )
            scale(0f, right, bottom)
        }
        path(FormCharacter.Orange, transformation) {
            // middle
            rect(centerX, bottom, right, bottom)
        }
        path(FormCharacter.White, transformation) {
            // upper
            rect(centerX, centerY, right, bottom)
        }
        path(FormCharacter.Orange, transformation) {
            // middle
            rect(centerX, bottom, right, bottom)
        }
    }

internal inline fun FourExit(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(FourWidth) {
        path(FormCharacter.Yellow, transformation) {
            // bottom
            rect(left, centerY, centerX, bottom)
        }
        path(FormCharacter.Yellow, transformation) {
            triangle(
                centerX, centerY,
                centerX, bottom,
                left, bottom,
            )
            scale(1f, right, bottom)
        }
        path(FormCharacter.Orange, transformation) {
            // middle
            rect(left, centerY, centerX, bottom)
        }
        path(FormCharacter.White, transformation) {
            // upper
            rect(left, centerY, centerX, bottom)
        }
        path(FormCharacter.Orange, transformation) {
            // middle
            rect(left, centerY, centerX, bottom)
        }
    }
