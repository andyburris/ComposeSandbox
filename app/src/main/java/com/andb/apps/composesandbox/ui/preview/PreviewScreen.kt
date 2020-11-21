package com.andb.apps.composesandbox.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andb.apps.composesandbox.data.model.toColors
import com.andb.apps.composesandbox.model.Project
import com.andb.apps.composesandbox.model.PrototypeComponent
import com.andb.apps.composesandbox.ui.common.RenderComponent

@Composable
fun PreviewScreen(project: Project, previewComponent: PrototypeComponent) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
    ) {
        MaterialTheme(colors = project.theme.toColors()) {
            RenderComponent(component = previewComponent)
        }
    }
}