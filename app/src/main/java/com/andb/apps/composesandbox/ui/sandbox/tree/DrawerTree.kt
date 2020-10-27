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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawShadow
import androidx.compose.ui.drawLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.dragGestureFilter
import androidx.compose.ui.gesture.rawPressStartGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.globalPosition
import androidx.compose.ui.onPositioned
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.PrototypeComponent
import com.andb.apps.composesandbox.data.model.plusChildInTree
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.BottomSheetState
import com.andb.apps.composesandbox.ui.common.BottomSheetValue

/**
 * Tree representing prototype components. Holds drag-and-drop logic currently. Uses [GenericTree] under the hood
 * @param opened the top-level component opened in the editor
 * @param sheetState the state of the bottom sheet, used to calculate the global position of each [TreeItem]
 * @param moving the component currently in the drag state of drag-and-drop. null if no component is currently being dragged
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawerTree(opened: PrototypeComponent, sheetState: BottomSheetState, moving: PrototypeComponent? = null, onTreeUpdate: (PrototypeComponent) -> Unit) {
    val actionHandler = ActionHandlerAmbient.current
    val density = DensityAmbient.current

    val dragPosition = remember { mutableStateOf(Position(0.dp, 0.dp)) }
    val globalPositionOffset = remember { mutableStateOf(Position(0.dp, 0.dp)) }
    val (hoverState, setHoverState) = remember { mutableStateOf<HoverState?>(null) }
    val onRelease = {
        hoverState?.run {
            // hovering item = tree item that finger is above
            // if drop position is above hovering item, add it to the same indent right above hovering item
            val updated = opened.plusChildInTree(moving!!, this.hoveringComponent, this.dropAbove)
            onTreeUpdate(updated)
        }
    }
    Column(
        modifier = Modifier
            .rawPressStartGestureFilter(onPressStart = { pointer ->
                dragPosition.value = pointer.toDpPosition(density)
            })
            .dragGestureFilter(
                dragObserver = object : DragObserver {
                    override fun onStart(downPosition: Offset) { dragPosition.value = downPosition.toDpPosition(density) }
                    override fun onDrag(dragDistance: Offset): Offset {
                        dragPosition.value = dragPosition.value + dragDistance.toDpPosition(density)
                        return dragDistance
                    }
                    override fun onStop(velocity: Offset) { onRelease.invoke() }
                    override fun onCancel() { onRelease.invoke() }
                },
                canDrag = { moving != null},
                startDragImmediately = false
            ).onPositioned {
                globalPositionOffset.value = it.globalPosition.toDpPosition(density)
            }
    ) {
        DrawerTreeHeader(opened, sheetState)
        Tree(
            parent = opened,
            modifier = Modifier.padding(start = 32.dp, end = 32.dp),
            globalPositionOffset = globalPositionOffset.value,
            movingPosition = moving?.let { dragPosition.value },
            onMove = { hoverState ->
                setHoverState(hoverState)
            }
        )
    }
    if (moving != null) {
        ComponentDragDropItem(component = moving, position = dragPosition.value)
        if (hoverState != null) {
            println("hoverstate padding = ${hoverState.dropIndicatorPosition}")
            DropIndicator(hoverState)
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
private fun DropIndicator(hoverState: HoverState) {
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