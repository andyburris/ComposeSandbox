package com.andb.apps.composesandboxdata.model

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
sealed class PrototypeModifier {
    abstract val id: String
    @Serializable sealed class Padding : PrototypeModifier() {
        @Serializable data class Individual(val start: Int, val end: Int, val top: Int, val bottom: Int, override val id: String = UUID.randomUUID().toString()) : Padding()
        @Serializable data class Sides(val horizontal: Int, val vertical: Int, override val id: String = UUID.randomUUID().toString()) : Padding()
        @Serializable data class All(val padding: Int, override val id: String = UUID.randomUUID().toString()) : Padding()
    }
    @Serializable data class Border(val strokeWidth: Int, val color: PrototypeColor, val cornerRadius: Int, override val id: String = UUID.randomUUID().toString()) : PrototypeModifier()
    @Serializable data class Background(val color: PrototypeColor, val cornerRadius: Int, override val id: String = UUID.randomUUID().toString()) : PrototypeModifier()
    @Serializable data class Width(val width: Int, override val id: String = UUID.randomUUID().toString()) : PrototypeModifier()
    @Serializable data class Height(val height: Int, override val id: String = UUID.randomUUID().toString()) : PrototypeModifier()
    @Serializable sealed class Size : PrototypeModifier() {
        @Serializable data class Individual(val width: Int, val height: Int, override val id: String = UUID.randomUUID().toString()) : Size()
        @Serializable data class All(val size: Int, override val id: String = UUID.randomUUID().toString()) : Size()
        fun toAll() = when(this) {
            is Individual -> All(width, id)
            is All -> this
        }
        fun toIndividual() = when(this) {
            is Individual -> this
            is All -> Individual(size, size, id)
        }

    }
    @Serializable data class FillMaxWidth(override val id: String = UUID.randomUUID().toString()) : PrototypeModifier()
    @Serializable data class FillMaxHeight(override val id: String = UUID.randomUUID().toString()) : PrototypeModifier()
    @Serializable data class FillMaxSize(override val id: String = UUID.randomUUID().toString()) : PrototypeModifier()
}

val PrototypeModifier.name: String
    get() = when(this) {
        is PrototypeModifier.Padding -> "Padding"
        is PrototypeModifier.Border -> "Border"
        is PrototypeModifier.Background -> "Background"
        is PrototypeModifier.Height -> "Height"
        is PrototypeModifier.Width -> "Width"
        is PrototypeModifier.Size -> "Size"
        is PrototypeModifier.FillMaxWidth -> "Fill Max Width"
        is PrototypeModifier.FillMaxHeight -> "Fill Max Height"
        is PrototypeModifier.FillMaxSize -> "Fill Max Size"
    }

val PrototypeModifier.summary: String
    get() = when(this) {
        is PrototypeModifier.Padding.Individual -> "Start: $start, End: $end, Top: $top, Bottom: $bottom"
        is PrototypeModifier.Padding.Sides -> "Horizontal: $horizontal, Vertical: $vertical"
        is PrototypeModifier.Padding.All -> "All: $padding"
        is PrototypeModifier.Border -> "Stroke: $strokeWidth, Corners: $cornerRadius"
        is PrototypeModifier.Background -> "Corners: $cornerRadius"
        is PrototypeModifier.Height -> "$height"
        is PrototypeModifier.Width -> "$width"
        is PrototypeModifier.Size.All -> "$size"
        is PrototypeModifier.Size.Individual -> "Width: $width, Height: $height"
        is PrototypeModifier.FillMaxWidth -> ""
        is PrototypeModifier.FillMaxHeight -> ""
        is PrototypeModifier.FillMaxSize -> ""
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

