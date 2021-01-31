package com.andb.apps.composesandbox.ui.sandbox.drawer.editproperties

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.toReadableString
import com.andb.apps.composesandboxdata.model.*

@Composable
fun TextProperties(component: PrototypeComponent.Text, onUpdate: (PrototypeComponent.Text) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextPicker(label = "Text", value = component.text) {
            onUpdate(component.copy(text = it))
        }
        NumberPicker(label = "Text Size", current = component.size) {
            onUpdate.invoke(component.copy(size = it))
        }
        OptionsPicker(label = "Font Weight", selected = component.weight, options = PrototypeComponent.Text.Weight.values().toList()) {
            onUpdate.invoke(component.copy(weight = it))
        }
        ColorPicker(label = "Text Color", current = component.color) {
            onUpdate.invoke(component.copy(color = it))
        }
    }
}

@OptIn(ExperimentalLayout::class)
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