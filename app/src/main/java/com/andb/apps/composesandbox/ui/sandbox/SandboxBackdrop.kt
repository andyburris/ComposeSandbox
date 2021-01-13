package com.andb.apps.composesandbox.ui.sandbox.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andb.apps.composesandbox.state.*
import com.andb.apps.composesandboxdata.model.*

@Composable
fun SandboxBackdrop(sandboxState: ViewState.Sandbox, onUpdateProject: (Project) -> Unit) {
    val actionHandler = Handler
    Column(Modifier.padding(vertical = 8.dp)) {
        val (screens, components) = sandboxState.project.trees.partition { it.treeType == TreeType.Screen }
        CategoryHeader(category = "Screens", modifier = Modifier.padding(bottom = 8.dp)) {
            onUpdateProject.invoke(sandboxState.project.copy(trees = sandboxState.project.trees + PrototypeTree(name = sandboxState.project.nextScreenName(), treeType = TreeType.Screen)))
        }
        val showEditDialog = savedInstanceState<String?> { null }
        screens.forEach { screen ->
            TreeItem(
                tree = screen,
                selected = sandboxState.openedTree == screen,
                onEdit = { showEditDialog.value = screen.id }
            ) {
                if (sandboxState.openedTree.id != screen.id) {
                    actionHandler.invoke(UserAction.UpdateSandbox((sandboxState.toScreen() as Screen.Sandbox).copy(openedTreeID = screen.id, drawerScreens = listOf(DrawerScreen.Tree))))
                }
            }
        }
        CategoryHeader(category = "Components", modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)) {
            onUpdateProject.invoke(sandboxState.project.copy(trees = sandboxState.project.trees + PrototypeTree(name = sandboxState.project.nextComponentName(), treeType = TreeType.Component)))
        }
        components.forEach { component ->
            TreeItem(
                tree = component,
                selected = sandboxState.openedTree == component,
                onEdit = { showEditDialog.value = component.id }
            ) {
                if (sandboxState.openedTree.id != component.id) {
                    actionHandler.invoke(UserAction.UpdateSandbox((sandboxState.toScreen() as Screen.Sandbox).copy(openedTreeID = component.id, drawerScreens = listOf(DrawerScreen.Tree))))
                }
            }
        }
        val openedTree = remember(showEditDialog.value) { sandboxState.project.trees.find { it.id == showEditDialog.value } }
        if (openedTree != null) {
            Dialog(onDismissRequest = { showEditDialog.value = null }) {
                ProvideTextStyle(TextStyle.Default.copy(color = MaterialTheme.colors.onBackground)) {
                    EditTreeDialog(
                        tree = openedTree,
                        canDelete = when(openedTree.treeType) {
                            TreeType.Screen -> screens.size > 2
                            TreeType.Component -> true
                        },
                        onDismiss = { showEditDialog.value = null },
                        onDelete = {
                            showEditDialog.value = null
                            val newTrees = sandboxState.project.trees.filter { it.id != openedTree.id }.map {
                                it.copy(component = it.component.replaceCustomWith(openedTree.id, openedTree.component))
                            }
                            actionHandler.invoke(UserAction.UpdateSandbox((sandboxState.toScreen() as Screen.Sandbox).copy(openedTreeID = newTrees.first().id)))
                            onUpdateProject.invoke(sandboxState.project.copy(trees = newTrees))
                        },
                        onUpdateScreen = { updatedScreen ->
                            showEditDialog.value = null
                            val newTrees = sandboxState.project.trees.map { if (it.id == updatedScreen.id) updatedScreen else it }
                            onUpdateProject.invoke(sandboxState.project.copy(trees = newTrees))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TreeItem(tree: PrototypeTree, selected: Boolean, onEdit: () -> Unit, onSelect: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .clickable (onClick = onSelect)
            .background(if (selected) MaterialTheme.colors.secondary else Color.Transparent)
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val icon = when(tree.treeType) {
                TreeType.Screen -> Icons.Default.PhoneAndroid
                TreeType.Component -> Icons.Default.Toll
            }
            Icon(imageVector = icon, tint = MaterialTheme.colors.onPrimary)
            Text(text = tree.name, color = MaterialTheme.colors.onPrimary, modifier = Modifier.padding(start = 16.dp))
        }
        Icon(imageVector = Icons.Default.Edit, tint = MaterialTheme.colors.onPrimary, modifier = Modifier.clickable(onClick = onEdit))
    }
}

@Composable
private fun EditTreeDialog(tree: PrototypeTree, canDelete: Boolean, onDismiss: () -> Unit, onDelete: () -> Unit, onUpdateScreen: (PrototypeTree) -> Unit) {
    val name = savedInstanceState { tree.name }
    Column(Modifier.background(MaterialTheme.colors.background, RoundedCornerShape(16.dp))) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(32.dp).fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Clear, tint = MaterialTheme.colors.onSecondary, modifier = Modifier.clickable(onClick = onDismiss))
                Text(text = if (tree.treeType == TreeType.Screen) "Edit Screen" else "Edit Component", style = MaterialTheme.typography.h6, color = MaterialTheme.colors.onSecondary, modifier = Modifier.padding(start = 16.dp))
            }
            if (canDelete) {
                Icon(imageVector = Icons.Default.Delete, tint = MaterialTheme.colors.onSecondary, modifier = Modifier.clickable(onClick = onDelete))
            }
        }
        OutlinedTextField(
            value = name.value,
            label = { Text(text = if (tree.treeType == TreeType.Screen) "Screen Name" else "Component Name") },
            onValueChange = { name.value = it },
            modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth()
        )
        Row (horizontalArrangement = Arrangement.End, modifier = Modifier.padding(16.dp).fillMaxWidth()){
            TextButton(
                onClick = {
                    val newScreen = tree.copy(name = name.value)
                    onUpdateScreen.invoke(newScreen)
                }
            ) {
                Text(text = "OK")
            }
        }
    }
}

@Composable
private fun CategoryHeader(category: String, modifier: Modifier = Modifier, onAdd: () -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
        Text(text = category.toUpperCase(), style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.onPrimary)
        Icon(imageVector = Icons.Default.Add, tint = MaterialTheme.colors.onPrimary, modifier = Modifier.clickable(onClick = onAdd))
    }
}
