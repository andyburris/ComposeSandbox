package com.andb.apps.composesandbox.ui.sandbox.drawer.properties

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.PrototypeModifier
import com.andb.apps.composesandbox.data.model.icon
import com.andb.apps.composesandbox.data.model.name
import com.andb.apps.composesandbox.data.model.summary


@Composable
fun ModifiersEditor(modifiers: List<PrototypeModifier>, onAdd: () -> Unit, onOpenModifier: (PrototypeModifier) -> Unit, onUpdate: (List<PrototypeModifier>) -> Unit) {
    val dialogShowing = remember { mutableStateOf(false) }
    Column {
        GenericPropertyEditor(label = "Modifiers") {
            Row {
                Icon(
                    asset = Icons.Default.Info,
                    tint = MaterialTheme.colors.onSecondary,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable { dialogShowing.value = true }
                )
                Icon(
                    asset = Icons.Default.Add,
                    tint = MaterialTheme.colors.onSecondary,
                    modifier = Modifier.clickable(onClick = onAdd)
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .background(Color.Black.copy(alpha = .12f), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
        ) {
            modifiers.forEach {
                ModifierItem(
                    prototypeModifier = it,
                    modifier = Modifier.clickable(onLongClick = { onUpdate(modifiers - it) }) {
                        onOpenModifier.invoke(it)
                    }
                )
            }
        }
    }
    ModifierInfoDialog(showing = dialogShowing.value, onDismiss = { dialogShowing.value = false })
}

@Composable
private fun ModifierInfoDialog(showing: Boolean, onDismiss: () -> Unit) {
    if (showing) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = onDismiss, elevation = 0.dp, backgroundColor = Color.Transparent) {
                    Text("OK", color = MaterialTheme.colors.primary)
                }
            },
            title = { Text(text = "Modifiers") },
            text = {
                Text(
                    text = "Modifiers allow you to tweak how a component is presented. \n" +
                            "\n" +
                            "Examples include Padding, Border, Background, Size, Width, Height, and more.\n" +
                            "\n" +
                            "You can chain multiple modifiers together to apply multiple decorations to components. Each modifier in the chain wraps the next (i.e. a Padding before a Background will give a margin to the background color, but vice versa will result in a background color that has expanded to the size of the padding)"
                )
            }
        )
    }
}

@Composable
fun ModifierItem(prototypeModifier: PrototypeModifier, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(asset = prototypeModifier.icon)
            Text(text = prototypeModifier.name, modifier = Modifier.padding(start = 16.dp))
        }
        Text(text = prototypeModifier.summary)
    }
}

