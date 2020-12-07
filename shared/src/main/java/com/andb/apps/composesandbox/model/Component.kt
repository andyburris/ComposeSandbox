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

        @Serializable
        data class Row(
            val horizontalArrangement: PrototypeArrangement = PrototypeArrangement.Horizontal.Start,
            val verticalAlignment: PrototypeAlignment.Vertical = PrototypeAlignment.Vertical.Top,
        ) : Group()

        @Serializable
        data class Column(
            val verticalArrangement: PrototypeArrangement = PrototypeArrangement.Vertical.Top,
            val horizontalAlignment: PrototypeAlignment.Horizontal = PrototypeAlignment.Horizontal.Start,
        ) : Group()

        @Serializable
        object Box : Group()
    }

    @Serializable
    sealed class Slotted : Properties() {

        @Serializable
        data class TopAppBar(
            val backgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Primary,
            val contentColor: PrototypeColor = PrototypeColor.ThemeColor.OnPrimary,
            val elevation: Int = 4
        ) : Slotted()

        @Serializable
        data class BottomAppBar(
            val backgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Primary,
            val contentColor: PrototypeColor = PrototypeColor.ThemeColor.OnPrimary,
            val elevation: Int = 4
        ) : Slotted()

        @Serializable
        data class ExtendedFloatingActionButton(
            val backgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Secondary,
            val contentColor: PrototypeColor = PrototypeColor.ThemeColor.OnSecondary,
            val defaultElevation: Int = 6,
            val pressedElevation: Int = 12,
        ) : Slotted()
    }
}

@Serializable
data class Slot(val name: String, val tree: PrototypeComponent = PrototypeComponent.Group.Box(), val optional: Boolean = true, val enabled: Boolean = true)

@Serializable
sealed class PrototypeComponent {
    abstract val id: String
    abstract val modifiers: List<PrototypeModifier>
    abstract val properties: Properties
    abstract val name: String

    @Serializable
    data class Text(
        override val properties: Properties.Text = Properties.Text("Text"),
        override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(), override val name: String = "Text",
    ) : PrototypeComponent()

    @Serializable
    data class Icon(
        override val properties: Properties.Icon = Properties.Icon(PrototypeIcon.Image),
        override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(), override val name: String = "Icon",
    ) : PrototypeComponent()

    @Serializable
    sealed class Group : PrototypeComponent() {
        abstract val children: List<PrototypeComponent>

        @Serializable
        data class Row(
            override val properties: Properties.Group.Row = Properties.Group.Row(),
            override val children: List<PrototypeComponent> = emptyList(),
            override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(), override val name: String = "Row",
        ) : Group()

        @Serializable
        data class Column(
            override val properties: Properties.Group.Column = Properties.Group.Column(),
            override val children: List<PrototypeComponent> = emptyList(),
            override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(), override val name: String = "Column",
        ) : Group()

        @Serializable
        data class Box(
            override val properties: Properties.Group.Box = Properties.Group.Box,
            override val children: List<PrototypeComponent> = emptyList(),
            override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(), override val name: String = "Box",
        ) : Group()
    }

    @Serializable
    sealed class Slotted : PrototypeComponent() {
        abstract val slots: List<Slot>

        @Serializable
        data class ExtendedFloatingActionButton(
            override val properties: Properties.Slotted.ExtendedFloatingActionButton = Properties.Slotted.ExtendedFloatingActionButton(),
            override val slots: List<Slot> = listOf(Slot("Icon"), Slot("Text", optional = false)),
            override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(), override val name: String = "ExtendedFloatingActionButton",
        ) : Slotted()
    }

    fun copy(
        modifiers: List<PrototypeModifier> = this.modifiers,
        properties: Properties = this.properties,
        name: String = this.name,
    ): PrototypeComponent = when (this) {
        is Text -> this.copy(id = this.id, modifiers = modifiers, properties = properties as Properties.Text, name = name)
        is Icon -> this.copy(id = this.id, modifiers = modifiers, properties = properties as Properties.Icon, name = name)
        is Group.Row -> this.copy(id = this.id, modifiers = modifiers, properties = properties as Properties.Group.Row, name = name)
        is Group.Column -> this.copy(id = this.id, modifiers = modifiers, properties = properties as Properties.Group.Column, name = name)
        is Group.Box -> this.copy(id = this.id, modifiers = modifiers, properties = properties as Properties.Group.Box, name = name)
        is Slotted.ExtendedFloatingActionButton -> this.copy(id = this.id, modifiers = modifiers, properties = properties as Properties.Slotted.ExtendedFloatingActionButton, name = name)
    }
}

private fun PrototypeComponent.Group.withChildren(children: List<PrototypeComponent> = this.children): PrototypeComponent.Group {
    return when (this) {
        is PrototypeComponent.Group.Column -> this.copy(children = children)
        is PrototypeComponent.Group.Row -> this.copy(children = children)
        is PrototypeComponent.Group.Box -> this.copy(children = children)
    }
}

private fun PrototypeComponent.Slotted.withSlots(slots: List<Slot>): PrototypeComponent.Slotted {
    return when (this) {
        is PrototypeComponent.Slotted.ExtendedFloatingActionButton -> this.copy(slots = slots)
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
fun PrototypeComponent.plusChildInTree(adding: PrototypeComponent, parent: PrototypeComponent.Group, indexInParent: Int): PrototypeComponent {
    println("adding child to tree - adding = $adding, parent = $parent, indexInParent = $indexInParent, this = $this")
    return when {
        this == parent -> {
            if (this !is PrototypeComponent.Group) throw Error("Can only add a child to a component that is a PrototypeComponent.Group")
            this.withChildren(children.plusElement(adding, indexInParent))
        }
        this is PrototypeComponent.Slotted -> {
            val newSlots = slots.map { slot ->
                val newTree = slot.tree.plusChildInTree(adding, parent, indexInParent)
                println("old = ${slot.tree}")
                println("new = $newTree")
                slot.copy(tree = newTree)
            }
            this.withSlots(newSlots)
        }
        this is PrototypeComponent.Group -> {
            this.withChildren(children = children.map { it.plusChildInTree(adding, parent, indexInParent) })
        }
        else -> this
    }
}

/**
 * Creates a copy of a component tree with a component removed from it.
 * Used recursively, and returns copy of component tree with no changes if [component] can't be found.
 * @param component the component to remove from the tree
 */
fun PrototypeComponent.minusChildFromTree(component: PrototypeComponent): PrototypeComponent {
    return when {
        this is PrototypeComponent.Slotted -> this.withSlots(slots = this.slots.map { slot -> slot.copy(tree = slot.tree.minusChildFromTree(component)) })
        this !is PrototypeComponent.Group -> this
        component !in this.children -> this.withChildren(children = this.children.map { it.minusChildFromTree(component) })
        else -> this.withChildren(children = this.children - component)
    }
}

/**
 * Creates a copy of a component tree with a component updated it. Finds original component in tree based on [PrototypeComponent.id]
 * Used recursively, and returns copy of component tree with no changes if [component] can't be found.
 * @param component the component to update from the tree
 */
fun PrototypeComponent.updatedChildInTree(component: PrototypeComponent): PrototypeComponent {
    return when {
        this.id == component.id -> component
        this is PrototypeComponent.Group -> this.withChildren(children = this.children.map { it.updatedChildInTree(component) })
        this is PrototypeComponent.Slotted -> this.withSlots(slots = this.slots.map { it.copy(tree = it.tree.updatedChildInTree(component)) })
        else -> this
    }
}

fun PrototypeComponent.updatedModifier(modifier: PrototypeModifier): PrototypeComponent {
    val updatedModifiers = this.modifiers.map { if (it.id == modifier.id) modifier else it }
    return this.copy(modifiers = updatedModifiers)
}

fun PrototypeComponent.findByIDInTree(id: String): PrototypeComponent? {
    if (this.id == id) return this
    if (this is PrototypeComponent.Group) {
        for (child in this.children) {
            child.findByIDInTree(id)?.let { return it }
        }
    }
    if (this is PrototypeComponent.Slotted) {
        this.slots.forEach { slot ->
            slot.tree.findByIDInTree(id)?.let { return it }
        }
    }
    return null
}

/**
 * Recursively traverses a tree and finds the parent and child index of a component
 * @param component the component to find the parent of
 */
fun PrototypeComponent.findParentOfComponent(component: PrototypeComponent): Pair<PrototypeComponent.Group, Int>? =
    when (this) {
        is PrototypeComponent.Slotted -> this.slots.map { it.tree.findParentOfComponent(component) }.filterNotNull().firstOrNull()
        is PrototypeComponent.Group -> {
            println("finding parent for $component, this = $this")
            val index = children.indexOf(component)
            println("index = $index")
            val parentPair = if (index == -1) children.map { it.findParentOfComponent(component) }.filterNotNull().firstOrNull() else Pair(this, index)
            println("parentPair = $parentPair")
            parentPair
        }
        else -> null
    }

fun PrototypeComponent.findModifierByIDInTree(id: String): PrototypeModifier? {
    //try to find the id in this component's modifiers
    modifiers.find { it.id == id }?.let { return it }

    //if not try to find it in children
    if (this !is PrototypeComponent.Group) return null
    for (child in this.children) {
        child.findModifierByIDInTree(id)?.let { return it }
    }
    return null
}

fun PrototypeComponent.toCode(indent: Boolean = false): String =
    ("${name.toPascalCase()}(${properties.toCode()}${modifiers.toCode()})" + when (this) {
        is PrototypeComponent.Group -> "{\n${this.childrenToCode().prependIndent("    ")}\n}"
        is PrototypeComponent.Slotted -> "{\n${this.slots.toCode().prependIndent("    ")}\n}"
        else -> ""
    }).prependIndent(if (indent) "    " else "")

fun Properties.toCode(): String = when (this) {
    is Properties.Text -> """text = "$text", color = ${color.toCode()}"""
    is Properties.Icon -> """asset = Icons.Default.${icon.name}, tint = ${tint.toCode()}"""
    is Properties.Group.Row -> """horizontalArrangement = ${horizontalArrangement.toCodeString()}, verticalAlignment = ${verticalAlignment.toCodeString()}"""
    is Properties.Group.Column -> """verticalArrangement = ${verticalArrangement.toCodeString()}, horizontalAlignment = ${horizontalAlignment.toCodeString()}"""
    is Properties.Group.Box -> ""
    is Properties.Slotted.ExtendedFloatingActionButton -> """backgroundColor = ${backgroundColor.toCode()}, """
    else -> this::class.members.joinToString { it.name + " = " + it.call().toCodeString() }
}

fun Any?.toCodeString(): String = when (this) {
    is String -> "\"$this\""
    is PrototypeIcon -> "Icons.Default.${this.name}"
    is PrototypeColor -> this.toCode()
    is PrototypeAlignment -> this.toCode()
    is PrototypeArrangement -> this.toCode()
    else -> this.toString()
}

fun PrototypeComponent.Group.childrenToCode() = children.joinToString("\n") { it.toCode(true) }

fun List<Slot>.toCode() = joinToString(", \n") {
    it.name.toCamelCase() + " = {\n" + it.tree.toCode(true) + "\n}"
}