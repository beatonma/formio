package org.beatonma.formio.app.ui.screens.settings.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import org.beatonma.formio.app.data.settings.Key
import org.beatonma.formio.app.data.settings.RichSetting
import org.beatonma.formio.app.ui.components.CardContent
import org.beatonma.formio.app.ui.resolve
import org.beatonma.formio.app.ui.theme.tokens.CardTokens
import org.jetbrains.compose.resources.StringResource


@Immutable
class SettingAction(
    val buttonText: StringResource,
    val onClick: () -> Unit,
)

typealias SettingActions = Map<Key.Action, SettingAction>

val LocalSettingActions: ProvidableCompositionLocal<SettingActions> =
    staticCompositionLocalOf { error("LocalSettingActions is not provided") }


@Composable
fun ActionCard(
    setting: RichSetting.ActionCard,
    modifier: Modifier = Modifier,
    actions: SettingActions = LocalSettingActions.current,
) {
    val action: SettingAction = remember(setting.key) {
        actions[setting.key] ?: error("ActionCard ${setting.key} has no registered action")
    }

    ActionCard(setting.name, setting.helpText, action, modifier)
}

@Composable
fun ActionCard(
    name: StringResource,
    helpText: StringResource,
    action: SettingAction,
    modifier: Modifier = Modifier,
) {
    Card(
        name.resolve(),
        helpText.resolve(),
        modifier,
        actions = {
            TextButton(action.onClick) {
                Text(action.buttonText.resolve())
            }
        }
    )
}

@Composable
fun InfoCard(
    setting: RichSetting.InfoCard,
    modifier: Modifier = Modifier,
) {
    InfoCard(setting.name, setting.helpText, modifier)
}

@Composable
fun InfoCard(
    name: StringResource,
    helpText: StringResource,
    modifier: Modifier = Modifier,
) {
    Card(name.resolve(), helpText.resolve(), modifier)
}

@Composable
private fun Card(
    title: String,
    supportingText: String,
    modifier: Modifier,
    actions: @Composable (RowScope.() -> Unit)? = null,
) {
    Card(modifier.fillMaxWidth().padding(bottom = CardTokens.BetweenCardsPadding)) {
        CardContent(
            title = title,
            supportingText = supportingText,
            actions = actions,
        )
    }
}