package org.beatonma.gclocks.app.data.settings

import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.setting_alignment_horizontal
import gclocks_multiplatform.app.generated.resources.setting_alignment_vertical
import gclocks_multiplatform.app.generated.resources.setting_choose_clock_style
import gclocks_multiplatform.app.generated.resources.setting_clock_layout
import gclocks_multiplatform.app.generated.resources.setting_clock_position
import gclocks_multiplatform.app.generated.resources.setting_clock_spacing
import gclocks_multiplatform.app.generated.resources.setting_colors
import gclocks_multiplatform.app.generated.resources.setting_help_clock_spacing
import gclocks_multiplatform.app.generated.resources.setting_help_second_scale
import gclocks_multiplatform.app.generated.resources.setting_second_scale
import gclocks_multiplatform.app.generated.resources.setting_time_is_24_hour
import gclocks_multiplatform.app.generated.resources.setting_time_is_zero_padded
import gclocks_multiplatform.app.generated.resources.setting_time_show_seconds
import org.beatonma.gclocks.compose.components.settings.data.Key
import org.beatonma.gclocks.compose.components.settings.data.RichSetting
import org.beatonma.gclocks.compose.components.settings.data.Setting
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.RectF
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.TimeFormat


object SettingKey {
    val clockLayout = Key.Enum<Layout>("clock_layout")
    val clockHorizontalAlignment = Key.Enum<HorizontalAlignment>("clock_horizontal_alignment")
    val clockVerticalAlignment = Key.Enum<VerticalAlignment>("clock_vertical_alignment")
    val clockSpacing = Key.Int("clock_spacing")
    val clockSecondsScale = Key.Float("clock_seconds_scale")
    val clockPosition = Key.RectF("clock_position")
    val clockType = Key.Enum<ClockType>("clock_type")
    val clockTimeFormatIs24Hour = Key.Bool("clock_is_24_hour")
    val clockTimeFormatIsZeroPadded = Key.Bool("clock_is_zero_padded")
    val clockTimeFormatShowSeconds = Key.Bool("clock_show_seconds")
    val clockColors = Key.ClockColors("clock_colors")
    val clockColorsWithBackground = Key.ClockColors("clock_colors_with_background")
    val clockStrokeWidthKey = Key.Float("stroke_width")
}

internal fun chooseClockColors(
    paints: Paints,
    onUpdatePaints: (value: List<Color>) -> Unit,
    palettes: List<ClockColors>,
    onUpdatePalettes: (List<ClockColors>) -> Unit,
) = RichSetting.ClockColors(
    key = SettingKey.clockColors,
    name = Res.string.setting_colors,
    helpText = null,
    value = ClockColors(null, paints.colors.toList()),
    onValueChange = { onUpdatePaints(it.colors) },
    palettes = palettes,
    onUpdatePalettes = onUpdatePalettes,
)

internal fun chooseClockColors(
    value: ClockColors,
    onValueChange: (ClockColors) -> Unit,
    palettes: List<ClockColors>,
    onUpdatePalettes: (List<ClockColors>) -> Unit,
) = RichSetting.ClockColors(
    key = SettingKey.clockColorsWithBackground,
    name = Res.string.setting_colors,
    helpText = null,
    value = value,
    onValueChange = onValueChange,
    palettes = palettes,
    onUpdatePalettes = onUpdatePalettes,
)

internal fun chooseLayout(value: Layout, onUpdate: (Layout) -> Unit) =
    RichSetting.SingleSelect(
        key = SettingKey.clockLayout,
        name = Res.string.setting_clock_layout,
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
        key = SettingKey.clockHorizontalAlignment,
        name = Res.string.setting_alignment_horizontal,
        value = value,
        values = HorizontalAlignment.entries.toSet(),
        onValueChange = onUpdate
    )

internal fun chooseVerticalAlignment(
    value: VerticalAlignment,
    onUpdate: (VerticalAlignment) -> Unit,
) =
    RichSetting.SingleSelect(
        key = SettingKey.clockVerticalAlignment,
        name = Res.string.setting_alignment_vertical,
        value = value,
        values = VerticalAlignment.entries.toSet(),
        onValueChange = onUpdate
    )

internal fun chooseTimeFormat(
    value: TimeFormat,
    onUpdate: (TimeFormat) -> Unit,
): List<Setting> = listOf(
    RichSetting.Bool(
        key = SettingKey.clockTimeFormatIs24Hour,
        name = Res.string.setting_time_is_24_hour,
        value = value.is24Hour,
        onValueChange = {
            onUpdate(
                TimeFormat.build(
                    is24Hour = it,
                    isZeroPadded = value.isZeroPadded,
                    showSeconds = value.showSeconds
                )
            )
        }
    ),
    RichSetting.Bool(
        key = SettingKey.clockTimeFormatIsZeroPadded,
        name = Res.string.setting_time_is_zero_padded,
        value = value.isZeroPadded,
        onValueChange = {
            onUpdate(
                TimeFormat.build(
                    is24Hour = value.is24Hour,
                    isZeroPadded = it,
                    showSeconds = value.showSeconds
                )
            )
        }
    ),
    RichSetting.Bool(
        key = SettingKey.clockTimeFormatShowSeconds,
        name = Res.string.setting_time_show_seconds,
        value = value.showSeconds,
        onValueChange = {
            onUpdate(
                TimeFormat.build(
                    is24Hour = value.is24Hour,
                    isZeroPadded = value.isZeroPadded,
                    showSeconds = it
                )
            )
        }
    ),
)


internal fun chooseSpacing(value: Int, onUpdate: (Int) -> Unit, default: Int, max: Int) =
    RichSetting.Int(
        key = SettingKey.clockSpacing,
        name = Res.string.setting_clock_spacing,
        helpText = Res.string.setting_help_clock_spacing,
        value = value,
        default = default,
        min = 0,
        max = max,
        onValueChange = onUpdate
    )


internal fun chooseSecondScale(value: Float, onUpdate: (Float) -> Unit) =
    RichSetting.Float(
        key = SettingKey.clockSecondsScale,
        name = Res.string.setting_second_scale,
        helpText = Res.string.setting_help_second_scale,
        value = value,
        default = 0.5f,
        min = 0.2f,
        max = 1f,
        stepSize = 0.1f,
        onValueChange = onUpdate
    )

internal fun chooseClockPosition(value: RectF, onUpdate: (RectF) -> Unit) =
    RichSetting.ClockPosition(
        key = SettingKey.clockPosition,
        name = Res.string.setting_clock_position,
        value = value,
        onValueChange = onUpdate,
    )

internal fun chooseClockType(value: ClockType, onUpdate: (ClockType) -> Unit) =
    RichSetting.ClockType(
        key = SettingKey.clockType,
        name = Res.string.setting_choose_clock_style,
        value = value,
        onValueChange = onUpdate,
    )
