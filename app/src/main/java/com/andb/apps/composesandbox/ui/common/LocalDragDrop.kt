package com.andb.apps.composesandbox.ui.common

import androidx.compose.runtime.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandboxdata.model.PrototypeComponent

@Composable
fun rememberDraggingComponent() = remember { mutableStateOf<PrototypeComponent?>(null) }
@Composable
fun rememberDragPosition() = remember { mutableStateOf(DpOffset(0.dp, 0.dp)) }
@Composable
fun rememberGlobalOffset() = remember { mutableStateOf(DpOffset(0.dp, 0.dp)) }

@Composable
fun rememberDragDropState(
    vararg key: Any,
    draggingComponent: MutableState<PrototypeComponent?> = rememberDraggingComponent(),
    dragPosition: MutableState<DpOffset> = rememberDragPosition(),
    globalOffset: MutableState<DpOffset> = rememberGlobalOffset(),
    treeItems: MutableList<TreeHoverItem> = mutableListOf(),
    onDrop: (dragging: PrototypeComponent, HoverState) -> Unit
) = remember(key) { DragDropState(draggingComponent, dragPosition, globalOffset, treeItems, onDrop) }

data class DragDropState(
    val draggingComponent: MutableState<PrototypeComponent?>,
    val dragPosition: MutableState<DpOffset>,
    val globalOffset: MutableState<DpOffset>,
    val treeItems: MutableList<TreeHoverItem>,
    val onDrop: (dragging: PrototypeComponent, HoverState) -> Unit
) {
    fun updateTreeItem(item: TreeHoverItem) {
        removeTreeItem(item.component.id)
        treeItems.add(item)
    }

    fun removeTreeItem(id: String) {
        treeItems.removeAll { it.component.id == id }
    }

    private fun hoveringOverItem(): TreeHoverItem? {
        val dragPosition = dragPosition.value
        val treeItemsWithGlobalOffset = treeItems.map { it.copy(position = it.position.copy(y = it.position.y - globalOffset.value.y)) }
        val dragIsBelowTree = dragPosition.y > treeItemsWithGlobalOffset.maxOfOrNull { it.position.y + it.height } ?: 0.dp
        return if (dragIsBelowTree) {
            val indent = (dragPosition.x.value / 40).toInt().coerceIn(0, treeItemsWithGlobalOffset.maxOfOrNull { it.indent } ?: 0)
            treeItemsWithGlobalOffset.filter { it.indent <= indent }.maxByOrNull { it.position.y + it.height }
        } else {
            treeItemsWithGlobalOffset
                .sortedByDescending { it.indent } // create drop priority by largest indent (most nested child takes priority)
                .find { it.isHovering(dragPosition) }
        }
    }

    fun drop() {
        when (val draggingComponentStatic = draggingComponent.value) {
            null -> throw Error("draggingComponent.value must not be null to drop")
            else -> onDrop.invoke(draggingComponentStatic, getDropState())
        }
        draggingComponent.value = null
    }

    fun getDropState(): HoverState {
        val treeItemsWithGlobalOffset = treeItems.map { it.copy(position = it.position.copy(y = it.position.y - globalOffset.value.y)) }
        val dragPosition = dragPosition.value
        val hoveringItem = hoveringOverItem() ?: return HoverState.OverNone
        val hoverDropPosition = hoveringItem.getDropPosition(dragPosition)

        // Correct for scenarios where a drop can't happen, namely when
        // the hovering either will nest directly in a scaffold (which redirects to nesting in its first slot)
        // or the hovering is Above or Below a TreeHoverItem where canDropAround is false (which redirects to either deleting or nesting at the First/Last position)
        val (droppingItem, dropPosition) = when (hoverDropPosition) {
            DropPosition.Above, DropPosition.Below -> when {
                hoveringItem.canDropAround -> Pair(hoveringItem, hoverDropPosition)
                else -> when (hoveringItem.component) {
                    is PrototypeComponent.Slotted -> Pair(null, hoverDropPosition)
                    is PrototypeComponent.Group -> Pair(hoveringItem, if (hoverDropPosition == DropPosition.Above) DropPosition.Nested.First else DropPosition.Nested.Last)
                    else -> return HoverState.OverNone // if a non-group or slotted component is top level, all other component drops are invalid
                }
            }
            is DropPosition.Nested -> when (hoveringItem.component) {
                is PrototypeComponent.Slotted -> Pair(treeItemsWithGlobalOffset.first {
                    val firstTree = hoveringItem.component.slots.enabledSlots().first().group
                    it.component == firstTree
                }, hoverDropPosition)
                is PrototypeComponent.Group -> Pair(hoveringItem, hoverDropPosition)
                else -> throw Error("Components that are not Group or Slotted can't have things nested in them")
            }
        }
        droppingItem ?: return HoverState.OverNone
        return HoverState.OverTreeItem(droppingItem.component, dropPosition)
    }
}

val LocalDragDrop = compositionLocalOf<DragDropState> { error("No DragDropState provided") }


@Composable
fun DragDropProvider(dragDropState: DragDropState, content: @Composable() () -> Unit) {
    CompositionLocalProvider(LocalDragDrop provides dragDropState) {
        content()
    }
}

sealed class HoverState {
    data class OverTreeItem(val hoveringComponent: PrototypeComponent, val dropPosition: DropPosition) : HoverState()
    object OverNone : HoverState()
}

sealed class DropPosition {
    object Above : DropPosition()
    sealed class Nested : DropPosition() {
        object First : Nested()
        object Last : Nested()
    }

    object Below : DropPosition()
}

data class TreeHoverItem(val component: PrototypeComponent, val position: DpOffset, val height: Dp, val indent: Int, val canDropAround: Boolean) {
    fun isHovering(hoverPosition: DpOffset): Boolean =
        hoverPosition.y in (position.y)..(position.y + height)

    fun getDropPosition(hoverPosition: DpOffset) = when (hoverPosition.y) {
        in (position.y)..(position.y + height / 2) -> DropPosition.Above
        in (position.y + height / 2)..(position.y + height) -> when (component) {
            is PrototypeComponent.Group, is PrototypeComponent.Slotted -> DropPosition.Nested.First
            else -> DropPosition.Below
        }
        else -> when {
            component is PrototypeComponent.Group && (hoverPosition.x >= position.x + 40.dp) -> DropPosition.Nested.Last
            else -> DropPosition.Below
        }
    }
}