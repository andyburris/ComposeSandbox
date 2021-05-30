package com.andb.apps.composesandbox.ui.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
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
    return@composed Modifier.pointerInput(canDrag) {
        awaitPointerEventScope {
            while (true) {
                val event: PointerEvent = this.awaitPointerEvent(pass = PointerEventPass.Initial)
                when {
                    event.changes.isEmpty() -> continue
                    event.changes.first().changedToUpIgnoreConsumed() -> break
                    else -> onPositionUpdate.invoke(event.changes.first().position.toDpPosition(density))
                }
            }

            //drag(down.id) { onPositionUpdate.invoke(it.position.toDpPosition(density)) }
            if (canDrag) onDrop.invoke()
        }
    }
}