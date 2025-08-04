@file:JvmName("LoggingJvm")

package org.beatonma.gclocks.core.util

actual fun debug(content: Any?) {
    println("$content")
}

actual fun info(content: Any?) {
    println("$content")
}

actual fun warn(content: Any?) {
    println("$content")
}