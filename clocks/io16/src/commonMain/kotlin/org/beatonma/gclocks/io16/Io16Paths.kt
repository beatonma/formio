import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.graphics.PathDefinition


private const val Io16Height = 118f


internal enum class Io16GlyphPath {
    ZeroOne {
        override val canonical = Zero_Canonical
        override val start = ZeroOne_Zero
        override val end = ZeroOne_One
    },
    OneTwo {
        override val canonical = One_Canonical
        override val start = OneTwo_One
        override val end = OneTwo_Two
    },
    TwoThree {
        override val canonical = Two_Canonical
        override val start = TwoThree_Two
        override val end = TwoThree_Three
    },
    ThreeFour {
        override val canonical = Three_Canonical
        override val start = ThreeFour_Three
        override val end = ThreeFour_Four
    },
    FourFive {
        override val canonical = Four_Canonical
        override val start = FourFive_Four
        override val end = FourFive_Five
    },
    FiveSix {
        override val canonical = Five_Canonical
        override val start = FiveSix_Five
        override val end = FiveSix_Six
    },
    SixSeven {
        override val canonical = Six_Canonical
        override val start = SixSeven_Six
        override val end = SixSeven_Seven
    },
    SevenEight {
        override val canonical = Seven_Canonical
        override val start = SevenEight_Seven
        override val end = SevenEight_Eight
    },
    EightNine {
        override val canonical = Eight_Canonical
        override val start = EightNine_Eight
        override val end = EightNine_Nine
    },
    OneZero {
        // 12 -> 01
        override val canonical = One_Canonical
        override val start = ZeroOne_One
        override val end = ZeroOne_Zero
    },
    TwoZero {
        // 23 -> 00
        override val canonical = Two_Canonical
        override val start = TwoZero_Two
        override val end = TwoZero_Zero
    },
    ThreeZero {
        // 23 -> 00
        override val canonical = Three_Canonical
        override val start = ThreeZero_Three
        override val end = ThreeZero_Zero
    },
    FiveZero {
        // 59 -> 00
        override val canonical = Five_Canonical
        override val start = FiveZero_Five
        override val end = FiveZero_Zero
    },
    NineZero {
        // 59 -> 00
        override val canonical = Nine_Canonical
        override val start = NineZero_Nine
        override val end = NineZero_Zero
    },
    TwoOne {
        // 12 -> 01
        override val canonical = Two_Canonical
        override val start = OneTwo_Two
        override val end = OneTwo_One
    },
    Zero {
        override val canonical = Zero_Canonical
    },
    One {
        override val canonical = One_Canonical
    },
    Two {
        override val canonical = Two_Canonical
    },
    Three {
        override val canonical = Three_Canonical
    },
    Four {
        override val canonical = Four_Canonical
    },
    Five {
        override val canonical = Five_Canonical
    },
    Six {
        override val canonical = Six_Canonical
    },
    Seven {
        override val canonical = Seven_Canonical
    },
    Eight {
        override val canonical = Eight_Canonical
    },
    Nine {
        override val canonical = Nine_Canonical
    },
    Separator {
        override val canonical = PathDefinition(16f, Io16Height) {
            beginPath()
            circle(8f, 48f, 8f, Path.Direction.AntiClockwise)
            beginPath()
            circle(8f, 96f, 8f, Path.Direction.AntiClockwise)
            beginPath()
        }
    }
    ;

    abstract val canonical: PathDefinition
    open val start: PathDefinition? = null
    open val end: PathDefinition? = null

    fun plot(canvas: Canvas, glyphProgress: Float, render: (() -> Unit)? = null) {
        if (start == null || end == null || glyphProgress == 0f) {
            canonical.plot(canvas, render)
        } else {
            start.plotInterpolated(canvas, end, glyphProgress, render)
        }
    }

    companion object {
        init {
            // Check that all entries with [start] and [end] states are able to interpolate correctly.
            Io16GlyphPath.entries.forEach { glyphPath ->
                if (glyphPath.start != null && glyphPath.end != null) {
                    check(glyphPath.start.commands.size == glyphPath.end.commands.size) {
                        "${glyphPath.name}: Invalid transition: Command length ${glyphPath.start.commands.size} != ${glyphPath.end.commands.size}"
                    }
                    glyphPath.start.commands.zip(glyphPath.end.commands).map { (a, b) ->
                        check(a::class == b::class) {
                            "${glyphPath.name}: Incompatible commands: ${a::class} != ${b::class}"
                        }
                    }
                } else if ((glyphPath.start == null || glyphPath.end == null) && (glyphPath.start != glyphPath.end)) {
                    throw IllegalStateException("${glyphPath.name}: [start], [end] must either both be null, or both be defined.")
                }
            }
        }
    }
}

/*
 * Canonical (non-animated) glyphs
 */
private val Zero_Canonical = PathDefinition(118f, Io16Height) {
    boundedArc(0f, 0f, 118f, 118f, startAngle = Angle.OneEighty, sweepAngle = Angle.ThreeSixty)
}
private val One_Canonical = PathDefinition(35.4f, Io16Height) {
    // M 0,118 H 35.4 V 0 H 0 Z
    moveTo(0f, 118f)
    lineTo(35.4f, 118f)
    lineTo(35.4f, 0f)
    lineTo(0f, 0f)
    closePath()
}
private val Two_Canonical = PathDefinition(80f, Io16Height) {
    // M 67,98 H 0 C 26,79 40,67 52,55 C 89,11 23,-24 1,21 L 17,38 C 39,-7.07 105,29 68,72 C 56,88 42,98 17,118 H 80 Z
    moveTo(67f, 98f)
    lineTo(0f, 98f)
    cubicTo(26f, 79f, 40f, 67f, 52f, 55f)
    cubicTo(89f, 11f, 23f, -24f, 1f, 21f)
    lineTo(17f, 38f)
    cubicTo(39f, -7.07f, 105f, 29f, 68f, 72f)
    cubicTo(56f, 88f, 42f, 98f, 17f, 118f)
    lineTo(80f, 118f)
    closePath()
}
private val Three_Canonical = PathDefinition(79.9f, Io16Height) {
    // M 0,77 C 15,116 57.1,103 63.1,77 C 65.1,66.9 63.1,55.9 49.1,46.9 C 56.1,39.9 60.1,34.8 59.1,23.6 C 58.1,-0.388 18,-12.4 2,18.6 L 18,36.8 C 34,4.64 74.1,16.6 75.1,40.9 C 76.1,51.9 72.1,56.9 65.1,63.9 C 79.2,72.9 81.2,84 79.2,94 C 73.1,119 31.1,132 16,94 Z
    moveTo(0f, 77f)
    cubicTo(15f, 116f, 57.1f, 103f, 63.1f, 77f)
    cubicTo(65.1f, 66.9f, 63.1f, 55.9f, 49.1f, 46.9f)
    cubicTo(56.1f, 39.9f, 60.1f, 34.8f, 59.1f, 23.6f)
    cubicTo(58.1f, -0.388f, 18f, -12.4f, 2f, 18.6f)
    lineTo(18f, 36.8f)
    cubicTo(34f, 4.64f, 74.1f, 16.6f, 75.1f, 40.9f)
    cubicTo(76.1f, 51.9f, 72.1f, 56.9f, 65.1f, 63.9f)
    cubicTo(79.2f, 72.9f, 81.2f, 84f, 79.2f, 94f)
    cubicTo(73.1f, 119f, 31.1f, 132f, 16f, 94f)
    closePath()
}
private val Four_Canonical = PathDefinition(90.9f, Io16Height) {
    // M 77.8,118 V 18.2 H 62.6 L 16.2,86.9 V 98 H 90.9 L 73.7,79.8 H 0 V 69.7 L 45.5,0 H 60.6 V 100 Z
    moveTo(77.8f, 118f)
    lineTo(77.8f, 18.2f)
    lineTo(62.6f, 18.2f)
    lineTo(16.2f, 86.9f)
    lineTo(16.2f, 98f)
    lineTo(90.9f, 98f)
    lineTo(73.7f, 79.8f)
    lineTo(0f, 79.8f)
    lineTo(0f, 69.7f)
    lineTo(45.5f, 0f)
    lineTo(60.6f, 0f)
    lineTo(60.6f, 100f)
    closePath()
}
private val Five_Canonical = PathDefinition(84.1f, Io16Height) {
    // M 0,74 C 15,118 60,100 66,77 C 73,52 45,15 4,49 C 4,49 10,0 10,0 C 10,0 61,0 61,0 C 61,0 78,17 78,17 C 78,17 78,17 78,17 C 78,17 27,17 27,17 C 27,17 27,17 27,17 C 27,17 20,67 20,67 C 62,33 90,69 83,94 C 77,117 32,135 17,91 Z
    moveTo(0f, 74f)
    cubicTo(15f, 118f, 60f, 100f, 66f, 77f)
    cubicTo(73f, 52f, 45f, 15f, 4f, 49f)
    cubicTo(4f, 49f, 10f, 0f, 10f, 0f)
    cubicTo(10f, 0f, 61f, 0f, 61f, 0f)
    cubicTo(61f, 0f, 78f, 17f, 78f, 17f)
    zeroCubic()
    cubicTo(78f, 17f, 27f, 17f, 27f, 17f)
    zeroCubic()
    cubicTo(27f, 17f, 20f, 67f, 20f, 67f)
    cubicTo(62f, 33f, 90f, 69f, 83f, 94f)
    cubicTo(77f, 117f, 32f, 135f, 17f, 91f)
    closePath()
}
private val Six_Canonical = PathDefinition(81.1f, Io16Height) {
    // M 25.6,37.6 C 74.1,25.8 82.1,107 25.6,100 C 16.6,98.1 -12.1,82.3 5.7,46.6 C 5.7,46.6 37.4,0 37.4,0 C 37.4,0 37.4,0 37.4,0 C 37.4,0 54.3,16.8 54.3,16.8 C 54.3,16.8 54.3,16.8 54.3,16.8 C 54.3,16.8 22.6,63.4 22.6,63.4 C 3.71,99.1 32.5,115 41.4,117 C 98,124 90,42.6 42.4,54.5 Z
    moveTo(25.6f, 37.6f)
    cubicTo(74.1f, 25.8f, 82.1f, 107f, 25.6f, 100f)
    cubicTo(16.6f, 98.1f, -12.1f, 82.3f, 5.7f, 46.6f)
    cubicTo(5.7f, 46.6f, 37.4f, 0f, 37.4f, 0f)
    zeroCubic()
    cubicTo(37.4f, 0f, 54.3f, 16.8f, 54.3f, 16.8f)
    zeroCubic()
    cubicTo(54.3f, 16.8f, 22.6f, 63.4f, 22.6f, 63.4f)
    cubicTo(3.71f, 99.1f, 32.5f, 115f, 41.4f, 117f)
    cubicTo(98f, 124f, 90f, 42.6f, 42.4f, 54.5f)
    closePath()
}
private val Seven_Canonical = PathDefinition(81.1f, Io16Height) {
    // M 32.6,118 L 81.1,31.6 V 17.3 H 17.3 L 0,0 H 64.3 V 14.3 L 15.3,101 Z
    moveTo(32.6f, 118f)
    lineTo(81.1f, 31.6f)
    lineTo(81.1f, 17.3f)
    lineTo(17.3f, 17.3f)
    lineTo(0f, 0f)
    lineTo(64.3f, 0f)
    lineTo(64.3f, 14.3f)
    lineTo(15.3f, 101f)
    closePath()
}
private val Eight_Canonical = PathDefinition(74.8f, Io16Height) {
    // M 18.1,56 C -6.93,68 -10.9,114 38.1,118 C 38.1,118 38.1,118 38.1,118 C 77.3,115 87.3,76 57.1,56 C 57.1,56 57.1,56 57.1,56 C 89.3,31 62.1,-0.977 37.1,0.023 C -3.93,4.02 -2.93,45 18.1,56 Z
    moveTo(18.1f, 56f)
    cubicTo(-6.93f, 68f, -10.9f, 114f, 38.1f, 118f)
    zeroCubic()
    cubicTo(77.3f, 115f, 87.3f, 76f, 57.1f, 56f)
    zeroCubic()
    cubicTo(89.3f, 31f, 62.1f, -0.977f, 37.1f, 0.023f)
    cubicTo(-3.93f, 4.02f, -2.93f, 45f, 18.1f, 56f)
    closePath()
}
private val Nine_Canonical = PathDefinition(82.4f, Io16Height) {
    // M 55.1,79.4 C 7.03,91.4 -0.97,11.4 56.1,18.4 C 65.2,20.4 95.2,35.4 76.2,71.4 L 43.1,118 L 27.1,100 L 59.1,54.4 C 78.2,18.4 49.1,2.43 39.1,0.43 C -16.9,-6.57 -8.97,74.4 39.1,62.4
    moveTo(55.1f, 79.4f)
    cubicTo(7.03f, 91.4f, -0.97f, 11.4f, 56.1f, 18.4f)
    cubicTo(65.2f, 20.4f, 95.2f, 35.4f, 76.2f, 71.4f)
    cubicTo(76.2f, 71.4f, 43.1f, 118f, 43.1f, 118f)
    cubicTo(43.1f, 118f, 27.1f, 100f, 27.1f, 100f)
    cubicTo(27.1f, 100f, 59.1f, 54.4f, 59.1f, 54.4f)
    cubicTo(78.2f, 18.4f, 49.1f, 2.43f, 39.1f, 0.43f)
    cubicTo(-16.9f, -6.57f, -8.97f, 74.4f, 39.1f, 62.4f)
    closePath()
}


/*
 * Animated glyphs
 *
 * Glyphs that are related should be named with StartEnd_Name format, where:
 * - Start is the starting glyph name,
 * - End is the ending glyph name,
 * - Name is either Start or End, depending on the part being defined.
 * e.g. Transition from zero to one is defined in two paths: `ZeroOne_Zero` and `ZeroOne_One`.
 *
 * All parts must share the same number and sequence of path commands so the
 * command values can be transitioned between via interpolation.
 */

private val ZeroOne_Zero = PathDefinition(Zero_Canonical.width, Io16Height) {
    // M 0,59 C 3.93,21.6 23.7,2.04 59.1,0 C 94,2.93 114,22.6 118,59 C 115,93.4 94,114 59.1,118 C 23.7,114 3.93,93.4 0,59 Z
    moveTo(0f, 59f)
    cubicTo(3.93f, 21.6f, 23.7f, 2.04f, 59.1f, 0f)
    cubicTo(94f, 2.93f, 114f, 22.6f, 118f, 59f)
    cubicTo(115f, 93.4f, 94f, 114f, 59.1f, 118f)
    cubicTo(23.7f, 114f, 3.93f, 93.4f, 0f, 59f)
    closePath()
}
private val ZeroOne_One = PathDefinition(One_Canonical.width, Io16Height) {
    // M 0,118 C 0,118 35.4,118 35.4,118 C 35.4,118 35.4,0 35.4,0 C 35.4,0 0,0 0,0 Z
    moveTo(0f, 118f)
    cubicTo(0f, 118f, 35.4f, 118f, 35.4f, 118f)
    cubicTo(35.4f, 118f, 35.4f, 0f, 35.4f, 0f)
    cubicTo(35.4f, 0f, 0f, 0f, 0f, 0f)
    cubicTo(0f, 0f, 0f, 118f, 0f, 118f)
    closePath()
}


private val OneTwo_One = PathDefinition(One_Canonical.width, Io16Height) {
    // M 0,118 H 17.7 C 17.7,118 35.4,118 35.4,118 C 35.4,118 35.4,47.2 35.4,47.2 V 0 C 35.4,0 17.7,0 17.7,0 C 17.7,0 0,0 0,0 Z
    moveTo(0f, 118f)
    lineTo(17.7f, 118f)
    cubicTo(17.7f, 118f, 35.4f, 118f, 35.4f, 118f)
    cubicTo(35.4f, 118f, 35.4f, 47.2f, 35.4f, 47.2f)
    lineTo(35.4f, 0f)
    cubicTo(35.4f, 0f, 17.7f, 0f, 17.7f, 0f)
    cubicTo(17.7f, 0f, 0f, 0f, 0f, 0f)
    lineTo(0f, 118f)
    closePath()
}
private val OneTwo_Two = PathDefinition(Two_Canonical.width, Io16Height) {
    // M 67,98 H 0 C 26,79 40,67 52,55 C 89,11 23,-24 1,21 L 17,38 C 39,-7.03 105,29 68,72 C 56,88 42,98 17,118 H 80 Z
    moveTo(67f, 98f)
    lineTo(0f, 98f)
    cubicTo(26f, 79f, 40f, 67f, 52f, 55f)
    cubicTo(89f, 11f, 23f, -24f, 1f, 21f)
    lineTo(17f, 38f)
    cubicTo(39f, -7.03f, 105f, 29f, 68f, 72f)
    cubicTo(56f, 88f, 42f, 98f, 17f, 118f)
    lineTo(80f, 118f)
    closePath()
}

private val TwoThree_Two = PathDefinition(Two_Canonical.width, Io16Height) {
    // M 67,98 C 67,98 33,98 34,98 C 34,98 0,98 0,98 C 26,79 40,67 52,55 C 89,11 23,-24 1,21 L 17,38 C 39,-7.01 105,29 68,72 C 56,88 42,98 17,118 C 17,118 48,118 48,118 C 48,118 80,118 80,118 Z
    moveTo(67f, 98f)
    cubicTo(67f, 98f, 33f, 98f, 34f, 98f)
    cubicTo(34f, 98f, 0f, 98f, 0f, 98f)
    cubicTo(26f, 79f, 40f, 67f, 52f, 55f)
    cubicTo(89f, 11f, 23f, -24f, 1f, 21f)
    lineTo(17f, 38f)
    cubicTo(39f, -7.01f, 105f, 29f, 68f, 72f)
    cubicTo(56f, 88f, 42f, 98f, 17f, 118f)
    cubicTo(17f, 118f, 48f, 118f, 48f, 118f)
    cubicTo(48f, 118f, 80f, 118f, 80f, 118f)
    closePath()
}
private val TwoThree_Three = PathDefinition(Three_Canonical.width, Io16Height) {
    // M 0,76.6 C 15,116 57,103 63,76.6 C 65,66.6 63,55.6 49,46.6 C 56,39.6 60,34.6 59,23.6 C 58,-0.43 18,-12.4 2,18.6 L 18,36.6 C 34,4.57 74,16.6 75,40.6 C 76,51.6 72,56.6 65,63.6 C 79,72.6 81,83.6 79,93.6 C 73,119 31,132 16,93.6 Z
    moveTo(0f, 76.6f)
    cubicTo(15f, 116f, 57f, 103f, 63f, 76.6f)
    cubicTo(65f, 66.6f, 63f, 55.6f, 49f, 46.6f)
    cubicTo(56f, 39.6f, 60f, 34.6f, 59f, 23.6f)
    cubicTo(58f, -0.43f, 18f, -12.4f, 2f, 18.6f)
    lineTo(18f, 36.6f)
    cubicTo(34f, 4.57f, 74f, 16.6f, 75f, 40.6f)
    cubicTo(76f, 51.6f, 72f, 56.6f, 65f, 63.6f)
    cubicTo(79f, 72.6f, 81f, 83.6f, 79f, 93.6f)
    cubicTo(73f, 119f, 31f, 132f, 16f, 93.6f)
    closePath()
}
private val TwoZero_Two = Two_Canonical
private val TwoZero_Zero = PathDefinition(Zero_Canonical.width, Io16Height) {
    moveTo(0f, 59f)
//    zeroLine(0f, 59f)
    zeroLine()
    cubicTo(3.93f, 21.6f, 23.7f, 2.04f, 59.1f, 0f)
    cubicTo(94f, 2.93f, 114f, 22.6f, 118f, 59f)
//    zeroLine(118f, 59f)
    zeroLine()
    cubicTo(115f, 93.4f, 94f, 114f, 59.1f, 118f)
    cubicTo(23.7f, 114f, 3.93f, 93.4f, 0f, 59f)
//    zeroLine(0f, 59f)
    zeroLine()
    closePath()
}

private val ThreeFour_Three = PathDefinition(Three_Canonical.width, Io16Height) {
    // M 0,76.6 C 15,116 57,103 63,76.6 C 65,66.6 63,55.6 49,46.6 C 56,39.6 60,34.6 59,23.6 C 58,-0.42 18,-12.4 2,18.6 C 2,18.6 2,18.6 2,18.6 C 2,18.6 18,36.6 18,36.6 C 18,36.6 18,36.6 18,36.6 C 34,4.58 74,16.6 75,40.6 C 76,51.6 72,56.6 65,63.6 C 79,72.6 81,83.6 79,93.6 C 73,119 31,132 16,93.6 Z
    moveTo(0f, 76.6f)
    cubicTo(15f, 116f, 57f, 103f, 63f, 76.6f)
    cubicTo(65f, 66.6f, 63f, 55.6f, 49f, 46.6f)
    cubicTo(56f, 39.6f, 60f, 34.6f, 59f, 23.6f)
    cubicTo(58f, -0.42f, 18f, -12.4f, 2f, 18.6f)
    zeroCubic()
    cubicTo(2f, 18.6f, 18f, 36.6f, 18f, 36.6f)
    zeroCubic()
    cubicTo(34f, 4.58f, 74f, 16.6f, 75f, 40.6f)
    cubicTo(76f, 51.6f, 72f, 56.6f, 65f, 63.6f)
    cubicTo(79f, 72.6f, 81f, 83.6f, 79f, 93.6f)
    cubicTo(73f, 119f, 31f, 132f, 16f, 93.6f)
    closePath()
}
private val ThreeFour_Four = PathDefinition(Four_Canonical.width, Io16Height) {
    // M 77.8,118 C 77.8,118 77.8,18.2 77.8,18.2 C 77.8,18.2 62.6,18.2 62.6,18.2 C 62.6,18.2 16.2,86.9 16.2,86.9 C 16.2,86.9 16.2,98 16.2,98 C 16.2,98 90.9,98 90.9,98 C 90.9,98 73.7,79.8 73.7,79.8 C 73.7,79.8 0,79.8 0,79.8 C 0,79.8 0,69.7 0,69.7 C 0,69.7 45.5,0 45.5,0 C 45.5,0 60.6,0 60.6,0 C 60.6,0 60.6,99.9 60.6,99.9 Z
    moveTo(77.8f, 118f)
    cubicTo(77.8f, 118f, 77.8f, 18.2f, 77.8f, 18.2f)
    cubicTo(77.8f, 18.2f, 62.6f, 18.2f, 62.6f, 18.2f)
    cubicTo(62.6f, 18.2f, 16.2f, 86.9f, 16.2f, 86.9f)
    cubicTo(16.2f, 86.9f, 16.2f, 98f, 16.2f, 98f)
    cubicTo(16.2f, 98f, 90.9f, 98f, 90.9f, 98f)
    cubicTo(90.9f, 98f, 73.7f, 79.8f, 73.7f, 79.8f)
    cubicTo(73.7f, 79.8f, 0f, 79.8f, 0f, 79.8f)
    cubicTo(0f, 79.8f, 0f, 69.7f, 0f, 69.7f)
    cubicTo(0f, 69.7f, 45.5f, 0f, 45.5f, 0f)
    cubicTo(45.5f, 0f, 60.6f, 0f, 60.6f, 0f)
    cubicTo(60.6f, 0f, 60.6f, 99.9f, 60.6f, 99.9f)
    closePath()
}
private val ThreeZero_Three = Three_Canonical
private val ThreeZero_Zero = PathDefinition(Zero_Canonical.width, Io16Height) {
    moveTo(0f, 59f)
    cubicTo(3.93f, 21.6f, 23.7f, 2.04f, 59.1f, 0f)
    zeroCubic()
    cubicTo(94f, 2.93f, 114f, 22.6f, 118f, 59f)
    zeroCubic()
    zeroLine()
    cubicTo(115f, 93.4f, 94f, 114f, 59.1f, 118f)
    zeroCubic()
    cubicTo(23.7f, 114f, 3.93f, 93.4f, 0f, 59f)
    zeroCubic()
    closePath()
}

private val FourFive_Four = PathDefinition(Four_Canonical.width, Io16Height) {
    // M 77.8,118 V 18.2 H 62.7 L 16.2,86.9 V 98 H 90.9 L 73.7,79.8 H 0 V 69.7 L 46,0 H 60.6 V 99.8 Z
    moveTo(77.8f, 118f)
    cubicTo(77.8f, 118f, 77.8f, 18.2f, 77.8f, 18.2f)
    cubicTo(77.8f, 18.2f, 62.7f, 18.2f, 62.7f, 18.2f)
    cubicTo(62.7f, 18.2f, 16.2f, 86.9f, 16.2f, 86.9f)
    cubicTo(16.2f, 86.9f, 16.2f, 98f, 16.2f, 98f)
    cubicTo(16.2f, 98f, 90.9f, 98f, 90.9f, 98f)
    cubicTo(90.9f, 98f, 73.7f, 79.8f, 73.7f, 79.8f)
    cubicTo(73.7f, 79.8f, 0f, 79.8f, 0f, 79.8f)
    cubicTo(0f, 79.8f, 0f, 69.7f, 0f, 69.7f)
    cubicTo(0f, 69.7f, 46f, 0f, 46f, 0f)
    cubicTo(46f, 0f, 60.6f, 0f, 60.6f, 0f)
    cubicTo(60.6f, 0f, 60.6f, 99.8f, 60.6f, 99.8f)
    closePath()
}
private val FourFive_Five = PathDefinition(Five_Canonical.width, Io16Height) {
    // M 0,74 C 15,118 60,100 66,77 C 73,52 45,15 4,49 C 4,49 10,0 10,0 C 10,0 61,0 61,0 C 61,0 78,17 78,17 C 78,17 78,17 78,17 C 78,17 27,17 27,17 C 27,17 27,17 27,17 C 27,17 20,67 20,67 C 62,33 90,69 83,94 C 77,117 32,135 17,91 Z
    moveTo(0f, 74f)
    cubicTo(15f, 118f, 60f, 100f, 66f, 77f)
    cubicTo(73f, 52f, 45f, 15f, 4f, 49f)
    cubicTo(4f, 49f, 10f, 0f, 10f, 0f)
    cubicTo(10f, 0f, 61f, 0f, 61f, 0f)
    cubicTo(61f, 0f, 78f, 17f, 78f, 17f)
    zeroCubic()
    cubicTo(78f, 17f, 27f, 17f, 27f, 17f)
    zeroCubic()
    cubicTo(27f, 17f, 20f, 67f, 20f, 67f)
    cubicTo(62f, 33f, 90f, 69f, 83f, 94f)
    cubicTo(77f, 117f, 32f, 135f, 17f, 91f)
    closePath()
}

private val FiveSix_Five = PathDefinition(Five_Canonical.width, Io16Height) {
    // M 0,74 C 15,118 60,99.7 66,77 C 73,52 45,15.1 4,49 L 10,0 H 61 L 78,17.1 H 27 L 20,67 C 62,33 90.1,69.1 83.1,93.8 C 76.8,117 32,135 17,91 Z
    moveTo(0f, 74f)
    cubicTo(15f, 118f, 60f, 99.7f, 66f, 77f)
    cubicTo(73f, 52f, 45f, 15.1f, 4f, 49f)
    cubicTo(4f, 49f, 10f, 0f, 10f, 0f)
    cubicTo(10f, 0f, 61f, 0f, 61f, 0f)
    cubicTo(61f, 0f, 78f, 17.1f, 78f, 17.1f)
    cubicTo(78f, 17.1f, 27f, 17.1f, 27f, 17.1f)
    cubicTo(27f, 17.1f, 20f, 67f, 20f, 67f)
    cubicTo(62f, 33f, 90.1f, 69.1f, 83.1f, 93.8f)
    cubicTo(76.8f, 117f, 32f, 135f, 17f, 91f)
    closePath()
}
private val FiveSix_Six = PathDefinition(Six_Canonical.width, Io16Height) {
    // M 25.6,37.7 C 74.1,25.8 82.1,107 25.6,100 C 16.6,98.3 -12.1,82.3 5.71,46.6 C 5.71,46.6 37.5,0 37.5,0 C 37.5,0 37.5,0 37.5,0 C 37.5,0 54.3,16.8 54.3,16.8 C 54.3,16.8 54.3,16.8 54.3,16.8 C 54.3,16.8 22.6,63.4 22.6,63.4 C 3.73,99.1 32.5,115 41.4,117 C 97.9,124 90,42.6 42.4,54.5 Z
    moveTo(25.6f, 37.7f)
    cubicTo(74.1f, 25.8f, 82.1f, 107f, 25.6f, 100f)
    cubicTo(16.6f, 98.3f, -12.1f, 82.3f, 5.71f, 46.6f)
    cubicTo(5.71f, 46.6f, 37.5f, 0f, 37.5f, 0f)
    zeroCubic()
    cubicTo(37.5f, 0f, 54.3f, 16.8f, 54.3f, 16.8f)
    zeroCubic()
    cubicTo(54.3f, 16.8f, 22.6f, 63.4f, 22.6f, 63.4f)
    cubicTo(3.73f, 99.1f, 32.5f, 115f, 41.4f, 117f)
    cubicTo(97.9f, 124f, 90f, 42.6f, 42.4f, 54.5f)
    closePath()
}
private val FiveZero_Five = Five_Canonical
private val FiveZero_Zero = PathDefinition(Zero_Canonical.width, Io16Height) {
    moveTo(0f, 59f)
    zeroCubic()
    zeroCubic()
    cubicTo(3.93f, 21.6f, 23.7f, 2.04f, 59.1f, 0f)
    zeroCubic()
    zeroCubic()
    cubicTo(94f, 2.93f, 114f, 22.6f, 118f, 59f)
    zeroCubic()
    zeroCubic()
    cubicTo(115f, 93.4f, 94f, 114f, 59.1f, 118f)
    zeroCubic()
    cubicTo(23.7f, 114f, 3.93f, 93.4f, 0f, 59f)
    closePath()
}

private val SixSeven_Six = PathDefinition(Six_Canonical.width, Io16Height) {
    // M 25.5,37.6 C 74.2,25.7 81.7,107 25.5,100 C 16.6,97.7 -12.1,82.2 5.73,46.5 C 5.73,46.5 37.4,0 37.4,0 C 37.4,0 54.3,16.8 54.3,16.8 C 54.3,16.8 22.6,63.4 22.6,63.4 C 3.75,98.7 32.5,115 41.4,117 C 97.5,124 89.6,42.6 42.4,54.5 Z
    moveTo(25.5f, 37.6f)
    cubicTo(74.2f, 25.7f, 81.7f, 107f, 25.5f, 100f)
    cubicTo(16.6f, 97.7f, -12.1f, 82.2f, 5.73f, 46.5f)
    cubicTo(5.73f, 46.5f, 37.4f, 0f, 37.4f, 0f)
    cubicTo(37.4f, 0f, 54.3f, 16.8f, 54.3f, 16.8f)
    cubicTo(54.3f, 16.8f, 22.6f, 63.4f, 22.6f, 63.4f)
    cubicTo(3.75f, 98.7f, 32.5f, 115f, 41.4f, 117f)
    cubicTo(97.5f, 124f, 89.6f, 42.6f, 42.4f, 54.5f)
    closePath()
}
private val SixSeven_Seven = PathDefinition(Seven_Canonical.width, Io16Height) {
    // M 32.6,118 L 81.1,31.6 V 17.3 H 17.3 L 0,0 H 64.4 L 64.3,14.3 L 15.3,101 Z
    moveTo(32.6f, 118f)
    cubicTo(32.6f, 118f, 81.1f, 31.6f, 81.1f, 31.6f)
    cubicTo(81.1f, 31.6f, 81.1f, 17.3f, 81.1f, 17.3f)
    cubicTo(81.1f, 17.3f, 17.3f, 17.3f, 17.3f, 17.3f)
    cubicTo(17.3f, 17.3f, 0f, 0f, 0f, 0f)
    cubicTo(0f, 0f, 64.4f, 0f, 64.4f, 0f)
    cubicTo(64.4f, 0f, 64.3f, 14.3f, 64.3f, 14.3f)
    cubicTo(64.3f, 14.3f, 15.3f, 101f, 15.3f, 101f)
    closePath()
}

private val SevenEight_Seven = PathDefinition(Seven_Canonical.width, Io16Height) {
    // M 32.6,118 L 81.1,31.6 V 17.3 H 17.3 L 0,0 H 64.3 V 14.3 L 15.3,101 Z
    moveTo(32.6f, 118f)
    cubicTo(32.6f, 118f, 81.1f, 31.6f, 81.1f, 31.6f)
    cubicTo(81.1f, 31.6f, 81.1f, 17.3f, 81.1f, 17.3f)
    cubicTo(81.1f, 17.3f, 17.3f, 17.3f, 17.3f, 17.3f)
    cubicTo(17.3f, 17.3f, 0f, 0f, 0f, 0f)
    cubicTo(0f, 0f, 64.3f, 0f, 64.3f, 0f)
    cubicTo(64.3f, 0f, 64.3f, 14.3f, 64.3f, 14.3f)
    cubicTo(64.3f, 14.3f, 15.3f, 101f, 15.3f, 101f)
    closePath()
}
private val SevenEight_Eight = PathDefinition(Eight_Canonical.width, Io16Height) {
    // M 18.1,56 C -6.95,68 -10.9,114 38.4,118 C 38.4,118 38.4,118 38.4,118 C 77.4,115 87.4,75.5 57.4,56 C 57.4,56 57.4,56 57.4,56 C 89.4,31 62.4,-0.98 37.4,0.02 C -3.95,4.02 -2.95,45 18.1,56 Z
    moveTo(18.1f, 56f)
    cubicTo(-6.95f, 68f, -10.9f, 114f, 38.4f, 118f)
    zeroCubic()
    cubicTo(77.4f, 115f, 87.4f, 75.5f, 57.4f, 56f)
    zeroCubic()
    cubicTo(89.4f, 31f, 62.4f, -0.98f, 37.4f, 0.02f)
    cubicTo(-3.95f, 4.02f, -2.95f, 45f, 18.1f, 56f)
    zeroCubic()
    closePath()
}

private val EightNine_Eight = PathDefinition(Eight_Canonical.width, Io16Height) {
    // M 18.1,56 C -6.92,68 -10.9,114 38.1,118 C 38.1,118 38.1,118 38.1,118 C 77.2,115 87.2,76 57.2,56 C 57.2,56 57.2,56 57.2,56 C 89.2,31 62.2,-0.98 37.1,0.02 C -3.92,4.02 -2.92,45 18.1,56 Z
    moveTo(18.1f, 56f)
    cubicTo(-6.92f, 68f, -10.9f, 114f, 38.1f, 118f)
    zeroCubic()
    cubicTo(77.2f, 115f, 87.2f, 76f, 57.2f, 56f)
    zeroCubic()
    cubicTo(89.2f, 31f, 62.2f, -0.98f, 37.1f, 0.02f)
    cubicTo(-3.92f, 4.02f, -2.92f, 45f, 18.1f, 56f)
    zeroCubic()
    closePath()
}
private val EightNine_Nine = PathDefinition(Nine_Canonical.width, Io16Height) {
    // M 55.2,79.4 C 7.06,91.4 -0.942,11.4 56.2,18.4 C 65.2,20.4 95.2,35.4 76.2,71.4 C 76.2,71.4 43.1,118 43.1,118 C 43.1,118 27.1,100 27.1,100 C 27.1,100 59.2,54.4 59.2,54.4 C 78.2,18.4 49.1,2.43 39.1,0.43 C -16.9,-6.6 -8.94,74.4 39.1,62.4 Z
    moveTo(55.2f, 79.4f)
    cubicTo(7.06f, 91.4f, -0.942f, 11.4f, 56.2f, 18.4f)
    cubicTo(65.2f, 20.4f, 95.2f, 35.4f, 76.2f, 71.4f)
    cubicTo(76.2f, 71.4f, 43.1f, 118f, 43.1f, 118f)
    cubicTo(43.1f, 118f, 27.1f, 100f, 27.1f, 100f)
    cubicTo(27.1f, 100f, 59.2f, 54.4f, 59.2f, 54.4f)
    cubicTo(78.2f, 18.4f, 49.1f, 2.43f, 39.1f, 0.43f)
    cubicTo(-16.9f, -6.6f, -8.94f, 74.4f, 39.1f, 62.4f)
    closePath()
}

private val NineZero_Nine = PathDefinition(Nine_Canonical.width, Io16Height) {
    // M 55.1,79.4 C 7.06,91.4 -0.942,11.4 56.1,18.4 C 65.1,20.4 95.1,35.4 76.1,71.4 C 76.1,71.4 43.1,118 43.1,118 C 43.1,118 27.1,100 27.1,100 C 27.1,100 59.1,54.4 59.1,54.4 C 78.1,18.4 49.1,2.43 39.1,0.43 C -16.9,-6.57 -8.94,74.4 39.1,62.4 Z
    moveTo(55.1f, 79.4f)
    cubicTo(7.06f, 91.4f, -0.942f, 11.4f, 56.1f, 18.4f)
    cubicTo(65.1f, 20.4f, 95.1f, 35.4f, 76.1f, 71.4f)
    cubicTo(76.1f, 71.4f, 43.1f, 118f, 43.1f, 118f)
    cubicTo(43.1f, 118f, 27.1f, 100f, 27.1f, 100f)
    cubicTo(27.1f, 100f, 59.1f, 54.4f, 59.1f, 54.4f)
    cubicTo(78.1f, 18.4f, 49.1f, 2.43f, 39.1f, 0.43f)
    cubicTo(-16.9f, -6.57f, -8.94f, 74.4f, 39.1f, 62.4f)
    closePath()
}
private val NineZero_Zero = PathDefinition(Zero_Canonical.width, Io16Height) {
    // M 0,59 C 3.93,21.6 23.6,1.97 59,0 C 59,0 59,0 59,0 C 59,0 59,0 59,0 C 59,0 59,0 59,0 C 94.4,2.95 114,22.6 118,59 C 115,93.4 94.4,114 59,118 C 23.6,114 3.93,93.4 0,59 Z
    moveTo(0f, 59f)
    cubicTo(3.93f, 21.6f, 23.6f, 1.97f, 59f, 0f)
    zeroCubic()
    zeroCubic()
    zeroCubic()
    cubicTo(94.4f, 2.95f, 114f, 22.6f, 118f, 59f)
    cubicTo(115f, 93.4f, 94.4f, 114f, 59f, 118f)
    cubicTo(23.6f, 114f, 3.93f, 93.4f, 0f, 59f)
    closePath()
}
