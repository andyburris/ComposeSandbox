package com.andb.apps.composesandbox.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.staticAmbientOf

typealias ActionHandler = (Action) -> Unit

val ActionHandlerAmbient = staticAmbientOf<ActionHandler>()

@Composable
fun ActionHandlerProvider(actionHandler: ActionHandler, content: @Composable() () -> Unit){
    Providers(ActionHandlerAmbient provides actionHandler) {
        content()
    }
}

@Composable
val Handler get() = ActionHandlerAmbient.current