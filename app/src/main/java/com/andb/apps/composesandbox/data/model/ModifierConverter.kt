package com.andb.apps.composesandbox.data.model

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.R
import com.andb.apps.composesandboxdata.model.PrototypeModifier

val PrototypeModifier.icon: ImageVector
    @Composable get() = when (this) {
        is PrototypeModifier.Padding -> Icons.Default.FlipToFront
        is PrototypeModifier.Border -> Icons.Default.BorderStyle
        is PrototypeModifier.Background -> Icons.Default.FormatPaint
        is PrototypeModifier.Height -> Icons.Default.Height
        is PrototypeModifier.Width -> ImageVector.vectorResource(id = R.drawable.ic_width)
        is PrototypeModifier.Size -> Icons.Default.ZoomOutMap
        is PrototypeModifier.FillMaxWidth -> ImageVector.vectorResource(id = R.drawable.ic_max_width)
        is PrototypeModifier.FillMaxHeight -> Icons.Default.UnfoldMore
        is PrototypeModifier.FillMaxSize -> Icons.Default.Fullscreen
    }

@SuppressLint("ComposableModifierFactory", "ModifierFactoryExtensionFunction")
@Composable
fun List<PrototypeModifier>.toModifier() : Modifier {
    return this.fold<PrototypeModifier, Modifier>(Modifier) { acc, prototypeModifier ->
        acc then when (prototypeModifier) {
            is PrototypeModifier.Padding.Individual -> Modifier.padding(start = prototypeModifier.start.dp, end = prototypeModifier.end.dp, top = prototypeModifier.top.dp, bottom = prototypeModifier.bottom.dp)
            is PrototypeModifier.Padding.Sides -> Modifier.padding(horizontal = prototypeModifier.horizontal.dp, vertical = prototypeModifier.vertical.dp)
            is PrototypeModifier.Padding.All -> Modifier.padding(all = prototypeModifier.padding.dp)
            is PrototypeModifier.Border -> Modifier.border(prototypeModifier.strokeWidth.dp, prototypeModifier.color.renderColor(), shape = RoundedCornerShape(prototypeModifier.cornerRadius.dp))
            is PrototypeModifier.Background -> Modifier.background(prototypeModifier.color.renderColor(), shape = RoundedCornerShape(prototypeModifier.cornerRadius.dp))
            is PrototypeModifier.Height -> Modifier.height(prototypeModifier.height.dp)
            is PrototypeModifier.Width -> Modifier.width(prototypeModifier.width.dp)
            is PrototypeModifier.Size.Individual -> Modifier.size(width = prototypeModifier.width.dp, height = prototypeModifier.height.dp)
            is PrototypeModifier.Size.All -> Modifier.size(size = prototypeModifier.size.dp)
            is PrototypeModifier.FillMaxWidth -> Modifier.fillMaxWidth()
            is PrototypeModifier.FillMaxHeight -> Modifier.fillMaxHeight()
            is PrototypeModifier.FillMaxSize -> Modifier.fillMaxSize()
        }
    }
}
