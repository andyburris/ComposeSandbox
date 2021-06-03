package com.andb.apps.composesandbox.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andb.apps.composesandbox.data.model.name
import com.andb.apps.composesandbox.data.model.toColor
import com.andb.apps.composesandbox.data.model.toPrototypeColor
import com.andb.apps.composesandbox.util.isDark
import com.andb.apps.composesandboxdata.model.*

@Composable
fun ThemeColorsEditor(themeColors: ThemeColors, modifier: Modifier = Modifier, onSelect: (ThemeColors) -> Unit) {
    val picking = remember { mutableStateOf<PrototypeColor.ThemeColor?>(null) }

    ThemeColorsPicker(themeColors = themeColors, current = null, modifier = modifier) {
        picking.value = it
    }

    val pickingColor = picking.value
    if (pickingColor != null) {
        Dialog(onDismissRequest = { picking.value = null }) {
            Column(Modifier.background(MaterialTheme.colors.background, RoundedCornerShape(16.dp))) {
                Text(text = "Pick Color", style = MaterialTheme.typography.h6, modifier = Modifier.padding(32.dp))
                ColorPickerWithoutTheme(current = themeColors.getColor(pickingColor).toColor()) {
                    onSelect.invoke(themeColors.withThemeColor(pickingColor, it.toPrototypeColor()))
                }
                Row(Modifier
                    .fillMaxWidth()
                    .padding(16.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { picking.value = null }) {
                        Text(text = "Select".uppercase())
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeColorsPicker(themeColors: ThemeColors, current: PrototypeColor?, modifier: Modifier = Modifier, onSelect: (PrototypeColor.ThemeColor) -> Unit) {
    LazyColumn(modifier) {
        items(themeColors.items()) { (color, themeColor) ->
            ThemeColorItem(
                color = color,
                themeColor = themeColor,
                picked = current == themeColor,
                modifier = Modifier
                    .clickable { onSelect.invoke(themeColor) }
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            )
        }
    }
}

@Composable
private fun ThemeColorItem(color: PrototypeColor.FixedColor, themeColor: PrototypeColor.ThemeColor, picked: Boolean, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = themeColor.name)
        Box(
            modifier = Modifier,
            content = {
                ColorPickerCircle(color = color.toColor())
                if (picked) {
                    Image(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(20.dp),
                        colorFilter = ColorFilter.tint(if (color.toColor().isDark()) Color.White else Color.Black)
                    )
                }
            }
        )
    }
}

@Composable
fun ColorPickerCircle(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(1.dp, MaterialTheme.colors.secondaryVariant, CircleShape)
            .background(color, CircleShape)
            .clip(CircleShape)
            .size(32.dp)
    )

}