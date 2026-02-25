package org.beatonma.gclocks.app

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val Dispatchers.io: CoroutineDispatcher
    inline get() = Dispatchers.IO
