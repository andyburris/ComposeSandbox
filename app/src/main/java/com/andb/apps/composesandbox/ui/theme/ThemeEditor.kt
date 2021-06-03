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
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandboxdata.model.Theme

enum class ThemeTabs {
    Colors, Typography
}

@Composable
fun MaterialThemeEditor(theme: Theme, modifier: Modifier = Modifier, onSelect: (Theme) -> Unit) {
    Column(modifier) {
        val selectedTab = remember { mutableStateOf(ThemeTabs.Colors) }
        TabRow(selectedTabIndex = selectedTab.value.ordinal, backgroundColor = MaterialTheme.colors.background) {
            Tab(selected = selectedTab.value == ThemeTabs.Colors, onClick = { selectedTab.value = ThemeTabs.Colors }) {
                Text(text = "Colors".uppercase(), modifier = Modifier.padding(vertical = 16.dp))
            }
            Tab(selected = selectedTab.value == ThemeTabs.Typography, onClick = { selectedTab.value = ThemeTabs.Typography }) {
                Text(text = "Typography".uppercase(), modifier = Modifier.padding(vertical = 16.dp))
            }
        }
        when(selectedTab.value) {
            ThemeTabs.Colors -> ThemeColorsEditor(themeColors = theme.colors) { onSelect.invoke(theme.copy(colors = it)) }
            ThemeTabs.Typography -> TypographyEditor(typography = theme.type) {
                println("updating type, new type = $it")
                onSelect.invoke(theme.copy(type = it))
            }
        }
    }
}
