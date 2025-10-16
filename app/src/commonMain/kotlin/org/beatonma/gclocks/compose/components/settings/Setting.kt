package org.beatonma.gclocks.compose.components.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.beatonma.gclocks.compose.components.settings.data.RichSetting

@Composable
fun Setting(
    setting: RichSetting<*>,
    modifier: Modifier,
) {
    when (setting) {
        is RichSetting.ClockColors -> ClockColorsSetting(setting, modifier)
        is RichSetting.ClockPosition -> ClockPositionSetting(setting, modifier)
        is RichSetting.ClockType -> ClockTypeSetting(setting, modifier)
        is RichSetting.SingleSelect<*> -> SingleSelectSetting(setting, modifier)
        is RichSetting.MultiSelect<*> -> MultiSelectSetting(setting, modifier)
        is RichSetting.Bool -> BooleanSetting(setting, modifier)
        is RichSetting.Float -> FloatSetting(setting, modifier)
        is RichSetting.Int -> IntegerSetting(setting, modifier)
    }
}
