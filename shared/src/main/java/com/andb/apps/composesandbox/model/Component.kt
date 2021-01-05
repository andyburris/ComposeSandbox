package com.andb.apps.composesandbox.model

import com.andb.apps.composesandbox.plusElement
import com.andb.apps.composesandbox.toCamelCase
import com.andb.apps.composesandbox.toPascalCase
import kotlinx.serialization.Serializable
import java.util.*

val allComponents = listOf(
    PrototypeComponent.Text(),
    PrototypeComponent.Icon(),
    PrototypeComponent.Group.Row(),
    PrototypeComponent.Group.Column(),
    PrototypeComponent.Group.Box(),
    PrototypeComponent.Slotted.TopAppBar(),
    PrototypeComponent.Slotted.BottomAppBar(),
    PrototypeComponent.Slotted.ExtendedFloatingActionButton(),
    PrototypeComponent.Slotted.Scaffold(),
)

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
        abstract override val properties: Properties.Group

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
        abstract override val properties: Properties.Slotted

        val Slot.enabled get() = !this.optional || properties.slotsEnabled[this.name] == true

        @Serializable
        data class TopAppBar(
            override val properties: Properties.Slotted.TopAppBar = Properties.Slotted.TopAppBar(),
            override val slots: List<Slot> = listOf(Slot("Navigation Icon"), Slot("Title", optional = false), Slot("Actions", PrototypeComponent.Group.Row())),
            override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(), override val name: String = "TopAppBar",
        ) : Slotted()

        @Serializable
        data class BottomAppBar(
            override val properties: Properties.Slotted.BottomAppBar = Properties.Slotted.BottomAppBar(),
            override val slots: List<Slot> = listOf(Slot("Content", PrototypeComponent.Group.Row(), optional = false)),
            override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(), override val name: String = "BottomAppBar",
        ) : Slotted()

        @Serializable
        data class ExtendedFloatingActionButton(
            override val properties: Properties.Slotted.ExtendedFloatingActionButton = Properties.Slotted.ExtendedFloatingActionButton(),
            override val slots: List<Slot> = listOf(Slot("Icon"), Slot("Text", optional = false)),
            override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(), override val name: String = "ExtendedFloatingActionButton",
        ) : Slotted()

        @Serializable
        data class Scaffold(
            override val properties: Properties.Slotted.Scaffold = Properties.Slotted.Scaffold(),
            override val slots: List<Slot> = listOf(Slot("Top App Bar"), Slot("Bottom App Bar"), Slot("Floating Action Button"), Slot("Drawer"), Slot("Body Content", optional = false)),
            override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(), override val name: String = "Scaffold",
        ) : Slotted()
    }
    fun copy(
        id: String = this.id,
        modifiers: List<PrototypeModifier> = this.modifiers,
        properties: Properties = this.properties,
        name: String = this.name,
    ): PrototypeComponent = when (this) {
        is Text -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Text, name = name)
        is Icon -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Icon, name = name)
        is Group.Row -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Group.Row, name = name)
        is Group.Column -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Group.Column, name = name)
        is Group.Box -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Group.Box, name = name)
        is Slotted.TopAppBar -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Slotted.TopAppBar, name = name)
        is Slotted.BottomAppBar -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Slotted.BottomAppBar, name = name)
        is Slotted.ExtendedFloatingActionButton -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Slotted.ExtendedFloatingActionButton, name = name)
        is Slotted.Scaffold -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Slotted.Scaffold, name = name)
    }
}

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

        abstract val slotsEnabled: Map<String, Boolean>

        @Serializable
        data class TopAppBar(
            override val slotsEnabled: Map<String, Boolean> = mapOf("Navigation Icon" to true, "Actions" to true),
            val backgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Primary,
            val contentColor: PrototypeColor = PrototypeColor.ThemeColor.OnPrimary,
            val elevation: Int = 4
        ) : Slotted()

        @Serializable
        data class BottomAppBar(
            override val slotsEnabled: Map<String, Boolean> = emptyMap(),
            val backgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Primary,
            val contentColor: PrototypeColor = PrototypeColor.ThemeColor.OnPrimary,
            val elevation: Int = 4
        ) : Slotted()
        @Serializable
        data class ExtendedFloatingActionButton(
            override val slotsEnabled: Map<String, Boolean> = mapOf("Icon" to true),
            val backgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Secondary,
            val contentColor: PrototypeColor = PrototypeColor.ThemeColor.OnSecondary,
            val defaultElevation: Int = 6,
            val pressedElevation: Int = 12,
        ) : Slotted()

        @Serializable
        data class Scaffold(
            override val slotsEnabled: Map<String, Boolean> = mapOf("Top App Bar" to true, "Bottom App Bar" to false, "Floating Action Button" to true, "Drawer" to false),
            val backgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Background,
            val contentColor: PrototypeColor = PrototypeColor.ThemeColor.OnBackground,
            val drawerBackgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Background,
            val drawerContentColor: PrototypeColor = PrototypeColor.ThemeColor.OnBackground,
            val drawerElevation: Int = 16,
            val floatingActionButtonPosition: FabPosition = FabPosition.End,
            val isFloatingActionButtonDocked: Boolean = false,
        ) : Slotted() {
            enum class FabPosition {
                Center, End
            }

            fun FabPosition.toCode() = when(this) {
                FabPosition.Center -> "FabPosition.Center"
                FabPosition.End -> "FabPosition.End"
            }
        }
    }
}

fun <T: Properties.Slotted> T.withSlotsEnabled(slotsEnabled: Map<String, Boolean>): T = when(this) {
    is Properties.Slotted.TopAppBar -> this.copy(slotsEnabled = slotsEnabled) as T
    is Properties.Slotted.BottomAppBar -> this.copy(slotsEnabled = slotsEnabled) as T
    is Properties.Slotted.ExtendedFloatingActionButton -> this.copy(slotsEnabled = slotsEnabled) as T
    is Properties.Slotted.Scaffold -> this.copy(slotsEnabled = slotsEnabled) as T
    else -> throw Error("Not possible")
}

@Serializable
data class Slot(val name: String, val tree: PrototypeComponent.Group = PrototypeComponent.Group.Box(), val optional: Boolean = true)

fun PrototypeComponent.Group.withChildren(children: List<PrototypeComponent> = this.children): PrototypeComponent.Group {
    return when (this) {
        is PrototypeComponent.Group.Column -> this.copy(children = children)
        is PrototypeComponent.Group.Row -> this.copy(children = children)
        is PrototypeComponent.Group.Box -> this.copy(children = children)
    }
}

fun PrototypeComponent.Slotted.withSlots(slots: List<Slot>): PrototypeComponent.Slotted {
    return when (this) {
        is PrototypeComponent.Slotted.ExtendedFloatingActionButton -> this.copy(slots = slots)
        is PrototypeComponent.Slotted.TopAppBar -> this.copy(slots = slots)
        is PrototypeComponent.Slotted.BottomAppBar -> this.copy(slots = slots)
        is PrototypeComponent.Slotted.Scaffold -> this.copy(slots = slots)
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
                slot.copy(tree = newTree as PrototypeComponent.Group)
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
        this is PrototypeComponent.Slotted -> this.withSlots(slots = this.slots.map { slot -> slot.copy(tree = slot.tree.minusChildFromTree(component) as PrototypeComponent.Group) })
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
        this is PrototypeComponent.Slotted -> this.withSlots(slots = this.slots.map { it.copy(tree = it.tree.updatedChildInTree(component) as PrototypeComponent.Group) })
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
    return when (this) {
        is PrototypeComponent.Group -> children.mapNotNull { it.findModifierByIDInTree(id) }.firstOrNull()
        is PrototypeComponent.Slotted -> slots.mapNotNull { it.tree.findModifierByIDInTree(id) }.firstOrNull()
        else -> null
    }
}

fun PrototypeComponent.toCode(): String {
    val functionName = name.toPascalCase()
    val properties = properties.toCode()
    val modifiers = modifiers.toCode()
    val slots = when (this) {
        is PrototypeComponent.Slotted -> this.slotsToCode()
        else -> ""
    }
    val parameters = listOf(properties, modifiers, slots).filter { it.isNotBlank() }.joinToString(", \n")
    val body = when (this) {
        is PrototypeComponent.Group -> {
            val children = this.childrenToCode()
            when {
                children.isNotEmpty() -> "{\n" + children.prependIndent("    ") + "\n}"
                else -> ""
            }

        }
        else -> ""
    }
    val parenthesis = when {
        parameters.isNotEmpty() -> "(\n" + parameters.prependIndent("    ") + "\n)"
        body.isNotEmpty() -> " "
        else -> "() "
    }
    return (functionName + parenthesis + body)
}


fun Properties.toCode(): String = when (this) {
    is Properties.Text -> """
        |text = "$text", 
        |color = ${color.toCode()}
    """.trimMargin()
    is Properties.Icon -> """
        |imageVector = Icons.Default.${icon.name}, 
        |tint = ${tint.toCode()}
    """.trimMargin()
    is Properties.Group.Row -> """
        |horizontalArrangement = ${horizontalArrangement.toCodeString()}, 
        |verticalAlignment = ${verticalAlignment.toCodeString()}
    """.trimMargin()
    is Properties.Group.Column -> """
        |verticalArrangement = ${verticalArrangement.toCodeString()},
        |horizontalAlignment = ${horizontalAlignment.toCodeString()}
    """.trimMargin()
    is Properties.Group.Box -> ""
    is Properties.Slotted.ExtendedFloatingActionButton -> """
        |backgroundColor = ${backgroundColor.toCode()}, 
        |defaultElevation = ${defaultElevation}.dp, 
        |pressedElevation = $pressedElevation.dp
    """.trimMargin()
    is Properties.Slotted.TopAppBar -> """
        |backgroundColor = ${backgroundColor.toCode()}, 
        |elevation = $elevation.dp
    """.trimMargin()
    is Properties.Slotted.BottomAppBar -> """
        |backgroundColor = ${backgroundColor.toCode()}, 
        |elevation = $elevation.dp
    """.trimMargin()
    is Properties.Slotted.Scaffold -> """
        |backgroundColor = ${backgroundColor.toCode()}, 
        |contentColor = ${contentColor.toCode()}, 
        |drawerBackgroundColor = ${drawerBackgroundColor.toCode()}, 
        |drawerContentColor = ${drawerContentColor.toCode()}, 
        |drawerElevation = $drawerElevation.dp, 
        |floatingActionButtonPosition = ${floatingActionButtonPosition.toCode()}, 
        |isFloatingActionButtonDocked = $isFloatingActionButtonDocked
    """.trimMargin()
}

fun Any?.toCodeString(): String = when (this) {
    is String -> "\"$this\""
    is PrototypeIcon -> "Icons.Default.${this.name}"
    is PrototypeColor -> this.toCode()
    is PrototypeAlignment -> this.toCode()
    is PrototypeArrangement -> this.toCode()
    else -> this.toString()
}

fun PrototypeComponent.Group.childrenToCode() = children.joinToString("\n") { it.toCode() }

fun PrototypeComponent.Slotted.slotsToCode() = slots.filter { !it.optional || properties.slotsEnabled[it.name] == true }.joinToString(", \n") {
    it.name.toCamelCase() + " = {\n" + it.tree.childrenToCode().prependIndent("    ") + "\n}"
}