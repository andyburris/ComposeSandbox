package com.andb.apps.composesandbox.ui.sandbox.properties

import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ControlPointDuplicate
import androidx.compose.runtime.Composable
import com.andb.apps.composesandbox.data.model.Component
import com.andb.apps.composesandbox.state.ActionHandler
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.sandbox.DrawerHeader

@Composable
fun DrawerEditProperties(component: Component, actionHandler: ActionHandler) {
    Column {
        DrawerEditPropertiesHeader(component)
        when (component) {
            is Component.Text -> TextProperties(component, actionHandler)
            is Component.Icon -> IconProperties(component, actionHandler)
            is Component.Group.Column -> ColumnProperties(component, actionHandler)
            is Component.Group.Row -> RowProperties(component, actionHandler)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DrawerEditPropertiesHeader(component: Component){
    val actionHandler = ActionHandlerAmbient.current
    DrawerHeader(title = component.name, onIconClick = { actionHandler.invoke(UserAction.Back) }) {
        Icon(asset = Icons.Default.ControlPointDuplicate)
    }
}

