package org.beatonma.formio.app.ui.screens.settings

import org.beatonma.formio.app.data.AppSettingsRepository

actual class SettingsEditorViewModel actual constructor(
    repository: AppSettingsRepository,
    onSave: (() -> Unit)?,
) : AbstractSettingsEditorViewModel(repository, onSave)
