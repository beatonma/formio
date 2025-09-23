package org.beatonma.gclocks.app

import kotlinx.browser.localStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.beatonma.gclocks.app.settings.AppSettings
import org.beatonma.gclocks.app.settings.DefaultAppSettings
import org.beatonma.gclocks.app.settings.DisplayContext
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.enums.enumEntries

class WebSettingsRepository : AppSettingsRepository {
    private val stateKey = AppSettingsRepository.Keys.AppState
    private fun contextKey(context: DisplayContext) = AppSettingsRepository.Keys.settingKey(context)

    override fun loadAppSettings(): Flow<AppSettings> {
        return flow {
            emit(
                try {
                    AppSettings(
                        state = deserialize(localStorage[stateKey]!!),
                        settings = enumEntries<DisplayContext>().associateWith {
                            deserialize(localStorage[contextKey(it)]!!)
                        }
                    )
                } catch (e: Exception) {
                    DefaultAppSettings
                }
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
