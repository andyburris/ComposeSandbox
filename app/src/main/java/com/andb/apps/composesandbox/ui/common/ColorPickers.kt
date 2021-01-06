package com.andb.apps.composesandbox.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.andb.apps.composecolorpicker.ui.ExpandedColorPicker
import com.andb.apps.composecolorpicker.ui.MaterialPalette
import com.andb.apps.composesandbox.data.model.name
import com.andb.apps.composesandbox.data.model.projectColor
import com.andb.apps.composesandbox.data.model.toColor
import com.andb.apps.composesandbox.data.model.toPrototypeColor
import com.andb.apps.composesandboxdata.model.PrototypeColor
import com.andb.apps.composesandboxdata.model.Theme
import com.andb.apps.composesandboxdata.model.getColor
import com.andb.apps.composesandboxdata.model.updateColor
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.GenericTree
import com.andb.apps.composesandbox.ui.sandbox.drawer.tree.TreeConfig
import com.andb.apps.composesandbox.util.isDark
import androidx.compose.foundation.layout.Box as LayoutBox

@Composable
fun MaterialThemeEditor(theme: Theme, modifier: Modifier = Modifier, onSelect: (Theme) -> Unit) {
    val picking = remember { mutableStateOf<PrototypeColor.ThemeColor?>(null) }

    MaterialThemeTrees(theme = theme, picked = null, modifier = modifier) {
        picking.value = it
    }

    val pickingColor = picking.value
    if (pickingColor != null) {
        Dialog(onDismissRequest = { picking.value = null }) {
            Column(Modifier.background(MaterialTheme.colors.background, RoundedCornerShape(16.dp))) {
                Text(text = "Pick Color", style = MaterialTheme.typography.h6, modifier = Modifier.padding(32.dp))
                ColorPickerWithoutTheme(current = theme.getColor(pickingColor).toColor()) {
                    onSelect.invoke(theme.updateColor(pickingColor, it.toPrototypeColor()))
                }
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { picking.value = null }) {
                        Text(text = "Select".toUpperCase())
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialThemePicker(current: PrototypeColor, modifier: Modifier = Modifier, onSelect: (PrototypeColor) -> Unit) {
    MaterialThemeTrees(
        theme = ProjectTheme,
        picked = current as? PrototypeColor.ThemeColor, // only show selected if it is a linked theme color
        modifier = modifier,
        onClick = onSelect
    )
}

@Composable
private fun MaterialThemeTrees(theme: Theme, picked: PrototypeColor.ThemeColor?, modifier: Modifier = Modifier, onClick: (PrototypeColor.ThemeColor) -> Unit) {
    Column(modifier) {
        ColorThemeTree(
            parent = PrototypeColor.ThemeColor.Primary,
            children = listOf(
                PrototypeColor.ThemeColor.PrimaryVariant,
                PrototypeColor.ThemeColor.OnPrimary,
            ),
            picked = picked,
            modifier = Modifier.padding(bottom = 16.dp),
            onClick = onClick
        )
        ColorThemeTree(
            parent = PrototypeColor.ThemeColor.Secondary,
            children = listOf(
                PrototypeColor.ThemeColor.SecondaryVariant,
                PrototypeColor.ThemeColor.OnSecondary,
            ),
            picked = picked,
            modifier = Modifier.padding(bottom = 16.dp),
            onClick = onClick
        )
        ColorThemeTree(
            parent = PrototypeColor.ThemeColor.Background,
            children = listOf(
                PrototypeColor.ThemeColor.OnBackground,
            ),
            picked = picked,
            modifier = Modifier.padding(bottom = 16.dp),
            onClick = onClick
        )
        ColorThemeTree(
            parent = PrototypeColor.ThemeColor.Surface,
            children = listOf(
                PrototypeColor.ThemeColor.OnSurface,
            ),
            picked = picked,
            modifier = Modifier.padding(bottom = 16.dp),
            onClick = onClick
        )
        ThemeColorItem(color = PrototypeColor.ThemeColor.Error, picked = picked == PrototypeColor.ThemeColor.Error) {
            onClick.invoke(PrototypeColor.ThemeColor.Error)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ColorThemeTree(parent: PrototypeColor.ThemeColor, picked: PrototypeColor.ThemeColor?, children: List<PrototypeColor.ThemeColor>, modifier: Modifier = Modifier, onClick: (PrototypeColor.ThemeColor) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { expanded.value = !expanded.value }) {
            val rotation = animate(target = if (expanded.value) 0f else -90f)
            Icon(imageVector = Icons.Default.ExpandMore, tint = MaterialTheme.colors.onSecondary, modifier = Modifier.padding(end = 16.dp).graphicsLayer(rotationZ = rotation))
            ThemeColorItem(color = parent, picked = picked == parent) {
                onClick.invoke(parent)
            }
        }
        AnimatedVisibility(visible = expanded.value) {
            GenericTree(items = children, treeConfig = TreeConfig(verticalPositionOnItem = 32.dp, verticalPaddingTop = 8.dp)) { themeColor ->
                ThemeColorItem(color = themeColor, picked = picked == themeColor, modifier = Modifier.padding(top = 16.dp, start = 8.dp)) {
                    onClick.invoke(themeColor)
                }
            }
        }
    }
}

@Composable
private fun ThemeColorItem(color: PrototypeColor.ThemeColor, picked: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = color.name)
        LayoutBox(
            modifier = Modifier,
            content = {
                ColorPickerCircle(color = color.projectColor(), onClick = onClick)
                if (picked) {
                    Icon(
                        imageVector = Icons.Default.Check.copy(defaultWidth = 20.dp, defaultHeight = 20.dp),
                        modifier = Modifier.align(Alignment.Center),
                        tint = if (color.projectColor().isDark()) Color.White else Color.Black
                    )
                }
            }
        )
    }

}

@Composable
fun ColorPickerCircle(color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    LayoutBox(
        modifier = modifier
            .border(1.dp, MaterialTheme.colors.secondaryVariant, CircleShape)
            .background(color, CircleShape)
            .clip(CircleShape)
            .clickable (onClick = onClick)
            .size(32.dp)
    )

}

enum class Tabs {
    PICKER, PALETTE, THEME
}

@Composable
fun ColorPickerWithTheme(current: PrototypeColor, modifier: Modifier = Modifier, onSelect: (PrototypeColor) -> Unit) {
    val currentTab = remember { mutableStateOf(Tabs.THEME) }
    Column(modifier) {
        TabRow(
            selectedTabIndex = when(currentTab.value) {
                Tabs.THEME -> 0
                Tabs.PICKER -> 1
                Tabs.PALETTE -> 2
            },
            backgroundColor = MaterialTheme.colors.background
        ) {
            Tab(
                selected = currentTab.value == Tabs.THEME,
                onClick = { currentTab.value = Tabs.THEME },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "THEME")
            }
            Tab(
                selected = currentTab.value == Tabs.PICKER,
                onClick = { currentTab.value = Tabs.PICKER },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "PICKER")
            }
            Tab(
                selected = currentTab.value == Tabs.PALETTE,
                onClick = { currentTab.value = Tabs.PALETTE },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "PALETTE")
            }
        }
        when (currentTab.value) {
            Tabs.THEME -> MaterialThemePicker(current, modifier = Modifier.padding(24.dp)) {
                onSelect.invoke(it)
            }
            Tabs.PICKER -> ExpandedColorPicker(selected = current.projectColor(), modifier = Modifier.padding(24.dp)) {
                onSelect.invoke(it.toPrototypeColor())
            }
            Tabs.PALETTE -> {
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
    val currentTab = remember { mutableStateOf(Tabs.PICKER) }
    Column {
        TabRow(
            selectedTabIndex = if (currentTab.value == Tabs.PICKER) 0 else 1,
            backgroundColor = MaterialTheme.colors.background
        ) {
            Tab(
                selected = currentTab.value == Tabs.PICKER,
                onClick = { currentTab.value = Tabs.PICKER },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "PICKER")
            }
            Tab(
                selected = currentTab.value == Tabs.PALETTE,
                onClick = { currentTab.value = Tabs.PALETTE },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "PALETTE")
            }
        }
        when (currentTab.value) {
            Tabs.PICKER -> ExpandedColorPicker(selected = current, modifier = Modifier.padding(24.dp), onSelect = onSelect)
            Tabs.PALETTE -> {
                MaterialTheme(typography = MaterialTheme.typography.copy(overline = MaterialTheme.typography.overline.copy(letterSpacing = 0.sp))) {
                    MaterialPalette(selected = current, modifier = Modifier.padding(24.dp), onSelect = onSelect)
                }
            }
        }
    }
}