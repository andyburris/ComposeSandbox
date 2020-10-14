package com.andb.apps.composesandbox.data.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.InternalLayoutApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.VectorAsset
import com.andb.apps.composesandbox.util.plusElement
import java.util.*

sealed class Component (open val id: String, open val name: String, open val modifiers: List<PrototypeModifier>) {

    fun withModifiers(modifiers: List<PrototypeModifier>) = when (this) {
        is Text -> this.copy(modifiers = modifiers)
        is Icon -> this.copy(modifiers = modifiers)
        is Group.Column -> this.copy(modifiers = modifiers)
        is Group.Row -> this.copy(modifiers = modifiers)
    }

    data class Text(
        val text: String,
        override val id: String = UUID.randomUUID().toString(),
        override val name: String = "Text",
        override val modifiers: List<PrototypeModifier> = emptyList()
    ) : Component(id, name, modifiers)

    data class Icon(
        val icon: VectorAsset,
        override val id: String = UUID.randomUUID().toString(),
        override val name: String = "Icon",
        override val modifiers: List<PrototypeModifier> = emptyList()
    ) : Component(id, name, modifiers)

    sealed class Group (
        open val children: List<Component>,
        id: String,
        name: String,
        modifiers: List<PrototypeModifier>
    ) : Component(id, name, modifiers) {

        @OptIn(InternalLayoutApi::class)
        data class Column  (
            override val children: List<Component>,
            val verticalArrangement: Arrangement.Vertical = Arrangement.Top,
            val horizontalAlignment: Alignment.Horizontal = Alignment.Start,
            override val id: String = UUID.randomUUID().toString(),
            override val name: String = "Column",
            override val modifiers: List<PrototypeModifier> = emptyList()
        ) : Group(children, id, name, modifiers)

        @OptIn(InternalLayoutApi::class)
        data class Row (
            override val children: List<Component>,
            val horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
            val verticalAlignment: Alignment.Vertical = Alignment.Top,
            override val id: String = UUID.randomUUID().toString(),
            override val name: String = "Row",
            override val modifiers: List<PrototypeModifier> = emptyList()
        ) : Group(children, id, name, modifiers)

        fun withChildren(children: List<Component> = this.children): Component.Group {
            return when(this) {
                is Column -> this.copy(children = children)
                is Row -> this.copy(children = children)
            }
        }
    }
}

data class PrototypeComponent(
    val id: String = UUID.randomUUID().toString(),
    val modifiers: List<PrototypeModifier> = emptyList(),
    val properties: Properties,
    val name: String = properties.componentName()
) {
    sealed class Properties {
        data class TextProperties (val text: String) : Properties()
        data class IconProperties (val icon: VectorAsset) : Properties()
        sealed class GroupProperties (open val children: List<Component>) : Properties() {
            data class RowProperties (override val children: List<Component>, val horizontalArrangement: Arrangement, val verticalAlignment: Alignment) : GroupProperties(children)
            data class ColumnProperties (override val children: List<Component>, val verticalArrangement: Arrangement, val horizontalAlignment: Alignment) : GroupProperties(children)
        }
    }

    fun isGroup() = properties is Properties.GroupProperties
    fun isText() = properties is Properties.TextProperties
    fun isIcon() = properties is Properties.IconProperties
    fun isRow() = properties is Properties.GroupProperties.RowProperties
    fun isColumn() = properties is Properties.GroupProperties.ColumnProperties
}

private fun PrototypeComponent.Properties.componentName() = when(this) {
    is PrototypeComponent.Properties.TextProperties -> "Text"
    is PrototypeComponent.Properties.IconProperties -> "Icon"
    is PrototypeComponent.Properties.GroupProperties.RowProperties -> "Row"
    is PrototypeComponent.Properties.GroupProperties.ColumnProperties -> "Column"
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
        children.indexOf(sibling) == -1 -> this.withChildren(children = children.map { it.plusChildInTree(adding, sibling, addBefore) })

        // if the hovering component is a group and the user is hovering over the bottom of it, then the adding component should be added as the first item nested in the hovering component group
        sibling is Component.Group && !addBefore -> this.withChildren(children = children
            .map { child ->
                if (child == sibling) {
                    (child as Component.Group).withChildren(children = child.children.plusElement(adding, 0))
                } else {
                    child
                }
            }
        )

        // find the sibling component and add the adding component next to it
        else -> this.withChildren(children = children.plusElement(adding, children.indexOf(sibling) + if (!addBefore) 1 else 0))
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
        component !in this.children -> this.withChildren(children = this.children.map { it.minusChildFromTree(component) })
        else -> this.withChildren(children = this.children - component)
    }
}

/**
 * Creates a copy of a component tree with a component updated it. Finds original component in tree based on [Component.id]
 * Used recursively, and returns copy of component tree with no changes if [component] can't be found.
 * @param component the component to update from the tree
 */
fun Component.updateChildInTree(component: Component): Component {
    return when {
        this.id == component.id -> component
        this is Component.Group -> this.withChildren(children = children.map { it.updateChildInTree(component) })
        else -> this
    }
}