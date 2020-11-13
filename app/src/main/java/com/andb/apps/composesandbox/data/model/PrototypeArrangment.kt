package com.andb.apps.composesandbox.data.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.InternalLayoutApi
import androidx.compose.ui.Alignment

val horizontalArrangements = listOf(Arrangement.Start, Arrangement.Center, Arrangement.End)
val verticalArrangements = listOf(Arrangement.Top, Arrangement.Center, Arrangement.Bottom)
val bothArrangements = listOf(Arrangement.SpaceBetween, Arrangement.SpaceEvenly, Arrangement.SpaceAround)
val verticalAlignments = listOf(Alignment.Top, Alignment.CenterVertically, Alignment.Bottom)
val horizontalAlignments = listOf(Alignment.Start, Alignment.CenterHorizontally, Alignment.End)

@OptIn(InternalLayoutApi::class)
fun Arrangement.Horizontal.toReadableString() = when (this) {
    Arrangement.Start -> "Start"
    Arrangement.Center -> "Center"
    Arrangement.End -> "End"
    Arrangement.SpaceBetween -> "Space Between"
    Arrangement.SpaceAround -> "Space Around"
    Arrangement.SpaceEvenly -> "Space Evenly"
    else -> ""
}

@OptIn(InternalLayoutApi::class)
fun Arrangement.Vertical.toReadableString() = when (this) {
    Arrangement.Top -> "Top"
    Arrangement.Center -> "Center"
    Arrangement.Bottom -> "Bottom"
    Arrangement.SpaceBetween -> "Space Between"
    Arrangement.SpaceAround -> "Space Around"
    Arrangement.SpaceEvenly -> "Space Evenly"
    else -> ""
}

@OptIn(InternalLayoutApi::class)
fun Arrangement.Horizontal.toCodeString(): String = when(this) {
    Arrangement.Start -> "Arrangement.Start"
    Arrangement.Center -> "Arrangement.Center"
    Arrangement.End -> "Arrangement.End"
    Arrangement.SpaceBetween -> "Arrangement.SpaceBetween"
    Arrangement.SpaceAround -> "Arrangement.SpaceAround"
    Arrangement.SpaceEvenly -> "Arrangement.SpaceEvenly"
    else -> ""
}


@OptIn(InternalLayoutApi::class)
fun Arrangement.Vertical.toCodeString() = when (this) {
    Arrangement.Top -> "Arrangement.Top"
    Arrangement.Center -> "Arrangement.Center"
    Arrangement.Bottom -> "Arrangement.Bottom"
    Arrangement.SpaceBetween -> "Arrangement.SpaceBetween"
    Arrangement.SpaceAround -> "Arrangement.SpaceAround"
    Arrangement.SpaceEvenly -> "Arrangement.SpaceEvenly"
    else -> ""
}

fun Alignment.Vertical.toReadableString() = when (this){
    Alignment.Top -> "Top"
    Alignment.CenterVertically -> "Center Vertically"
    Alignment.Bottom -> "Bottom"
    else -> ""
}

fun Alignment.Horizontal.toReadableString() = when (this) {
    Alignment.Start -> "Start"
    Alignment.CenterHorizontally -> "Center Horizontally"
    Alignment.End -> "End"
    else -> ""
}

fun Alignment.Vertical.toCodeString() = when (this){
    Alignment.Top -> "Alignment.Top"
    Alignment.CenterVertically -> "Alignment.CenterVertically"
    Alignment.Bottom -> "Alignment.Bottom"
    else -> ""
}

fun Alignment.Horizontal.toCodeString() = when (this) {
    Alignment.Start -> "Alignment.Start"
    Alignment.CenterHorizontally -> "Alignment.CenterHorizontally"
    Alignment.End -> "Alignment.End"
    else -> ""
}