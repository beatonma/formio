package org.beatonma.formio.app.ui.theme.tokens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal object ButtonGroupTokens {
    sealed interface ButtonGroupSize {
        val ButtonSpacing: Dp
        val Height: Dp
        val SmallRadius: Dp
        val LargeRadius: Dp
        val ContentPadding: PaddingValues
        val TextStyle: TextStyle @Composable get
    }

    object Default : ButtonGroupSize {
        override val ButtonSpacing: Dp = 2.dp
        override val Height = 40.dp
        override val SmallRadius = 8.dp
        override val LargeRadius = 20.dp
        override val ContentPadding: PaddingValues = ButtonDefaults.ContentPadding
        override val TextStyle @Composable get() = typography.labelLarge
    }

    object Small : ButtonGroupSize {
        override val ButtonSpacing: Dp = 2.dp
        override val Height = 32.dp
        override val SmallRadius = 4.dp
        override val LargeRadius = 16.dp

        private val HorizontalContentPadding = 12.dp
        private val VerticalContentPadding = 0.dp
        override val ContentPadding: PaddingValues = PaddingValues(
            start = HorizontalContentPadding,
            end = HorizontalContentPadding,
            top = VerticalContentPadding,
            bottom = VerticalContentPadding,
        )

        override val TextStyle @Composable get() = typography.labelSmall
    }
}