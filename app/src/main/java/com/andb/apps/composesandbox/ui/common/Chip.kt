package com.andb.apps.composesandbox.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun Chip(
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    selected: Boolean = false,
    backgroundColor: Color = if (selected) MaterialTheme.colors.onSecondary else Color.Transparent,
    borderColor: Color = if (selected) Color.Transparent else MaterialTheme.colors.onSecondary,
    textColor: Color = if (selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSecondary
) {
    Row(
        modifier = modifier
            .background(backgroundColor, CircleShape)
            .border(1.dp, borderColor, CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.padding(end = 12.dp))
        }
        Text(text = label, color = textColor)
    }
}