package org.beatonma.formio.compose.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import org.beatonma.formio.app.theme.tokens.ChipTokens

@Composable
fun AssistChip(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    AssistChip(
        onClick,
        { Text(label) },
        leadingIcon = icon?.let { { Icon(icon, null, Modifier.size(ChipTokens.Assistive.IconSize)) } },
        modifier = modifier,
    )
}


@Composable
fun InputChip(
    onClick: () -> Unit,
    label: String,
    trailingIcon: ImageVector,
    selected: Boolean = false,
    modifier: Modifier = Modifier,
) {
    InputChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier,
        trailingIcon = { Icon(trailingIcon, null, Modifier.size(ChipTokens.Input.IconSize)) }
    )
}