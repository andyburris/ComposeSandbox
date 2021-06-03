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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.ui.common.ProjectProvider
import com.andb.apps.composesandbox.ui.common.RenderComponentParent
import com.andb.apps.composesandboxdata.model.Project

@Composable
fun ProjectItem(project: Project, modifier: Modifier = Modifier, selected: Boolean = false) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, MaterialTheme.colors.secondaryVariant, RoundedCornerShape(8.dp))
                .aspectRatio(LocalConfiguration.current.screenWidthDp.toFloat() / LocalConfiguration.current.screenHeightDp)
                .fillMaxWidth()
        ) {
            BoxWithConstraints() {
                val width = LocalConfiguration.current.screenWidthDp.dp
                val height = LocalConfiguration.current.screenHeightDp.dp
                val scaleX = maxWidth / width
                val scaleY = maxHeight / height
                println("projectItem, screen width = $width, boxWidth = ${maxWidth}, scaleX = $scaleX, scaleY = $scaleY")
                ProjectProvider(project = project) {
                    Box(modifier = Modifier.background(Color.Green).size(width = maxWidth, height = maxHeight))
                    Box(modifier = Modifier
                        .graphicsLayer(scaleX = scaleX, scaleY = scaleY)
                        .requiredSize(width, height)
                    ) {
                        RenderComponentParent(theme = project.theme, component = project.trees.first().component)
                    }
                }
            }
            if (selected) {
                val size = remember { mutableStateOf(IntSize(0, 0)) }
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colors.primary.copy(alpha = .5f),
                                    MaterialTheme.colors.primary
                                ),
                                start = Offset(0f,
                                    0f), end = Offset(size.value.width.toFloat(),
                                    size.value.height.toFloat()
                                )),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .onSizeChanged {
                            size.value = it
                        }
                        .fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomEnd),
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
        Text(text = project.name, style = MaterialTheme.typography.subtitle1, modifier = Modifier.padding(8.dp))
    }
}