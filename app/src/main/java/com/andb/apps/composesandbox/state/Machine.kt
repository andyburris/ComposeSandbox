package com.andb.apps.composesandbox.state

import com.andb.apps.composesandboxdata.local.DatabaseHelper
import com.andb.apps.composesandboxdata.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalCoroutinesApi::class)
class Machine(coroutineScope: CoroutineScope) {
    private val allProjects: Flow<List<Project>> = DatabaseHelper.allProjects
    private val screens = MutableStateFlow(listOf<Screen>(Screen.Projects))

    val stack: StateFlow<List<ViewState>> = combine(allProjects, screens) { projects, screens ->
        screens.map { screen ->
            when (screen) {
                Screen.Projects -> ViewState.Projects(projects)
                Screen.AddProject -> ViewState.AddProject
                is Screen.Sandbox -> {
                    val project = projects.first { it.id == screen.projectID }
                    val openedTree = project.trees.find { it.id == screen.openedTreeID } ?: project.trees.first()
                    val drawerStack = screen.drawerScreens.map { drawerScreen ->
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
                    ViewState.Sandbox(project, screen.openedTreeID, validStack)
                }
                is Screen.Preview -> {
                    val project = projects.first { it.id == screen.projectID }
                    val currentScreen = project.trees.first { it.id == screen.currentScreenID }
                    ViewState.Preview(project, currentScreen)
                }
                is Screen.Code -> ViewState.Code(projects.first { it.id == screen.projectID })
                is Screen.Test -> ViewState.Test
            }
        }
    }.stateIn(coroutineScope, SharingStarted.Lazily, listOf(ViewState.Projects(listOf())))

    operator fun plusAssign(action: Action) = handleAction(action)

    @OptIn(ExperimentalTime::class)
    fun handleAction(action: Action) {
        when (action) {
            UserAction.Back -> handleBack()
            is UserAction.OpenScreen -> screens.value += action.screen
            is UserAction.UpdateSandbox -> screens.updateSandbox { action.screen }
            is UserAction.OpenDrawerScreen -> screens.updateSandbox {
                it.copy(drawerScreens = it.drawerScreens + action.drawerScreen)
            }
            is UserAction.AddProject -> {
                DatabaseHelper.upsertProject(action.project)
                screens.value = listOf(Screen.Projects, Screen.Sandbox(action.project.id, action.project.trees.first().id))
            }
            is UserAction.UpdateProject -> {
                println("UserAction.UpdateProject time = " + measureTime {
                    var newProject: Project
                    val applyTime = measureTime { newProject = action.project.apply(action.action) }
                    println("apply time = $applyTime")
                    DatabaseHelper.upsertProject(newProject)
                })
            }
            is UserAction.DeleteProject -> {
                screens.value = listOf(Screen.Projects)
                DatabaseHelper.deleteProject(action.project)
            }
            is UserAction.Undo -> DatabaseHelper.upsertProject(action.project.undo())
            is UserAction.Redo -> DatabaseHelper.upsertProject(action.project.redo())
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