package org.beatonma.gclocks.app.data

import org.beatonma.gclocks.app.data.settings.AppSettings
import org.beatonma.gclocks.app.data.settings.ClockType
import org.beatonma.gclocks.app.data.settings.ContextClockOptions
import org.beatonma.gclocks.app.data.settings.DisplayContextDefaults
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.toColor
import org.beatonma.gclocks.core.options.AnyOptions
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.TimeFormat


internal data class ClockSearchParams(
    val clock: ClockType?,
    val background: Color?,
    val colors: List<Color>?,
    val format: TimeFormat?,
    val layout: Layout?,
) {
    companion object {
        private const val ClockKey = "clock"
        private const val BackgroundKey = "background"
        private const val ColorKey = "colors"
        private const val LayoutKey = "layout"
        private const val FormatKey = "format"

        fun fromString(search: String): ClockSearchParams? {
            try {
                val map: Map<String, String> = search.removePrefix("?")
                    .split("&")
                    .mapNotNull { param ->
                        param.split("=").let { parts ->
                            when (parts.size) {
                                2 -> parts
                                else -> null
                            }
                        }
                    }
                    .associate { it[0] to decodeURIComponent(it[1]) }


                return ClockSearchParams(
                    clock = map[ClockKey]?.let { enumValueOrNull<ClockType>(it) },
                    background = map[BackgroundKey]?.toColor(),
                    colors = map[ColorKey]?.split(",")?.map(String::toColor),
                    format = map[FormatKey]?.let { enumValueOrNull<TimeFormat>(it) },
                    layout = map[LayoutKey]?.let { enumValueOrNull<Layout>(it) }
                )
            } catch (e: Exception) {
                return null
            }
        }
    }
}

internal fun mergeSettings(
    appSettings: AppSettings,
    searchParams: ClockSearchParams?
): AppSettings {
    if (searchParams == null) return appSettings

    return appSettings.letNotNull(searchParams.clock) {
        appSettings.copyWithClock(it)
    }.let {
        val merged = mergeOptions(it.contextOptions, searchParams)
        it.copyWithOptions(
            clockOptions = merged.clockOptions,
            displayOptions = merged.displayOptions
        )
    }
}

private fun <T, V> T.letNotNull(value: V?, block: T.(V) -> T): T {
    return when (value) {
        null -> this
        else -> block(value)
    }
}

private inline fun <reified E : Enum<E>> enumValueOrNull(key: String): E? =
    enumValues<E>().find { it.name.equals(key, ignoreCase = true) }

external fun decodeURIComponent(uri: String): String


private fun <O : AnyOptions> mergeOptions(
    options: ContextClockOptions<O>,
    custom: ClockSearchParams
): ContextClockOptions<O> {
    return options.copy(
        clockOptions = options.clockOptions.merge(custom),
        displayOptions = DisplayContextDefaults.WithBackground(
            custom.background ?: DisplayContextDefaults.DefaultBackgroundColor,
        )
    )
}

@Suppress("UNCHECKED_CAST")
private fun <O : AnyOptions> O.merge(custom: ClockSearchParams): O =
    copy(
        layout = layout.copy(
            layout = custom.layout ?: layout.layout,
            format = custom.format ?: layout.format
        ),
        paints = paints.copy(
            colors = mergeColors(paints.colors, custom.colors)
        )
    ) as O

private fun mergeColors(initial: List<Color>, new: List<Color>?): List<Color> {
    if (new == null) return initial
    if (new.size < initial.size) return initial
    return new.take(initial.size)
}
