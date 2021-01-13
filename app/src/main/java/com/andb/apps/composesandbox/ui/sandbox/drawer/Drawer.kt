package com.andb.apps.composesandbox.ui.sandbox.drawer

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.dragGestureFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.globalPosition
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.DrawerState
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.state.ViewState
import com.andb.apps.composesandbox.ui.common.*
import com.andb.apps.composesandbox.ui.sandbox.drawer.modifiers.DrawerEditModifiers
import com.andb.apps.composesandbox.ui.sandbox.drawer.properties.DrawerEditProperties
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.ComponentItem
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.DrawerTree
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.toDpPosition
import com.andb.apps.composesandbox.ui.util.ItemSwitcher
import com.andb.apps.composesandbox.ui.util.ItemTransitionState
import com.andb.apps.composesandboxdata.model.*
import com.andb.apps.composesandboxdata.plusElement

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Drawer(
    sandboxState: ViewState.Sandbox,
    sheetState: BottomSheetState,
    modifier: Modifier = Modifier,
    onScreenUpdate: (PrototypeTree) -> Unit,
    onThemeUpdate: (Theme) -> Unit,
    onExtractComponent: (PrototypeComponent) -> Unit,
    onDragUpdate: (dragging: Boolean) -> Unit
) {
    val density = AmbientDensity.current
    val actionHandler = ActionHandlerAmbient.current
    val movingComponent = remember { mutableStateOf<PrototypeComponent?>(null) }
    val (contentSize, setContentSize) = remember { mutableStateOf(Size(0f, 0f)) }
    val drawerState = sandboxState.drawerStack.last()
    println("drawerState = $drawerState")
    val dragPosition = remember { mutableStateOf(Position(0.dp, 0.dp)) }
    val dragDropState = remember(sandboxState.openedTree, drawerState) {
        DragDropState(dragPosition, mutableStateOf(Position(0.dp, 0.dp)), mutableListOf()) { dropState ->
            when (dropState) {
                is DropState.OverTreeItem -> {
                    println("dropping, movingComponent? = ${movingComponent.value}, dropState = $dropState")
                    val moving = movingComponent.value ?: return@DragDropState
                    println("adding to tree")
                    val isNesting = dropState.dropPosition is DropPosition.NESTED
                    println("isNesting = $isNesting")
                    val updatedTree = when (dropState.dropPosition) {
                        is DropPosition.NESTED.First -> sandboxState.openedTree.tree.plusChildInTree(moving, dropState.hoveringComponent as PrototypeComponent.Group, 0)
                        is DropPosition.NESTED.Last -> sandboxState.openedTree.tree.plusChildInTree(moving, dropState.hoveringComponent as PrototypeComponent.Group, dropState.hoveringComponent.children.size)
                        else -> {
                            val (parent, index) = sandboxState.openedTree.tree.findParentOfComponent(dropState.hoveringComponent)!!
                            println("not nesting, parent = $parent, index = $index")
                            when (dropState.dropPosition) {
                                DropPosition.ABOVE -> sandboxState.openedTree.tree.plusChildInTree(moving, parent, index)
                                DropPosition.BELOW -> sandboxState.openedTree.tree.plusChildInTree(moving, parent, index + 1)
                                else -> throw Error("will never reach here")
                            }
                        }
                    } as PrototypeComponent.Group
                    println("updated tree = $updatedTree")
                    movingComponent.value = null
                    onDragUpdate.invoke(false)
                    onScreenUpdate.invoke(sandboxState.openedTree.copy(tree = updatedTree))
                }
                is DropState.OverNone -> {
                    movingComponent.value = null
                    onDragUpdate.invoke(false)
                }
            }
        }
    }
    DragDropProvider(dragDropState = dragDropState) {
        val oldState = remember { mutableStateOf<DrawerState?>(null) }
        val transitionDefinition = getDrawerContentTransition(
            offsetPx = contentSize.width.toFloat(),
            reverse = when (drawerState) {
                is DrawerState.Tree -> true // always at bottom of stack
                is DrawerState.AddComponent, is DrawerState.AddModifier, is DrawerState.EditModifier, is DrawerState.EditTheme -> false // always at top of stack
                is DrawerState.EditComponent -> oldState.value !is DrawerState.Tree
            },
            enabled = oldState.value != null && oldState.value!!::class != drawerState::class
        )
        oldState.value = drawerState
        val dragDropScrolling = if (movingComponent.value != null) {
            val heightDp = with(density) { contentSize.height.toDp() }
                when(dragPosition.value.y) {
                    in 0.dp..112.dp -> DragDropScrolling.ScrollingUp
                    in heightDp - 24.dp..heightDp -> DragDropScrolling.ScrollingDown
                    else -> DragDropScrolling.None
                }
        } else DragDropScrolling.None
        ItemSwitcher(
            current = drawerState,
            animateIf = { old, current -> old != null && old::class != current::class },
            keyFinder = { it::class },
            transitionDefinition = transitionDefinition,
            modifier = modifier
                .dragGestureFilter(
                    dragObserver = object : DragObserver {
                        override fun onDrag(dragDistance: Offset): Offset {
                            dragDropState.dragPosition.value = dragDropState.dragPosition.value + dragDistance.toDpPosition(density)
                            println("dragging, value = ${dragDropState.dragPosition.value}")
                            return dragDistance
                        }

                        override fun onStop(velocity: Offset) {
                            dragDropState.drop()
                        }

                        override fun onCancel() {
                            dragDropState.drop()
                        }
                    },
                    canDrag = { drawerState is DrawerState.Tree && movingComponent.value != null },
                    startDragImmediately = false
                )
                .pointerInteropFilter { event ->
                    println("pointerEvent = $event")
                    dragDropState.dragPosition.value = Offset(event.x, event.y).toDpPosition(density)
                    false
                }
                .onGloballyPositioned {
                    setContentSize(it.size.toSize())
                    dragDropState.globalOffset.value = it.globalPosition.toDpPosition(density)
                }
        ) { drawerState, transitionState ->
            Box(
                modifier = Modifier.graphicsLayer(
                    translationX = transitionState[ContentOffset],
                    alpha = transitionState[Alpha]
                )
            ) {
                when (drawerState) {
                    is DrawerState.Tree -> DrawerTree(opened = sandboxState.openedTree, sheetState = sheetState, hovering = movingComponent.value?.let { dragDropState.getDropState() }, scrolling = dragDropScrolling) {
                        val updatedTree = sandboxState.openedTree.tree.minusChildFromTree(it)
                        onDragUpdate.invoke(true)
                        onScreenUpdate.invoke(sandboxState.openedTree.copy(tree = updatedTree))
                        movingComponent.value = it
                    }
                    DrawerState.AddComponent -> ComponentList(project = sandboxState.project, currentTreeID = sandboxState.openedTree.id) {
                        movingComponent.value = it
                        onDragUpdate.invoke(true)
                        actionHandler.invoke(UserAction.Back)
                    }
                    is DrawerState.EditComponent -> DrawerEditProperties(drawerState.component, actionHandler, onExtractComponent = onExtractComponent) { updatedComponent ->
                        val updatedTree = sandboxState.openedTree.tree.updatedChildInTree(updatedComponent)
                        onScreenUpdate.invoke(sandboxState.openedTree.copy(tree = updatedTree))
                    }
                    is DrawerState.AddModifier -> AddModifierList {
                        val withModifier = sandboxState.editingComponent.copy(modifiers = sandboxState.editingComponent.modifiers.plusElement(it, 0))
                        val updatedTree = sandboxState.openedTree.tree.updatedChildInTree(withModifier)
                        onScreenUpdate.invoke(sandboxState.openedTree.copy(tree = updatedTree))
                        actionHandler.invoke(UserAction.Back)
                    }
                    is DrawerState.EditModifier -> DrawerEditModifiers(prototypeModifier = drawerState.modifier) {
                        println("edited modifier = $it")
                        val updatedTree = sandboxState.openedTree.tree.updatedChildInTree(sandboxState.editingComponent.updatedModifier(it))
                        onScreenUpdate.invoke(sandboxState.openedTree.copy(tree = updatedTree))
                    }
                    is DrawerState.EditTheme -> DrawerEditTheme(theme = sandboxState.project.theme) {
                        onThemeUpdate.invoke(it)
                    }
                }
                val currentMovingComponent = movingComponent.value
                if (currentMovingComponent != null) {
                    ComponentDragDropItem(component = currentMovingComponent, position = dragPosition.value)
                }
            }
        }
    }
}

enum class DragDropScrolling { ScrollingUp, None, ScrollingDown }

private val Alpha = FloatPropKey()
private val ContentOffset = FloatPropKey()

@Composable
private fun getDrawerContentTransition(
    duration: Int = 183,
    offsetPx: Float,
    reverse: Boolean = false,
    enabled: Boolean = true
): TransitionDefinition<ItemTransitionState> = remember(reverse, offsetPx, duration, enabled) {
    val enabledDuration: Int = if (enabled) duration else 0
    transitionDefinition {
        state(ItemTransitionState.Visible) {
            this[Alpha] = 1f
            this[ContentOffset] = 0f
        }
        state(ItemTransitionState.BecomingVisible) {
            this[Alpha] = 0f
            this[ContentOffset] = if (reverse) -offsetPx else offsetPx
        }
        state(ItemTransitionState.BecomingNotVisible) {
            this[Alpha] = 0f
            this[ContentOffset] = if (reverse) offsetPx else -offsetPx
        }

        val halfDuration = enabledDuration / 2

        transition(
            fromState = ItemTransitionState.BecomingVisible,
            toState = ItemTransitionState.Visible
        ) {
            // TODO: look at whether this can be implemented using `spring` to enable
            //  interruptions, etc
            Alpha using tween(
                durationMillis = halfDuration,
                delayMillis = halfDuration,
                easing = LinearEasing
            )
            ContentOffset using tween(
                durationMillis = halfDuration,
                delayMillis = halfDuration,
                easing = LinearOutSlowInEasing
            )
        }

        transition(
            fromState = ItemTransitionState.Visible,
            toState = ItemTransitionState.BecomingNotVisible
        ) {
            Alpha using tween(
                durationMillis = halfDuration,
                easing = LinearEasing,
                delayMillis = 24
            )
            ContentOffset using tween(
                durationMillis = halfDuration,
                easing = LinearOutSlowInEasing,
                delayMillis = 24
            )
        }
    }
}

@Composable
fun DrawerHeader(
    title: String,
    modifier: Modifier = Modifier,
    screenName: String? = null,
    icon: ImageVector = Icons.Default.ArrowBack,
    iconSlot: @Composable (icon: ImageVector) -> Unit = { Icon(imageVector = it, modifier = Modifier.clickable(onClick = onIconClick, indication = rememberRipple(bounded = false, radius = 16.dp))) },
    onIconClick: () -> Unit,
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.padding(32.dp).fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            iconSlot(icon)
            Column(Modifier.padding(start = 16.dp)) {
                if (screenName != null) {
                    Text(text = screenName, style = MaterialTheme.typography.overline, color = MaterialTheme.colors.onSecondary)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6,
                )
            }
        }

        if (actions != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                actions()
            }
        }
    }
}

@Composable
fun ScrollableDrawer(header: @Composable () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    val scrollState = rememberScrollState()
    Column {
        Box(
            modifier = Modifier
                .zIndex(4f)
                .shadow(scrollState.toShadow())
                .background(AmbientElevationOverlay.current?.apply(color = MaterialTheme.colors.surface, elevation = AmbientAbsoluteElevation.current + scrollState.toShadow()) ?: MaterialTheme.colors.surface)
        ) {
            header()
        }
        ScrollableColumn(scrollState = scrollState) {
            content()
        }
    }
}

@Composable
fun ScrollState.toShadow() = with(AmbientDensity.current){ this@toShadow.value.toDp() }.coerceAtMost(4.dp)

@Composable
private fun ComponentDragDropItem(component: PrototypeComponent, position: Position) {
    ComponentItem(
        component = component,
        modifier = Modifier
            .offset(position.x, position.y)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.background, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    )
}