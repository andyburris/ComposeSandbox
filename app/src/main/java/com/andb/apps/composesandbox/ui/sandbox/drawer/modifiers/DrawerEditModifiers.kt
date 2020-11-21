package com.andb.apps.composesandbox.ui.sandbox.drawer.modifiers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.model.PrototypeModifier
import com.andb.apps.composesandbox.model.toAll
import com.andb.apps.composesandbox.model.toIndividual
import com.andb.apps.composesandbox.model.toSides
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.Chip
import com.andb.apps.composesandbox.ui.sandbox.drawer.DrawerHeader
import com.andb.apps.composesandbox.ui.sandbox.drawer.properties.ColorPicker
import com.andb.apps.composesandbox.ui.sandbox.drawer.properties.NumberPicker

@Composable
fun DrawerEditModifiers(prototypeModifier: PrototypeModifier, onEdit: (PrototypeModifier) -> Unit) {
    val actionHandler = ActionHandlerAmbient.current
    Column {
        DrawerHeader(title = "Edit Modifier", onIconClick = { actionHandler.invoke(UserAction.Back) })

        when (prototypeModifier) {
            is PrototypeModifier.Padding -> PaddingModifierEditor(prototypeModifier = prototypeModifier, onEdit)
            is PrototypeModifier.Border -> BorderModifierEditor(prototypeModifier = prototypeModifier, onEdit)
            is PrototypeModifier.Width -> WidthModifierEditor(prototypeModifier = prototypeModifier, onEdit = onEdit)
            is PrototypeModifier.Height -> HeightModifierEditor(prototypeModifier = prototypeModifier, onEdit = onEdit)
            is PrototypeModifier.FillMaxWidth -> NoOptions()
            is PrototypeModifier.FillMaxHeight -> NoOptions()
        }
    }
}

@Composable
private fun PaddingModifierEditor(prototypeModifier: PrototypeModifier.Padding, onEdit: (PrototypeModifier) -> Unit) {
    Column {
        Row(Modifier.padding(start = 32.dp)) {
            Chip(
                label = "All",
                selected = prototypeModifier is PrototypeModifier.Padding.All,
                modifier = Modifier.padding(end = 8.dp).clickable{ onEdit(prototypeModifier.toAll()) }
            )
            Chip(
                label = "Sides",
                selected = prototypeModifier is PrototypeModifier.Padding.Sides,
                modifier = Modifier.padding(end = 8.dp).clickable{ onEdit(prototypeModifier.toSides()) }
            )
            Chip(
                label = "Individual",
                selected = prototypeModifier is PrototypeModifier.Padding.Individual,
                modifier = Modifier.clickable{ onEdit(prototypeModifier.toIndividual()) }
            )
        }
        when(prototypeModifier) {
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
}

@Composable
private fun BorderModifierEditor(prototypeModifier: PrototypeModifier.Border, onEdit: (PrototypeModifier) -> Unit) {
    NumberPicker(label = "Stroke Width", current = prototypeModifier.strokeWidth) {
        onEdit.invoke(prototypeModifier.copy(strokeWidth = it))
    }
    NumberPicker(label = "Corner Radius", current = prototypeModifier.cornerRadius) {
        onEdit.invoke(prototypeModifier.copy(cornerRadius = it))
    }
    ColorPicker(label = "Stroke Color", current = prototypeModifier.color) {
        onEdit.invoke(prototypeModifier.copy(color = it))
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
    NumberPicker(label = "Width", current = prototypeModifier.height) {
        onEdit.invoke(prototypeModifier.copy(height = it))
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