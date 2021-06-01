package com.andb.apps.composesandbox.ui.sandbox.drawer.tree

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
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
import com.andb.apps.composesandbox.ui.util.genericDroppable
import com.andb.apps.composesandbox.util.divider
import com.andb.apps.composesandbox.util.onBackgroundSecondary
import com.andb.apps.composesandboxdata.model.PrototypeComponent
import com.andb.apps.composesandboxdata.model.Slot
import com.andb.apps.composesandboxdata.model.stringify


@Composable
fun Tree(parent: PrototypeComponent, modifier: Modifier = Modifier, onMoveComponent: (PrototypeComponent, pointerOffset: DpOffset) -> Unit) {
    Column(modifier) {
        GenericTree {
            ComponentTreeItem(component = parent, lastItem = true, onMoveComponent = onMoveComponent)
        }
    }
}

@Composable
private fun DropIndicator(indent: Int) {
    Row(modifier = Modifier
        .offset(x = (-8).dp)
        .fillMaxWidth()
        .requiredHeight(0.dp)) {
        repeat(indent) {
            Box(
                Modifier
                    .requiredSize(40.dp, 2.dp)
                    .padding(end = 2.dp)
                    .background(MaterialTheme.colors.primary.copy(alpha = .5f)),
            )
        }
        Box(
            Modifier
                .requiredHeight(2.dp)
                .weight(1f)
                .background(MaterialTheme.colors.primary),
        )
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
fun TreeScope.ComponentTreeItem(component: PrototypeComponent, lastItem: Boolean, modifier: Modifier = Modifier, onMoveComponent: (PrototypeComponent, pointerOffset: DpOffset) -> Unit) {
    val actionHandler = LocalActionHandler.current
    val density = LocalDensity.current
    val dragDropState = LocalDragDrop.current
    ComponentDropIndicator(component = component) {
        GenericTreeItem(
            lastItem = lastItem,
            modifier = modifier
                .clickable { actionHandler.invoke(UserAction.OpenDrawerScreen(DrawerScreen.EditComponent(component.id))) }
                .genericDroppable(
                    onMeasure = { offset, size ->
                        val hoverItem = TreeHoverItem(
                            component,
                            offset.toDpPosition(density),
                            with(density) { size.height.toDp() },
                            branchesShowing.size,
                            true
                        )
                        dragDropState.updateTreeItem(hoverItem)
                    },
                    onDispose = { dragDropState.removeTreeItem(component.id) }
                )
                .fillMaxWidth()
        ) {
            ComponentItem(
                component = component,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                draggable = true,
                onDrag = {
                    println("onDrag, moving ${component.stringify()}")
                    onMoveComponent.invoke(component, it)
                }
            )
        }
    }

    when (component) {
        is PrototypeComponent.Group -> ChildrenDropIndicator(component = component) {
            GenericTree(showLastBranch = !lastItem) {
                component.children.forEachIndexed { index, childComponent ->
                    ComponentTreeItem(component = childComponent, lastItem = index == component.children.size - 1, onMoveComponent = onMoveComponent)
                }
            }
        }
        is PrototypeComponent.Slotted -> GenericTree(showLastBranch = !lastItem) {
            component.slots.enabledSlots().forEachIndexed { index, slot ->
                SlotTreeItem(slot = slot, lastItem = index == component.slots.enabledSlots().size - 1, onMoveComponent = onMoveComponent)
            }
        }
    }
}

@Composable
fun TreeScope.SlotTreeItem(slot: Slot, lastItem: Boolean, modifier: Modifier = Modifier, indent: Int = 0, onMoveComponent: (PrototypeComponent, pointerOffset: DpOffset) -> Unit) {
    val density = LocalDensity.current
    val dragDropState = LocalDragDrop.current
    GenericTreeItem(
        lastItem = lastItem,
        modifier = modifier.genericDroppable(
            onMeasure = { offset, size ->
                val hoverItem = TreeHoverItem(
                    slot.group,
                    offset.toDpPosition(density),
                    with(density) { size.height.toDp() },
                    branchesShowing.size,
                    false
                )
                dragDropState.updateTreeItem(hoverItem)
                println("updated tree item with $hoverItem")
            },
            onDispose = { dragDropState.removeTreeItem(slot.group.id); println("removed hover item with id = ${slot.group.id}") }
        )
    ) {
        MaterialTheme(colors = MaterialTheme.colors.copy(onBackground = MaterialTheme.colors.onBackgroundSecondary)) {
            ProvideTextStyle(value = TextStyle.Default.copy(fontStyle = FontStyle.Italic)) {
                ComponentItem(
                    component = slot.group,
                    name = slot.name,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
    ChildrenDropIndicator(component = slot.group) {
        GenericTree(showLastBranch = !lastItem) {
            slot.group.children.forEachIndexed { index, childComponent ->
                ComponentTreeItem(component = childComponent, lastItem = index == slot.group.children.size - 1, onMoveComponent = onMoveComponent)
            }
        }
    }
}

data class TreeScope(val branchesShowing: List<Boolean>)

@Composable
fun GenericTree(content: @Composable TreeScope.() -> Unit) {
    val scope = TreeScope(branchesShowing = emptyList())
    content.invoke(scope)
}

@Composable
fun TreeScope.GenericTree(showLastBranch: Boolean, content: @Composable TreeScope.() -> Unit) {
    val indentedScope = when {
        this.branchesShowing.isEmpty() -> this.copy(branchesShowing = listOf(true))
        else -> this.copy(branchesShowing = this.branchesShowing.dropLast(1) + showLastBranch + true)
    }
    content.invoke(indentedScope)
}

@Composable
fun TreeScope.GenericTreeItem(lastItem: Boolean, modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(modifier.height(IntrinsicSize.Max)) {
        branchesShowing.forEachIndexed { index, showing ->
            when {
                !showing -> Spacer(modifier = Modifier.width(40.dp))
                showing -> BranchBlock(index == branchesShowing.size - 1, Modifier
                    .background(MaterialTheme.colors.divider)
                    .fillMaxHeight(if (lastItem && index == branchesShowing.size - 1) .5f else 1f)
                    .width(1.dp))
            }
        }
        content()
    }
}

@Composable
private fun TreeScope.BranchBlock(end: Boolean, branchModifier: Modifier = Modifier) {
    Row() {
        Spacer(modifier = Modifier
            .width(11.dp)
            .fillMaxHeight())
        Box(modifier = branchModifier)
        if (end) {
            Box(Modifier
                .background(MaterialTheme.colors.divider)
                .width(20.dp)
                .height(1.dp)
                .align(Alignment.CenterVertically))
        } else {
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
fun TreeScope.ComponentDropIndicator(component: PrototypeComponent, content: @Composable () -> Unit) {
    val dragDropState = LocalDragDrop.current
    val dropState = dragDropState.getDropStateForComponent(component)
    if (dropState?.dropPosition == DropPosition.Above) DropIndicator(indent = branchesShowing.size)
    content()
    if (dropState?.dropPosition == DropPosition.Below) DropIndicator(indent = branchesShowing.size)
}

@Composable
fun TreeScope.ChildrenDropIndicator(component: PrototypeComponent, content: @Composable () -> Unit) {
    val dragDropState = LocalDragDrop.current
    val dropState = dragDropState.getDropStateForComponent(component)
    if (dropState?.dropPosition == DropPosition.Nested.First) DropIndicator(indent = branchesShowing.size + 1)
    content()
    if (dropState?.dropPosition == DropPosition.Nested.Last) DropIndicator(indent = branchesShowing.size + 1)
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
            Icon(imageVector = Icons.Default.DragIndicator,
                tint = MaterialTheme.colors.divider,
                contentDescription = "Move Component",
                modifier = Modifier.pointerInput(component) {
                    detectTapGestures(onPress = { onDrag?.invoke(it.toDpPosition(density)) })
                }
            )
        }
    }
}

private fun DragDropState.getDropStateForComponent(component: PrototypeComponent) =
    (getDropState() as? HoverState.OverTreeItem)?.let {
        return@let if (draggingComponent.value != null && it.hoveringComponent == component) it else null
    }