package com.andb.apps.composesandbox.data.model

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BorderStyle
import androidx.compose.material.icons.filled.FlipToFront
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import com.andb.apps.composesandbox.R
import java.util.*

sealed class PrototypeModifier(open val id: String) {
    sealed class Padding(id: String) : PrototypeModifier(id) {
        data class Individual(val start: Dp, val end: Dp, val top: Dp, val bottom: Dp, override val id: String = UUID.randomUUID().toString()) : Padding(id)
        data class Sides(val horizontal: Dp, val vertical: Dp, override val id: String = UUID.randomUUID().toString()) : Padding(id)
        data class All(val padding: Dp, override val id: String = UUID.randomUUID().toString()) : Padding(id)
    }
    data class Border(val strokeWidth: Dp, val color: PrototypeColor, val cornerRadius: Dp, override val id: String = UUID.randomUUID().toString()) : PrototypeModifier(id)
    data class Width(val width: Dp, override val id: String = UUID.randomUUID().toString()) : PrototypeModifier(id)
    data class Height(val height: Dp, override val id: String = UUID.randomUUID().toString()) : PrototypeModifier(id)
    data class FillMaxWidth(override val id: String = UUID.randomUUID().toString()) : PrototypeModifier(id)
    data class FillMaxHeight(override val id: String = UUID.randomUUID().toString()) : PrototypeModifier(id)
}

@Composable
val PrototypeModifier.icon: VectorAsset
    get() = when (this) {
        is PrototypeModifier.Padding -> Icons.Default.FlipToFront
        is PrototypeModifier.Border -> Icons.Default.BorderStyle
        is PrototypeModifier.Height -> Icons.Default.Height
        is PrototypeModifier.Width -> vectorResource(id = R.drawable.ic_width)
        is PrototypeModifier.FillMaxWidth -> vectorResource(id = R.drawable.ic_max_width)
        is PrototypeModifier.FillMaxHeight -> Icons.Default.UnfoldMore
    }

val PrototypeModifier.name: String
    get() = when(this) {
        is PrototypeModifier.Padding -> "Padding"
        is PrototypeModifier.Border -> "Border"
        is PrototypeModifier.Height -> "Height"
        is PrototypeModifier.Width -> "Width"
        is PrototypeModifier.FillMaxWidth -> "Fill Max Width"
        is PrototypeModifier.FillMaxHeight -> "Fill Max Height"
    }

val PrototypeModifier.summary: String
    get() = when(this) {
        is PrototypeModifier.Padding.Individual -> "Start: $start, End: $end, Top: $top, Bottom: $bottom"
        is PrototypeModifier.Padding.Sides -> "Horizontal: $horizontal, Vertical: $vertical"
        is PrototypeModifier.Padding.All -> "All: $padding"
        is PrototypeModifier.Border -> "Stroke: $strokeWidth, Corners: $cornerRadius"
        is PrototypeModifier.Height -> "$height"
        is PrototypeModifier.Width -> "$width"
        is PrototypeModifier.FillMaxWidth -> ""
        is PrototypeModifier.FillMaxHeight -> ""
    }

@Composable
fun List<PrototypeModifier>.toModifier() : Modifier {
    return this.fold<PrototypeModifier, Modifier>(Modifier) { acc, prototypeModifier ->
        acc then when (prototypeModifier) {
            is PrototypeModifier.Padding.Individual -> Modifier.padding(start = prototypeModifier.start, end = prototypeModifier.end, top = prototypeModifier.top, bottom = prototypeModifier.bottom)
            is PrototypeModifier.Padding.Sides -> Modifier.padding(horizontal = prototypeModifier.horizontal, vertical = prototypeModifier.vertical)
            is PrototypeModifier.Padding.All -> Modifier.padding(all = prototypeModifier.padding)
            is PrototypeModifier.Border -> Modifier.border(prototypeModifier.strokeWidth, prototypeModifier.color.renderColor(), shape = RoundedCornerShape(prototypeModifier.cornerRadius))
            is PrototypeModifier.Height -> Modifier.height(prototypeModifier.height)
            is PrototypeModifier.Width -> Modifier.width(prototypeModifier.width)
            is PrototypeModifier.FillMaxWidth -> Modifier.fillMaxWidth()
            is PrototypeModifier.FillMaxHeight -> Modifier.fillMaxHeight()
        }
    }
}


fun PrototypeModifier.Padding.toAll(): PrototypeModifier.Padding.All = when (this) {
    is PrototypeModifier.Padding.All -> this
    is PrototypeModifier.Padding.Sides -> PrototypeModifier.Padding.All(
        listOf(this.horizontal, this.vertical)
            .groupBy { it }
            .maxByOrNull { it.value.size }!!
            .key,
        this.id
    )
    is PrototypeModifier.Padding.Individual -> PrototypeModifier.Padding.All(
        listOf(this.top, this.bottom, this.start, this.end)
            .groupBy { it }
            .maxByOrNull { it.value.size }!!
            .key,
        this.id
    )
}

fun PrototypeModifier.Padding.toSides(): PrototypeModifier.Padding.Sides = when (this) {
    is PrototypeModifier.Padding.All -> PrototypeModifier.Padding.Sides(this.padding, this.padding, this.id)
    is PrototypeModifier.Padding.Sides -> this
    is PrototypeModifier.Padding.Individual -> PrototypeModifier.Padding.Sides(
        listOf(this.start, this.end)
            .groupBy { it }
            .maxByOrNull { it.value.size }!!
            .key,
        listOf(this.top, this.bottom)
            .groupBy { it }
            .maxByOrNull { it.value.size }!!
            .key,
        this.id
    )
}

fun PrototypeModifier.Padding.toIndividual(): PrototypeModifier.Padding.Individual = when (this) {
    is PrototypeModifier.Padding.All -> PrototypeModifier.Padding.Individual(this.padding, this.padding, this.padding, this.padding, this.id)
    is PrototypeModifier.Padding.Sides -> PrototypeModifier.Padding.Individual(this.horizontal, this.horizontal, this.vertical, this.vertical, this.id)
    is PrototypeModifier.Padding.Individual -> this
}

fun List<PrototypeModifier>.toCode(): String {
    if (isEmpty()) return ""

    return buildString {
        append(", modifier = Modifier.")
        append(this@toCode.joinToString(".") { it.toCode() })
    }
}

fun PrototypeModifier.toCode() = when (this) {
    is PrototypeModifier.Border -> "border(width = $strokeWidth, color = ${color.toCode()})"
    is PrototypeModifier.Padding.Individual -> "padding(start = $start, end = $end, top = $top, bottom = $bottom)"
    is PrototypeModifier.Padding.Sides -> "padding(horizontal = $horizontal, vertical = $vertical)"
    is PrototypeModifier.Padding.All -> "padding($padding)"
    is PrototypeModifier.Height -> "height(height = $height)"
    is PrototypeModifier.Width -> "width(width = $width)"
    is PrototypeModifier.FillMaxWidth -> "fillMaxWidth()"
    is PrototypeModifier.FillMaxHeight -> "fillMaxHeight()"
}