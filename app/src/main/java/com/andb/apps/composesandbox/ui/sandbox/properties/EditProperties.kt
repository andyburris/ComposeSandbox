package com.andb.apps.composesandbox.ui.sandbox.properties

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.*
import com.andb.apps.composesandbox.state.ActionHandler
import com.andb.apps.composesandbox.state.UserAction

@Composable
fun TextProperties(component: Component.Text, actionHandler: ActionHandler) {
    Column {
        TextPropertyEditor(label = "Text", value = component.text) {
            actionHandler.invoke(UserAction.UpdateComponent(component.copy(text = it)))
        }
        ModifiersEditor(component = component, actionHandler)
    }
}

@Composable
fun IconProperties(component: Component.Icon, actionHandler: ActionHandler) {
    Column {
        OptionsPropertyEditor(label = "Icon", selected = null, options = listOf(), onValueChange = {})
        ModifiersEditor(component = component, actionHandler)
    }
}

val horizontalArrangements = listOf(Arrangement.Start, Arrangement.Center, Arrangement.End)
val verticalArrangements = listOf(Arrangement.Top, Arrangement.Center, Arrangement.Bottom)
val bothArrangements = listOf(Arrangement.SpaceBetween, Arrangement.SpaceEvenly, Arrangement.SpaceAround)
val verticalAlignments = listOf(Alignment.Top, Alignment.CenterVertically, Alignment.Bottom)
val horizontalAlignments = listOf(Alignment.Start, Alignment.CenterHorizontally, Alignment.End)

@OptIn(InternalLayoutApi::class)
fun Arrangement.Horizontal.toReadableString() = when (this) {
    Arrangement.Start -> "Start"
    Arrangement.Center -> "Center"
    Arrangement.End -> "End"
    Arrangement.SpaceBetween -> "Space Between"
    Arrangement.SpaceAround -> "Space Around"
    Arrangement.SpaceEvenly -> "Space Evenly"
    else -> ""
}

@OptIn(InternalLayoutApi::class)
fun Arrangement.Vertical.toReadableString() = when (this) {
    Arrangement.Top -> "Top"
    Arrangement.Center -> "Center"
    Arrangement.Bottom -> "Bottom"
    Arrangement.SpaceBetween -> "Space Between"
    Arrangement.SpaceAround -> "Space Around"
    Arrangement.SpaceEvenly -> "Space Evenly"
    else -> ""
}

fun Alignment.Vertical.toReadableString() = when (this){
    Alignment.Top -> "Top"
    Alignment.CenterVertically -> "Center Vertically"
    Alignment.Bottom -> "Bottom"
    else -> ""
}

fun Alignment.Horizontal.toReadableString() = when (this) {
    Alignment.Start -> "Start"
    Alignment.CenterHorizontally -> "Center Horizontally"
    Alignment.End -> "End"
    else -> ""
}

@Composable
fun ColumnProperties(component: Component.Group.Column, actionHandler: ActionHandler) {
    Column {
        OptionsPropertyEditor(label = "Vertical Arrangement", selected = Arrangement.Top, options = verticalArrangements + bothArrangements, stringify = { it.toReadableString() }) {
            actionHandler.invoke(UserAction.UpdateComponent(component.copy(verticalArrangement = it)))
        }
        OptionsPropertyEditor(label = "Horizontal Alignment", selected = Alignment.Start, options = horizontalAlignments, stringify = { it.toReadableString() }) {
            actionHandler.invoke(UserAction.UpdateComponent(component.copy(horizontalAlignment = it)))
        }
        ModifiersEditor(component = component, actionHandler)
    }
}

@Composable
fun RowProperties(component: Component.Group.Row, actionHandler: ActionHandler) {
    Column {
        OptionsPropertyEditor(label = "Horizontal Arrangement", selected = component.horizontalArrangement, options = horizontalArrangements + bothArrangements, stringify = { it.toReadableString() }) {
            actionHandler.invoke(UserAction.UpdateComponent(component.copy(horizontalArrangement = it)))
        }
        OptionsPropertyEditor(label = "Vertical Alignment", selected = component.verticalAlignment, options = verticalAlignments, stringify = { it.toReadableString() }) {
            actionHandler.invoke(UserAction.UpdateComponent(component.copy(verticalAlignment = it)))
        }
        ModifiersEditor(component = component, actionHandler)
    }
}

@Composable
private fun ModifiersEditor(component: Component, actionHandler: ActionHandler) {
    val dialogShowing = remember { mutableStateOf(false) }
    Column {
        GenericPropertyEditor(label = "Modifiers") {
            Row {
                Icon(
                    asset = Icons.Default.Info,
                    tint = MaterialTheme.colors.onSecondary,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable { dialogShowing.value = true }
                )
                Icon(
                    asset = Icons.Default.Add,
                    tint = MaterialTheme.colors.onSecondary,
                    modifier = Modifier.clickable { actionHandler.invoke(UserAction.OpenModifierList(component)) }
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .background(Color.Black.copy(alpha = .12f), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
        ) {
            component.modifiers.forEach {
                ModifierItem(prototypeModifier = it, modifier = Modifier.clickable { actionHandler.invoke(UserAction.EditModifier(component, it))})
            }
        }
    }
    ModifierInfoDialog(showing = dialogShowing.value, onDismiss = { dialogShowing.value = false })
}

@Composable
private fun ModifierInfoDialog(showing: Boolean, onDismiss: () -> Unit) {
    if (showing) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = onDismiss, elevation = 0.dp, backgroundColor = Color.Transparent) {
                    Text("OK", color = MaterialTheme.colors.primary)
                }
            },
            title = { Text(text = "Modifiers") },
            text = {
                Text(
                    text = "Modifiers allow you to tweak how a component is presented. \n" +
                            "\n" +
                            "Examples include Padding, Border, Background, Size, Width, Height, and more.\n" +
                            "\n" +
                            "You can chain multiple modifiers together to apply multiple decorations to components. Each modifier in the chain wraps the next (i.e. a Padding before a Background will give a margin to the background color, but vice versa will result in a background color that has expanded to the size of the padding)"
                )
            }
        )
    }
}

@Composable
fun ModifierItem(prototypeModifier: PrototypeModifier, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalGravity = Alignment.CenterVertically
    ) {
        Row(verticalGravity = Alignment.CenterVertically) {
            Icon(asset = prototypeModifier.icon)
            Text(text = prototypeModifier.name, modifier = Modifier.padding(start = 16.dp))
        }
        Text(text = prototypeModifier.summary)
    }
}

@Composable
private fun TextPropertyEditor(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(text = label) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp))
}


@Composable
private fun <T> OptionsPropertyEditor(label: String, selected: T, options: List<T>, stringify: (T) -> String = { it.toString() }, onValueChange: (T) -> Unit) {
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