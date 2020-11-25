package com.andb.apps.composesandbox.ui.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.toColors
import com.andb.apps.composesandbox.model.Project
import com.andb.apps.composesandbox.ui.common.RenderComponent

@Composable
fun ProjectItem(project: Project, selected: Boolean = false, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .border(2.dp, MaterialTheme.colors.secondaryVariant, RoundedCornerShape(8.dp))
                .aspectRatio(.5f)
                .fillMaxWidth()
        ) {
            MaterialTheme(colors = project.theme.toColors()) {
                RenderComponent(component = project.screens.first())
            }
            if (selected) {
                val size = remember { mutableStateOf(IntSize(0, 0)) }
                Box(
                    modifier = Modifier
                        .background(
                            brush = LinearGradient(
                                listOf(
                                    MaterialTheme.colors.primary.copy(alpha = .5f),
                                    MaterialTheme.colors.primary
                                ),
                                startX = 0f,
                                startY = 0f,
                                endX = size.value.width.toFloat(),
                                endY = size.value.height.toFloat()
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .onSizeChanged {
                            size.value = it
                        }
                        .fillMaxSize()
                ) {
                    Icon(
                        asset = Icons.Default.Check,
                        modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd),
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
        Text(text = project.name, style = MaterialTheme.typography.subtitle1, modifier = Modifier.padding(8.dp))
    }
}