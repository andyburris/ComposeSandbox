package com.andb.apps.composesandbox.state

sealed class Action

sealed class UserAction : Action() {
    object Back : UserAction()
    data class OpenScreen(val screen: Screen) : UserAction()
    data class AddProject(val name: String) : UserAction()
}