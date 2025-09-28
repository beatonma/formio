package org.beatonma.gclocks.app.data.settings

enum class ClockType {
    Form,
    Io16,
    Io18,
    ;

    companion object {
        val Default get() = Form
    }
}
