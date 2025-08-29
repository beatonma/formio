package org.beatonma.gclocks.compose.components.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.ui_show_less
import gclocks_multiplatform.app.generated.resources.ui_show_more
import org.beatonma.gclocks.app.Localization.helpStringResourceMap
import org.beatonma.gclocks.app.Localization.stringResourceMap
import org.beatonma.gclocks.compose.AppIcon
import org.beatonma.gclocks.compose.animation.EnterVertical
import org.beatonma.gclocks.compose.animation.ExitVertical
import org.beatonma.gclocks.compose.components.settings.components.CheckableSettingLayout
import org.beatonma.gclocks.compose.components.settings.components.CollapsibleSettingLayout
import org.beatonma.gclocks.compose.components.settings.components.SettingName
import org.beatonma.gclocks.compose.components.settings.components.SettingValue
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import kotlin.enums.EnumEntries


@OptIn(ExperimentalResourceApi::class)
@Composable
fun <E : Enum<E>> SingleSelectSetting(
    setting: Setting.SingleSelect<E>,
    modifier: Modifier = Modifier,
) {
    SingleSelectSetting(
        name = setting.localized.resolve(),
        value = setting.value,
        values = setting.values,
        modifier = modifier,
        helpText = setting.helpText?.resolve(),
        onValueChange = setting.onValueChange,
    )
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun <E : Enum<E>> SingleSelectSetting(
    name: String,
    value: E,
    values: EnumEntries<E>,
    modifier: Modifier = Modifier,
    helpText: String? = null,
    onValueChange: (newValue: E) -> Unit,
) {
    val resourceMap = remember { value::class.stringResourceMap }
    val helpResourceMap = remember { value::class.helpStringResourceMap }

    CollapsibleGroup(name, modifier, helpText) {
        for (v in values) {
            val onClick = { onValueChange(v) }

            CheckableSettingLayout(
                helpText = helpResourceMap?.getValue(v)?.resolve(),
                onClick = onClick,
                role = Role.RadioButton,
                text = { SettingValue(resourceMap.getValue(v).resolve()) }
            ) {
                RadioButton(
                    selected = value == v,
                    onClick = onClick,
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun <E : Enum<E>> MultiSelectSetting(
    setting: Setting.MultiSelect<E>,
    modifier: Modifier = Modifier,
    defaultValue: E = setting.values.first(),
    allowEmptySet: Boolean = false,
) {
    MultiSelectSetting(
        name = setting.localized.resolve(),
        value = setting.value,
        values = setting.values,
        onValueChange = setting.onValueChange,
        modifier = modifier,
        helpText = setting.helpText?.resolve(),
        defaultValue = defaultValue,
        allowEmptySet = allowEmptySet
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun <E : Enum<E>> MultiSelectSetting(
    name: String,
    value: Set<E>,
    values: EnumEntries<E>,
    onValueChange: (newValue: Set<E>) -> Unit,
    modifier: Modifier = Modifier,
    helpText: String? = null,
    defaultValue: E = values.first(),
    allowEmptySet: Boolean = false,
) {
    val resourceMap = remember { defaultValue::class.stringResourceMap }
    val helpResourceMap = remember { defaultValue::class.helpStringResourceMap }

    CollapsibleGroup(name, modifier, helpText) {
        for (v in values) {
            val onClick = {
                if (v in value) {
                    val newValue = value.filter { it != v }.toSet()
                    if (!allowEmptySet && newValue.isEmpty()) {
                        onValueChange(setOf(defaultValue))
                    } else {
                        onValueChange(newValue)
                    }
                } else {
                    onValueChange((value + v).toSet())
                }
            }

            CheckableSettingLayout(
                helpText = helpResourceMap?.getValue(v)?.resolve(),
                onClick = onClick,
                role = Role.Checkbox,
                text = { SettingValue(resourceMap.getValue(v).resolve()) }
            ) {
                Checkbox(
                    checked = v in value,
                    onCheckedChange = { onClick() }
                )
            }
        }
    }
}

@Composable
private fun CollapsibleGroup(
    name: String,
    modifier: Modifier,
    helpText: String?,
    content: @Composable ColumnScope.() -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val onClick = { expanded = !expanded }

    val iconRotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    CollapsibleSettingLayout(expanded, modifier, helpText) {
        CheckableSettingLayout(
            helpText = helpText,
            onClick = onClick,
            role = Role.DropdownList,
            text = { SettingName(name) }
        ) {
            IconButton(onClick = onClick) {
                Icon(
                    AppIcon.ArrowDropdown,
                    stringResource(
                        when (expanded) {
                            true -> Res.string.ui_show_less
                            false -> Res.string.ui_show_more
                        }
                    ),
                    modifier = Modifier.rotate(iconRotation)
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = EnterVertical,
            exit = ExitVertical,
        ) {
            Column(content = content)
        }
    }
}
