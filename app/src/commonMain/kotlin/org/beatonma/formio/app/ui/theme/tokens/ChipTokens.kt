package org.beatonma.formio.app.ui.theme.tokens

import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.InputChipDefaults
import androidx.compose.ui.unit.Dp

/* https://m3.material.io/components/chips/specs */
internal object ChipTokens {
    object Assistive {
        val IconSize: Dp = AssistChipDefaults.IconSize
    }

    object Input {
        val IconSize: Dp = InputChipDefaults.IconSize
    }
}