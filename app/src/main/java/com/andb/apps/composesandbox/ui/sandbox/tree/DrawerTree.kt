package com.andb.apps.composesandbox.ui.sandbox.tree

import androidx.compose.animation.animate
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawShadow
import androidx.compose.ui.drawLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.PrototypeComponent
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.BottomSheetState
import com.andb.apps.composesandbox.ui.common.BottomSheetValue
import com.andb.apps.composesandbox.ui.common.DragDropAmbient
import com.andb.apps.composesandbox.ui.common.HoverState

data class MovingState(val component: PrototypeComponent, val position: Position)

/**
 * Tree representing prototype components. Holds drag-and-drop logic currently. Uses [GenericTree] under the hood
 * @param opened the top-level component opened in the editor
 * @param sheetState the state of the bottom sheet, used to calculate the global position of each [TreeItem]
 * @param hovering the component currently in the drag state of drag-and-drop. null if no component is currently being dragged
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawerTree(opened: PrototypeComponent, sheetState: BottomSheetState, hovering: HoverState?) {
    val actionHandler = ActionHandlerAmbient.current
    val density = DensityAmbient.current

    val dragPosition = DragDropAmbient.current.dragPosition
    Column(
        modifier = Modifier
    ) {
        DrawerTreeHeader(opened, sheetState)
        Tree(
            parent = opened,
            modifier = Modifier.padding(start = 32.dp, end = 32.dp)
        )
    }
    if (hovering != null) {
        ComponentDragDropItem(component = hovering.draggingComponent, position = dragPosition.value)
        if (hovering is HoverState.OverTreeItem) {
            println("hoverstate padding = ${hovering.dropIndicatorPosition}")
            DropIndicator(hovering)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DrawerTreeHeader(opened: PrototypeComponent, sheetState: BottomSheetState) {
    val actionHandler = ActionHandlerAmbient.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(32.dp).fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val iconRotation = animate(target = if (sheetState.targetValue != BottomSheetValue.Peek) 180f else 0f)
            Icon(
                asset = Icons.Default.KeyboardArrowUp,
                modifier = Modifier
                    .clickable { if (sheetState.isPeek) sheetState.open() else sheetState.peek() }
                    .drawLayer(rotationZ = iconRotation)
            )
            Text(
                text = opened.name,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Icon(asset = Icons.Default.Add, modifier = Modifier.clickable { actionHandler.invoke(UserAction.OpenComponentList) })
    }
}

@Composable
private fun ComponentDragDropItem(component: PrototypeComponent, position: Position) {
    ComponentItem(
        component = component,
        modifier = Modifier
            .offset(position.x, position.y)
            .drawShadow(4.dp, RoundedCornerShape(8.dp))
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    )
}

@Composable
private fun DropIndicator(hoverState: HoverState.OverTreeItem) {
    val position = hoverState.dropIndicatorPosition
    Row(modifier = Modifier.padding(start = 24.dp, top = position).fillMaxWidth()) {
        repeat(hoverState.indent) {
            Box(
                Modifier.size(40.dp, 2.dp).padding(end = 2.dp),
                backgroundColor = MaterialTheme.colors.primary.copy(alpha = .5f)
            )
        }
        Box(
            Modifier.height(2.dp).weight(1f),
            backgroundColor = MaterialTheme.colors.primary
        )
    }
}

fun Offset.toDpPosition(density: Density) = with(density) { Position(this@toDpPosition.x.toDp(), this@toDpPosition.y.toDp()) }