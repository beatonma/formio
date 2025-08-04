package org.beatonma.gclocks.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


@Composable
fun lifecycleAwareEffect(
    context: CoroutineContext,
    scope: CoroutineScope,
    lifecycleOwner: LifecycleOwner,
    keys: Array<out Any> = arrayOf(Unit),
    onStop: (() -> Unit)?,
    block: suspend CoroutineScope.() -> Unit,
): CoroutineScope {
    var job: Job? by remember { mutableStateOf(null) }

    DisposableEffect(*keys) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME -> {
                    if (job?.isActive != true) {
                        job = scope.launch(context) {
                            block()
                        }
                    }
                }

                Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_DESTROY -> {
                    job?.cancel()
                    job = null
                    onStop?.invoke()
                }

                else -> {

                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            job?.cancel()
            job = null
            lifecycleOwner.lifecycle.removeObserver(observer)
            onStop?.invoke()
        }
    }

    return scope
}
