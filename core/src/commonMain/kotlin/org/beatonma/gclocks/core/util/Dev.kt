package org.beatonma.gclocks.core.util

import org.beatonma.gclocks.core.Build
import kotlin.time.Duration
import kotlin.time.measureTime

inline fun debug(enabled: Boolean = true, block: () -> Unit) {
    if (enabled && Build.isDebug) {
        run(block)
    }
}

inline fun <T> debugValue(debugValue: () -> T, normalValue: () -> T): T {
    return if (Build.isDebug) debugValue() else normalValue()
}


inline fun debugMeasureTime(label: String?, block: () -> Unit): Duration {
    if (!Build.isDebug) {
        block()
        return Duration.ZERO
    }

    val duration = measureTime(block)
    debug("$label: ${duration.inWholeMicroseconds}us")
    return duration
}
