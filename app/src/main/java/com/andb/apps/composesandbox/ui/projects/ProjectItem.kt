package com.andb.apps.composesandbox.ui.projects

import androidx.compose.foundation.Border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.Project
import com.andb.apps.composesandbox.ui.common.RenderComponent

@Composable
fun ProjectItem(project: Project, modifier: Modifier = Modifier, onClick: (project: Project) -> Unit) {
    Column(modifier = modifier.clickable(onClick = { onClick.invoke(project) })) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.Gray),
            modifier = Modifier.aspectRatio(.5f).fillMaxWidth()
        ){
            RenderComponent(component = project.screens.first())
        }
        Text(text = project.name)
    }
}