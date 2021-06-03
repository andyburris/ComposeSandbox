package com.andb.apps.composesandbox.state

import com.andb.apps.composesandboxdata.local.DatabaseHelper
import com.andb.apps.composesandboxdata.model.Project
import com.andb.apps.composesandboxdata.model.apply
import com.andb.apps.composesandboxdata.model.redo
import com.andb.apps.composesandboxdata.model.undo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalCoroutinesApi::class)
class Machine(coroutineScope: CoroutineScope) {
    private val allProjects: Flow<List<Project>> = DatabaseHelper.allProjects
    val screens = MutableStateFlow(listOf<Screen>(Screen.Projects))

    val stack: StateFlow<List<ViewState>> = combine(allProjects, screens) { projects, screens ->
        screens.map { screen ->
            screen.toViewState(projects)
        }.filterNotNull()
    }.stateIn(coroutineScope, SharingStarted.Lazily, listOf(ViewState.Projects(listOf())))

    operator fun plusAssign(action: Action) = handleAction(action)

    @OptIn(ExperimentalTime::class)
    fun handleAction(action: Action) {
        when (action) {
            UserAction.Back -> handleBack()
            is UserAction.OpenScreen -> {
                screens.value += action.screen
                println("screens = ${screens.value}")
            }
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