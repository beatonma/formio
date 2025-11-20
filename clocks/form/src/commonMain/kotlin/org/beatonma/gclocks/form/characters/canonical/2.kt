package org.beatonma.gclocks.form.characters.canonical


import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.graphics.paths.Keyframe
import org.beatonma.gclocks.core.graphics.paths.KeyframeAnimation
import org.beatonma.gclocks.core.graphics.paths.KeyframeTransition
import org.beatonma.gclocks.core.graphics.paths.MultipartAnimation
import org.beatonma.gclocks.core.graphics.paths.PathAnimation
import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.core.util.lerp
import org.beatonma.gclocks.form.characters.FormCharacter

internal const val TwoWidth = 144f
private const val KeylineX = 8f
private const val KeylineY = 108f

internal val Two = Two()
internal inline fun Two(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(TwoWidth) {
        path(FormCharacter.White, transformation) {
            triangle(left, bottom, centerX, centerY, centerX, bottom)
        }

        path(FormCharacter.Orange, transformation) {
            boundedArc(centerX, top, right, centerY)
            rect(KeylineX, top, threeQuarterX, centerY)
        }

        path(FormCharacter.Yellow, transformation) {
            // lower rect
            rect(centerX, centerY, right, bottom)
        }
    }


private const val Left = 0f
private const val Top = 0f
private const val Right = TwoWidth
private const val Bottom = FormCharacter.Height
private const val CenterX = Right / 2f
private const val CenterY = Bottom / 2f
internal inline fun TwoEnter(crossinline transformation: Path.() -> Unit = {}) =
    MultipartAnimation(
        listOf(
            PathAnimation { path, progress, render ->
                // Orange upper part of 2
                val d1 = FormCharacter.easeProgress(progress, 0.3f, 0.8f)
                if (d1 > 0f) {
                    val d2 = FormCharacter.easeProgress(progress, 0.5f, 1f)

                    with(path) {
                        beginPath()
                        boundedArc(CenterX, Top, Right, CenterY)
                        rect(d2.lerp(CenterX, KeylineX), Top, d2.lerp(Bottom, KeylineY), CenterY)
                        translate(Left, d1.lerp(CenterX, Top))
                        transformation()
                    }

                    render(FormCharacter.Orange)
                }
            },
            KeyframeAnimation(
                Empty(TwoWidth),
                FormCharacter::ease,
                listOf(
                    KeyframeTransition(
                        Keyframe(0.5f, FormCharacter.keyframe(TwoWidth) {
                            path(FormCharacter.White, transformation) {
                                triangle(centerX, bottom, right, centerY, right, bottom)
                            }
                            path(FormCharacter.Yellow, transformation) {
                                rect(centerX, centerY, right, bottom)
                            }
                        }),
                        Keyframe(1f, FormCharacter.keyframe(TwoWidth) {
                            path(FormCharacter.White, transformation) {
                                triangle(left, bottom, centerX, centerY, centerX, bottom)
                            }
                            path(FormCharacter.Yellow, transformation) {
                                rect(centerX, centerY, right, bottom)
                            }
                        }),
                    ),
                )
            ),
        )
    )


internal inline fun TwoExit(transformation: PathTransformation = {}) =
    FormCharacter.keyframe(TwoWidth) {
        path(FormCharacter.White, transformation) {
            triangle(centerX, bottom, right, centerY, right, bottom)
        }

        path(FormCharacter.Orange, transformation) {
            boundedArc(centerX, centerY, right, bottom)
            rect(centerX, centerY, right, bottom)
        }

        path(FormCharacter.Yellow, transformation) {
            // lower rect
            rect(centerX, centerY, right, bottom)
        }
    }
