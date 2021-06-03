package com.andb.apps.composesandbox.ui.util

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntSize

fun Modifier.genericDroppable(onMeasure: (positionInWindow: Offset, size: IntSize) -> Unit, onDispose: () -> Unit) = composed {
    DisposableEffect(onDispose) {
        onDispose(onDispose)
    }
    this.onGloballyPositioned {
        onMeasure(it.positionInWindow(),  it.size)
    }
}