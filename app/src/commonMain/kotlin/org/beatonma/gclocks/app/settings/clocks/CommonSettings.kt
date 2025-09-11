package org.beatonma.gclocks.app.settings.clocks

import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.setting_alignment_horizontal
import gclocks_multiplatform.app.generated.resources.setting_alignment_vertical
import gclocks_multiplatform.app.generated.resources.setting_background_color
import gclocks_multiplatform.app.generated.resources.setting_clock_layout
import gclocks_multiplatform.app.generated.resources.setting_clock_spacing
import gclocks_multiplatform.app.generated.resources.setting_colors
import gclocks_multiplatform.app.generated.resources.setting_help_clock_spacing
import gclocks_multiplatform.app.generated.resources.setting_help_second_scale
import gclocks_multiplatform.app.generated.resources.setting_second_scale
import gclocks_multiplatform.app.generated.resources.setting_time_format
import org.beatonma.gclocks.app.LocalizedString
import org.beatonma.gclocks.compose.components.settings.Key
import org.beatonma.gclocks.compose.components.settings.RichSetting
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.TimeFormat


object CommonKeys {
    val clockColors = Key.ColorsKey("clock_colors")
    val clockLayout = Key.EnumKey<Layout>("clock_layout")
    val clockHorizontalAlignment = Key.EnumKey<HorizontalAlignment>("clock_horizontal_alignment")
    val clockVerticalAlignment = Key.EnumKey<VerticalAlignment>("clock_vertical_alignment")
    val clockTimeFormat = Key.EnumKey<TimeFormat>("clock_time_format")
    val clockSpacing = Key.IntKey("clock_spacing")
    val clockSecondsScale = Key.FloatKey("clock_seconds_scale")
    val backgroundColor = Key.ColorKey("background_color")
}


internal fun createColorsAdapter(
    paints: Paints,
    onValueChange: (value: List<Color>) -> Unit,
) = RichSetting.Colors(
    key = CommonKeys.clockColors,
    localized = LocalizedString(Res.string.setting_colors),
    helpText = null,
    value = paints.colors.toList(),
    onValueChange = onValueChange,
)

internal fun chooseLayout(value: Layout, onUpdate: (Layout) -> Unit) =
    RichSetting.SingleSelect(
        key = CommonKeys.clockLayout,
        localized = LocalizedString(Res.string.setting_clock_layout),
        helpText = null,
        value = value,
        values = Layout.entries.toSet(),
        onValueChange = onUpdate
    )

internal fun chooseHorizontalAlignment(
    value: HorizontalAlignment,
    onUpdate: (HorizontalAlignment) -> Unit,
) =
    RichSetting.SingleSelect(
        key = CommonKeys.clockHorizontalAlignment,
        localized = LocalizedString(Res.string.setting_alignment_horizontal),
        value = value,
        values = HorizontalAlignment.entries.toSet(),
        onValueChange = onUpdate
    )

internal fun chooseVerticalAlignment(
    value: VerticalAlignment,
    onUpdate: (VerticalAlignment) -> Unit,
) =
    RichSetting.SingleSelect(
        key = CommonKeys.clockVerticalAlignment,
        localized = LocalizedString(Res.string.setting_alignment_vertical),
        value = value,
        values = VerticalAlignment.entries.toSet(),
        onValueChange = onUpdate
    )


internal fun chooseTimeFormat(
    value: TimeFormat,
    onUpdate: (TimeFormat) -> Unit,
    filterValues: ((Set<TimeFormat>) -> Set<TimeFormat>)? = null,
) =
    RichSetting.SingleSelect(
        key = CommonKeys.clockTimeFormat,
        localized = LocalizedString(Res.string.setting_time_format),
        value = value,
        values = TimeFormat.entries.toSet().run {
            filterValues?.let { filter -> filter(this) } ?: this
        },
        onValueChange = onUpdate
    )


internal fun chooseSpacing(value: Int, onUpdate: (Int) -> Unit, default: Int, max: Int) =
    RichSetting.Int(
        key = CommonKeys.clockSpacing,
        localized = LocalizedString(Res.string.setting_clock_spacing),
        helpText = LocalizedString(Res.string.setting_help_clock_spacing),
        value = value,
        default = default,
        min = 0,
        max = max,
        onValueChange = onUpdate
    )


internal fun chooseSecondScale(value: Float, onUpdate: (Float) -> Unit) =
    RichSetting.Float(
        key = CommonKeys.clockSecondsScale,
        localized = LocalizedString(Res.string.setting_second_scale),
        helpText = LocalizedString(Res.string.setting_help_second_scale),
        value = value,
        default = 0.5f,
        min = 0.2f,
        max = 1f,
        stepSize = 0.1f,
        onValueChange = onUpdate
    )

internal fun chooseBackgroundColor(value: Color, onUpdate: (Color) -> Unit) = RichSetting.Color(
    key = CommonKeys.backgroundColor,
    localized = LocalizedString(Res.string.setting_background_color),
    value = value,
    onValueChange = onUpdate,
)
