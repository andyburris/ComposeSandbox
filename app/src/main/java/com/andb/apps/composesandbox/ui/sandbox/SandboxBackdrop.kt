package com.andb.apps.composesandbox.ui.sandbox

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Toll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.state.*
import com.andb.apps.composesandboxdata.model.*

@Composable
fun SandboxBackdrop(sandboxState: ViewState.Sandbox, onUpdateProject: (Project) -> Unit) {
    val actionHandler = Handler
    val (screens, components) = sandboxState.project.trees.partition { it.treeType == TreeType.Screen }
    LazyColumn {
        item {
            CategoryHeader(category = "Screens", modifier = Modifier.padding(bottom = 8.dp)) {
                onUpdateProject.invoke(sandboxState.project.copy(trees = sandboxState.project.trees + PrototypeTree(name = sandboxState.project.nextScreenName(), treeType = TreeType.Screen)))
            }
        }
        items(screens) { screen ->
            TreeItem(
                tree = screen,
                selected = sandboxState.openedTree == screen,
            ) {
                if (sandboxState.openedTree.id != screen.id) {
                    actionHandler.invoke(UserAction.UpdateSandbox((sandboxState.toScreen() as Screen.Sandbox).copy(openedTreeID = screen.id, drawerScreens = listOf(DrawerScreen.Tree))))
                }
            }
        }
        item {
            CategoryHeader(category = "Components", modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)) {
                onUpdateProject.invoke(sandboxState.project.copy(trees = sandboxState.project.trees + PrototypeTree(name = sandboxState.project.nextComponentName(), treeType = TreeType.Component)))
            }
        }
        items(components) { component ->
            TreeItem(
                tree = component,
                selected = sandboxState.openedTree == component,
            ) {
                if (sandboxState.openedTree.id != component.id) {
                    actionHandler.invoke(UserAction.UpdateSandbox((sandboxState.toScreen() as Screen.Sandbox).copy(openedTreeID = component.id, drawerScreens = listOf(DrawerScreen.Tree))))
                }
            }
        }
    }
}

@Composable
private fun TreeItem(tree: PrototypeTree, selected: Boolean, onSelect: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .clickable(onClick = onSelect)
            .background(if (selected) MaterialTheme.colors.secondary else Color.Transparent)
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val icon = when (tree.treeType) {
                TreeType.Screen -> Icons.Default.PhoneAndroid
                TreeType.Component -> Icons.Default.Toll
            }
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colors.onPrimary)
            Text(text = tree.name, color = MaterialTheme.colors.onPrimary, modifier = Modifier.padding(start = 16.dp))
        }
    }
}

@Composable
private fun CategoryHeader(category: String, modifier: Modifier = Modifier, onAdd: () -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier
        .padding(horizontal = 16.dp)
        .fillMaxWidth()) {
        Text(text = category.toUpperCase(), style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.onPrimary)
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colors.onPrimary, modifier = Modifier.clickable(onClick = onAdd))
    }
}
