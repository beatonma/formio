package org.beatonma.formio.compose.components.settings.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.beatonma.formio.compose.components.Row


@Composable
internal fun LabelledSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    min: Float,
    max: Float,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    startLabel: String? = null,
    endLabel: String? = null,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Row.MediumSpacingArrangement,
    ) {
        startLabel?.let { Text(it) }


        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            steps = kotlin.math.max(
                0,
                steps - 2 /* Slider doesn't consider the endpoints to be steps */
            ),
            modifier = Modifier.weight(1f),
        )

        endLabel?.let { Text(it) }
    }
}
