package com.andb.apps.composesandbox.ui.sandbox.drawer.addcomponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.icon
import com.andb.apps.composesandbox.data.model.name
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.sandbox.drawer.DrawerHeader
import com.andb.apps.composesandbox.ui.sandbox.drawer.ScrollableDrawer
import com.andb.apps.composesandbox.util.gridItems
import com.andb.apps.composesandboxdata.model.*
import java.util.*


private val separated = allComponents.partition { it is PrototypeComponent.Text || it is PrototypeComponent.Icon || it is PrototypeComponent.Group || it is PrototypeComponent.Slotted.Scaffold }
private val commonComponents = separated.first
private val otherComponents = separated.second

@Composable
fun ComponentList(project: Project, currentTreeID: String, title: String = "Add Component", requiresLongClick: Boolean, onSelect: (PrototypeComponent) -> Unit) {
    ScrollableDrawer(
        header = {
            ComponentListHeader(title)
        }
    ) {
        val searchTerm = savedInstanceState { "" }

        Column(modifier = Modifier.padding(horizontal = 32.dp), verticalArrangement = Arrangement.spacedBy(16.dp),) {

            AddComponentHeader(text = "Common Components")
            gridItems(commonComponents, rowArrangement = Arrangement.spacedBy(16.dp)) { component ->
                AddComponentItem(
                    component = component,
                    modifier = Modifier.weight(1f),
                    requiresLongClick = requiresLongClick,
                    onSelect = { onSelect.invoke(it.copy(id = UUID.randomUUID().toString())) }
                )
            }

            AddComponentHeader(text = "Custom Components")
            gridItems(project.trees.filter { it.treeType == TreeType.Component }, rowArrangement = Arrangement.spacedBy(16.dp)) { tree ->
                val component = PrototypeComponent.Custom(tree.id)
                AddComponentItem(
                    component = component,
                    modifier = Modifier.weight(1f),
                    requiresLongClick = requiresLongClick,
                    enabled = tree.id != currentTreeID && !tree.component.containsCustomComponent(currentTreeID),
                    onSelect = { onSelect.invoke(component.copy(treeID = tree.id, id = UUID.randomUUID().toString())) }
                )
            }

            AddComponentHeader(text = "Other Components")
            gridItems(otherComponents, rowArrangement = Arrangement.spacedBy(16.dp)) { component ->
                AddComponentItem(
                    component = component,
                    modifier = Modifier.weight(1f),
                    requiresLongClick = requiresLongClick,
                    onSelect = { onSelect.invoke(it.copy(id = UUID.randomUUID().toString())) }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AddComponentHeader(text: String, modifier: Modifier = Modifier) {
    Text(text = text.toUpperCase(), style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.primary, modifier = modifier.padding(start = 0.dp, end = 32.dp, top = 8.dp))
}

@Composable
private fun AddComponentItem(component: PrototypeComponent, modifier: Modifier = Modifier, enabled: Boolean = true, requiresLongClick: Boolean, onSelect: (PrototypeComponent) -> Unit) {
    Column(
        modifier = modifier
            .graphicsLayer(alpha = if (enabled) 1.0f else 0.5f)
            //.draggable(Orientation.Horizontal, onDragStarted = { onSelect.invoke(component) }) {},
            .shadow(2.dp, shape = RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.background, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable(onLongClick = { if (requiresLongClick) onSelect.invoke(component) }, onClick = { if (!requiresLongClick) onSelect.invoke(component) })
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = component.icon, contentDescription = null, tint = MaterialTheme.colors.onSecondary)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = component.name, style = MaterialTheme.typography.subtitle1)
            Text(text = component.documentation, style = MaterialTheme.typography.body2, color = MaterialTheme.colors.onSecondary)
        }
    }
/*    val color = if (enabled) Color.Unspecified else MaterialTheme.colors.secondary
    ComponentItem(
        component = component,
        modifier = Modifier
                then (if (enabled) Modifier.clickable(onLongClick = { onSelect.invoke(component) }, onClick = {}) else Modifier)
            .padding(horizontal = 32.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = Pair(color, color)
    )*/

}

@Composable
private fun ComponentListHeader(title: String) {
    val actionHandler = ActionHandlerAmbient.current
    DrawerHeader(title = title, onIconClick = { actionHandler.invoke(UserAction.Back) })
}

private val PrototypeComponent.documentation
    get() = when (this) {
        is PrototypeComponent.Text -> "Displays text on the screen"
        is PrototypeComponent.Icon -> "Displays an icon on the screen"
        is PrototypeComponent.Group.Row -> "Arranges child components horizontally"
        is PrototypeComponent.Group.Column -> "Arranges child components vertically"
        is PrototypeComponent.Group.Box -> "Arranges child components overlapping each other"
        is PrototypeComponent.Slotted.TopAppBar -> "An app bar arranges the title and relevant actions on a screen"
        is PrototypeComponent.Slotted.BottomAppBar -> "An app bar arranges the title and relevant actions on a screen"
        is PrototypeComponent.Slotted.ExtendedFloatingActionButton -> "A FAB with text and an optional icon"
        is PrototypeComponent.Slotted.Scaffold -> "Arranges components in common layout positions app bars, FABs, and drawers"
        is PrototypeComponent.Custom -> ""
    }