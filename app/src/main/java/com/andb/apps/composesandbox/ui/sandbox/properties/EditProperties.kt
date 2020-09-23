package com.andb.apps.composesandbox.ui.sandbox.properties

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.Component

@Composable
fun TextProperties(component: Component.Text) {
    Column {
        TextPropertyEditor(label = "Text", value = component.text, onValueChange = {})
    }
}

@Composable
fun IconProperties(component: Component.Icon) {
    Column {
        OptionsPropertyEditor(label = "Icon", selected = null, options = listOf(), onValueChange = {})
    }
}

@Composable
fun ColumnProperties(component: Component.Group.Column) {
    Column {
        OptionsPropertyEditor(label = "Vertical Arrangment", selected = null, options = listOf(), onValueChange = {})
        OptionsPropertyEditor(label = "Horizontal Alignment", selected = null, options = listOf(), onValueChange = {})
    }
}

@Composable
fun RowProperties(component: Component.Group.Row) {
    Column {
        OptionsPropertyEditor(label = "Horizontal Arrangment", selected = null, options = listOf(), onValueChange = {})
        OptionsPropertyEditor(label = "Vertical Alignment", selected = null, options = listOf(), onValueChange = {})
    }
}

@Composable
private fun TextPropertyEditor(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(text = label) })
}


@Composable
private fun <T> OptionsPropertyEditor(label: String, selected: T, options: List<T>, onValueChange: (T) -> Unit) {
    PropertyEditor(label) {

    }
}

@Composable
fun PropertyEditor(label: String, modifier: Modifier = Modifier, widget: @Composable() () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalGravity = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 16.dp)
    ) {
        Text(label)
        widget()
    }
}