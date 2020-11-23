package com.andb.apps.composesandbox.model

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val screens: List<PrototypeComponent> = listOf(
        PrototypeComponent(
            properties = Properties.Group.Column(
                children = listOf(
                    PrototypeComponent(
                        properties = Properties.Group.Row(children = listOf(
                            PrototypeComponent(properties = Properties.Text("Hello")),
                            PrototypeComponent(properties = Properties.Text("Text")),
                        ))
                    ),
                    PrototypeComponent(properties = Properties.Text("Hello")),
                    PrototypeComponent(properties = Properties.Text("Text")),
                )
            )
        )
    ),
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