package com.andb.apps.composesandbox.data.model

import androidx.compose.ui.graphics.vector.VectorAsset
import com.andb.apps.composesandbox.util.plusElement
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

/**
 * Creates a copy of a component tree with a component added next to (or in some cases nested in) its sibling.
 * [adding] is nested as the first child of [sibling] only if sibling is an instance of [Component.Group] and [addBefore] is false.
 * Used recursively, and returns copy of component tree with no changes if [sibling] can't be found.
 * @param adding the component to add to the tree
 * @param sibling the component that [adding] is inserted next to (or or in some cases nested in)
 * @param addBefore whether [adding] should be inserted before or after [sibling]
 */
fun Component.plusChildInTree(adding: Component, sibling: Component, addBefore: Boolean): Component {
    return when {
        // if this isn't a group, then skip
        this !is Component.Group -> this

        // if the component that is hovered over isn't in this group, then try next level of nesting
        children.indexOf(sibling) == -1 -> this.copy(children = children.map { it.plusChildInTree(adding, sibling, addBefore) })

        // if the hovering component is a group and the user is hovering over the bottom of it, then the adding component should be added as the first item nested in the hovering component group
        sibling is Component.Group && !addBefore -> this.copy(children = children
            .map { child ->
                if (child == sibling) {
                    (child as Component.Group).copy(children = child.children.plusElement(adding, 0))
                } else {
                    child
                }
            }
        )

        // find the sibling component and add the adding component next to it
        else -> this.copy(children = children.plusElement(adding, children.indexOf(sibling) + if (!addBefore) 1 else 0))
    }
}

/**
 * Creates a copy of a component tree with a component removed from it.
 * Used recursively, and returns copy of component tree with no changes if [component] can't be found.
 * @param component the component to remove from the tree
 */
fun Component.minusChildFromTree(component: Component): Component {
    return when {
        this !is Component.Group -> this
        component !in this.children -> this.copy(children = this.children.map { it.minusChildFromTree(component) })
        else -> this.copy(children = this.children - component)
    }
}