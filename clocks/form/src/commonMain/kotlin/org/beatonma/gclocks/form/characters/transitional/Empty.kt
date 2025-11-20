package org.beatonma.gclocks.form.characters.transitional

import org.beatonma.gclocks.core.geometry.degrees
import org.beatonma.gclocks.core.graphics.paths.Keyframe
import org.beatonma.gclocks.core.graphics.paths.KeyframeAnimation
import org.beatonma.gclocks.core.graphics.paths.KeyframeTransition
import org.beatonma.gclocks.core.graphics.paths.MultipartAnimation
import org.beatonma.gclocks.core.graphics.paths.PathAnimation
import org.beatonma.gclocks.core.graphics.paths.PathImage
import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.core.util.lerp
import org.beatonma.gclocks.core.util.progressIn
import org.beatonma.gclocks.form.characters.FormCharacter
import org.beatonma.gclocks.form.characters.canonical.Eight
import org.beatonma.gclocks.form.characters.canonical.EightEnter
import org.beatonma.gclocks.form.characters.canonical.EightExit
import org.beatonma.gclocks.form.characters.canonical.EightWidth
import org.beatonma.gclocks.form.characters.canonical.Empty
import org.beatonma.gclocks.form.characters.canonical.Five
import org.beatonma.gclocks.form.characters.canonical.FiveEnter
import org.beatonma.gclocks.form.characters.canonical.FiveExit
import org.beatonma.gclocks.form.characters.canonical.FiveWidth
import org.beatonma.gclocks.form.characters.canonical.Four
import org.beatonma.gclocks.form.characters.canonical.FourEnter
import org.beatonma.gclocks.form.characters.canonical.FourExit
import org.beatonma.gclocks.form.characters.canonical.FourWidth
import org.beatonma.gclocks.form.characters.canonical.Nine
import org.beatonma.gclocks.form.characters.canonical.NineEnter
import org.beatonma.gclocks.form.characters.canonical.NineWidth
import org.beatonma.gclocks.form.characters.canonical.One
import org.beatonma.gclocks.form.characters.canonical.OneEnter
import org.beatonma.gclocks.form.characters.canonical.OneExit
import org.beatonma.gclocks.form.characters.canonical.OneWidth
import org.beatonma.gclocks.form.characters.canonical.Separator
import org.beatonma.gclocks.form.characters.canonical.SeparatorEnter
import org.beatonma.gclocks.form.characters.canonical.SeparatorExit
import org.beatonma.gclocks.form.characters.canonical.SeparatorWidth
import org.beatonma.gclocks.form.characters.canonical.Seven
import org.beatonma.gclocks.form.characters.canonical.SevenEnter
import org.beatonma.gclocks.form.characters.canonical.SevenExit
import org.beatonma.gclocks.form.characters.canonical.SevenWidth
import org.beatonma.gclocks.form.characters.canonical.Six
import org.beatonma.gclocks.form.characters.canonical.SixEnter
import org.beatonma.gclocks.form.characters.canonical.SixWidth
import org.beatonma.gclocks.form.characters.canonical.Three
import org.beatonma.gclocks.form.characters.canonical.ThreeEnter
import org.beatonma.gclocks.form.characters.canonical.ThreeExit
import org.beatonma.gclocks.form.characters.canonical.ThreeWidth
import org.beatonma.gclocks.form.characters.canonical.Two
import org.beatonma.gclocks.form.characters.canonical.TwoEnter
import org.beatonma.gclocks.form.characters.canonical.TwoExit
import org.beatonma.gclocks.form.characters.canonical.TwoWidth
import org.beatonma.gclocks.form.characters.canonical.Zero
import org.beatonma.gclocks.form.characters.canonical.ZeroEnterSmallPill
import org.beatonma.gclocks.form.characters.canonical.ZeroExitSmallPill
import org.beatonma.gclocks.form.characters.canonical.ZeroPill
import org.beatonma.gclocks.form.characters.canonical.ZeroWidth

private fun enter(
    image: (PathTransformation) -> PathImage,
    pivotX: Float = 0f,
    pivotY: Float = FormCharacter.Height,
): KeyframeTransition =
    KeyframeTransition(
        Keyframe(0f, image { scale(0f, pivotX, pivotY) }),
        Keyframe(0.5f, image { scaleNoop(pivotX, pivotY) })
    )

private fun enterVertical(
    image: (PathTransformation) -> PathImage,
    pivotX: Float = 0f,
    pivotY: Float = FormCharacter.Height,
): KeyframeTransition =
    KeyframeTransition(
        Keyframe(0f, image { scale(1f, 0f, pivotX, pivotY) }),
        Keyframe(0.5f, image { scaleNoop(pivotX, pivotY) })
    )

private fun enterHorizontal(
    image: (PathTransformation) -> PathImage,
    pivotX: Float = 0f,
    pivotY: Float = FormCharacter.Height,
): KeyframeTransition =
    KeyframeTransition(
        Keyframe(0f, image { scale(0f, 1f, pivotX, pivotY) }),
        Keyframe(0.5f, image { scaleNoop(pivotX, pivotY) })
    )

private fun exit(
    image: (PathTransformation) -> PathImage,
    pivotX: Float = 0f,
    pivotY: Float = FormCharacter.Height,
): KeyframeTransition =
    KeyframeTransition(
        Keyframe(0.5f, image { scaleNoop(pivotX, pivotY) }),
        Keyframe(1f, image { scale(0f, pivotX, pivotY) })
    )

private fun exitVertical(
    image: (PathTransformation) -> PathImage,
    pivotX: Float = 0f,
    pivotY: Float = FormCharacter.Height,
): KeyframeTransition =
    KeyframeTransition(
        Keyframe(0.5f, image { scaleNoop(pivotX, pivotY) }),
        Keyframe(1f, image { scale(1f, 0f, pivotX, pivotY) })
    )

private fun exitHorizontal(
    image: (PathTransformation) -> PathImage,
    pivotX: Float = 0f,
    pivotY: Float = FormCharacter.Height,
): KeyframeTransition =
    KeyframeTransition(
        Keyframe(0.5f, image { scaleNoop(pivotX, pivotY) }),
        Keyframe(1f, image { scale(0f, 1f, pivotX, pivotY) })
    )


internal val EmptyZero = KeyframeAnimation(
    Empty(ZeroWidth),
    FormCharacter::ease,
    listOf(
        enter(::ZeroEnterSmallPill, ZeroWidth / 2f),
        KeyframeTransition(
            Keyframe(0.5f, ZeroEnterSmallPill { }),
            Keyframe(1f, ZeroPill { })
        )
    )
)
internal val ZeroEmpty = KeyframeAnimation(
    Zero,
    FormCharacter::ease,
    listOf(
        KeyframeTransition(
            Keyframe(0f, ZeroPill { }),
            Keyframe(0.5f, ZeroExitSmallPill { })
        ),
        exit(::ZeroExitSmallPill, ZeroWidth / 2f),
    )
)

internal val EmptyOne = KeyframeAnimation(
    Empty(OneWidth),
    FormCharacter::ease,
    listOf(
        enter(::OneEnter, OneWidth),
        KeyframeTransition(
            Keyframe(0.5f, OneEnter { }),
            Keyframe(1f, One)
        )
    )
)
internal val OneEmpty = KeyframeAnimation(
    One,
    FormCharacter::ease,
    listOf(
        KeyframeTransition(
            Keyframe(0f, One { }),
            Keyframe(0.5f, OneExit { })
        ),
        exit(::OneExit, OneWidth),
    )
)

internal val EmptyTwo = MultipartAnimation(
    listOf(
        KeyframeAnimation(
            Empty(TwoWidth),
            FormCharacter::ease,
            listOf(
                KeyframeTransition(
                    Keyframe(0f, FormCharacter.keyframe(TwoWidth) {
                        path(FormCharacter.Yellow) {
                            rect(right, bottom, right, bottom)
                        }
                    }),
                    Keyframe(0.5f, FormCharacter.keyframe(TwoWidth) {
                        path(FormCharacter.Yellow) {
                            rect(centerX, centerY, right, bottom)
                        }
                    })
                )
            )
        ),
        TwoEnter()
    )
)

internal val TwoEmpty = KeyframeAnimation(
    Two,
    FormCharacter::ease,
    listOf(
        KeyframeTransition(
            Keyframe(0f, Two { }),
            Keyframe(0.5f, TwoExit { })
        ),
        exit(::TwoExit, TwoWidth),
    )
)

internal val EmptyThree = KeyframeAnimation(
    Empty(ThreeWidth),
    FormCharacter::ease,
    listOf(
        enter(::ThreeEnter, ThreeWidth),
        KeyframeTransition(
            Keyframe(0.5f, ThreeEnter { }),
            Keyframe(1f, Three)
        )
    )
)
internal val ThreeEmpty = KeyframeAnimation(
    Three,
    FormCharacter::ease,
    listOf(
        KeyframeTransition(
            Keyframe(0f, Three { }),
            Keyframe(0.5f, ThreeExit { })
        ),
        exit(::ThreeExit, ThreeWidth),
    )
)
internal val EmptyFour = KeyframeAnimation(
    Empty(FourWidth),
    FormCharacter::ease,
    listOf(
        enter(::FourEnter, FourWidth),
        KeyframeTransition(
            Keyframe(0.5f, FourEnter { }),
            Keyframe(1f, Four)
        )
    )
)
internal val FourEmpty = KeyframeAnimation(
    Four,
    FormCharacter::ease,
    listOf(
        KeyframeTransition(
            Keyframe(0f, Four { }),
            Keyframe(0.5f, FourExit { })
        ),
        exit(::FourExit, FourWidth),
    )
)
internal val EmptyFive = KeyframeAnimation(
    Empty(FiveWidth),
    FormCharacter::ease,
    listOf(
        enter(::FiveEnter),
        KeyframeTransition(
            Keyframe(0.5f, FiveEnter { }),
            Keyframe(1f, Five)
        )
    )
)
internal val FiveEmpty = KeyframeAnimation(
    Five,
    FormCharacter::ease,
    listOf(
        KeyframeTransition(
            Keyframe(0f, Five { }),
            Keyframe(0.5f, FiveExit { })
        ),
        exit(::FiveExit),
    )
)

internal val EmptySix = KeyframeAnimation(
    Empty(SixWidth),
    FormCharacter::ease,
    listOf(
        enterVertical(::SixEnter, 0f, 0f),
        KeyframeTransition(
            Keyframe(0.5f, SixEnter { }),
            Keyframe(1f, Six)
        )
    )
)
internal val SixEmpty = PathAnimation { path, progress, render ->
    val d1 = FormCharacter.easeProgress(progress, 0f, 0.5f)
    val d2 = d1.progressIn(0.5f, 1f)
    val d3 = FormCharacter.easeProgress(progress, 0.5f, 1f)

    val halfWidth = SixWidth / 2f
    val halfHeight = FormCharacter.Height / 2f
    if (d3 == 0f) {
        path.beginPath()
        path.sector(
            halfWidth,
            halfHeight,
            halfWidth,
            d1.lerp(0f, -180f).degrees,
            d2.lerp(180f, 0f).degrees
        )
        render(FormCharacter.White)
    }

    path.beginPath()
    path.tetragon(
        d1.lerp(36f, 0f), 0f,
        d1.lerp(108f, d3.lerp(halfWidth, 0f)), 0f,
        d3.lerp(halfWidth, 0f), halfHeight,
        0f, halfHeight
    )
    render(FormCharacter.Yellow)
}
internal val EmptySeven = KeyframeAnimation(
    Empty(SevenWidth),
    FormCharacter::ease,
    listOf(
        enter(::SevenEnter),
        KeyframeTransition(
            Keyframe(0.5f, SevenEnter { }),
            Keyframe(1f, Seven)
        )
    )
)
internal val SevenEmpty = KeyframeAnimation(
    Seven,
    FormCharacter::ease,
    listOf(
        KeyframeTransition(
            Keyframe(0f, Seven { }),
            Keyframe(0.5f, SevenExit { })
        ),
        exit(::SevenExit),
    )
)
internal val EmptyEight = KeyframeAnimation(
    Empty(EightWidth),
    FormCharacter::ease,
    listOf(
        enter(::EightEnter, EightWidth / 2f),
        KeyframeTransition(
            Keyframe(0.5f, EightEnter { }),
            Keyframe(1f, Eight)
        )
    )
)
internal val EightEmpty = KeyframeAnimation(
    Eight,
    FormCharacter::ease,
    listOf(
        KeyframeTransition(
            Keyframe(0f, Eight { }),
            Keyframe(0.5f, EightExit { })
        ),
        exit(::EightExit, EightWidth / 2f),
    )
)

internal val EmptyNine = KeyframeAnimation(
    Empty(NineWidth),
    FormCharacter::ease,
    listOf(
        enterVertical(::NineEnter, NineWidth),
        KeyframeTransition(
            Keyframe(0.5f, NineEnter { }),
            Keyframe(1f, Nine)
        )
    )
)
internal val NineEmpty = PathAnimation { path, progress, render ->
    val d1 = FormCharacter.easeProgress(progress, 0f, 0.5f)
    val d2 = d1.progressIn(0.5f, 1f)
    val d3 = FormCharacter.easeProgress(progress, 0.5f, 1f)

    val width = NineWidth
    val halfWidth = NineWidth / 2f
    val height = FormCharacter.Height
    val halfHeight = FormCharacter.Height / 2f
    if (d3 == 0f) {
        path.beginPath()
        path.sector(
            halfWidth,
            halfHeight,
            halfWidth,
            d1.lerp(180f, 0f).degrees,
            d2.lerp(180f, 0f).degrees
        )
        render(FormCharacter.White)
    }

    path.beginPath()
    path.tetragon(
        d1.lerp(108f, width), height,
        d1.lerp(36f, d3.lerp(halfWidth, width)), height,
        d3.lerp(halfWidth, width), halfHeight,
        width, halfHeight
    )
    render(FormCharacter.Yellow)
}

internal val EmptySeparator = KeyframeAnimation(
    Empty(SeparatorWidth),
    FormCharacter::ease,
    listOf(
        enter(::SeparatorEnter, SeparatorWidth / 2f, 120f),
        KeyframeTransition(
            Keyframe(0.5f, SeparatorEnter { }),
            Keyframe(1f, Separator)
        )
    )
)
internal val SeparatorEmpty = KeyframeAnimation(
    Separator,
    FormCharacter::ease,
    listOf(
        KeyframeTransition(
            Keyframe(0f, Separator { }),
            Keyframe(0.5f, SeparatorExit { })
        ),
        exit(::SeparatorExit, SeparatorWidth / 2f, 120f),
    )
)
