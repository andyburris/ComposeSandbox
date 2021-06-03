package com.andb.apps.composesandbox.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.toTextStyle
import com.andb.apps.composesandbox.ui.sandbox.drawer.editproperties.NumberPicker
import com.andb.apps.composesandbox.ui.sandbox.drawer.editproperties.OptionsPicker
import com.andb.apps.composesandbox.util.divider
import com.andb.apps.composesandbox.util.onBackgroundSecondary
import com.andb.apps.composesandboxdata.model.*

@Composable
fun TypographyEditor(typography: ThemeTypography, modifier: Modifier = Modifier, onSelect: (ThemeTypography) -> Unit) {
    val expanded = remember { mutableStateOf<PrototypeTextStyle.ThemeStyle?>(null) }
    LazyColumn(modifier) {
        items(typography.items().toList()) { (style, themeStyle) ->
            TextStyleEditor(
                style = style,
                themeStyle = themeStyle,
                expanded = expanded.value == themeStyle,
                onToggle = { if (expanded.value != themeStyle) expanded.value = themeStyle else expanded.value = null },
                onSelect = { onSelect.invoke(typography.withThemeStyle(themeStyle, it)) }
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TextStyleEditor(
    style: PrototypeTextStyle.FixedStyle,
    themeStyle: PrototypeTextStyle.ThemeStyle,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit,
    onSelect: (PrototypeTextStyle.FixedStyle) -> Unit
) {
    val padding = animateDpAsState(if (expanded) 8.dp else 0.dp).value
    val elevation = animateDpAsState(if (expanded) 4.dp else 0.dp).value
    Column(
        modifier = modifier
            .padding(padding)
            .shadow(elevation, RoundedCornerShape(padding))
            .background(MaterialTheme.colors.background, RoundedCornerShape(padding))
    ) {
        TextStyleHeader(
            style = themeStyle,
            expanded = expanded,
            modifier = Modifier
                .clickable(onClick = onToggle)
                .padding(horizontal = 32.dp - padding, vertical = 16.dp + padding)
        )
        AnimatedVisibility(visible = expanded) {
            TextStyleOptions(style = style, onSelect = onSelect, modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp))
        }
    }
}

@Composable
private fun TextStyleHeader(style: PrototypeTextStyle.ThemeStyle, expanded: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = style.name, style = MaterialTheme.typography.subtitle1)
        when (expanded) {
            true -> Icon(imageVector = Icons.Default.UnfoldLess, contentDescription = "Collapse")
            false -> Icon(imageVector = Icons.Default.UnfoldMore, contentDescription = "Expand")
        }
    }
}

@Composable
fun TextStyleOptions(style: PrototypeTextStyle.FixedStyle, modifier: Modifier = Modifier, onSelect: (PrototypeTextStyle.FixedStyle) -> Unit) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Preview".uppercase(), style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.onBackgroundSecondary)
            Text(
                text = "The quick brown fox jumped over the lazy dog",
                style = style.toTextStyle(),
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colors.divider, MaterialTheme.shapes.medium)
                    .horizontalScroll(rememberScrollState())
                    .padding(16.dp)

            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Options".uppercase(), style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.onBackgroundSecondary)
            OptionsPicker(label = "Font Weight", selected = style.fontWeight, options = fontWeights, stringify = { it.name }) {
                onSelect.invoke(style.copy(fontWeight = it))
            }
            NumberPicker(label = "Font Size", current = style.fontSize) {
                onSelect.invoke(style.copy(fontSize = it))
            }
        }
    }
}