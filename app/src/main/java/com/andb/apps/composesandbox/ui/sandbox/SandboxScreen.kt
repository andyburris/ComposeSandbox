package com.andb.apps.composesandbox.ui.sandbox

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.TransformOrigin
import androidx.compose.ui.drawLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.onPositioned
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.Project
import com.andb.apps.composesandbox.state.SandboxState
import com.andb.apps.composesandbox.ui.common.RenderComponent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SandboxScreen(sandboxState: SandboxState) {
    val (backdropState, setBackdropState) = remember { mutableStateOf(BackdropState.CONCEALED) }
    Backdrop(
        backdropState = backdropState,
        modifier = Modifier.fillMaxSize(),
        peekContent = { state ->
            SandboxAppBar(
                project = sandboxState.project,
                iconState = backdropState,
                onToggle = { setBackdropState(backdropState.other()) }
            )
        },
        backdropContent = {
            SandboxBackdrop(project = sandboxState.project)
        },
        bodyColor = Color(229, 229, 229),
        bodyContent = {
            Drawer(sandboxState = sandboxState) { sheetState ->
                MaterialTheme(colors = sandboxState.project.theme.colors) {
                    val (height, setHeight) = remember { mutableStateOf(0) }
                    val scale = (sheetState.offset.value / height).coerceIn(0.5f..1f)

                    Stack(
                        modifier = Modifier
                            .drawLayer(scaleX = scale, scaleY = scale, transformOrigin = TransformOrigin(0.5f, 0f))
                            .onPositioned { setHeight(it.size.height) }
                            .padding(start = 32.dp, end = 32.dp, top = 32.dp, bottom = 100.dp)
                            .background(MaterialTheme.colors.background)
                            .fillMaxSize()
                    ) {
                        RenderComponent(component = sandboxState.openedTree)
                    }
                }
            }
        }
    )
}

@Composable
private fun SandboxAppBar(project: Project, iconState: BackdropState, onToggle: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconToggleButton(
                checked = iconState == BackdropState.REVEALED,
                onCheckedChange = { onToggle.invoke() }
            ) {
                Icon(asset = if (iconState == BackdropState.CONCEALED) Icons.Default.Menu else Icons.Default.Clear)
            }
        },
        title = { Text(text = project.name) },
        actions = {
            IconButton(onClick = {}) { Icon(asset = Icons.Default.Palette) }
            IconButton(onClick = {}) { Icon(asset = Icons.Default.PlayCircleFilled) }
            IconButton(onClick = {}) { Icon(asset = Icons.Default.MoreVert) }
        },
        elevation = 0.dp
    )
}

@Composable
private fun SandboxBackdrop(project: Project) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        CategoryHeader(category = "Screens", onAdd = {})
        CategoryHeader(category = "Components", onAdd = {})
    }
}

@Composable
private fun CategoryHeader(category: String, onAdd: () -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(text = category.toUpperCase(), style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.onPrimary)
        Icon(asset = Icons.Default.Add, tint = MaterialTheme.colors.onPrimary)
    }
}
