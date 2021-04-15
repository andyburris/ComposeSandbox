package com.andb.apps.composesandbox.ui.util

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
    content: @Composable (value: T, visibility: Float, isTop: Boolean) -> Unit
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
                val animation = animatedVisibility(
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
                val isTop = when {
                    key.index > currentIndex -> true
                    key.index < currentIndex -> false
                    else /*key.index == currentIndex*/ -> !state.items.any { it.index > key.index }
                }
                children(animation.value, isTop)
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

private typealias StackItemTransitionContent = @Composable (children: @Composable (progress: Float, isTop: Boolean) -> Unit) -> Unit

@Composable
private fun animatedVisibility(
    animation: AnimationSpec<Float>,
    visible: Boolean,
    onAnimationFinish: () -> Unit = {}
): Animatable<Float, AnimationVector1D> {
    val animatedFloat = remember { Animatable(if (visible) 1f else 0f) }
    LaunchedEffect(visible) {
        val result = animatedFloat.animateTo(
            if (visible) 1f else 0f,
            animationSpec = animation
        )
        if (result.endReason == AnimationEndReason.Finished) {
            onAnimationFinish()
        }
    }
    return animatedFloat
}

/*
@Composable
fun <T> StackSwitcher2(
    stack: List<T>,
    modifier: Modifier = Modifier,
    animateIf: (old: T?, current: T) -> Boolean = { old, new -> old != new },
    animationSpec: FiniteAnimationSpec<Float> = tween(),
    content: @Composable (T, StackItemTransitionState, Float) -> Unit
) {
    val items = remember { mutableStateListOf<StackAnimationItem2<T>>() }
    val transitionState = remember { MutableTransitionState(stack) }
    val targetChanged = (stack != transitionState.targetState)
    transitionState.targetState = stack
    val transition = updateTransition(transitionState)
    if (targetChanged || items.isEmpty()) {
        // Only manipulate the list when the state is changed, or in the first run.
        val keys = items.map { it.key }.run {
            if (!contains(stack)) {
                toMutableList().also { it.add(stack) }
            } else {
                this
            }
        }
        items.clear()
        keys.mapTo(items) { key ->
            StackAnimationItem2(key) {
                val alpha by transition.animateFloat(
                    transitionSpec = { animationSpec }
                ) { if (it == key) 1f else 0f }
                Box(Modifier.alpha(alpha = alpha)) {
                    content(key)
                }
            }
        }
    } else if (transitionState.currentState == transitionState.targetState) {
        // Remove all the intermediate items from the list once the animation is finished.
        items.removeAll { it.key != transitionState.targetState }
    }

    Box(modifier) {
        items.forEach {
            key(it.key) {
                it.content()
            }
        }
    }
}

private data class StackAnimationItem2<T>(
    val key: T,
    val index: Int,
    val content: @Composable () -> Unit
)*/
