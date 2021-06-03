package com.andb.apps.composesandbox.ui.preview

import androidx.compose.runtime.Composable
import com.andb.apps.composesandbox.ui.common.ProjectProvider
import com.andb.apps.composesandbox.ui.common.RenderComponentParent
import com.andb.apps.composesandboxdata.model.Project
import com.andb.apps.composesandboxdata.model.PrototypeTree

@Composable
fun PreviewScreen(project: Project, previewScreen: PrototypeTree) {
    ProjectProvider(project = project) {
        RenderComponentParent(theme = project.theme, component = previewScreen.component)
    }
}