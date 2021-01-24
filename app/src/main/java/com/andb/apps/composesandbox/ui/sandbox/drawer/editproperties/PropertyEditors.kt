package com.andb.apps.composesandbox.ui.sandbox.drawer.editproperties

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.toReadableString
import com.andb.apps.composesandboxdata.model.*

@Composable
fun TextProperties(properties: Properties.Text, onUpdate: (Properties.Text) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextPicker(label = "Text", value = properties.text) {
            onUpdate(properties.copy(text = it))
        }
        NumberPicker(label = "Text Size", current = properties.size) {
            onUpdate.invoke(properties.copy(size = it))
        }
        OptionsPicker(label = "Font Weight", selected = properties.weight, options = Properties.Text.Weight.values().toList()) {
            onUpdate.invoke(properties.copy(weight = it))
        }
        ColorPicker(label = "Text Color", current = properties.color) {
            onUpdate.invoke(properties.copy(color = it))
        }
    }
}

@OptIn(ExperimentalLayout::class)
@Composable
fun IconProperties(properties: Properties.Icon, onUpdate: (Properties.Icon) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        IconPicker(icon = properties.icon){
            onUpdate.invoke(properties.copy(icon = it))
        }
        ColorPicker(label = "Icon Tint", current = properties.tint) {
            onUpdate.invoke(properties.copy(tint = it))
        }
    }
}


@Composable
fun ColumnProperties(properties: Properties.Group.Column, onUpdate: (Properties.Group.Column) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PickerWithChildren(
            childrenExpanded = properties.verticalArrangement is PrototypeArrangement.Vertical.SpacedBy,
            parent = {
                OptionsPicker(label = "Vertical Arrangement", selected = properties.verticalArrangement, options = verticalArrangements + bothArrangements, stringify = { it.toReadableString() }) {
                    val newProperties = properties.copy(verticalArrangement = it)
                    onUpdate(newProperties)
                }
            },
            children = {
                val arrangement = properties.verticalArrangement
                if (arrangement is PrototypeArrangement.Vertical.SpacedBy) {
                    NumberPicker(label = "Spacing", current = arrangement.space) {
                        val newProperties = properties.copy(verticalArrangement = arrangement.copy(space = it))
                        onUpdate(newProperties)
                    }
                    OptionsPicker(label = "Alignment", selected = arrangement.alignment, options = verticalAlignments, stringify = { it.toReadableString() }) {
                        val newProperties = properties.copy(verticalArrangement = arrangement.copy(alignment = it))
                        onUpdate(newProperties)
                    }
                }
            }
        )
        OptionsPicker(label = "Horizontal Alignment", selected = properties.horizontalAlignment, options = horizontalAlignments, stringify = { it.toReadableString() }) {
            val newProperties = properties.copy(horizontalAlignment = it)
            onUpdate(newProperties)
        }
    }
}

@Composable
fun RowProperties(properties: Properties.Group.Row, onUpdate: (Properties.Group.Row) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PickerWithChildren(
            childrenExpanded = properties.horizontalArrangement is PrototypeArrangement.Horizontal.SpacedBy,
            parent = {
                OptionsPicker(label = "Horizontal Arrangement", selected = properties.horizontalArrangement, options = horizontalArrangements + bothArrangements, stringify = { it.toReadableString() }) {
                    val newProperties = properties.copy(horizontalArrangement = it)
                    onUpdate(newProperties)
                }
            },
            children = {
                val arrangement = properties.horizontalArrangement
                if (arrangement is PrototypeArrangement.Horizontal.SpacedBy) {
                    NumberPicker(label = "Spacing", current = arrangement.space) {
                        val newProperties = properties.copy(horizontalArrangement = arrangement.copy(space = it))
                        onUpdate(newProperties)
                    }
                    OptionsPicker(label = "Alignment", selected = arrangement.alignment, options = horizontalAlignments, stringify = { it.toReadableString() }) {
                        val newProperties = properties.copy(horizontalArrangement = arrangement.copy(alignment = it))
                        onUpdate(newProperties)
                    }
                }
            }
        )
        OptionsPicker(label = "Vertical Alignment", selected = properties.verticalAlignment, options = verticalAlignments, stringify = { it.toReadableString() }) {
            val newProperties = properties.copy(verticalAlignment = it)
            onUpdate(newProperties)
        }
    }
}

@Composable
fun TopAppBarProperties(properties: Properties.Slotted.TopAppBar, onUpdate: (Properties.Slotted.TopAppBar) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ColorPicker(label = "Background Color", current = properties.backgroundColor) {
            onUpdate.invoke(properties.copy(backgroundColor = it))
        }
        NumberPicker(label = "Elevation", current = properties.elevation) {
            onUpdate.invoke(properties.copy(elevation = it))
        }
        SlotPicker(name = "Navigation Icon", properties = properties, onSelect = onUpdate)
    }
}

@Composable
fun BottomAppBarProperties(properties: Properties.Slotted.BottomAppBar, onUpdate: (Properties.Slotted.BottomAppBar) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ColorPicker(label = "Background Color", current = properties.backgroundColor) {
            onUpdate.invoke(properties.copy(backgroundColor = it))
        }
        NumberPicker(label = "Elevation", current = properties.elevation) {
            onUpdate.invoke(properties.copy(elevation = it))
        }
    }
}

@Composable
fun ExtendedFloatingActionButtonProperties(properties: Properties.Slotted.ExtendedFloatingActionButton, onUpdate: (Properties.Slotted.ExtendedFloatingActionButton) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ColorPicker(label = "Background Color", current = properties.backgroundColor) {
            onUpdate.invoke(properties.copy(backgroundColor = it))
        }
        NumberPicker(label = "Default Elevation", current = properties.defaultElevation) {
            onUpdate.invoke(properties.copy(defaultElevation = it))
        }
        NumberPicker(label = "Pressed Elevation", current = properties.pressedElevation) {
            onUpdate.invoke(properties.copy(pressedElevation = it))
        }
        SlotPicker(
            name = "Icon",
            enabled = properties.slotsEnabled["Icon"] == true,
            onToggle = { onUpdate.invoke(properties.copy(slotsEnabled = properties.slotsEnabled + ("Icon" to it))) }
        )
    }
}

@Composable
fun ScaffoldProperties(properties: Properties.Slotted.Scaffold, onUpdate: (Properties.Slotted.Scaffold) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ColorPicker(label = "Background Color", current = properties.backgroundColor) {
            onUpdate.invoke(properties.copy(backgroundColor = it))
        }
        SlotPicker(
            name = "Drawer",
            enabled = properties.slotsEnabled.getValue("Drawer"),
            onToggle = { onUpdate.invoke(properties.copy(slotsEnabled = properties.slotsEnabled.plus("Drawer" to it))) }
        ) {
            NumberPicker(label = "Drawer Elevation", current = properties.drawerElevation) {
                onUpdate.invoke(properties.copy(drawerElevation = it))
            }
            ColorPicker(label = "Drawer Background Color", current = properties.drawerBackgroundColor) {
                onUpdate.invoke(properties.copy(drawerBackgroundColor = it))
            }
        }
        SlotPicker(
            name = "Top Bar",
            enabled = properties.slotsEnabled.getValue("Top Bar"),
            onToggle = { onUpdate.invoke(properties.copy(slotsEnabled = properties.slotsEnabled.plus("Top App Bar" to it)))}
        )
        SlotPicker(
            name = "Bottom Bar",
            enabled = properties.slotsEnabled.getValue("Bottom Bar"),
            onToggle = { onUpdate.invoke(properties.copy(slotsEnabled = properties.slotsEnabled.plus("Bottom App Bar" to it)))}
        )
        SlotPicker(
            name = "Floating Action Button",
            enabled = properties.slotsEnabled.getValue("Floating Action Button"),
            onToggle = { onUpdate.invoke(properties.copy(slotsEnabled = properties.slotsEnabled.plus("Floating Action Button" to it)))}
        ) {
            OptionsPicker(
                label = "FAB Position",
                selected = properties.floatingActionButtonPosition,
                options = Properties.Slotted.Scaffold.FabPosition.values().toList(),
                onValueChange = { onUpdate.invoke(properties.copy(floatingActionButtonPosition = it)) }
            )
            SwitchPicker(
                label = "Dock FAB",
                current = properties.isFloatingActionButtonDocked,
                onToggle = { onUpdate.invoke(properties.copy(isFloatingActionButtonDocked = it)) }
            )
        }
    }
}