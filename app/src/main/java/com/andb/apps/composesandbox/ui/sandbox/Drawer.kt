package com.andb.apps.composesandbox.ui.sandbox

import androidx.compose.animation.animate
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.DrawerState
import com.andb.apps.composesandbox.state.SandboxState
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.BottomSheetLayout
import com.andb.apps.composesandbox.ui.common.BottomSheetState
import com.andb.apps.composesandbox.ui.common.BottomSheetValue
import com.andb.apps.composesandbox.ui.common.rememberBottomSheetState
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
                val drawerState = sandboxState.drawerState
                Stack(
/*                    modifier = Modifier.drawLayer(
                        translationX = transitionState[Offset],
                        alpha = transitionState[Alpha]
                    )*/
                ) {
                    val actionHandler = ActionHandlerAmbient.current
                    when (drawerState) {
                        is DrawerState.Tree -> DrawerTree(opened = sandboxState.opened, sheetState = sheetState, moving = drawerState.movingComponent)
                        DrawerState.AddComponent -> ComponentList(project = sandboxState.project, onSelect = { actionHandler.invoke(UserAction.MoveComponent(it)) })
                        is DrawerState.EditProperties -> DrawerEditProperties(drawerState.component)
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
