package com.anorlddroid.wallpapers4e.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = Purple,
    primaryVariant = Love,
    background = Neutral7.copy(alpha = 0.76f),
    secondary = Neutral0,
    surface = Neutral2,
    onSurface = Neutral3,
    onPrimary = Neutral1,
    onBackground = Neutral7

)

private val LightColorPalette = lightColors(
    primary = Purple,
    primaryVariant = Love,
    background = Color.White.copy(alpha = 0.76f),
    secondary = Neutral7,
    surface = Neutral6,
    onSurface = statusBar,
    onPrimary = Neutral2,
    onBackground = statusBar

)
object ThemeState {
    var selectedTheme by mutableStateOf("")
}
@Composable
fun Wallpapers4ETheme(content: @Composable() () -> Unit) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = MaterialTheme.colors.onBackground)
    val colors = if (ThemeState.selectedTheme == "On") {
        DarkColorPalette
    } else if (ThemeState.selectedTheme == "Off") {
        LightColorPalette
    } else {
        if (isSystemInDarkTheme()) {
            DarkColorPalette
        } else {
            LightColorPalette
        }
    }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}