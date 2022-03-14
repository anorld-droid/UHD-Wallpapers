package com.anorlddroid.wallpapers4e.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.anorlddroid.wallpapers4e.UHDViewModel
import com.anorlddroid.wallpapers4e.ui.theme.Wallpapers4ETheme
import com.anorlddroid.wallpapers4e.ui.utils.ConnectionState
import com.anorlddroid.wallpapers4e.ui.utils.connectivityState

@Composable
fun Recent(
    state: SearchState,
    navController: NavController
) {

    val connection by connectivityState()
    val isConnected = remember { mutableStateOf(connection == ConnectionState.Available) }
    if (isConnected.value) {
        val viewModel: UHDViewModel = viewModel()
        val recentPhotos by viewModel.recentPhotos.collectAsState()
        val searchResults by viewModel.searchResults.collectAsState()
        Wallpapers4ETheme {
            Scaffold {
                LaunchedEffect(state.query.text) {
                    state.searching = true
                    state.searchResults = searchResults
                    state.searching = false
                }
                when (state.searchDisplay) {
                    SearchDisplay.Wallpapers -> PhotosLayout(
                        recentPhotos,
                        null,
                        navController,
                        isRandomPhotos = false
                    )
                    SearchDisplay.Results -> PhotosLayout(
                        photos = null,
                        searchResults = state.searchResults,
                        navController = navController,
                        isRandomPhotos = false
                    )
                    SearchDisplay.NoResults -> NoResults(state.query.text)
                    else -> Column(modifier = Modifier.fillMaxSize()) {}
                }
            }
        }
    } else {
        NetworkError(error = "No internet Connection")
    }
}

