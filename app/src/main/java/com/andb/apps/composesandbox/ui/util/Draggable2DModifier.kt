package com.andb.apps.composesandbox.ui.util

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.toDpPosition

fun Modifier.draggable2D(
    onDrop: () -> Unit,
    canDrag: Boolean = true,
    onDragStart: (DpOffset) -> Unit,
    onPositionUpdate: (delta: DpOffset) -> Unit
): Modifier = composed {
    val density = LocalDensity.current
    return@composed Modifier.pointerInput(canDrag) {
        detectDragGestures(
            onDragStart = { onDragStart.invoke(it.toDpPosition(density)) },
            onDragEnd = { onDrop.invoke() },
            onDragCancel = { onDrop.invoke() }
        ) { change, dragAmount ->
            if (canDrag) { change.consumePositionChange() }
            onPositionUpdate.invoke(dragAmount.toDpPosition(density))
        }
/*            detectTapGestures(onPress = {
                onPositionUpdate.invoke(Offset(it.x, it.y).toDpPosition(density))
            })*/
    }
}