package com.andb.apps.composesandbox.ui.sandbox.modifiers

import androidx.compose.runtime.Composable
import com.andb.apps.composesandbox.data.model.Component
import com.andb.apps.composesandbox.data.model.PrototypeModifier
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.sandbox.DrawerHeader

@Composable
fun DrawerEditModifiers(editingComponent: Component, modifier: PrototypeModifier) {
    val actionHandler = ActionHandlerAmbient.current
    DrawerHeader(title = "Edit Modifier", onIconClick = { actionHandler.invoke(UserAction.Back) })
}