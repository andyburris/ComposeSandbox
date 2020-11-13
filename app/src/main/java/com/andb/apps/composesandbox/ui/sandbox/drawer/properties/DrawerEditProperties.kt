package com.andb.apps.composesandbox.ui.sandbox.drawer.properties

import androidx.compose.material.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ControlPointDuplicate
import androidx.compose.runtime.Composable
import com.andb.apps.composesandbox.data.model.Properties
import com.andb.apps.composesandbox.data.model.PrototypeComponent
import com.andb.apps.composesandbox.state.ActionHandler
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.sandbox.drawer.DrawerHeader

@Composable
fun DrawerEditProperties(component: PrototypeComponent, actionHandler: ActionHandler, onUpdate: (PrototypeComponent) -> Unit) {
    Column {
        DrawerEditPropertiesHeader(component)
        when (component.properties) {
            is Properties.Text -> TextProperties(component.properties) {
                onUpdate(component.copy(properties = it))
            }
            is Properties.Icon -> IconProperties(component.properties) {
                onUpdate(component.copy(properties = it))
            }
            is Properties.Group.Column -> ColumnProperties(component.properties) {
                onUpdate(component.copy(properties = it))
            }
            is Properties.Group.Row -> RowProperties(component.properties) {
                onUpdate(component.copy(properties = it))
            }
        }
        ModifiersEditor(
            modifiers = component.modifiers,
            onAdd = { actionHandler.invoke(UserAction.OpenModifierList) },
            onOpenModifier = { actionHandler.invoke(UserAction.EditModifier(it.id))},
            onUpdate = {
                onUpdate(component.copy(modifiers = it))
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DrawerEditPropertiesHeader(component: PrototypeComponent){
    val actionHandler = ActionHandlerAmbient.current
    DrawerHeader(title = component.name, onIconClick = { actionHandler.invoke(UserAction.Back) }) {
        Icon(asset = Icons.Default.ControlPointDuplicate)
    }
}

