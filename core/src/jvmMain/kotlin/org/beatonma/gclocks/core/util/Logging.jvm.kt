@file:JvmName("LoggingJvm")

package org.beatonma.gclocks.core.util

fun <T> T.dump(label: String? = null): T {
    debug(if (label != null) "$label: $this" else "$this")
    return this
}

actual fun debug(content: Any?) {
    println("$content")
}

actual fun info(content: Any?) {
    println("$content")
}

actual fun warn(content: Any?) {
    println("$content")
}