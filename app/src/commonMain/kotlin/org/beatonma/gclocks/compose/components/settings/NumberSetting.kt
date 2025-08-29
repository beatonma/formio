package org.beatonma.gclocks.compose.components.settings


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.compose.components.settings.components.LabelledSlider
import org.beatonma.gclocks.compose.components.settings.components.SettingLayout
import org.beatonma.gclocks.compose.components.settings.components.SettingName
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
        stepSize = setting.stepSize
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
    stepSize: Int? = null,
) {
    NumberSettingLayout(
        name = name,
        helpText = helpText,
        value = value,
        onOffsetChange = { offset -> onValueChange(offset.roundToInt()) },
        min = min,
        max = max,
        modifier = modifier,
        steps = when (stepSize) {
            null -> 0
            0 -> 0
            else -> (max - min) / stepSize
        }
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
        stepSize = setting.stepSize,
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
    stepSize: Float? = null,
) {
    NumberSettingLayout(
        name = name,
        helpText = helpText,
        value = value,
        onOffsetChange = onValueChange,
        min = min,
        max = max,
        modifier = modifier,
        steps = when (stepSize) {
            null -> 0
            0f -> 0
            else -> ((max - min) / stepSize).roundToInt() + 1
        }
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
    steps: Int,
    modifier: Modifier,
) {
    SettingLayout(helpText = helpText) {
        Column(
            modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SettingName(
                "$name: ${value.format()}",
                Modifier
                    .padding(bottom = 4.dp)
                    .align(Alignment.CenterHorizontally)
            )

            LabelledSlider(
                value = value.toFloat(),
                onValueChange = onOffsetChange,
                min = min.toFloat(),
                max = max.toFloat(),
                steps = steps,
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
