package org.beatonma.gclocks.app

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import org.beatonma.gclocks.app.settings.DisplayMetrics
import org.beatonma.gclocks.core.util.dump

private const val DisplayMetricsKey = "display_metrics"


@OptIn(ExperimentalCoroutinesApi::class)
fun AppSettingsRepository.loadDisplayMetrics(): Flow<DisplayMetrics> =
    loadString(DisplayMetricsKey).mapLatest { value ->
        value?.let {
            deserialize<DisplayMetrics>(it.dump("metrics str"))
        } ?: DisplayMetrics().dump("metrics default")
    }


suspend fun AppSettingsRepository.save(metrics: DisplayMetrics) =
    save(DisplayMetricsKey, serialize(metrics))
