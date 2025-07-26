@file:JvmName("TimeJvmKt")

package org.beatonma.gclocks.core.util

import java.time.LocalDateTime

actual fun getTime(): TimeOfDay {
    val now = LocalDateTime.now()
    return object : TimeOfDay {
        override val hour = now.hour
        override val minute = now.minute
        override val second = now.second
    }
}