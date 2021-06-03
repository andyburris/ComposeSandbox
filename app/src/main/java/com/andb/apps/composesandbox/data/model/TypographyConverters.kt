package com.andb.apps.composesandbox.data.model

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.andb.apps.composesandbox.ui.common.ProjectTheme
import com.andb.apps.composesandboxdata.model.PrototypeFontWeight
import com.andb.apps.composesandboxdata.model.PrototypeTextStyle
import com.andb.apps.composesandboxdata.model.ThemeTypography

fun PrototypeTextStyle.FixedStyle.toTextStyle() = TextStyle(fontSize = this.fontSize.sp, fontWeight = this.fontWeight.toFontWeight(), letterSpacing = this.letterSpacing.sp)

fun PrototypeFontWeight.toFontWeight() = when(this) {
    PrototypeFontWeight.Black -> FontWeight.Black
    PrototypeFontWeight.Bold -> FontWeight.Bold
    PrototypeFontWeight.ExtraBold -> FontWeight.ExtraBold
    PrototypeFontWeight.ExtraLight -> FontWeight.ExtraLight
    PrototypeFontWeight.Light -> FontWeight.Light
    PrototypeFontWeight.Medium -> FontWeight.Medium
    PrototypeFontWeight.Normal -> FontWeight.Normal
    PrototypeFontWeight.SemiBold -> FontWeight.SemiBold
    PrototypeFontWeight.Thin -> FontWeight.Thin
}

fun ThemeTypography.toTypography() = Typography(
    h1 = this.h1.toTextStyle(),
    h2 = this.h2.toTextStyle(),
    h3 = this.h3.toTextStyle(),
    h4 = this.h4.toTextStyle(),
    h5 = this.h5.toTextStyle(),
    h6 = this.h6.toTextStyle(),
    subtitle1 = this.subtitle1.toTextStyle(),
    subtitle2 = this.subtitle2.toTextStyle(),
    body1 = this.body1.toTextStyle(),
    body2 = this.body2.toTextStyle(),
    button = this.button.toTextStyle(),
    caption = this.caption.toTextStyle(),
    overline = this.overline.toTextStyle(),
)


/**
 * Use when showing in properties UI
 */
@Composable
fun PrototypeTextStyle.projectStyle() = this.projectStyle(ProjectTheme.type)

/**
 * Use when showing in properties UI
 */
fun PrototypeTextStyle.projectStyle(typography: ThemeTypography) = when (this){
    is PrototypeTextStyle.FixedStyle -> this
    PrototypeTextStyle.ThemeStyle.Body1 -> typography.body1
    PrototypeTextStyle.ThemeStyle.Body2 -> typography.body2
    PrototypeTextStyle.ThemeStyle.Button -> typography.button
    PrototypeTextStyle.ThemeStyle.Caption -> typography.caption
    PrototypeTextStyle.ThemeStyle.H1 -> typography.h1
    PrototypeTextStyle.ThemeStyle.H2 -> typography.h2
    PrototypeTextStyle.ThemeStyle.H3 -> typography.h3
    PrototypeTextStyle.ThemeStyle.H4 -> typography.h4
    PrototypeTextStyle.ThemeStyle.H5 -> typography.h5
    PrototypeTextStyle.ThemeStyle.H6 -> typography.h6
    PrototypeTextStyle.ThemeStyle.Overline -> typography.overline
    PrototypeTextStyle.ThemeStyle.Subtitle1 -> typography.subtitle1
    PrototypeTextStyle.ThemeStyle.Subtitle2 -> typography.subtitle2
}

/**
 * Use when showing in properties UI
 */
@Composable
fun PrototypeTextStyle.renderStyle() = when (this){
    is PrototypeTextStyle.FixedStyle -> this.toTextStyle()
    PrototypeTextStyle.ThemeStyle.Body1 -> MaterialTheme.typography.body1
    PrototypeTextStyle.ThemeStyle.Body2 -> MaterialTheme.typography.body2
    PrototypeTextStyle.ThemeStyle.Button -> MaterialTheme.typography.button
    PrototypeTextStyle.ThemeStyle.Caption -> MaterialTheme.typography.caption
    PrototypeTextStyle.ThemeStyle.H1 -> MaterialTheme.typography.h1
    PrototypeTextStyle.ThemeStyle.H2 -> MaterialTheme.typography.h2
    PrototypeTextStyle.ThemeStyle.H3 -> MaterialTheme.typography.h3
    PrototypeTextStyle.ThemeStyle.H4 -> MaterialTheme.typography.h4
    PrototypeTextStyle.ThemeStyle.H5 -> MaterialTheme.typography.h5
    PrototypeTextStyle.ThemeStyle.H6 -> MaterialTheme.typography.h6
    PrototypeTextStyle.ThemeStyle.Overline -> MaterialTheme.typography.overline
    PrototypeTextStyle.ThemeStyle.Subtitle1 -> MaterialTheme.typography.subtitle1
    PrototypeTextStyle.ThemeStyle.Subtitle2 -> MaterialTheme.typography.subtitle2
}


