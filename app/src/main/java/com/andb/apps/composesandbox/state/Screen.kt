package com.andb.apps.composesandbox.state

import com.andb.apps.composesandbox.data.model.Project

sealed class Screen {
    data class Projects(val projects: List<Project>) : Screen()
    data class Sandbox(val project: Project) : Screen()
}