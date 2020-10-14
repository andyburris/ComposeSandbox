package com.andb.apps.composesandbox.state

import com.andb.apps.composesandbox.data.model.Component
import com.andb.apps.composesandbox.data.model.Project
import com.andb.apps.composesandbox.data.model.PrototypeModifier

sealed class Action

sealed class UserAction : Action() {
    /** Navigate back in the navigation stack */
    object Back : UserAction()
    /** Add a [Screen] to the top of the navigation stack */
    data class OpenScreen(val screen: Screen) : UserAction()
    /** Add a [Project] to the list of saved projects */
    data class AddProject(val name: String) : UserAction()
    /** Open the editor for [component] in [Screen.Sandbox]*/
    data class OpenComponent(val component: Component) : UserAction()
    /** Open the list of components to add in [Screen.Sandbox]*/
    object OpenComponentList : UserAction()
    data class MoveComponent(val moving: Component) : UserAction()
    data class UpdateTree (val updated: Component) : UserAction()
    /** Update a component in the currently opened tree */
    data class UpdateComponent(val updating: Component) : UserAction()
    data class UpdateModifier(val editingComponent: Component, val updating: PrototypeModifier) : UserAction()
    data class OpenModifierList(val editingComponent: Component) : UserAction()
    data class EditModifier(val editingComponent: Component, val modifier: PrototypeModifier) : UserAction()
}