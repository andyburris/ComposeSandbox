package com.andb.apps.composesandbox.ui.sandbox.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.andb.apps.composesandbox.state.DrawerViewState
import com.andb.apps.composesandbox.state.LocalActionHandler
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.state.ViewState
import com.andb.apps.composesandbox.ui.common.ConfirmationDialog
import com.andb.apps.composesandbox.ui.common.DragDropState
import com.andb.apps.composesandbox.ui.sandbox.drawer.addcomponent.ComponentList
import com.andb.apps.composesandbox.ui.sandbox.drawer.addmodifier.AddModifierList
import com.andb.apps.composesandbox.ui.sandbox.drawer.editmodifier.DrawerEditModifiers
import com.andb.apps.composesandbox.ui.sandbox.drawer.editproperties.DrawerEditProperties
import com.andb.apps.composesandbox.ui.sandbox.drawer.theme.DrawerEditTheme
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.ComponentItem
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.DrawerTree
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.toDpPosition
import com.andb.apps.composesandbox.ui.util.StackSwitcher
import com.andb.apps.composesandbox.ui.util.draggable2D
import com.andb.apps.composesandboxdata.model.*
import com.andb.apps.composesandboxdata.plusElement

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Drawer(
    sandboxState: ViewState.Sandbox,
    sheetState: BottomSheetState,
    dragDropState: DragDropState,
    modifier: Modifier = Modifier,
    onScreenUpdate: (PrototypeTree) -> Unit,
    onThemeUpdate: (Theme) -> Unit,
    onDeleteTree: (PrototypeTree) -> Unit,
    onExtractComponent: (PrototypeComponent) -> Unit,
    onDrag: (draggingComponent: PrototypeComponent?) -> Unit
) {
    val density = LocalDensity.current
    val actionHandler = LocalActionHandler.current
    val (contentSize, setContentSize) = remember { mutableStateOf(Size(0f, 0f)) }
    val dragDropScrolling = if (dragDropState.draggingComponent.value != null) {
        val heightDp = with(density) { contentSize.height.toDp() }
        when (dragDropState.dragPosition.value.y) {
            in 0.dp..112.dp -> DragDropScrolling.ScrollingUp
            in heightDp - 24.dp..heightDp -> DragDropScrolling.ScrollingDown
            else -> DragDropScrolling.None
        }
    } else DragDropScrolling.None
    StackSwitcher(
        stack = sandboxState.drawerStack,
        animateIf = { old, current -> old != null && old::class != current::class },
        modifier = modifier
            .onGloballyPositioned {
                setContentSize(it.size.toSize())
                dragDropState.globalOffset.value = it
                    .positionInWindow()
                    .toDpPosition(density)
            }
            .draggable2D(
                canDrag = dragDropState.draggingComponent.value != null,
                onDrop = { dragDropState.drop() },
                onPositionUpdate = {
                    dragDropState.dragPosition.value = it
                    //println("dragging, value = ${dragDropState.dragPosition.value}")
                }
            )
    ) { switchDrawerState, visibilityProgress, isTop ->
        Box(
            modifier = Modifier.graphicsLayer(
                translationX = (1 - visibilityProgress) * contentSize.width * if (isTop) 1 else -1,
                alpha = visibilityProgress
            )
        ) {
            when (switchDrawerState) {
                is DrawerViewState.Tree -> DrawerTree(
                    sandboxState = sandboxState,
                    sheetState = sheetState,
                    hovering = dragDropState.draggingComponent.value?.let { dragDropState.getDropState() },
                    scrolling = dragDropScrolling,
                    onTreeNameChanged = { onScreenUpdate.invoke(it) },
                    onMoveComponent = { component, pointer ->
                        println("moving ${component.stringify()}, openedTree.component = ${sandboxState.openedTree.component.stringify()}")
                        dragDropState.dragPosition.value = pointer
                        onDrag.invoke(component)
                    },
                    onDeleteTree = onDeleteTree
                )
                DrawerViewState.AddComponent -> ComponentList(project = sandboxState.project, currentTreeID = sandboxState.openedTree.id, requiresLongClick = true) {
                    onDrag.invoke(it)
                    actionHandler.invoke(UserAction.Back)
                }
                is DrawerViewState.EditComponent -> DrawerEditProperties(
                    component = switchDrawerState.component,
                    isBaseComponent = switchDrawerState.component.id == sandboxState.openedTree.component.id,
                    actionHandler = actionHandler,
                    onExtractComponent = onExtractComponent,
                    onUpdate = { updatedComponent ->
                        val updatedTree = sandboxState.openedTree.component.updatedChildInTree(updatedComponent)
                        onScreenUpdate.invoke(sandboxState.openedTree.copy(component = updatedTree))
                    }
                )
                is DrawerViewState.PickBaseComponent -> {
                    ConfirmationDialog { confirmationState ->
                        ComponentList(project = sandboxState.project, title = "Pick Base Component", currentTreeID = sandboxState.openedTree.id, requiresLongClick = false) {
                            val (newBaseComponent, losesChildren) = sandboxState.openedTree.component.replaceParent(it)
                            confirmationState.confirm(title = "Confirm Change?", summary = "This will delete all children of the old component", needToConfirm = losesChildren) {
                                onScreenUpdate.invoke(sandboxState.openedTree.copy(component = newBaseComponent))
                                actionHandler.invoke(UserAction.Back)
                                actionHandler.invoke(UserAction.Back)
                            }
                        }
                    }
                }
                is DrawerViewState.AddModifier -> AddModifierList {
                    val withModifier = sandboxState.editingComponent.copy(modifiers = sandboxState.editingComponent.modifiers.plusElement(it, 0))
                    val updatedTree = sandboxState.openedTree.component.updatedChildInTree(withModifier)
                    onScreenUpdate.invoke(sandboxState.openedTree.copy(component = updatedTree))
                    actionHandler.invoke(UserAction.Back)
                }
                is DrawerViewState.EditModifier -> DrawerEditModifiers(prototypeModifier = switchDrawerState.modifier) {
                    println("edited modifier = $it")
                    val updatedTree = sandboxState.openedTree.component.updatedChildInTree(sandboxState.editingComponent.updatedModifier(it))
                    onScreenUpdate.invoke(sandboxState.openedTree.copy(component = updatedTree))
                }
                is DrawerViewState.EditTheme -> DrawerEditTheme(theme = sandboxState.project.theme) {
                    onThemeUpdate.invoke(it)
                }
            }
            val draggingComponent = dragDropState.draggingComponent.value
            if (draggingComponent != null) {
                ComponentDragDropItem(component = draggingComponent, position = dragDropState.dragPosition.value)
            }
        }
    }
}

enum class DragDropScrolling { ScrollingUp, None, ScrollingDown }

@Composable
private fun ComponentDragDropItem(component: PrototypeComponent, position: DpOffset, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .offset(position.x - 16.dp, position.y - 8.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.background, shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComponentItem(component = component)
        val children = when (component) {
            is PrototypeComponent.Group -> component.children.size
            is PrototypeComponent.Slotted -> component.slots.allSlots().sumBy { it.group.children.size }
            else -> 0
        }
        if (children > 0) {
            Box(modifier = Modifier
                .background(MaterialTheme.colors.primary, shape = CircleShape)
                .size(20.dp)) {
                Text(children.toString(), style = MaterialTheme.typography.overline, color = MaterialTheme.colors.onPrimary, modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

