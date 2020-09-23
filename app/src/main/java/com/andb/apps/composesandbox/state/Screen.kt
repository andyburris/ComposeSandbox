package com.andb.apps.composesandbox.state

import com.andb.apps.composesandbox.data.model.Component
import com.andb.apps.composesandbox.data.model.Project

sealed class Screen {
    data class Projects(val projects: List<Project>) : Screen()
    data class Sandbox(val state: SandboxState) : Screen()
}

data class SandboxState(val project: Project, val opened: Component = project.screens.first(), val drawerState: DrawerState = DrawerState.Tree())
sealed class DrawerState {
    data class Tree(val movingComponent: Component? = null) : DrawerState()
    object AddComponent : DrawerState()
    data class EditProperties (val component: Component) : DrawerState()
}