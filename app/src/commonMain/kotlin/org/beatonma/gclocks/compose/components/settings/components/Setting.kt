package org.beatonma.gclocks.compose.components.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberPlainTooltipPositionProvider
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.cd_show_less
import gclocks_multiplatform.app.generated.resources.cd_show_more
import org.beatonma.gclocks.compose.AppIcon
import org.beatonma.gclocks.compose.animation.EnterFade
import org.beatonma.gclocks.compose.animation.EnterVertical
import org.beatonma.gclocks.compose.animation.ExitFade
import org.beatonma.gclocks.compose.animation.ExitVertical
import org.jetbrains.compose.resources.stringResource


@Composable
internal fun SettingLayout(
    modifier: Modifier = Modifier,
    helpText: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    TooltipLayout(helpText, modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
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
    val innerHorizontalPadding by animateDpAsState(if (isExpanded) 8.dp else 0.dp)
    val backgroundColor by animateColorAsState(if (isExpanded) colorScheme.surface else colorScheme.background)
    val elevation by animateDpAsState(if (isExpanded) 1.dp else 0.dp)

    Surface(
        modifier.padding(vertical = verticalPadding, horizontal = horizontalPadding),
        color = backgroundColor,
        shape = shapes.small,
        shadowElevation = elevation,
    ) {
        SettingLayout(
            Modifier.padding(vertical = innerVerticalPadding, horizontal = innerHorizontalPadding),
            helpText,
            content
        )
    }
}

@Composable
internal fun DropdownSettingLayout(
    name: String,
    modifier: Modifier,
    helpText: String?,
    valueDescription: String,
    content: @Composable ColumnScope.() -> Unit,
) {

    var expanded by remember { mutableStateOf(false) }
    val onClick = { expanded = !expanded }

    val iconRotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    CollapsibleSettingLayout(expanded, modifier, helpText) {
        CheckableSettingLayout(
            helpText = helpText,
            onClick = onClick,
            role = Role.DropdownList,
            text = {
                SettingName(name)
            }
        ) {
            ValuePreview(!expanded, valueDescription)
            Icon(
                AppIcon.ArrowDropdown,
                stringResource(
                    when (expanded) {
                        true -> Res.string.cd_show_less
                        false -> Res.string.cd_show_more
                    }
                ),
                modifier = Modifier.weight(1f, false).rotate(iconRotation)
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = EnterVertical,
            exit = ExitVertical,
        ) {
            Column(content = content)
        }
    }
}

@Composable
internal fun CheckableSettingLayout(
    onClick: () -> Unit,
    role: Role?,
    modifier: Modifier = Modifier,
    helpText: String? = null,
    text: @Composable RowScope.() -> Unit,
    checkable: @Composable RowScope.() -> Unit,
) {
    TooltipLayout(helpText, modifier) {
        Row(
            Modifier
                .clickable(onClick = onClick, role = role)
                .minimumInteractiveComponentSize()
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                text()
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                checkable()
            }
        }
    }
}

@Composable
internal fun ValuePreview(isVisible: Boolean, value: String) {
    AnimatedVisibility(isVisible, enter = EnterFade, exit = ExitFade) {
        SettingValue(
            value,
            color = colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TooltipLayout(
    text: String?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    text?.let {
        TooltipBox(
            rememberPlainTooltipPositionProvider(0.dp),
            { Tooltip(text) },
            rememberTooltipState(isPersistent = true),
            modifier,
            content = content,
        )
    } ?: Box(modifier) { content() }
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
