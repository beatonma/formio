@file:JvmName("RepositoryJvm")

package org.beatonma.formio.app.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

fun createDataStore(): DataStore<Preferences> = createDataStore {
    val home = System.getProperty("user.home")
    val directory = File(home, "org.beatonma.formio")

    File(directory, DataStoreFileName).path
}
