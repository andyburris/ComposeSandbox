package com.andb.apps.composesandbox.data.model

import androidx.compose.material.Colors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.toArgb
import com.andb.apps.composesandboxdata.model.PrototypeColor
import com.andb.apps.composesandboxdata.model.ThemeColors


fun ThemeColors.toColors(): Colors = lightColors(
    primary = primary.toColor(),
    primaryVariant = primaryVariant.toColor(),
    secondary = secondary.toColor(),
    secondaryVariant = secondaryVariant.toColor(),
    background = background.toColor(),
    surface = surface.toColor(),
    error = error.toColor(),
    onPrimary = onPrimary.toColor(),
    onSecondary = onSecondary.toColor(),
    onBackground = onBackground.toColor(),
    onSurface = onSurface.toColor(),
    onError = onError.toColor()
)

fun Colors.toThemeColors() = ThemeColors(
    primary = PrototypeColor.FixedColor(primary.toArgb()),
    primaryVariant = PrototypeColor.FixedColor(primaryVariant.toArgb()),
    secondary = PrototypeColor.FixedColor(secondary.toArgb()),
    secondaryVariant = PrototypeColor.FixedColor(secondaryVariant.toArgb()),
    background = PrototypeColor.FixedColor(background.toArgb()),
    surface = PrototypeColor.FixedColor(surface.toArgb()),
    error = PrototypeColor.FixedColor(error.toArgb()),
    onPrimary = PrototypeColor.FixedColor(onPrimary.toArgb()),
    onSecondary = PrototypeColor.FixedColor(onSecondary.toArgb()),
    onBackground = PrototypeColor.FixedColor(onBackground.toArgb()),
    onSurface = PrototypeColor.FixedColor(onSurface.toArgb()),
    onError = PrototypeColor.FixedColor(onError.toArgb())
)