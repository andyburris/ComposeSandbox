package com.andb.apps.composesandboxdata.model

import kotlinx.serialization.Serializable

private fun Long.toFixedColor() = PrototypeColor.FixedColor(this.toInt())
private val White = 0xFFFFFFFF.toFixedColor()
private val Black = 0xFF000000.toFixedColor()

@Serializable
data class Theme(
    val primary: PrototypeColor.FixedColor = 0xFF6200EE.toFixedColor(),
    val primaryVariant: PrototypeColor.FixedColor = 0xFF3700B3.toFixedColor(),
    val onPrimary: PrototypeColor.FixedColor = White,
    val secondary: PrototypeColor.FixedColor = 0xFF03DAC6.toFixedColor(),
    val secondaryVariant: PrototypeColor.FixedColor = 0xFF018786.toFixedColor(),
    val onSecondary: PrototypeColor.FixedColor = Black,
    val background: PrototypeColor.FixedColor = White,
    val onBackground: PrototypeColor.FixedColor = Black,
    val surface: PrototypeColor.FixedColor = White,
    val onSurface: PrototypeColor.FixedColor = Black,
    val error: PrototypeColor.FixedColor = 0xFFB00020.toFixedColor(),
    val onError: PrototypeColor.FixedColor = White,
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