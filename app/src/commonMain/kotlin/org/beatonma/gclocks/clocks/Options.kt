package org.beatonma.gclocks.clocks

import org.beatonma.gclocks.app.data.settings.ContextClockOptions
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.LayoutOptions
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.form.FormLayoutOptions
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16LayoutOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18LayoutOptions
import org.beatonma.gclocks.io18.Io18Options


inline fun <O : Options<*>, R> whenOptions(
    options: O,
    form: (formOptions: FormOptions) -> R,
    io16: (io16Options: Io16Options) -> R,
    io18: (io18Options: Io18Options) -> R,
): R {
    return when (options) {
        is FormOptions -> form(options)
        is Io16Options -> io16(options)
        is Io18Options -> io18(options)
        else -> throw IllegalArgumentException("Unhandled Options class ${options::class}")
    }
}


inline fun <O : Options<*>, R> whenOptions(
    options: ContextClockOptions<O>,
    form: (ContextClockOptions<FormOptions>) -> R,
    io16: (ContextClockOptions<Io16Options>) -> R,
    io18: (ContextClockOptions<Io18Options>) -> R,
): R {
    @Suppress("UNCHECKED_CAST")
    return when (options.clockOptions) {
        is FormOptions -> form(options as ContextClockOptions<FormOptions>)
        is Io16Options -> io16(options as ContextClockOptions<Io16Options>)
        is Io18Options -> io18(options as ContextClockOptions<Io18Options>)
        else -> throw IllegalArgumentException("Unhandled ContextClockOptions class ${options::class}")
    }
}


inline fun <reified T : LayoutOptions> layoutOptions(
    layout: Layout? = null,
    format: TimeFormat? = null,
    spacingPx: Int? = null,
    horizontalAlignment: HorizontalAlignment? = null,
    verticalAlignment: VerticalAlignment? = null,
    secondsGlyphScale: Float? = null,
): T {
    return when (T::class) {
        FormLayoutOptions::class -> {
            val defaults = FormLayoutOptions()
            FormLayoutOptions(
                layout = layout ?: defaults.layout,
                format = format ?: defaults.format,
                spacingPx = spacingPx ?: defaults.spacingPx,
                horizontalAlignment = horizontalAlignment ?: defaults.horizontalAlignment,
                verticalAlignment = verticalAlignment ?: defaults.verticalAlignment,
                secondsGlyphScale = secondsGlyphScale ?: defaults.secondsGlyphScale
            )
        }

        Io16LayoutOptions::class -> {
            val defaults = Io16LayoutOptions()
            Io16LayoutOptions(
                layout = layout ?: defaults.layout,
                format = format ?: defaults.format,
                spacingPx = spacingPx ?: defaults.spacingPx,
                horizontalAlignment = horizontalAlignment ?: defaults.horizontalAlignment,
                verticalAlignment = verticalAlignment ?: defaults.verticalAlignment,
                secondsGlyphScale = secondsGlyphScale ?: defaults.secondsGlyphScale
            )
        }

        Io18LayoutOptions::class -> {
            val defaults = Io18LayoutOptions()
            Io18LayoutOptions(
                layout = layout ?: defaults.layout,
                format = format ?: defaults.format,
                spacingPx = spacingPx ?: defaults.spacingPx,
                horizontalAlignment = horizontalAlignment ?: defaults.horizontalAlignment,
                verticalAlignment = verticalAlignment ?: defaults.verticalAlignment,
                secondsGlyphScale = secondsGlyphScale ?: defaults.secondsGlyphScale
            )
        }

        else -> throw IllegalArgumentException("Unhandled LayoutOptions class ${T::class}")
    } as T
}
