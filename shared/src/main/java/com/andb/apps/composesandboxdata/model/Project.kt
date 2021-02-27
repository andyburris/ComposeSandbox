package com.andb.apps.composesandboxdata.model

import android.content.Context
import com.andb.apps.composesandboxdata.local.HistoryEntry
import com.andb.apps.composesandboxdata.state.ProjectAction
import com.andb.apps.composesandboxdata.state.ProjectActionResidual
import com.andb.apps.composesandboxdata.toPascalCase
import kotlinx.serialization.Serializable
import java.io.File
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Serializable
data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val trees: List<PrototypeTree>,
    val theme: Theme,
    val history: List<HistoryEntry>
)

fun Project.reduce(action: ProjectAction): Project = when(action) {
    is ProjectAction.DeleteTree -> {
        val removed = this.removeTree(action.tree)
        val changedIDs = this.flattenIDs() - removed.flattenIDs()
        removed.withHistoryEntry(HistoryEntry(residual = ProjectActionResidual.DeleteTree()))
    }
    is ProjectAction.ExtractComponent -> {
        val extractedTrees = this.trees.extractComponent(action.oldComponent)
        val oldIDs = this.trees.flatMap { it.component.flattenIDs() }
        val changedIDs = ext
        this.extractComponent(action.oldComponent)
    }
    is ProjectAction.UpdateTheme -> {
        val oldTheme = this.theme
        this.updatedTheme(action.theme).withHistoryEntry(HistoryEntry(residual = ProjectActionResidual.UpdateTheme(oldTheme)))
    }
    is ProjectAction.TreeAction -> this.copy(trees = this.trees.map { it.reduce(action) })
}

fun PrototypeTree.reduce(action: ProjectAction.TreeAction): Pair<Project, ProjectActionResidual.TreeActionResidual> = when(action) {
    is ProjectAction.TreeAction.AddComponent -> this.copy(component = component.plusChildInTree(action.adding, action.parent, action.indexInParent))
    is ProjectAction.TreeAction.DeleteComponent -> this.copy(component = component.minusChildFromTree(action.component))
    is ProjectAction.TreeAction.MoveComponent -> this.copy()
    is ProjectAction.TreeAction.UpdateComponent -> this.copy(component = component.updatedChildInTree(action.component))
}

fun Project.undo(): Project {
    val residual = history.last().residual
    when(residual) {
        is ProjectActionResidual.TreeActionResidual -> this.trees.map { it.undo(residual) }
        is ProjectActionResidual.DeleteTree -> {
            val withTree = this.copy(trees = trees + residual.action.tree)
            val unreplacedTrees = withTree.trees.map { it.component.replaceWithCustom(replacementCustomComponent = ) }
        }
        is ProjectActionResidual.ExtractComponent -> {
            val oldTrees = this.trees.map { it.copy(it.component.re) }
        }
        is ProjectActionResidual.UpdateTheme -> TODO()
    }
}

fun PrototypeTree.undo(residual: ProjectActionResidual.TreeActionResidual) = when(residual) {
    is ProjectActionResidual.TreeActionResidual.AddComponent -> this.copy(component = component.minusChildFromTree(residual.adding))
    is ProjectActionResidual.TreeActionResidual.DeleteComponent -> this.copy(component = component.plusChildInTree(residual.component, residual.parent, residual.indexInParent))
    is ProjectActionResidual.TreeActionResidual.MoveComponent -> this.copy()
    is ProjectActionResidual.TreeActionResidual.UpdateComponent -> this.copy(component = component.updatedChildInTree(residual.oldComponent))
}

fun Project.updatedTree(tree: PrototypeTree) = this.copy(
    trees = this.trees.map {
        when (it.id) {
            tree.id -> tree
            else -> it
        }
    },
)
fun Project.removeTree(tree: PrototypeTree): Project {
    val newTrees = this.trees.filter { it.id != tree.id }.map {
        it.copy(component = it.component.replaceCustomWith(tree.id, tree.component))
    }
    return this.copy(trees = newTrees)
}

fun Project.updatedTheme(theme: Theme) = this.copy(theme = theme)
fun Project.withHistoryEntry(historyEntry: HistoryEntry) = this.copy(history = this.history + historyEntry)
fun Project.flattenIDs() = this.trees.flatMap { it.component.flattenIDs() }

@Serializable
data class PrototypeTree(val id: String = UUID.randomUUID().toString(), val name: String, val treeType: TreeType, val component: PrototypeComponent = PrototypeComponent.Group.Column(modifiers = listOf(PrototypeModifier.FillMaxSize())))
enum class TreeType { Screen, Component }

fun Project.nextScreenName(): String {
    val screens = trees.screens()
    val oldComponentsMax = screens.mapNotNull { it.name.removePrefix("Screen ").toIntOrNull() }.maxOrNull() ?: 0
    val componentNumber = maxOf(oldComponentsMax, screens.size) + 1
    return "Screen $componentNumber"
}

fun List<PrototypeTree>.extractComponent(oldComponent: PrototypeComponent): List<PrototypeTree> {
    val customTree = PrototypeTree(name = this.nextComponentName(), treeType = TreeType.Component, component = oldComponent)
    val customComponent = PrototypeComponent.Custom(treeID = customTree.id)
    val editedTrees = this.map { it.copy(component = it.component.replaceWithCustom(oldComponent.id, customComponent)) }
    return  editedTrees + customTree
}

fun List<PrototypeTree>.nextComponentName(): String {
    val components = components()
    val oldComponentsMax = components.mapNotNull { it.name.removePrefix("Component ").toIntOrNull() }.maxOrNull() ?: 0
    val componentNumber = maxOf(oldComponentsMax, components.size) + 1
    return "Component $componentNumber"
}

fun PrototypeTree.updatedChildComponent(childComponent: PrototypeComponent) = this.copy(component = component.updatedChildInTree(childComponent))

fun List<PrototypeTree>.screens() = this.filter { it.treeType == TreeType.Screen }
fun List<PrototypeTree>.components() = this.filter { it.treeType == TreeType.Component }

fun Project.exportZip(context: Context): File {
    val generator = CodeGenerator(this)
    val directory = context.cacheDir
    val zipFile = File.createTempFile("${this.name.toPascalCase()}-export", ".zip", directory)
    val zipOutputStream = ZipOutputStream(zipFile.outputStream())
    trees.forEach {
        zipOutputStream.putNextEntry(ZipEntry(it.name.toPascalCase() + ".kt"))
        zipOutputStream.write(with(generator) { it.toCode() }.encodeToByteArray())
        zipOutputStream.closeEntry()
    }
    zipOutputStream.close()
    return zipFile
}