package com.andb.apps.composesandbox.ui.sandbox.tree

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.longPressGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.globalPosition
import androidx.compose.ui.onGloballyPositioned
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.R
import com.andb.apps.composesandbox.data.model.Properties
import com.andb.apps.composesandbox.data.model.PrototypeComponent
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.DragDropAmbient

data class HoverState(val dropIndicatorPosition: Dp, val indent: Int, val hoveringComponent: PrototypeComponent, val dropAbove: Boolean)
private data class TreeHoverItem(val component: PrototypeComponent, val position: Position, val height: Dp, val indent: Int = 0) {
    fun isHovering(hoverPosition: Position): Boolean =
        hoverPosition.y in position.y..(position.y + height)

    fun getHoverState(hoverPosition: Position): HoverState {
        val hoverInTopHalf = hoverPosition.y < (position.y + height / 2) && indent > 0
        val dropIndicatorPosition = when {
            hoverInTopHalf -> position
            else -> position.copy(y = position.y + height)
        }
        val indent = this.indent + if (component.properties is Properties.Group && component.properties.children.isEmpty() && !hoverInTopHalf) 1 else 0
        return HoverState(dropIndicatorPosition.y, indent, component, hoverInTopHalf)
    }
}

@Composable
fun Tree(parent: PrototypeComponent, modifier: Modifier = Modifier, globalPositionOffset: Position, movingPosition: Position?, onMove: (HoverState) -> Unit) {
    val (treePositions, setTreePositions) = remember { mutableStateOf<List<TreeHoverItem>>(emptyList()) }
    TreeItem(component = parent, modifier) { newPositions ->
        setTreePositions(newPositions.map { it.copy(position = it.position - globalPositionOffset) }.sortedByDescending { it.indent })
        //println("new tree positions (size = ${treePositions.size}) = $treePositions")
    }
    if (movingPosition != null) {
        val treeTop = treePositions.maxOfOrNull { it.position.y } ?: 0.dp
        val treeBottom = treePositions.maxOfOrNull { it.position.y + it.height } ?: 0.dp
        val above = movingPosition.y < treeTop
        val below = movingPosition.y > treeBottom
        val hovering = treePositions.find { it.isHovering(movingPosition) }
        //println("finding hover at $movingPosition, hover positions = ${treePositions.map { it.position.y..(it.position.y + it.height) }} hovering = $hovering")
        when {
            /*below -> {
                val indent = (movingPosition.x / 40.dp).toInt().coerceAtMost(treePositions.maxByOrNull { it.position.y }?.indent ?: 0)
                val hoverState = HoverState(treeBottom, indent)
                onMove.invoke(hoverState)
            } TODO: decide whether multiple top-levels can be used in a tree, include this code if yes */
            hovering != null -> {
                //val parent = treePositions.maxByOrNull { it.position.y < hovering.position.y && it.indent == hovering.indent - 1 }
                onMove.invoke(hovering.getHoverState(movingPosition))
            }
        }
    }
}

@Composable
private fun TreeItem(component: PrototypeComponent, modifier: Modifier = Modifier, hoistTreePositions: (List<TreeHoverItem>) -> Unit) {
    val actionHandler = ActionHandlerAmbient.current
    val density = DensityAmbient.current
    val dragDropState = DragDropAmbient.current
    val childTreePositions = remember(component) { mutableStateOf<List<TreeHoverItem>>(emptyList()) }
    Column(
        modifier = modifier
            .onGloballyPositioned {
                val hoverItem = TreeHoverItem(
                    component,
                    it.globalPosition.toDpPosition(density),
                    with(density) { it.size.height.toDp() }
                )
                hoistTreePositions.invoke(childTreePositions.value.plusElement(hoverItem))
            }
            .clickable(
                onClick = { actionHandler.invoke(UserAction.OpenComponent(component.id)) }
            )

            .longPressGestureFilter {
                dragDropState.positionState.value = it.toDpPosition(density)
                actionHandler.invoke(UserAction.MoveComponent(component))
            }
            .fillMaxWidth()
    ) {
        ComponentItem(
            component = component,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        if (component.properties is Properties.Group) {
            GenericTree(items = component.properties.children) { child ->
                TreeItem(child) { treePositions ->
                    val indented = treePositions.map { it.copy(indent = it.indent + 1) }
                    childTreePositions.value = childTreePositions.value
                        .plus(indented)
                        .distinctBy { it.component.id }
                }
            }
        }
    }
}

data class TreeConfig(
    val lineWidth: Dp = 1.dp,
    val horizontalLineLength: Dp = 20.dp,
    val horizontalPaddingStart: Dp = 12.dp,
    val horizontalPaddingEnd: Dp = 8.dp,
)

@Composable
fun <T> GenericTree(items: List<T>, modifier: Modifier = Modifier, treeConfig: TreeConfig = TreeConfig(), component: @Composable RowScope.(T) -> Unit) {
    Box(modifier) {
        val (height, setHeight) = remember { mutableStateOf(0) }
        val (lastItemHeight, setLastItemHeight) = remember { mutableStateOf(0) }
        Column(Modifier.onGloballyPositioned { setHeight(it.size.height) }) {
            for ((index, item) in items.withIndex()) {
                Row(
                    modifier = when (index) {
                        items.size - 1 -> Modifier.onGloballyPositioned { setLastItemHeight(it.size.height) }
                        else -> Modifier
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .padding(start = treeConfig.horizontalPaddingStart, top = 20.dp, end = treeConfig.horizontalPaddingEnd)
                            .size(treeConfig.horizontalLineLength, treeConfig.lineWidth)
                            .background(Color.Black.copy(alpha = .25f))
                    )
                    component(item)
                }
            }
        }
        if (items.isNotEmpty()){
            Box(
                modifier = Modifier
                    .padding(start = treeConfig.horizontalPaddingStart - 1.dp)
                    .size(1.dp, (with(DensityAmbient.current) { height.toDp() } - (with(DensityAmbient.current) { lastItemHeight.toDp() } - 20.dp) + 1.dp).coerceAtLeast(0.dp))
                    .background(MaterialTheme.colors.secondaryVariant)
            )
        }
    }
}

@Composable
fun ComponentItem(component: PrototypeComponent, modifier: Modifier = Modifier) {
    val icon = when (component.properties) {
        is Properties.Text -> Icons.Default.TextFields
        is Properties.Icon -> Icons.Default.Image
        is Properties.Group.Column -> vectorResource(id = R.drawable.ic_column)
        is Properties.Group.Row -> vectorResource(id = R.drawable.ic_row)
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(asset = icon)
        Text(text = component.name, modifier = Modifier.padding(start = 16.dp))
    }
}