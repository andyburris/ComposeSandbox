package com.andb.apps.composesandbox.ui.util

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

/**
 * [StackSwitcher] allows to switch between items in a list with a transition defined by
 * [transitionDefinition].
 *
 * @param current is a key representing your current layout state. Every time you change a key
 * the animation will be triggered. The [content] called with the old key will be animated out while
 * the [content] called with the new key will be animated in.
 * @param transitionDefinition is a [TransitionDefinition] using [ItemTransitionState] as
 * the state type.
 * @param modifier Modifier to be applied to the animation container.
 */
@Composable
fun <T> StackSwitcher(
    stack: List<T>,
    modifier: Modifier = Modifier,
    animateIf: (old: T?, current: T) -> Boolean = { old, new -> old != new },
    animationSpec: AnimationSpec<Float> = tween(),
    content: @Composable (T, StackItemTransitionState, Float) -> Unit
) {
    val state = remember { StackTransitionInnerState<T>() }

    val current = stack.withIndex().last()
    if (state.items.isEmpty() || animateIf(state.current?.value, current.value)) {
        state.current = current
        val newStackAnimationItems: List<IndexedValue<T>> = stack.withIndex().toList()
        val oldStackAnimationItems: List<IndexedValue<T>> = state.items.map { itemTransitionItem -> itemTransitionItem.key }.withIndex().toList()
        val keys: List<IndexedValue<T>> = (oldStackAnimationItems + newStackAnimationItems).distinctBy { it.index }
        state.items.clear()

        keys.mapTo(state.items) { key ->
            StackAnimationItem(key.value, key.index) { children ->
                val animationProgress = animatedVisibility(
                    animation = animationSpec,
                    visible = key == current,
                    onAnimationFinish = {
                        if (key == state.current) {
                            // leave only the current in the list
                            state.items.removeAll { it.key != state.current }
                            state.scope?.invalidate()
                        }
                    }
                )
                val currentIndex = current.index
                val transitionState = when {
                    animationProgress == 1f -> StackItemTransitionState.Visible
                    key.index > currentIndex -> StackItemTransitionState.Removing
                    key.index < currentIndex -> StackItemTransitionState.Hiding
                    else /*key.index == currentIndex*/ -> if (state.items.any { it.index > key.index }) StackItemTransitionState.Revealing else StackItemTransitionState.Adding
                }
                children(transitionState, animationProgress)
            }
        }
    } else if (current != state.current) {
        state.current = current
        state.items = state.items.mapIndexed { index, stackAnimationItem -> stackAnimationItem.copy(key = stack[index]) }.toMutableList()
    }
    Box(modifier) {
        state.scope = currentRecomposeScope
        state.items.forEach { (item, index, transition) ->
            key(index) {
                transition { transitionState, progress ->
                    content(item, transitionState, progress)
                }
            }
        }
    }
}

enum class StackItemTransitionState {
    Visible, Adding, Removing, Hiding, Revealing
}

private class StackTransitionInnerState<T> {
    // we use Any here as something which will not be equals to the real initial value
    var current: IndexedValue<T>? = null
    var items = mutableListOf<StackAnimationItem<T>>()
    var scope: RecomposeScope? = null
}

private data class StackAnimationItem<T>(
    val key: T,
    val index: Int,
    val content: StackItemTransitionContent
)

private typealias StackItemTransitionContent = @Composable (children: @Composable (StackItemTransitionState, Float) -> Unit) -> Unit

@Composable
private fun animatedVisibility(
    animation: AnimationSpec<Float>,
    visible: Boolean,
    onAnimationFinish: () -> Unit = {}
): Float {
    val animatedFloat = animatedFloat(if (!visible) 1f else 0f)
    DisposableEffect(visible) {
        animatedFloat.animateTo(
            if (visible) 1f else 0f,
            anim = animation,
            onEnd = { reason, _ ->
                if (reason == AnimationEndReason.TargetReached) {
                    onAnimationFinish()
                }
            }
        )
        onDispose {
            animatedFloat.stop()
        }
    }
    return animatedFloat.value
}
