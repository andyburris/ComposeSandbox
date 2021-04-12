package com.andb.apps.composesandbox.ui.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun TestScreen() {
    val visible = remember { mutableStateOf(false) }
    Column(Modifier.padding(32.dp)) {
        Button(onClick = { visible.value = !visible.value }) {
            Text(text = "Toggle Visibility")
        }
        if(visible.value) {
            val progress = rememberSaveable { mutableStateOf(0f) }
            Slider(value = progress.value, onValueChange = { progress.value = it })
        }
    }
}

