package com.andb.apps.composesandbox.ui.sandbox.drawer.tree

import androidx.compose.animation.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.drawLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.model.PrototypeComponent
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.DrawerScreen
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.*

/**
 * Tree representing prototype components. Holds drag-and-drop logic currently. Uses [GenericTree] under the hood
 * @param opened the top-level component opened in the editor
 * @param sheetState the state of the bottom sheet, used to calculate the global position of each [TreeItem]
 * @param hovering the component currently in the drag state of drag-and-drop. null if no component is currently being dragged
 */
@Composable
fun DrawerTree(opened: PrototypeComponent, sheetState: BottomSheetState, hovering: DropState?, onMoveComponent: (PrototypeComponent) -> Unit) {

    val dragPosition = DragDropAmbient.current.dragPosition
    Column(
        modifier = Modifier
    ) {
        DrawerTreeHeader(opened, sheetState)
        Tree(
            parent = opened,
            modifier = Modifier.padding(start = 32.dp, end = 32.dp),
            onMoveComponent = onMoveComponent
        )
    }
    if (hovering != null) {
        DeleteOverlay(hovering)
        if (hovering is DropState.OverTreeItem) {
            println("hoverstate padding = ${hovering.indicatorState.position}")
            DropIndicator(hovering)
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
        Icon(asset = Icons.Default.Add, modifier = Modifier.clickable { actionHandler.invoke(UserAction.OpenDrawerScreen(DrawerScreen.AddComponent)) })
    }
}


@Composable
private fun DropIndicator(dropState: DropState.OverTreeItem) {
    val position = dropState.indicatorState.position
    Row(modifier = Modifier.padding(start = 24.dp, top = position).fillMaxWidth()) {
        repeat(dropState.indicatorState.indent) {
            Box(
                Modifier.size(40.dp, 2.dp).padding(end = 2.dp).background(MaterialTheme.colors.primary.copy(alpha = .5f)),
            )
        }
        Box(
            Modifier.height(2.dp).weight(1f).background(MaterialTheme.colors.primary),
        )
    }
}

@Composable
private fun DeleteOverlay(dropState: DropState) {
    val backgroundColor = animate(target = if (dropState is DropState.OverNone) MaterialTheme.colors.error else MaterialTheme.colors.background)
    val textColor = animate(target = if (dropState is DropState.OverNone) MaterialTheme.colors.onError else MaterialTheme.colors.onBackground)
    val gradient = LinearGradient(colors = listOf(backgroundColor, backgroundColor.copy(alpha = 0f)), startX = 0f, startY = 0f, endX = 0f, endY = with(DensityAmbient.current) { 144.dp.toPx() })
    Box(modifier = Modifier.background(gradient).fillMaxWidth().height(144.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 32.dp).align(Alignment.TopCenter)) {
            Icon(asset = Icons.Default.Delete, tint = textColor, modifier = Modifier.padding(end = 16.dp))
            Text(text = "Delete", style = MaterialTheme.typography.subtitle1, color = textColor)
        }
    }
}

fun Offset.toDpPosition(density: Density) = with(density) { Position(this@toDpPosition.x.toDp(), this@toDpPosition.y.toDp()) }