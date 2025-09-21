package org.beatonma.gclocks.compose

enum class Platform {
    Android,
    Desktop,
    Web,
    ;
}

expect val platform: Platform
