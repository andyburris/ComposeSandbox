package com.andb.apps.composesandbox.state

import com.andb.apps.composesandbox.model.Project
import com.andb.apps.composesandbox.model.PrototypeComponent

sealed class Action

sealed class UserAction : Action() {
    /** Navigate back in the navigation stack */
    object Back : UserAction()
    /** Add a [Screen] to the top of the navigation stack */
    data class OpenScreen(val screen: Screen) : UserAction()
    data class UpdateScreen(val screen: Screen) : UserAction()

    /** Add a [Project] to the list of saved projects */
    data class AddProject(val name: String) : UserAction()
    /** Open the list of components to add in [Screen.Sandbox]*/
    object OpenComponentList : UserAction()
    data class MoveComponent(val moving: PrototypeComponent) : UserAction()
    data class UpdateTree (val updated: PrototypeComponent) : UserAction()
    object OpenModifierList : UserAction()
    /** Open the editor for the component with [componentID] in [Screen.Sandbox]*/
    data class OpenComponent(val componentID: String) : UserAction()
    /** Open the editor for the modifier with [modifierID] in [Screen.Sandbox]*/
    data class EditModifier(val modifierID: String) : UserAction()
    object OpenThemeEditor : UserAction()
}