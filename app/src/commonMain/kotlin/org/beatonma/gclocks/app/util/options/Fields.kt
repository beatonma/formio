package org.beatonma.gclocks.app.util.options

import org.beatonma.gclocks.compose.components.settings.Setting
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.TimeFormat


internal fun createColorsAdapter(
    paints: Paints,
    onValueChange: (value: List<Color>) -> Unit,
) = Setting.Colors(
    name = "Colors",
    helpText = null,
    value = paints.colors.toList(),
    onValueChange = onValueChange,
)
//): SettingsGroup = SettingsGroup(
//    name = "Colors",
//    settings = paints.colors.mapIndexed { index, color ->
//        Setting.Color(
//            name = "color$index",
//            helpText = null,
//            value = color,
//            onValueChange = { newValue -> onValueChange(index, newValue) },
//        )
//    }
//)


internal fun chooseLayout(value: Layout, onUpdate: (Layout) -> Unit) =
    Setting.SingleSelect(
        name = "Layout",
        helpText = null,
        value = value,
        values = Layout.entries,
        onValueChange = onUpdate
    )

internal fun chooseHorizontalAlignment(
    value: HorizontalAlignment,
    onUpdate: (HorizontalAlignment) -> Unit,
) =
    Setting.SingleSelect(
        name = "Horizontal alignment",
        value = value,
        values = HorizontalAlignment.entries,
        onValueChange = onUpdate
    )

internal fun chooseVerticalAlignment(
    value: VerticalAlignment,
    onUpdate: (VerticalAlignment) -> Unit,
) =
    Setting.SingleSelect(
        name = "Vertical alignment",
        value = value,
        values = VerticalAlignment.entries,
        onValueChange = onUpdate
    )


internal fun chooseTimeFormat(value: TimeFormat, onUpdate: (TimeFormat) -> Unit) =
    Setting.SingleSelect(
        name = "Time format",
        value = value,
        values = TimeFormat.entries,
        onValueChange = onUpdate
    )


internal fun chooseSpacing(value: Int, onUpdate: (Int) -> Unit, default: Int, max: Int) =
    Setting.Int(
        name = "Spacing",
        value = value,
        default = default,
        min = 0,
        max = max,
        onValueChange = onUpdate
    )


internal fun chooseSecondScale(value: Float, onUpdate: (Float) -> Unit) =
    Setting.Float(
        name = "Second scale",
        value = value,
        default = 0.5f,
        min = 0.2f,
        max = 1f,
        step = 0.1f,
        onValueChange = onUpdate
    )