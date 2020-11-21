package com.andb.apps.composesandbox.model

import kotlinx.serialization.Serializable

@Serializable
sealed class PrototypeArrangement {
    @Serializable sealed class Horizontal : PrototypeArrangement() {
        object Start : Horizontal()
        object Center : Horizontal()
        object End : Horizontal()
    }
    @Serializable sealed class Vertical : PrototypeArrangement() {
        object Top : Vertical()
        object Center : Vertical()
        object Bottom : Vertical()
    }
    @Serializable sealed class Both : PrototypeArrangement() {
        object SpaceBetween : Both()
        object SpaceEvenly : Both()
        object SpaceAround : Both()
    }
}

sealed class PrototypeAlignment {
    @Serializable sealed class Horizontal : PrototypeAlignment() {
        object Start : Horizontal()
        object CenterHorizontally : Horizontal()
        object End : Horizontal()
    }
    @Serializable sealed class Vertical : PrototypeAlignment() {
        object Top : Vertical()
        object CenterVertically : Vertical()
        object Bottom : Vertical()
    }
}

val horizontalArrangements = listOf(PrototypeArrangement.Horizontal.Start, PrototypeArrangement.Horizontal.Center, PrototypeArrangement.Horizontal.End)
val verticalArrangements = listOf(PrototypeArrangement.Vertical.Top, PrototypeArrangement.Vertical.Center, PrototypeArrangement.Vertical.Bottom)
val bothArrangements = listOf(PrototypeArrangement.Both.SpaceBetween, PrototypeArrangement.Both.SpaceEvenly, PrototypeArrangement.Both.SpaceAround)
val verticalAlignments = listOf(PrototypeAlignment.Vertical.Top, PrototypeAlignment.Vertical.CenterVertically, PrototypeAlignment.Vertical.Bottom)
val horizontalAlignments = listOf(PrototypeAlignment.Horizontal.Start, PrototypeAlignment.Horizontal.CenterHorizontally, PrototypeAlignment.Horizontal.End)


fun PrototypeArrangement.toCodeString(): String = when(this) {
    PrototypeArrangement.Horizontal.Start -> "Arrangement.Start"
    PrototypeArrangement.Horizontal.Center -> "Arrangement.Center"
    PrototypeArrangement.Horizontal.End -> "Arrangement.End"
    PrototypeArrangement.Vertical.Top -> "Arrangement.Top"
    PrototypeArrangement.Vertical.Center -> "Arrangement.Center"
    PrototypeArrangement.Vertical.Bottom -> "Arrangement.Bottom"
    PrototypeArrangement.Both.SpaceBetween -> "Arrangement.SpaceBetween"
    PrototypeArrangement.Both.SpaceEvenly -> "Arrangement.SpaceEvenly"
    PrototypeArrangement.Both.SpaceAround -> "Arrangement.SpaceAround"
}


fun PrototypeAlignment.Vertical.toCodeString() = when (this){
    PrototypeAlignment.Vertical.Top -> "Alignment.Top"
    PrototypeAlignment.Vertical.CenterVertically -> "Alignment.CenterVertically"
    PrototypeAlignment.Vertical.Bottom -> "Alignment.Bottom"
    else -> ""
}

fun PrototypeAlignment.Horizontal.toCodeString() = when (this) {
    PrototypeAlignment.Horizontal.Start -> "Alignment.Start"
    PrototypeAlignment.Horizontal.CenterHorizontally -> "Alignment.CenterHorizontally"
    PrototypeAlignment.Horizontal.End -> "Alignment.End"
    else -> ""
}