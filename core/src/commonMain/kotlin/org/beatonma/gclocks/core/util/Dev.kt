package org.beatonma.gclocks.core.util

import org.beatonma.gclocks.core.Build
import kotlin.time.measureTime

inline fun debug(block: () -> Unit) {
    if (Build.isDebug) {
        run(block)
    }
}


inline fun debugMeasureTime(label: String?, block: () -> Unit) {
    if (!Build.isDebug) {
        block()
        return
    }

    val duration = measureTime(block)
    debug("$label: ${duration.inWholeMicroseconds}us")
}