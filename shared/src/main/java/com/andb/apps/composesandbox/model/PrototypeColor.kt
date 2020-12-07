package com.andb.apps.composesandbox.model

import kotlinx.serialization.Serializable

@Serializable
sealed class PrototypeColor {
    @Serializable data class FixedColor (val color: Int) : PrototypeColor()
    sealed class ThemeColor : PrototypeColor() {
        @Serializable object Primary : ThemeColor()
        @Serializable object PrimaryVariant : ThemeColor()
        @Serializable object OnPrimary : ThemeColor()
        @Serializable object Secondary : ThemeColor()
        @Serializable object SecondaryVariant : ThemeColor()
        @Serializable object OnSecondary : ThemeColor()
        @Serializable object Background : ThemeColor()
        @Serializable object OnBackground : ThemeColor()
        @Serializable object Surface : ThemeColor()
        @Serializable object OnSurface : ThemeColor()
        @Serializable object Error : ThemeColor()
        @Serializable object OnError : ThemeColor()
    }
}

fun PrototypeColor.toCode() = when (this) {
    is PrototypeColor.FixedColor -> "Color(0x${this.color.toUInt().toString(16).toUpperCase()})"
    is PrototypeColor.ThemeColor.Primary -> "MaterialTheme.colors.primary"
    is PrototypeColor.ThemeColor.PrimaryVariant -> "MaterialTheme.colors.primaryVariant"
    is PrototypeColor.ThemeColor.OnPrimary -> "MaterialTheme.colors.onPrimary"
    is PrototypeColor.ThemeColor.Secondary -> "MaterialTheme.colors.secondary"
    is PrototypeColor.ThemeColor.SecondaryVariant -> "MaterialTheme.colors.secondaryVariant"
    is PrototypeColor.ThemeColor.OnSecondary -> "MaterialTheme.colors.onSecondary"
    is PrototypeColor.ThemeColor.Background -> "MaterialTheme.colors.background"
    is PrototypeColor.ThemeColor.OnBackground -> "MaterialTheme.colors.onBackground"
    is PrototypeColor.ThemeColor.Surface -> "MaterialTheme.colors.surface"
    is PrototypeColor.ThemeColor.OnSurface -> "MaterialTheme.colors.onSurface"
    is PrototypeColor.ThemeColor.Error -> "MaterialTheme.colors.error"
    PrototypeColor.ThemeColor.OnError -> "MaterialTheme.colors.onError"
}