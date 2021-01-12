package com.andb.apps.composesandbox.ui.sandbox.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.ComponentItem
import com.andb.apps.composesandboxdata.model.*
import java.util.*

@Composable
fun ComponentList(project: Project, currentTreeID: String,  onSelect: (PrototypeComponent) -> Unit) {
    ScrollableDrawer(
        header = {
            ComponentListHeader()
        },
        content = {
            val searchTerm = savedInstanceState { "" }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    AddComponentHeader(text = "Common Components")
                    allComponents.forEach { component ->
                        AddComponentItem(
                            component = component,
                            onSelect = { onSelect.invoke(component.copy(id = UUID.randomUUID().toString())) }
                        )
                    }
                }
                Column {
                    AddComponentHeader(text = "Custom Components")
                    project.trees.filter { it.treeType == TreeType.Component }.forEach { tree ->
                        val component = PrototypeComponent.Custom(treeID = tree.id)
                        val enabled = tree.id != currentTreeID && !tree.tree.containsCustomComponent(currentTreeID)
                        AddComponentItem(
                            component = component,
                            enabled = enabled,
                            onSelect = { onSelect.invoke(component.copy(treeID = tree.id, id = UUID.randomUUID().toString())) }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun AddComponentHeader(text: String) {
    Text(text = text.toUpperCase(), style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.primary, modifier = Modifier.padding(start = 32.dp, end = 32.dp, bottom = 8.dp))
}

@Composable
private fun AddComponentItem(component: PrototypeComponent, enabled: Boolean = true, onSelect: (PrototypeComponent) -> Unit) {
    val color = if (enabled) Color.Unspecified else MaterialTheme.colors.secondary
    ComponentItem(
        component = component,
        modifier = Modifier
                then (if (enabled) Modifier.clickable(onLongClick = { onSelect.invoke(component) }, onClick = {}) else Modifier)
            .padding(horizontal = 32.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = Pair(color, color)
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
            imageVector = Icons.Default.ArrowBack,
            modifier = Modifier.clickable { actionHandler.invoke(UserAction.Back) }
        )
        Text(
            text = "Add Component",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

private val PrototypeComponent.documentation get() = when (this) {
    is PrototypeComponent.Text -> "Displays text on the screen"
    is PrototypeComponent.Icon -> "Displays an icon on the screen"
    is PrototypeComponent.Group.Row -> "Arranges child components horizontally"
    is PrototypeComponent.Group.Column -> "Arranges child components vertically"
    is PrototypeComponent.Group.Box -> "Arranges child components overlapping each other"
    is PrototypeComponent.Slotted.TopAppBar -> "Arranges components in common layouts like app bars, FABs, and drawers"
    is PrototypeComponent.Slotted.BottomAppBar -> "Arranges the title and relevant actions on a screen"
    is PrototypeComponent.Slotted.ExtendedFloatingActionButton -> "A FAB with text and an optional icon"
    is PrototypeComponent.Slotted.Scaffold -> "Arranges components in common layout positions app bars, FABs, and drawers"
    is PrototypeComponent.Custom -> ""
}