package org.beatonma.gclocks.core.layout

import org.beatonma.gclocks.core.fixtures.TestLayoutOptions
import org.beatonma.gclocks.core.fixtures.TestOptions
import org.beatonma.gclocks.core.fixtures.getTestLayout
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test
import org.beatonma.gclocks.core.options.Layout as LayoutOption

private val Unbound = MeasureConstraints.Infinity

data class LayoutConfig(
    val layout: LayoutOption,
    val nativeWidth: Float,
    val nativeHeight: Float,
    val expectedCenterHorizontalX: Float, // Expected x position when centered in a container twice its nativeWidth
    val expectedCenterVerticalY: Float, // Expected y position when centered in a container twice its nativeHeight
    val expectedRowsWithSeconds: Int, // How many rows there should be in this layout when seconds are included
    val expectedRowsWithoutSeconds: Int, // How many rows there should be when seconds are omitted.
    val expectedEndHorizontalX: Float = nativeWidth, // Expected x position when aligned to end of a container twice its nativeWidth
    val expectedEndVerticalY: Float = nativeHeight, // Expected y position when aligned to end of a container twice its nativeHeight
)

private val HorizontalLayoutConfig = LayoutConfig(
    LayoutOption.Horizontal,
    nativeWidth = 500f,
    nativeHeight = 100f,
    expectedRowsWithSeconds = 1,
    expectedRowsWithoutSeconds = 1,
    expectedCenterHorizontalX = 250f,
    expectedCenterVerticalY = 50f,
)
private val VerticalLayoutConfig = LayoutConfig(
    LayoutOption.Vertical,
    nativeWidth = 200f,
    nativeHeight = 250f,
    expectedRowsWithSeconds = 3,
    expectedRowsWithoutSeconds = 2,
    expectedCenterHorizontalX = 100f,
    expectedCenterVerticalY = 125f,
)
private val WrappedLayoutConfig = LayoutConfig(
    LayoutOption.Wrapped,
    nativeWidth = 400f,
    nativeHeight = 150f,
    expectedRowsWithSeconds = 2,
    expectedRowsWithoutSeconds = 1,
    expectedCenterHorizontalX = 200f,
    expectedCenterVerticalY = 75f,
)

class HorizontalLayoutMeasureTest : LayoutMeasureTest(HorizontalLayoutConfig)
class VerticalLayoutMeasureTest : LayoutMeasureTest(VerticalLayoutConfig)
class WrappedLayoutMeasureTest : LayoutMeasureTest(WrappedLayoutConfig)


abstract class LayoutMeasureTest(val config: LayoutConfig) {
    private fun withLayout(
        horizontalAlignment: HorizontalAlignment,
        verticalAlignment: VerticalAlignment,
        format: TimeFormat = TimeFormat.HH_MM_SS_24,
        block: ClockLayout<*, *>.() -> Unit,
    ) {
        val layout = getTestLayout(
            TestOptions(
                layout = TestLayoutOptions(
                    layout = config.layout,
                    horizontalAlignment = horizontalAlignment,
                    verticalAlignment = verticalAlignment,
                    format = format,
                    spacingPx = 0,
                    secondsGlyphScale = 0.5f,
                )
            ),
        ).apply {
            update(TimeOfDay(12, 0, 0))
        }

        with(layout, block)
    }

    @Test
    fun `MeasureScope values are correct`() {
        forEachAlignment { horizontalAlignment, verticalAlignment ->
            withLayout(horizontalAlignment, verticalAlignment, format = TimeFormat.HH_MM_SS_24) {
                setConstraints(MeasureConstraints(config.nativeWidth, config.nativeHeight))
                measureFrame { _, _, _ ->
                    this.rowSizes.size shouldbe config.expectedRowsWithSeconds
                }
            }
            withLayout(horizontalAlignment, verticalAlignment, format = TimeFormat.HH_MM_24) {
                setConstraints(MeasureConstraints(config.nativeWidth, config.nativeHeight))
                measureFrame { _, _, _ ->
                    this.rowSizes.size shouldbe config.expectedRowsWithoutSeconds
                }
            }
        }
    }

    @Test
    fun `measureFrame scale is correct`() {
        forEachAlignment { horizontalAlignment, verticalAlignment ->
            withLayout(horizontalAlignment, verticalAlignment) {
                setConstraints(MeasureConstraints(config.nativeWidth, Unbound))
                measureFrame { _, _, scale ->
                    scale shouldbe 1f
                }

                setConstraints(MeasureConstraints(config.nativeWidth * 2f, Unbound))
                measureFrame { _, _, scale ->
                    scale shouldbe 2f
                }

                setConstraints(MeasureConstraints(config.nativeWidth * 0.5f, Unbound))
                measureFrame { _, _, scale ->
                    scale shouldbe 0.5f
                }
            }

            withLayout(horizontalAlignment, verticalAlignment) {
                setConstraints(MeasureConstraints(Unbound, config.nativeHeight))
                measureFrame { _, _, scale ->
                    scale shouldbe 1f
                }
            }
        }
    }

    @Test
    fun `measureFrame horizontal alignment is correct`() {
        val constraints = MeasureConstraints(config.nativeWidth * 2f, config.nativeHeight)
        forEachVerticalAlignment { verticalAlignment ->
            withLayout(HorizontalAlignment.Start, verticalAlignment) {
                setConstraints(constraints)
                measureFrame { translationX, _, _ ->
                    translationX shouldbe 0f
                }
            }
            withLayout(HorizontalAlignment.Center, verticalAlignment) {
                setConstraints(constraints)
                measureFrame { translationX, _, _ ->
                    translationX shouldbe config.expectedCenterHorizontalX
                }
            }
            withLayout(HorizontalAlignment.End, verticalAlignment) {
                setConstraints(constraints)
                measureFrame { translationX, _, _ ->
                    translationX shouldbe config.expectedEndHorizontalX
                }
            }
        }
    }

    @Test
    fun `measureFrame vertical alignment is correct`() {
        val constraints = MeasureConstraints(config.nativeWidth, config.nativeHeight * 2f)
        forEachHorizontalAlignment { horizontalAlignment ->
            withLayout(horizontalAlignment, VerticalAlignment.Top) {
                setConstraints(constraints)
                measureFrame { _, translationY, _ ->
                    translationY shouldbe 0f
                }
            }
            withLayout(horizontalAlignment, VerticalAlignment.Center) {
                setConstraints(constraints)
                measureFrame { _, translationY, _ ->
                    translationY shouldbe config.expectedCenterVerticalY
                }
            }
            withLayout(horizontalAlignment, VerticalAlignment.Bottom) {
                setConstraints(constraints)
                measureFrame { _, translationY, _ ->
                    translationY shouldbe config.expectedEndVerticalY
                }
            }
        }
    }
}


private inline fun forEachHorizontalAlignment(block: (HorizontalAlignment) -> Unit) {
    HorizontalAlignment.entries.forEach(block)
}

private inline fun forEachVerticalAlignment(block: (VerticalAlignment) -> Unit) {
    VerticalAlignment.entries.forEach(block)
}

private inline fun forEachAlignment(block: (HorizontalAlignment, VerticalAlignment) -> Unit) {
    forEachHorizontalAlignment { horizontal ->
        forEachVerticalAlignment { vertical ->
            block(
                horizontal,
                vertical
            )
        }
    }
}
