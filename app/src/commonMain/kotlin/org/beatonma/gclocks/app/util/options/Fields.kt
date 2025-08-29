package org.beatonma.gclocks.app.util.options

import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.setting_alignment_horizontal
import gclocks_multiplatform.app.generated.resources.setting_alignment_vertical
import gclocks_multiplatform.app.generated.resources.setting_clock_layout
import gclocks_multiplatform.app.generated.resources.setting_clock_spacing
import gclocks_multiplatform.app.generated.resources.setting_colors
import gclocks_multiplatform.app.generated.resources.setting_help_clock_spacing
import gclocks_multiplatform.app.generated.resources.setting_help_second_scale
import gclocks_multiplatform.app.generated.resources.setting_second_scale
import gclocks_multiplatform.app.generated.resources.setting_time_format
import org.beatonma.gclocks.app.LocalizedString
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
    key = "colors",
    localized = LocalizedString(Res.string.setting_colors),
    helpText = null,
    value = paints.colors.toList(),
    onValueChange = onValueChange,
)

internal fun chooseLayout(value: Layout, onUpdate: (Layout) -> Unit) =
    Setting.SingleSelect(
        key = "layout",
        localized = LocalizedString(Res.string.setting_clock_layout),
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
        key = "horizontal_alignment",
        localized = LocalizedString(Res.string.setting_alignment_horizontal),
        value = value,
        values = HorizontalAlignment.entries,
        onValueChange = onUpdate
    )

internal fun chooseVerticalAlignment(
    value: VerticalAlignment,
    onUpdate: (VerticalAlignment) -> Unit,
) =
    Setting.SingleSelect(
        key = "vertical_alignment",
        localized = LocalizedString(Res.string.setting_alignment_vertical),
        value = value,
        values = VerticalAlignment.entries,
        onValueChange = onUpdate
    )


internal fun chooseTimeFormat(value: TimeFormat, onUpdate: (TimeFormat) -> Unit) =
    Setting.SingleSelect(
        key = "time_format",
        localized = LocalizedString(Res.string.setting_time_format),
        value = value,
        values = TimeFormat.entries,
        onValueChange = onUpdate
    )


internal fun chooseSpacing(value: Int, onUpdate: (Int) -> Unit, default: Int, max: Int) =
    Setting.Int(
        key = "spacing",
        localized = LocalizedString(Res.string.setting_clock_spacing),
        helpText = LocalizedString(Res.string.setting_help_clock_spacing),
        value = value,
        default = default,
        min = 0,
        max = max,
        onValueChange = onUpdate
    )


internal fun chooseSecondScale(value: Float, onUpdate: (Float) -> Unit) =
    Setting.Float(
        key = "second_scale",
        localized = LocalizedString(Res.string.setting_second_scale),
        helpText = LocalizedString(Res.string.setting_help_second_scale),
        value = value,
        default = 0.5f,
        min = 0.2f,
        max = 1f,
        stepSize = 0.1f,
        onValueChange = onUpdate
    )