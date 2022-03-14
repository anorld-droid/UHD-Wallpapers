package com.anorlddroid.wallpapers4e.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.anorlddroid.wallpapers4e.UHDViewModel
import com.anorlddroid.wallpapers4e.data.network.APIResult
import com.anorlddroid.wallpapers4e.data.network.APIStatus
import com.anorlddroid.wallpapers4e.data.network.pojo.UnsplashPhoto
import com.anorlddroid.wallpapers4e.ui.components.VerticalGrid
import com.anorlddroid.wallpapers4e.ui.theme.Wallpapers4ETheme
import com.anorlddroid.wallpapers4e.ui.utils.ConnectionState
import com.anorlddroid.wallpapers4e.ui.utils.connectivityState
import com.anorlddroid.wallpapers4e.ui.utils.currentConnectionState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun Random(
    state: SearchState,
    navController: NavController
) {

    val connection by connectivityState()
    val isConnected = remember { mutableStateOf(connection == ConnectionState.Available) }
    if (isConnected.value) {
        val viewModel: UHDViewModel = viewModel()
        val randomPhotos by viewModel.randomPhotos.collectAsState()
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
                        randomPhotos, null, navController, isRandomPhotos = true
                    )
                    SearchDisplay.Results -> PhotosLayout(
                        photos = null,
                        searchResults = state.searchResults,
                        navController = navController,
                        isRandomPhotos = true
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

@Composable
fun PhotosLayout(
    photos: APIResult<List<UnsplashPhoto>>?,
    searchResults: APIResult<List<UnsplashPhoto>>?,
    navController: NavController,
    isRandomPhotos: Boolean,
) {
    val viewModel: UHDViewModel = viewModel()
    val context = LocalContext.current
    val result = searchResults ?: photos
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    var refresh = rememberSwipeRefreshState(isRefreshing)
    Box(modifier = Modifier.fillMaxSize()) {
        SwipeRefresh(
            state = refresh,
            onRefresh = {
                if (context.currentConnectionState == ConnectionState.Available) {
                    if (isRandomPhotos) viewModel.refreshRandomPhotos()
                    else viewModel.refreshRecentPhotos()

                }
            },
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    scale = true,
                    backgroundColor = MaterialTheme.colors.secondary,
                    contentColor = MaterialTheme.colors.background
                )

            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (result != null) {
                    when (result.status) {
                        APIStatus.SUCCESS -> {
                            LazyColumn {
                                item {
                                    Column(modifier = Modifier.padding(2.dp)) {
                                        VerticalGrid(
                                            Modifier.padding(horizontal = 1.dp),
                                            columns = 3
                                        ) {
                                            refresh = rememberSwipeRefreshState(isRefreshing = true)
                                            result.data?.asReversed()?.forEach { category ->
                                                ImageLayout(category, navController)
                                            }
                                            refresh =
                                                rememberSwipeRefreshState(isRefreshing = false)
                                        }
                                        Spacer(Modifier.height(2.dp))
                                    }
                                }
                            }
                        }
                        APIStatus.ERROR -> {
                            result.message?.let { NetworkError(error = it) }
                        }
                        APIStatus.LOADING -> {
                            refresh = rememberSwipeRefreshState(isRefreshing = true)
                        }
                    }
                }
            }
        }
    }
}