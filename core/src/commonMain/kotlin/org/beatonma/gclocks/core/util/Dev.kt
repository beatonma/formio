package org.beatonma.gclocks.core.util

import org.beatonma.gclocks.core.Build

inline fun debug(block: () -> Unit) {
    if (Build.isDebug) {
        run(block)
    }
}
