package com.andb.apps.composesandbox.data.model

import androidx.compose.ui.graphics.vector.VectorAsset

sealed class Component (open val name: String) {
    data class Text(val text: String, override val name: String = "Text") : Component(name)
    data class Icon(val icon: VectorAsset, override val name: String = "Icon") : Component(name)
    sealed class Group (open val children: List<Component>, name: String) : Component(name) {
        data class Column(override val children: List<Component>) : Group(children, "Column")
        data class Row(override val children: List<Component>) : Group(children, "Row")
    }
}