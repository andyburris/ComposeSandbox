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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andb.apps.composesandbox.BuildConfig
import com.andb.apps.composesandbox.model.*
import com.andb.apps.composesandbox.state.*
import com.andb.apps.composesandbox.ui.common.ProjectThemeProvider
import com.andb.apps.composesandbox.ui.common.RenderComponentParent
import com.andb.apps.composesandbox.ui.sandbox.drawer.Drawer
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.ComponentItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SandboxScreen(sandboxState: ViewState.Sandbox, onUpdateProject: (Project) -> Unit) {
    ProjectThemeProvider(projectTheme = sandboxState.project.theme) {
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
                        onScreenUpdate = { onUpdateProject.invoke(sandboxState.project.updatedScreen(it)) },
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
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        CategoryHeader(category = "Screens", modifier = Modifier.padding(bottom = 8.dp)) {
            val oldScreensMax = sandboxState.project.screens.mapNotNull { it.name.removePrefix("Screen ").toIntOrNull() }.maxOrNull() ?: 0
            val screenNumber = maxOf(oldScreensMax, sandboxState.project.screens.size) + 1
            onUpdateProject.invoke(sandboxState.project.copy(screens = sandboxState.project.screens + PrototypeScreen(name = "Screen $screenNumber")))
        }
        sandboxState.project.screens.forEach { screen ->
            val showEditDialog = savedInstanceState { false }
            ScreenItem(
                screen = screen,
                selected = sandboxState.openedTree == screen,
                onEdit = { showEditDialog.value = true }
            ) {
                actionHandler.invoke(UserAction.UpdateSandbox((sandboxState.toScreen() as Screen.Sandbox).copy(openedScreenID = screen.id)))
            }
            if (showEditDialog.value) {
                Dialog(onDismissRequest = { showEditDialog.value = false }) {
                    EditScreenDialog(
                        screen = screen,
                        canDelete = sandboxState.project.screens.size > 1,
                        onDismiss = { showEditDialog.value = false },
                        onDelete = {
                            showEditDialog.value = false
                            val newScreens = sandboxState.project.screens.filter { it.id != screen.id }
                            actionHandler.invoke(UserAction.UpdateSandbox((sandboxState.toScreen() as Screen.Sandbox).copy(openedScreenID = newScreens.first().id)))
                            onUpdateProject.invoke(sandboxState.project.copy(screens = newScreens))
                        },
                        onUpdateScreen = { updatedScreen ->
                            showEditDialog.value = false
                            val newScreens = sandboxState.project.screens.map { if (it.id == updatedScreen.id) updatedScreen else it }
                            onUpdateProject.invoke(sandboxState.project.copy(screens = newScreens))
                        }
                    )
                }
            }
        }
        CategoryHeader(category = "Components", modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)) {

        }
    }
}

@Composable
private fun ScreenItem(screen: PrototypeScreen, selected: Boolean, onEdit: () -> Unit, onSelect: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .clickable (onClick = onSelect)
            .background(if (selected) MaterialTheme.colors.secondary else Color.Transparent)
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.PhoneAndroid, tint = MaterialTheme.colors.onPrimary)
            Text(text = screen.name, color = MaterialTheme.colors.onPrimary, modifier = Modifier.padding(start = 16.dp))
        }
        Icon(imageVector = Icons.Default.Edit, tint = MaterialTheme.colors.onPrimary, modifier = Modifier.clickable(onClick = onEdit))
    }
}

@Composable
private fun EditScreenDialog(screen: PrototypeScreen, canDelete: Boolean, onDismiss: () -> Unit, onDelete: () -> Unit, onUpdateScreen: (PrototypeScreen) -> Unit) {
    val name = savedInstanceState { screen.name }
    val baseComponent = remember { mutableStateOf(screen.tree) } //TODO: use savedInstanceState
    Column(Modifier.background(MaterialTheme.colors.background, RoundedCornerShape(16.dp))) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(32.dp).fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Clear, tint = MaterialTheme.colors.onSecondary, modifier = Modifier.clickable(onClick = onDismiss))
                Text(text = "Edit Screen", style = MaterialTheme.typography.h6, color = MaterialTheme.colors.onSecondary, modifier = Modifier.padding(start = 16.dp))
            }
            if (canDelete) {
                Icon(imageVector = Icons.Default.Delete, tint = MaterialTheme.colors.onSecondary, modifier = Modifier.clickable(onClick = onDelete))
            }
        }
        OutlinedTextField(
            value = name.value,
            label = { Text(text = "Screen Name") },
            onValueChange = { name.value = it },
            modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth()
        )
        Text(text = "Base Component", style = MaterialTheme.typography.subtitle1, modifier = Modifier.padding(start = 32.dp, top = 32.dp, bottom = 16.dp))
        val dropdownOpen = remember { mutableStateOf(false) }
        DropdownMenu(
            toggle = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.clickable { dropdownOpen.value = true }.fillMaxWidth().padding(horizontal = 32.dp)) {
                    ComponentItem(component = baseComponent.value)
                    Icon(imageVector = Icons.Default.ArrowDropDown)
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
                    val newTree = baseComponent.value.withChildren(screen.tree.children).copy(modifiers = screen.tree.modifiers, properties = if (screen.tree::class == baseComponent::class) screen.tree.properties else baseComponent.value.properties) as PrototypeComponent.Group
                    val newScreen = screen.copy(name = name.value, tree = newTree)
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
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier.fillMaxWidth()) {
        Text(text = category.toUpperCase(), style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.onPrimary)
        Icon(imageVector = Icons.Default.Add, tint = MaterialTheme.colors.onPrimary, modifier = Modifier.clickable(onClick = onAdd))
    }
}
