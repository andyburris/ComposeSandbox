package com.andb.apps.composesandbox.ui.sandbox.drawer.tree

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.longPressGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.globalPosition
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.icon
import com.andb.apps.composesandbox.data.model.name
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.DrawerScreen
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.AmbientDragDrop
import com.andb.apps.composesandbox.ui.common.TreeHoverItem
import com.andb.apps.composesandboxdata.model.PrototypeComponent
import com.andb.apps.composesandboxdata.model.Slot


@Composable
fun Tree(parent: PrototypeComponent, modifier: Modifier = Modifier, onMoveComponent: (PrototypeComponent) -> Unit) {
    TreeItem(component = parent, modifier, onMoveComponent = onMoveComponent)
}

@Composable
private fun TreeItem(component: PrototypeComponent, modifier: Modifier = Modifier, indent: Int = 0, onMoveComponent: (PrototypeComponent) -> Unit) {
    val actionHandler = ActionHandlerAmbient.current
    val density = AmbientDensity.current
    val dragDropState = AmbientDragDrop.current
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.onGloballyPositioned {
                val hoverItem = TreeHoverItem(
                    component,
                    it.globalPosition.toDpPosition(density),
                    with(density) { it.size.height.toDp() },
                    indent,
                    indent != 0
                )
                dragDropState.updateTreeItem(hoverItem)
            }
        ) {
            ComponentItem(
                component = component,
                modifier = Modifier
                    .clickable(
                        onClick = { actionHandler.invoke(UserAction.OpenDrawerScreen(DrawerScreen.EditComponent(component.id))) }
                    )
                    .longPressGestureFilter {
                        onMoveComponent.invoke(component)
                    }
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
        if (component is PrototypeComponent.Group) {
            GenericTree(items = component.children) { child ->
                TreeItem(child, indent = indent + 1, onMoveComponent = onMoveComponent)
            }
        }
        if (component is PrototypeComponent.Slotted) {
            GenericTree(items = component.slots.filter { with(component) { it.enabled } }) { slot ->
                SlotItem(slot = slot, indent = indent + 1, onMoveComponent = onMoveComponent)
            }
        }
    }
}

@Composable
private fun SlotItem(slot: Slot, modifier: Modifier = Modifier, indent: Int, onMoveComponent: (PrototypeComponent) -> Unit) {
    val density = AmbientDensity.current
    val dragDropState = AmbientDragDrop.current
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        ProvideTextStyle(value = TextStyle.Default.copy(fontStyle = FontStyle.Italic)) {
            Row (
                modifier = Modifier.onGloballyPositioned {
                    val hoverItem = TreeHoverItem(
                        slot.group,
                        it.globalPosition.toDpPosition(density),
                        with(density) { it.size.height.toDp() },
                        indent,
                        false
                    )
                    dragDropState.updateTreeItem(hoverItem)
                }

            ){
                ComponentItem(
                    component = slot.group,
                    modifier = Modifier.padding(vertical = 8.dp),
                    name = slot.name + " Slot",
                    colors = Pair(MaterialTheme.colors.onSecondary, MaterialTheme.colors.onSecondary)
                )
            }
        }
        GenericTree(items = slot.group.children) { child ->
            TreeItem(child, indent = indent + 1, onMoveComponent = onMoveComponent)
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
                            .background(MaterialTheme.colors.onBackground.copy(alpha = .25f))
                    )
                    component(item)
                }
            }
        }
        if (items.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .padding(start = treeConfig.horizontalPaddingStart - 1.dp, top = treeConfig.verticalPaddingTop)
                    .size(1.dp, (with(AmbientDensity.current) { height.toDp() } - (with(AmbientDensity.current) { lastItemHeight.toDp() } - treeConfig.verticalPositionOnItem) + 1.dp).coerceAtLeast(0.dp) - treeConfig.verticalPaddingTop)
                    .background(MaterialTheme.colors.onBackground.copy(alpha = .25f))
            )
        }
    }
}


@Composable
fun ComponentItem(component: PrototypeComponent, modifier: Modifier = Modifier, name: String = component.name, colors: Pair<Color, Color> = Pair(MaterialTheme.colors.onSecondary, MaterialTheme.colors.onBackground)) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = component.icon, tint = colors.first)
        Text(text = name, modifier = Modifier.padding(start = 16.dp), color = colors.second)
    }
}