package com.andb.apps.composesandbox.ui.test

import androidx.compose.foundation.layout.Column
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.andb.apps.composesandbox.ui.sandbox.drawer.properties.TextPicker


@Composable
fun TestScreen() {
    val fieldContents = remember { mutableStateOf("") }
    Column {
        OutlinedTextField(value = fieldContents.value, onValueChange = { fieldContents.value = it })
        TextPicker(label = "Picker", value = fieldContents.value, onValueChange = { fieldContents.value = it })
    }
}

