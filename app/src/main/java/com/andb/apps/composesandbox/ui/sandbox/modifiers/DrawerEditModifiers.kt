package com.andb.apps.composesandbox.ui.sandbox.modifiers

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.PrototypeModifier
import com.andb.apps.composesandbox.data.model.toAll
import com.andb.apps.composesandbox.data.model.toIndividual
import com.andb.apps.composesandbox.data.model.toSides
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.Chip
import com.andb.apps.composesandbox.ui.sandbox.DrawerHeader
import com.andb.apps.composesandbox.ui.sandbox.properties.NumberPicker

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
                NumberPicker(label = "Padding", current = prototypeModifier.padding.value.toInt()) {
                    onEdit.invoke(prototypeModifier.copy(padding = it.dp))
                }
            }
            is PrototypeModifier.Padding.Sides -> {
                NumberPicker(label = "Horizontal Padding", current = prototypeModifier.horizontal.value.toInt()) {
                    onEdit.invoke(prototypeModifier.copy(horizontal = it.dp))
                }
                NumberPicker(label = "Vertical Padding", current = prototypeModifier.vertical.value.toInt()) {
                    onEdit.invoke(prototypeModifier.copy(vertical = it.dp))
                }
            }
            is PrototypeModifier.Padding.Individual -> {
                NumberPicker(label = "Start Padding", current = prototypeModifier.start.value.toInt()) {
                    onEdit.invoke(prototypeModifier.copy(start = it.dp))
                }
                NumberPicker(label = "End Padding", current = prototypeModifier.end.value.toInt()) {
                    onEdit.invoke(prototypeModifier.copy(end = it.dp))
                }
                NumberPicker(label = "Top Padding", current = prototypeModifier.top.value.toInt()) {
                    onEdit.invoke(prototypeModifier.copy(top = it.dp))
                }
                NumberPicker(label = "Bottom Padding", current = prototypeModifier.bottom.value.toInt()) {
                    onEdit.invoke(prototypeModifier.copy(bottom = it.dp))
                }
            }
        }
    }
}

@Composable
private fun BorderModifierEditor(prototypeModifier: PrototypeModifier.Border, onEdit: (PrototypeModifier) -> Unit) {
    NumberPicker(label = "Stroke Width", current = prototypeModifier.strokeWidth.value.toInt()) {
        onEdit.invoke(prototypeModifier.copy(strokeWidth = it.dp))
    }
    NumberPicker(label = "Corner Radius", current = prototypeModifier.cornerRadius.value.toInt()) {
        onEdit.invoke(prototypeModifier.copy(cornerRadius = it.dp))
    }
}

@Composable
private fun WidthModifierEditor(prototypeModifier: PrototypeModifier.Width, onEdit: (PrototypeModifier) -> Unit) {
    NumberPicker(label = "Width", current = prototypeModifier.width.value.toInt()) {
        onEdit.invoke(prototypeModifier.copy(width = it.dp))
    }
}

@Composable
private fun HeightModifierEditor(prototypeModifier: PrototypeModifier.Height, onEdit: (PrototypeModifier) -> Unit) {
    NumberPicker(label = "Width", current = prototypeModifier.height.value.toInt()) {
        onEdit.invoke(prototypeModifier.copy(height = it.dp))
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