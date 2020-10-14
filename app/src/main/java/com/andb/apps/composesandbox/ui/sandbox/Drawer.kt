package com.andb.apps.composesandbox.ui.sandbox

import androidx.compose.animation.animate
import androidx.compose.animation.core.*
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.DrawerState
import com.andb.apps.composesandbox.state.SandboxState
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.BottomSheetLayout
import com.andb.apps.composesandbox.ui.common.BottomSheetState
import com.andb.apps.composesandbox.ui.common.BottomSheetValue
import com.andb.apps.composesandbox.ui.common.rememberBottomSheetState
import com.andb.apps.composesandbox.ui.sandbox.modifiers.DrawerEditModifiers
import com.andb.apps.composesandbox.ui.sandbox.properties.DrawerEditProperties
import com.andb.apps.composesandbox.ui.sandbox.tree.DrawerTree
import com.andb.apps.composesandbox.ui.util.ItemTransitionState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Drawer(sandboxState: SandboxState, bodyContent: @Composable() (sheetState: BottomSheetState) -> Unit) {
    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Peek)
    val cornerRadius = animate(target = if (sheetState.targetValue == BottomSheetValue.Expanded) 16.dp else 32.dp)
    BottomSheetLayout(
        sheetState = sheetState,
        closeable = false,
        peekHeight = 88.dp,
        sheetShape = RoundedCornerShape(topLeft = cornerRadius, topRight = cornerRadius),
        bodyContent = { bodyContent(sheetState) },
        sheetContent = {
            val (contentWidth, setContentWidth) = remember { mutableStateOf(0) }
/*            ItemSwitcher(
                current = sandboxState.drawerState,
                transitionDefinition = getDrawerContentTransition(offsetPx = contentWidth.toFloat(), reverse = sandboxState.drawerState is DrawerState.Tree),
                modifier = Modifier.onPositioned { setContentWidth(it.size.width) }
            ) { drawerState, transitionState ->*/
            val drawerState = sandboxState.drawerStack.last()
            Stack {
                val actionHandler = ActionHandlerAmbient.current
                when (drawerState) {
                    is DrawerState.Tree -> DrawerTree(opened = sandboxState.openedTree, sheetState = sheetState, moving = drawerState.movingComponent)
                    DrawerState.AddComponent -> ComponentList(project = sandboxState.project, onSelect = { actionHandler.invoke(UserAction.MoveComponent(it)) })
                    is DrawerState.EditComponent -> DrawerEditProperties(drawerState.editing, actionHandler)
                    is DrawerState.AddModifier -> AddModifierList {
                        val withModifier = drawerState.editingComponent.withModifiers(drawerState.editingComponent.modifiers + it)
                        val updateAction = UserAction.UpdateComponent(withModifier)
                        actionHandler.invoke(updateAction)
                        actionHandler.invoke(UserAction.Back)
                    }
                    is DrawerState.EditModifier -> DrawerEditModifiers(editingComponent = drawerState.editingComponent, modifier = drawerState.modifier)
                }
            }
            //}
        },
    )
}

private val Alpha = FloatPropKey()
private val Offset = FloatPropKey()

@Composable
private fun getDrawerContentTransition(
    duration: Int = 183,
    offsetPx: Float,
    reverse: Boolean = false
): TransitionDefinition<ItemTransitionState> = remember(reverse, offsetPx, duration) {
    transitionDefinition {
        state(ItemTransitionState.Visible) {
            this[Alpha] = 1f
            this[Offset] = 0f
        }
        state(ItemTransitionState.BecomingVisible) {
            this[Alpha] = 0f
            this[Offset] = if (reverse) -offsetPx else offsetPx
        }
        state(ItemTransitionState.BecomingNotVisible) {
            this[Alpha] = 0f
            this[Offset] = if (reverse) offsetPx else -offsetPx
        }

        val halfDuration = duration / 2

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
            Offset using tween(
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
            Offset using tween(
                durationMillis = halfDuration,
                easing = LinearOutSlowInEasing,
                delayMillis = 24
            )
        }
    }
}

@Composable
fun DrawerHeader(title: String, icon: VectorAsset = Icons.Default.ArrowBack, onIconClick: () -> Unit, actions: (@Composable RowScope.() -> Unit)? = null) {

    Row(
        verticalGravity = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(32.dp).fillMaxWidth()
    ) {
        Row(
            verticalGravity = Alignment.CenterVertically
        ) {
            Icon(
                asset = icon,
                modifier = Modifier.clickable(onClick = onIconClick)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        if(actions != null) {
            Row(verticalGravity = Alignment.CenterVertically) {
                actions()
            }
        }
    }
}