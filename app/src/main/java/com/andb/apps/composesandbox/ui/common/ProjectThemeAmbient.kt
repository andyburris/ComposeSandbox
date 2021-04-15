package com.andb.apps.composesandbox.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.andb.apps.composesandboxdata.model.Project

val LocalProject = staticCompositionLocalOf<Project> { error("No LocalProject provided") }

val ProjectTheme @Composable get() = LocalProject.current.theme

@Composable
fun ProjectProvider(project: Project, content: @Composable() () -> Unit){
    CompositionLocalProvider(LocalProject provides project) {
        content()
    }
}
