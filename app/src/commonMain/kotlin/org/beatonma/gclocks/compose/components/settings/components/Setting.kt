package org.beatonma.gclocks.compose.components.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberPlainTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
internal fun SettingLayout(
    modifier: Modifier = Modifier,
    helpText: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    TooltipLayout(helpText) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
        }
    }
}

@Composable
internal fun CollapsibleSettingLayout(
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    helpText: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val horizontalPadding by animateDpAsState(if (isExpanded) 8.dp else 0.dp)
    val verticalPadding by animateDpAsState(if (isExpanded) 16.dp else 0.dp)
    val innerVerticalPadding by animateDpAsState(if (isExpanded) 8.dp else 0.dp)
    val backgroundColor by animateColorAsState(if (isExpanded) colorScheme.surface else colorScheme.background)

    Surface(
        modifier.padding(vertical = verticalPadding, horizontal = horizontalPadding),
        color = backgroundColor,
        shape = shapes.small,
    ) {
        SettingLayout(Modifier.padding(vertical = innerVerticalPadding), helpText, content)
    }
}

@Composable
internal fun CheckableSettingLayout(
    modifier: Modifier = Modifier,
    helpText: String? = null,
    onClick: (() -> Unit),
    role: Role,
    text: @Composable () -> Unit,
    checkable: @Composable () -> Unit,
) {
    TooltipLayout(helpText) {
        Row(
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick, role = role)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            text()
            checkable()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TooltipLayout(text: String?, content: @Composable () -> Unit) {
    text?.let {
        TooltipBox(
            rememberPlainTooltipPositionProvider(0.dp),
            { Tooltip(text) },
            rememberTooltipState(isPersistent = true),
            content = content,
        )
    } ?: content()
}


@Composable
private fun Tooltip(text: String) {
    Text(
        text,
        Modifier
            .background(colorScheme.surfaceVariant, shapes.small)
            .padding(8.dp, 4.dp)
    )
}
