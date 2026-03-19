package org.beatonma.formio.app.data.settings

import org.beatonma.formio.clocks.whenOptions
import org.beatonma.formio.core.graphics.Color
import org.beatonma.formio.core.options.AnyOptions

enum class ClockType {
    Form,
    Io16,
    Io18,
    ;

    companion object {
        val Default = Form
    }
}


fun AnyOptions.resolveClockType(): ClockType = whenOptions(
    this,
    form = { ClockType.Form },
    io16 = { ClockType.Io16 },
    io18 = { ClockType.Io18 },
)


@Suppress("UNCHECKED_CAST")
fun <O : AnyOptions> O.copyWithColors(colors: List<Color>): O = whenOptions(
    this,
    form = { it.copy(paints = it.paints.copy(colors = colors)) as O },
    io16 = { it.copy(paints = it.paints.copy(colors = colors)) as O },
    io18 = { it.copy(paints = it.paints.copy(colors = colors)) as O },
)
