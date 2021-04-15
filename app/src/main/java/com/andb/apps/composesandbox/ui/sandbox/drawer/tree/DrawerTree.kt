package com.andb.apps.composesandbox.ui.sandbox.drawer.tree

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.InternalTextApi
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.state.DrawerScreen
import com.andb.apps.composesandbox.state.LocalActionHandler
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.state.ViewState
import com.andb.apps.composesandbox.ui.common.*
import com.andb.apps.composesandbox.ui.sandbox.drawer.DragDropScrolling
import com.andb.apps.composesandbox.ui.sandbox.drawer.DrawerHeader
import com.andb.apps.composesandbox.ui.sandbox.drawer.toShadow
import com.andb.apps.composesandboxdata.model.PrototypeComponent
import com.andb.apps.composesandboxdata.model.PrototypeTree
import com.andb.apps.composesandboxdata.model.TreeType
import com.andb.apps.composesandboxdata.model.screens
import kotlinx.coroutines.launch

/**
 * Tree representing prototype components. Holds drag-and-drop logic currently. Uses [GenericTree] under the hood
 * @param opened the top-level component opened in the editor
 * @param sheetState the state of the bottom sheet, used to calculate the global position of each [TreeItem]
 * @param hovering the component currently in the drag state of drag-and-drop. null if no component is currently being dragged
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawerTree(
    sandboxState: ViewState.Sandbox,
    sheetState: BottomSheetState,
    hovering: HoverState?,
    scrolling: DragDropScrolling,
    onMoveComponent: (PrototypeComponent, pointerOffset: DpOffset) -> Unit,
    onTreeNameChanged: (PrototypeTree) -> Unit,
    onDeleteTree: (PrototypeTree) -> Unit,
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val distanceToTop = remember(scrolling) { with(density) { scrollState.value.toDp() } }
    val distanceToBottom = remember(scrolling) { with(density) { (scrollState.maxValue - scrollState.value).toDp() } }
    LaunchedEffect(scrolling) {
        when(scrolling) {
            DragDropScrolling.ScrollingUp -> scrollState.animateScrollTo(0, tween(distanceToTop.value.toInt() * 2))
            DragDropScrolling.ScrollingDown -> if (scrollState.maxValue > 0) scrollState.animateScrollTo(scrollState.maxValue, tween(distanceToBottom.value.toInt() * 2))
            DragDropScrolling.None -> scrollState.stopScroll()
        }
    }
    Box {
        Column {
            val coroutineScope = rememberCoroutineScope()
            DrawerTreeHeader(
                sandboxState,
                modifier = Modifier
                    .shadow(scrollState.toShadow())
                    .background(LocalElevationOverlay.current?.apply(color = MaterialTheme.colors.surface, elevation = LocalAbsoluteElevation.current + scrollState.toShadow())
                        ?: MaterialTheme.colors.surface),
                isExpanded = sheetState.targetValue == BottomSheetValue.Expanded,
                onToggleExpand = {
                    coroutineScope.launch {
                        if (sheetState.isExpanded) sheetState.collapse() else sheetState.expand()
                    }
                },
                onTreeNameChanged = {
                    onTreeNameChanged.invoke(sandboxState.openedTree.copy(name = it))
                },
                onDeleteTree = {
                    onDeleteTree.invoke(sandboxState.openedTree)
                }
            )
            Column(Modifier.verticalScroll(scrollState, enabled = hovering == null)) {
                Tree(
                    parent = sandboxState.openedTree.component,
                    modifier = Modifier.padding(start = 32.dp, end = 32.dp, bottom = 32.dp),
                    onMoveComponent = onMoveComponent
                )
            }
        }
        if (hovering != null) {
            DeleteOverlay(hovering)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, InternalTextApi::class)
@Composable
private fun DrawerTreeHeader(sandboxState: ViewState.Sandbox, modifier: Modifier = Modifier, isExpanded: Boolean, onTreeNameChanged: (String) -> Unit, onDeleteTree: () -> Unit, onToggleExpand: () -> Unit) {
    val actionHandler = LocalActionHandler.current
    val iconRotation = animateFloatAsState(targetValue = if (isExpanded) 180f else 0f).value
    DrawerHeader(
        title = sandboxState.openedTree.name,
        titleSlot = {
            Box {
                BasicTextField(
                    value = it,
                    textStyle = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onBackground),
                    onValueChange = {
                        onTreeNameChanged.invoke(it)
                    },
                    decorationBox = { innerTextField ->
                        innerTextField()
                        if (it.isEmpty()) {
                            Text(
                                text = if (sandboxState.openedTree.treeType == TreeType.Screen) "Screen Name" else "Component Name",
                                style = MaterialTheme.typography.h6,
                                color = MaterialTheme.colors.secondaryVariant
                            )
                        }
                    }
                )
            }
        },
        modifier = modifier,
        icon = Icons.Default.KeyboardArrowUp,
        iconSlot = {
            IconButton(onClick = onToggleExpand) {
                Icon(
                    imageVector = it,
                    contentDescription = if (isExpanded) "Collapse Drawer" else "Expand Drawer",
                    modifier = Modifier
                        .graphicsLayer(rotationZ = iconRotation)
                )
            }
        },
        onIconClick = onToggleExpand
    ) {
        IconButton(onClick = { actionHandler.invoke(UserAction.OpenDrawerScreen(DrawerScreen.AddComponent)) }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Component")
        }
        OverflowMenu {
            MenuItem(
                icon = Icons.Default.Delete,
                title = if (sandboxState.openedTree.treeType == TreeType.Screen) "Delete Screen" else "Delete Component",
                enabled = sandboxState.openedTree.treeType == TreeType.Component || sandboxState.project.trees.screens().size >= 2,
                onClick = {
                    onDeleteTree.invoke()
                }
            )
        }
    }
}




@Composable
private fun DeleteOverlay(hoverState: HoverState) {
    val backgroundColor = animateColorAsState(targetValue = if (hoverState is HoverState.OverNone) MaterialTheme.colors.error else MaterialTheme.colors.background).value
    val textColor = animateColorAsState(targetValue = if (hoverState is HoverState.OverNone) MaterialTheme.colors.onError else MaterialTheme.colors.onBackground).value
    val gradient = Brush.verticalGradient(colors = listOf(backgroundColor, backgroundColor.copy(alpha = 0f)), startY = 0f, endY = with(LocalDensity.current) { 144.dp.toPx() })
    Box(modifier = Modifier
        .background(gradient)
        .fillMaxWidth()
        .height(144.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .padding(top = 32.dp)
            .align(Alignment.TopCenter)) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = textColor, modifier = Modifier.padding(end = 16.dp))
            Text(text = "Delete", style = MaterialTheme.typography.subtitle1, color = textColor)
        }
    }
}

fun Offset.toDpPosition(density: Density) = with(density) { DpOffset(this@toDpPosition.x.toDp(), this@toDpPosition.y.toDp()) }