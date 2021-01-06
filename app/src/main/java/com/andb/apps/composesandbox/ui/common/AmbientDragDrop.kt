package com.andb.apps.composesandbox.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Providers
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.model.PrototypeComponent


data class DragDropState(val dragPosition: MutableState<Position>, val globalOffset: MutableState<Position>, val treeItems: MutableList<TreeHoverItem>, val onDrop: (DropState)->Unit) {
    fun updateTreeItem(item: TreeHoverItem) {
        treeItems.removeAll { it.component.id == item.component.id }
        treeItems.add(item)
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
        onDrop.invoke(getDropState())
    }

    fun getDropState(): DropState {
        val treeItemsWithGlobalOffset = treeItems.map { it.copy(position = it.position.copy(y = it.position.y - globalOffset.value.y)) }
        val dragPosition = dragPosition.value
        val hoveringItem = hoveringOverItem() ?: return DropState.OverNone
        val hoverDropPosition = hoveringItem.getDropPosition(dragPosition)

        // Correct for scenarios where a drop can't happen, namely when
        // the hovering either will nest directly in a scaffold (which redirects to nesting in its first slot)
        // or the hovering is Above or Below a TreeHoverItem where canDropAround is false (which redirects to either deleting or nesting at the First/Last position)
        val (droppingItem, dropPosition) = when (hoverDropPosition) {
            DropPosition.ABOVE, DropPosition.BELOW -> when {
                hoveringItem.canDropAround -> Pair(hoveringItem, hoverDropPosition)
                else -> when (hoveringItem.component) {
                    is PrototypeComponent.Slotted -> Pair(null, hoverDropPosition)
                    is PrototypeComponent.Group -> Pair(hoveringItem, if (hoverDropPosition == DropPosition.ABOVE) DropPosition.NESTED.First else DropPosition.NESTED.Last)
                    else -> throw Error("Components that are not Group or Slotted should always be able to be dropped around")
                }
            }
            is DropPosition.NESTED -> when (hoveringItem.component) {
                is PrototypeComponent.Slotted -> Pair(treeItemsWithGlobalOffset.first {
                    val firstTree = hoveringItem.component.slots.first{ hoveringItem.component.properties.slotsEnabled[it.name] != false }.tree
                    it.component == firstTree
                }, hoverDropPosition)
                is PrototypeComponent.Group -> Pair(hoveringItem, hoverDropPosition)
                else -> throw Error("Components that are not Group or Slotted can't have things nested in them")
            }
        }
        droppingItem ?: return DropState.OverNone
        val indicatorState = IndicatorState(
            position = when (dropPosition) {
                is DropPosition.ABOVE -> droppingItem.position.y
                is DropPosition.NESTED.First -> droppingItem.position.y + droppingItem.height
                is DropPosition.BELOW, DropPosition.NESTED.Last -> droppingItem.heightWithChildren(treeItemsWithGlobalOffset)
            },
            indent = if (dropPosition is DropPosition.NESTED) droppingItem.indent + 1 else droppingItem.indent
        )
        return DropState.OverTreeItem(droppingItem.component, dropPosition, indicatorState)
    }
}
val AmbientDragDrop = staticAmbientOf<DragDropState>()


@Composable
fun DragDropProvider(dragDropState: DragDropState, content: @Composable() () -> Unit){
    Providers(AmbientDragDrop provides dragDropState) {
        content()
    }
}

sealed class DropState {
    data class OverTreeItem(val hoveringComponent: PrototypeComponent, val dropPosition: DropPosition, val indicatorState: IndicatorState) : DropState()
    object OverNone : DropState()
}
sealed class DropPosition {
    object ABOVE : DropPosition()
    sealed class NESTED : DropPosition() {
        object First : NESTED()
        object Last : NESTED()
    }
    object BELOW : DropPosition()
}
data class IndicatorState(val position: Dp, val indent: Int)
data class TreeHoverItem(val component: PrototypeComponent, val position: Position, val height: Dp, val indent: Int, val canDropAround: Boolean) {
    fun isHovering(hoverPosition: Position): Boolean = hoverPosition.y in (position.y)..(position.y + height)
    fun getDropPosition(hoverPosition: Position) = when {
        hoverPosition.y in (position.y)..(position.y + height/2) -> DropPosition.ABOVE
        hoverPosition.y in (position.y + height/2)..(position.y + height) -> when (component) {
            is PrototypeComponent.Group, is PrototypeComponent.Slotted -> DropPosition.NESTED.First
            else -> DropPosition.BELOW
        }
        else -> when {
            component is PrototypeComponent.Group && (hoverPosition.x >= position.x + 40.dp) -> DropPosition.NESTED.Last
            else -> DropPosition.BELOW
        }
    }
    fun heightWithChildren(treeItems: List<TreeHoverItem>): Dp = when (component) {
        // if droppingItem is a Group, dropIndicator should be at the bottom of its last child
        is PrototypeComponent.Group -> treeItems.find { it.component == component.children.lastOrNull() }?.heightWithChildren(treeItems) ?: position.y + height
        // if droppingItem is a Slotted, dropIndicator should be at the bottom of its last slot's last child
        is PrototypeComponent.Slotted -> treeItems.find { it.component == (component.slots.lastOrNull()?.tree as PrototypeComponent.Group).children.lastOrNull() }?.heightWithChildren(treeItems) ?: position.y + height
        else -> position.y + height
    }
}