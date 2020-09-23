package com.andb.apps.composesandbox.ui.sandbox.properties

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ControlPointDuplicate
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.Component
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction

@Composable
fun DrawerEditProperties(component: Component) {
    Column {
        DrawerEditPropertiesHeader(component)
        when (component) {
            is Component.Text -> TextProperties(component)
            is Component.Icon -> IconProperties(component)
            is Component.Group.Column -> ColumnProperties(component)
            is Component.Group.Row -> RowProperties(component)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DrawerEditPropertiesHeader(component: Component){
    Row(
        verticalGravity = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(32.dp).fillMaxWidth()
    ) {
        Row(verticalGravity = Alignment.CenterVertically) {
            val actionHandler = ActionHandlerAmbient.current
            Icon(
                asset = Icons.Default.ArrowBack,
                modifier = Modifier.clickable { actionHandler.invoke(UserAction.Back) }
            )
            Text(
                text = component.name,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Icon(asset = Icons.Default.ControlPointDuplicate)
    }
}

