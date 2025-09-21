package org.beatonma.gclocks.app

import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.beatonma.gclocks.app.settings.AppSettings
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.core.util.debug
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.enums.enumEntries

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        val factory = remember {
            AppViewModelFactory(
                repository = WebSettingsRepository()
            )
        }

        val viewmodel: AppViewModel = viewModel(factory = factory)

        App(viewmodel)
    }
}


private class WebSettingsRepository : AppSettingsRepository {
    private val stateKey = AppSettingsRepository.Keys.AppState
    private fun contextKey(context: DisplayContext) = AppSettingsRepository.Keys.settingKey(context)

    override fun loadAppSettings(): Flow<AppSettings> {
        debug("loadAppSettings")
        return flow {
            debug("loadAppSettings flow")
            emit(
                AppSettings(
                    state = deserialize(localStorage[stateKey]!!),
                    settings = enumEntries<DisplayContext>().associateWith {
                        deserialize(localStorage[contextKey(it)]!!)
                    }
                )
            )
        }
    }

    override suspend fun save(appSettings: AppSettings) {
        localStorage[stateKey] = serialize(appSettings.state)
        enumEntries<DisplayContext>().forEach {
            localStorage[contextKey(it)] = serialize(appSettings.settings[it])
        }
    }

    override suspend fun save(key: String, value: String) {
        localStorage[key] = value
    }

    override fun loadString(key: String): Flow<String?> {
        return flow { localStorage[key] }
    }

    override suspend fun forgetString(key: String) {
        localStorage.removeItem(key)
    }
}
