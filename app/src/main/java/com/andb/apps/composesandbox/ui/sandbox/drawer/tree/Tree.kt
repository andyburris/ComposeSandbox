package com.andb.apps.composesandbox.ui.sandbox.drawer.tree

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.R
import com.andb.apps.composesandbox.model.Properties
import com.andb.apps.composesandbox.model.PrototypeComponent
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.DragDropAmbient
import com.andb.apps.composesandbox.ui.common.TreeHoverItem


@Composable
fun Tree(parent: PrototypeComponent, modifier: Modifier = Modifier) {
    TreeItem(component = parent, modifier)
}

@Composable
private fun TreeItem(component: PrototypeComponent, modifier: Modifier = Modifier, indent: Int = 0) {
    val actionHandler = ActionHandlerAmbient.current
    val density = DensityAmbient.current
    val dragDropState = DragDropAmbient.current
    Column(
        modifier = modifier
            .onGloballyPositioned {
                val hoverItem = TreeHoverItem(
                    component,
                    it.globalPosition.toDpPosition(density),
                    with(density) { it.size.height.toDp() },
                    indent
                )
                dragDropState.updateTreeItem(hoverItem)
            }
            .clickable(
                onClick = { actionHandler.invoke(UserAction.OpenComponent(component.id)) }
            )
            .longPressGestureFilter {
                actionHandler.invoke(UserAction.MoveComponent(component))
            }
            .fillMaxWidth()
    ) {
        ComponentItem(
            component = component,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        if (component.properties is Properties.Group) {
            GenericTree(items = (component.properties as Properties.Group).children) { child ->
                TreeItem(child, indent = indent + 1)
            }
        }
    }
}

data class TreeConfig(
    val lineWidth: Dp = 1.dp,
    val horizontalLineLength: Dp = 20.dp,
    val horizontalPaddingStart: Dp = 12.dp,
    val horizontalPaddingEnd: Dp = 8.dp,
    val verticalPaddingTop: Dp = 0.dp,
    val verticalPositionOnItem: Dp = 20.dp,
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
                            .padding(start = treeConfig.horizontalPaddingStart, top = treeConfig.verticalPositionOnItem, end = treeConfig.horizontalPaddingEnd)
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
                    .padding(start = treeConfig.horizontalPaddingStart - 1.dp, top = treeConfig.verticalPaddingTop)
                    .size(1.dp, (with(DensityAmbient.current) { height.toDp() } - (with(DensityAmbient.current) { lastItemHeight.toDp() } - treeConfig.verticalPositionOnItem) + 1.dp).coerceAtLeast(0.dp) - treeConfig.verticalPaddingTop)
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