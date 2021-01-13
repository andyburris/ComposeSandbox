package com.andb.apps.composesandbox.ui.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.Direction
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.dragGestureFilter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.Position
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.toDpPosition

fun Modifier.draggable2D(
    currentPosition: Position,
    onDrop: (Position) -> Unit,
    canDrag: ((Direction) -> Boolean)? = null,
    startDragImmediately: Boolean = false,
    onPositionUpdate: (Position) -> Unit
) : Modifier = composed {
    val density = AmbientDensity.current
    Modifier.dragGestureFilter(
        dragObserver = object : DragObserver {
            override fun onDrag(dragDistance: Offset): Offset {
                onPositionUpdate.invoke(currentPosition + dragDistance.toDpPosition(density))
                println("dragging, value = ${currentPosition + dragDistance.toDpPosition(density)}")
                return dragDistance
            }

            override fun onStop(velocity: Offset) { onDrop.invoke(currentPosition) }

            override fun onCancel() { onDrop.invoke(currentPosition) }
        },
        canDrag = canDrag,
        startDragImmediately = startDragImmediately
    )
    .pointerInteropFilter { event ->
        println("pointerEvent = $event")
        onPositionUpdate.invoke(Offset(event.x, event.y).toDpPosition(density))
        false
    }
}