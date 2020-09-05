package com.andb.apps.composesandbox.state

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.staticAmbientOf

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