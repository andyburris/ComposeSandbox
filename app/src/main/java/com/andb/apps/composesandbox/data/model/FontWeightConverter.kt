package com.andb.apps.composesandbox.data.model

import androidx.compose.ui.text.font.FontWeight
import com.andb.apps.composesandbox.model.Properties

fun Properties.Text.Weight.toFontWeight() = when (this) {
    Properties.Text.Weight.Thin -> FontWeight.Thin
    Properties.Text.Weight.ExtraLight -> FontWeight.ExtraLight
    Properties.Text.Weight.Light -> FontWeight.Light
    Properties.Text.Weight.Normal -> FontWeight.Normal
    Properties.Text.Weight.Medium -> FontWeight.Medium
    Properties.Text.Weight.SemiBold -> FontWeight.SemiBold
    Properties.Text.Weight.Bold -> FontWeight.Bold
    Properties.Text.Weight.ExtraBold -> FontWeight.ExtraBold
    Properties.Text.Weight.Black -> FontWeight.Black
}