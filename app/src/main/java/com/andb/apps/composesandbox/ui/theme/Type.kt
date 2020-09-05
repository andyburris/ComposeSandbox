package com.andb.apps.composesandbox.ui.theme

import androidx.compose.Composable
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Typography
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontWeight
import androidx.ui.unit.sp

// Set of Material typography styles to start with
val typography = Typography(
    h4 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 34.sp,
        letterSpacing = 0.25.sp
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.15.sp
    ),
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)