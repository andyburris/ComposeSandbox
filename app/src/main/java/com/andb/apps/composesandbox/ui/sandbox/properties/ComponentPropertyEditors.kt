package com.andb.apps.composesandbox.ui.sandbox.properties

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.InternalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.andb.apps.composesandbox.data.model.Properties

@Composable
fun TextProperties(properties: Properties.Text, onUpdate: (Properties.Text) -> Unit) {
    Column {
        TextPicker(label = "Text", value = properties.text) {
            onUpdate(properties.copy(text = it))
        }
    }
}

@Composable
fun IconProperties(properties: Properties.Icon, onUpdate: (Properties.Icon) -> Unit) {
    Column {
        OptionsPicker(label = "Icon", selected = null, options = listOf(), onValueChange = {})
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
fun ColumnProperties(properties: Properties.Group.Column, onUpdate: (Properties.Group.Column) -> Unit) {
    Column {
        OptionsPicker(label = "Vertical Arrangement", selected = properties.verticalArrangement, options = verticalArrangements + bothArrangements, stringify = { it.toReadableString() }) {
            val newProperties = properties.copy(verticalArrangement = it)
            onUpdate(newProperties)
        }
        OptionsPicker(label = "Horizontal Alignment", selected = properties.horizontalAlignment, options = horizontalAlignments, stringify = { it.toReadableString() }) {
            val newProperties = properties.copy(horizontalAlignment = it)
            onUpdate(newProperties)
        }
    }
}

@Composable
fun RowProperties(properties: Properties.Group.Row, onUpdate: (Properties.Group.Row) -> Unit) {
    Column {
        OptionsPicker(label = "Horizontal Arrangement", selected = properties.horizontalArrangement, options = horizontalArrangements + bothArrangements, stringify = { it.toReadableString() }) {
            val newProperties = properties.copy(horizontalArrangement = it)
            onUpdate(newProperties)
        }
        OptionsPicker(label = "Vertical Alignment", selected = properties.verticalAlignment, options = verticalAlignments, stringify = { it.toReadableString() }) {
            val newProperties = properties.copy(verticalAlignment = it)
            onUpdate(newProperties)
        }
    }
}