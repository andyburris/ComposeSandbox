package com.andb.apps.composesandbox.ui.sandbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SandboxScreen(sandboxState: ViewState.Sandbox, onUpdateProject: (Project) -> Unit) {
    val actionHandler = Handler
    println("updating sandbox state, sandboxState.openedTree.component = ${sandboxState.openedTree.component.stringify()}")
    val dragDropState = rememberDragDropState(sandboxState) { draggingComponent, dropState ->
        println("dropping, sandboxState.openedTree.component = ${sandboxState.openedTree.component.stringify()}")
        val updatedComponent = sandboxState.openedTree.component.handleDropComponent(draggingComponent, dropState)
        val updatedTree = sandboxState.openedTree.copy(component = updatedComponent)
        println("updatedComponent = ${updatedComponent.stringify()}")
        onUpdateProject.invoke(sandboxState.project.updatedTree(updatedTree))
        println("after drop, openedTree.component = ${sandboxState.openedTree.component.stringify()}")
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
                        iconState = backdropState.currentValue,
                        onToggle = {
                            coroutineScope.launch {
                                if (backdropState.currentValue == BackdropValue.Concealed) backdropState.reveal() else backdropState.conceal()
                            }
                        }
                    )
                },
                backLayerContent = {
                    SandboxBackdrop(sandboxState) {
                        onUpdateProject.invoke(it)
                    }
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
                            val sandboxStateWithoutDraggingComponent = when (val component = dragDropState.draggingComponent.value) {
                                null -> sandboxState
                                else -> sandboxState.copy(project = sandboxState.project.updatedTree(sandboxState.openedTree.copy(component = sandboxState.openedTree.component.minusChildFromTree(component))))
                            }
                            Drawer(
                                sandboxState = sandboxStateWithoutDraggingComponent,
                                sheetState = bottomSheetState,
                                dragDropState = dragDropState,
                                modifier = Modifier.height(with(LocalDensity.current) { (height / 2).toDp() } + 88.dp),
                                onScreenUpdate = { onUpdateProject.invoke(sandboxState.project.updatedTree(it)) },
                                onThemeUpdate = { onUpdateProject.invoke(sandboxState.project.copy(theme = it)) },
                                onExtractComponent = { oldComponent ->
                                    val customTree = PrototypeTree(name = sandboxState.project.nextComponentName(), treeType = TreeType.Component, component = oldComponent)
                                    val customComponent = PrototypeComponent.Custom(treeID = customTree.id)
                                    val editedTrees = sandboxState.project.trees.map { it.copy(component = it.component.replaceWithCustom(oldComponent.id, customComponent)) }
                                    onUpdateProject.invoke(sandboxState.project.copy(trees = editedTrees + customTree))
                                },
                                onDrag = { dragDropState.draggingComponent.value = it },
                                onDeleteTree = {
                                    val newTrees = sandboxState.project.trees.filter { it.id != sandboxState.openedTree.id }.map {
                                        it.copy(component = it.component.replaceCustomWith(sandboxState.openedTree.id, sandboxState.openedTree.component))
                                    }
                                    actionHandler.invoke(UserAction.UpdateSandbox((sandboxState.toScreen() as Screen.Sandbox).copy(openedTreeID = newTrees.first().id)))
                                    onUpdateProject.invoke(sandboxState.project.copy(trees = newTrees))
                                }
                            )
                        },
                        content = {
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

private fun PrototypeComponent.handleDropComponent(droppingComponent: PrototypeComponent, hoverState: HoverState): PrototypeComponent {
    return when (hoverState) {
        is HoverState.OverTreeItem -> {
            if (droppingComponent == hoverState.hoveringComponent) return this
            val updatedTree = when (hoverState.dropPosition) {
                is DropPosition.Nested.First -> this.minusChildFromTree(droppingComponent).plusChildInTree(droppingComponent, hoverState.hoveringComponent as PrototypeComponent.Group, 0)
                is DropPosition.Nested.Last -> this.minusChildFromTree(droppingComponent).plusChildInTree(droppingComponent, hoverState.hoveringComponent as PrototypeComponent.Group, hoverState.hoveringComponent.children.size)
                else -> {
                    val (parent, index) = this.findParentOfComponent(hoverState.hoveringComponent)!!
                    println("not nesting, parent = $parent, index = $index")
                    when (hoverState.dropPosition) {
                        DropPosition.Above -> this.minusChildFromTree(droppingComponent).plusChildInTree(droppingComponent, parent, index)
                        DropPosition.Below -> this.minusChildFromTree(droppingComponent).plusChildInTree(droppingComponent, parent, index + 1)
                        else -> throw Error("will never reach here")
                    }
                }
            }
            println("updated tree = ${updatedTree.stringify()}")
            return updatedTree
        }
        is HoverState.OverNone -> this.minusChildFromTree(droppingComponent)
    }
}