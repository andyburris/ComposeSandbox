package com.andb.apps.composesandbox.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val blueGrey500 = Color(0xFF718792)
private val blueGrey700 = Color(0xFF455A64)
private val blueGrey900 = Color(0xFF1c313a)

private val DarkColorPalette = darkColors(
    primary = blueGrey500,
    primaryVariant = blueGrey700,
    secondary = Color.Black.copy(alpha = .05f),
    onSecondary = Color.Black.copy(alpha = 0.5f)
)

private val LightColorPalette = lightColors(
    primary = blueGrey700,
    primaryVariant = blueGrey900,
    secondary = Color.Black.copy(alpha = .05f),
    onSecondary = Color.Black.copy(alpha = 0.5f),
    secondaryVariant = Color.Black.copy(alpha = .25f)

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}