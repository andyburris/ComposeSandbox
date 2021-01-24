package com.andb.apps.composesandboxdata.model

import kotlinx.serialization.Serializable

@Serializable
sealed class PrototypeArrangement {
    @Serializable sealed class Horizontal : PrototypeArrangement() {
        @Serializable object Start : Horizontal()
        @Serializable object Center : Horizontal()
        @Serializable object End : Horizontal()
        @Serializable data class SpacedBy(val space: Int, val alignment: PrototypeAlignment.Horizontal) : Horizontal()
    }
    @Serializable sealed class Vertical : PrototypeArrangement() {
        @Serializable object Top : Vertical()
        @Serializable object Center : Vertical()
        @Serializable object Bottom : Vertical()
        @Serializable data class SpacedBy(val space: Int, val alignment: PrototypeAlignment.Vertical) : Vertical()
    }
    @Serializable sealed class Both : PrototypeArrangement() {
        @Serializable object SpaceBetween : Both()
        @Serializable object SpaceEvenly : Both()
        @Serializable object SpaceAround : Both()
    }
}

@Serializable sealed class PrototypeAlignment {
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

val horizontalArrangements = listOf(PrototypeArrangement.Horizontal.Start, PrototypeArrangement.Horizontal.Center, PrototypeArrangement.Horizontal.End, PrototypeArrangement.Horizontal.SpacedBy(16, PrototypeAlignment.Horizontal.Start))
val verticalArrangements = listOf(PrototypeArrangement.Vertical.Top, PrototypeArrangement.Vertical.Center, PrototypeArrangement.Vertical.Bottom, PrototypeArrangement.Vertical.SpacedBy(16, PrototypeAlignment.Vertical.Top))
val bothArrangements = listOf(PrototypeArrangement.Both.SpaceBetween, PrototypeArrangement.Both.SpaceEvenly, PrototypeArrangement.Both.SpaceAround)
val verticalAlignments = listOf(PrototypeAlignment.Vertical.Top, PrototypeAlignment.Vertical.CenterVertically, PrototypeAlignment.Vertical.Bottom)
val horizontalAlignments = listOf(PrototypeAlignment.Horizontal.Start, PrototypeAlignment.Horizontal.CenterHorizontally, PrototypeAlignment.Horizontal.End)


fun PrototypeArrangement.toCode(): String = when(this) {
    PrototypeArrangement.Horizontal.Start -> "Arrangement.Start"
    PrototypeArrangement.Horizontal.Center -> "Arrangement.Center"
    PrototypeArrangement.Horizontal.End -> "Arrangement.End"
    is PrototypeArrangement.Horizontal.SpacedBy -> "Arrangement.spacedBy(space = $space.dp, alignment = ${alignment.toCode()})"
    PrototypeArrangement.Vertical.Top -> "Arrangement.Top"
    PrototypeArrangement.Vertical.Center -> "Arrangement.Center"
    PrototypeArrangement.Vertical.Bottom -> "Arrangement.Bottom"
    is PrototypeArrangement.Vertical.SpacedBy -> "Arrangement.spacedBy(space = $space.dp, alignment = ${alignment.toCode()})"
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
