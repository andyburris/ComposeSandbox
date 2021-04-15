package com.andb.apps.composesandbox.ui.util

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.toDpPosition

fun Modifier.draggable2D(
    onDrop: () -> Unit,
    canDrag: Boolean = true,
    onPositionUpdate: (position: DpOffset) -> Unit
): Modifier = composed {
    val density = LocalDensity.current
/*    return@composed Modifier.pointerInteropFilter() {
        println("pointer action = ${it.action}")
        when (it.action) {
            MotionEvent.ACTION_DOWN -> onPositionUpdate.invoke(Offset(it.x, it.y).toDpPosition(density))
            MotionEvent.ACTION_MOVE -> onPositionUpdate.invoke(Offset(it.x, it.y).toDpPosition(density))
            MotionEvent.ACTION_UP -> if (canDrag) onDrop.invoke()
        }
        canDrag
    }*/
    return@composed Modifier.pointerInput(canDrag) {
        awaitPointerEventScope {
            val down = this.awaitFirstDown()
            onPositionUpdate.invoke(down.position.toDpPosition(density))
            drag(down.id) { onPositionUpdate.invoke(it.position.toDpPosition(density)) }
            if (canDrag) onDrop.invoke()
        }
    }
/*    return@composed Modifier.pointerInput(Unit) {
        detectDragGestures(onDragEnd = { if (canDrag) onDrop.invoke() }) { change, dragAmount ->
            if (canDrag) change.consumeAllChanges()
            //onPositionUpdate.invoke()
        }
    }*/
}