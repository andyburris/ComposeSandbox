package com.andb.apps.composesandbox.ui.sandbox

import androidx.compose.Composable
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.*
import androidx.ui.unit.dp
import com.andb.apps.composesandbox.data.model.Project
import com.andb.apps.composesandbox.ui.common.BottomSheetLayout
import com.andb.apps.composesandbox.ui.common.BottomSheetState
import com.andb.apps.composesandbox.ui.common.RenderComponent

@Composable
fun SandboxScreen(project: Project) {
    val (backdropState, setBackdropState) = state { BackdropState.CONCEALED }
    Backdrop(
        backdropState = backdropState,
        modifier = Modifier.fillMaxSize(),
        peekContent = { state ->
            SandboxAppBar(
                project = project,
                iconState = backdropState,
                onToggle = { setBackdropState(backdropState.other()) }
            )
        },
        backdropContent = {
            SandboxBackdrop(project = project)
        },
        bodyColor = Color(229, 229, 229),
        bodyContent = {
            val sheetState = remember { mutableStateOf(BottomSheetState.Peek) }
            BottomSheetLayout(
                sheetState = sheetState.value,
                onStateChange = { sheetState.value = it },
                hideable = false,
                drawerContent = {
                    Text(text = project.name)
                }
            ) {
                MaterialTheme(colors = project.theme.colors) {
                    Stack(Modifier.padding(32.dp).drawBackground(MaterialTheme.colors.background).fillMaxSize()) {
                        RenderComponent(component = project.screens.first())
                    }
                }
            }
        }
    )
}

@Composable
private fun SandboxAppBar(project: Project, iconState: BackdropState, onToggle: () -> Unit){
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
private fun SandboxBackdrop(project: Project){
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
