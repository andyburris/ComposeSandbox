package com.andb.apps.composesandbox.data.model

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.andb.apps.composesandbox.model.PrototypeColor
import com.andb.apps.composesandbox.ui.common.ProjectTheme

fun Color.toPrototypeColor() = PrototypeColor.FixedColor(this.toArgb())
fun PrototypeColor.FixedColor.toColor(): Color = Color(this.color)

/**
 * Use in RenderComponent
 */
@Composable
fun PrototypeColor.renderColor() = when(this) {
    is PrototypeColor.FixedColor -> this.toColor()
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
    is PrototypeColor.ThemeColor.OnError -> MaterialTheme.colors.onError
}

@Composable
        /**
         * Use when showing in properties UI
         */
fun PrototypeColor.projectColor() = when (this){
    is PrototypeColor.FixedColor -> this.toColor()
    is PrototypeColor.ThemeColor.Primary -> ProjectTheme.primary.toColor()
    is PrototypeColor.ThemeColor.PrimaryVariant -> ProjectTheme.primaryVariant.toColor()
    is PrototypeColor.ThemeColor.OnPrimary -> ProjectTheme.onPrimary.toColor()
    is PrototypeColor.ThemeColor.Secondary -> ProjectTheme.secondary.toColor()
    is PrototypeColor.ThemeColor.SecondaryVariant -> ProjectTheme.secondaryVariant.toColor()
    is PrototypeColor.ThemeColor.OnSecondary -> ProjectTheme.onSecondary.toColor()
    is PrototypeColor.ThemeColor.Background -> ProjectTheme.background.toColor()
    is PrototypeColor.ThemeColor.OnBackground -> ProjectTheme.onBackground.toColor()
    is PrototypeColor.ThemeColor.Surface -> ProjectTheme.surface.toColor()
    is PrototypeColor.ThemeColor.OnSurface -> ProjectTheme.onSurface.toColor()
    is PrototypeColor.ThemeColor.Error -> ProjectTheme.error.toColor()
    PrototypeColor.ThemeColor.OnError -> ProjectTheme.onError.toColor()
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
    is PrototypeColor.ThemeColor.OnError -> "On Error"
}
