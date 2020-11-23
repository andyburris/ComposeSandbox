package com.andb.apps.composesandbox.state

import com.andb.apps.composesandbox.local.DatabaseHelper
import com.andb.apps.composesandbox.model.Project
import com.andb.apps.composesandbox.model.findByIDInTree
import com.andb.apps.composesandbox.model.findModifierByIDInTree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class Machine(coroutineScope: CoroutineScope) {
    private val allProjects: Flow<List<Project>> = DatabaseHelper.allProjects
    private val screens = MutableStateFlow(listOf<Screen>(Screen.Projects))

    val stack: StateFlow<List<ViewState>> = combine(allProjects, screens) { projects, screens ->
        screens.map { screen ->
            when (screen) {
                Screen.Projects -> ViewState.ProjectsState(projects)
                is Screen.Sandbox -> {
                    val project = projects.first { it.id == screen.projectID }
                    val openedTree = project.screens.first { it.id == screen.openedTreeID }
                    val drawerStack = screen.drawerScreens.map { drawerScreen ->
                        when (drawerScreen) {
                            DrawerScreen.Tree -> DrawerState.Tree
                            DrawerScreen.AddComponent -> DrawerState.AddComponent
                            is DrawerScreen.EditComponent -> DrawerState.EditComponent(openedTree.findByIDInTree(drawerScreen.componentID)!!)
                            DrawerScreen.AddModifier -> DrawerState.AddModifier
                            is DrawerScreen.EditModifier -> DrawerState.EditModifier(openedTree.findModifierByIDInTree(drawerScreen.modifierID)!!)
                            DrawerScreen.EditTheme -> DrawerState.EditTheme
                        }
                    }
                    ViewState.SandboxState(project, openedTree, drawerStack)
                }
                is Screen.Preview -> {
                    val project = projects.first { it.id == screen.projectID }
                    val currentScreen = project.screens.first { it.id == screen.currentScreenID }
                    ViewState.PreviewState(project, currentScreen)
                }
                is Screen.Code -> ViewState.CodeState(projects.first { it.id == screen.projectID })
                is Screen.Test -> ViewState.TestState
            }
        }
    }.stateIn(coroutineScope, SharingStarted.Lazily, listOf(ViewState.ProjectsState(listOf())))

    operator fun plusAssign(action: Action) = handleAction(action)

    fun handleAction(action: Action) {
        when (action) {
            UserAction.Back -> handleBack()
            is UserAction.OpenScreen -> screens.value += action.screen
            is UserAction.OpenDrawerScreen -> screens.updateSandbox {
                it.copy(drawerScreens = it.drawerScreens + action.drawerScreen)
            }
            is UserAction.AddProject -> addProject(action.project)
            is UserAction.UpdateProject -> {
                DatabaseHelper.upsertProject(action.updated)
            }
        }
    }

    private fun handleBack() {
        val currentScreen = screens.value.last()
        when {
            currentScreen is Screen.Sandbox && currentScreen.drawerScreens.last() !is DrawerScreen.Tree -> {
                screens.updateSandbox { it.copy(drawerScreens = it.drawerScreens.dropLast(1)) }
            }
            screens.value.size > 1 -> screens.value = screens.value.dropLast(1)
        }
    }

    private fun addProject(project: Project) {
        DatabaseHelper.upsertProject(project)
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
private inline fun MutableStateFlow<List<Screen>>.updateSandbox(transform: (Screen.Sandbox) -> Screen.Sandbox) {
    updateScreen<Screen.Sandbox> { transform(it) }
}