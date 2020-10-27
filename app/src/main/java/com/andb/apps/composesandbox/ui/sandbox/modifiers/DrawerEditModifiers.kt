package com.andb.apps.composesandbox.ui.sandbox.modifiers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
fun DrawerEditModifiers(modifier: PrototypeModifier, onEdit: (PrototypeModifier) -> Unit) {
    val actionHandler = ActionHandlerAmbient.current
    Column {
        DrawerHeader(title = "Edit Modifier", onIconClick = { actionHandler.invoke(UserAction.Back) })

        when (modifier) {
            is PrototypeModifier.Padding -> PaddingModifierEditor(prototypeModifier = modifier, onEdit)
            is PrototypeModifier.Border -> BorderModifierEditor(prototypeModifier = modifier, onEdit)
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

}