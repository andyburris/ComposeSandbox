package com.andb.apps.composesandbox.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

typealias Content = @Composable() () -> Unit

fun Modifier.startBorder(width: Dp, color: Color) = drawBehind {
    drawRect(color, size = this.size.copy(width = width.toPx()))
}
fun Modifier.endBorder(width: Dp, color: Color) = drawBehind {
    drawRect(
        color = color,
        topLeft = Offset.Zero.copy(x = this.size.width - width.toPx()),
        size = this.size.copy(width.toPx())
    )
}