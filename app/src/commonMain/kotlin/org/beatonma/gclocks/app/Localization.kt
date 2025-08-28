package org.beatonma.gclocks.app

import androidx.compose.runtime.Composable
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.core.util.debug
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.enums.EnumEntries
import kotlin.reflect.KClass


@Composable
internal fun maybeStringResource(resource: StringResource?): String? =
    resource?.let { stringResource(it) }

private class LocalizationError(message: String? = null) : IllegalArgumentException(message)

data class LocalizedString(
    private val literal: String? = null,
    private val resource: StringResource? = null,
) {
    init {
        debug {
            check((literal == null && resource != null) || (literal != null && resource == null)) {
                "LocalizedString requires exactly one of `literal` or `resource` to be defined."
            }
        }
    }

    @Composable
    fun resolve(vararg formatArgs: Any): String = when {
        literal != null -> literal
        resource != null -> stringResource(resource, *formatArgs)
        else -> throw LocalizationError("Failed to resolve string")
    }
}

internal object Localization {
    /**
     * Retrieve a mapping of Enum values to translatable string resources.
     */
    @Suppress("UNCHECKED_CAST")
    val <E : Enum<E>> KClass<out E>.stringResourceMap: Map<E, LocalizedString>
        get() = when (this) {
            TimeFormat::class -> enumLiteral(TimeFormat.entries)
            Layout::class -> enumLiteral(Layout.entries)
            HorizontalAlignment::class -> enumLiteral(HorizontalAlignment.entries)
            VerticalAlignment::class -> enumLiteral(VerticalAlignment.entries)

            else -> throw IllegalArgumentException("Unhandled class $this")
        } as Map<E, LocalizedString>

    @Suppress("UNCHECKED_CAST")
    val <E : Enum<E>> KClass<out E>.helpStringResourceMap: Map<E, LocalizedString>?
        get() = when (this) {
            else -> null
        } as? Map<E, LocalizedString>

    private fun <E : Enum<E>> enumLiteral(entries: EnumEntries<E>) =
        entries.associateWith { LocalizedString(it.name, null) }
}
