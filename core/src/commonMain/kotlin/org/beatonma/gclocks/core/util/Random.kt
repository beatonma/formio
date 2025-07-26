package org.beatonma.gclocks.core.util

interface Random {
    fun nextInt(): Int
}

expect fun getRandom(seed: Int? = null): Random