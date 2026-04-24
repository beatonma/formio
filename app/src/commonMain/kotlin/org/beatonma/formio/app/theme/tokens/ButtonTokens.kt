package org.beatonma.formio.app.theme.tokens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.unit.Dp

/* https://m3.material.io/components/buttons/specs */
internal object ButtonTokens {
    private interface ButtonSize {
        val ButtonWithIconContentPadding: PaddingValues
        val IconSize: Dp
        val IconSpacing: Dp
    }

    object Default : ButtonSize {
        override val ButtonWithIconContentPadding: PaddingValues = ButtonDefaults.ButtonWithIconContentPadding
        override val IconSpacing: Dp = ButtonDefaults.IconSpacing
        override val IconSize: Dp = ButtonDefaults.IconSize
    }
}
