package com.andb.apps.composesandbox.ui.util

import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.draggable2D(
    onDrop: () -> Unit,
    onPositionUpdate: (position: Offset) -> Unit
): Modifier = composed {
    val onDropState = rememberUpdatedState(newValue = onDrop)
    this.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event: PointerEvent = this.awaitPointerEvent(pass = PointerEventPass.Initial)
                when {
                    event.changes.isEmpty() -> continue
                    event.changes.first().changedToUpIgnoreConsumed() -> onDropState.value.invoke()
                    !event.changes.first().pressed -> continue
                    else -> onPositionUpdate.invoke(event.changes.first().position)
                }
            }
        }
    }
}