package com.andb.apps.composesandbox.ui.sandbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.state.*
import com.andb.apps.composesandbox.ui.common.*
import com.andb.apps.composesandbox.ui.sandbox.drawer.Drawer
import com.andb.apps.composesandboxdata.model.*
import com.andb.apps.composesandboxdata.state.ProjectAction
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SandboxScreen(sandboxState: ViewState.Sandbox, onUpdateProject: (UserAction.UpdateProject) -> Unit) {
    val draggingComponent = rememberDraggingComponent()
    val sandboxStateWithoutDraggingComponent = when (val component = draggingComponent.value) {
        null -> sandboxState
        else -> sandboxState.copy(project = sandboxState.project.updatedTree(sandboxState.openedTree.copy(component = sandboxState.openedTree.component.minusChildFromTree(component))))
    }
    val dragDropState = rememberDragDropState(sandboxState, draggingComponent = draggingComponent) { draggingComponent, hoverState ->
        println("dropping ${draggingComponent.stringify(showIDs = true)} over $hoverState")
        val isMoving = sandboxStateWithoutDraggingComponent.openedTree.component.findByIDInTree(draggingComponent.id) != null
        when(hoverState) {
            HoverState.OverNone -> {
                if (!isMoving) return@rememberDragDropState // the component would be added, so deleting it doesn't affect the tree
                onUpdateProject.invoke(UserAction.UpdateProject(sandboxState.project, ProjectAction.TreeAction.DeleteComponent(draggingComponent))) // the component is being moved, so deleting it deletes it from the tree
            }
            is HoverState.OverTreeItem -> {
                val (parent, indexInParent) = sandboxStateWithoutDraggingComponent.openedTree.component.handleDropComponent(hoverState)
                val action = when(isMoving) {
                    true -> ProjectAction.TreeAction.MoveComponent(draggingComponent, parent, indexInParent)
                    false -> ProjectAction.TreeAction.AddComponent(draggingComponent, parent, indexInParent)
                }
                onUpdateProject.invoke(UserAction.UpdateProject(sandboxStateWithoutDraggingComponent.project, action))
            }
        }
    }
    val backdropState = rememberBackdropScaffoldState(initialValue = BackdropValue.Concealed)
    ProjectProvider(project = sandboxState.project) {
        DragDropProvider(dragDropState = dragDropState) {
            BackdropScaffold(
                scaffoldState = backdropState,
                gesturesEnabled = dragDropState.draggingComponent.value == null,
                appBar = {
                    val coroutineScope = rememberCoroutineScope()
                    SandboxAppBar(
                        sandboxState = sandboxState,
                        project = sandboxState.project,
                        iconState = backdropState.targetValue,
                        onToggle = {
                            coroutineScope.launch {
                                if (backdropState.targetValue == BackdropValue.Concealed) backdropState.reveal() else backdropState.conceal()
                            }
                        },
                        onUpdateProject = onUpdateProject
                    )
                },
                backLayerContent = {
                    LaunchedEffect(sandboxState) {
                        println("selected tree = ${sandboxState.openedTree.name}")
                    }
                    SandboxBackdrop(sandboxState, onUpdateProject = { onUpdateProject.invoke(UserAction.UpdateProject(sandboxState.project, it)) })
                },
                frontLayerContent = {
                    val bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
                    val (height, setHeight) = remember { mutableStateOf(0) }
                    BottomSheetScaffold(
                        modifier = Modifier.onGloballyPositioned { setHeight(it.size.height) },
                        scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState),
                        sheetPeekHeight = 88.dp,
                        sheetGesturesEnabled = dragDropState.draggingComponent.value == null,
                        sheetContent = {
                            Drawer(
                                sandboxState = sandboxStateWithoutDraggingComponent,
                                sheetState = bottomSheetState,
                                dragDropState = dragDropState,
                                modifier = Modifier.height(with(LocalDensity.current) { (height / 2).toDp() } + 88.dp),
                                onUpdateProject = { onUpdateProject.invoke(UserAction.UpdateProject(sandboxState.project, it)) },
                                onDrag = { dragDropState.draggingComponent.value = it },
                            )
                        },
                        content = {
                            val offset = if (bottomSheetState.offset.value.isNaN()) 0f else bottomSheetState.offset.value
                            val scale = (offset / height).coerceIn(0f..1f)
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
                                    selected = sandboxState.drawerStack.filterIsInstance<DrawerViewState.EditComponent>().firstOrNull()?.component?.id
                                )
                            }
                        }
                    )
                },
            )
        }
    }
}

private fun PrototypeComponent.handleDropComponent(hoverState: HoverState.OverTreeItem): Pair<PrototypeComponent.Group, Int> = when(hoverState.dropPosition) {
    DropPosition.Above -> this.findParentOfComponent(hoverState.hoveringComponent)!!
    DropPosition.Below -> this.findParentOfComponent(hoverState.hoveringComponent)!!.let { it.copy(second = it.second + 1) }
    DropPosition.Nested.First -> Pair(hoverState.hoveringComponent as PrototypeComponent.Group, 0)
    DropPosition.Nested.Last -> Pair(hoverState.hoveringComponent as PrototypeComponent.Group, hoverState.hoveringComponent.children.size)
}