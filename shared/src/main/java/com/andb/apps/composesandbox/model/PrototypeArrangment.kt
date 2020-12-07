package com.andb.apps.composesandbox.model

import kotlinx.serialization.Serializable

@Serializable
sealed class PrototypeArrangement {
    @Serializable sealed class Horizontal : PrototypeArrangement() {
        @Serializable object Start : Horizontal()
        @Serializable object Center : Horizontal()
        @Serializable object End : Horizontal()
    }
    @Serializable sealed class Vertical : PrototypeArrangement() {
        @Serializable object Top : Vertical()
        @Serializable object Center : Vertical()
        @Serializable object Bottom : Vertical()
    }
    @Serializable sealed class Both : PrototypeArrangement() {
        @Serializable object SpaceBetween : Both()
        @Serializable object SpaceEvenly : Both()
        @Serializable object SpaceAround : Both()
    }
}

sealed class PrototypeAlignment {
    @Serializable sealed class Horizontal : PrototypeAlignment() {
        @Serializable object Start : Horizontal()
        @Serializable object CenterHorizontally : Horizontal()
        @Serializable object End : Horizontal()
    }
    @Serializable sealed class Vertical : PrototypeAlignment() {
        @Serializable object Top : Vertical()
        @Serializable object CenterVertically : Vertical()
        @Serializable object Bottom : Vertical()
    }
}

val horizontalArrangements = listOf(PrototypeArrangement.Horizontal.Start, PrototypeArrangement.Horizontal.Center, PrototypeArrangement.Horizontal.End)
val verticalArrangements = listOf(PrototypeArrangement.Vertical.Top, PrototypeArrangement.Vertical.Center, PrototypeArrangement.Vertical.Bottom)
val bothArrangements = listOf(PrototypeArrangement.Both.SpaceBetween, PrototypeArrangement.Both.SpaceEvenly, PrototypeArrangement.Both.SpaceAround)
val verticalAlignments = listOf(PrototypeAlignment.Vertical.Top, PrototypeAlignment.Vertical.CenterVertically, PrototypeAlignment.Vertical.Bottom)
val horizontalAlignments = listOf(PrototypeAlignment.Horizontal.Start, PrototypeAlignment.Horizontal.CenterHorizontally, PrototypeAlignment.Horizontal.End)


fun PrototypeArrangement.toCode(): String = when(this) {
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


fun PrototypeAlignment.toCode() = when (this){
    PrototypeAlignment.Vertical.Top -> "Alignment.Top"
    PrototypeAlignment.Vertical.CenterVertically -> "Alignment.CenterVertically"
    PrototypeAlignment.Vertical.Bottom -> "Alignment.Bottom"
    PrototypeAlignment.Horizontal.Start -> "Alignment.Start"
    PrototypeAlignment.Horizontal.CenterHorizontally -> "Alignment.CenterHorizontally"
    PrototypeAlignment.Horizontal.End -> "Alignment.End"
    else -> ""
}
