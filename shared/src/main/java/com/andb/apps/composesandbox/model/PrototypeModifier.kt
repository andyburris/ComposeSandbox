package com.andb.apps.composesandbox.model

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
sealed class PrototypeModifier(open val id: String) {
    sealed class Padding(id: String) : PrototypeModifier(id) {
        data class Individual(val start: Int, val end: Int, val top: Int, val bottom: Int, override val id: String = UUID.randomUUID().toString()) : Padding(id)
        data class Sides(val horizontal: Int, val vertical: Int, override val id: String = UUID.randomUUID().toString()) : Padding(id)
        data class All(val padding: Int, override val id: String = UUID.randomUUID().toString()) : Padding(id)
    }
    data class Border(val strokeWidth: Int, val color: PrototypeColor, val cornerRadius: Int, override val id: String = UUID.randomUUID().toString()) : PrototypeModifier(id)
    data class Width(val width: Int, override val id: String = UUID.randomUUID().toString()) : PrototypeModifier(id)
    data class Height(val height: Int, override val id: String = UUID.randomUUID().toString()) : PrototypeModifier(id)
    data class FillMaxWidth(override val id: String = UUID.randomUUID().toString()) : PrototypeModifier(id)
    data class FillMaxHeight(override val id: String = UUID.randomUUID().toString()) : PrototypeModifier(id)
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
    is PrototypeModifier.Border -> "border(width = $strokeWidth.dp, color = ${color.toCode()})"
    is PrototypeModifier.Padding.Individual -> "padding(start = $start.dp, end = $end.dp, top = $top.dp, bottom = $bottom.dp)"
    is PrototypeModifier.Padding.Sides -> "padding(horizontal = $horizontal.dp, vertical = $vertical.dp)"
    is PrototypeModifier.Padding.All -> "padding($padding.dp)"
    is PrototypeModifier.Height -> "height(height = $height.dp)"
    is PrototypeModifier.Width -> "width(width = $width.dp)"
    is PrototypeModifier.FillMaxWidth -> "fillMaxWidth()"
    is PrototypeModifier.FillMaxHeight -> "fillMaxHeight()"
}