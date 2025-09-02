@file:JvmName("DataStoreJvm")

package org.beatonma.gclocks.app.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.beatonma.gclocks.core.util.dump
import java.io.File

fun createDataStore(): DataStore<Preferences> = createDataStore {
    val home = System.getProperty("user.home")
    val directory = File(home, ".gclocks")

    File(directory, DataStoreFileName).path.dump("datastore")
}