package org.beatonma.gclocks.core.layout

import org.beatonma.gclocks.core.fixtures.TestLayoutOptions
import org.beatonma.gclocks.core.fixtures.TestOptions
import org.beatonma.gclocks.core.fixtures.getTestLayout
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.core.options.Layout as LayoutOption
import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test


class LayoutTest {
    private val NativeWidth = 500f
    private val NativeHeight = 100f

    /* Height that is large enough to ensure that width will determine the scale */
    private val UnboundHeight = MeasureConstraints.Infinity

    private fun getLayout(
        horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Start,
        verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
    ) = getTestLayout(
        TestOptions(
            layout = TestLayoutOptions(
                layout = LayoutOption.Horizontal,
                horizontalAlignment = horizontalAlignment,
                verticalAlignment = verticalAlignment,
                format = TimeFormat.HH_MM_SS_24,
                spacingPx = 0,
            )
        ),
    ).apply {
        update(TimeOfDay(12, 0, 0))
    }

    @Test
    fun `measureFrame scale is correct`() {
        with(
            getLayout(HorizontalAlignment.Start)
        ) {
            setConstraints(MeasureConstraints(NativeWidth, UnboundHeight))
            measureFrame { _, _, scale ->
                scale shouldbe 1f
            }

            setConstraints(MeasureConstraints(NativeWidth * 2f, UnboundHeight))
            measureFrame { _, _, scale ->
                scale shouldbe 2f
            }

            setConstraints(MeasureConstraints(NativeWidth * 0.5f, UnboundHeight))
            measureFrame { _, _, scale ->
                scale shouldbe 0.5f
            }
        }

        with(
            getLayout(HorizontalAlignment.Start)
        ) {
            setConstraints(MeasureConstraints(NativeWidth * 2f, NativeHeight))
            measureFrame { _, _, scale ->
                scale shouldbe 1f
            }
        }
    }

    @Test
    fun `measureFrame horizontal alignment is correct`() {
        with(
            getLayout(HorizontalAlignment.Start)
        ) {
            setConstraints(MeasureConstraints(NativeWidth * 2f, NativeHeight))
            measureFrame { translationX, _, _ ->
                translationX shouldbe 0f
            }
        }

        with(
            getLayout(HorizontalAlignment.Center)
        ) {
            setConstraints(MeasureConstraints(NativeWidth * 2f, NativeHeight))
            measureFrame { translationX, _, _ ->
                translationX shouldbe 250f
            }
        }

        with(
            getLayout(HorizontalAlignment.End)
        ) {
            setConstraints(MeasureConstraints(NativeWidth * 2f, NativeHeight))
            measureFrame { translationX, _, _ ->
                translationX shouldbe 500f
            }
        }
    }

    @Test
    fun `measureFrame vertical alignment is correct`() {
        with(
            getLayout(verticalAlignment = VerticalAlignment.Top)
        ) {
            setConstraints(MeasureConstraints(NativeWidth, UnboundHeight))
            measureFrame { _, translationY, _ ->
                translationY shouldbe 0f
            }
        }

        with(
            getLayout(verticalAlignment = VerticalAlignment.Center)
        ) {
            setConstraints(MeasureConstraints(NativeWidth, 1000f))
            measureFrame { _, translationY, _ ->
                translationY shouldbe 450f
            }
        }

        with(
            getLayout(verticalAlignment = VerticalAlignment.Bottom)
        ) {
            setConstraints(MeasureConstraints(NativeWidth, 1000f)) /* 1x scale */
            measureFrame { _, translationY, _ ->
                translationY shouldbe 900f
            }
        }
        with(
            getLayout(verticalAlignment = VerticalAlignment.Bottom)
        ) {
            setConstraints(MeasureConstraints(NativeWidth * 2f, 1000f)) /* 2x scale */
            measureFrame { _, translationY, scale ->
                scale shouldbe 2f
                translationY shouldbe 800f
            }
        }
    }
}