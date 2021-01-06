package com.andb.apps.composesandbox.model

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
data class PrototypeTree(val id: String = UUID.randomUUID().toString(), val name: String, val treeType: TreeType, val tree: PrototypeComponent.Group = PrototypeComponent.Group.Column())
enum class TreeType { Screen, Component }