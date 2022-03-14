package com.anorlddroid.wallpapers4e.ui.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.RunningWithErrors
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.anorlddroid.wallpapers4e.MainDestinations
import com.anorlddroid.wallpapers4e.R
import com.anorlddroid.wallpapers4e.UHDViewModel
import com.anorlddroid.wallpapers4e.data.network.APIResult
import com.anorlddroid.wallpapers4e.data.network.pojo.UnsplashPhoto
import com.anorlddroid.wallpapers4e.ui.theme.AlphaNearOpaque
import com.anorlddroid.wallpapers4e.ui.theme.ThemeState
import com.anorlddroid.wallpapers4e.ui.theme.Wallpapers4ETheme
import com.anorlddroid.wallpapers4e.ui.utils.*
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.*
import com.google.gson.Gson
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@ExperimentalPagerApi
fun NavGraphBuilder.home(
    onboardingComplete: State<Boolean>, // https://issuetracker.google.com/174783110
    navController: NavHostController,
    coroutine: CoroutineScope,
    finishActivity: () -> Unit
) {
    composable(MainDestinations.HOME_DATA) { _ ->
        // Show onboarding instead if not shown yet.
        LaunchedEffect(onboardingComplete) {
            if (!onboardingComplete.value) {
                navController.navigate(MainDestinations.ONBOARDING_ROUTE)
            }
        }
        if (onboardingComplete.value) { // Avoid glitch when showing onboarding
            HomeData(
                navController = navController,
                coroutine = coroutine,
                finishActivity = finishActivity
            )
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun HomeData(
    navController: NavController,
    coroutine: CoroutineScope,
    state: SearchState = rememberSearchState(),
    finishActivity: () -> Unit
) {
    val tabs = listOf(
        HomeItem.Categories,
        HomeItem.Recent,
        HomeItem.Random
    )
    val viewModel: UHDViewModel = viewModel()
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = tabs.size)
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val makeCall = remember { mutableStateOf(false) }
    Wallpapers4ETheme {
        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetShape = RoundedCornerShape(22.dp),
            scrimColor = MaterialTheme.colors.background,
            sheetBackgroundColor = MaterialTheme.colors.background.copy(alpha = AlphaNearOpaque),
            sheetContent = {
                Column(
                    modifier = Modifier
                        .background(color = MaterialTheme.colors.background.copy(alpha = AlphaNearOpaque)),
                ) {
                    Icon(
                        imageVector = Icons.Filled.DragHandle,
                        contentDescription = "Drag the bottom sheet",
                        modifier = Modifier.align(Alignment.CenterHorizontally)

                    )
                    Text(
                        text = "Dark Mode",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 24.sp
                        ),
                        modifier = Modifier.padding(
                            top = 2.dp,
                            bottom = 4.dp,
                            start = 6.dp,
                            end = 3.dp
                        ),
                        color = MaterialTheme.colors.secondary
                    )
                    Divider(color = MaterialTheme.colors.onSecondary.copy(alpha = 0.56f))
                    val theme by viewModel.themeState.collectAsState()
                    RadioThemeGroup(
                        radioOptions = listOf("On", "Off", "Auto"),
                        theme,
                        viewModel = viewModel
                    )
                    ThemeState.selectedTheme = theme
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Request a feature",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 24.sp
                        ),
                        modifier = Modifier.padding(
                            top = 2.dp,
                            bottom = 4.dp,
                            start = 6.dp,
                            end = 3.dp
                        ),
                        color = MaterialTheme.colors.secondary
                    )
                    Divider(color = MaterialTheme.colors.onSecondary.copy(alpha = 0.56f))
                    Text(
                        text = "Twitter",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .padding(start = 30.dp, bottom = 12.dp, top = 10.dp)
                            .clickable {
                                openTwitter(context)
                            },
                        color = MaterialTheme.colors.secondary,
                        textDecoration = TextDecoration.Underline
                    )
                    Text(
                        text = "Whatsapp",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .padding(start = 30.dp, bottom = 12.dp, top = 10.dp)
                            .clickable {
                                openWhatsApp(context)
                            },
                        color = MaterialTheme.colors.secondary,
                        textDecoration = TextDecoration.Underline
                    )
                    Text(
                        text = "Phone call",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .padding(start = 30.dp, bottom = 12.dp, top = 10.dp)
                            .clickable {
                                makeCall.value = true
                            },
                        color = MaterialTheme.colors.secondary,
                        textDecoration = TextDecoration.Underline
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
                if (makeCall.value) {
                    Permission(
                        permission = Manifest.permission.CALL_PHONE,
                        permissionNotAvailableContent = {
                            Column(Modifier) {
                                Text(text = "You need to give the app Camera access to proceed.")
                                Spacer(modifier = Modifier.height(8.dp))
                                Row {
                                    TextButton(onClick = {
                                        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                            data =
                                                Uri.fromParts("package", context.packageName, null)
                                        })
                                    }) {
                                        Text(
                                            text = "Open settings",
                                            style = MaterialTheme.typography.subtitle2,
                                            color = MaterialTheme.colors.secondary
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        makePhoneCall(context)
                        makeCall.value = false
                    }
                }
            }
        ) {
            ConnectionNotification(finishActivity)
            Scaffold(
                topBar = {
                    TopBar(
                        query = state.query,
                        onQueryChange = {
                            if (context.currentConnectionState == ConnectionState.Available) {
                                state.query = it
                            }
                        },
                        onSearchFocusChange = { state.focused = it },
                        onClearQuery = { state.query = TextFieldValue("") },
                        searching = state.searching,
                        coroutineScope = coroutine,
                        bottomSheetState = bottomSheetState,
                    )
                }
            ) {
                Column {
                    Tabs(tabs = tabs, pagerState = pagerState)
                    TabsContent(
                        tabs = tabs,
                        pagerState = pagerState,
                        navController = navController,
                        coroutine = coroutine,
                        state = state,
                    )
                }
            }
        }

    }
}


@ExperimentalMaterialApi
@Composable
private fun TopBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    searching: Boolean,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
) {
    val search = remember { mutableStateOf(false) }
    Column(modifier = Modifier.statusBarsPadding())
    {
        TopAppBar(
            modifier = Modifier,
            backgroundColor = MaterialTheme.colors.background.copy(alpha = AlphaNearOpaque),
            contentColor = MaterialTheme.colors.secondary,
            actions = {
                IconButton(onClick = {
                    search.value = !search.value
                }) {
                    Icon(
                        Icons.Outlined.Search,
                        tint = MaterialTheme.colors.secondary,
                        contentDescription = null
                    )
                }
                if (!search.value) {
                    IconButton(onClick = {
                        coroutineScope.launch { bottomSheetState.show() }
                    }) {
                        Icon(
                            Icons.Outlined.AutoAwesome,
                            tint = MaterialTheme.colors.primary,
                            contentDescription = "Bottom Sheet"
                        )
                    }
                }
            },
            title = {
                if (!search.value) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.secondary,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(5.dp)
                            .weight(1f)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    Box {
                        Surface(
                            color = MaterialTheme.colors.onSurface,
                            contentColor = MaterialTheme.colors.secondary,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier
                                .height(56.dp)
                                .padding(start = 2.dp, top = 8.dp, end = 2.dp, bottom = 8.dp)
                        ) {
                            Box(Modifier.fillMaxSize()) {
                                if (query.text.isEmpty()) {
                                    SearchHint()
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentHeight()
                                ) {
                                    BasicTextField(
                                        value = query,
                                        onValueChange = onQueryChange,
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .weight(1f)
                                            .onFocusChanged {
                                                onSearchFocusChange(it.isFocused)
                                            },
                                        textStyle = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.secondary),
                                        singleLine = true,
                                        cursorBrush = Brush.horizontalGradient(
                                            colors = listOf(
                                                MaterialTheme.colors.secondary,
                                                MaterialTheme.colors.secondary
                                            )
                                        )
                                    )
                                    if (query.text.isNotEmpty()) {
                                        IconButton(onClick = onClearQuery) {
                                            Icon(
                                                imageVector = Icons.Filled.Cancel,
                                                tint = MaterialTheme.colors.primary,
                                                contentDescription = stringResource(R.string.clear),
                                                modifier = Modifier
                                                    .size(25.dp)
                                            )
                                        }
                                    }
                                    if (searching) {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.colors.primary,
                                            modifier = Modifier
                                                .padding(horizontal = 6.dp)
                                                .size(25.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            navigationIcon = {
                AppLogo()
            }
        )
    }
}

typealias ComposableFun = @Composable (navController: NavController, coroutine: CoroutineScope, state: SearchState) -> Unit

sealed class HomeItem(var title: String, var screen: ComposableFun) {
    object Categories : HomeItem("Categories", { navController, _, state ->
        Categories(state = state, navController = navController)
    })

    object Recent : HomeItem("Recent", { navController, _, state ->
        Recent(state = state, navController = navController)
    })

    object Random : HomeItem("Random", { navController, _, state ->
        Random(state = state, navController = navController)
    })
}


@ExperimentalPagerApi
@Composable
fun Tabs(tabs: List<HomeItem>, pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    TabRow(
        modifier = Modifier.fillMaxWidth(),
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.secondary,
        indicator = { tabPosition ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPosition)
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                modifier = Modifier
                    .padding(horizontal = 12.dp),
                text = { Text(tab.title, style = MaterialTheme.typography.subtitle2) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
    }
}


@ExperimentalPagerApi
@Composable
fun TabsContent(
    tabs: List<HomeItem>,
    pagerState: PagerState,
    navController: NavController,
    coroutine: CoroutineScope,
    state: SearchState,
) {
    HorizontalPager(state = pagerState) { page ->
        tabs[page].screen(navController, coroutine, state)

    }
}

enum class SearchDisplay {
    Wallpapers, Results, NoResults, NoWallpapers
}

@Composable
private fun rememberSearchState(
    query: TextFieldValue = TextFieldValue(""),
    focused: Boolean = false,
    searching: Boolean = false,
    searchResults: APIResult<List<UnsplashPhoto>>? = null
): SearchState {
    return remember {
        SearchState(
            query = query,
            focused = focused,
            searching = searching,
            searchResults = searchResults
        )
    }
}

@Stable
class SearchState(
    query: TextFieldValue,
    focused: Boolean,
    searching: Boolean,
    searchResults: APIResult<List<UnsplashPhoto>>?
) {
    var query by mutableStateOf(query)
    var focused by mutableStateOf(focused)
    var searching by mutableStateOf(searching)
    var searchResults by mutableStateOf(searchResults)
    val searchDisplay: SearchDisplay
        get() = when {
            !focused && query.text.isEmpty() -> SearchDisplay.Wallpapers
            focused && query.text.isEmpty() -> SearchDisplay.NoWallpapers
            searchResults?.data?.isEmpty() == true -> SearchDisplay.NoResults
            else -> SearchDisplay.Results
        }
}

@Composable
fun AppLogo() {
    Surface(
        color = MaterialTheme.colors.background,
        shape = CutCornerShape(2.dp),
        modifier = Modifier
            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
            .size(40.dp)
    ) {

        Image(
            painter = painterResource(id = R.drawable.icon),
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.Transparent,
                    shape = CutCornerShape(3.dp)
                ),
            contentDescription = "App Logo",
            contentScale = ContentScale.Crop,
        )

    }
}

@Composable
fun NoResults(
    query: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.search_no_matches, query),
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.search_no_matches_retry),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SearchHint() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = "Search",
            color = MaterialTheme.colors.surface,
            style = MaterialTheme.typography.subtitle2,
        )
    }
}


@Composable
fun RadioThemeGroup(
    radioOptions: List<String>,
    selected: String,
    viewModel: UHDViewModel
) {
    val (_, onOptionSelected) = remember {
        mutableStateOf(selected)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.background.copy(alpha = AlphaNearOpaque)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.background.copy(alpha = AlphaNearOpaque)),
        ) {
            radioOptions.forEach { text ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                        .selectable(
                            selected = (text == selected),
                            onClick = {
                                onOptionSelected(text)
                                viewModel.insertSetting("Theme", text)
                            }
                        ),
                ) {
                    RadioButton(
                        selected = (text == selected),
                        onClick = {
                            onOptionSelected(text)
                            viewModel.insertSetting("Theme", text)
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colors.primary,
                            unselectedColor = MaterialTheme.colors.secondary
                        )
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(start = 10.dp),
                        color = MaterialTheme.colors.secondary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Composable
fun ImageLayout(unsplashPhoto: UnsplashPhoto, navController: NavController) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .padding(4.dp)
            .size(width = 100.dp, height = 150.dp),
        color = MaterialTheme.colors.background,
        elevation = 3.dp,
        shape = CutCornerShape(8.dp)
    ) {
        GlideImage(
            imageModel = unsplashPhoto.urls.small,
            circularReveal = CircularReveal(duration = 320),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    if (context.currentConnectionState == ConnectionState.Available) {
                        val json = Uri.encode(Gson().toJson(unsplashPhoto))
                        navController.navigate(
                            "details/photoDetails/$json"
                        )
                    }
                },
        )
    }
}


@Composable
fun NetworkError(
    error: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Icon(
            imageVector = Icons.Outlined.RunningWithErrors,
            contentDescription = "Error icon",
            modifier = Modifier
                .padding(4.dp)
                .size(35.dp),
            tint = MaterialTheme.colors.secondary
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.secondary

        )
    }
}