package com.andb.apps.composesandbox.data.model

import androidx.compose.ui.graphics.vector.VectorAsset
import java.util.*

sealed class Component (open val id: String, open val name: String) {
    data class Text(val text: String, override val id: String = UUID.randomUUID().toString(), override val name: String = "Text") : Component(id, name)
    data class Icon(val icon: VectorAsset, override val id: String = UUID.randomUUID().toString(), override val name: String = "Icon") : Component(id, name)
    sealed class Group (open val children: List<Component>, id: String, name: String) : Component(id, name) {
        class Column(override val children: List<Component>, override val id: String = UUID.randomUUID().toString()) : Group(children, id, "Column")
        class Row(override val children: List<Component>, override val id: String = UUID.randomUUID().toString()) : Group(children, id, "Row")
        fun copy(children: List<Component> = this.children, id: String = this.id, name: String = this.name): Component.Group {
            return when(this) {
                is Column -> Column(children, id)
                is Row -> Row(children, id)
            }
        }
    }
}