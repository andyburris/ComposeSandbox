package com.andb.apps.composesandboxdata.model

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val trees: List<PrototypeTree>,
    val theme: Theme,
)

fun Project.updatedTree(tree: PrototypeTree) = this.copy(
    trees = this.trees.map {
        when (it.id) {
            tree.id -> tree
            else -> it
        }
    },
)

@Serializable
data class PrototypeTree(val id: String = UUID.randomUUID().toString(), val name: String, val treeType: TreeType, val component: PrototypeComponent = PrototypeComponent.Group.Column(modifiers = listOf(PrototypeModifier.FillMaxSize())))
enum class TreeType { Screen, Component }

fun Project.nextScreenName(): String {
    val screens = trees.screens()
    val oldComponentsMax = screens.mapNotNull { it.name.removePrefix("Screen ").toIntOrNull() }.maxOrNull() ?: 0
    val componentNumber = maxOf(oldComponentsMax, screens.size) + 1
    return "Screen $componentNumber"
}

fun Project.nextComponentName(): String {
    val components = trees.components()
    val oldComponentsMax = components.mapNotNull { it.name.removePrefix("Component ").toIntOrNull() }.maxOrNull() ?: 0
    val componentNumber = maxOf(oldComponentsMax, components.size) + 1
    return "Component $componentNumber"
}

fun List<PrototypeTree>.screens() = this.filter { it.treeType == TreeType.Screen }
fun List<PrototypeTree>.components() = this.filter { it.treeType == TreeType.Component }