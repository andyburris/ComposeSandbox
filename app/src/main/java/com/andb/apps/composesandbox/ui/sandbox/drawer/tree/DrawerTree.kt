package com.andb.apps.composesandbox.ui.sandbox.drawer.tree

import androidx.compose.animation.animate
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.model.PrototypeComponent
import com.andb.apps.composesandbox.model.PrototypeScreen
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.DrawerScreen
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.*
import com.andb.apps.composesandbox.ui.sandbox.drawer.toShadow

/**
 * Tree representing prototype components. Holds drag-and-drop logic currently. Uses [GenericTree] under the hood
 * @param opened the top-level component opened in the editor
 * @param sheetState the state of the bottom sheet, used to calculate the global position of each [TreeItem]
 * @param hovering the component currently in the drag state of drag-and-drop. null if no component is currently being dragged
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawerTree(opened: PrototypeScreen, sheetState: BottomSheetState, hovering: DropState?, onMoveComponent: (PrototypeComponent) -> Unit) {
    val scrollState = rememberScrollState()
    Box {
        Column(
        ) {
            DrawerTreeHeader(
                opened,
                modifier = Modifier
                    //.border(4.dp, Color.Red)
                    .shadow(scrollState.toShadow())
                    .background(MaterialTheme.colors.background),
                isExpanded = sheetState.targetValue == BottomSheetValue.Expanded
            ) { if (sheetState.isExpanded) sheetState.collapse() else sheetState.expand() }
            ScrollableColumn(scrollState = scrollState) {
                Tree(
                    parent = opened.tree,
                    modifier = Modifier.padding(start = 32.dp, end = 32.dp),
                    onMoveComponent = onMoveComponent
                )
            }
        }
        if (hovering != null) {
            DeleteOverlay(hovering)
            if (hovering is DropState.OverTreeItem) {
                println("hoverstate padding = ${hovering.indicatorState.position}")
                DropIndicator(hovering)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DrawerTreeHeader(opened: PrototypeScreen, modifier: Modifier = Modifier, isExpanded: Boolean, onClick: () -> Unit) {
    val actionHandler = ActionHandlerAmbient.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.padding(32.dp).fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val iconRotation = animate(target = if (isExpanded) 180f else 0f)
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .graphicsLayer(rotationZ = iconRotation)
            )
            Text(
                text = opened.name,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Icon(imageVector = Icons.Default.Add, modifier = Modifier.clickable { actionHandler.invoke(UserAction.OpenDrawerScreen(DrawerScreen.AddComponent)) })
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
    val gradient = Brush.verticalGradient(colors = listOf(backgroundColor, backgroundColor.copy(alpha = 0f)), startY = 0f, endY = with(AmbientDensity.current) { 144.dp.toPx() })
    Box(modifier = Modifier.background(gradient).fillMaxWidth().height(144.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 32.dp).align(Alignment.TopCenter)) {
            Icon(imageVector = Icons.Default.Delete, tint = textColor, modifier = Modifier.padding(end = 16.dp))
            Text(text = "Delete", style = MaterialTheme.typography.subtitle1, color = textColor)
        }
    }
}

fun Offset.toDpPosition(density: Density) = with(density) { Position(this@toDpPosition.x.toDp(), this@toDpPosition.y.toDp()) }