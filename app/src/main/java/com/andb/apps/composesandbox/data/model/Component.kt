package com.andb.apps.composesandbox.data.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.InternalLayoutApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.VectorAsset
import com.andb.apps.composesandbox.util.plusElement
import java.util.*

sealed class Properties {
    data class Text (val text: String) : Properties()
    data class Icon (val icon: VectorAsset) : Properties()
    sealed class Group (open val children: List<PrototypeComponent>) : Properties() {
        @OptIn(InternalLayoutApi::class)
        data class Row (
            override val children: List<PrototypeComponent>,
            val horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
            val verticalAlignment: Alignment.Vertical = Alignment.Top
        ) : Group(children)
        @OptIn(InternalLayoutApi::class)
        data class Column (
            override val children: List<PrototypeComponent>,
            val verticalArrangement: Arrangement.Vertical = Arrangement.Top,
            val horizontalAlignment: Alignment.Horizontal = Alignment.Start
        ) : Group(children)
    }
}

data class PrototypeComponent(
    val id: String = UUID.randomUUID().toString(),
    val modifiers: List<PrototypeModifier> = emptyList(),
    val properties: Properties,
    val name: String = properties.componentName()
)

private fun Properties.Group.withChildren(children: List<PrototypeComponent> = this.children): Properties.Group {
    return when(this) {
        is Properties.Group.Column -> this.copy(children = children)
        is Properties.Group.Row -> this.copy(children = children)
    }
}

private fun Properties.componentName() = when(this) {
    is Properties.Text -> "Text"
    is Properties.Icon -> "Icon"
    is Properties.Group.Row -> "Row"
    is Properties.Group.Column -> "Column"
}

fun Properties.toComponent() =  PrototypeComponent(properties = this)

/**
 * Creates a copy of a component tree with a component added next to (or in some cases nested in) its sibling.
 * [adding] is nested as the first child of [sibling] only if sibling is an instance of [Component.Group] and [addBefore] is false.
 * Used recursively, and returns copy of component tree with no changes if [sibling] can't be found.
 * @param adding the component to add to the tree
 * @param sibling the component that [adding] is inserted next to (or or in some cases nested in)
 * @param addBefore whether [adding] should be inserted before or after [sibling]
 */
fun PrototypeComponent.plusChildInTree(adding: PrototypeComponent, sibling: PrototypeComponent, addBefore: Boolean): PrototypeComponent {
    return this.copy(properties = this.properties.run {
        when {
            // if this isn't a group, then skip
            this !is Properties.Group -> this

            // if the component that is hovered over isn't in this group, then try next level of nesting
            children.indexOf(sibling) == -1 -> withChildren(children = children.map { it.plusChildInTree(adding, sibling, addBefore) })

            // if the hovering component is a group and the user is hovering over the bottom of it, then the adding component should be added as the first item nested in the hovering component group
            sibling.properties is Properties.Group && !addBefore -> withChildren(children = children.map { child ->
                if (child == sibling) {
                    child.copy(properties = (child.properties as Properties.Group).withChildren(children = child.properties.children.plusElement(adding, 0)))
                } else {
                    child
                }
            }
            )

            // find the sibling component and add the adding component next to it
            else -> withChildren(children = children.plusElement(adding, children.indexOf(sibling) + if (!addBefore) 1 else 0))
        }
    })
}

/**
 * Creates a copy of a component tree with a component removed from it.
 * Used recursively, and returns copy of component tree with no changes if [component] can't be found.
 * @param component the component to remove from the tree
 */
fun PrototypeComponent.minusChildFromTree(component: PrototypeComponent): PrototypeComponent {
    return this.copy(properties = this.properties.run {
        when {
            this !is Properties.Group -> this
            component !in this.children -> this.withChildren(children = this.children.map { it.minusChildFromTree(component) })
            else -> this.withChildren(children = this.children - component)
        }
    })
}

/**
 * Creates a copy of a component tree with a component updated it. Finds original component in tree based on [PrototypeComponent.id]
 * Used recursively, and returns copy of component tree with no changes if [component] can't be found.
 * @param component the component to update from the tree
 */
fun PrototypeComponent.updatedChildInTree(component: PrototypeComponent): PrototypeComponent {
    return when {
        this.id == component.id -> component
        this.properties is Properties.Group -> this.copy(properties = this.properties.withChildren(children = this.properties.children.map { it.updatedChildInTree(component) }))
        else -> this
    }
}

fun PrototypeComponent.updatedModifier(modifier: PrototypeModifier): PrototypeComponent {
    val updatedModifiers = this.modifiers.map { oldMod  ->
        if (oldMod.id == modifier.id) modifier else oldMod
    }
    println("updating modifier in tree, old = ${this.modifiers}, new = $updatedModifiers")
    return this.copy(modifiers = updatedModifiers)
}

fun PrototypeComponent.findByIDInTree(id: String): PrototypeComponent? {
    if (this.id == id) return this
    if (this.properties !is Properties.Group) return null
    for (child in this.properties.children) {
        child.findByIDInTree(id)?.let { return it }
    }
    return null
}

fun PrototypeComponent.findModifierByIDInTree(id: String): PrototypeModifier? {
    //try to find the id in this component's modifiers
    modifiers.find { it.id == id }?.let { return it }

    //if not try to find it in children
    if (this.properties !is Properties.Group) return null
    for (child in this.properties.children) {
        child.findModifierByIDInTree(id)?.let { return it }
    }
    return null
}