package com.andb.apps.composesandbox.ui.sandbox.drawer.properties

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andb.apps.composesandbox.data.model.projectColor
import com.andb.apps.composesandbox.data.model.vectorAsset
import com.andb.apps.composesandbox.model.PrototypeColor
import com.andb.apps.composesandbox.model.PrototypeIcon
import com.andb.apps.composesandbox.model.icons
import com.andb.apps.composesandbox.ui.common.ColorPickerCircle
import com.andb.apps.composesandbox.ui.common.ColorPickerWithTheme
import com.andb.apps.composesandbox.util.isDark

@Composable
fun TextPicker(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(text = label) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp))
}


@Composable
fun <T> OptionsPicker(label: String, selected: T, options: List<T>, stringify: (T) -> String = { it.toString() }, onValueChange: (T) -> Unit) {
    val opened = remember { mutableStateOf(false) }
    GenericPropertyEditor(label) {
        DropdownMenu(
            toggle = {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { opened.value = true }) {
                    Text(text = stringify(selected))
                    Icon(asset = Icons.Default.ArrowDropDown)
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
        Column(
            modifier = Modifier
                .width(108.dp)
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
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    asset = Icons.Default.Remove.copy(defaultHeight = 16.dp, defaultWidth = 16.dp),
                    tint = MaterialTheme.colors.onSecondary,
                    modifier = Modifier.clickable { onValueChange.invoke((current - 1).coerceIn(minValue..maxValue)) }.padding(12.dp)
                )
                Text(text = current.toString())
                Icon(
                    asset = Icons.Default.Add.copy(defaultHeight = 16.dp, defaultWidth = 16.dp),
                    tint = MaterialTheme.colors.onSecondary,
                    modifier = Modifier.clickable { onValueChange.invoke((current + 1).coerceIn(minValue..maxValue)) }.padding(12.dp)
                )
            }
            Box(modifier = Modifier.background(MaterialTheme.colors.onSecondary).fillMaxWidth().height(1.dp))
        }
    }
}

@Composable
fun GenericPropertyEditor(label: String, modifier: Modifier = Modifier, widget: @Composable() () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 16.dp)
    ) {
        Text(label)
        widget()
    }
}

@Composable
fun ColorPicker(label: String, current: PrototypeColor, modifier: Modifier = Modifier, onSelect: (PrototypeColor)->Unit) {
    val pickingColor = remember { mutableStateOf(false) }
    GenericPropertyEditor(label = label, modifier) {
        Box {
            ColorPickerCircle(color = current.projectColor()) {
                pickingColor.value = true
            }
            if (current is PrototypeColor.ThemeColor) {
                Icon(
                    asset = Icons.Default.Link.copy(defaultWidth = 20.dp, defaultHeight = 20.dp),
                    modifier = Modifier.align(Alignment.Center),
                    tint = if (current.projectColor().isDark()) Color.White else Color.Black)
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
                ColorPickerWithTheme(current = current ,onSelect = onSelect)
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
fun IconPicker(icon: PrototypeIcon, onSelect: (PrototypeIcon) -> Unit) {
    val picking = remember { mutableStateOf(false) }
    GenericPropertyEditor(label = "Icon") {
        Row(
            modifier = Modifier
                .clickable { picking.value = true }
                .background(MaterialTheme.colors.secondary, CircleShape)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(asset = icon.vectorAsset)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = icon.name, color = MaterialTheme.colors.onSecondary)
        }
    }
    if(picking.value) {
        Dialog(onDismissRequest = { picking.value = false }) {
            Column(Modifier.background(MaterialTheme.colors.background, RoundedCornerShape(16.dp))) {
                Text(text = "Pick Icon", style = MaterialTheme.typography.h6, modifier = Modifier.padding(32.dp))
                IconPickerDialogContent(selected = icon, Modifier.padding(horizontal = 32.dp)) {
                    onSelect.invoke(it)
                    picking.value = false
                }
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { picking.value = false }) {
                        Text(text = "Select".toUpperCase())
                    }
                }
            }
        }
    }
}

@Composable
private fun IconPickerDialogContent(selected: PrototypeIcon, modifier: Modifier, onSelect: (PrototypeIcon) -> Unit) {
    val searchTerm = remember { mutableStateOf("") }

    Column(modifier) {
        OutlinedTextField(
            label = { Text(text = "Search Icons") },
            value = searchTerm.value,
            onValueChange = {
                searchTerm.value = it
            },
            modifier = Modifier.fillMaxWidth()
        )
        LazyColumn {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(icons) { iconSection ->
                Text(
                    text = iconSection.sectionName.toUpperCase(),
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                val rowSize = 3
                iconSection.icons.filter { searchTerm.value in it.name }.chunked(rowSize).forEach { icons ->
                    Row(Modifier.padding(bottom = 8.dp)) {
                        icons.forEach { icon ->
                            IconPickerItem(icon = icon, selected = icon == selected) {
                                onSelect.invoke(icon)
                            }
                        }
                        repeat(rowSize-icons.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.IconPickerItem(icon: PrototypeIcon, modifier: Modifier = Modifier, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(if (selected) MaterialTheme.colors.secondary else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .weight(1f)
            .padding(horizontal = 8.dp)
    ) {
        Icon(asset = icon.vectorAsset.copy(defaultHeight = 36.dp, defaultWidth = 36.dp))
        Text(text = icon.name, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

}