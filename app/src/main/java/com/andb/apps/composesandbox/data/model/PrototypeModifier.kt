package com.andb.apps.composesandbox.data.model

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BorderStyle
import androidx.compose.material.icons.filled.FlipToFront
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.util.toHexString
import java.util.*

sealed class PrototypeModifier(open val id: String) {
    sealed class Padding(id: String) : PrototypeModifier(id) {
        data class Individual(val top: Dp, val bottom: Dp, val start: Dp, val end: Dp, override val id: String = UUID.randomUUID().toString()) : Padding(id)
        data class Sides(val horizontal: Dp, val vertical: Dp, override val id: String = UUID.randomUUID().toString()) : Padding(id)
        data class All(val padding: Dp, override val id: String = UUID.randomUUID().toString()) : Padding(id)
    }

    data class Border(val strokeWidth: Dp, val color: Color, val cornerRadius: Dp, override val id: String = UUID.randomUUID().toString()) : PrototypeModifier(id)
}

val PrototypeModifier.icon: VectorAsset
    get() = when (this) {
        is PrototypeModifier.Padding -> Icons.Default.FlipToFront
        is PrototypeModifier.Border -> Icons.Default.BorderStyle
    }

val PrototypeModifier.name: String
    get() = when(this) {
        is PrototypeModifier.Padding -> "Padding"
        is PrototypeModifier.Border -> "Border"
    }

val PrototypeModifier.summary: String
    get() = when(this) {
        is PrototypeModifier.Padding.Individual -> "Start: $start, End: $end, Top: $top, Bottom: $bottom"
        is PrototypeModifier.Padding.Sides -> "Horizontal: $horizontal, Vertical: $vertical"
        is PrototypeModifier.Padding.All -> "All: $padding"
        is PrototypeModifier.Border -> "Stroke: $strokeWidth, Corners: $cornerRadius, Color: ${color.toArgb().toHexString()}"
    }

fun List<PrototypeModifier>.toModifier() : Modifier {
    return this.fold<PrototypeModifier, Modifier>(Modifier) { acc, prototypeModifier ->
        acc then when (prototypeModifier) {
            is PrototypeModifier.Padding.Individual -> Modifier.padding(start = prototypeModifier.start, end = prototypeModifier.end, top = prototypeModifier.top, bottom = prototypeModifier.bottom)
            is PrototypeModifier.Padding.Sides -> Modifier.padding(horizontal = prototypeModifier.horizontal, vertical = prototypeModifier.vertical)
            is PrototypeModifier.Padding.All -> Modifier.padding(all = prototypeModifier.padding)
            is PrototypeModifier.Border -> Modifier.border(prototypeModifier.strokeWidth, prototypeModifier.color, shape = RoundedCornerShape(prototypeModifier.cornerRadius))
        }
    }
}