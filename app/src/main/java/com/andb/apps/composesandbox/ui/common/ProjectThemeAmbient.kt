package com.andb.apps.composesandbox.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.staticAmbientOf
import com.andb.apps.composesandboxdata.model.Project

val AmbientProject = staticAmbientOf<Project>()

@Composable
val ProjectTheme get() = AmbientProject.current.theme

@Composable
fun ProjectProvider(project: Project, content: @Composable() () -> Unit){
    Providers(AmbientProject provides project) {
        content()
    }
}
