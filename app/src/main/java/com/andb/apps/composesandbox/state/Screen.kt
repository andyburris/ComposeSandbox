package com.andb.apps.composesandbox.state

import com.andb.apps.composesandbox.data.model.Component
import com.andb.apps.composesandbox.data.model.Project
import com.andb.apps.composesandbox.data.model.PrototypeModifier

sealed class Screen {
    data class Projects(val projects: List<Project>) : Screen()
    data class Sandbox(val state: SandboxState) : Screen()
}

data class SandboxState(val project: Project, val openedTree: Component = project.screens.first(), val drawerStack: List<DrawerState> = listOf(DrawerState.Tree()))


sealed class DrawerState {
    data class Tree(val movingComponent: Component? = null) : DrawerState()
    object AddComponent : DrawerState()
    data class EditComponent (val editing: Component) : DrawerState()
    data class AddModifier(val editingComponent: Component) : DrawerState()
    data class EditModifier(val editingComponent: Component, val modifier: PrototypeModifier) : DrawerState()
}