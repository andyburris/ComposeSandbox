package com.andb.apps.composesandbox.state

import com.andb.apps.composesandboxdata.model.Project
import com.andb.apps.composesandboxdata.state.ProjectAction

sealed class Action

sealed class UserAction : Action() {
    /** Navigate back in the navigation stack */
    object Back : UserAction()
    /** Add a [Screen] to the top of the navigation stack */
    data class OpenScreen(val screen: Screen) : UserAction()
    data class UpdateSandbox(val screen: Screen.Sandbox) : UserAction()
    data class OpenDrawerScreen(val drawerScreen: DrawerScreen) : UserAction()

    /** Add a [Project] to the list of saved projects */
    data class AddProject(val project: Project) : UserAction()
    data class UpdateProject (val project: Project, val action: ProjectAction) : UserAction()
    data class DeleteProject (val project: Project) : UserAction()
    data class Undo(val project: Project) : UserAction()
    data class Redo(val project: Project) : UserAction()


}