package com.andb.apps.composesandbox.state

import com.andb.apps.composesandbox.data.model.Component

sealed class Action

sealed class UserAction : Action() {
    object Back : UserAction()
    data class OpenScreen(val screen: Screen) : UserAction()
    data class AddProject(val name: String) : UserAction()
    data class OpenComponent(val component: Component) : UserAction()
    object OpenComponentList : UserAction()
    data class MoveComponent(val moving: Component) : UserAction()
    data class UpdateTree (val updated: Component) : UserAction()
}