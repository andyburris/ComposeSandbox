package com.andb.apps.composesandbox.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Providers
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.unit.Position


data class DragDropState(val positionState: MutableState<Position>)
val DragDropAmbient = staticAmbientOf<DragDropState>()


@Composable
fun DragDropProvider(dragDropState: DragDropState, content: @Composable() () -> Unit){
    Providers(DragDropAmbient provides dragDropState) {
        content()
    }
}

