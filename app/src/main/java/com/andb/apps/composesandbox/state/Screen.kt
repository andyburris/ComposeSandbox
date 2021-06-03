package com.andb.apps.composesandbox.state

import com.andb.apps.composesandboxdata.model.*

sealed class Screen {
    object Projects : Screen()
    object AddProject : Screen()
    data class Sandbox(val projectID: String, val openedTreeID: String, val drawerScreens: List<DrawerScreen> = listOf(DrawerScreen.Tree)) : Screen()
    data class Preview(val projectID: String, val currentScreenID: String) : Screen()
    data class Code(val projectID: String) : Screen()
    object Test : Screen()
}

sealed class ViewState {
    data class Projects (val projects: List<Project>) : ViewState()
    object AddProject : ViewState()
    data class Sandbox(val project: Project, val openedTreeID: String, val drawerStack: List<DrawerViewState>) : ViewState() {
        val openedTree get() = project.trees.first { it.id == openedTreeID }
        val editingComponent: PrototypeComponent get() {
            if (drawerStack.none { it is DrawerViewState.EditComponent }) throw Error("Can't access editingComponent until drawerStack contains a DrawerState.EditComponent, currently is $drawerStack")
            return drawerStack.filterIsInstance<DrawerViewState.EditComponent>().first().component
        }
        val editingModifier: PrototypeModifier get() {
            if (drawerStack.none { it is DrawerViewState.EditModifier }) throw Error("Can't access editingModifier until drawerStack contains a DrawerState.EditModifier, currently is $drawerStack")
            return drawerStack.filterIsInstance<DrawerViewState.EditModifier>().first().modifier
        }
    }
    data class Preview(val project: Project, val currentTree: PrototypeTree) : ViewState()
    data class Code(val project: Project) : ViewState()
    object Test : ViewState()
}

fun ViewState.toScreen() = when (this) {
    is ViewState.Projects -> Screen.Projects
    ViewState.AddProject -> Screen.AddProject
    is ViewState.Sandbox -> Screen.Sandbox(project.id, openedTree.id, drawerStack.map { it.toDrawerScreen() })
    is ViewState.Preview -> Screen.Preview(project.id, currentTree.id)
    is ViewState.Code -> Screen.Code(project.id)
    ViewState.Test -> Screen.Test
}

fun Screen.toViewState(projects: List<Project>): ViewState? {
    return when (this) {
        Screen.Projects -> ViewState.Projects(projects)
        Screen.AddProject -> ViewState.AddProject
        is Screen.Sandbox -> {
            val project = projects.find { it.id == this.projectID } ?: return null
            val openedTree = project.trees.find { it.id == this.openedTreeID } ?: project.trees.first()
            val drawerStack = this.drawerScreens.map { drawerScreen ->
                when (drawerScreen) {
                    DrawerScreen.Tree -> DrawerViewState.Tree
                    DrawerScreen.AddComponent -> DrawerViewState.AddComponent
                    is DrawerScreen.EditComponent -> openedTree.component.findByIDInTree(drawerScreen.componentID)?.let { DrawerViewState.EditComponent(it) }
                    is DrawerScreen.PickBaseComponent -> DrawerViewState.PickBaseComponent(openedTree.component)
                    DrawerScreen.AddModifier -> DrawerViewState.AddModifier
                    is DrawerScreen.EditModifier -> openedTree.component.findModifierByIDInTree(drawerScreen.modifierID)?.let { DrawerViewState.EditModifier(it) }
                    DrawerScreen.EditTheme -> DrawerViewState.EditTheme
                }
            }
            val validStack = drawerStack.takeWhile { it != null }.filterNotNull()
            ViewState.Sandbox(project, this.openedTreeID, validStack)
        }
        is Screen.Preview -> {
            val project = projects.find { it.id == this.projectID } ?: return null
            val currentScreen = project.trees.first { it.id == this.currentScreenID }
            ViewState.Preview(project, currentScreen)
        }
        is Screen.Code -> ViewState.Code(projects.first { it.id == this.projectID })
        is Screen.Test -> ViewState.Test
    }
}

sealed class DrawerScreen {
    object Tree : DrawerScreen()
    object AddComponent : DrawerScreen()
    data class EditComponent (val componentID: String) : DrawerScreen()
    object PickBaseComponent : DrawerScreen()
    object AddModifier : DrawerScreen()
    data class EditModifier(val modifierID: String) : DrawerScreen()
    object EditTheme : DrawerScreen()
}


sealed class DrawerViewState {
    object Tree : DrawerViewState()
    object AddComponent : DrawerViewState()
    data class EditComponent (val component: PrototypeComponent) : DrawerViewState()
    data class PickBaseComponent(val currentBaseComponent: PrototypeComponent) : DrawerViewState()
    object AddModifier : DrawerViewState()
    data class EditModifier(val modifier: PrototypeModifier) : DrawerViewState()
    object EditTheme : DrawerViewState()
}


fun DrawerViewState.toDrawerScreen() = when (this) {
    DrawerViewState.Tree -> DrawerScreen.Tree
    DrawerViewState.AddComponent -> DrawerScreen.AddComponent
    is DrawerViewState.EditComponent -> DrawerScreen.EditComponent(component.id)
    is DrawerViewState.PickBaseComponent -> DrawerScreen.PickBaseComponent
    DrawerViewState.AddModifier -> DrawerScreen.AddModifier
    is DrawerViewState.EditModifier -> DrawerScreen.EditModifier(modifier.id)
    DrawerViewState.EditTheme -> DrawerScreen.EditTheme
}
