package com.andb.apps.composesandbox.ui.sandbox.properties

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.andb.apps.composesandbox.data.model.*

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