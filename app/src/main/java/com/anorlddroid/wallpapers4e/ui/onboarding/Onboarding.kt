package com.anorlddroid.wallpapers4e.ui.onboarding

import android.util.Log
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.anorlddroid.wallpapers4e.R
import com.anorlddroid.wallpapers4e.UHDViewModel
import com.anorlddroid.wallpapers4e.data.Categories
import com.anorlddroid.wallpapers4e.data.categories
import com.anorlddroid.wallpapers4e.ui.components.UHDStaggeredGrid
import com.anorlddroid.wallpapers4e.ui.theme.*
import com.anorlddroid.wallpapers4e.ui.utils.ConnectionAlertDialog
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding


@ExperimentalCoilApi
@Composable
fun Onboarding(
    onboardingComplete: () -> Unit,
    saveCategory: State<Boolean>,
    finishActivity: () -> Unit
) {
    val checkList = remember { mutableStateOf(0) }
    Wallpapers4ETheme {
        Scaffold(
            topBar = { AppBar(finishActivity = finishActivity) },
            backgroundColor = Neutral7,
            floatingActionButton = {
                Log.d("ONFAB", checkList.value.toString())
                if (checkList.value >= 4) {
                    FloatingActionButton(
                        onClick = onboardingComplete,
                        backgroundColor = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .navigationBarsPadding()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = stringResource(R.string.done),
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(innerPadding)
            ) {
                Text(
                    text = stringResource(R.string.choose_categories_that_interest_you),
                    style = MaterialTheme.typography.h4,
                    color = Neutral0,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 2.dp
                    )
                )
                CategorysGrid(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    saveCategory = saveCategory,
                    checkList = checkList
                )
                Spacer(Modifier.height(56.dp)) // center grid accounting for FAB
            }
        }
    }
}

@Composable
private fun AppBar(finishActivity: () -> Unit) {
    Column(modifier = Modifier.statusBarsPadding())
    {
        ConnectionAlertDialog(finishActivity = finishActivity)
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.h6,
                color = Neutral0,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(
                    horizontal = 0.dp,
                    vertical = 10.dp
                )
            )
        }
    }
}

@ExperimentalCoilApi
@Composable
private fun CategorysGrid(
    modifier: Modifier = Modifier,
    saveCategory: State<Boolean>,
    checkList: MutableState<Int>
) {

    LazyRow(modifier = Modifier.padding(start = 8.dp, top = 12.dp)) {
        item {
            UHDStaggeredGrid(
                modifier = Modifier.padding(top = 12.dp)
            ) {
                categories.forEach { category ->
                    CategoryChip(
                        category = category,
                        saveCategory = saveCategory,
                        checkList = checkList
                    )
                }
            }
        }
    }
}

private enum class SelectionState { Unselected, Selected }

/**
 * Class holding animating values when transitioning Category chip states.
 */
private class CategoryChipTransition(
    cornerRadius: State<Dp>,
    selectedAlpha: State<Float>,
    checkScale: State<Float>
) {
    val cornerRadius by cornerRadius
    val selectedAlpha by selectedAlpha
    val checkScale by checkScale
}

@Composable
private fun categoryChipTransition(CategorySelected: Boolean): CategoryChipTransition {
    val transition = updateTransition(
        targetState = if (CategorySelected) SelectionState.Selected else SelectionState.Unselected,
        label = ""
    )
    val cornerRadius = transition.animateDp(label = "corner radius") { state ->
        when (state) {
            SelectionState.Unselected -> 8.dp
            SelectionState.Selected -> 20.dp
        }
    }
    val selectedAlpha = transition.animateFloat(label = "selected alpha") { state ->
        when (state) {
            SelectionState.Unselected -> 0f
            SelectionState.Selected -> 0.8f
        }
    }
    val checkScale = transition.animateFloat(label = "check scale") { state ->
        when (state) {
            SelectionState.Unselected -> 0.6f
            SelectionState.Selected -> 1f
        }
    }
    return remember(transition) {
        CategoryChipTransition(cornerRadius, selectedAlpha, checkScale)
    }
}

@ExperimentalCoilApi
@Composable
private fun CategoryChip(
    category: Categories,
    saveCategory: State<Boolean>,
    checkList: MutableState<Int>
) {
    val context = LocalContext.current
    val viewModel: UHDViewModel = viewModel()
    val selected = remember { mutableStateOf(false) }
    val categoryChipTransitionState = categoryChipTransition(selected.value)
    Surface(
        modifier = Modifier.padding(4.dp),
        color = Neutral3,
        elevation = 3.dp,
        shape = MaterialTheme.shapes.medium.copy(
            topStart = CornerSize(
                categoryChipTransitionState.cornerRadius
            )
        )
    ) {
        Row(
            modifier = Modifier
                .toggleable(
                    value = selected.value,
                    onValueChange = {
                        selected.value = !selected.value
                        if (selected.value) ++checkList.value
                        else --checkList.value
                        Log.d("ONVALUECHANGE", checkList.value.toString())
                    }
                )
                .background(color = Neutral3)
        ) {
            Box {
                NetworkImage(
                    url = category.imageUrl
                )
                if (categoryChipTransitionState.selectedAlpha > 0f) {
                    Surface(
                        color = MaterialTheme.colors.primary.copy(alpha = categoryChipTransitionState.selectedAlpha),
                        modifier = Modifier.matchParentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onPrimary.copy(
                                alpha = categoryChipTransitionState.selectedAlpha
                            ),
                            modifier = Modifier
                                .wrapContentSize()
                                .scale(categoryChipTransitionState.checkScale)
                        )
                    }
                }
            }
            Column {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.body1,
                    color = Neutral0,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 26.dp,
                        bottom = 8.dp
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Icon(
                            imageVector = Icons.Filled.CloudDownload,
                            contentDescription = null,
                            tint = Neutral1,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .size(12.dp)
                        )
                        Text(
                            text = category.downloads,
                            style = MaterialTheme.typography.caption,
                            color = Neutral0,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
    if (saveCategory.value && selected.value) {
        viewModel.insertCategory(category.name, context = context)
    }
}


@ExperimentalCoilApi
@Composable
fun NetworkImage(
    url: String,
) {
    Surface(
        modifier = Modifier
            .size(width = 72.dp, height = 72.dp),
        color = Neutral3,
    ) {
        Image(
            painter = rememberImagePainter(
                data = url,
                builder = {
                    crossfade(true)
                    placeholder(R.drawable.placeholder)
                }
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 72.dp, height = 72.dp)
        )
    }
}


@ExperimentalCoilApi
@Preview(name = "Onboarding")
@Composable
private fun OnboardingPreview() {
    val saveCategory = remember {
        mutableStateOf(true)
    }
    Onboarding(onboardingComplete = { }, saveCategory, {})
}

@ExperimentalCoilApi
@Preview("Categoty Chip")
@Composable
private fun CategoryChipPreview() {
    val saveCategory = remember {
        mutableStateOf(true)
    }
    val checkList = remember { mutableStateOf(0) }
    Wallpapers4ETheme {
        CategoryChip(categories.first(), saveCategory, checkList)
    }
}
