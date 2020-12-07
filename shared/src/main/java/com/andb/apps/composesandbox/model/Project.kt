package com.andb.apps.composesandbox.model

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val screens: List<PrototypeComponent>,
    val components: List<PrototypeComponent> = listOf(),
    val theme: Theme,
)

fun Project.updatedTree(tree: PrototypeComponent) = this.copy(
    screens = this.screens.map {
        when (it.id) {
            tree.id -> tree
            else -> it
        }
    },
    components = this.components.map {
        when (it.id) {
            tree.id -> tree
            else -> it
        }
    }
)