package com.andb.apps.composesandbox.state

import com.andb.apps.composesandboxdata.model.Project
import com.andb.apps.composesandboxdata.model.PrototypeComponent
import com.andb.apps.composesandboxdata.model.PrototypeModifier
import com.andb.apps.composesandboxdata.model.PrototypeTree

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
    data class Sandbox(val project: Project, val openedTree: PrototypeTree, val drawerStack: List<DrawerState>) : ViewState() {
        val editingComponent: PrototypeComponent get() {
            if (drawerStack.none { it is DrawerState.EditComponent }) throw Error("Can't access editingComponent until drawerStack contains a DrawerState.EditComponent, currently is $drawerStack")
            return drawerStack.filterIsInstance<DrawerState.EditComponent>().first().component
        }
        val editingModifier: PrototypeModifier get() {
            if (drawerStack.none { it is DrawerState.EditModifier }) throw Error("Can't access editingModifier until drawerStack contains a DrawerState.EditModifier, currently is $drawerStack")
            return drawerStack.filterIsInstance<DrawerState.EditModifier>().first().modifier
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

sealed class DrawerScreen {
    object Tree : DrawerScreen()
    object AddComponent : DrawerScreen()
    data class EditComponent (val componentID: String) : DrawerScreen()
    object PickBaseComponent : DrawerScreen()
    object AddModifier : DrawerScreen()
    data class EditModifier(val modifierID: String) : DrawerScreen()
    object EditTheme : DrawerScreen()
}


sealed class DrawerState {
    object Tree : DrawerState()
    object AddComponent : DrawerState()
    data class EditComponent (val component: PrototypeComponent) : DrawerState()
    data class PickBaseComponent(val currentBaseComponent: PrototypeComponent) : DrawerState()
    object AddModifier : DrawerState()
    data class EditModifier(val modifier: PrototypeModifier) : DrawerState()
    object EditTheme : DrawerState()
}


fun DrawerState.toDrawerScreen() = when (this) {
    DrawerState.Tree -> DrawerScreen.Tree
    DrawerState.AddComponent -> DrawerScreen.AddComponent
    is DrawerState.EditComponent -> DrawerScreen.EditComponent(component.id)
    is DrawerState.PickBaseComponent -> DrawerScreen.PickBaseComponent
    DrawerState.AddModifier -> DrawerScreen.AddModifier
    is DrawerState.EditModifier -> DrawerScreen.EditModifier(modifier.id)
    DrawerState.EditTheme -> DrawerScreen.EditTheme
}
