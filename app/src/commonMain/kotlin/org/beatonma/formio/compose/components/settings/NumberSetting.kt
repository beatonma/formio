package org.beatonma.formio.compose.components.settings


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.beatonma.formio.app.ui.resolve
import org.beatonma.formio.compose.components.Column
import org.beatonma.formio.compose.components.settings.components.LabelledSlider
import org.beatonma.formio.compose.components.settings.components.SettingLayout
import org.beatonma.formio.compose.components.settings.components.SettingName
import org.beatonma.formio.compose.components.settings.components.SettingTokens
import org.beatonma.formio.compose.components.settings.data.RichSetting
import kotlin.math.roundToInt

@Composable
fun IntegerSetting(
    setting: RichSetting.Int,
    modifier: Modifier = Modifier,
) {
    IntegerSetting(
        name = setting.name.resolve(),
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
        onValueChange = { offset -> onValueChange(offset.roundToInt()) },
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
    setting: RichSetting.Float,
    modifier: Modifier = Modifier,
) {
    FloatSetting(
        name = setting.name.resolve(),
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
        onValueChange = onValueChange,
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
    onValueChange: (Float) -> Unit,
    min: N,
    max: N,
    steps: Int,
    modifier: Modifier,
) {
    SettingLayout(modifier, helpText) {
        Column(
            Modifier.padding(horizontal = SettingTokens.SettingHorizontalPadding),
            verticalArrangement = Column.SmallSpacingArrangement,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SettingName("$name: ${value.format()}")

            LabelledSlider(
                value = value.toFloat(),
                onValueChange = onValueChange,
                min = min.toFloat(),
                max = max.toFloat(),
                steps = steps,
                modifier = Modifier.fillMaxWidth()
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
