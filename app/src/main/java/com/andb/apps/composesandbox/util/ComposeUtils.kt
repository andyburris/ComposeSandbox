package com.andb.apps.composesandbox.util

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

fun Modifier.startBorder(width: Dp, color: Color) = this.drawBehind {
    drawRect(color, size = this.size.copy(width = width.toPx()))
}

fun Modifier.endBorder(width: Dp, color: Color) = drawBehind {
    drawRect(
        color = color,
        topLeft = Offset.Zero.copy(x = this.size.width - width.toPx()),
        size = this.size.copy(width = width.toPx())
    )
}

fun Modifier.bottomBorder(height: Dp, color: Color) = drawBehind {
    drawRect(
        color = color,
        topLeft = Offset.Zero.copy(y = this.size.height - height.toPx()),
        size = this.size.copy(height = height.toPx())
    )
}

fun Offset.toDpOffset(density: Density) = with(density) { DpOffset(this@toDpOffset.x.toDp(), this@toDpOffset.y.toDp()) }
fun Offset.toIntOffset() =  IntOffset(this.x.roundToInt(), this.y.roundToInt())

fun Color.isDark(): Boolean {
    val darkness: Double = 1 - (0.299 * this.red + 0.587 * this.green + 0.114 * this.blue)
    return darkness >= 0.3
}

@Composable
fun <T> ColumnScope.gridItems(
    items: List<T>,
    columns: Int = 2,
    rowModifier: Modifier = Modifier,
    rowArrangement: Arrangement.Horizontal = Arrangement.Start,
    rowAlignment: Alignment.Vertical = Alignment.Top,
    itemContent: @Composable() RowScope.(T) -> Unit
) {
    items.chunked(columns).forEach { rowItems ->
        Row(modifier = rowModifier, horizontalArrangement = rowArrangement, verticalAlignment = rowAlignment) {
            rowItems.forEach {
                itemContent(it)
            }
            repeat(columns - rowItems.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}


fun <T> LazyListScope.gridItems(
    items: List<T>,
    columns: Int = 2,
    rowModifier: Modifier = Modifier,
    rowArrangement: Arrangement.Horizontal = Arrangement.Start,
    rowAlignment: Alignment.Vertical = Alignment.Top,
    itemContent: @Composable() RowScope.(T) -> Unit
) {
    items(items.chunked(columns)) { rowItems ->
        Row(modifier = rowModifier, horizontalArrangement = rowArrangement, verticalAlignment = rowAlignment) {
            rowItems.forEach {
                itemContent(it)
            }
            repeat(columns - rowItems.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}


val Colors.onBackgroundSecondary get() = this.onSecondary
val Colors.divider get() = this.secondaryVariant
val Colors.overlay get() = this.secondary