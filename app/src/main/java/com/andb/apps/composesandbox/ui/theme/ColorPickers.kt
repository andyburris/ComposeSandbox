package com.andb.apps.composesandbox.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andb.apps.composecolorpicker.ui.ColorPickerLayout
import com.andb.apps.composecolorpicker.ui.MaterialPalette
import com.andb.apps.composesandbox.data.model.projectColor
import com.andb.apps.composesandbox.data.model.toPrototypeColor
import com.andb.apps.composesandbox.ui.common.ProjectTheme
import com.andb.apps.composesandboxdata.model.PrototypeColor

enum class ColorTabs {
    Theme, Picker, Palette,
}

@Composable
fun ColorPickerWithTheme(current: PrototypeColor, modifier: Modifier = Modifier, onSelect: (PrototypeColor) -> Unit) {
    val currentTab = remember { mutableStateOf(ColorTabs.Theme) }
    Column(modifier) {
        TabRow(selectedTabIndex = currentTab.value.ordinal, backgroundColor = MaterialTheme.colors.background) {
            Tab(selected = currentTab.value == ColorTabs.Theme, onClick = { currentTab.value = ColorTabs.Theme }) {
                Text(text = "Theme".uppercase(), modifier = Modifier.padding(vertical = 16.dp))
            }
            Tab(selected = currentTab.value == ColorTabs.Picker, onClick = { currentTab.value = ColorTabs.Picker }) {
                Text(text = "Picker".uppercase(), modifier = Modifier.padding(vertical = 16.dp))
            }
            Tab(selected = currentTab.value == ColorTabs.Palette, onClick = { currentTab.value = ColorTabs.Palette }) {
                Text(text = "Palette".uppercase(), modifier = Modifier.padding(vertical = 16.dp))
            }
        }

        when (currentTab.value) {
            ColorTabs.Theme -> ThemeColorsPicker(ProjectTheme.colors, current) { onSelect.invoke(it) }
            ColorTabs.Picker -> ColorPickerLayout(selected = current.projectColor(), modifier = Modifier.padding(24.dp)) { onSelect.invoke(it.toPrototypeColor()) }
            ColorTabs.Palette -> {
                MaterialTheme(typography = MaterialTheme.typography.copy(overline = MaterialTheme.typography.overline.copy(letterSpacing = 0.sp))) {
                    MaterialPalette (selected = current.projectColor(), modifier = Modifier.padding(24.dp)) {
                        onSelect.invoke(it.toPrototypeColor())
                    }
                }
            }
        }
    }
}

@Composable
fun ColorPickerWithoutTheme(current: Color, onSelect: (Color) -> Unit) {
    val currentTab = remember { mutableStateOf(ColorTabs.Picker) }
    Column {
        TabRow(
            selectedTabIndex = if (currentTab.value == ColorTabs.Picker) 0 else 1,
            backgroundColor = MaterialTheme.colors.background
        ) {
            Tab(
                selected = currentTab.value == ColorTabs.Picker,
                onClick = { currentTab.value = ColorTabs.Picker },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "PICKER")
            }
            Tab(
                selected = currentTab.value == ColorTabs.Palette,
                onClick = { currentTab.value = ColorTabs.Palette },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "PALETTE")
            }
        }
        when (currentTab.value) {
            ColorTabs.Picker -> ColorPickerLayout(selected = current, modifier = Modifier.padding(24.dp), onSelect = onSelect)
            ColorTabs.Palette -> {
                MaterialTheme(typography = MaterialTheme.typography.copy(overline = MaterialTheme.typography.overline.copy(letterSpacing = 0.sp))) {
                    MaterialPalette(selected = current, modifier = Modifier.padding(24.dp), onSelect = onSelect)
                }
            }
        }
    }
}