package com.andb.apps.composesandbox.ui.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.InternalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@InternalLayoutApi
@Composable
fun SpacedColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalGravity: Alignment.Horizontal = Alignment.Start,
    innerPadding: Dp = 0.dp,
    children: @Composable ColumnScope.() -> Unit
) {
    val spaced = object : Arrangement.Vertical {
        override val spacing: Dp
            get() = innerPadding

        override fun arrange(totalSize: Int, size: List<Int>, density: Density, outPosition: MutableList<Int>) = verticalArrangement.arrange(totalSize, size, density, outPosition)
    }
    Column(modifier, spaced, horizontalGravity) {

    }
}