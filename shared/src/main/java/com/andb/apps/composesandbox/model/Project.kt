package com.andb.apps.composesandbox.model

import com.andb.apps.composesandbox.toPascalCase
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val screens: List<PrototypeScreen>,
    val components: List<PrototypeComponent> = listOf(),
    val theme: Theme,
)

fun Project.updatedScreen(screen: PrototypeScreen) = this.copy(
    screens = this.screens.map {
        when (it.id) {
            screen.id -> screen
            else -> it
        }
    },
)

fun Project.updatedComponent(component: PrototypeComponent) = this.copy(
    components = this.components.map {
        when (it.id) {
            component.id -> component
            else -> it
        }
    }
)

@Serializable
data class PrototypeScreen(val id: String = UUID.randomUUID().toString(), val name: String, val tree: PrototypeComponent.Group = PrototypeComponent.Group.Column())
fun PrototypeScreen.toCode() = """
    |@Composable
    |fun ${name.toPascalCase()}() {
    |${tree.toCode().prependIndent("    ")}
    |}
""".trimMargin()