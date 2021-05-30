package com.andb.apps.composesandboxdata.model

import com.andb.apps.composesandboxdata.plusElement
import kotlinx.serialization.Serializable
import java.util.*

val allComponents = listOf(
    PrototypeComponent.Text("Text"),
    PrototypeComponent.Icon(PrototypeIcon.Image),
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

    @Serializable
    data class Text(
        val text: String,
        val weight: Weight = Weight.Normal,
        val size: Int = 14,
        val color: PrototypeColor = PrototypeColor.ThemeColor.OnBackground,
        override val id: String = UUID.randomUUID().toString(),
        override val modifiers: List<PrototypeModifier> = emptyList(),
    ) : PrototypeComponent() {
        enum class Weight {
            Thin, ExtraLight, Light, Normal, Medium, SemiBold, Bold, ExtraBold, Black,
        }
    }

    @Serializable
    data class Icon(
        val icon: PrototypeIcon,
        val tint: PrototypeColor = PrototypeColor.ThemeColor.OnBackground,
        override val id: String = UUID.randomUUID().toString(),
        override val modifiers: List<PrototypeModifier> = emptyList(),
    ) : PrototypeComponent()

    @Serializable
    sealed class Group : PrototypeComponent() {

        abstract val children: List<PrototypeComponent>

        @Serializable
        data class Row(
            val horizontalArrangement: PrototypeArrangement = PrototypeArrangement.Horizontal.Start,
            val verticalAlignment: PrototypeAlignment.Vertical = PrototypeAlignment.Vertical.Top,
            override val children: List<PrototypeComponent> = emptyList(),
            override val id: String = UUID.randomUUID().toString(),
            override val modifiers: List<PrototypeModifier> = emptyList(),
        ) : Group()

        @Serializable
        data class Column(
            val verticalArrangement: PrototypeArrangement = PrototypeArrangement.Vertical.Top,
            val horizontalAlignment: PrototypeAlignment.Horizontal = PrototypeAlignment.Horizontal.Start,
            override val children: List<PrototypeComponent> = emptyList(),
            override val id: String = UUID.randomUUID().toString(),
            override val modifiers: List<PrototypeModifier> = emptyList(),
        ) : Group()
        @Serializable
        data class Box(
            override val children: List<PrototypeComponent> = emptyList(),
            override val id: String = UUID.randomUUID().toString(),
            override val modifiers: List<PrototypeModifier> = emptyList(),
        ) : Group()
    }

    @Serializable
    sealed class Slotted : PrototypeComponent() {

        abstract val slots: Slots

        @Serializable
        data class TopAppBar(
            val backgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Primary,
            val contentColor: PrototypeColor = PrototypeColor.ThemeColor.OnPrimary,
            val elevation: Int = 4,
            override val slots: Slots.TopAppBar = Slots.TopAppBar(),
            override val id: String = UUID.randomUUID().toString(),
            override val modifiers: List<PrototypeModifier> = emptyList(),
        ) : Slotted() {
        }

        @Serializable
        data class BottomAppBar(
            val backgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Primary,
            val contentColor: PrototypeColor = PrototypeColor.ThemeColor.OnPrimary,
            val elevation: Int = 4,
            override val slots: Slots.BottomAppBar = Slots.BottomAppBar(),
            override val id: String = UUID.randomUUID().toString(),
            override val modifiers: List<PrototypeModifier> = emptyList(),
        ) : Slotted()

        @Serializable
        data class ExtendedFloatingActionButton(
            val backgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Secondary,
            val contentColor: PrototypeColor = PrototypeColor.ThemeColor.OnSecondary,
            val defaultElevation: Int = 6,
            val pressedElevation: Int = 12,
            override val slots: Slots.ExtendedFloatingActionButton = Slots.ExtendedFloatingActionButton(),
            override val id: String = UUID.randomUUID().toString(),
            override val modifiers: List<PrototypeModifier> = emptyList(),
        ) : Slotted()

        @Serializable
        data class Scaffold(
            val backgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Background,
            val contentColor: PrototypeColor = PrototypeColor.ThemeColor.OnBackground,
            val drawerBackgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Background,
            val drawerContentColor: PrototypeColor = PrototypeColor.ThemeColor.OnBackground,
            val drawerElevation: Int = 16,
            val floatingActionButtonPosition: FabPosition = FabPosition.End,
            val isFloatingActionButtonDocked: Boolean = false,
            override val slots: Slots.Scaffold = Slots.Scaffold(),
            override val id: String = UUID.randomUUID().toString(),
            override val modifiers: List<PrototypeModifier> = emptyList(),
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

    @Serializable
    data class Custom(
        val treeID: String,
        override val id: String = UUID.randomUUID().toString(),
        override val modifiers: List<PrototypeModifier> = emptyList()
    ) : PrototypeComponent()

    fun copy(
        id: String = this.id,
        modifiers: List<PrototypeModifier> = this.modifiers,
    ): PrototypeComponent = when (this) {
        is Text -> this.copy(id = id, modifiers = modifiers, text = text)
        is Icon -> this.copy(id = id, modifiers = modifiers, icon = icon)
        is Group.Row -> this.copy(id = id, modifiers = modifiers, children = children)
        is Group.Column -> this.copy(id = id, modifiers = modifiers, children = children)
        is Group.Box -> this.copy(id = id, modifiers = modifiers, children = this.children)
        is Slotted.TopAppBar -> this.copy(id = id, modifiers = modifiers, slots = slots)
        is Slotted.BottomAppBar -> this.copy(id = id, modifiers = modifiers, slots = slots)
        is Slotted.ExtendedFloatingActionButton -> this.copy(id = id, modifiers = modifiers, slots = slots)
        is Slotted.Scaffold -> this.copy(id = id, modifiers = modifiers, slots = slots)
        is Custom -> this.copy(treeID = this.treeID, id = id, modifiers = modifiers)
    }

    fun name(project: Project) = when (this) {
        is Text -> "Text"
        is Icon -> "Icon"
        is Group.Row -> "Row"
        is Group.Column -> "Column"
        is Group.Box -> "Box"
        is Slotted.TopAppBar -> "TopAppBar"
        is Slotted.BottomAppBar -> "BottomAppBar"
        is Slotted.ExtendedFloatingActionButton -> "ExtendedFloatingActionButton"
        is Slotted.Scaffold -> "Scaffold"
        is Custom -> project.trees.first { it.id == this.treeID }.name
    }
}

@Serializable
sealed class Slots {
    abstract fun allSlots(): List<Slot>
    fun enabledSlots() = allSlots().filter { it.enabled }
    fun map(transform: (Slot) -> Slot): Slots {
        return when (this) {
            is TopAppBar -> TopAppBar(transform(navigationIcon), transform(title), transform(actions))
            is BottomAppBar -> BottomAppBar(transform(content))
            is ExtendedFloatingActionButton -> ExtendedFloatingActionButton(transform(icon), transform(text))
            is Scaffold -> Scaffold(transform(topBar), transform(bottomBar), transform(floatingActionButton), transform(drawer), transform(content))
        }
    }
    fun mapIndexed(transform: (Int, Slot) -> Slot): Slots {
        val all = allSlots()
        return this.map { transform(all.indexOf(it), it) }
    }


    @Serializable
    data class TopAppBar(
        val navigationIcon: Slot = Slot("Navigation Icon", enabled = true),
        val title: Slot = Slot("Title", optional = false, enabled = true),
        val actions: Slot = Slot("Actions", group = PrototypeComponent.Group.Row(), enabled = true)
    ) : Slots() {
        override fun allSlots() = listOf(navigationIcon, title, actions)
    }

    @Serializable
    data class BottomAppBar(
        val content: Slot = Slot("Content", PrototypeComponent.Group.Row(), enabled = true, optional = false),
    ) : Slots() {
        override fun allSlots() = listOf(content)
    }

    @Serializable
    data class ExtendedFloatingActionButton(
        val icon: Slot = Slot("Icon", enabled = true),
        val text: Slot = Slot("Text", optional = false, enabled = true),
    ) : Slots() {
        override fun allSlots() = listOf(icon, text)
    }

    @Serializable
    data class Scaffold(
        val topBar: Slot = Slot("Top Bar", enabled = true),
        val bottomBar: Slot = Slot("Bottom Bar", enabled = false),
        val floatingActionButton: Slot = Slot("Floating Action Button", enabled = true),
        val drawer: Slot = Slot("Drawer", enabled = false),
        val content: Slot = Slot("Content", optional = false, enabled = true)
    ) : Slots() {
        override fun allSlots() = listOf(topBar, bottomBar, floatingActionButton, drawer, content)
    }
}

@Serializable
data class Slot(val name: String, val group: PrototypeComponent.Group = PrototypeComponent.Group.Box(), val optional: Boolean = true, val enabled: Boolean)

fun PrototypeComponent.Group.withChildren(children: List<PrototypeComponent> = this.children): PrototypeComponent.Group {
    return when (this) {
        is PrototypeComponent.Group.Column -> this.copy(children = children)
        is PrototypeComponent.Group.Row -> this.copy(children = children)
        is PrototypeComponent.Group.Box -> this.copy(children = children)
    }
}

fun PrototypeComponent.Slotted.withSlots(slots: Slots): PrototypeComponent.Slotted {
    return when (this) {
        is PrototypeComponent.Slotted.ExtendedFloatingActionButton -> this.copy(slots = slots as Slots.ExtendedFloatingActionButton)
        is PrototypeComponent.Slotted.TopAppBar -> this.copy(slots = slots as Slots.TopAppBar)
        is PrototypeComponent.Slotted.BottomAppBar -> this.copy(slots = slots as Slots.BottomAppBar)
        is PrototypeComponent.Slotted.Scaffold -> this.copy(slots = slots as Slots.Scaffold)
    }
}

/**
 * Creates a copy of a component tree with a component nested in its sibling.
 * Used recursively, and returns copy of component tree with no changes if [sibling] can't be found.
 * @param adding the component to add to the tree
 * @param parent the component that [adding] is nested in
 * @param indexInParent the position in the parent group that [adding] should be inserted at
 */
fun PrototypeComponent.plusChildInTree(adding: PrototypeComponent, parent: PrototypeComponent.Group, indexInParent: Int): PrototypeComponent {
    println("adding child to tree - adding = ${adding.stringify()}, parent = ${parent.stringify()}, indexInParent = $indexInParent, this = ${this.stringify()}")
    return when {
        this == parent -> {
            if (this !is PrototypeComponent.Group) throw Error("Can only add a child to a component that is a PrototypeComponent.Group")
            this.withChildren(children.plusElement(adding, indexInParent))
        }
        this is PrototypeComponent.Slotted -> {
            val newSlots = slots.map { slot ->
                val newTree = slot.group.plusChildInTree(adding, parent, indexInParent)
                println("old = ${slot.group}")
                println("new = $newTree")
                slot.copy(group = newTree as PrototypeComponent.Group)
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
        this is PrototypeComponent.Slotted -> this.withSlots(slots = this.slots.map { slot -> slot.copy(group = slot.group.minusChildFromTree(component) as PrototypeComponent.Group) })
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
        this is PrototypeComponent.Slotted -> this.withSlots(slots = this.slots.map { it.copy(group = it.group.updatedChildInTree(component) as PrototypeComponent.Group) })
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
        this.slots.allSlots().forEach { slot ->
            slot.group.findByIDInTree(id)?.let { return it }
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
        is PrototypeComponent.Slotted -> this.slots.allSlots().mapNotNull { it.group.findParentOfComponent(component) }.firstOrNull()
        is PrototypeComponent.Group -> {
            println("finding parent for ${component.stringify(showIDs = true)}, this = ${this.stringify(showIDs = true)}")
            val index = children.indexOfFirst { it.id == component.id }
            println("index = $index")
            val parentPair = if (index == -1) children.mapNotNull { it.findParentOfComponent(component) }.firstOrNull() else Pair(this, index)
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
        is PrototypeComponent.Slotted -> slots.allSlots().mapNotNull { it.group.findModifierByIDInTree(id) }.firstOrNull()
        else -> null
    }
}

data class ReplacementComponents(val originalComponent: PrototypeComponent, val replacements: List<PrototypeComponent>)
/**
 * Creates a copy of a component tree with a custom component replaced by another tree. Finds original component in tree based on [PrototypeComponent.id]
 * Used recursively, and returns copy of component tree with no changes if [component] can't be found.
 * @param customTreeID the id of the custom component to replace in the tree
 * @param replacementComponent the component to replace it with
 */
fun PrototypeComponent.replaceCustomWith(customTreeID: String, replacementComponents: ReplacementComponents) : PrototypeComponent {
    return when {
        this is PrototypeComponent.Custom && this.treeID == customTreeID -> (replacementComponents.replacements.find { it.id == this.id } ?: replacementComponents.originalComponent).let { it.copy(modifiers = this.modifiers + it.modifiers) }
        this is PrototypeComponent.Group -> this.withChildren(children = this.children.map { it.replaceCustomWith(customTreeID, replacementComponents) })
        this is PrototypeComponent.Slotted -> this.withSlots(slots = this.slots.map { it.copy(group = it.group.replaceCustomWith(customTreeID, replacementComponents) as PrototypeComponent.Group) })
        else -> this
    }
}

fun PrototypeComponent.reassignIDs(): PrototypeComponent {
    return when(this) {
        is PrototypeComponent.Group -> {
            var acc = this.id.increment()
            val newChildren = children.map { child ->
                val newChild = child.copy(id = acc).reassignIDs()
                acc = acc.increment(amount = child.flatten().size)
                newChild
            }
            this.withChildren(newChildren)
        }
        is PrototypeComponent.Slotted -> {
            var acc = this.id.increment()
            val newSlots = this.slots.map {
                val newSlot = it.copy(group = it.group.copy(id = acc).reassignIDs() as PrototypeComponent.Group)
                acc = acc.increment(newSlot.group.flatten().size)
                newSlot
            }
            this.withSlots(newSlots)
        }
        else -> this
    }
}

fun String.increment(amount: Int = 1): String {
    var incremented = this.last()
    var rolledOver = 0
    repeat(amount) {
        incremented = incremented.increment()
        if (incremented == '0') rolledOver++
    }
    return if (rolledOver > 0) this.dropLast(1).increment(rolledOver) + incremented else this.dropLast(1) + incremented
}
fun Char.increment(): Char = when(this) {
    '9' -> 'a'
    'z' -> 'A'
    'Z' -> '0'
    else -> this + 1
}

fun PrototypeComponent.replaceWithCustom(oldComponents: List<PrototypeComponent>, replacementCustomComponents: List<PrototypeComponent.Custom>): PrototypeComponent {
    return when (this) {
        in oldComponents -> replacementCustomComponents.first { it.id == this.id }
        is PrototypeComponent.Group -> this.withChildren(this.children.map { it.replaceWithCustom(oldComponents, replacementCustomComponents) })
        is PrototypeComponent.Slotted -> this.withSlots(slots = this.slots.map { it.copy(group = it.group.replaceWithCustom(oldComponents, replacementCustomComponents) as PrototypeComponent.Group) })
        else -> this
    }
}

fun PrototypeComponent.containsCustomComponent(customTreeID: String): Boolean {
    return when {
        this is PrototypeComponent.Custom && this.treeID == customTreeID -> true
        this is PrototypeComponent.Group -> this.children.any { it.containsCustomComponent(customTreeID) }
        this is PrototypeComponent.Slotted -> this.slots.allSlots().any { it.group.containsCustomComponent(customTreeID) }
        else -> false
    }
}

fun PrototypeComponent.replaceParent(replacementComponent: PrototypeComponent): Pair<PrototypeComponent, Boolean> {
    if (this::class == replacementComponent::class) return Pair(this, false)
    val losesChildren = when (this) {
        is PrototypeComponent.Group -> this.children.isNotEmpty() && !(replacementComponent is PrototypeComponent.Group || replacementComponent is PrototypeComponent.Slotted)
        is PrototypeComponent.Slotted -> this.slots.allSlots().flatMap { it.group.children }.isNotEmpty() && !(replacementComponent is PrototypeComponent.Group || replacementComponent is PrototypeComponent.Slotted)
        else -> false
    }
    val oldChildrenSlots: List<List<PrototypeComponent>> = when(this) {
        is PrototypeComponent.Group -> listOf(this.children)
        is PrototypeComponent.Slotted -> this.slots.allSlots().map { it.group.children }
        else -> emptyList()
    }
    val newParent = when (replacementComponent) {
        is PrototypeComponent.Group -> replacementComponent.withChildren(oldChildrenSlots.flatten())
        is PrototypeComponent.Slotted -> {
            val newSlotChildren = oldChildrenSlots.fitToSize(replacementComponent.slots.allSlots().size)
            val newSlots = replacementComponent.slots.mapIndexed { index, slot ->
                slot.copy(group = slot.group.withChildren(newSlotChildren[index]))
            }
            replacementComponent.withSlots(newSlots)
        }
        else -> replacementComponent
    }
    val newComponent = newParent.copy(id = this.id, modifiers = this.modifiers)
    return Pair(newComponent, losesChildren)
}

fun PrototypeComponent.toTree(project: Project) = PrototypeTree(name = project.trees.nextComponentName(), treeType = TreeType.Component, component = this)


fun PrototypeComponent.flatten(): List<PrototypeComponent> = when(this) {
    is PrototypeComponent.Group -> this.children.flatMap { it.flatten() }.plusElement(this, 0)
    is PrototypeComponent.Slotted -> this.slots.allSlots().flatMap { it.group.flatten() }.plusElement(this, 0)
    else -> listOf(this)
}

fun <T> List<List<T>>.fitToSize(size: Int) : List<List<T>> {
    if (this.size == size) return this
    if (this.size < size) return this + (0 until size - this.size).map { emptyList() } //pad right with empty lists if list just expands

    val flattenedLast = this.slice(size - 1 until this.size).flatten()
    return (0 until size).map { index ->
        val isLastNewSlot = index == size - 1
        if (isLastNewSlot) flattenedLast else this[index]
    }
}

fun PrototypeComponent.stringify(showIDs: Boolean = false): String = when(this) {
    is PrototypeComponent.Custom -> "Custom(${printID(showIDs)}treeID = ${this.treeID})"
    is PrototypeComponent.Group.Box -> "Box(${printID(showIDs)}${children.stringifyChildren(showIDs)})"
    is PrototypeComponent.Group.Column -> "Column(${printID(showIDs)}${children.stringifyChildren(showIDs)})"
    is PrototypeComponent.Group.Row -> "Row(${printID(showIDs)}${children.stringifyChildren(showIDs)})"
    is PrototypeComponent.Icon -> "Icon(${printID(showIDs)}${icon.name})"
    is PrototypeComponent.Slotted.BottomAppBar -> "BottomAppBar(${printID(showIDs)}content = ${slots.allSlots().stringifySlots(showIDs)})"
    is PrototypeComponent.Slotted.ExtendedFloatingActionButton -> "ExtendedFloatingActionButton(${printID(showIDs)}${slots.allSlots().stringifySlots(showIDs)})"
    is PrototypeComponent.Slotted.Scaffold -> "${printID(showIDs)}Scaffold(${slots.allSlots().stringifySlots(showIDs)})"
    is PrototypeComponent.Slotted.TopAppBar -> "TopAppBar(${printID(showIDs)}${slots.allSlots().stringifySlots(showIDs)})"
    is PrototypeComponent.Text -> "Text(${printID(showIDs)}\"${this.text}\")"
}

fun PrototypeComponent.printID(showIDs: Boolean) = if (showIDs) "id = ${this.id}, " else ""
fun List<Slot>.stringifySlots(showIDs: Boolean) = this.filter { it.enabled }.joinToString() { "${it.name} = ${it.group.stringify(showIDs)}" }
fun List<PrototypeComponent>.stringifyChildren(showIDs: Boolean) = this.joinToString { it.stringify(showIDs) }