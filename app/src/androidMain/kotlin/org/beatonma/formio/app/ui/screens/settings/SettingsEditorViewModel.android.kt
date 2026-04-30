package org.beatonma.formio.app.ui.screens.settings

import formio.app.generated.resources.Res
import formio.app.generated.resources.setting_help_lwp_launcher_pages
import formio.app.generated.resources.setting_lwp_launcher_pages
import formio.app.generated.resources.setting_lwp_launcher_pages_all
import formio.app.generated.resources.setting_placeholder_lwp_launcher_pages
import formio.app.generated.resources.widget_alarm_permission_request_description
import formio.app.generated.resources.widget_alarm_permission_request_title
import org.beatonma.formio.app.data.AppSettingsRepository
import org.beatonma.formio.app.data.settings.DisplayContext
import org.beatonma.formio.app.data.settings.GlobalOptions
import org.beatonma.formio.app.data.settings.Key
import org.beatonma.formio.app.data.settings.RichSetting
import org.beatonma.formio.app.data.settings.RichSettings
import org.beatonma.formio.app.data.settings.SettingKey
import org.beatonma.formio.app.data.settings.ValidationFailed
import org.beatonma.formio.app.data.settings.chooseClockColors
import org.beatonma.formio.app.data.settings.chooseClockPosition
import org.beatonma.formio.app.data.settings.replace


actual class SettingsEditorViewModel actual constructor(
    repository: AppSettingsRepository,
    onSave: (() -> Unit)?,
) : AbstractSettingsEditorViewModel(repository, onSave) {
    companion object {
        val WidgetAlarmPermission = Key.Action("test_widget_alarm_permission")
    }

    var shouldShowWidgetPermissionRequest: Boolean = true
        set(value) {
            field = value
            refreshRichSettings()
        }

    override fun addDisplaySettings(
        settings: RichSettings,
        displayContextOptions: DisplayContext.Options,
        updateDisplayContextOptions: (DisplayContext.Options) -> Unit,
        globalOptions: GlobalOptions,
        updateGlobalOptions: (GlobalOptions) -> Unit,
    ): RichSettings =
        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        when (displayContextOptions) {
            is DisplayContext.Options.Screensaver -> addScreensaverDisplaySettings(
                settings,
                displayContextOptions,
                updateDisplayContextOptions,
                globalOptions,
                updateGlobalOptions
            )

            is DisplayContext.Options.Wallpaper -> addWallpaperDisplaySettings(
                settings,
                displayContextOptions,
                updateDisplayContextOptions,
                globalOptions,
                updateGlobalOptions
            )

            is DisplayContext.Options.Widget -> addWidgetDisplaySettings(
                settings,
                displayContextOptions,
                updateDisplayContextOptions,
                globalOptions,
                updateGlobalOptions
            )

            else -> super.addDisplaySettings(
                settings,
                displayContextOptions,
                updateDisplayContextOptions,
                globalOptions,
                updateGlobalOptions
            )
        }

    private fun addWallpaperDisplaySettings(
        settings: RichSettings,
        displayContextOptions: DisplayContext.Options.Wallpaper,
        updateDisplayContextOptions: (DisplayContext.Options.Wallpaper) -> Unit,
        globalOptions: GlobalOptions,
        updateGlobalOptions: (GlobalOptions) -> Unit,
    ): RichSettings =
        settings.copy(
            colors = settings.colors.replace(SettingKey.clockColors) { previous ->
                previous as RichSetting.ClockColors
                chooseClockColors(
                    value = previous.value.copy(background = displayContextOptions.backgroundColor),
                    onValueChange = { updated ->
                        updated.background?.let {
                            updateDisplayContextOptions(
                                displayContextOptions.copy(
                                    backgroundColor = it
                                )
                            )
                        }
                        previous.onValueChange(updated)
                    },
                    palettes = globalOptions.colorPalettes,
                    onUpdatePalettes = { updateGlobalOptions(globalOptions.copy(colorPalettes = it)) }
                )
            },
            layout = listOf(
                chooseClockPosition(displayContextOptions.position) {
                    updateDisplayContextOptions(displayContextOptions.copy(position = it))
                },
                chooseLwpLauncherPages(displayContextOptions.visibleOnLauncherPages) {
                    updateDisplayContextOptions(
                        displayContextOptions.copy(
                            visibleOnLauncherPages = it
                        )
                    )
                }
            ) + settings.layout,
        )


    private fun addScreensaverDisplaySettings(
        settings: RichSettings,
        displayContextOptions: DisplayContext.Options.Screensaver,
        updateDisplayContextOptions: (DisplayContext.Options.Screensaver) -> Unit,
        globalOptions: GlobalOptions,
        updateGlobalOptions: (GlobalOptions) -> Unit,
    ): RichSettings =
        settings.copy(
            colors = settings.colors.replace(
                SettingKey.clockColors,
                { previous ->
                    previous as RichSetting.ClockColors
                    chooseClockColors(
                        value = previous.value.copy(background = displayContextOptions.backgroundColor),
                        onValueChange = { updated ->
                            updated.background?.let {
                                updateDisplayContextOptions(
                                    displayContextOptions.copy(
                                        backgroundColor = it
                                    )
                                )
                            }
                            previous.onValueChange(updated)
                        },
                        palettes = globalOptions.colorPalettes,
                        onUpdatePalettes = {
                            updateGlobalOptions(
                                globalOptions.copy(
                                    colorPalettes = it
                                )
                            )
                        })
                },
            ),
            layout = listOf(
                chooseClockPosition(displayContextOptions.position) {
                    updateDisplayContextOptions(displayContextOptions.copy(position = it))
                },
            ) + settings.layout,
        )

    private fun addWidgetDisplaySettings(
        settings: RichSettings,
        displayContextOptions: DisplayContext.Options.Widget,
        updateDisplayContextOptions: (DisplayContext.Options.Widget) -> Unit,
        globalOptions: GlobalOptions,
        updateGlobalOptions: (GlobalOptions) -> Unit,
    ): RichSettings = settings.copy(
        core = when (shouldShowWidgetPermissionRequest) {
            true -> listOf(widgetAlarmPermission()) + settings.core
            false -> settings.core
        }
    )
}


private val WallpaperLauncherPagesKey = Key.IntList("lwp_launcher_pages")
private fun chooseLwpLauncherPages(value: List<Int>, onUpdate: (List<Int>) -> Unit) =
    RichSetting.IntList(
        key = WallpaperLauncherPagesKey,
        name = Res.string.setting_lwp_launcher_pages,
        helpText = Res.string.setting_help_lwp_launcher_pages,
        placeholder = Res.string.setting_placeholder_lwp_launcher_pages,
        defaultValueDescription = Res.string.setting_lwp_launcher_pages_all,
        value = value,
        onValueChange = onUpdate,
        validator = { page ->
            if (page < 1) {
                throw ValidationFailed("Launcher pages must be positive integers")
            }
        }
    )


private fun widgetAlarmPermission() = RichSetting.ActionCard(
    key = SettingsEditorViewModel.WidgetAlarmPermission,
    name = Res.string.widget_alarm_permission_request_title,
    helpText = Res.string.widget_alarm_permission_request_description,
)