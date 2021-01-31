package com.andb.apps.composesandbox.ui.sandbox.drawer.editproperties

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ControlPointDuplicate
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.name
import com.andb.apps.composesandbox.state.ActionHandler
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.DrawerScreen
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.sandbox.drawer.DrawerHeader
import com.andb.apps.composesandbox.ui.sandbox.drawer.toShadow
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.ComponentItem
import com.andb.apps.composesandboxdata.model.PrototypeComponent

@Composable
fun DrawerEditProperties(component: PrototypeComponent, isBaseComponent: Boolean, actionHandler: ActionHandler, onExtractComponent: (PrototypeComponent) -> Unit, onUpdate: (PrototypeComponent) -> Unit,) {
    val scrollState = rememberScrollState()
    Column {
        DrawerEditPropertiesHeader(
            component,
            isBaseComponent = isBaseComponent,
            modifier = Modifier
                .shadow(scrollState.toShadow())
                .background(AmbientElevationOverlay.current?.apply(color = MaterialTheme.colors.surface, elevation = AmbientAbsoluteElevation.current + scrollState.toShadow())
                    ?: MaterialTheme.colors.surface),
            onExtractToComponent = { onExtractComponent.invoke(component) }
        )
        ScrollableColumn(scrollState = scrollState, modifier = Modifier.padding(horizontal = 32.dp), verticalArrangement = Arrangement.spacedBy(32.dp)) {
            if (isBaseComponent) {
                BaseComponentSwitcher(component = component) {
                    actionHandler.invoke(UserAction.OpenDrawerScreen(DrawerScreen.PickBaseComponent))
                }
            }
            when (component) {
                is PrototypeComponent.Text -> TextProperties(component) { onUpdate(it) }
                is PrototypeComponent.Icon -> IconProperties(component) { onUpdate(it) }
                is PrototypeComponent.Group.Column -> ColumnProperties(component) { onUpdate(it) }
                is PrototypeComponent.Group.Row -> RowProperties(component) { onUpdate(it) }
                is PrototypeComponent.Group.Box -> {}
                is PrototypeComponent.Slotted.TopAppBar -> TopAppBarProperties(component) { onUpdate(it) }
                is PrototypeComponent.Slotted.BottomAppBar -> BottomAppBarProperties(component) { onUpdate(it) }
                is PrototypeComponent.Slotted.ExtendedFloatingActionButton -> ExtendedFloatingActionButtonProperties(component) { onUpdate(it) }
                is PrototypeComponent.Slotted.Scaffold -> ScaffoldProperties(component) { onUpdate(it) }
                is PrototypeComponent.Custom -> {}
            }
            ModifiersEditor(
                modifiers = component.modifiers,
                onAdd = { actionHandler.invoke(UserAction.OpenDrawerScreen(DrawerScreen.AddModifier)) },
                onOpenModifier = { actionHandler.invoke(UserAction.OpenDrawerScreen(DrawerScreen.EditModifier(it.id))) },
                onUpdate = {
                    onUpdate(component.copy(modifiers = it))
                }
            )
        }
    }
}

@Composable
private fun BaseComponentSwitcher(component: PrototypeComponent, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "BASE COMPONENT", style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.primary)
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onClick).fillMaxWidth()) {
            ComponentItem(component = component)
            Icon(imageVector = Icons.Default.KeyboardArrowRight)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DrawerEditPropertiesHeader(component: PrototypeComponent, isBaseComponent: Boolean, modifier: Modifier = Modifier, onExtractToComponent: () -> Unit){
    val actionHandler = ActionHandlerAmbient.current
    DrawerHeader(title = component.name, screenName = (if (isBaseComponent) "Edit Base Component" else "Edit Component").toUpperCase(), modifier = modifier, onIconClick = { actionHandler.invoke(UserAction.Back) }) {
        IconButton(onClick = onExtractToComponent) {
            Icon(imageVector = Icons.Default.ControlPointDuplicate)
        }
    }
}

