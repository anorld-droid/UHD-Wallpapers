package com.anorlddroid.wallpapers4e

import android.annotation.SuppressLint
import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.anorlddroid.wallpapers4e.data.database.CategoryEntity
import com.anorlddroid.wallpapers4e.data.database.SettingsEntity
import com.anorlddroid.wallpapers4e.data.database.UHDDatabase
import com.anorlddroid.wallpapers4e.data.network.APIResult
import com.anorlddroid.wallpapers4e.data.network.pojo.UnsplashPhoto
import com.anorlddroid.wallpapers4e.data.repository.Repository
import com.anorlddroid.wallpapers4e.ui.details.setWallpaper
import com.anorlddroid.wallpapers4e.ui.utils.ConnectionState
import com.anorlddroid.wallpapers4e.ui.utils.currentConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.util.Collections.emptyList

class UHDViewModel(application: Application) : AndroidViewModel(application) {
    //initialze the repository
    private var repository: Repository =
        Repository(UHDDatabase.getDatabase(application))

    // Holds our currently selected category
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?>
        get() = _selectedCategory

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>>
        get() = _categories

    //Holds dark/light/auto theme states from the database
    private val _themeState = MutableStateFlow("")
    val themeState: StateFlow<String>
        get() = _themeState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing

    private val _randomPhotos = MutableStateFlow<APIResult<List<UnsplashPhoto>>?>(null)
    val randomPhotos: StateFlow<APIResult<List<UnsplashPhoto>>?>
        get() = _randomPhotos

    private val _recentPhotos = MutableStateFlow<APIResult<List<UnsplashPhoto>>?>(null)
    val recentPhotos: StateFlow<APIResult<List<UnsplashPhoto>>?>
        get() = _recentPhotos

    private val _categoryPhotos = MutableStateFlow<APIResult<List<UnsplashPhoto>>?>(null)
    val categoryPhotos: StateFlow<APIResult<List<UnsplashPhoto>>?>
        get() = _categoryPhotos

    private val _searchResults = MutableStateFlow<APIResult<List<UnsplashPhoto>>?>(null)
    val searchResults: StateFlow<APIResult<List<UnsplashPhoto>>?>
        get() = _searchResults

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _themeState.value = repository.getSetting("Theme")
            repository.getAllCategories().collect { categories ->
                if (categories.isNotEmpty() && _selectedCategory.value == null) {
                    repository.searchPhoto(categories[0]).collectLatest { categoryPhotos ->
                        _categoryPhotos.value = categoryPhotos
                    }
                    _selectedCategory.value = categories[0]
                }
                _categories.value = categories
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            PreferenceManager.getDefaultSharedPreferences(application).apply {
                if (!getBoolean(PHOTOS_DOWNLOADED, false)) {
                    if (application.baseContext.currentConnectionState == ConnectionState.Available) {
                        repository.downloadRandomPhotos()
                        repository.downloadRecentPhotos()
                        PreferenceManager.getDefaultSharedPreferences(application).edit().apply {
                            putBoolean(PHOTOS_DOWNLOADED, true).apply()
                        }
                    }
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            delay(2000)
            repository.getRecentPhotos().collectLatest {
                _recentPhotos.value = it
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            delay(2000)
            repository.getRandomPhotos().collectLatest {
                _randomPhotos.value = it
            }
        }
    }

    fun onFilterSelected(category: String) {
        _isRefreshing.value = true
        _selectedCategory.value = category
        viewModelScope.launch {
            repository.searchPhoto(category).collectLatest {
                _categoryPhotos.value = it
            }
            _isRefreshing.value = false
        }
    }

    fun insertSetting(name: String, setting: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _themeState.value = setting
            val settings = SettingsEntity(name = name, setting = setting)
            repository.insertSetting(settings)
        }
    }

    fun insertCategory(name: String, context: Context) {
        viewModelScope.launch {
            val category = CategoryEntity(name = name)
            repository.insertCategory(category)
        }
    }

    fun searchPhoto(query: String) {
        _isRefreshing.value = true
        viewModelScope.launch {
            repository.searchPhoto(query).collect {
                _searchResults.value = it
            }
            _isRefreshing.value = false
        }
    }

    fun refreshRandomPhotos() {
        _isRefreshing.value = true
        viewModelScope.launch {
            repository.downloadRandomPhotos()
            _isRefreshing.value = false
        }
    }

    fun refreshRecentPhotos() {
        _isRefreshing.value = false
        viewModelScope.launch {
            repository.downloadRecentPhotos()
            _isRefreshing.value = false
        }
    }

    fun getDownloadLink(
        url: String,
        context: Context,
        description: String,
        setWallpaper: Boolean,
        location: Int
    ) {
        downloadImage(
            url,
            context,
            description,
            setWallpaper,
            location
        )
    }

    @SuppressLint("Range")
    private fun downloadImage(
        url: String,
        context: Context,
        description: String,
        setWallpaper: Boolean,
        location: Int
    ) {
        val directory =
            File(Environment.getExternalStorageDirectory().absolutePath + "/Pictures")
        val file =
            File(directory, url.split("/")[3].split("?")[0] + ".jpg")
        if (file.exists()) {
            if (setWallpaper) {
                val streamIn = FileInputStream(file)
                val bitmap: Bitmap = BitmapFactory.decodeStream(streamIn)
                setWallpaper(
                    context,
                    bitmap,
                    location
                )
                streamIn.close()
            } else {
                Toast.makeText(context, "File already exists", Toast.LENGTH_LONG).show()
            }
        } else {
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val downloadUri = Uri.parse(url)

            val request = DownloadManager.Request(downloadUri).apply {
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(url.split("/")[3].split("?")[0])
                    .setDescription(description)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_PICTURES,
                        url.split("/")[3].split("?")[0] + ".jpg"
                    )

            }

            val downloadId = downloadManager.enqueue(request)
            val query = DownloadManager.Query().setFilterById(downloadId)

            viewModelScope.launch(Dispatchers.IO) {
                var lastMsg = ""
                var downloading = true
                while (downloading) {
                    val cursor: Cursor = downloadManager.query(query)
                    cursor.moveToFirst()
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false
                    }
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val msg: String =
                        statusMessage(url, File(Environment.DIRECTORY_PICTURES), status)
                    Log.e("DownloadManager", " Status is :$msg")
                    if (msg != lastMsg) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            delay(2000)
                            if (msg.contains("successfully") && setWallpaper) {
                                val streamIn = FileInputStream(file)
                                val bitmap: Bitmap = BitmapFactory.decodeStream(streamIn)
                                setWallpaper(
                                    context,
                                    bitmap,
                                    location
                                )
                                streamIn.close()
                            }
                        }
                        lastMsg = msg ?: ""
                    }
                    cursor.close()
                }
            }
        }
    }

    private fun statusMessage(url: String, directory: File, status: Int): String {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download has  failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully to $directory" + File.separator + url.split(
                "/"
            )[3].split("?")[0] + ".jpg"
            else -> "There's nothing to download"
        }
        return msg
    }
}

