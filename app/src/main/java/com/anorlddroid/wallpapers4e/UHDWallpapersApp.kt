package com.anorlddroid.wallpapers4e

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.anorlddroid.wallpapers4e.ui.theme.Wallpapers4ETheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun UHDWallpapersApp(finishActivity: () -> Unit, context: Context) {
    ProvideWindowInsets {
        Wallpapers4ETheme {
            val navController = rememberNavController()
            val scope = rememberCoroutineScope()

            Scaffold(
                backgroundColor = MaterialTheme.colors.background,
            ) { innerPaddingModifier ->
                NavGraph(
                    finishActivity = finishActivity,
                    navController = navController,
                    modifier = Modifier.padding(innerPaddingModifier),
                    context = context,
                    coroutine = scope
                )
            }
        }
    }
}
