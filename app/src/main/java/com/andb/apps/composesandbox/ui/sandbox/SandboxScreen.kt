package com.andb.apps.composesandbox.ui.sandbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
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
import com.andb.apps.composesandbox.state.DrawerState
import com.andb.apps.composesandbox.ui.common.*
import com.andb.apps.composesandbox.ui.sandbox.drawer.Drawer
import com.andb.apps.composesandboxdata.model.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SandboxScreen(sandboxState: ViewState.Sandbox, onUpdateProject: (Project) -> Unit) {
    val actionHandler = Handler
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
            val (height, setHeight) = remember { mutableStateOf(0) }
            val canDragSheet = remember { mutableStateOf(true) }
            BottomSheetScaffold(
                modifier = Modifier.onGloballyPositioned { setHeight(it.size.height) },
                scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState),
                sheetPeekHeight = 88.dp,
                sheetGesturesEnabled = true,
                sheetContent = {
                    Drawer(
                        sandboxState = sandboxState,
                        sheetState = bottomSheetState,
                        modifier = Modifier.height(with(AmbientDensity.current) { (height / 2).toDp() } + 88.dp),
                        onScreenUpdate = { onUpdateProject.invoke(sandboxState.project.updatedTree(it)) },
                        onThemeUpdate = { onUpdateProject.invoke(sandboxState.project.copy(theme = it)) },
                        onExtractComponent = { oldComponent ->
                            val customTree = PrototypeTree(name = sandboxState.project.nextComponentName(), treeType = TreeType.Component, component = oldComponent)
                            val customComponent = PrototypeComponent.Custom(treeID = customTree.id)
                            val editedTrees = sandboxState.project.trees.map { it.copy(component = it.component.replaceWithCustom(oldComponent.id, customComponent)) }
                            onUpdateProject.invoke(sandboxState.project.copy(trees = editedTrees + customTree))
                        },
                        onDragUpdate = { canDragSheet.value = !it },
                        onDeleteTree = {
                            val newTrees = sandboxState.project.trees.filter { it.id != sandboxState.openedTree.id }.map {
                                it.copy(component = it.component.replaceCustomWith(sandboxState.openedTree.id, sandboxState.openedTree.component))
                            }
                            actionHandler.invoke(UserAction.UpdateSandbox((sandboxState.toScreen() as Screen.Sandbox).copy(openedTreeID = newTrees.first().id)))
                            onUpdateProject.invoke(sandboxState.project.copy(trees = newTrees))
                        }
                    )
                },
                bodyContent = {
                    val offset = if (bottomSheetState.offset.value.isNaN()) 0f else bottomSheetState.offset.value
                    val scale = (offset / height).coerceIn(0f..1f)
                    //Box(Modifier.background(Color.Red).size(128.dp))
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colors.secondary)
                            .graphicsLayer(scaleX = scale, scaleY = scale, transformOrigin = TransformOrigin(0.5f, 0f))
                            .padding(start = 32.dp, end = 32.dp, top = 32.dp, bottom = 32.dp)
                            .clipToBounds()
                            .background(MaterialTheme.colors.background)
                            .fillMaxSize()
                    ) {
                        RenderComponentParent(
                            theme = sandboxState.project.theme,
                            component = sandboxState.openedTree.component,
                            selected = sandboxState.drawerStack.filterIsInstance<DrawerState.EditComponent>().firstOrNull()?.component?.id
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun SandboxAppBar(sandboxState: ViewState.Sandbox, project: Project, iconState: BackdropValue, onToggle: () -> Unit) {
    val actionHandler = Handler
    TopAppBar(
        navigationIcon = {
            IconToggleButton(
                checked = iconState == BackdropValue.Revealed,
                onCheckedChange = { onToggle.invoke() }
            ) {
                when (iconState) {
                    BackdropValue.Concealed -> Icon(imageVector = Icons.Default.Menu, contentDescription = "Open Backdrop")
                    else -> Icon(imageVector = Icons.Default.Clear, contentDescription = "Close Backdrop")
                }
            }
        },
        title = {
            Box {
                BasicTextField(
                    value = project.name,
                    textStyle = AmbientTextStyle.current.copy(color = AmbientContentColor.current),
                    cursorColor = AmbientContentColor.current,
                    onValueChange = {
                        actionHandler.invoke(UserAction.UpdateProject(project.copy(name = it)))
                    },
                    decorationBox = { innerTextField ->
                        innerTextField()
                        if (project.name.isEmpty()) {
                            Text(
                                text = "Project Name",
                                style = MaterialTheme.typography.h6,
                                color = MaterialTheme.colors.onPrimary.copy(alpha = .12f)
                            )
                        }
                    }
                )
            }
        },
        actions = {
            IconButton(onClick = { actionHandler.invoke(UserAction.OpenDrawerScreen(DrawerScreen.EditTheme)) }) { Icon(imageVector = Icons.Default.Palette, contentDescription = "Open Theme Editor") }
            IconButton(onClick = { actionHandler.invoke(UserAction.OpenScreen(Screen.Preview(project.id, sandboxState.openedTree.id))) }) { Icon(imageVector = Icons.Default.PlayCircleFilled, contentDescription = "Play Prototype") }
            OverflowMenu {
                MenuItem(icon = Icons.Default.Share, title = "Export Code") {
                    val action = UserAction.OpenScreen(Screen.Code(project.id))
                    actionHandler.invoke(action)
                }

                if (BuildConfig.DEBUG) {
                    MenuItem(icon = Icons.Default.Build, title = "Test Screen") {
                        val action = UserAction.OpenScreen(Screen.Test)
                        actionHandler.invoke(action)
                    }
                }
                ConfirmationDialog { confirmationState ->
                    MenuItem(icon = Icons.Default.Delete, title = "Delete Project") {
                        confirmationState.confirm("Delete Project?", "You cannot undo this action") {
                            val action = UserAction.DeleteProject(sandboxState.project)
                            actionHandler.invoke(action)
                        }
                    }
                }
            }
        },
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.primary
    )
}