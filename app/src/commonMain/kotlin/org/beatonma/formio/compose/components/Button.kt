package org.beatonma.formio.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import org.beatonma.formio.app.theme.tokens.ButtonTokens

@Composable
fun ButtonContent(icon: ImageVector, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ButtonTokens.Default.IconSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(ButtonTokens.Default.IconSize))
        Text(text)
    }
}

@Composable
fun Button(icon: ImageVector, text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick, modifier, contentPadding = ButtonTokens.Default.ButtonWithIconContentPadding) {
        ButtonContent(icon, text)
    }
}

@Composable
fun OutlinedButton(icon: ImageVector, text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(onClick, modifier, contentPadding = ButtonTokens.Default.ButtonWithIconContentPadding) {
        ButtonContent(icon, text)
    }
}