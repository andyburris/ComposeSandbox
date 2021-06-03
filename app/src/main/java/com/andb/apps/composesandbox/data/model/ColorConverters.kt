package com.andb.apps.composesandbox.data.model

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.andb.apps.composesandbox.ui.common.ProjectTheme
import com.andb.apps.composesandboxdata.model.PrototypeColor

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
    is PrototypeColor.ThemeColor.Primary -> ProjectTheme.colors.primary.toColor()
    is PrototypeColor.ThemeColor.PrimaryVariant -> ProjectTheme.colors.primaryVariant.toColor()
    is PrototypeColor.ThemeColor.OnPrimary -> ProjectTheme.colors.onPrimary.toColor()
    is PrototypeColor.ThemeColor.Secondary -> ProjectTheme.colors.secondary.toColor()
    is PrototypeColor.ThemeColor.SecondaryVariant -> ProjectTheme.colors.secondaryVariant.toColor()
    is PrototypeColor.ThemeColor.OnSecondary -> ProjectTheme.colors.onSecondary.toColor()
    is PrototypeColor.ThemeColor.Background -> ProjectTheme.colors.background.toColor()
    is PrototypeColor.ThemeColor.OnBackground -> ProjectTheme.colors.onBackground.toColor()
    is PrototypeColor.ThemeColor.Surface -> ProjectTheme.colors.surface.toColor()
    is PrototypeColor.ThemeColor.OnSurface -> ProjectTheme.colors.onSurface.toColor()
    is PrototypeColor.ThemeColor.Error -> ProjectTheme.colors.error.toColor()
    PrototypeColor.ThemeColor.OnError -> ProjectTheme.colors.onError.toColor()
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
