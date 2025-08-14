package org.beatonma.gclocks.compose

enum class Platform {
    Android,
    Desktop,
    Ios,
    Web,
    ;
}

expect val platform: Platform