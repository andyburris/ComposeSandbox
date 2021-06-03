package com.andb.apps.composesandbox.ui.sandbox.drawer.editproperties

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LineWeight
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.projectStyle
import com.andb.apps.composesandbox.data.model.toReadableString
import com.andb.apps.composesandbox.ui.common.ProjectTheme
import com.andb.apps.composesandbox.util.divider
import com.andb.apps.composesandbox.util.onBackgroundSecondary
import com.andb.apps.composesandbox.util.overlay
import com.andb.apps.composesandboxdata.model.*

@Composable
fun TextProperties(component: PrototypeComponent.Text, onUpdate: (PrototypeComponent.Text) -> Unit) {
    val projectTypography = ProjectTheme.type
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextPicker(label = "Text", value = component.text) {
            onUpdate(component.copy(text = it))
        }
        GenericPropertyEditor(label = "Text Style") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                ToggleIcon(
                    icon = Icons.Default.LineWeight,
                    contentDescription = "Toggle Inherit Weight",
                    enabled = component.weight == null,
                    onToggle = {
                        val newWeight = if (component.weight == null) component.style.projectStyle(projectTypography).fontWeight else null
                        onUpdate.invoke(component.copy(weight = newWeight))
                    }
                )
                ToggleIcon(
                    icon = Icons.Default.TextFields,
                    contentDescription = "Toggle Inherit Size",
                    enabled = component.size == null,
                    onToggle = {
                        val newSize = if (component.size == null) component.style.projectStyle(projectTypography).fontSize else null
                        onUpdate.invoke(component.copy(size = newSize))
                    }
                )
                Dropdown(
                    selected = component.style,
                    options = ProjectTheme.type.items().values,
                    stringify = { it.name },
                    onValueChange = {
                        onUpdate.invoke(component.copy(style = it))
                    }
                )
            }
        }
        if (component.size != null) {
            NumberPicker(label = "Font Size", current = component.size!!) {
                onUpdate.invoke(component.copy(size = it))
            }
        }
        if (component.weight != null) {
            OptionsPicker(label = "Font Weight", selected = component.weight!!, options = fontWeights.toList(), stringify = { it.name }) {
                onUpdate.invoke(component.copy(weight = it))
            }
        }
        ColorPicker(label = "Text Color", current = component.color) {
            onUpdate.invoke(component.copy(color = it))
        }
    }
}

@Composable
private fun ToggleIcon(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    val backgroundColor = if (enabled) MaterialTheme.colors.overlay else Color.Transparent
    val borderColor = if (enabled) Color.Transparent else MaterialTheme.colors.divider
    Box(
        modifier = modifier
            .border(1.dp, borderColor, CircleShape)
            .background(backgroundColor, CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onToggle)
            .size(32.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) MaterialTheme.colors.onBackgroundSecondary else MaterialTheme.colors.divider,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun IconProperties(component: PrototypeComponent.Icon, onUpdate: (PrototypeComponent.Icon) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        IconPicker(icon = component.icon){
            onUpdate.invoke(component.copy(icon = it))
        }
        ColorPicker(label = "Icon Tint", current = component.tint) {
            onUpdate.invoke(component.copy(tint = it))
        }
    }
}


@Composable
fun ColumnProperties(component: PrototypeComponent.Group.Column, onUpdate: (PrototypeComponent.Group.Column) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PickerWithChildren(
            childrenExpanded = component.verticalArrangement is PrototypeArrangement.Vertical.SpacedBy,
            parent = {
                OptionsPicker(label = "Vertical Arrangement", selected = component.verticalArrangement, options = verticalArrangements + bothArrangements, stringify = { it.toReadableString() }) {
                    val newProperties = component.copy(verticalArrangement = it)
                    onUpdate(newProperties)
                }
            },
            children = {
                val arrangement = component.verticalArrangement
                if (arrangement is PrototypeArrangement.Vertical.SpacedBy) {
                    NumberPicker(label = "Spacing", current = arrangement.space) {
                        val newProperties = component.copy(verticalArrangement = arrangement.copy(space = it))
                        onUpdate(newProperties)
                    }
                    OptionsPicker(label = "Alignment", selected = arrangement.alignment, options = verticalAlignments, stringify = { it.toReadableString() }) {
                        val newProperties = component.copy(verticalArrangement = arrangement.copy(alignment = it))
                        onUpdate(newProperties)
                    }
                }
            }
        )
        OptionsPicker(label = "Horizontal Alignment", selected = component.horizontalAlignment, options = horizontalAlignments, stringify = { it.toReadableString() }) {
            val newProperties = component.copy(horizontalAlignment = it)
            onUpdate(newProperties)
        }
    }
}

@Composable
fun RowProperties(component: PrototypeComponent.Group.Row, onUpdate: (PrototypeComponent.Group.Row) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PickerWithChildren(
            childrenExpanded = component.horizontalArrangement is PrototypeArrangement.Horizontal.SpacedBy,
            parent = {
                OptionsPicker(label = "Horizontal Arrangement", selected = component.horizontalArrangement, options = horizontalArrangements + bothArrangements, stringify = { it.toReadableString() }) {
                    val newProperties = component.copy(horizontalArrangement = it)
                    onUpdate(newProperties)
                }
            },
            children = {
                val arrangement = component.horizontalArrangement
                if (arrangement is PrototypeArrangement.Horizontal.SpacedBy) {
                    NumberPicker(label = "Spacing", current = arrangement.space) {
                        val newProperties = component.copy(horizontalArrangement = arrangement.copy(space = it))
                        onUpdate(newProperties)
                    }
                    OptionsPicker(label = "Alignment", selected = arrangement.alignment, options = horizontalAlignments, stringify = { it.toReadableString() }) {
                        val newProperties = component.copy(horizontalArrangement = arrangement.copy(alignment = it))
                        onUpdate(newProperties)
                    }
                }
            }
        )
        OptionsPicker(label = "Vertical Alignment", selected = component.verticalAlignment, options = verticalAlignments, stringify = { it.toReadableString() }) {
            val newProperties = component.copy(verticalAlignment = it)
            onUpdate(newProperties)
        }
    }
}

@Composable
fun TopAppBarProperties(component: PrototypeComponent.Slotted.TopAppBar, onUpdate: (PrototypeComponent.Slotted.TopAppBar) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ColorPicker(label = "Background Color", current = component.backgroundColor) {
            onUpdate.invoke(component.copy(backgroundColor = it))
        }
        NumberPicker(label = "Elevation", current = component.elevation) {
            onUpdate.invoke(component.copy(elevation = it))
        }
        SlotPicker(
            slot = component.slots.navigationIcon,
            onUpdate = {
                val newSlots = component.slots.copy(navigationIcon = it)
                onUpdate.invoke(component.copy(slots = newSlots))
            }
        )
    }
}

@Composable
fun BottomAppBarProperties(component: PrototypeComponent.Slotted.BottomAppBar, onUpdate: (PrototypeComponent.Slotted.BottomAppBar) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ColorPicker(label = "Background Color", current = component.backgroundColor) {
            onUpdate.invoke(component.copy(backgroundColor = it))
        }
        NumberPicker(label = "Elevation", current = component.elevation) {
            onUpdate.invoke(component.copy(elevation = it))
        }
    }
}

@Composable
fun ExtendedFloatingActionButtonProperties(component: PrototypeComponent.Slotted.ExtendedFloatingActionButton, onUpdate: (PrototypeComponent.Slotted.ExtendedFloatingActionButton) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ColorPicker(label = "Background Color", current = component.backgroundColor) {
            onUpdate.invoke(component.copy(backgroundColor = it))
        }
        NumberPicker(label = "Default Elevation", current = component.defaultElevation) {
            onUpdate.invoke(component.copy(defaultElevation = it))
        }
        NumberPicker(label = "Pressed Elevation", current = component.pressedElevation) {
            onUpdate.invoke(component.copy(pressedElevation = it))
        }
        SlotPicker(
            slot = component.slots.icon,
            onUpdate = {
                val newSlots = component.slots.copy(icon = it)
                onUpdate.invoke(component.copy(slots = newSlots))
            }
        )
    }
}

@Composable
fun ScaffoldProperties(component: PrototypeComponent.Slotted.Scaffold, onUpdate: (PrototypeComponent.Slotted.Scaffold) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ColorPicker(label = "Background Color", current = component.backgroundColor) {
            onUpdate.invoke(component.copy(backgroundColor = it))
        }
        SlotPicker(
            slot = component.slots.drawer,
            onUpdate = {
                val newSlots = component.slots.copy(drawer = it)
                onUpdate.invoke(component.copy(slots = newSlots))
            }
        ) {
            NumberPicker(label = "Drawer Elevation", current = component.drawerElevation) {
                onUpdate.invoke(component.copy(drawerElevation = it))
            }
            ColorPicker(label = "Drawer Background Color", current = component.drawerBackgroundColor) {
                onUpdate.invoke(component.copy(drawerBackgroundColor = it))
            }
        }
        SlotPicker(
            slot = component.slots.topBar,
            onUpdate = {
                val newSlots = component.slots.copy(topBar = it)
                onUpdate.invoke(component.copy(slots = newSlots))
            }
        )
        SlotPicker(
            slot = component.slots.bottomBar,
            onUpdate = {
                val newSlots = component.slots.copy(bottomBar = it)
                onUpdate.invoke(component.copy(slots = newSlots))
            }
        )
        SlotPicker(
            slot = component.slots.floatingActionButton,
            onUpdate = {
                val newSlots = component.slots.copy(floatingActionButton = it)
                onUpdate.invoke(component.copy(slots = newSlots))
            }
        ) {
            OptionsPicker(
                label = "FAB Position",
                selected = component.floatingActionButtonPosition,
                options = PrototypeComponent.Slotted.Scaffold.FabPosition.values().toList(),
                onValueChange = { onUpdate.invoke(component.copy(floatingActionButtonPosition = it)) }
            )
            SwitchPicker(
                label = "Dock FAB",
                current = component.isFloatingActionButtonDocked,
                onToggle = { onUpdate.invoke(component.copy(isFloatingActionButtonDocked = it)) }
            )
        }
    }
}