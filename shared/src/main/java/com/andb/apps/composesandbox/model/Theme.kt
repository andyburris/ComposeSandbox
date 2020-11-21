package com.andb.apps.composesandbox.model

import kotlinx.serialization.Serializable

@Serializable
data class Theme(
    val primary: PrototypeColor.FixedColor,
    val primaryVariant: PrototypeColor.FixedColor,
    val onPrimary: PrototypeColor.FixedColor,
    val secondary: PrototypeColor.FixedColor,
    val secondaryVariant: PrototypeColor.FixedColor,
    val onSecondary: PrototypeColor.FixedColor,
    val background: PrototypeColor.FixedColor,
    val onBackground: PrototypeColor.FixedColor,
    val surface: PrototypeColor.FixedColor,
    val onSurface: PrototypeColor.FixedColor,
    val error: PrototypeColor.FixedColor,
    val onError: PrototypeColor.FixedColor,
)

fun Theme.getColor(prototypeColor: PrototypeColor.ThemeColor) = when (prototypeColor) {
    PrototypeColor.ThemeColor.Primary -> primary
    PrototypeColor.ThemeColor.PrimaryVariant -> primaryVariant
    PrototypeColor.ThemeColor.OnPrimary -> onPrimary
    PrototypeColor.ThemeColor.Secondary -> secondary
    PrototypeColor.ThemeColor.SecondaryVariant -> secondaryVariant
    PrototypeColor.ThemeColor.OnSecondary -> onSecondary
    PrototypeColor.ThemeColor.Background -> background
    PrototypeColor.ThemeColor.OnBackground -> onBackground
    PrototypeColor.ThemeColor.Surface -> surface
    PrototypeColor.ThemeColor.OnSurface -> onSurface
    PrototypeColor.ThemeColor.Error -> error
    PrototypeColor.ThemeColor.OnError -> onError
}

fun Theme.updateColor(prototypeColor: PrototypeColor.ThemeColor, updatedColor: PrototypeColor.FixedColor) = when (prototypeColor) {
    PrototypeColor.ThemeColor.Primary -> this.copy(primary = updatedColor)
    PrototypeColor.ThemeColor.PrimaryVariant -> this.copy(primaryVariant = updatedColor)
    PrototypeColor.ThemeColor.OnPrimary -> this.copy(onPrimary = updatedColor)
    PrototypeColor.ThemeColor.Secondary -> this.copy(secondary = updatedColor)
    PrototypeColor.ThemeColor.SecondaryVariant -> this.copy(secondaryVariant = updatedColor)
    PrototypeColor.ThemeColor.OnSecondary -> this.copy(onSecondary = updatedColor)
    PrototypeColor.ThemeColor.Background -> this.copy(background = updatedColor)
    PrototypeColor.ThemeColor.OnBackground -> this.copy(onBackground = updatedColor)
    PrototypeColor.ThemeColor.Surface -> this.copy(surface = updatedColor)
    PrototypeColor.ThemeColor.OnSurface -> this.copy(onSurface = updatedColor)
    PrototypeColor.ThemeColor.Error -> this.copy(error = updatedColor)
    PrototypeColor.ThemeColor.OnError -> this.copy(onError = updatedColor)
}