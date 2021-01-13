package com.andb.apps.composesandbox.ui.sandbox.drawer

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.andb.apps.composesandbox.ui.util.ItemTransitionState

val Alpha = FloatPropKey()
val ContentOffset = FloatPropKey()

@Composable
fun getDrawerContentTransition(
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