package com.anorlddroid.wallpapers4e.ui.details

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anorlddroid.wallpapers4e.R
import com.anorlddroid.wallpapers4e.UHDViewModel
import com.anorlddroid.wallpapers4e.data.network.pojo.UnsplashPhoto
import com.anorlddroid.wallpapers4e.ui.theme.AlphaNearOpaque
import com.anorlddroid.wallpapers4e.ui.theme.Love
import com.anorlddroid.wallpapers4e.ui.theme.Neutral3
import com.anorlddroid.wallpapers4e.ui.theme.Wallpapers4ETheme
import com.anorlddroid.wallpapers4e.ui.utils.ConnectionState
import com.anorlddroid.wallpapers4e.ui.utils.Permission
import com.anorlddroid.wallpapers4e.ui.utils.currentConnectionState
import com.google.accompanist.insets.statusBarsPadding
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Details(
    unsplashPhoto: UnsplashPhoto,
    upPress: () -> Unit,
    scope: CoroutineScope
) {
    val context = LocalContext.current
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(22.dp),
        scrimColor = MaterialTheme.colors.background,
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetContent = {
            SheetContent(unsplashPhoto = unsplashPhoto)
            Spacer(modifier = Modifier.height(12.dp))
        }
    ) {
        Wallpapers4ETheme {
            Scaffold(
                backgroundColor = MaterialTheme.colors.background,
                topBar = {
                    TopBar(upPress, unsplashPhoto, context)
                    Divider(thickness = 0.dp, color = Neutral3)
                },
                bottomBar = {
                    BottomBar(
                        modalBottomSheetState = bottomSheetState,
                        coroutineScope = scope,
                        unsplashPhoto = unsplashPhoto
                    )
                }
            ) {
                Box {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        color = MaterialTheme.colors.background,
                    ) {
                        val photoUrl = unsplashPhoto.urls.medium ?: unsplashPhoto.urls.small
                        GlideImage(
                            imageModel = photoUrl,
                            circularReveal = CircularReveal(duration = 320),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(upPress: () -> Unit, unsplashPhoto: UnsplashPhoto, context: Context) {
    Column(modifier = Modifier.statusBarsPadding()) {
        TopAppBar(
            modifier = Modifier,
            backgroundColor = MaterialTheme.colors.background.copy(alpha = AlphaNearOpaque),
            contentColor = MaterialTheme.colors.secondary,
            actions = {
                IconButton(onClick = {
                    shareImage(unsplashPhoto, context)
                }) {
                    Icon(
                        Icons.Outlined.Share,
                        tint = MaterialTheme.colors.secondary,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            },
            title = {
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = unsplashPhoto.user.name,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 24.sp
                        ),
                        modifier = Modifier.padding(
                            top = 2.dp,
                            bottom = 2.dp,
                            start = 2.dp,
                            end = 3.dp
                        ),
                        color = MaterialTheme.colors.secondary
                    )
                    Text(
                        text = "from Unsplash",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 24.sp
                        ),
                        modifier = Modifier
                            .padding(
                                top = 2.dp,
                                bottom = 2.dp,
                                start = 2.dp,
                                end = 3.dp
                            )
                            .align(
                                Alignment.CenterHorizontally
                            ),
                        color = MaterialTheme.colors.secondary
                    )
                }
            },
            navigationIcon = {
                Up(
                    upPress = upPress
                )
            }
        )
    }
}


@Composable
fun Up(upPress: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = upPress,
        modifier = modifier
            .padding(start = 8.dp, top = 10.dp, bottom = 10.dp, end = 2.dp)
            .size(20.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.ArrowBackIos,
            tint = MaterialTheme.colors.secondary,
            contentDescription = stringResource(R.string.label_back),
            modifier = modifier
                .size(26.dp)
        )
    }
}

@Composable
fun NavIcon(unsplashPhoto: UnsplashPhoto) {
    Surface(
        color = Color.LightGray,
        shape = CircleShape,
        modifier = Modifier
            .size(40.dp)
    ) {
        GlideImage(
            imageModel = unsplashPhoto.user.profile_image,
            circularReveal = CircularReveal(duration = 320),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.Transparent,
                    shape = CircleShape
                ),
            placeHolder = ImageBitmap.imageResource(id = R.drawable.placeholder),
            error = ImageBitmap.imageResource(id = R.drawable.placeholder)
        )
    }
}

@Composable
fun SheetContent(unsplashPhoto: UnsplashPhoto) {
    Column(
    ) {
        if (unsplashPhoto.description != null) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                unsplashPhoto.description.let {
                    Text(
                        text = "Description: ",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 24.sp
                        ),
                        modifier = Modifier.padding(
                            top = 2.dp,
                            bottom = 8.dp,
                            start = 1.dp,
                            end = 3.dp
                        ),
                        color = MaterialTheme.colors.secondary
                    )
                    Text(
                        text = it,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 24.sp
                        ),
                        modifier = Modifier.padding(
                            top = 2.dp,
                            bottom = 2.dp,
                            start = 2.dp,
                            end = 3.dp
                        ),
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
        }
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Created on: ",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 24.sp
                ),
                modifier = Modifier.padding(
                    top = 2.dp,
                    bottom = 4.dp,
                    start = 1.dp,
                    end = 3.dp
                ),
                color = MaterialTheme.colors.secondary
            )
            Text(
                text = unsplashPhoto.created_at,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 24.sp
                ),
                modifier = Modifier.padding(
                    top = 2.dp,
                    bottom = 2.dp,
                    start = 2.dp,
                    end = 3.dp
                ),
                color = MaterialTheme.colors.secondary
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomBar(
    unsplashPhoto: UnsplashPhoto,
    modalBottomSheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope
) {
    val download = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val viewModel: UHDViewModel = viewModel()
    val setWallpaper = remember { mutableStateOf(false) }
    val showOptions = remember { mutableStateOf(false) }
    val location = remember { mutableStateOf(2) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color.Transparent
            )
            .heightIn(min = 56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Transparent
                )
                .align(Alignment.BottomCenter)
                .heightIn(min = 56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                modifier = Modifier.padding(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Favorite,
                    tint = Love,
                    contentDescription = null,
                    modifier = Modifier.size(23.dp)
                )
                Text(
                    text = unsplashPhoto.likes.toString(),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier.padding(
                        top = 2.dp,
                        bottom = 2.dp,
                        start = 4.dp,
                        end = 3.dp
                    ),
                    color = MaterialTheme.colors.secondary
                )
            }
            IconButton(onClick = {
                download.value = true
            }) {
                Icon(
                    Icons.Outlined.FileDownload,
                    tint = MaterialTheme.colors.secondary,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
            }
            IconButton(onClick = {
                showOptions.value = true
            }) {
                Icon(
                    Icons.Outlined.Wallpaper,
                    tint = MaterialTheme.colors.secondary,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
            }
            IconButton(onClick = {
                coroutineScope.launch {
                    modalBottomSheetState.show()
                }
            }) {
                Icon(
                    Icons.Outlined.MoreVert,
                    tint = MaterialTheme.colors.secondary,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
            }

        }
        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(
                medium = RoundedCornerShape(
                    16.dp
                )
            )
        ) {
            DropdownMenu(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.onSurface,
                    )
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.BottomCenter),
                expanded = showOptions.value,
                onDismissRequest = { showOptions.value = false },

                ) {
                DropdownMenuItem(onClick = {
                    download.value = true
                    setWallpaper.value = true
                    location.value = 0
                    showOptions.value = false
                }) {
                    Text(
                        text = "Home Screen",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.secondary
                    )
                }
                DropdownMenuItem(onClick = {
                    download.value = true
                    setWallpaper.value = true
                    location.value = 1
                    showOptions.value = false
                }) {
                    Text(
                        text = "Lock Screen",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.secondary
                    )
                }
                DropdownMenuItem(onClick = {
                    download.value = true
                    setWallpaper.value = true
                    location.value = 2
                    showOptions.value = false
                }) {
                    Text(
                        text = "Home Screen and Lock screen",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
        }
        if (download.value) {
            Permission(
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
                permissionNotAvailableContent = {
                    Column(Modifier) {
                        Text(text = "You need to give the app Camera access to proceed.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            TextButton(onClick = {
                                context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
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
                if (context.currentConnectionState == ConnectionState.Available) {
                    viewModel.getDownloadLink(
                        unsplashPhoto.urls.small,
                        context,
                        unsplashPhoto.description ?: "Photo from unsplash ",
                        setWallpaper.value,
                        location.value
                    )
                    download.value = false
                    setWallpaper.value = false
                }
            }
        }
    }
}

private fun shareImage(unsplashPhoto: UnsplashPhoto, context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, "Photo from unsplash")
        putExtra(Intent.EXTRA_TEXT, unsplashPhoto.urls.small)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_image)))
}