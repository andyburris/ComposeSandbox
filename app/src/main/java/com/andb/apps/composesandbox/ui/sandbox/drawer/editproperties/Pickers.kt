package com.andb.apps.composesandbox.ui.sandbox.drawer.editproperties

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andb.apps.composesandbox.data.model.projectColor
import com.andb.apps.composesandbox.ui.common.ColorPickerCircle
import com.andb.apps.composesandbox.ui.common.ColorPickerWithTheme
import com.andb.apps.composesandbox.util.bottomBorder
import com.andb.apps.composesandbox.util.isDark
import com.andb.apps.composesandboxdata.model.PrototypeColor
import com.andb.apps.composesandboxdata.model.Slot

@Composable
fun TextPicker(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(text = label) }, modifier = Modifier.fillMaxWidth())
}


@Composable
fun <T> OptionsPicker(label: String, selected: T, options: List<T>, stringify: (T) -> String = { it.toString() }, onValueChange: (T) -> Unit) {
    val opened = remember { mutableStateOf(false) }
    GenericPropertyEditor(label) {
        DropdownMenu(
            toggle = {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { opened.value = true }) {
                    Text(text = stringify(selected))
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Open Dropdown")
                }
            },
            expanded = opened.value,
            onDismissRequest = { opened.value = false }
        ) {
            for (option in options) {
                DropdownMenuItem(onClick = { onValueChange.invoke(option); }) {
                    Text(text = stringify(option))
                }
            }
        }
    }
}

@Composable
fun NumberPicker(label: String, current: Int, minValue: Int = 0, maxValue: Int = Int.MAX_VALUE, onValueChange: (Int) -> Unit) {
    GenericPropertyEditor(label = label) {
        val dragged = remember { mutableStateOf(Pair(current, 0f)) }
        Row(
            modifier = Modifier
                .preferredWidth(96.dp)
                .bottomBorder(1.dp, MaterialTheme.colors.onSecondary)
                .background(
                    MaterialTheme.colors.secondary,
                    shape = RoundedCornerShape(topLeft = 8.dp, topRight = 8.dp)
                )
                .draggable(
                    orientation = Orientation.Vertical,
                    onDragStopped = {
                        dragged.value = Pair(current, 0f)
                    },
                    onDrag = { delta ->
                        dragged.value = dragged.value.copy(second = dragged.value.second - delta) // minus since dragging down is a positive delta, but should make numbers go down
                        val numbersDragged = dragged.value.second.toDp().value
                        onValueChange.invoke((dragged.value.first + numbersDragged).toInt().coerceIn(minValue..maxValue))
                    }
                )
                .draggable(
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        dragged.value = Pair(current, 0f)
                    },
                    onDrag = { delta ->
                        dragged.value = dragged.value.copy(second = dragged.value.second + delta) // minus since dragging down is a positive delta, but should make numbers go down
                        val numbersDragged = dragged.value.second.toDp().value
                        onValueChange.invoke((dragged.value.first + numbersDragged).toInt().coerceIn(minValue..maxValue))
                    }
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrement",
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary),
                modifier = Modifier.clickable { onValueChange.invoke((current - 1).coerceIn(minValue..maxValue)) }.padding(8.dp).size(16.dp)
            )
            Text(text = current.toString())
            Image(
                imageVector = Icons.Default.Add,
                contentDescription = "Increment",
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary),
                modifier = Modifier.clickable { onValueChange.invoke((current + 1).coerceIn(minValue..maxValue)) }.padding(8.dp).size(16.dp)
            )
        }
    }
}

@Composable
fun SwitchPicker(label: String, current: Boolean, onToggle: (Boolean) -> Unit) {
    GenericPropertyEditor(label = label) {
        Switch(checked = current, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary))
    }
}

@Composable
fun GenericPropertyEditor(label: String, modifier: Modifier = Modifier, widget: @Composable() RowScope.() -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.height(32.dp).fillMaxWidth()
    ) {
        Text(label)
        widget()
    }
}

@Composable
fun ColorPicker(label: String, current: PrototypeColor, modifier: Modifier = Modifier, onSelect: (PrototypeColor) -> Unit) {
    val pickingColor = remember { mutableStateOf(false) }
    GenericPropertyEditor(label = label, modifier) {
        Box {
            ColorPickerCircle(color = current.projectColor()) {
                pickingColor.value = true
            }
            if (current is PrototypeColor.ThemeColor) {
                Image(
                    imageVector = Icons.Default.Link,
                    contentDescription = "Linked to Theme",
                    modifier = Modifier.align(Alignment.Center).size(20.dp),
                    colorFilter = ColorFilter.tint(if (current.projectColor().isDark()) Color.White else Color.Black))
            }
        }
    }
    if (pickingColor.value) {
        AlertDialog(
            onDismissRequest = { pickingColor.value = false },
            title = { Text(text = "Pick Color") },
            text = {
                ColorPickerWithTheme(current = current, onSelect = onSelect)
            },
            buttons = {
                Button(onClick = { pickingColor.value = false }) {
                    Text(text = "Select")
                }
            },
        )
        Dialog(onDismissRequest = { pickingColor.value = false }) {
            Column(Modifier.background(MaterialTheme.colors.background, RoundedCornerShape(16.dp))) {
                Text(text = "Pick Color", style = MaterialTheme.typography.h6, modifier = Modifier.padding(32.dp))
                ColorPickerWithTheme(current = current, onSelect = onSelect)
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { pickingColor.value = false }) {
                        Text(text = "Select".toUpperCase())
                    }
                }
            }
        }
    }
}

@Composable
fun SlotPicker(slot: Slot, onUpdate: (Slot) -> Unit, children: (@Composable ColumnScope.() -> Unit)? = null) {
    PickerWithChildren(
        childrenExpanded = slot.enabled,
        parent = {
            SwitchPicker(label = slot.name, current = slot.enabled) { onUpdate.invoke(slot.copy(enabled = it)) }
        },
        children = children
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SlotPicker(name: String, enabled: Boolean, onToggle: (Boolean) -> Unit, children: (@Composable ColumnScope.() -> Unit)? = null) {
    PickerWithChildren(
        childrenExpanded = enabled,
        parent = {
            SwitchPicker(label = name, current = enabled, onToggle = onToggle)
        },
        children = children
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PickerWithChildren(childrenExpanded: Boolean, parent: @Composable () -> Unit, children: (@Composable ColumnScope.() -> Unit)? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        parent()
        if (children != null) {
            AnimatedVisibility(visible = childrenExpanded) {
                Column(Modifier.background(MaterialTheme.colors.secondary, RoundedCornerShape(8.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top), content = children)
            }
        }
    }
}

