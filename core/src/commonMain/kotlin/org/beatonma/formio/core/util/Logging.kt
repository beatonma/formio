package org.beatonma.formio.core.util


fun <T> T.dump(label: String? = null): T {
    debug(if (label != null) "$label: $this" else "$this")
    return this
}

expect fun debug(content: Any?)
expect fun info(content: Any?)
expect fun warn(content: Any?)
