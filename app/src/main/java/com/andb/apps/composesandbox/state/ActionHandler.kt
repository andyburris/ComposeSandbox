package com.andb.apps.composesandbox.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

typealias ActionHandler = (Action) -> Unit

val LocalActionHandler = staticCompositionLocalOf<ActionHandler>{ error("No ActionHandler provided") }

@Composable
fun ActionHandlerProvider(actionHandler: ActionHandler, content: @Composable() () -> Unit){
    CompositionLocalProvider(LocalActionHandler provides actionHandler) {
        content()
    }
}

val Handler @Composable get() = LocalActionHandler.current