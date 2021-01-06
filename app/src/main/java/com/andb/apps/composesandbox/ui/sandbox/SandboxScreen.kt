package com.andb.apps.composesandbox.ui.sandbox

import androidx.compose.animation.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andb.apps.composesandbox.BuildConfig
import com.andb.apps.composesandbox.state.*
import com.andb.apps.composesandbox.ui.common.ProjectProvider
import com.andb.apps.composesandbox.ui.common.RenderComponentParent
import com.andb.apps.composesandbox.ui.sandbox.drawer.Drawer
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.ComponentItem
import com.andb.apps.composesandboxdata.model.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SandboxScreen(sandboxState: ViewState.Sandbox, onUpdateProject: (Project) -> Unit) {
    ProjectProvider(project = sandboxState.project) {
        val backdropState = rememberBackdropScaffoldState(initialValue = BackdropValue.Concealed)
        BackdropScaffold(
            scaffoldState = backdropState,
            appBar = {
                SandboxAppBar(
                    sandboxState = sandboxState,
                    project = sandboxState.project,
                    iconState = backdropState.value,
                    onToggle = { if (backdropState.value == BackdropValue.Concealed) backdropState.reveal() else backdropState.conceal() }
                )
            },
            backLayerContent = {
                SandboxBackdrop(sandboxState) {
                    onUpdateProject.invoke(it)
                }
            }
        ) {
            val bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
            val cornerRadius = animate(target = if (bottomSheetState.targetValue == BottomSheetValue.Expanded) 16.dp else 32.dp)
            val (height, setHeight) = remember { mutableStateOf(0) }
            val canDragSheet = remember { mutableStateOf(true) }
            BottomSheetScaffold(
                modifier = Modifier.onGloballyPositioned { setHeight(it.size.height) },
                scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState),
                sheetShape = RoundedCornerShape(topLeft = cornerRadius, topRight = cornerRadius),
                sheetPeekHeight = 88.dp,
                sheetGesturesEnabled = true,
                sheetContent = {
                    Drawer(
                        sandboxState = sandboxState,
                        sheetState = bottomSheetState,
                        modifier = Modifier.height(with(AmbientDensity.current) { (height/2).toDp() } + 88.dp),
                        onScreenUpdate = { onUpdateProject.invoke(sandboxState.project.updatedTree(it)) },
                        onThemeUpdate = { onUpdateProject.invoke(sandboxState.project.copy(theme = it)) },
                        onDragUpdate = { canDragSheet.value = !it }
                    )
                }
            ) {
                val offset = if (bottomSheetState.offset.value.isNaN()) 0f else bottomSheetState.offset.value
                val scale = (offset / height).coerceIn(0f..1f)
                println("height = $height, offset = ${bottomSheetState.offset.value}, scale = $scale")
                //Box(Modifier.background(Color.Red).size(128.dp))
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colors.secondary)
                        .graphicsLayer(scaleX = scale, scaleY = scale, transformOrigin = TransformOrigin(0.5f, 0f))
                        .padding(start = 32.dp, end = 32.dp, top = 32.dp, bottom = 32.dp /*+ with(AmbientDensity.current) { (height-offset).toDp() }*/)
                        .clipToBounds()
                        .background(MaterialTheme.colors.background)
                        .fillMaxSize()
                ) {
                    RenderComponentParent(theme = sandboxState.project.theme, component = sandboxState.openedTree.tree)
                }
            }
        }
    }
}

@Composable
private fun SandboxAppBar(sandboxState: ViewState.Sandbox, project: Project, iconState: BackdropValue, onToggle: () -> Unit) {
    val actionHandler = Handler
    val menuShowing = remember { mutableStateOf(false) }
    TopAppBar(
        navigationIcon = {
            IconToggleButton(
                checked = iconState == BackdropValue.Revealed,
                onCheckedChange = { onToggle.invoke() }
            ) {
                Icon(imageVector = if (iconState == BackdropValue.Concealed) Icons.Default.Menu else Icons.Default.Clear)
            }
        },
        title = { Text(text = project.name) },
        actions = {
            IconButton(onClick = { actionHandler.invoke(UserAction.OpenDrawerScreen(DrawerScreen.EditTheme)) }) { Icon(imageVector = Icons.Default.Palette) }
            IconButton(onClick = { actionHandler.invoke(UserAction.OpenScreen(Screen.Preview(project.id, sandboxState.openedTree.id))) }) { Icon(imageVector = Icons.Default.PlayCircleFilled) }
            DropdownMenu(
                toggle = {
                    IconButton(onClick = { menuShowing.value = true }) {
                        Icon(imageVector = Icons.Default.MoreVert)
                    }
                },
                expanded = menuShowing.value,
                onDismissRequest = {
                    menuShowing.value = false
                }
            ) {
                DropdownMenuItem(
                    onClick = {
                        val action = UserAction.OpenScreen(Screen.Code(project.id))
                        actionHandler.invoke(action)
                    }
                ) {
                    Text("Export Code")
                }
                if (BuildConfig.DEBUG) {
                    DropdownMenuItem(onClick = {
                        val action = UserAction.OpenScreen(Screen.Test)
                        actionHandler.invoke(action)
                    }) {
                        Text("Open Test Screen")
                    }
                }
                DropdownMenuItem(onClick = {
                    val action = UserAction.DeleteProject(sandboxState.project)
                    actionHandler.invoke(action)
                }) {
                    Text("Delete Project")
                }
            }
        },
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.primary
    )
}

@Composable
private fun SandboxBackdrop(sandboxState: ViewState.Sandbox, onUpdateProject: (Project) -> Unit) {
    val actionHandler = Handler
    Column(Modifier.padding(vertical = 8.dp)) {
        val (screens, components) = sandboxState.project.trees.partition { it.treeType == TreeType.Screen }
        CategoryHeader(category = "Screens", modifier = Modifier.padding(bottom = 8.dp)) {
            val oldScreensMax = screens.mapNotNull { it.name.removePrefix("Screen ").toIntOrNull() }.maxOrNull() ?: 0
            val screenNumber = maxOf(oldScreensMax, screens.size) + 1
            onUpdateProject.invoke(sandboxState.project.copy(trees = sandboxState.project.trees + PrototypeTree(name = "Screen $screenNumber", treeType = TreeType.Screen)))
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
            val oldComponentsMax = components.mapNotNull { it.name.removePrefix("Component ").toIntOrNull() }.maxOrNull() ?: 0
            val componentNumber = maxOf(oldComponentsMax, components.size) + 1
            onUpdateProject.invoke(sandboxState.project.copy(trees = sandboxState.project.trees + PrototypeTree(name = "Component $componentNumber", treeType = TreeType.Component)))
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
                                it.copy(tree = it.tree.replaceCustomWith(openedTree.id, openedTree.tree) as PrototypeComponent.Group)
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
    val baseComponent = remember { mutableStateOf(tree.tree) } //TODO: use savedInstanceState
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
        Text(text = "Base Component".toUpperCase(), style = MaterialTheme.typography.subtitle1, modifier = Modifier.padding(start = 32.dp, top = 32.dp, bottom = 16.dp), color = MaterialTheme.colors.onSecondary)
        val dropdownOpen = remember { mutableStateOf(false) }
        DropdownMenu(
            toggle = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.clickable { dropdownOpen.value = true }.fillMaxWidth().padding(horizontal = 32.dp)) {
                    ComponentItem(component = baseComponent.value)
                    Icon(imageVector = Icons.Default.ArrowDropDown, tint = MaterialTheme.colors.onSecondary)
                }
            },
            expanded = dropdownOpen.value,
            onDismissRequest = { dropdownOpen.value = false }
        ) {
            allComponents.filterIsInstance<PrototypeComponent.Group>().forEach {
                ComponentItem(component = it, modifier = Modifier.clickable(onClick = { baseComponent.value = it }).fillMaxWidth())
            }
        }
        Row (horizontalArrangement = Arrangement.End, modifier = Modifier.padding(16.dp).fillMaxWidth()){
            TextButton(
                onClick = {
                    val newTree = baseComponent.value.withChildren(tree.tree.children).copy(modifiers = tree.tree.modifiers, properties = if (tree.tree::class == baseComponent::class) tree.tree.properties else baseComponent.value.properties) as PrototypeComponent.Group
                    val newScreen = tree.copy(name = name.value, tree = newTree)
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
