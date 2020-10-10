package com.andb.apps.composesandbox.ui.projects

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.Project
import com.andb.apps.composesandbox.ui.common.RenderComponent

@Composable
fun ProjectItem(project: Project, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(2.dp, Color.Black.copy(alpha = .12f)),
            modifier = Modifier.aspectRatio(.5f).fillMaxWidth()
        ){
            RenderComponent(component = project.screens.first())
        }
        Text(text = project.name, style = MaterialTheme.typography.subtitle1, modifier = Modifier.padding(8.dp))
    }
}