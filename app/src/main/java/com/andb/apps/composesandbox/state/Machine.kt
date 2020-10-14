package com.andb.apps.composesandbox.state

import com.andb.apps.composesandbox.data.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalCoroutinesApi::class)
class Machine {
    val screens: MutableStateFlow<List<Screen>> = MutableStateFlow(listOf(Screen.Projects(listOf(Project("Demo Project"), Project("Hello World")))))

    operator fun plusAssign(action: Action) = handleAction(action)

    fun handleAction(action: Action){
        when(action){
            UserAction.Back -> handleBack()
            is UserAction.OpenScreen -> screens.value += action.screen
            is UserAction.AddProject -> addProject(Project(action.name))
            is UserAction.OpenComponent -> screens.updateSandbox { it.copy(drawerStack = it.drawerStack + DrawerState.EditComponent(action.component)) }
            is UserAction.OpenComponentList -> screens.updateSandbox { it.copy(drawerStack = it.drawerStack + DrawerState.AddComponent) }
            is UserAction.OpenModifierList -> screens.updateSandbox { it.copy(drawerStack = it.drawerStack + DrawerState.AddModifier(action.editingComponent)) }
            is UserAction.EditModifier -> screens.updateSandbox { it.copy(drawerStack = it.drawerStack + DrawerState.EditModifier(action.editingComponent, action.modifier)) }
            is UserAction.MoveComponent -> moveComponent(action.moving)
            is UserAction.UpdateComponent -> updateComponent(action.updating)
            is UserAction.UpdateModifier -> updateModifier(action.editingComponent, action.updating)
            is UserAction.UpdateTree -> updateTree(action.updated)
        }
    }

    private fun handleBack() {
        val currentScreen = screens.value.last()
        when {
            currentScreen is Screen.Sandbox && currentScreen.state.drawerStack.last() !is DrawerState.Tree -> {
                screens.updateSandbox { it.copy(drawerStack = it.drawerStack.dropLast(1)) }
            }
            screens.value.size > 1 -> screens.value = screens.value.dropLast(1)
        }
    }

    private fun addProject(project: Project){

    }

    private fun moveComponent(moving: Component) {
        screens.updateSandbox { sandboxState ->
            sandboxState.copy(openedTree = sandboxState.openedTree.minusChildFromTree(moving), drawerStack = listOf(DrawerState.Tree(moving)))
        }
    }

    private fun updateComponent(updating: Component) {
        screens.updateSandbox { sandboxState ->
            sandboxState
                .copy(openedTree = sandboxState.openedTree.updateChildInTree(updating))
                .withTree { DrawerState.Tree() }
                .withEditingComponent { DrawerState.EditComponent(updating) }

        }
    }

    private fun updateModifier(editingComponent: Component, updating: PrototypeModifier) {
        updateComponent(editingComponent)
        screens.updateSandbox { sandboxState ->
            sandboxState.withEditingModifier { DrawerState.EditModifier(editingComponent, updating) }
        }
    }

    private fun updateTree(updated: Component) {
        screens.updateSandbox {
            it.copy(openedTree = updated).withTree { DrawerState.Tree() }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun MutableStateFlow<List<Screen>>.updateEach(transform: (Screen) -> Screen) {
    value = value.map(transform)
}

@OptIn(ExperimentalCoroutinesApi::class)
private inline fun <reified T> MutableStateFlow<List<Screen>>.updateScreen(transform: (T) -> Screen) {
    value = value.map {
        when (it) {
            is T -> transform(it)
            else -> it
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private inline fun MutableStateFlow<List<Screen>>.updateSandbox(transform: (sandboxState: SandboxState) -> SandboxState) {
    updateScreen<Screen.Sandbox> { it.copy(state = transform(it.state)) }
}

private fun SandboxState.withTree(transform: (DrawerState.Tree) -> DrawerState.Tree): SandboxState = this.copy(
    drawerStack = drawerStack.map {
        return@map when (it) {
            is DrawerState.Tree -> transform(it)
            else -> it
        }
    }
)

private fun SandboxState.withEditingComponent(transform: (DrawerState.EditComponent) -> DrawerState.EditComponent): SandboxState = this.copy(
    drawerStack = drawerStack.map {
        return@map when (it) {
            is DrawerState.EditComponent -> transform(it)
            else -> it
        }
    }
)

private fun SandboxState.withEditingModifier(transform: (DrawerState.EditModifier) -> DrawerState.EditModifier): SandboxState = this.copy(
    drawerStack = drawerStack.map {
        return@map when (it) {
            is DrawerState.EditModifier -> transform(it)
            else -> it
        }
    }
)