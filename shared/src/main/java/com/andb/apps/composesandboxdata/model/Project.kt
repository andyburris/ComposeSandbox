package com.andb.apps.composesandboxdata.model

import android.content.Context
import com.andb.apps.composesandboxdata.local.HistoryEntry
import com.andb.apps.composesandboxdata.state.ActionResult
import com.andb.apps.composesandboxdata.state.ProjectAction
import com.andb.apps.composesandboxdata.toPascalCase
import com.andb.apps.composesandboxdata.unzip
import kotlinx.serialization.Serializable
import java.io.File
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Serializable
data class Project(
    val id: String,
    val name: String,
    val trees: List<PrototypeTree>,
    val theme: Theme,
    val pastHistory: List<HistoryEntry>,
    val futureHistory: List<HistoryEntry>
)

fun newProject(name: String, trees: List<PrototypeTree>, theme: Theme = Theme()) = Project(id = UUID.randomUUID().toString(), name, trees, theme, emptyList(), emptyList())

fun Project.apply(action: ProjectAction): Project {
    val (newProject, residual) = this.reduce(action)
    if (residual == null) throw Error("residual must not be null (action = $action)")
    return newProject.copy(pastHistory = pastHistory + HistoryEntry(action = action, residual = residual), futureHistory = emptyList())
}

fun Project.undo(): Project {
    val pastHistoryEntry = pastHistory.last()
    val pastResidual = pastHistoryEntry.residual
    println("undoing (action = $pastResidual)")
    val (newProject, futureResidual) = this.reduce(pastResidual)
    if (futureResidual == null) throw Error("futureResidual must not be null (action = $pastResidual)")
    return newProject.copy(pastHistory = newProject.pastHistory.dropLast(1), futureHistory = newProject.futureHistory + pastHistoryEntry.copy(action = pastResidual, residual = futureResidual))
}

fun Project.redo(): Project {
    val futureHistoryEntry = futureHistory.last()
    val futureResidual = futureHistoryEntry.residual
    println("redoing (action = $futureResidual)")
    val (newProject, pastResidual) = this.reduce(futureResidual)
    println("redid, newProject = ${newProject.stringify()}")
    if (pastResidual == null) throw Error("pastResidual must not be null (action = $futureResidual)")
    return newProject.copy(futureHistory = newProject.futureHistory.dropLast(1), pastHistory = newProject.pastHistory + futureHistoryEntry.copy(action = futureResidual, residual = pastResidual))
}

fun Project.reduce(action: ProjectAction): ActionResult<Project> = when(action) {
    is ProjectAction.AddTree -> {
        val withTree = this.copy(trees = this.trees + action.tree)
        ActionResult(withTree, ProjectAction.DeleteTree(action.tree))
    }
    is ProjectAction.DeleteTree -> when(action.tree.treeType) {
            TreeType.Screen -> ActionResult(this.copy(trees = trees - action.tree), ProjectAction.AddTree(action.tree))
            TreeType.Component -> {
                val removed = this.removeTree(action.tree)
                val currentFlattened = this.trees.minus(action.tree).flatten()
                val changed = removed.trees.flatten().filter { removed -> currentFlattened.none { removed.id == it.id && removed::class == it::class } }
                println("changed components = ${changed.stringifyChildren(true)}")
                val residual = ProjectAction.ExtractComponent(tree = action.tree, oldComponents = changed)
                ActionResult(removed, residual)
            }
        }
    is ProjectAction.ExtractComponent -> {
        val extractedTrees = this.trees.map { tree ->
            val replacementComponents = action.oldComponents.map { oldComponent ->
                PrototypeComponent.Custom(id = oldComponent.id, treeID = action.tree.id)
            }
            tree.copy(component = tree.component.replaceWithCustom(action.oldComponents, replacementComponents))
        }
        val withCustomComponent = this.copy(trees = extractedTrees + action.tree)
        val residual = ProjectAction.DeleteTree(action.tree)
        ActionResult(withCustomComponent, residual)
    }
    is ProjectAction.TreeAction -> {
        val (newTrees, potentialResiduals) = this.trees.map { it.reduce(action) }.unzip { it.value to it.result }
        ActionResult(this.copy(trees = newTrees), potentialResiduals.filterNotNull().firstOrNull())
    }
    is ProjectAction.UpdateTheme -> ActionResult(this.updatedTheme(action.theme), ProjectAction.UpdateTheme(this.theme))
    is ProjectAction.UpdateName -> ActionResult(this.copy(name = action.name), ProjectAction.UpdateName(this.name))
}

fun PrototypeTree.reduce(action: ProjectAction.TreeAction): ActionResult<PrototypeTree> = when(action) {
    is ProjectAction.TreeAction.AddComponent -> {
        val newTree = this.copy(component = component.plusChildInTree(action.adding, action.parent, action.indexInParent))
        val residual = ProjectAction.TreeAction.DeleteComponent(action.adding)
        ActionResult(newTree, residual)
    }
    is ProjectAction.TreeAction.DeleteComponent -> {
        println("deleting ${action.deleting.stringify()} from tree = ${this.stringify()}")
        val newTree = this.copy(component = component.minusChildFromTree(action.deleting))
        val oldParentInfo = this.component.findParentOfComponent(action.deleting)
        val residual = oldParentInfo?.let { ProjectAction.TreeAction.AddComponent(action.deleting, oldParentInfo.first.withChildren(children = oldParentInfo.first.children.filter { it != action.deleting }), oldParentInfo.second) }
        ActionResult(newTree, residual)
    }
    is ProjectAction.TreeAction.MoveComponent -> {
        val newTree = this.copy(component = this.component.minusChildFromTree(action.moving).plusChildInTree(action.moving, action.newParent, action.indexInNewParent))
        val oldParentInfo = this.component.findParentOfComponent(action.moving)
        val residual = oldParentInfo?.let { ProjectAction.TreeAction.MoveComponent(action.moving, oldParentInfo.first, oldParentInfo.second) }
        ActionResult(newTree, residual)
    }
    is ProjectAction.TreeAction.UpdateComponent -> {
        val newTree = this.copy(component = component.updatedChildInTree(action.component))
        val oldComponent = this.component.findByIDInTree(action.component.id)
        val residual = oldComponent?.let { ProjectAction.TreeAction.UpdateComponent(oldComponent) }
        ActionResult(newTree, residual)
    }
    is ProjectAction.TreeAction.UpdateName -> {
        when(this) {
            action.tree -> {
                val newTree = this.copy(name = action.name)
                val residual = ProjectAction.TreeAction.UpdateName(newTree, this.name)
                ActionResult(newTree, residual)
            }
            else -> ActionResult(this, null)
        }
    }
}

fun Project.updatedTree(tree: PrototypeTree) = this.copy(
    trees = this.trees.map {
        when (it.id) {
            tree.id -> tree
            else -> it
        }
    },
)
fun Project.removeTree(customTree: PrototypeTree): Project {
    println("removing ${customTree.stringify()} from trees = ${trees.stringify()}")
    val originalCustomComponents = trees.flatten().filterIsInstance<PrototypeComponent.Custom>()
    val originalComponent = customTree.component
    val replacements = originalCustomComponents.drop(1).map { originalComponent.copy(id = it.id).reassignIDs() }
    val replacementComponents = ReplacementComponents(originalComponent = originalComponent, replacements = replacements)
    val newTrees = this.trees.filter { it != customTree }.map { tree ->
        tree.copy(component = tree.component.replaceCustomWith(customTree.id, replacementComponents))
    }
    println("newTrees = ${newTrees.stringify()}")
    return this.copy(trees = newTrees - customTree)
}

fun Project.updatedTheme(theme: Theme) = this.copy(theme = theme)
fun List<PrototypeTree>.flatten() = this.flatMap { it.component.flatten() }
fun Project.stringify() = "Project(name = $name, trees = [${trees.stringify()}])"
fun List<PrototypeTree>.stringify() = this.joinToString { it.stringify() }
fun PrototypeTree.stringify() = "Tree(name = ${name}, component = ${component.stringify()})"

@Serializable
data class PrototypeTree(val id: String = UUID.randomUUID().toString(), val name: String, val treeType: TreeType, val component: PrototypeComponent = PrototypeComponent.Group.Column(modifiers = listOf(PrototypeModifier.FillMaxSize())))
enum class TreeType { Screen, Component }

fun List<PrototypeTree>.nextScreenName(): String {
    val screens = screens()
    val oldComponentsMax = screens.mapNotNull { it.name.removePrefix("Screen ").toIntOrNull() }.maxOrNull() ?: 0
    val componentNumber = maxOf(oldComponentsMax, screens.size) + 1
    return "Screen $componentNumber"
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