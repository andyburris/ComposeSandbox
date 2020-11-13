package com.andb.apps.composesandbox.ui.sandbox.drawer

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.longPressGestureFilter
import androidx.compose.ui.layout.globalPosition
import androidx.compose.ui.onGloballyPositioned
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.Project
import com.andb.apps.composesandbox.data.model.Properties
import com.andb.apps.composesandbox.data.model.PrototypeComponent
import com.andb.apps.composesandbox.data.model.toComponent
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.DragDropAmbient
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.ComponentItem

@Composable
fun ComponentList(project: Project, onSelect: (PrototypeComponent) -> Unit) {
    Column {
        ComponentListHeader()
        val searchTerm = savedInstanceState { "" }

        AddComponentHeader(text = "Common Components")
        AddComponentItem(Properties.Text("Text").toComponent(), onSelect)
        AddComponentItem(Properties.Icon(Icons.Default.Image).toComponent(), onSelect)
        AddComponentItem(Properties.Group.Row(emptyList()).toComponent(), onSelect)
        AddComponentItem(Properties.Group.Column(emptyList()).toComponent(), onSelect)
    }
}

@Composable
fun AddComponentHeader(text: String) {
    Text(text = text.toUpperCase(), style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.primary, modifier = Modifier.padding(horizontal = 32.dp))
}

@Composable
private fun AddComponentItem(component: PrototypeComponent, onSelect: (PrototypeComponent) -> Unit) {
    val dragDropState = DragDropAmbient.current
    val density = DensityAmbient.current
    val globalPositionOffset = remember { mutableStateOf(Offset.Zero) }
    ComponentItem(
        component = component,
        modifier = Modifier
            .longPressGestureFilter {
                //dragDropState.dragPosition.value = it.toDpPosition(density) + globalPositionOffset.value.toDpPosition(density) - dragDropState.globalPosition.value
                onSelect.invoke(component)
            }
            .onGloballyPositioned {
                globalPositionOffset.value = it.globalPosition
            }
            .padding(horizontal = 32.dp, vertical = 8.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun ComponentListHeader() {

    Row(
        verticalAlignment = Alignment.CenterVertically,
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