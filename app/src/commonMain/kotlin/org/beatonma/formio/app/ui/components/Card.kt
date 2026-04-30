package org.beatonma.formio.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.beatonma.formio.app.ui.theme.tokens.CardTokens

@Composable
fun CardContent(
    image: @Composable (() -> Unit)? = null,
    title: String,
    supportingText: String,
    actions: @Composable (RowScope.() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    CardContent(
        title = { MarkdownText(title) },
        supportingText = { MarkdownText(supportingText) },
        image = image,
        actions = actions,
        modifier
    )
}

@Composable
fun CardContent(
    title: @Composable () -> Unit,
    supportingText: @Composable () -> Unit,
    image: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        image?.let { image ->
            Box { image() }
        }

        Column(Modifier.fillMaxWidth().padding(CardTokens.ContentPadding)) {
            ProvideTextStyle(typography.headlineSmall) { title() }

            Spacer(Modifier.height(CardTokens.TitleSupportingTextSpacing))

            ProvideTextStyle(typography.bodyMedium) { supportingText() }

            actions?.let {
                Row(
                    Modifier.align(Alignment.End).padding(top = CardTokens.ActionButtonsPadding),
                    horizontalArrangement = Arrangement.spacedBy(CardTokens.ActionButtonsSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions
                )
            }
        }
    }
}