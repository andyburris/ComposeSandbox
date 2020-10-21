package com.andb.apps.composesandbox.state

import com.andb.apps.composesandbox.data.model.*

sealed class Screen {
    data class Projects(val projects: List<Project>) : Screen()
    data class Sandbox(val state: SandboxState) : Screen()
}

data class SandboxState(val project: Project, val openedTree: PrototypeComponent = project.screens.first(), val drawerStack: List<DrawerState> = listOf(DrawerState.Tree())) {
    val editingComponent: PrototypeComponent get() {
        if (drawerStack.none { it is DrawerState.EditComponent }) throw Error("Can't access editingComponent until drawerStack contains a DrawerState.EditComponent, currently is $drawerStack")
        val id = drawerStack.filterIsInstance<DrawerState.EditComponent>().first().componentID
        return openedTree.findByIDInTree(id) ?: throw Error("Can't find componentID in tree, make sure DrawerState.EditComponent only ever edits a component in the current tree")
    }
    val editingModifier: PrototypeModifier get() {
        if (drawerStack.none { it is DrawerState.EditModifier }) throw Error("Can't access editingModifier until drawerStack contains a DrawerState.EditModifier, currently is $drawerStack")
        val id = drawerStack.filterIsInstance<DrawerState.EditModifier>().first().modifierID
        return openedTree.findModifierByIDInTree(id) ?: throw Error("Can't find modifierID in tree, make sure DrawerState.EditModifier only ever edits a modifier in the current tree")
    }
}


sealed class DrawerState {
    data class Tree(val movingComponent: PrototypeComponent? = null) : DrawerState()
    object AddComponent : DrawerState()
    data class EditComponent (val componentID: String) : DrawerState()
    object AddModifier : DrawerState()
    data class EditModifier(val modifierID: String) : DrawerState()
}