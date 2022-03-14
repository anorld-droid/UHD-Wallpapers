package com.anorlddroid.wallpapers4e

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import coil.annotation.ExperimentalCoilApi
import com.anorlddroid.wallpapers4e.data.network.pojo.UnsplashPhoto
import com.anorlddroid.wallpapers4e.ui.details.Details
import com.anorlddroid.wallpapers4e.ui.home.home
import com.anorlddroid.wallpapers4e.ui.onboarding.Onboarding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope

/**
 * Destinations used in the ([UHD]).
 */
object MainDestinations {
    const val ONBOARDING_ROUTE = "onboarding"
    const val HOME_ROUTE = "home"
    const val HOME_DATA = "home_data"
//    const val PHOTO_DETAIL_ROUTE =
}

const val ONBOARDINGCOMPLETE = "showed onboarding page"

@OptIn(ExperimentalCoilApi::class)
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    finishActivity: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
    startDestination: String = MainDestinations.HOME_ROUTE,
    showOnboardingInitially: Boolean = true,
    context: Context,
    coroutine: CoroutineScope
) {

//     Onboarding  read from shared preferences.
    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
//
    val onboardingComplete = remember(prefs.getBoolean(ONBOARDINGCOMPLETE, false)) {
        mutableStateOf(prefs.getBoolean(ONBOARDINGCOMPLETE, false))
    }
    val saveCategory = remember(showOnboardingInitially) {
        mutableStateOf(!showOnboardingInitially)
    }

    val actions = remember(navController) { MainActions(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination,

        ) {
        composable(
            MainDestinations.ONBOARDING_ROUTE
        ) {
            // Intercept back in Onboarding: make it finish the activity
            BackHandler {
                finishActivity()
            }
            Onboarding(
                finishActivity = finishActivity,
                onboardingComplete = {
                    // Set the flag so that onboarding is not shown next time.
                    saveCategory.value = true
                    onboardingComplete.value = true
                    actions.onboardingComplete()
                    prefs.edit().putBoolean(ONBOARDINGCOMPLETE, true).apply()
                },
                saveCategory = saveCategory
            )
        }
        composable(
            "details/photoDetails/{photo}",
            arguments = listOf(
                navArgument("photo") {
                    type = AssertParamType()
                }
            )
        ) { backStackEntry ->
            val unsplashPhotoObj = backStackEntry.arguments?.getParcelable<UnsplashPhoto>("photo")
            if (unsplashPhotoObj != null) {
                Details(
                    unsplashPhoto = unsplashPhotoObj,
                    upPress = { navController.navigateUp() },
                    scope = coroutine
                )
            }
        }
        navigation(
            route = MainDestinations.HOME_ROUTE,
            startDestination = MainDestinations.HOME_DATA
        ) {
            home(
                finishActivity = finishActivity,
                onboardingComplete = onboardingComplete,
                navController = navController,
                coroutine = coroutine
            )
        }
    }
}

/**
 * Models the navigation actions in the app.
 */
class MainActions(navController: NavHostController) {
    val onboardingComplete: () -> Unit = {
        navController.popBackStack()
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

class AssertParamType : NavType<UnsplashPhoto>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): UnsplashPhoto? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): UnsplashPhoto {
        return Gson().fromJson(value, UnsplashPhoto::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: UnsplashPhoto) {
        bundle.putParcelable(key, value)
    }

}