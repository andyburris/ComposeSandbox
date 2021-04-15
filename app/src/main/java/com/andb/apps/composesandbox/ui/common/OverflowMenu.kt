package com.andb.apps.composesandbox.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun OverflowMenu(icon: ImageVector = Icons.Default.MoreVert, content: @Composable ColumnScope.() -> Unit) {
    val menuShowing = remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { menuShowing.value = true }) {
            Icon(imageVector = icon, contentDescription = "Open Overflow")
        }
    }
    DropdownMenu(
        expanded = menuShowing.value,
        onDismissRequest = { menuShowing.value = false },
        content = content,
        modifier = Modifier.width(196.dp)
    )
}

@Composable
fun MenuItem(icon: ImageVector, title: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    DropdownMenuItem(onClick = { if (enabled) onClick.invoke() }, modifier = modifier.graphicsLayer(alpha = if (enabled) 1f else 0.25f)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colors.onSecondary)
            Text(text = title, style = MaterialTheme.typography.body1)
        }
    }
}
