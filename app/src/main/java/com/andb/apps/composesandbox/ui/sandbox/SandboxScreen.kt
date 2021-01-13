package com.andb.apps.composesandbox.ui.sandbox

import androidx.compose.animation.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.BuildConfig
import com.andb.apps.composesandbox.state.*
import com.andb.apps.composesandbox.ui.common.ProjectProvider
import com.andb.apps.composesandbox.ui.common.RenderComponentParent
import com.andb.apps.composesandbox.ui.sandbox.drawer.Drawer
import com.andb.apps.composesandbox.ui.sandbox.drawer.SandboxBackdrop
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
                        onExtractComponent = { oldComponent ->
                            val customTree = PrototypeTree(name = sandboxState.project.nextComponentName(), treeType = TreeType.Component, component = oldComponent)
                            val customComponent = PrototypeComponent.Custom(treeID = customTree.id)
                            val editedTrees = sandboxState.project.trees.map { it.copy(component = it.component.replaceWithCustom(oldComponent.id, customComponent)) }
                            onUpdateProject.invoke(sandboxState.project.copy(trees = editedTrees + customTree))
                        },
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
                    RenderComponentParent(theme = sandboxState.project.theme, component = sandboxState.openedTree.component)
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