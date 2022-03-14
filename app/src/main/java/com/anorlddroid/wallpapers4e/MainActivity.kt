package com.anorlddroid.wallpapers4e

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.anorlddroid.wallpapers4e.data.database.DataGenerator
import com.anorlddroid.wallpapers4e.data.database.UHDDatabase
import com.anorlddroid.wallpapers4e.data.repository.Repository
import com.anorlddroid.wallpapers4e.ui.utils.ConnectionAlertDialog
import com.anorlddroid.wallpapers4e.ui.utils.ConnectionState
import com.anorlddroid.wallpapers4e.ui.utils.currentConnectionState
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch


const val DATABASE_CREATED: String = "Database created"
const val PHOTOS_DOWNLOADED: String = "Downloaded photos"

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this
        lifecycleScope.launch {
            val dbInstance = UHDDatabase.getDatabase(context)
            val repository = Repository(dbInstance)
            PreferenceManager.getDefaultSharedPreferences(context).apply {
                if (!getBoolean(DATABASE_CREATED, false)) {
                    dbInstance.settingsDao().insert(
                        DataGenerator.insertSettings()[0],
                    )
                    PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                        putBoolean(DATABASE_CREATED, true).apply()
                    }
                }
            }
            PreferenceManager.getDefaultSharedPreferences(context).apply {
                if (!getBoolean(PHOTOS_DOWNLOADED, false)) {
                    if (application.baseContext.currentConnectionState == ConnectionState.Available) {
                        repository.downloadRandomPhotos()
                        repository.downloadRecentPhotos()
                        PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                            putBoolean(PHOTOS_DOWNLOADED, true).apply()
                        }
                    }
                }
            }
        }
        // This app draws behind the system bars, so we want to handle fitting system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            UHDWallpapersApp(finishActivity = { finish() }, context = this)
            ConnectionAlertDialog(finishActivity = { finish() })
        }
    }
}



