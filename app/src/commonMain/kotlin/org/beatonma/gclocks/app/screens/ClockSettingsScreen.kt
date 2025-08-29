package org.beatonma.gclocks.app.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.app.util.options.getOptionsAdapter
import org.beatonma.gclocks.compose.components.settings.BooleanSetting
import org.beatonma.gclocks.compose.components.settings.ColorSetting
import org.beatonma.gclocks.compose.components.settings.FloatSetting
import org.beatonma.gclocks.compose.components.settings.IntegerSetting
import org.beatonma.gclocks.compose.components.settings.MultiColorSetting
import org.beatonma.gclocks.compose.components.settings.MultiSelectSetting
import org.beatonma.gclocks.compose.components.settings.Setting
import org.beatonma.gclocks.compose.components.settings.SettingsGroup
import org.beatonma.gclocks.compose.components.settings.SettingsWrapper
import org.beatonma.gclocks.compose.components.settings.SingleSelectSetting
import org.beatonma.gclocks.core.options.Options


@Composable
fun <O : Options<*>> ClockSettingsScreen(
    options: O,
    onEditOptions: (O) -> Unit,
    modifier: Modifier = Modifier,
) {
    val optionsAdapter = remember(options) {
        getOptionsAdapter(options) {
            onEditOptions(it)
        }
    }

    LazyVerticalStaggeredGrid(
//        StaggeredGridCells.Adaptive(300.dp),
        StaggeredGridCells.Fixed(1),
        modifier
    ) {
        optionsAdapter.settings.map { setting ->
            SettingOrGroup(setting, Modifier.padding(bottom = 4.dp))
        }
    }
}

private fun LazyStaggeredGridScope.SettingOrGroup(
    item: SettingsWrapper,
    modifier: Modifier = Modifier,
) {
    when (item) {
        is SettingsGroup -> Group(item, modifier)
        is Setting<*> -> Setting(item, modifier)
    }
}

private fun LazyStaggeredGridScope.Group(group: SettingsGroup, modifier: Modifier = Modifier) {
    item {
        Text(group.name, modifier, style = typography.titleSmall)
    }

    group.settings.forEach {
        SettingOrGroup(it)
    }
}

private fun <T> LazyStaggeredGridScope.Setting(setting: Setting<T>, modifier: Modifier = Modifier) {
    when (setting) {
        is Setting.Color -> item {
            ColorSetting(
                setting,
                modifier = modifier,
            )
        }

        is Setting.Colors -> item {
            MultiColorSetting(setting, modifier = modifier)
        }

        is Setting.SingleSelect<*> -> item {
            SingleSelectSetting(setting, modifier = modifier)
        }

        is Setting.MultiSelect<*> -> item {
            MultiSelectSetting(setting, modifier = modifier)
        }

        is Setting.Bool -> item {
            BooleanSetting(
                setting,
                modifier = modifier
            )
        }

        is Setting.Int -> item { IntegerSetting(setting, modifier = modifier) }
        is Setting.Float -> item { FloatSetting(setting, modifier = modifier) }
    }
}
