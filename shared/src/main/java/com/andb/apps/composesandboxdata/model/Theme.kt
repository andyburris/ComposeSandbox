package com.andb.apps.composesandboxdata.model

import kotlinx.serialization.Serializable

private fun Long.toFixedColor() = PrototypeColor.FixedColor(this.toInt())
private val White = 0xFFFFFFFF.toFixedColor()
private val Black = 0xFF000000.toFixedColor()

@Serializable
data class Theme(
    val colors: ThemeColors = ThemeColors(),
    val type: ThemeTypography = ThemeTypography(),
)

@Serializable
data class ThemeColors(
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

fun ThemeColors.items() = listOf(
    primary to PrototypeColor.ThemeColor.Primary,
    primaryVariant to PrototypeColor.ThemeColor.PrimaryVariant,
    onPrimary to PrototypeColor.ThemeColor.OnPrimary,
    secondary to PrototypeColor.ThemeColor.Secondary,
    secondaryVariant to PrototypeColor.ThemeColor.SecondaryVariant,
    onSecondary to PrototypeColor.ThemeColor.OnSecondary,
    background to PrototypeColor.ThemeColor.Background,
    onBackground to PrototypeColor.ThemeColor.OnBackground,
    surface to PrototypeColor.ThemeColor.Surface,
    onSurface to PrototypeColor.ThemeColor.OnSurface,
    error to PrototypeColor.ThemeColor.Error,
    onError to PrototypeColor.ThemeColor.OnError,
)

fun ThemeColors.getColor(prototypeColor: PrototypeColor.ThemeColor) = when (prototypeColor) {
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

fun ThemeColors.withThemeColor(themeColor: PrototypeColor.ThemeColor, updatedColor: PrototypeColor.FixedColor) = when (themeColor) {
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

@Serializable
data class ThemeTypography(
    val h1: PrototypeTextStyle.FixedStyle = PrototypeTextStyle.FixedStyle(
        fontWeight = PrototypeFontWeight.Light,
        fontSize = 96,
        letterSpacing = (-1.5)
    ),
    val h2: PrototypeTextStyle.FixedStyle = PrototypeTextStyle.FixedStyle(
        fontWeight = PrototypeFontWeight.Light,
        fontSize = 60,
        letterSpacing = (-0.5)
    ),
    val h3: PrototypeTextStyle.FixedStyle = PrototypeTextStyle.FixedStyle(
        fontWeight = PrototypeFontWeight.Normal,
        fontSize = 48,
        letterSpacing = 0.0
    ),
    val h4: PrototypeTextStyle.FixedStyle = PrototypeTextStyle.FixedStyle(
        fontWeight = PrototypeFontWeight.Normal,
        fontSize = 34,
        letterSpacing = 0.25
    ),
    val h5: PrototypeTextStyle.FixedStyle = PrototypeTextStyle.FixedStyle(
        fontWeight = PrototypeFontWeight.Normal,
        fontSize = 24,
        letterSpacing = 0.0
    ),
    val h6: PrototypeTextStyle.FixedStyle = PrototypeTextStyle.FixedStyle(
        fontWeight = PrototypeFontWeight.Medium,
        fontSize = 20,
        letterSpacing = 0.15
    ),
    val subtitle1: PrototypeTextStyle.FixedStyle = PrototypeTextStyle.FixedStyle(
        fontWeight = PrototypeFontWeight.Medium,
        fontSize = 16,
        letterSpacing = 0.15
    ),
    val subtitle2: PrototypeTextStyle.FixedStyle = PrototypeTextStyle.FixedStyle(
        fontWeight = PrototypeFontWeight.Medium,
        fontSize = 14,
        letterSpacing = 0.1
    ),
    val body1: PrototypeTextStyle.FixedStyle = PrototypeTextStyle.FixedStyle(
        fontWeight = PrototypeFontWeight.Normal,
        fontSize = 16,
        letterSpacing = 0.5
    ),
    val body2: PrototypeTextStyle.FixedStyle = PrototypeTextStyle.FixedStyle(
        fontWeight = PrototypeFontWeight.Normal,
        fontSize = 14,
        letterSpacing = 0.25
    ),
    val button: PrototypeTextStyle.FixedStyle = PrototypeTextStyle.FixedStyle(
        fontWeight = PrototypeFontWeight.Medium,
        fontSize = 14,
        letterSpacing = 1.25
    ),
    val caption: PrototypeTextStyle.FixedStyle = PrototypeTextStyle.FixedStyle(
        fontWeight = PrototypeFontWeight.Normal,
        fontSize = 12,
        letterSpacing = 0.4
    ),
    val overline: PrototypeTextStyle.FixedStyle = PrototypeTextStyle.FixedStyle(
        fontWeight = PrototypeFontWeight.Normal,
        fontSize = 10,
        letterSpacing = 1.5
    )
)

fun ThemeTypography.items() = mapOf(
    h1 to PrototypeTextStyle.ThemeStyle.H1,
    h2 to PrototypeTextStyle.ThemeStyle.H2,
    h3 to PrototypeTextStyle.ThemeStyle.H3,
    h4 to PrototypeTextStyle.ThemeStyle.H4,
    h5 to PrototypeTextStyle.ThemeStyle.H5,
    h6 to PrototypeTextStyle.ThemeStyle.H6,
    subtitle1 to PrototypeTextStyle.ThemeStyle.Subtitle1,
    subtitle2 to PrototypeTextStyle.ThemeStyle.Subtitle2,
    body1 to PrototypeTextStyle.ThemeStyle.Body1,
    body2 to PrototypeTextStyle.ThemeStyle.Body2,
    button to PrototypeTextStyle.ThemeStyle.Button,
    caption to PrototypeTextStyle.ThemeStyle.Caption,
    overline to PrototypeTextStyle.ThemeStyle.Overline
)

fun ThemeTypography.withThemeStyle(themeStyle: PrototypeTextStyle.ThemeStyle, updatedStyle: PrototypeTextStyle.FixedStyle) = when(themeStyle) {
    PrototypeTextStyle.ThemeStyle.Body1 -> this.copy(body1 = updatedStyle)
    PrototypeTextStyle.ThemeStyle.Body2 -> this.copy(body2 = updatedStyle)
    PrototypeTextStyle.ThemeStyle.Button -> this.copy(button = updatedStyle)
    PrototypeTextStyle.ThemeStyle.Caption -> this.copy(caption = updatedStyle)
    PrototypeTextStyle.ThemeStyle.H1 -> this.copy(h1 = updatedStyle)
    PrototypeTextStyle.ThemeStyle.H2 -> this.copy(h2 = updatedStyle)
    PrototypeTextStyle.ThemeStyle.H3 -> this.copy(h3 = updatedStyle)
    PrototypeTextStyle.ThemeStyle.H4 -> this.copy(h4 = updatedStyle)
    PrototypeTextStyle.ThemeStyle.H5 -> this.copy(h5 = updatedStyle)
    PrototypeTextStyle.ThemeStyle.H6 -> this.copy(h6 = updatedStyle)
    PrototypeTextStyle.ThemeStyle.Overline -> this.copy(overline = updatedStyle)
    PrototypeTextStyle.ThemeStyle.Subtitle1 -> this.copy(subtitle1 = updatedStyle)
    PrototypeTextStyle.ThemeStyle.Subtitle2 -> this.copy(subtitle2 = updatedStyle)
}