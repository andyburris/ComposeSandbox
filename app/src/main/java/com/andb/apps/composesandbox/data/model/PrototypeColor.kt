package com.andb.apps.composesandbox.data.model

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.util.toHexString
import com.andb.apps.composesandbox.ui.common.ProjectTheme

sealed class PrototypeColor {
    data class FixedColor (val color: Color) : PrototypeColor()
    sealed class ThemeColor : PrototypeColor() {
        object Primary : ThemeColor()
        object PrimaryVariant : ThemeColor()
        object OnPrimary : ThemeColor()
        object Secondary : ThemeColor()
        object SecondaryVariant : ThemeColor()
        object OnSecondary : ThemeColor()
        object Background : ThemeColor()
        object OnBackground : ThemeColor()
        object Surface : ThemeColor()
        object OnSurface : ThemeColor()
        object Error : ThemeColor()
    }
}

/**
 * Use in RenderComponent
 */
@Composable
fun PrototypeColor.renderColor() = when(this) {
    is PrototypeColor.FixedColor -> this.color
    is PrototypeColor.ThemeColor.Primary -> MaterialTheme.colors.primary
    is PrototypeColor.ThemeColor.PrimaryVariant -> MaterialTheme.colors.primaryVariant
    is PrototypeColor.ThemeColor.OnPrimary -> MaterialTheme.colors.onPrimary
    is PrototypeColor.ThemeColor.Secondary -> MaterialTheme.colors.secondary
    is PrototypeColor.ThemeColor.SecondaryVariant -> MaterialTheme.colors.secondaryVariant
    is PrototypeColor.ThemeColor.OnSecondary -> MaterialTheme.colors.onSecondary
    is PrototypeColor.ThemeColor.Background -> MaterialTheme.colors.background
    is PrototypeColor.ThemeColor.OnBackground -> MaterialTheme.colors.onBackground
    is PrototypeColor.ThemeColor.Surface -> MaterialTheme.colors.surface
    is PrototypeColor.ThemeColor.OnSurface -> MaterialTheme.colors.onSurface
    is PrototypeColor.ThemeColor.Error -> MaterialTheme.colors.error
}

@Composable
/**
 * Use when showing in properties UI
 */
fun PrototypeColor.projectColor() = when (this){
    is PrototypeColor.FixedColor -> this.color
    is PrototypeColor.ThemeColor.Primary -> ProjectTheme.colors.primary
    is PrototypeColor.ThemeColor.PrimaryVariant -> ProjectTheme.colors.primaryVariant
    is PrototypeColor.ThemeColor.OnPrimary -> ProjectTheme.colors.onPrimary
    is PrototypeColor.ThemeColor.Secondary -> ProjectTheme.colors.secondary
    is PrototypeColor.ThemeColor.SecondaryVariant -> ProjectTheme.colors.secondaryVariant
    is PrototypeColor.ThemeColor.OnSecondary -> ProjectTheme.colors.onSecondary
    is PrototypeColor.ThemeColor.Background -> ProjectTheme.colors.background
    is PrototypeColor.ThemeColor.OnBackground -> ProjectTheme.colors.onBackground
    is PrototypeColor.ThemeColor.Surface -> ProjectTheme.colors.surface
    is PrototypeColor.ThemeColor.OnSurface -> ProjectTheme.colors.onSurface
    is PrototypeColor.ThemeColor.Error -> ProjectTheme.colors.error
}

val PrototypeColor.ThemeColor.name get() = when (this) {
    is PrototypeColor.ThemeColor.Primary -> "Primary"
    is PrototypeColor.ThemeColor.PrimaryVariant -> "Primary Variant"
    is PrototypeColor.ThemeColor.OnPrimary -> "On Primary"
    is PrototypeColor.ThemeColor.Secondary -> "Secondary"
    is PrototypeColor.ThemeColor.SecondaryVariant -> "Secondary Variant"
    is PrototypeColor.ThemeColor.OnSecondary -> "On Secondary"
    is PrototypeColor.ThemeColor.Background -> "Background"
    is PrototypeColor.ThemeColor.OnBackground -> "On Background"
    is PrototypeColor.ThemeColor.Surface -> "Surface"
    is PrototypeColor.ThemeColor.OnSurface -> "On Surface"
    is PrototypeColor.ThemeColor.Error -> "Error"
}

fun PrototypeColor.toCode() = when (this) {
    is PrototypeColor.FixedColor -> "Color(${this.color.toArgb().toHexString()})"
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
}