package org.beatonma.formio.compose.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.beatonma.formio.app.theme.tokens.ColumnTokens
import org.beatonma.formio.app.theme.tokens.RowTokens

internal object Row {
    val SmallSpacingArrangement = Arrangement.spacedBy(RowTokens.SmallSpacing)
    val MediumSpacingArrangement = Arrangement.spacedBy(RowTokens.MediumSpacing)
    val LargeSpacingArrangement = Arrangement.spacedBy(RowTokens.LargeSpacing)

    internal fun smallSpacingArrangement(alignment: Alignment.Horizontal) =
        Arrangement.spacedBy(RowTokens.SmallSpacing, alignment)

    internal fun mediumSpacingArrangement(alignment: Alignment.Horizontal) =
        Arrangement.spacedBy(RowTokens.MediumSpacing, alignment)

    internal fun largeSpacingArrangement(alignment: Alignment.Horizontal) =
        Arrangement.spacedBy(RowTokens.LargeSpacing, alignment)
}

internal object Column {
    val SmallSpacingArrangement = Arrangement.spacedBy(ColumnTokens.SmallSpacing)
    val MediumSpacingArrangement = Arrangement.spacedBy(ColumnTokens.MediumSpacing)
    val LargeSpacingArrangement = Arrangement.spacedBy(ColumnTokens.LargeSpacing)

    internal fun smallSpacingArrangement(alignment: Alignment.Vertical) =
        Arrangement.spacedBy(ColumnTokens.SmallSpacing, alignment)

    internal fun mediumSpacingArrangement(alignment: Alignment.Vertical) =
        Arrangement.spacedBy(ColumnTokens.MediumSpacing, alignment)

    internal fun largeSpacingArrangement(alignment: Alignment.Vertical) =
        Arrangement.spacedBy(ColumnTokens.LargeSpacing, alignment)
}


/**
 * A LazyRow which can be scrolled with a mouse on desktop environments.
 */
@Composable
internal fun ScrollingRow(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    scope: CoroutineScope = rememberCoroutineScope(),
    content: LazyListScope.() -> Unit,
) {
    LazyRow(
        modifier.draggable(
            orientation = Orientation.Horizontal,
            state = rememberDraggableState { delta ->
                scope.launch {
                    state.scrollBy(-delta)
                }
            },
        ),
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement,
        state = state,
        contentPadding = contentPadding,
        content = content
    )
}

/**
 * A LazyColumn which can be scrolled with a mouse on desktop environments.
 */
@Composable
internal fun ScrollingColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scope: CoroutineScope = rememberCoroutineScope(),
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier.draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                scope.launch {
                    state.scrollBy(-delta)
                }
            },
        ),
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}
