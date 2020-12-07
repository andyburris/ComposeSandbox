package com.andb.apps.composesandbox.model

import com.andb.apps.composesandbox.plusElement
import com.andb.apps.composesandbox.toCamelCase
import com.andb.apps.composesandbox.toPascalCase
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
sealed class Properties {
    @Serializable
    data class Text(val text: String, val weight: Weight = Weight.Normal, val size: Int = 14, val color: PrototypeColor = PrototypeColor.ThemeColor.OnBackground) : Properties() {
        enum class Weight {
            Thin, ExtraLight, Light, Normal, Medium, SemiBold, Bold, ExtraBold, Black,
        }
    }

    @Serializable
    data class Icon(val icon: PrototypeIcon, val tint: PrototypeColor = PrototypeColor.ThemeColor.OnBackground) : Properties()

    @Serializable
    sealed class Group : Properties() {
        abstract val children: List<PrototypeComponent>

        @Serializable
        data class Row(
            override val children: List<PrototypeComponent> = emptyList(),
            val horizontalArrangement: PrototypeArrangement = PrototypeArrangement.Horizontal.Start,
            val verticalAlignment: PrototypeAlignment.Vertical = PrototypeAlignment.Vertical.Top,
        ) : Group()

        @Serializable
        data class Column(
            override val children: List<PrototypeComponent> = emptyList(),
            val verticalArrangement: PrototypeArrangement = PrototypeArrangement.Vertical.Top,
            val horizontalAlignment: PrototypeAlignment.Horizontal = PrototypeAlignment.Horizontal.Start,
        ) : Group()

        @Serializable
        data class Box(override val children: List<PrototypeComponent> = emptyList()) : Group()
    }

    @Serializable
    sealed class Slotted : Properties() {
        abstract val slots: List<Slot>

        @Serializable
        data class ExtendedFloatingActionButton(override val slots: List<Slot> = listOf(Slot("Icon"), Slot("Text", optional = false))) : Slotted()
    }
}

@Serializable
data class Slot(val name: String, val tree: PrototypeComponent = PrototypeComponent(properties = Properties.Group.Box()), val optional: Boolean = true, val enabled: Boolean = true)

@Serializable
data class PrototypeComponent(
    val id: String = UUID.randomUUID().toString(),
    val modifiers: List<PrototypeModifier> = emptyList(),
    val properties: Properties,
    val name: String,
)

/**
 * Convenience function to initialize a [PrototypeComponent] with the default name for its [properties]
 */
fun PrototypeComponent(
    id: String = UUID.randomUUID().toString(),
    modifiers: List<PrototypeModifier> = emptyList(),
    properties: Properties,
) = PrototypeComponent(id, modifiers, properties, properties.componentName())

private fun Properties.Group.withChildren(children: List<PrototypeComponent> = this.children): Properties.Group {
    return when (this) {
        is Properties.Group.Column -> this.copy(children = children)
        is Properties.Group.Row -> this.copy(children = children)
        is Properties.Group.Box -> this.copy(children = children)
    }
}

private fun Properties.Slotted.withSlots(slots: List<Slot>): Properties.Slotted {
    return when (this) {
        is Properties.Slotted.ExtendedFloatingActionButton -> this.copy(slots = slots)
    }
}

private fun Properties.componentName() = when (this) {
    is Properties.Text -> "Text"
    is Properties.Icon -> "Icon"
    is Properties.Group.Row -> "Row"
    is Properties.Group.Column -> "Column"
    is Properties.Group.Box -> "Box"
    is Properties.Slotted.ExtendedFloatingActionButton -> "Extended FAB"
}

fun Properties.toComponent() = PrototypeComponent(properties = this)

/**
 * Creates a copy of a component tree with a component added next to (or in some cases nested in) its sibling.
 * [adding] is nested as the first child of [sibling] only if sibling is an instance of [Component.Group] and [addBefore] is false.
 * Used recursively, and returns copy of component tree with no changes if [sibling] can't be found.
 * @param adding the component to add to the tree
 * @param sibling the component that [adding] is inserted next to (or or in some cases nested in)
 * @param addBefore whether [adding] should be inserted before or after [sibling]
 */
fun PrototypeComponent.plusChildInTree(adding: PrototypeComponent, parent: PrototypeComponent, indexInParent: Int): PrototypeComponent {
    println("adding child to tree - adding = $adding, parent = $parent, indexInParent = $indexInParent, this = $this")
    return this.copy(properties = this.properties.run {
        when {
            this@plusChildInTree == parent -> {
                if (this !is Properties.Group) throw Error("Can only add a child to a component with Properties.Group")
                this.withChildren(children.plusElement(adding, indexInParent))
            }
            this is Properties.Slotted -> {
                val newSlots = slots.map { slot ->
                    val newTree = slot.tree.plusChildInTree(adding, parent, indexInParent)
                    println("old = ${slot.tree}")
                    println("new = $newTree")
                    slot.copy(tree = newTree)
                }
                this.withSlots(newSlots)
            }
            this is Properties.Group -> {
                withChildren(children = children.map { it.plusChildInTree(adding, parent, indexInParent) })
            }
            else -> this
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
            this is Properties.Slotted -> this.withSlots(slots = this.slots.map { slot -> slot.copy(tree = slot.tree.minusChildFromTree(component)) })
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
        this.properties is Properties.Slotted -> this.copy(properties = this.properties.withSlots(slots = this.properties.slots.map { it.copy(tree = it.tree.updatedChildInTree(component)) }))
        else -> this
    }
}

fun PrototypeComponent.updatedModifier(modifier: PrototypeModifier): PrototypeComponent {
    val updatedModifiers = this.modifiers.map { oldMod ->
        if (oldMod.id == modifier.id) modifier else oldMod
    }
    println("updating modifier in tree, old = ${this.modifiers}, new = $updatedModifiers")
    return this.copy(modifiers = updatedModifiers)
}

fun PrototypeComponent.findByIDInTree(id: String): PrototypeComponent? {
    if (this.id == id) return this
    if (this.properties is Properties.Group) {
        for (child in this.properties.children) {
            child.findByIDInTree(id)?.let { return it }
        }
    }
    if (this.properties is Properties.Slotted) {
        this.properties.slots.forEach { slot ->
            slot.tree.findByIDInTree(id)?.let { return it }
        }
    }
    return null
}

/**
 * Recursively traverses a tree and finds the parent and child index of a component
 * @param component the component to find the parent of
 */
fun PrototypeComponent.findParentOfComponent(component: PrototypeComponent): Pair<PrototypeComponent, Int>? = when (this.properties) {
    is Properties.Slotted -> this.properties.slots.map { it.tree.findParentOfComponent(component) }.filterNotNull().firstOrNull()
    is Properties.Group -> {
        println("finding parent for $component, this = $this")
        val index = properties.children.indexOf(component)
        println("index = $index")
        val parentPair = if (index == -1) properties.children.map { it.findParentOfComponent(component) }.filterNotNull().firstOrNull() else Pair(this, index)
        println("parentPair = $parentPair")
        parentPair
    }
    else -> null
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

fun PrototypeComponent.toCode(indent: Boolean = false): String =
    ("${name.toPascalCase()}(${properties.toCode()}${modifiers.toCode()})" + when (this.properties) {
        is Properties.Group -> """{
${properties.childrenToCode()}
}"""
        else -> ""
    }).prependIndent(if (indent) "    " else "")

fun Properties.toCode(): String = when (this) {
    is Properties.Text -> """text = "$text", color = ${color.toCode()}"""
    is Properties.Icon -> """asset = Icons.Default.${icon.name}, tint = ${tint.toCode()}"""
    is Properties.Group.Row -> """horizontalArrangement = ${horizontalArrangement.toCodeString()}, verticalAlignment = ${verticalAlignment.toCodeString()}"""
    is Properties.Group.Column -> """verticalArrangement = ${verticalArrangement.toCodeString()}, horizontalAlignment = ${horizontalAlignment.toCodeString()}"""
    is Properties.Group.Box -> ""
    is Properties.Slotted.ExtendedFloatingActionButton -> """
${slots.toCode().prependIndent("    ")}
"""
}

fun Properties.Group.childrenToCode() = children.joinToString("\n") { it.toCode(true) }

fun List<Slot>.toCode() = joinToString(", \n") {
    it.name.toCamelCase() + " = {\n" + it.tree.toCode(true) + "\n}"
}