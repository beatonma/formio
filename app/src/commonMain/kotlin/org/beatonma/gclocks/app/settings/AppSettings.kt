package org.beatonma.gclocks.app.settings

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options


interface CommonAppSettings {
    var context: SettingsContext

    var clock: Clock
    var settings: Map<SettingsContext, ContextSettings>

    suspend fun getSettings(context: SettingsContext): ContextSettings =
        settings[context] ?: run {
            debug("getSettings($context) = null")
            ContextSettings()
        }

    suspend fun <O : Options<*>> saveSettings(context: SettingsContext, options: O) {
        val updated = settings.toMutableMap().apply {
            val ctx = this[context] ?: ContextSettings()

            this[context] = when (options) {
                is FormOptions -> ctx.copy(form = options)
                is Io16Options -> ctx.copy(io16 = options)
                is Io18Options -> ctx.copy(io18 = options)
                else -> throw IllegalStateException("Unhandled options class ${options::class}")
            }
        }

        this.settings = updated.toMap()
        save()
    }

    suspend fun save()

    companion object {
        enum class Clock {
            Form,
            Io16,
            Io18,
            ;
        }
    }
}

/**
 * Context in which a set of [org.beatonma.gclocks.core.options.Options] is applied.
 */
expect enum class SettingsContext

/** Per-context set of options for each clock type. */
@Serializable
data class ContextSettings(
    val form: FormOptions = FormOptions(),
    val io16: Io16Options = Io16Options(),
    val io18: Io18Options = Io18Options(),
)

expect fun loadAppSettings(): AppSettings


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class AppSettings : CommonAppSettings
