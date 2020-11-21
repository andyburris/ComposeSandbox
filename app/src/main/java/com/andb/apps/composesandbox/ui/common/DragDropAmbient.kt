package com.andb.apps.composesandbox.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Providers
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Position
import com.andb.apps.composesandbox.model.Properties
import com.andb.apps.composesandbox.model.PrototypeComponent


data class DragDropState(val dragPosition: MutableState<Position>, val globalPosition: MutableState<Position>, val treeItems: MutableList<TreeHoverItem>, val onDrop: (DropState)->Unit) {
    fun updateTreeItem(item: TreeHoverItem) {
        treeItems.removeAll { it.component.id == item.component.id }
        treeItems.add(item)
    }
    private fun hoveringTreeItem() = treeItems
        .map { it.copy(position = it.position.copy(y = it.position.y - globalPosition.value.y)) }
        .sortedByDescending { it.indent } // create drop priority by largest indent (most nested child takes priority)
        .find { it.isHovering(dragPosition.value) }
    fun drop() {
        val hoveringTreeItem = hoveringTreeItem()
        println("hoveringTreeItem = $hoveringTreeItem")
        if (hoveringTreeItem == null) {
            onDrop.invoke(DropState.OverNone)
            return
        }
        val dropAbove: Boolean = dragPosition.value.y < (hoveringTreeItem.position.y + hoveringTreeItem.height / 2) && hoveringTreeItem.indent > 0
        val dropState = DropState.OverTreeItem(hoveringTreeItem.component, dropAbove)
        onDrop.invoke(dropState)
    }
    fun getHoverState(draggingComponent: PrototypeComponent): HoverState? {
        val hoveringTreeItem = hoveringTreeItem() ?: return HoverState.OverNone(draggingComponent)
        println("hoveringTreeItem = $hoveringTreeItem, allTreeItems = $treeItems")
        val hoverInTopHalf = dragPosition.value.y < (hoveringTreeItem.position.y + hoveringTreeItem.height / 2) && hoveringTreeItem.indent > 0
        val dropIndicatorPosition = when {
            hoverInTopHalf -> hoveringTreeItem.position
            else -> hoveringTreeItem.position.copy(y = hoveringTreeItem.position.y + hoveringTreeItem.height)
        }
        val indent = hoveringTreeItem.indent + if (hoveringTreeItem.component.properties is Properties.Group && (hoveringTreeItem.component.properties as Properties.Group).children.isEmpty() && !hoverInTopHalf) 1 else 0
        return HoverState.OverTreeItem(draggingComponent, dropIndicatorPosition.y, indent)
    }
}
val DragDropAmbient = staticAmbientOf<DragDropState>()


@Composable
fun DragDropProvider(dragDropState: DragDropState, content: @Composable() () -> Unit){
    Providers(DragDropAmbient provides dragDropState) {
        content()
    }
}

sealed class DropState {
    data class OverTreeItem(val hoveringComponent: PrototypeComponent, val dropAbove: Boolean) : DropState()
    object OverNone : DropState()
}
sealed class HoverState(open val draggingComponent: PrototypeComponent) {
    data class OverTreeItem(override val draggingComponent: PrototypeComponent, val dropIndicatorPosition: Dp, val indent: Int) : HoverState(draggingComponent)
    data class OverNone(override val draggingComponent: PrototypeComponent) : HoverState(draggingComponent)
}
data class TreeHoverItem(val component: PrototypeComponent, val position: Position, val height: Dp, val indent: Int) {
    fun isHovering(hoverPosition: Position): Boolean = hoverPosition.y in (position.y)..(position.y + height)
}

// create new dragdropstate on each tree update (using statefor)
// update treeItems each redraw?
// create drop priority by largest indent (most nested child takes priority) .sortedByDescending { it.indent }

/*

val (treePositions, setTreePositions) = remember { mutableStateOf<List<TreeHoverItem>>(emptyList()) }
TreeItem(component = parent, modifier){ newPositions ->
        setTreePositions(newPositions.map { it.copy(position = it.position - globalPositionOffset) })
        //println("new tree positions (size = ${treePositions.size}) = $treePositions")
}
if (movingPosition != null) {
    val treeTop = treePositions.maxOfOrNull { it.position.y } ?: 0.dp
    val treeBottom = treePositions.maxOfOrNull { it.position.y + it.height } ?: 0.dp
    val above = movingPosition.y < treeTop
    val below = movingPosition.y > treeBottom
    val hovering = treePositions.find { it.isHovering(movingPosition) }
    //println("finding hover at $movingPosition, hover positions = ${treePositions.map { it.position.y..(it.position.y + it.height) }} hovering = $hovering")
    when {
        /*below -> {
            val indent = (movingPosition.x / 40.dp).toInt().coerceAtMost(treePositions.maxByOrNull { it.position.y }?.indent ?: 0)
            val hoverState = HoverState(treeBottom, indent)
            onMove.invoke(hoverState)
        } TODO: decide whether multiple top-levels can be used in a tree, include this code if yes */
        hovering != null -> {
            //val parent = treePositions.maxByOrNull { it.position.y < hovering.position.y && it.indent == hovering.indent - 1 }
            onMove.invoke(hovering.getHoverState(movingPosition))
        }
    }
}
 */