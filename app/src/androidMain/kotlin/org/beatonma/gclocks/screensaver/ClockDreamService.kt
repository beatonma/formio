package org.beatonma.gclocks.screensaver

import android.content.Intent
import android.service.dreams.DreamService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.lifecycleScope
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.app.settings.settingsRepository
import org.beatonma.gclocks.core.options.Options

/**
 * LifecycleOwner implementation lifted from androidx.lifecycle.LifecycleService
 * (which we are unable to use directly because we must extend DreamService)
 */
class ClockDreamService : DreamService(), LifecycleOwner, SavedStateRegistryOwner {
    @OptIn(ExperimentalCoroutinesApi::class)
    private lateinit var settings: Flow<ContextClockOptions<*>>

    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private val dispatcher = ServiceLifecycleDispatcher(this)
    override val lifecycle: Lifecycle get() = dispatcher.lifecycle


    override fun onCreate() {
        dispatcher.onServicePreSuperOnCreate()
        super.onCreate()
        settings = settingsRepository.load()
            .mapLatest { it.getOptions(DisplayContext.Screensaver) }

        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        dispatcher.onServicePreSuperOnStart()
    }

    override fun onDestroy() {
        dispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = true
        isScreenBright = true
        isFullscreen = true

        val view = ClockView(this)
        lifecycleScope.launch {
            settings.collectLatest { view.options = it }
        }
        setContentView(view)

//        setContentView(ComposeView(this).apply {
//            setViewTreeLifecycleOwner(this@ClockDreamService)
//            setViewTreeSavedStateRegistryOwner(this@ClockDreamService)
//            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//
//            setContent {
//                val optionsState = settings.collectAsState(FormOptions())
//                val options = optionsState.value ?: return@setContent Loading()
//
//                Box(
//                    Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Clock(options, Modifier.fillMaxSize())
//                }
//            }
//        })
    }
}