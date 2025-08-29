package org.beatonma.gclocks.compose.components.settings

import org.beatonma.gclocks.app.LocalizedString
import kotlin.enums.EnumEntries
import org.beatonma.gclocks.core.graphics.Color as GraphicsColor

interface SettingsWrapper

@JvmInline
value class Key(val key: String)


sealed interface Setting<T> : SettingsWrapper {
    val name: String
    val localized: LocalizedString
    val helpText: LocalizedString?
    val value: T
    val onValueChange: (T) -> Unit

    val key: Key get() = createKey(name)

    fun createKey(src: String): Key = Key(
        src
            .replace(Regex("[-.\\s]"), "_")
            .replace("\\W", "")
            .lowercase()
    )

    data class Color(
        override val name: String,
        override val localized: LocalizedString = LocalizedString(literal = name),
        override val helpText: LocalizedString? = null,
        override val value: GraphicsColor,
        override val onValueChange: (GraphicsColor) -> Unit,
    ) : Setting<GraphicsColor>

    data class Colors(
        override val name: String,
        override val localized: LocalizedString = LocalizedString(literal = name),
        override val helpText: LocalizedString? = null,
        override val value: List<GraphicsColor>,
        override val onValueChange: (List<GraphicsColor>) -> Unit,
    ) : Setting<List<GraphicsColor>>

    data class SingleSelect<E : Enum<E>>(
        override val name: String,
        override val localized: LocalizedString = LocalizedString(literal = name),
        override val helpText: LocalizedString? = null,
        override val value: E,
        val values: EnumEntries<E>,
        override val onValueChange: (E) -> Unit,
    ) : Setting<E>


    data class MultiSelect<E : Enum<E>>(
        override val name: String,
        override val localized: LocalizedString = LocalizedString(literal = name),
        override val helpText: LocalizedString? = null,
        override val value: Set<E>,
        val values: EnumEntries<E>,
        override val onValueChange: (Set<E>) -> Unit,
    ) : Setting<Set<E>>

    data class Int(
        override val name: String,
        override val localized: LocalizedString = LocalizedString(literal = name),
        override val helpText: LocalizedString? = null,
        override val value: kotlin.Int,
        val default: kotlin.Int,
        val min: kotlin.Int,
        val max: kotlin.Int,
        val step: kotlin.Int = 1,
        override val onValueChange: (kotlin.Int) -> Unit,
    ) : Setting<kotlin.Int>

    data class Float(
        override val name: String,
        override val localized: LocalizedString = LocalizedString(literal = name),
        override val helpText: LocalizedString? = null,
        override val value: kotlin.Float,
        val default: kotlin.Float,
        val min: kotlin.Float,
        val max: kotlin.Float,
        val step: kotlin.Float,
        override val onValueChange: (kotlin.Float) -> Unit,
    ) : Setting<kotlin.Float>

    data class Bool(
        override val name: String,
        override val localized: LocalizedString = LocalizedString(literal = name),
        override val helpText: LocalizedString? = null,
        override val value: Boolean,
        override val onValueChange: (Boolean) -> Unit,
    ) : Setting<Boolean>


}


data class SettingsGroup(
    val name: String,
    val settings: List<SettingsWrapper>,
) : SettingsWrapper


