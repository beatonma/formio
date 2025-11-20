package org.beatonma.gclocks.form.characters

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.graphics.paths.PathAnimation
import org.beatonma.gclocks.core.graphics.paths.PathImage
import org.beatonma.gclocks.core.graphics.paths.PathTransformation
import org.beatonma.gclocks.core.graphics.paths.StyleId
import org.beatonma.gclocks.core.util.decelerate5
import org.beatonma.gclocks.core.util.lerp
import org.beatonma.gclocks.core.util.progressIn
import org.beatonma.gclocks.form.characters.canonical.Eight
import org.beatonma.gclocks.form.characters.canonical.Five
import org.beatonma.gclocks.form.characters.canonical.Four
import org.beatonma.gclocks.form.characters.canonical.Nine
import org.beatonma.gclocks.form.characters.canonical.One
import org.beatonma.gclocks.form.characters.canonical.Separator
import org.beatonma.gclocks.form.characters.canonical.Seven
import org.beatonma.gclocks.form.characters.canonical.Six
import org.beatonma.gclocks.form.characters.canonical.Three
import org.beatonma.gclocks.form.characters.canonical.Two
import org.beatonma.gclocks.form.characters.canonical.Zero
import org.beatonma.gclocks.form.characters.transitional.EightEmpty
import org.beatonma.gclocks.form.characters.transitional.EightNine
import org.beatonma.gclocks.form.characters.transitional.EmptyEight
import org.beatonma.gclocks.form.characters.transitional.EmptyFive
import org.beatonma.gclocks.form.characters.transitional.EmptyFour
import org.beatonma.gclocks.form.characters.transitional.EmptyNine
import org.beatonma.gclocks.form.characters.transitional.EmptyOne
import org.beatonma.gclocks.form.characters.transitional.EmptySeparator
import org.beatonma.gclocks.form.characters.transitional.EmptySeven
import org.beatonma.gclocks.form.characters.transitional.EmptySix
import org.beatonma.gclocks.form.characters.transitional.EmptyThree
import org.beatonma.gclocks.form.characters.transitional.EmptyTwo
import org.beatonma.gclocks.form.characters.transitional.EmptyZero
import org.beatonma.gclocks.form.characters.transitional.FiveEmpty
import org.beatonma.gclocks.form.characters.transitional.FiveSix
import org.beatonma.gclocks.form.characters.transitional.FiveZero
import org.beatonma.gclocks.form.characters.transitional.FourEmpty
import org.beatonma.gclocks.form.characters.transitional.FourFive
import org.beatonma.gclocks.form.characters.transitional.NineEmpty
import org.beatonma.gclocks.form.characters.transitional.NineZero
import org.beatonma.gclocks.form.characters.transitional.OneEmpty
import org.beatonma.gclocks.form.characters.transitional.OneTwo
import org.beatonma.gclocks.form.characters.transitional.OneZero
import org.beatonma.gclocks.form.characters.transitional.SeparatorEmpty
import org.beatonma.gclocks.form.characters.transitional.SevenEight
import org.beatonma.gclocks.form.characters.transitional.SevenEmpty
import org.beatonma.gclocks.form.characters.transitional.SixEmpty
import org.beatonma.gclocks.form.characters.transitional.SixSeven
import org.beatonma.gclocks.form.characters.transitional.ThreeEmpty
import org.beatonma.gclocks.form.characters.transitional.ThreeFour
import org.beatonma.gclocks.form.characters.transitional.ThreeZero
import org.beatonma.gclocks.form.characters.transitional.TwoEmpty
import org.beatonma.gclocks.form.characters.transitional.TwoOne
import org.beatonma.gclocks.form.characters.transitional.TwoThree
import org.beatonma.gclocks.form.characters.transitional.TwoZero
import org.beatonma.gclocks.form.characters.transitional.ZeroEmpty
import org.beatonma.gclocks.form.characters.transitional.ZeroOne


object FormCharacter {
    internal const val Height = 144f

    internal const val Orange: StyleId = 0
    internal const val Yellow: StyleId = 1
    internal const val White: StyleId = 2

    val Canonical
        get() = listOf(
            Zero, One, Two, Three, Four, Five, Six, Seven, Eight, Nine, Separator,
        )

    val Transitional: List<PathAnimation>
        get() = listOf(
            ZeroOne,
            OneTwo,
            TwoThree,
            ThreeFour,
            FourFive,
            FiveSix,
            SixSeven,
            SevenEight,
            EightNine,
            NineZero,

            OneZero,
            TwoZero,
            ThreeZero,
            FiveZero,
            TwoOne,

            EmptyZero,
            EmptyOne,
            EmptyTwo,
            EmptyThree,
            EmptyFour,
            EmptyFive,
            EmptySix,
            EmptySeven,
            EmptyEight,
            EmptyNine,
            EmptySeparator,

            ZeroEmpty,
            OneEmpty,
            TwoEmpty,
            ThreeEmpty,
            FourEmpty,
            FiveEmpty,
            SixEmpty,
            SevenEmpty,
            EightEmpty,
            NineEmpty,
            SeparatorEmpty,
        )

    internal inline fun keyframe(width: Float, block: PathImage.Builder.() -> Unit) =
        PathImage(width, Height, block)

    internal fun ease(f: Float) = decelerate5(f)
    internal fun easeProgress(value: Float, min: Float, max: Float) =
        ease(value.progressIn(min, max))

    internal inline fun PathImage.Builder.pill(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        styleLeft: StyleId,
        styleRight: StyleId,
        split: Float,
        transformation: PathTransformation = {}
    ) {
        val height = bottom - top
        val diameter = height
        val radius = diameter / 2f

        // width of stretchy middle bit
        val stretchX = right - left - diameter
        val splitX = left + radius + split.lerp(0f, stretchX)

        path(styleLeft, transformation) {
            moveTo(left + radius, top)
            lineTo(splitX, top)
            lineTo(splitX, bottom)
            lineTo(left + radius, bottom)
            arcTo(
                left,
                top,
                left + diameter,
                bottom,
                Angle.Ninety,
                Angle.OneEighty,
                forceMoveTo = false
            )
        }

        path(styleRight, transformation) {
            moveTo(right - radius, bottom)
            lineTo(splitX, bottom)
            lineTo(splitX, top)
            lineTo(right - radius, top)
            arcTo(
                right - diameter,
                top,
                right,
                bottom,
                Angle.TwoSeventy,
                Angle.OneEighty,
                forceMoveTo = false
            )
        }
    }
}
