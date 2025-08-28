package org.beatonma.gclocks.app.screens

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.beatonma.gclocks.app.util.options.getOptionsAdapter
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.compose.components.settings.BooleanSetting
import org.beatonma.gclocks.compose.components.settings.ColorSetting
import org.beatonma.gclocks.compose.components.settings.FloatSetting
import org.beatonma.gclocks.compose.components.settings.IntegerSetting
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
) {
    val optionsAdapter = remember(options) {
        getOptionsAdapter(options) {
            onEditOptions(it)
        }
    }

    LazyColumn {
        item {
            Clock(options, Modifier.fillMaxWidth().aspectRatio(16f / 9f))
        }

        item {
            Text(options.toString())
        }

        optionsAdapter.settings.map { setting ->
            SettingOrGroup(setting)
        }
    }
}

private fun LazyListScope.SettingOrGroup(
    item: SettingsWrapper,
    modifier: Modifier = Modifier,
) {
    when (item) {
        is SettingsGroup -> Group(item, modifier)
        is Setting<*> -> Setting(item, modifier)
    }
}

private fun LazyListScope.Group(group: SettingsGroup, modifier: Modifier = Modifier) {
    item {
        Text(group.name, modifier)
    }

    group.settings.forEach {
        SettingOrGroup(it)
    }
}

private fun <T> LazyListScope.Setting(setting: Setting<T>, modifier: Modifier = Modifier) {
    when (setting) {
        is Setting.Color -> item {
            ColorSetting(
                setting,
                modifier = modifier,
            )
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
