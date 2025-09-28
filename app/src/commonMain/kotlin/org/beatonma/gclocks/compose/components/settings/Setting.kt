package org.beatonma.gclocks.compose.components.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.beatonma.gclocks.compose.components.settings.components.OnFocusSetting
import org.beatonma.gclocks.compose.components.settings.data.RichSetting

@Composable
fun Setting(
    setting: RichSetting<*>,
    modifier: Modifier,
    onFocus: OnFocusSetting?,
) {
    when (setting) {
        is RichSetting.ClockPosition -> ClockPositionSetting(setting, modifier)
        is RichSetting.ClockType -> ClockTypeSetting(setting, modifier)
        is RichSetting.Color -> ColorSetting(setting, modifier, onFocus = onFocus)
        is RichSetting.Colors -> MultiColorSetting(setting, modifier, onFocus = onFocus)
        is RichSetting.SingleSelect<*> -> SingleSelectSetting(setting, modifier, onFocus = onFocus)
        is RichSetting.MultiSelect<*> -> MultiSelectSetting(setting, modifier, onFocus = onFocus)
        is RichSetting.Bool -> BooleanSetting(setting, modifier)
        is RichSetting.Float -> FloatSetting(setting, modifier)
        is RichSetting.Int -> IntegerSetting(setting, modifier)
    }
}
