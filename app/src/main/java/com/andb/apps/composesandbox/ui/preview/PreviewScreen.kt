package com.andb.apps.composesandbox.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andb.apps.composesandbox.model.Project
import com.andb.apps.composesandbox.model.PrototypeScreen
import com.andb.apps.composesandbox.ui.common.RenderComponentParent

@Composable
fun PreviewScreen(project: Project, previewScreen: PrototypeScreen) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
    ) {
        RenderComponentParent(theme = project.theme, component = previewScreen.tree)
    }
}