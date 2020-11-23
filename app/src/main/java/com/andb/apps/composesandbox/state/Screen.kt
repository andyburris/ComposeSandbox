package com.andb.apps.composesandbox.state

import com.andb.apps.composesandbox.model.Project
import com.andb.apps.composesandbox.model.PrototypeComponent
import com.andb.apps.composesandbox.model.PrototypeModifier

sealed class Screen {
    object Projects : Screen()
    data class Sandbox(val projectID: String, val openedTreeID: String, val drawerScreens: List<DrawerScreen> = listOf(DrawerScreen.Tree)) : Screen() {

    }
    data class Preview(val projectID: String, val currentScreenID: String) : Screen()
    data class Code(val projectID: String) : Screen()
    object Test : Screen()
}

sealed class ViewState {
    data class SandboxState(val project: Project, val openedTree: PrototypeComponent = project.screens.first(), val drawerStack: List<DrawerState>) : ViewState() {
        val editingComponent: PrototypeComponent get() {
            if (drawerStack.none { it is DrawerState.EditComponent }) throw Error("Can't access editingComponent until drawerStack contains a DrawerState.EditComponent, currently is $drawerStack")
            return drawerStack.filterIsInstance<DrawerState.EditComponent>().first().component
        }
        val editingModifier: PrototypeModifier get() {
            if (drawerStack.none { it is DrawerState.EditModifier }) throw Error("Can't access editingModifier until drawerStack contains a DrawerState.EditModifier, currently is $drawerStack")
            return drawerStack.filterIsInstance<DrawerState.EditModifier>().first().modifier
        }
    }
    data class PreviewState(val project: Project, val currentScreen: PrototypeComponent) : ViewState()
    data class ProjectsState (val projects: List<Project>) : ViewState()
    data class CodeState(val project: Project) : ViewState()
    object TestState : ViewState()
}


sealed class DrawerScreen {
    object Tree : DrawerScreen()
    object AddComponent : DrawerScreen()
    data class EditComponent (val componentID: String) : DrawerScreen()
    object AddModifier : DrawerScreen()
    data class EditModifier(val modifierID: String) : DrawerScreen()
    object EditTheme : DrawerScreen()
}


sealed class DrawerState {
    object Tree : DrawerState()
    object AddComponent : DrawerState()
    data class EditComponent (val component: PrototypeComponent) : DrawerState()
    object AddModifier : DrawerState()
    data class EditModifier(val modifier: PrototypeModifier) : DrawerState()
    object EditTheme : DrawerState()
}

