package com.andb.apps.composesandbox.ui.projects

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Border
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.aspectRatio
import androidx.ui.layout.fillMaxWidth
import androidx.ui.material.Surface
import androidx.ui.unit.dp
import com.andb.apps.composesandbox.data.model.Project
import com.andb.apps.composesandbox.ui.common.RenderComponent

@Composable
fun ProjectItem(project: Project, modifier: Modifier = Modifier, onClick: (project: Project) -> Unit) {
    Column(modifier = modifier.clickable(onClick = { onClick.invoke(project) })) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = Border(1.dp, Color.Gray),
            modifier = Modifier.aspectRatio(.5f).fillMaxWidth()
        ){
            RenderComponent(component = project.screens.first())
        }
        Text(text = project.name)
    }
}