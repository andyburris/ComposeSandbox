package com.andb.apps.composesandboxdata.model

import kotlinx.serialization.Serializable

@Serializable
sealed class PrototypeTextStyle {
    @Serializable data class FixedStyle (val fontWeight: PrototypeFontWeight, val fontSize: Int, val letterSpacing: Double) : PrototypeTextStyle()
    @Serializable sealed class ThemeStyle : PrototypeTextStyle() {
        @Serializable object H1: ThemeStyle()
        @Serializable object H2: ThemeStyle()
        @Serializable object H3: ThemeStyle()
        @Serializable object H4: ThemeStyle()
        @Serializable object H5: ThemeStyle()
        @Serializable object H6: ThemeStyle()
        @Serializable object Subtitle1: ThemeStyle()
        @Serializable object Subtitle2: ThemeStyle()
        @Serializable object Body1: ThemeStyle()
        @Serializable object Body2: ThemeStyle()
        @Serializable object Button: ThemeStyle()
        @Serializable object Caption: ThemeStyle()
        @Serializable object Overline: ThemeStyle()
    }
}

val PrototypeTextStyle.name get() = when(this) {
    is PrototypeTextStyle.FixedStyle -> "Manual"
    PrototypeTextStyle.ThemeStyle.Body1 -> "Body 1"
    PrototypeTextStyle.ThemeStyle.Body2 -> "Body 2"
    PrototypeTextStyle.ThemeStyle.Button -> "Button"
    PrototypeTextStyle.ThemeStyle.Caption -> "Caption"
    PrototypeTextStyle.ThemeStyle.H1 -> "Heading 1"
    PrototypeTextStyle.ThemeStyle.H2 -> "Heading 2"
    PrototypeTextStyle.ThemeStyle.H3 -> "Heading 3"
    PrototypeTextStyle.ThemeStyle.H4 -> "Heading 4"
    PrototypeTextStyle.ThemeStyle.H5 -> "Heading 5"
    PrototypeTextStyle.ThemeStyle.H6 -> "Heading 6"
    PrototypeTextStyle.ThemeStyle.Overline -> "Overline"
    PrototypeTextStyle.ThemeStyle.Subtitle1 -> "Subtitle 1"
    PrototypeTextStyle.ThemeStyle.Subtitle2 -> "Subtitle 2"
}


@Serializable
sealed class PrototypeFontWeight {
    @Serializable object Thin : PrototypeFontWeight()
    @Serializable object ExtraLight : PrototypeFontWeight()
    @Serializable object Light : PrototypeFontWeight()
    @Serializable object Normal : PrototypeFontWeight()
    @Serializable object Medium : PrototypeFontWeight()
    @Serializable object SemiBold : PrototypeFontWeight()
    @Serializable object Bold : PrototypeFontWeight()
    @Serializable object ExtraBold : PrototypeFontWeight()
    @Serializable object Black : PrototypeFontWeight()
}

val fontWeights = listOf(
    PrototypeFontWeight.Thin,
    PrototypeFontWeight.ExtraLight,
    PrototypeFontWeight.Light,
    PrototypeFontWeight.Normal,
    PrototypeFontWeight.Medium,
    PrototypeFontWeight.SemiBold,
    PrototypeFontWeight.Bold,
    PrototypeFontWeight.ExtraBold,
    PrototypeFontWeight.Black,
)

val PrototypeFontWeight.name get() = when(this) {
    PrototypeFontWeight.Thin -> "Thin"
    PrototypeFontWeight.ExtraLight -> "Extra Light"
    PrototypeFontWeight.Light -> "Light"
    PrototypeFontWeight.Normal -> "Normal"
    PrototypeFontWeight.Medium -> "Medium"
    PrototypeFontWeight.SemiBold -> "Semi Bold"
    PrototypeFontWeight.Bold -> "Bold"
    PrototypeFontWeight.ExtraBold -> "Extra Bold"
    PrototypeFontWeight.Black -> "Black"
}