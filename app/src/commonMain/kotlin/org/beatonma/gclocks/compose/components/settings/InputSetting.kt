package org.beatonma.gclocks.compose.components.settings


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.app.ui.resolve
import org.beatonma.gclocks.compose.AppIcon
import org.beatonma.gclocks.compose.components.settings.components.DropdownSettingLayout
import org.beatonma.gclocks.compose.components.settings.data.RichSetting
import org.beatonma.gclocks.compose.components.settings.data.SettingValidator
import org.beatonma.gclocks.compose.components.settings.data.ValidationFailed


@Composable
fun IntListSetting(
    setting: RichSetting.IntList,
    modifier: Modifier = Modifier,
) {
    IntListSetting(
        name = setting.localized.resolve(),
        helpText = setting.helpText?.resolve(),
        setting.placeholder?.resolve(),
        setting.value,
        setting.onValueChange,
        setting.validator,
        modifier,
    )
}

@Composable
fun IntListSetting(
    name: String,
    helpText: String?,
    placeholder: String?,
    value: List<Int>,
    onValueChange: (newValue: List<Int>) -> Unit,
    validator: SettingValidator<Int>,
    modifier: Modifier = Modifier,
) {
    DropdownSettingLayout(
        name, modifier, helpText, summariseIntList(value).ifEmpty { "-" }
    ) {
        var text by remember { mutableStateOf("") }

        FlowRow(
            Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            value.forEach { n ->
                InputChip(
                    false,
                    onClick = { onValueChange(value.filter { it != n }) }, label = { Text("$n") },
                    trailingIcon = { Icon(AppIcon.Close, null) }
                )
            }
            OutlinedTextField(
                value = text,
                onValueChange = { newValue ->
                    parseInt(
                        newValue,
                        validator,
                        onSuccess = { n ->
                            text = when (n) {
                                null -> ""
                                else -> "$n"
                            }
                        }
                    )
                },
                Modifier.widthIn(max = 128.dp),
                prefix = { Icon(AppIcon.Add, null) },
                placeholder = placeholder?.let { { Text(it, maxLines = 1) } },
                singleLine = true,
                keyboardOptions = remember {
                    KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        parseInt(text, validator, { n ->
                            if (n != null) {
                                onValueChange((value + listOf(n)).sorted().distinct())
                            }
                            text = ""
                        })
                    }
                )
            )

        }
    }
}

/**
 * Stringify a list of Ints, reducing runs of consecutive numbers to a range.
 * e.g. 1, 2, 3, 6, 8 -> "1-3, 6, 8"
 */
internal fun summariseIntList(list: List<Int>): String {
    if (list.isEmpty()) return ""

    val parts = mutableListOf<String>()

    var previousValue = list.first()
    val run = mutableListOf(previousValue)

    fun rememberRun() {
        if (run.size > 2) {
            parts.add("${run.first()}-${run.last()}")
        } else {
            parts.addAll(run.map { it.toString() })
        }
        run.clear()
    }

    for (index in list.indices) {
        val value = list[index]
        if (value == previousValue) continue
        if (value != previousValue + 1) {
            rememberRun()
        }
        run.add(value)
        previousValue = value
    }
    if (run.isNotEmpty()) {
        rememberRun()
    }

    return parts.joinToString(", ")
}

private fun parseInt(
    text: String,
    validator: SettingValidator<Int>,
    onSuccess: (Int?) -> Unit,
    onError: (() -> Unit)? = null
) {
    if (text.isBlank()) {
        onSuccess(null)
    }
    try {
        val n = text.toInt().also(validator::validate)
        onSuccess(n)
    } catch (e: NumberFormatException) {
        onError?.invoke()
    } catch (e: ValidationFailed) {
        onError?.invoke()
    }
}
