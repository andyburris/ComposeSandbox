package com.andb.apps.composesandbox.data.model

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

data class Theme(val colors: Colors)

fun Theme.getColor(prototypeColor: PrototypeColor.ThemeColor) = when (prototypeColor) {
    PrototypeColor.ThemeColor.Primary -> colors.primary
    PrototypeColor.ThemeColor.PrimaryVariant -> colors.primaryVariant
    PrototypeColor.ThemeColor.OnPrimary -> colors.onPrimary
    PrototypeColor.ThemeColor.Secondary -> colors.secondary
    PrototypeColor.ThemeColor.SecondaryVariant -> colors.secondaryVariant
    PrototypeColor.ThemeColor.OnSecondary -> colors.onSecondary
    PrototypeColor.ThemeColor.Background -> colors.background
    PrototypeColor.ThemeColor.OnBackground -> colors.onBackground
    PrototypeColor.ThemeColor.Surface -> colors.surface
    PrototypeColor.ThemeColor.OnSurface -> colors.onSurface
    PrototypeColor.ThemeColor.Error -> colors.error
}

fun Theme.updateColor(prototypeColor: PrototypeColor.ThemeColor, updatedColor: Color) = this.copy(
    colors = when (prototypeColor) {
        PrototypeColor.ThemeColor.Primary -> colors.copy(primary = updatedColor)
        PrototypeColor.ThemeColor.PrimaryVariant -> colors.copy(primaryVariant = updatedColor)
        PrototypeColor.ThemeColor.OnPrimary -> colors.copy(onPrimary = updatedColor)
        PrototypeColor.ThemeColor.Secondary -> colors.copy(secondary = updatedColor)
        PrototypeColor.ThemeColor.SecondaryVariant -> colors.copy(secondaryVariant = updatedColor)
        PrototypeColor.ThemeColor.OnSecondary -> colors.copy(onSecondary = updatedColor)
        PrototypeColor.ThemeColor.Background -> colors.copy(background = updatedColor)
        PrototypeColor.ThemeColor.OnBackground -> colors.copy(onBackground = updatedColor)
        PrototypeColor.ThemeColor.Surface -> colors.copy(surface = updatedColor)
        PrototypeColor.ThemeColor.OnSurface -> colors.copy(onSurface = updatedColor)
        PrototypeColor.ThemeColor.Error -> colors.copy(error = updatedColor)
    }
)