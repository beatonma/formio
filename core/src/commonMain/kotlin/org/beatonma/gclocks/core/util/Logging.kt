package org.beatonma.gclocks.core.util


inline fun <T> T.debug(label: String? = null): T {
    org.beatonma.gclocks.core.util.debug(if (label != null) "$label: $this" else "$this")
    return this
}

expect fun debug(content: Any?)
expect fun info(content: Any?)
expect fun warn(content: Any?)
