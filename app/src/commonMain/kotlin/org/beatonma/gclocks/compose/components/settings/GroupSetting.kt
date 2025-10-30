package org.beatonma.gclocks.compose.components.settings

import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import org.beatonma.gclocks.app.ui.Localization.helpStringResourceMap
import org.beatonma.gclocks.app.ui.Localization.stringResourceMap
import org.beatonma.gclocks.compose.components.settings.components.CheckableSettingLayout
import org.beatonma.gclocks.compose.components.settings.components.DropdownSettingLayout
import org.beatonma.gclocks.compose.components.settings.components.SettingValue
import org.beatonma.gclocks.compose.components.settings.data.RichSetting
import org.jetbrains.compose.resources.ExperimentalResourceApi


@OptIn(ExperimentalResourceApi::class)
@Composable
fun <E : Enum<E>> SingleSelectSetting(
    setting: RichSetting.SingleSelect<E>,
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
    values: Set<E>,
    modifier: Modifier = Modifier,
    helpText: String? = null,
    onValueChange: (newValue: E) -> Unit,
) {
    val resourceMap = remember(value) { value::class.stringResourceMap }
    val helpResourceMap = remember(value) { value::class.helpStringResourceMap }

    DropdownSettingLayout(
        name,
        modifier,
        helpText = helpText,
        valueDescription = resourceMap.getValue(value).resolve()
    ) {
        for (v in values) {
            val onClick = { onValueChange(v) }

            CheckableSettingLayout(
                role = Role.RadioButton,
                onClick = onClick,
                helpText = helpResourceMap?.getValue(v)?.resolve(),
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
    setting: RichSetting.MultiSelect<E>,
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
    values: Set<E>,
    onValueChange: (newValue: Set<E>) -> Unit,
    modifier: Modifier = Modifier,
    helpText: String? = null,
    defaultValue: E = values.first(),
    allowEmptySet: Boolean = false,
) {
    val resourceMap = remember(defaultValue) { defaultValue::class.stringResourceMap }
    val helpResourceMap = remember(defaultValue) { defaultValue::class.helpStringResourceMap }

    @Suppress("SimplifiableCallChain")
    DropdownSettingLayout(
        name,
        modifier,
        helpText = helpText,
        valueDescription = values.map { resourceMap.getValue(it).resolve() }.joinToString(", ")
    ) {
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
