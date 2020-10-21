package com.andb.apps.composesandbox.ui.sandbox.properties

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                Row(verticalGravity = Alignment.CenterVertically, modifier = Modifier.clickable { opened.value = true }) {
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
fun NumberPicker(label: String, current: Int, onValueChange: (Int) -> Unit) {
    GenericPropertyEditor(label = label) {
        Column(
            modifier = Modifier
                .width(108.dp)
                .background(
                    MaterialTheme.colors.secondary,
                    shape = RoundedCornerShape(topLeft = 8.dp, topRight = 8.dp)
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalGravity = Alignment.CenterVertically
            ) {
                Icon(
                    asset = Icons.Default.Remove,
                    tint = MaterialTheme.colors.onSecondary,
                    modifier = Modifier.clickable { onValueChange.invoke(current - 1) }.padding(8.dp)
                )
                Text(text = current.toString())
                Icon(
                    asset = Icons.Default.Add,
                    tint = MaterialTheme.colors.onSecondary,
                    modifier = Modifier.clickable { onValueChange.invoke(current + 1) }.padding(8.dp)
                )
            }
            Box(backgroundColor = MaterialTheme.colors.onSecondary, modifier = Modifier.fillMaxWidth().height(1.dp))
        }
    }
}

@Composable
fun GenericPropertyEditor(label: String, modifier: Modifier = Modifier, widget: @Composable() () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalGravity = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 16.dp)
    ) {
        Text(label)
        widget()
    }
}