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
    PICKER, PALETTE, THEME
}

@Composable
fun ColorPickerWithTheme(current: PrototypeColor, modifier: Modifier = Modifier, onSelect: (PrototypeColor) -> Unit) {
    val currentTab = remember { mutableStateOf(ColorTabs.THEME) }
    Column(modifier) {
        TabRow(
            selectedTabIndex = when(currentTab.value) {
                ColorTabs.THEME -> 0
                ColorTabs.PICKER -> 1
                ColorTabs.PALETTE -> 2
            },
            backgroundColor = MaterialTheme.colors.background
        ) {
            Tab(
                selected = currentTab.value == ColorTabs.THEME,
                onClick = { currentTab.value = ColorTabs.THEME },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "THEME")
            }
            Tab(
                selected = currentTab.value == ColorTabs.PICKER,
                onClick = { currentTab.value = ColorTabs.PICKER },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "PICKER")
            }
            Tab(
                selected = currentTab.value == ColorTabs.PALETTE,
                onClick = { currentTab.value = ColorTabs.PALETTE },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "PALETTE")
            }
        }
        when (currentTab.value) {
            ColorTabs.THEME -> ThemeColorsPicker(ProjectTheme.colors, current, modifier = Modifier.padding(24.dp)) { onSelect.invoke(it) }
            ColorTabs.PICKER -> ColorPickerLayout(selected = current.projectColor(), modifier = Modifier.padding(24.dp)) { onSelect.invoke(it.toPrototypeColor()) }
            ColorTabs.PALETTE -> {
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
    val currentTab = remember { mutableStateOf(ColorTabs.PICKER) }
    Column {
        TabRow(
            selectedTabIndex = if (currentTab.value == ColorTabs.PICKER) 0 else 1,
            backgroundColor = MaterialTheme.colors.background
        ) {
            Tab(
                selected = currentTab.value == ColorTabs.PICKER,
                onClick = { currentTab.value = ColorTabs.PICKER },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "PICKER")
            }
            Tab(
                selected = currentTab.value == ColorTabs.PALETTE,
                onClick = { currentTab.value = ColorTabs.PALETTE },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "PALETTE")
            }
        }
        when (currentTab.value) {
            ColorTabs.PICKER -> ColorPickerLayout(selected = current, modifier = Modifier.padding(24.dp), onSelect = onSelect)
            ColorTabs.PALETTE -> {
                MaterialTheme(typography = MaterialTheme.typography.copy(overline = MaterialTheme.typography.overline.copy(letterSpacing = 0.sp))) {
                    MaterialPalette(selected = current, modifier = Modifier.padding(24.dp), onSelect = onSelect)
                }
            }
        }
    }
}