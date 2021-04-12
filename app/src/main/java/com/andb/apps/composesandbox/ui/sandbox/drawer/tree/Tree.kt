package com.andb.apps.composesandbox.ui.sandbox.drawer.tree

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.icon
import com.andb.apps.composesandbox.data.model.name
import com.andb.apps.composesandbox.state.DrawerScreen
import com.andb.apps.composesandbox.state.LocalActionHandler
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.*
import com.andb.apps.composesandbox.util.divider
import com.andb.apps.composesandbox.util.onBackgroundSecondary
import com.andb.apps.composesandboxdata.model.PrototypeComponent
import com.andb.apps.composesandboxdata.model.Slot
import com.andb.apps.composesandboxdata.model.stringify


@Composable
fun Tree(parent: PrototypeComponent, modifier: Modifier = Modifier, onMoveComponent: (PrototypeComponent, pointerOffset: DpOffset) -> Unit) {
    TreeItem(component = parent, modifier, onMoveComponent = onMoveComponent)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TreeItem(component: PrototypeComponent, modifier: Modifier = Modifier, indent: Int = 0, onMoveComponent: (PrototypeComponent, pointerOffset: DpOffset) -> Unit) {
    val actionHandler = LocalActionHandler.current
    val density = LocalDensity.current
    val dragDropState = LocalDragDrop.current
    Column(
        modifier = modifier
    ) {
        val dropState = dragDropState.getDropStateForComponent(component)
        if (dropState?.dropPosition == DropPosition.Above) DropIndicator(indent = indent)
        Row(
            modifier = Modifier.onGloballyPositioned {
                val hoverItem = TreeHoverItem(
                    component,
                    it.positionInWindow().toDpPosition(density),
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
                    .clickable {
                        actionHandler.invoke(UserAction.OpenDrawerScreen(DrawerScreen.EditComponent(component.id)))
                    }
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                draggable = true,
                onDrag = {
                    println("onDrag, moving ${component.stringify()}")
                    onMoveComponent.invoke(component, it)
                }
            )
        }
        if (dropState?.dropPosition == DropPosition.Nested.First) DropIndicator(indent = indent + 1)
        if (component is PrototypeComponent.Group) {
            GenericTree(items = component.children) { child ->
                TreeItem(child, indent = indent + 1, onMoveComponent = onMoveComponent)
            }
        }
        if (component is PrototypeComponent.Slotted) {
            GenericTree(items = component.slots.enabledSlots()) { slot ->
                SlotItem(slot = slot, indent = indent + 1, onMoveComponent = onMoveComponent)
            }
        }
        if (dropState?.dropPosition == DropPosition.Nested.Last) DropIndicator(indent = indent + 1)
        if (dropState?.dropPosition == DropPosition.Below) DropIndicator(indent = indent)
    }
}

@Composable
private fun DropIndicator(indent: Int) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .requiredHeight(0.dp)) {
        Box(Modifier.requiredWidth(0.dp)) {
            Row(Modifier
                .requiredWidth(42.dp * indent)
                .offset(x = (-42).dp * indent / 2)) {
                repeat(indent) {
                    Box(
                        Modifier
                            .requiredSize(40.dp, 2.dp)
                            .padding(end = 2.dp)
                            .background(MaterialTheme.colors.primary.copy(alpha = .5f)),
                    )
                }
            }
        }
        Box(
            Modifier
                .requiredHeight(2.dp)
                .weight(1f)
                .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
        )
    }
}

@Composable
private fun SlotItem(slot: Slot, modifier: Modifier = Modifier, indent: Int, onMoveComponent: (PrototypeComponent, pointerOffset: DpOffset) -> Unit) {
    val density = LocalDensity.current
    val dragDropState = LocalDragDrop.current
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        val dropState = dragDropState.getDropStateForComponent(slot.group)
        if (dropState?.dropPosition == DropPosition.Above) DropIndicator(indent = indent)
        MaterialTheme(colors = MaterialTheme.colors.copy(onBackground = MaterialTheme.colors.onBackgroundSecondary)) {
            ProvideTextStyle(value = TextStyle.Default.copy(fontStyle = FontStyle.Italic)) {
                Row(
                    modifier = Modifier.onGloballyPositioned {
                        val hoverItem = TreeHoverItem(
                            slot.group,
                            it.positionInWindow().toDpPosition(density),
                            with(density) { it.size.height.toDp() },
                            indent,
                            false
                        )
                        dragDropState.updateTreeItem(hoverItem)
                    }

                ) {
                    ComponentItem(
                        component = slot.group,
                        modifier = Modifier.padding(vertical = 8.dp),
                        name = slot.name + " Slot",
                    )
                }
            }
        }
        if (dropState?.dropPosition == DropPosition.Nested.First) DropIndicator(indent = indent + 1)
        GenericTree(items = slot.group.children) { child ->
            TreeItem(child, indent = indent + 1, onMoveComponent = onMoveComponent)
        }
        if (dropState?.dropPosition == DropPosition.Nested.Last) DropIndicator(indent = indent + 1)
        if (dropState?.dropPosition == DropPosition.Below) DropIndicator(indent = indent)
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
                    .size(1.dp, (with(LocalDensity.current) { height.toDp() } - (with(LocalDensity.current) { lastItemHeight.toDp() } - treeConfig.verticalPositionOnItem) + 1.dp).coerceAtLeast(0.dp) - treeConfig.verticalPaddingTop)
                    .background(MaterialTheme.colors.onBackground.copy(alpha = .25f))
            )
        }
    }
}


@Composable
fun ComponentItem(
    component: PrototypeComponent,
    modifier: Modifier = Modifier,
    name: String = component.name,
    draggable: Boolean = false,
    onDrag: ((pointerOffset: DpOffset) -> Unit)? = null
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = component.icon, contentDescription = null, tint = MaterialTheme.colors.onBackgroundSecondary)
            Text(text = name, modifier = Modifier.padding(start = 16.dp), color = MaterialTheme.colors.onBackground)
        }
        if (draggable) {
            val density = LocalDensity.current
            Icon(imageVector = Icons.Default.DragIndicator, tint = MaterialTheme.colors.divider, contentDescription = "Move Component", modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(onPress = { onDrag?.invoke(it.toDpPosition(density)) })
            })
        }
    }
}

private fun DragDropState.getDropStateForComponent(component: PrototypeComponent) = (getDropState() as? HoverState.OverTreeItem)?.let {
    return@let if (draggingComponent.value != null && it.hoveringComponent == component) it else null
}