package org.beatonma.gclocks.compose.components.settings


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.beatonma.gclocks.compose.components.settings.components.LabelledSlider
import org.beatonma.gclocks.compose.components.settings.components.SettingLayout
import kotlin.math.roundToInt

@Composable
fun IntegerSetting(
    setting: Setting.Int,
    modifier: Modifier = Modifier,
) {
    IntegerSetting(
        name = setting.localized.resolve(),
        helpText = setting.helpText?.resolve(),
        value = setting.value,
        onValueChange = setting.onValueChange,
        min = setting.min,
        max = setting.max,
        modifier = modifier,
    )
}

@Composable
fun IntegerSetting(
    name: String,
    helpText: String?,
    value: Int,
    onValueChange: (newValue: Int) -> Unit,
    min: Int,
    max: Int,
    modifier: Modifier = Modifier,
) {
    NumberSettingLayout(
        name = name,
        helpText = helpText,
        value = value,
        onOffsetChange = { offset -> onValueChange(offset.roundToInt()) },
        min = min,
        max = max,
        modifier = modifier,
    )
}


@Composable
fun FloatSetting(
    setting: Setting.Float,
    modifier: Modifier = Modifier,
) {
    FloatSetting(
        name = setting.localized.resolve(),
        helpText = setting.helpText?.resolve(),
        value = setting.value,
        onValueChange = setting.onValueChange,
        min = setting.min,
        max = setting.max,
        modifier = modifier,
    )
}

@Composable
fun FloatSetting(
    name: String,
    helpText: String?,
    value: Float,
    onValueChange: (newValue: Float) -> Unit,
    min: Float,
    max: Float,
    modifier: Modifier = Modifier,
) {
    NumberSettingLayout(
        name = name,
        helpText = helpText,
        value = value,
        onOffsetChange = onValueChange,
        min = min,
        max = max,
        modifier = modifier,
    )
}


@Composable
private fun <N : Number> NumberSettingLayout(
    name: String,
    helpText: String?,
    value: N,
    onOffsetChange: (Float) -> Unit,
    min: N,
    max: N,
    modifier: Modifier,
) {
    SettingLayout(helpText = helpText) {
        Column(
            modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("$name: ${value.format()}", Modifier.align(Alignment.CenterHorizontally))

            LabelledSlider(
                value = value.toFloat(),
                onValueChange = onOffsetChange,
                min = min.toFloat(),
                max = max.toFloat(),
            )
        }
    }
}


fun Number.format(decimalPlaces: Int = 2) = this.toString().run {
    indexOf('.').let { index ->
        when (index) {
            -1 -> this
            else -> take(index + decimalPlaces + 1)
        }
    }
}
