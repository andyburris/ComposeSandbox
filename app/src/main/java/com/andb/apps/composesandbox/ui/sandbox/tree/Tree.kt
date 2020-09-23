package com.andb.apps.composesandbox.ui.sandbox.tree

import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.onPositioned
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.R
import com.andb.apps.composesandbox.data.model.Component
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction

@Composable
fun Tree(parent: Component, modifier: Modifier = Modifier) {
    TreeItem(component = parent, modifier)
}

@Composable
fun TreeItem(component: Component, modifier: Modifier = Modifier) {
    val actionHandler = ActionHandlerAmbient.current

    Column(modifier.fillMaxWidth().clickable { actionHandler.invoke(UserAction.OpenComponent(component)) }) {
        ComponentItem(component = component)
        if (component is Component.Group) {
            Stack {
                val (height, setHeight) = remember { mutableStateOf(0) }
                Column(Modifier.onPositioned { setHeight(it.size.height) }) {
                    for (child in component.children) {
                        Stack {
                            Box(
                                modifier = Modifier
                                    .padding(start = 12.dp, top = 28.dp)
                                    .size(20.dp, 1.dp),
                                backgroundColor = Color.Black.copy(alpha = .25f)
                            )
                            TreeItem(child, modifier = Modifier.padding(start = 40.dp, top = 16.dp))
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(start = 11.dp, top = 8.dp)
                        .size(1.dp, (with(DensityAmbient.current){ height.toDp() } - 19.dp).coerceAtLeast(0.dp)),
                    backgroundColor = Color.Black.copy(alpha = .25f)
                )
            }
        }
    }
}

@Composable
fun ComponentItem(component: Component, modifier: Modifier = Modifier) {
    val icon = when (component) {
        is Component.Text -> Icons.Default.TextFields
        is Component.Icon -> Icons.Default.Image
        is Component.Group.Column -> vectorResource(id = R.drawable.ic_column)
        is Component.Group.Row -> vectorResource(id = R.drawable.ic_row)
    }

    Row(modifier = modifier, verticalGravity = Alignment.CenterVertically) {
        Icon(asset = icon)
        Text(text = component.name, modifier = Modifier.padding(start = 16.dp))
    }
}