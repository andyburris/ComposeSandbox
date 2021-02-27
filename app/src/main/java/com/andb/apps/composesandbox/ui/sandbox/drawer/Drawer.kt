package com.andb.apps.composesandbox.ui.sandbox.drawer

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.andb.apps.composesandbox.state.*
import com.andb.apps.composesandbox.state.DrawerState
import com.andb.apps.composesandbox.ui.common.*
import com.andb.apps.composesandbox.ui.sandbox.drawer.addcomponent.ComponentList
import com.andb.apps.composesandbox.ui.sandbox.drawer.addmodifier.AddModifierList
import com.andb.apps.composesandbox.ui.sandbox.drawer.editmodifier.DrawerEditModifiers
import com.andb.apps.composesandbox.ui.sandbox.drawer.editproperties.DrawerEditProperties
import com.andb.apps.composesandbox.ui.sandbox.drawer.theme.DrawerEditTheme
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.ComponentItem
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.DrawerTree
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.toDpPosition
import com.andb.apps.composesandbox.ui.util.StackItemTransitionState
import com.andb.apps.composesandbox.ui.util.StackSwitcher
import com.andb.apps.composesandbox.ui.util.draggable2D
import com.andb.apps.composesandboxdata.model.*
import com.andb.apps.composesandboxdata.plusElement

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Drawer(
    sandboxState: ViewState.Sandbox,
    sheetState: BottomSheetState,
    modifier: Modifier = Modifier,
    onProjectUpdate: (UserAction.UpdateProject) -> Unit,
    onDragUpdate: (dragging: Boolean) -> Unit,
    onUndo: () -> Unit
) {
    val density = AmbientDensity.current
    val actionHandler = ActionHandlerAmbient.current
    val movingComponent = remember { mutableStateOf<PrototypeComponent?>(null) }
    val (contentSize, setContentSize) = remember { mutableStateOf(Size(0f, 0f)) }
    val drawerState = sandboxState.drawerStack.last()
    val dragPosition = remember { mutableStateOf(DpOffset(0.dp, 0.dp)) } //remember separately so it isn't reset on the input change of dragDropState
    val dragDropState = remember(sandboxState.openedTree, drawerState) {
        DragDropState(dragPosition, mutableStateOf(DpOffset(0.dp, 0.dp)), mutableListOf()) { dropState ->
            when (dropState) {
                is DropState.OverTreeItem -> {
                    val moving = movingComponent.value ?: return@DragDropState
                    val updatedTree = when (dropState.dropPosition) {
                        is DropPosition.NESTED.First -> sandboxState.openedTree.component.plusChildInTree(moving, dropState.hoveringComponent as PrototypeComponent.Group, 0)
                        is DropPosition.NESTED.Last -> sandboxState.openedTree.component.plusChildInTree(moving, dropState.hoveringComponent as PrototypeComponent.Group, dropState.hoveringComponent.children.size)
                        else -> {
                            val (parent, index) = sandboxState.openedTree.component.findParentOfComponent(dropState.hoveringComponent)!!
                            println("not nesting, parent = $parent, index = $index")
                            when (dropState.dropPosition) {
                                DropPosition.ABOVE -> sandboxState.openedTree.component.plusChildInTree(moving, parent, index)
                                DropPosition.BELOW -> sandboxState.openedTree.component.plusChildInTree(moving, parent, index + 1)
                                else -> throw Error("will never reach here")
                            }
                        }
                    }
                    println("updated tree = $updatedTree")
                    movingComponent.value = null
                    onDragUpdate.invoke(false)
                    val project = sandboxState.project.updatedTree(sandboxState.openedTree.copy(component = updatedTree))
                    onProjectUpdate.invoke(UserAction.UpdateProject(project))
                }
                is DropState.OverNone -> {
                    movingComponent.value = null
                    onProjectUpdate.invoke(UserAction.UpdateProject(sandboxState.project)) //make the delete undoable
                    onDragUpdate.invoke(false)
                }
            }
        }
    }
    DragDropProvider(dragDropState = dragDropState) {
        val oldStack = remember { mutableStateOf(sandboxState.drawerStack) }
        val dragDropScrolling = if (movingComponent.value != null) {
            val heightDp = with(density) { contentSize.height.toDp() }
            when(dragDropState.dragPosition.value.y) {
                in 0.dp..112.dp -> DragDropScrolling.ScrollingUp
                in heightDp - 24.dp..heightDp -> DragDropScrolling.ScrollingDown
                else -> DragDropScrolling.None
            }
        } else DragDropScrolling.None
        StackSwitcher(
            stack = sandboxState.drawerStack,
            animateIf = { old, current -> old != null && old::class != current::class },
            modifier = modifier
                .draggable2D(
                    dragDropState.dragPosition.value,
                    onDrop = { dragDropState.drop() },
                    canDrag = { drawerState is DrawerState.Tree && movingComponent.value != null }
                ) {
                    dragDropState.dragPosition.value = it
                }
                .onGloballyPositioned {
                    setContentSize(it.size.toSize())
                    dragDropState.globalOffset.value = it.positionInWindow().toDpPosition(density)
                }
        ) { switchDrawerState, transitionState, visibilityProgress ->
            val onLeft = when (transitionState) {
                StackItemTransitionState.Revealing, StackItemTransitionState.Hiding -> true // always at bottom of stack
                StackItemTransitionState.Adding, StackItemTransitionState.Removing -> false // always at top of stack
                else -> true //doesn't matter since visibilityProgress will be 1
            }
            Box(
                modifier = Modifier.graphicsLayer(
                    translationX = (1 - visibilityProgress) * contentSize.width * if (onLeft) -1 else 1,
                    alpha = visibilityProgress
                )
            ) {
                when (switchDrawerState) {
                    is DrawerState.Tree -> DrawerTree(
                        sandboxState = sandboxState,
                        sheetState = sheetState,
                        hovering = movingComponent.value?.let { dragDropState.getDropState() },
                        scrolling = dragDropScrolling,
                        onTreeNameChanged = {
                            onProjectUpdate.invoke(UserAction.UpdateProject(sandboxState.project.updatedTree(tree = it)))
                        },
                        onMoveComponent = {
                            val updatedBaseComponent = sandboxState.openedTree.component.minusChildFromTree(it)
                            val updatedTree = sandboxState.openedTree.copy(component = updatedBaseComponent)
                            onDragUpdate.invoke(true)
                            onProjectUpdate.invoke(UserAction.UpdateProject(sandboxState.project.updatedTree(updatedTree), addToHistory = false))
                            movingComponent.value = it
                        },
                        onDeleteTree = {
                            val updatedProject = sandboxState.project.removeTree(it)
                            actionHandler.invoke(UserAction.UpdateSandbox((sandboxState.toScreen() as Screen.Sandbox).copy(openedTreeID = updatedProject.trees.first().id)))
                            onProjectUpdate.invoke(UserAction.UpdateProject(updatedProject))
                            actionHandler.invoke(UserAction.Back)
                        },
                        onUndo = onUndo
                    )
                    DrawerState.AddComponent -> ComponentList(project = sandboxState.project, currentTreeID = sandboxState.openedTree.id, requiresLongClick = true) {
                        movingComponent.value = it
                        onDragUpdate.invoke(true)
                        actionHandler.invoke(UserAction.Back)
                    }
                    is DrawerState.EditComponent -> DrawerEditProperties(
                        switchDrawerState.component,
                        isBaseComponent = switchDrawerState.component.id == sandboxState.openedTree.component.id,
                        actionHandler,
                        onExtractComponent = { oldComponent ->
                            onProjectUpdate.invoke(UserAction.UpdateProject(sandboxState.project.extractComponent(oldComponent)))
                        },
                        onUpdate = { updatedComponent ->
                            val updatedTree = sandboxState.openedTree.updatedChildComponent(updatedComponent)
                            onProjectUpdate.invoke(UserAction.UpdateProject(sandboxState.project.updatedTree(updatedTree)))
                        }
                    )
                    is DrawerState.PickBaseComponent -> {
                        ConfirmationDialog { confirmationState ->
                            ComponentList(project = sandboxState.project, title = "Pick Base Component", currentTreeID = sandboxState.openedTree.id, requiresLongClick = false) {
                                val (newBaseComponent, losesChildren) = sandboxState.openedTree.component.replaceParent(it)
                                confirmationState.confirm(title = "Confirm Change?", summary = "This will delete all children of the old component", needToConfirm = losesChildren) {
                                    val updatedTree = sandboxState.openedTree.copy(component = newBaseComponent)
                                    onProjectUpdate.invoke(UserAction.UpdateProject(sandboxState.project.updatedTree(updatedTree)))
                                    actionHandler.invoke(UserAction.Back)
                                }
                            }
                        }
                    }
                    is DrawerState.AddModifier -> AddModifierList {
                        val withModifier = sandboxState.editingComponent.copy(modifiers = sandboxState.editingComponent.modifiers.plusElement(it, 0))
                        val updatedTree = sandboxState.openedTree.updatedChildComponent(withModifier)
                        onProjectUpdate.invoke(UserAction.UpdateProject(sandboxState.project.updatedTree(updatedTree)))
                        actionHandler.invoke(UserAction.Back)
                    }
                    is DrawerState.EditModifier -> DrawerEditModifiers(prototypeModifier = switchDrawerState.modifier) {
                        println("edited modifier = $it")
                        val updatedTree = sandboxState.openedTree.updatedChildComponent(sandboxState.editingComponent.updatedModifier(it))
                        onProjectUpdate.invoke(UserAction.UpdateProject(sandboxState.project.updatedTree(updatedTree)))
                    }
                    is DrawerState.EditTheme -> DrawerEditTheme(theme = sandboxState.project.theme) {
                        val project = sandboxState.project.updatedTheme(it)
                        onProjectUpdate.invoke(UserAction.UpdateProject(project))
                    }
                }
                val currentMovingComponent = movingComponent.value
                if (currentMovingComponent != null) {
                    ComponentDragDropItem(component = currentMovingComponent, position = dragDropState.dragPosition.value)
                }
            }
        }
        oldStack.value = sandboxState.drawerStack
    }
}

enum class DragDropScrolling { ScrollingUp, None, ScrollingDown }

@Composable
private fun ComponentDragDropItem(component: PrototypeComponent, position: DpOffset) {
    ComponentItem(
        component = component,
        modifier = Modifier
            .offset(position.x, position.y)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.background, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    )
}