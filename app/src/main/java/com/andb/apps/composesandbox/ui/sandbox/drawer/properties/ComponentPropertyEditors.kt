package com.andb.apps.composesandbox.ui.sandbox.drawer.properties

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.andb.apps.composesandbox.data.model.toReadableString
import com.andb.apps.composesandbox.model.*

@Composable
fun TextProperties(properties: Properties.Text, onUpdate: (Properties.Text) -> Unit) {
    Column {
        TextPicker(label = "Text", value = properties.text) {
            onUpdate(properties.copy(text = it))
        }
    }
}

@OptIn(ExperimentalLayout::class)
@Composable
fun IconProperties(properties: Properties.Icon, onUpdate: (Properties.Icon) -> Unit) {
    Column {
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
    Column {
        OptionsPicker(label = "Vertical Arrangement", selected = properties.verticalArrangement, options = verticalArrangements + bothArrangements, stringify = { it.toReadableString() }) {
            val newProperties = properties.copy(verticalArrangement = it)
            onUpdate(newProperties)
        }
        OptionsPicker(label = "Horizontal Alignment", selected = properties.horizontalAlignment, options = horizontalAlignments, stringify = { it.toReadableString() }) {
            val newProperties = properties.copy(horizontalAlignment = it)
            onUpdate(newProperties)
        }
        val number = remember { mutableStateOf(0) }
        NumberPicker(label = "Test", current = properties.number) {
            number.value = it
            onUpdate(properties.copy(number = it))
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