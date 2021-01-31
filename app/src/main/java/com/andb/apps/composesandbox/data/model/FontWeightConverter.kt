package com.andb.apps.composesandbox.data.model

import androidx.compose.ui.text.font.FontWeight
import com.andb.apps.composesandboxdata.model.PrototypeComponent

fun PrototypeComponent.Text.Weight.toFontWeight() = when (this) {
    PrototypeComponent.Text.Weight.Thin -> FontWeight.Thin
    PrototypeComponent.Text.Weight.ExtraLight -> FontWeight.ExtraLight
    PrototypeComponent.Text.Weight.Light -> FontWeight.Light
    PrototypeComponent.Text.Weight.Normal -> FontWeight.Normal
    PrototypeComponent.Text.Weight.Medium -> FontWeight.Medium
    PrototypeComponent.Text.Weight.SemiBold -> FontWeight.SemiBold
    PrototypeComponent.Text.Weight.Bold -> FontWeight.Bold
    PrototypeComponent.Text.Weight.ExtraBold -> FontWeight.ExtraBold
    PrototypeComponent.Text.Weight.Black -> FontWeight.Black
}