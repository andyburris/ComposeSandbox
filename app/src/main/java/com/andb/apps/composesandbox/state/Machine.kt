package com.andb.apps.composesandbox.state

import com.andb.apps.composesandbox.data.model.Project
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
        }
    }

    private fun handleBack() {
        if (screens.value.size > 1){
            screens.value = screens.value.dropLast(1)
        }
    }

    private fun addProject(project: Project){

    }
}