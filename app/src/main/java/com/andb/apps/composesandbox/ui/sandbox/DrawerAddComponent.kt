package com.andb.apps.composesandbox.ui.sandbox

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.Component
import com.andb.apps.composesandbox.data.model.Project
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.sandbox.tree.ComponentItem

@Composable
fun ComponentList(project: Project, onSelect: (Component) -> Unit) {
    Column {
        ComponentListHeader()
        val searchTerm = savedInstanceState { "" }

        AddComponentHeader(text = "Common Components")
        AddComponentItem(Component.Text("Text"), onSelect)
        AddComponentItem(Component.Icon(Icons.Default.Image), onSelect)
        AddComponentItem(Component.Group.Row(emptyList()), onSelect)
        AddComponentItem(Component.Group.Column(emptyList()), onSelect)
    }
}

@Composable
fun AddComponentHeader(text: String) {
    Text(text = text.toUpperCase(), style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.primary, modifier = Modifier.padding(horizontal = 32.dp))
}

@Composable
private fun AddComponentItem(component: Component, onSelect: (Component) -> Unit) {
    ComponentItem(
        component = component,
        modifier = Modifier
            .clickable(onLongClick = {onSelect.invoke(component)}){}
            .padding(horizontal = 32.dp, vertical = 8.dp)
            .fillMaxWidth()
    )
}

@Composable
fun ComponentListHeader() {

    Row(
        verticalGravity = Alignment.CenterVertically,
        modifier = Modifier.padding(32.dp).fillMaxWidth()
    ) {
        val actionHandler = ActionHandlerAmbient.current
        Icon(
            asset = Icons.Default.ArrowBack,
            modifier = Modifier.clickable { actionHandler.invoke(UserAction.Back) }
        )
        Text(
            text = "Add Component",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}