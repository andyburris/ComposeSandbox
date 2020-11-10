package com.andb.apps.composesandbox.ui.test

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animate
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.drawLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.Theme
import com.andb.apps.composesandbox.ui.sandbox.tree.GenericTree
import com.andb.apps.composesandbox.ui.sandbox.tree.TreeConfig

data class ThemeColor(val name: String, val color: Color)

@Composable
fun TestScreen() {
    MaterialThemeTrees(theme = Theme(lightColors()), modifier = Modifier.padding(32.dp))
}

@Composable
fun MaterialThemeTrees(theme: Theme, modifier: Modifier = Modifier) {
    Column(modifier) {
        ColorThemeTree(
            parent = ThemeColor("Primary", theme.colors.primary),
            children = listOf(
                ThemeColor("Primary Variant", theme.colors.primaryVariant),
                ThemeColor("On Primary", theme.colors.onPrimary)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        ColorThemeTree(
            parent = ThemeColor("Secondary", theme.colors.secondary),
            children = listOf(
                ThemeColor("Secondary Variant", theme.colors.secondaryVariant),
                ThemeColor("On Secondary", theme.colors.onSecondary)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        ColorThemeTree(
            parent = ThemeColor("Background", theme.colors.background),
            children = listOf(
                ThemeColor("On Background", theme.colors.onBackground)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        ColorThemeTree(
            parent = ThemeColor("Surface", theme.colors.surface),
            children = listOf(
                ThemeColor("On Surface", theme.colors.onSurface)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        ThemeColorItem(themeColor = ThemeColor("Error", theme.colors.error)) {}
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ColorThemeTree(parent: ThemeColor, children: List<ThemeColor>, modifier: Modifier = Modifier) {
    val expanded = remember { mutableStateOf(false) }
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { expanded.value = !expanded.value }) {
            val rotation = animate(target = if (expanded.value) 0f else -90f)
            Icon(asset = Icons.Default.ExpandMore, tint = MaterialTheme.colors.onSecondary, modifier = Modifier.padding(end = 16.dp).drawLayer(rotationZ = rotation))
            ThemeColorItem(themeColor = parent) {}
        }
        AnimatedVisibility(visible = expanded.value) {
            GenericTree(items = children, treeConfig = TreeConfig(verticalPositionOnItem = 32.dp, verticalPaddingTop = 8.dp)) { themeColor ->
                ThemeColorItem(themeColor = themeColor, modifier = Modifier.padding(top = 16.dp, start = 8.dp)) {}
            }
        }
    }
}

@Composable
fun ThemeColorItem(themeColor: ThemeColor, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = themeColor.name)
        Box(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colors.secondaryVariant, CircleShape)
                .background(themeColor.color, CircleShape)
                .clip(CircleShape)
                .clickable(onClick = onClick)
                .size(32.dp)
        )
    }
}