package com.andb.apps.composesandbox.ui.sandbox.drawer.editmodifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.state.LocalActionHandler
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.Chip
import com.andb.apps.composesandbox.ui.sandbox.drawer.DrawerHeader
import com.andb.apps.composesandbox.ui.sandbox.drawer.editproperties.ColorPicker
import com.andb.apps.composesandbox.ui.sandbox.drawer.editproperties.NumberPicker
import com.andb.apps.composesandboxdata.model.*

@Composable
fun DrawerEditModifiers(prototypeModifier: PrototypeModifier, onEdit: (PrototypeModifier) -> Unit) {
    val actionHandler = LocalActionHandler.current
    Column() {
        DrawerHeader(title = prototypeModifier.name, screenName = "Edit Modifier".uppercase(), onIconClick = { actionHandler.invoke(UserAction.Back) })
        Column(Modifier.padding(horizontal = 32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            when (prototypeModifier) {
                is PrototypeModifier.Padding -> PaddingModifierEditor(prototypeModifier = prototypeModifier, onEdit)
                is PrototypeModifier.Border -> BorderModifierEditor(prototypeModifier = prototypeModifier, onEdit)
                is PrototypeModifier.Background -> BackgroundModifierEditor(prototypeModifier = prototypeModifier, onEdit)
                is PrototypeModifier.Width -> WidthModifierEditor(prototypeModifier = prototypeModifier, onEdit = onEdit)
                is PrototypeModifier.Height -> HeightModifierEditor(prototypeModifier = prototypeModifier, onEdit = onEdit)
                is PrototypeModifier.Size -> SizeModifierEditor(prototypeModifier = prototypeModifier, onEdit = onEdit)
                is PrototypeModifier.FillMaxWidth -> NoOptions()
                is PrototypeModifier.FillMaxHeight -> NoOptions()
                is PrototypeModifier.FillMaxSize -> NoOptions()
            }
        }
    }
}

@Composable
private fun PaddingModifierEditor(prototypeModifier: PrototypeModifier.Padding, onEdit: (PrototypeModifier) -> Unit) {
    Row {
        Chip(
            label = "All",
            selected = prototypeModifier is PrototypeModifier.Padding.All,
            modifier = Modifier.padding(end = 8.dp).clickable { onEdit(prototypeModifier.toAll()) }
        )
        Chip(
            label = "Sides",
            selected = prototypeModifier is PrototypeModifier.Padding.Sides,
            modifier = Modifier.padding(end = 8.dp).clickable { onEdit(prototypeModifier.toSides()) }
        )
        Chip(
            label = "Individual",
            selected = prototypeModifier is PrototypeModifier.Padding.Individual,
            modifier = Modifier.clickable { onEdit(prototypeModifier.toIndividual()) }
        )
    }
    when (prototypeModifier) {
        is PrototypeModifier.Padding.All -> {
            NumberPicker(label = "Padding", current = prototypeModifier.padding) {
                onEdit.invoke(prototypeModifier.copy(padding = it))
            }
        }
        is PrototypeModifier.Padding.Sides -> {
            NumberPicker(label = "Horizontal Padding", current = prototypeModifier.horizontal) {
                onEdit.invoke(prototypeModifier.copy(horizontal = it))
            }
            NumberPicker(label = "Vertical Padding", current = prototypeModifier.vertical) {
                onEdit.invoke(prototypeModifier.copy(vertical = it))
            }
        }
        is PrototypeModifier.Padding.Individual -> {
            NumberPicker(label = "Start Padding", current = prototypeModifier.start) {
                onEdit.invoke(prototypeModifier.copy(start = it))
            }
            NumberPicker(label = "End Padding", current = prototypeModifier.end) {
                onEdit.invoke(prototypeModifier.copy(end = it))
            }
            NumberPicker(label = "Top Padding", current = prototypeModifier.top) {
                onEdit.invoke(prototypeModifier.copy(top = it))
            }
            NumberPicker(label = "Bottom Padding", current = prototypeModifier.bottom) {
                onEdit.invoke(prototypeModifier.copy(bottom = it))
            }
        }
    }
}

@Composable
private fun BorderModifierEditor(prototypeModifier: PrototypeModifier.Border, onEdit: (PrototypeModifier) -> Unit) {
    ColorPicker(label = "Stroke Color", current = prototypeModifier.color) {
        onEdit.invoke(prototypeModifier.copy(color = it))
    }
    NumberPicker(label = "Stroke Width", current = prototypeModifier.strokeWidth) {
        onEdit.invoke(prototypeModifier.copy(strokeWidth = it))
    }
    NumberPicker(label = "Corner Radius", current = prototypeModifier.cornerRadius) {
        onEdit.invoke(prototypeModifier.copy(cornerRadius = it))
    }
}

@Composable
private fun WidthModifierEditor(prototypeModifier: PrototypeModifier.Width, onEdit: (PrototypeModifier) -> Unit) {
    NumberPicker(label = "Width", current = prototypeModifier.width) {
        onEdit.invoke(prototypeModifier.copy(width = it))
    }
}

@Composable
private fun HeightModifierEditor(prototypeModifier: PrototypeModifier.Height, onEdit: (PrototypeModifier) -> Unit) {
    NumberPicker(label = "Height", current = prototypeModifier.height) {
        onEdit.invoke(prototypeModifier.copy(height = it))
    }
}

@Composable
private fun BackgroundModifierEditor(prototypeModifier: PrototypeModifier.Background, onEdit: (PrototypeModifier) -> Unit) {
    ColorPicker(label = "Background Color", current = prototypeModifier.color) {
        onEdit.invoke(prototypeModifier.copy(color = it))
    }
    NumberPicker(label = "Corner Radius", current = prototypeModifier.cornerRadius) {
        onEdit.invoke(prototypeModifier.copy(cornerRadius = it))
    }
}

@Composable
fun SizeModifierEditor(prototypeModifier: PrototypeModifier.Size, onEdit: (PrototypeModifier) -> Unit) {
    Row {
        Chip(
            label = "All",
            selected = prototypeModifier is PrototypeModifier.Size.All,
            modifier = Modifier.padding(end = 8.dp).clickable { onEdit(prototypeModifier.toAll()) }
        )
        Chip(
            label = "Individual",
            selected = prototypeModifier is PrototypeModifier.Size.Individual,
            modifier = Modifier.padding(end = 8.dp).clickable { onEdit(prototypeModifier.toIndividual()) }
        )
    }
    when(prototypeModifier) {
        is PrototypeModifier.Size.Individual -> {
            NumberPicker(label = "Width", current = prototypeModifier.width) {
                onEdit.invoke(prototypeModifier.copy(width = it))
            }
            NumberPicker(label = "Height", current = prototypeModifier.height) {
                onEdit.invoke(prototypeModifier.copy(height = it))
            }
        }
        is PrototypeModifier.Size.All -> NumberPicker(label = "Size", current = prototypeModifier.size) {
            onEdit.invoke(prototypeModifier.copy(size = it))
        }
    }
}

@Composable
private fun NoOptions() {
    Text(
        text = "No options available",
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    )
}