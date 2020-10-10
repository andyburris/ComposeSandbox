package com.andb.apps.composesandbox.state

import com.andb.apps.composesandbox.data.model.Component
import com.andb.apps.composesandbox.data.model.Project
import com.andb.apps.composesandbox.data.model.minusChildFromTree
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
            is UserAction.OpenComponent -> screens.updateSandbox { it.copy(drawerState = DrawerState.EditProperties(action.component)) }
            is UserAction.OpenComponentList -> screens.updateSandbox { it.copy(drawerState = DrawerState.AddComponent) }
            is UserAction.MoveComponent -> screens.updateSandbox { it.copy(drawerState = DrawerState.Tree(action.moving), opened = it.opened.minusChildFromTree(action.moving)) }
            is UserAction.UpdateTree -> updateTree(action.updated)
        }
    }

    private fun handleBack() {
        val currentScreen = screens.value.last()
        when {
            currentScreen is Screen.Sandbox && currentScreen.state.drawerState !is DrawerState.Tree -> {
                screens.updateScreen<Screen.Sandbox> { it.copy(state = it.state.copy(drawerState = DrawerState.Tree())) }
            }
            screens.value.size > 1 -> screens.value = screens.value.dropLast(1)
        }
    }

    private fun addProject(project: Project){

    }

    private fun updateTree(updated: Component) {
        //TODO: update project with new tree
        screens.updateSandbox { it.copy(opened = updated, drawerState = DrawerState.Tree()) }
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