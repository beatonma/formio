package org.beatonma.gclocks.core.util

import kotlin.random.Random as KotlinRandom


class ActualRandom(private val delegate: KotlinRandom) : Random {
    override fun nextInt(): Int = delegate.nextInt()
}

actual fun getRandom(seed: Int?): Random =
    ActualRandom(
        if (seed == null) KotlinRandom.Default
        else KotlinRandom(seed)
    )
