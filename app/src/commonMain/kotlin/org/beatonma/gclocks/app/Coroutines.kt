package org.beatonma.gclocks.app

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


expect val Dispatchers.io: CoroutineDispatcher
