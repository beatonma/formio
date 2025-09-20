package org.beatonma.gclocks.app.settings.clocks

import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.setting_alignment_horizontal
import gclocks_multiplatform.app.generated.resources.setting_alignment_vertical
import gclocks_multiplatform.app.generated.resources.setting_background_color
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
import org.beatonma.gclocks.app.LocalizedString
import org.beatonma.gclocks.compose.components.settings.Key
import org.beatonma.gclocks.compose.components.settings.RichSetting
import org.beatonma.gclocks.compose.components.settings.Setting
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.RectF
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.TimeFormat


object SettingKey {
    val clockColors = Key.ColorsKey("clock_colors")
    val clockLayout = Key.EnumKey<Layout>("clock_layout")
    val clockHorizontalAlignment = Key.EnumKey<HorizontalAlignment>("clock_horizontal_alignment")
    val clockVerticalAlignment = Key.EnumKey<VerticalAlignment>("clock_vertical_alignment")
    val clockSpacing = Key.IntKey("clock_spacing")
    val clockSecondsScale = Key.FloatKey("clock_seconds_scale")
    val backgroundColor = Key.ColorKey("background_color")
    val clockPosition = Key.RectFKey("clock_position")
    val clockTimeFormatIs24Hour = Key.BoolKey("clock_is_24_hour")
    val clockTimeFormatIsZeroPadded = Key.BoolKey("clock_is_zero_padded")
    val clockTimeFormatShowSeconds = Key.BoolKey("clock_show_seconds")
}


internal fun createColorsAdapter(
    paints: Paints,
    onValueChange: (value: List<Color>) -> Unit,
) = RichSetting.Colors(
    key = SettingKey.clockColors,
    localized = LocalizedString(Res.string.setting_colors),
    helpText = null,
    value = paints.colors.toList(),
    onValueChange = onValueChange,
)

internal fun chooseLayout(value: Layout, onUpdate: (Layout) -> Unit) =
    RichSetting.SingleSelect(
        key = SettingKey.clockLayout,
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
        key = SettingKey.clockHorizontalAlignment,
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
        key = SettingKey.clockVerticalAlignment,
        localized = LocalizedString(Res.string.setting_alignment_vertical),
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
        localized = LocalizedString(Res.string.setting_time_is_24_hour),
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
        localized = LocalizedString(Res.string.setting_time_is_zero_padded),
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
        localized = LocalizedString(Res.string.setting_time_show_seconds),
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
        key = SettingKey.clockSecondsScale,
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
    key = SettingKey.backgroundColor,
    localized = LocalizedString(Res.string.setting_background_color),
    value = value,
    onValueChange = onUpdate,
)

internal fun chooseClockPosition(value: RectF, onUpdate: (RectF) -> Unit) = RichSetting.RectF(
    key = SettingKey.clockPosition,
    localized = LocalizedString(Res.string.setting_clock_position),
    value = value,
    onValueChange = onUpdate,
)
